app.controller('indexController',function ($scope,$controller,indexService) {

    $scope.showLoginName = function () {
        indexService.loginName().success(
            function (response) {

                $scope.loginName = response.loginName;
            }
        )
    } 
})