 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService, itemCatService,typeTemplateService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){

		//获取富文本编辑器中的内容到entity对象中
		$scope.entity.goodsDesc.introduction = editor.html();

		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID ,需要判断goods中的属性
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					location.href = 'goods.html';
				}else{
					alert(response.message);
				}

				editor.html(''); //清空富文本编辑器
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	$scope.init = function(){
        $scope.selectItemCat1List();
	}

	//获取一级分类的下拉列表数据
	$scope.selectItemCat1List = function () {
		itemCatService.findListByParentId(0).success(
			function (response) {
				$scope.itemCat1List = response;
            }
		)
    }

    //通过一级分类选择后，查询二级分类,参数一是要观察变量的字符串 是否发生改变，如果发生变化，watch就会触发
	$scope.$watch('entity.goods.category1Id',function (newValue, oldValue) {
		itemCatService.findListByParentId(newValue).success(
			function (response) {
				$scope.itemCat2List = response;
            }
		)
    })

	//通过选择二级分类，确定三级分类列表
	$scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
		itemCatService.findListByParentId(newValue).success(
			function (resonse) {
				$scope.itemCat3List = resonse;
            }
		)
    })

	//通过选择三级分类，获取三级分类中的typeId 就可以获取类型模版id
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
        itemCatService.findOne(newValue).success(
            function (resonse) {
            	//将分类中对应的模版id封装到商品封装对象中
                $scope.entity.goods.typeTemplateId = resonse.typeId;
            }
        )
    })

	//根据当前的模版的id获取模版对象
    $scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
        typeTemplateService.findOne(newValue).success(
        	function (response) {
				$scope.typeTemplate = response;
				$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds); //将字符串转json对象
				//将模版中的自定义属性赋值给商品封装对象中的自定义属性
				if($location.search()['id']==null){ //如果是新增再赋值，否则跳过该代码
					$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
				}
            }
		)
		//根据模版id获取该模版下的所有规格选项
		typeTemplateService.findSpecList(newValue).success(
			function (response) {
				$scope.specList = response;
			}
		)
    })
    
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if(response.success){
                    $scope.image_entity.url = response.message;
                }
            }
        )
    }


	$scope.status = ['未审核','申请中','审核通过','已驳回'];

	/*$scope.init = function(){
		$scope.selectItemCat1List();
		$scope.entity = {goodsDesc:{itemImages:[],specificationItems:[]}}; //给图片属性进行初始化

	}*/
	$scope.itemCatList = []; //需要进行初始化数组
	//查询所有的分类数据，返给前端进行处理
	$scope.findItemCatList = function(){
		itemCatService.findAll().success(
			function (response) {
				for (var i = 0; i < response.length; i++) {
					$scope.itemCatList[response[i].id] = response[i].name;
				}

				//alert(JSON.stringify($scope.itemCatList));
			}
		)
	}

	//商家修改商品状态
	$scope.updateStatus = function (status) {
		goodsService.updateStatus($scope.selectIds,status).success(
			function (response) {
				if(response.success){
					//刷新列表
					$scope.reloadList();
				}else{
					alert(response.message);
				}
			}
		)
	}

	//确认当前规格选项是否需要勾选
	$scope.checkAttributeValue=function(specName,optionName){
		var items= $scope.entity.goodsDesc.specificationItems;
		var object= searchObjectByKey(items,specName);
		if(object==null){
			return false;
		}else{
			if(object.attributeValue.indexOf(optionName)>=0){
				return true;
			}else{
				return false;
			}
		}
	}
});	
