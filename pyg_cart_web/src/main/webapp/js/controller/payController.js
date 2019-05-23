app.controller('payController',function ($scope,payService) {

    //生成二维码
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {
                //生成二维码
                var qr = window.qr = new QRious({
                    element: document.getElementById('qrious'),
                    size: 250,
                    value: response.code_url,
                    level: 'H'
                })

                $scope.out_trade_no = response.out_trade_no; //支付单号
                $scope.total_fee = (response.total_fee / 100).toFixed(2);  //支付金额 toFixed只取两位小数

                //查询支付状态
                queryPayStatus();
            }
        )
    }


    queryPayStatus = function () {
        payService.queryPayStatus($scope.out_trade_no).success(
            function (response) {
                if(response.success){
                    location.href="paysuccess.html";
                }else{

                    //需要处理超时的问题
                    if(response.message == 'timeout'){
                        $scope.createNative(); //再次请求支付地址
                    }else{
                        location.href="payfail.html";
                    }
                }
            }
        )
    }
})