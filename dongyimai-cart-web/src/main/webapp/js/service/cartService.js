app.service('cartService',function ($http) {

    this.submitOrder = function (order) {
        return $http.post('../order/add.do',order)
    }

    this.findCartList = function () {
        return $http.get('../cart/findCartList.do');
    }
    this.add = function (itemId,num) {
        return $http.post('../cart/addGoodsToCartList.do?itemId='+itemId + '&num='+num);
    }
})