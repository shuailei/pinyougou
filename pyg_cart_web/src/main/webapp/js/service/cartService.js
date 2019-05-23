app.service('cartService',function ($http) {

    this.findCartList = function () {
        return $http.get('cart/findCartList.do');
    }
    
    this.addItemToCartList = function (itemId,num) {
        return $http.get('cart/addItemToCartList.do?itemId='+itemId+"&num="+num);
    }
})