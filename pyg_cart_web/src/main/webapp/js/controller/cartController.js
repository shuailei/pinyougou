app.controller('cartController',function ($scope, cartService , addressService, orderService) {

    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                //每次查询购物车，计算总金额总数量
                sum();
            }
        )
    }

    //添加商品数量到购物车
    $scope.addItemToCartList = function (itemId, num) {
        cartService.addItemToCartList(itemId,num).success(
            function (response) {
                if(response.success){
                    //刷新一下购物车列表
                    $scope.findCartList();
                }else{
                    alert(response.message);
                }
            }
        )
    }

    //获取地址列表
    $scope.findListByUserId = function () {
        addressService.findListByUserId().success(
            function (response) {
                $scope.addressList = response;
                for (var i = 0; i < response.length; i++) {
                   if(response[i].isDefault == 1){
                       $scope.address = response[i]; // 将默认地址赋值给$scope.address
                   }
                }

            }
        )
    }

    //保存当前选择的地址
    $scope.selectAddress=function(address){
        //定义前端的变量address
        $scope.address = address;
    }

    //判断是否是当前勾选的地址
    $scope.isSelectAddress = function (address) {
        if($scope.address == address){
            return true;
        }else{
            return false;
        }
    }

    //浏览器报：TypeError：Cannot set property 'payMentType' of undefined,表示没定义order对象
    //选择支付方式保存到order对象上 定义一个order对象
    $scope.order = {};

    //支付类型
    $scope.selectPaymentType = function (type) {
        $scope.order.paymentType = type;
        /*前端测试方法之alert。   alert(type);*/
    }

    //数量和总金额的累加方法
    sum = function () {
        $scope.totalMoney = 0; //总金额
        $scope.totalNum = 0;

        for (var i = 0; i < $scope.cartList.length; i++) {
            var orderItems = $scope.cartList[i].orderItemList;
            for (var j = 0; j < orderItems.length; j++) {
                $scope.totalMoney += orderItems[j].totalFee; //累加订单明细商品中的总金额
                $scope.totalNum += orderItems[j].num;
            }
        }
    }

    //订单保存按钮
    $scope.saveOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address; //收货地址封装到订单中
        $scope.order.receiverMobile = $scope.address.mobile; //收货人手机
        $scope.order.receiver = $scope.address.contact;    //收货联系人

        orderService.add($scope.order).success(
            function (response) {
                if(response.success){
                    location.href = "pay.html";
                }else{
                    alert(response.message);
                }
            }
        )
    }

})