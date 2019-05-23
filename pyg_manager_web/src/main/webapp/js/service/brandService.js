//服务层  在ng-app下自定义服务，它的作用是可以将控制器下的多个服务功能封装到一个服务对象中去，便于重复调用，并降低模块之间到耦合。这就是为什么将多个服务功能从控制器中抽取出来封装成一个自定义模块service对象的原因！！！！
app.service('brandService',function($http){
	    	
	//读取列表数据绑定到表单中          注意，在AngularJS中 this指向$scope,   所以下面的一切定义最后都交给来全域对象$scope来管理！！！
	this.findAll=function(){
		return $http.get('../brand/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../brand/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../brand/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../brand/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../brand/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../brand/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../brand/search.do?page='+page+"&rows="+rows, searchEntity);
	}
	//搜索全部的品牌列表（id，text）
	this.selectOptionList=function () {
		return $http.get('../brand/selectOptionList.do');
	}
});
/*
//把内置服务发送请求方法全部抽取到服务层代码 brandService.js
//定义service服务层
app.service("brandService",function($http){
	//查询所有 不需要注入参数 利用框架的内置服务协议发送ajax异步get请求，切记不要忘记写return，因为必须将请求的数据结果返回给调用者
	this.findAll=function () {
		return $http.get("../brand/findAll");
	};

	//分页chaxun  需要传入参数page 当前页码，rows 当前页记录数 ，有了这俩个参数就可以实现分页
	this.findPage=function (page,rows) {
		return $http.get("../brand/findPage/"+page+"/"+rows);
	};

	//添加 注入实体对象
	this.add=function (entity) {
		return $http.post("../brand/add/"+entity);
	};

	//修改
	this.update=function (entity) {
		return $http.post("../brand/update",entity);
	};

	//批量删除 传入一个整数数组
	this.del=function (ids) {
		return $http.post("../brand/del/"+ids);
	};

	//根据id查询
	this.findOne=function (id) {
		return $http.get("../brand/findOne/"+id);
	}
})
*/
























































