app.service('brandService',function ($http) {

    this.delete = function (ids) {
        return $http.get('../brand/delete.do?ids='+ids);
    };
    this.save = function (method,entity) {
        return $http.post('../brand/'+method+'.do',entity)
    };
    this.getById = function (id) {
        return $http.get('../brand/getById.do?id='+id)
    };
    this.search = function (pageNum,pageSize,searchEntity) {
        return $http.post('../brand/search.do?pageNum='+pageNum+'&pageSize='+pageSize,searchEntity)
    };
    this.findAll = function () {
        return $http.get('../brand/findAll.do');
    }
});