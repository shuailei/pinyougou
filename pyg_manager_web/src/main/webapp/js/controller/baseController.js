 //品牌控制层 
app.controller('baseController' ,function($scope){	
	
    //重新加载列表 数据
    $scope.reloadList=function(){
    	//切换页码  
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);	   	
    }
    
	//分页控件配置 
	$scope.paginationConf = {
         currentPage: 1,
         totalItems: 10,
         itemsPerPage: 10,
         perPageOptions: [10, 20, 30, 40, 50],
         onChange: function(){
        	 $scope.reloadList();//重新加载
     	 }
	}; 
	
	$scope.selectIds=[];//选中的ID集合   html中调用使用的变量，方法、对象都必须先绑定在$scope作用域中才可以被调用

	//更新复选  $event代表了触发事件，比如接收 点击引发的目标事件Dom对象
	$scope.updateSelection = function($event, id) {		
		if($event.target.checked){//如果是被选中,则增加到数组
			$scope.selectIds.push( id);			
		}else{
			var idx = $scope.selectIds.indexOf(id); //获取指定元素（id）在数组selectIds中的索引位置
            $scope.selectIds.splice(idx, 1);//删除  idx表示在数组的位置，1表示splice要移除的元素个数，1即移除当前取消选择的复选框
		}
	}

	//以便通过指令ng-checked来判断数组中是否已存在该id，如果已存在，则记录为勾选状态，避免翻页时，刷新状态清空勾选按钮！！！
	$scope.isChecked = function (id) {
		if($scope.selectIds.indexOf(id)!=-1){
			return true;
		}else{
			return false;
		}
	}
	
});	