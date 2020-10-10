app.controller('searchController',function ($scope,$location,searchService) {
    $scope.searchMap = {
        keywords:'',
        category:'',
        brand:'',
        price:'',
        pageNum:1,
        pageSize:10,
        sortValue:'',
        sortField:'',
        spec:{}
    };
    //加载传入的查询关键字
    $scope.loadKeywords = function(){
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }
    //隐藏品牌
    $scope.keywordsIsBrand = function(){
        for (var i = 0;i < $scope.resultMap.brandList.length; i++){
            if ($scope.resultMap.keywords.indexOf($scope.resultMap.brandList[i].text >= 0)){
                return true;
            }
        }
        return false;
    }
    //排序查询
    $scope.sortSearch = function(sortValue,sortField){
        $scope.searchMap.sortValue = sortValue;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    }
    //判断是否为当前页
    $scope.isPageNum = function(p){
        if (parseInt(p) == parseInt($scope.searchMap.pageNum)){
            return true;
        }
        return false;
    }

    //给当前页赋值查询
    $scope.searchPageNum = function(pageNum){

        if (pageNum < 1 || pageNum > $scope.searchMap.totalPages){
            return;
        }
        $scope.searchMap.pageNum = pageNum;
        $scope.search();
    }

    //分页
    buildPageLabel = function(){
        //分页栏属性
        $scope.pageLabel = [];

        var firstPage = 1;
        var lastPage = $scope.resultMap.totalPages;
        //显示省略号，定义标记
        $scope.firstDot = true;
        $scope.lastDot = true;
        //自定义页数
        if (lastPage > 5){
            //如果当前页小于5时
            if ($scope.searchMap.pageNum <= 3){
               lastPage = 5;
                $scope.firstDot = false;
            }else if ($scope.searchMap.pageNum > (lastPage - 2)){
                firstPage = lastPage - 4;
                $scope.lastDot = false;
            }else {
                firstPage = $scope.searchMap.pageNum - 2;
                lastPage = $scope.searchMap.pageNum + 2;
            }
        }else {
            $scope.firstDot=false;
            $scope.lastDot=false;
        }
        for (var i = firstPage; i <= lastPage; i++){
            $scope.pageLabel.push(i);
        }
    }
    //撤销面包屑
    $scope.removeSearchItem = function(key){
        $scope.searchMap.pageNum = 1;
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = '';
        }else{
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
    //添加搜索条件
    $scope.addSearchItem = function(key,value){
        $scope.searchMap.pageNum = 1;
        if (key == 'category' || key == 'brand'|| key == 'price'){
            $scope.searchMap[key] = value;
        }else{
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }

    $scope.search = function () {
        $scope.searchMap.pageNum = parseInt($scope.searchMap.pageNum);
        searchService.search($scope.searchMap).success(
            function(response){
                $scope.resultMap = response;
                buildPageLabel();
            }
        )
    }
})