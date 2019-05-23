 //在模块下定义控制器
//控制层 操作服务层 控制器中的所以scope属性都可以看成是真正提供服务的功能---个人看法
app.controller('brandController' ,function($scope,$controller   ,brandService){	
	//控制层依赖服务层，依赖的方式是通过继承来实现的，如下：
	$controller('baseController',{$scope:$scope});//继承 继承了baseController基础控制器

	//将所有的函数、模型数据都绑定到$scope域中！！！

    //读取列表数据绑定到表单中  直接通过服务对象调用成员，获取数据成功后调用回调函数，并将前面获取到数据传入进行前端处理
	/*
	* 功能： 查询所有品牌 findAll（）
	* 参数：不需要传入参数
	* 结果：一个list集合
	* */
	$scope.findAll=function(){
		//使用$http内置服务调用后台restful请求（get进行数据查询并将返回结果存在scope域中的list属性中）
		brandService.findAll().success(
			function(response){
				$scope.list=response;  //将查询到的数据初始化到list数组集合中
			}			
		);
	}    
	
	//分页
	/*
	*
	* */
	$scope.findPage=function(page,rows){			
		brandService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		brandService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=brandService.update( $scope.entity ); //修改  
		}else{
			serviceObject=brandService.add( $scope.entity  );//增加 
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
		brandService.dele( $scope.selectIds ).success(
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
		brandService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
