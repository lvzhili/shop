//在模块中建立控制器
app.controller('brandController',function ($scope,$http,$controller,brandService) {
    $controller('baseController',{$scope:$scope});
    //定义一个集合，存储被选中的id
    $scope.delete = function(){
        brandService.delete($scope.selectIds).success(
            function (response) {
                if (response.success){
                    $scope.reloadList();
                }else {
                    alert(response.message);
                }
            }
        )
    };

    $scope.getById = function(id){
        brandService.getById(id).success(
            function (response) {
                $scope.entity=response;
            }
        )
    };

    $scope.save = function (){
        var method = "add";
        if ($scope.entity.id != null){
            method = "update"
        }
        brandService.save(method,$scope.entity).success(
            function (response) {
                if (response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            }
        )
    };

    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };


    $scope.findPage = function(pageNum,pageSize){
        $http.get('../brand/findPage.do?pageNum='+pageNum+'&pageSize='+pageSize).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        );
    };
    $scope.searchEntity={};
    $scope.search=function (pageNum,pageSize) {
        brandService.search(pageNum,pageSize,$scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        )
    }
});