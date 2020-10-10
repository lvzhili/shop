app.controller('loginController',function ($scope, loginService, $controller) {

    $scope.showLoginName = function () {
        loginService.loginName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        )
    }
})