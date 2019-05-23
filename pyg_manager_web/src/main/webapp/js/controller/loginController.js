app.controller('loginController',function ($scope, loginService) {

    //显示当前登录人名
    $scope.showLoginName = function () {
        loginService.showLoginName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        )
    }
})