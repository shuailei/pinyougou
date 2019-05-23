 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;

				//前端将字符串转对象 这三个在类型模版表中是字符串属性，需要在前端段转成json对象，才可以被angular解析数据转换
				$scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
                $scope.entity.specIds = JSON.parse($scope.entity.specIds);
                $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);

			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//新增模版的品牌下拉列表
	$scope.findBrandList = function(){
		brandService.selectOptionList().success(
			function (response) {
				$scope.brandList = {data:response};
            }
		)
	}

	//新增模版的规格下拉列表
	$scope.findSpecList = function () {
		specificationService.selectOptionList().success(
			function (response) {
				$scope.specList = {data:response};
            }
		)
    }

    $scope.init = function () {
        $scope.findBrandList();
        $scope.findSpecList();

        $scope.entity = {customAttributeItems:[]}; // 需要初始化自定义扩展属性为数组格式
    }

    //新增自定义扩展属性空行
    $scope.addTableRow = function () {
		$scope.entity.customAttributeItems.push({});
    }

    //删除自定义扩展属性空行
	$scope.deleTableRow = function ($index) {
		$scope.entity.customAttributeItems.splice($index,1);
    }

    /*$scope.config3 = {data: [{id:1,text:'bugtest'},{id:2,text:'duplicate'},{id:3,text:'invalid'},{id:4,text:'wontfix'}]};*/

    $scope.jsonToString=function(jsonString,key){
		var jsonStr = JSON.parse(jsonString);
        var value = "";
        for (var i = 0; i < jsonStr.length; i++) {
        	if(i>0){
        		value+=",";
			}
			value += jsonStr[i][key];/*集合中取属性为key的对应的值，表示索引第i个元素的属性为key的值！！！*/
        }
        return value;
	}
});	
