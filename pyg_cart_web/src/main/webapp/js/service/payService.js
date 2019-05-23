app.service('payService',function ($http) {
    //生成支付地址
    this.createNative = function () {
        return $http.get('pay/createNative.do');
    }

    //支付状态
    this.queryPayStatus = function (out_trade_no) {
        return $http.get('pay/queryPayStatus.do?out_trade_no='+out_trade_no);
    }
})