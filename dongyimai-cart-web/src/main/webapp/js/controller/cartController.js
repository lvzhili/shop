app.controller('cartController',function ($scope,cartService,addressService) {
    //选择支付方式
    $scope.order ={paymentType:1};

    $scope.selectedPayType = function(type){
        $scope.order.paymentType = type;
    }
    $scope.submitOrder = function(){
        $scope.order.receiverAreaName = $scope.address.address;//收件人地址
        $scope.order.receiverMobile=$scope.address.mobile;//收件人手机号
        $scope.order.receiver=$scope.address.contact;//收件人
        cartService.submitOrder($scope.order).success(
            function (response) {
                if (response.success){
                    if ($scope.order.paymentType == '1'){
                        location.href="pay.html#?total_fee="+$scope.totleValue.totleMoney;
                    }else {
                        location.href='paysuccess'
                    }
                }else{
                    alert(response.message);
                }
            }
        )
    }
    $scope.checkedAlias = function(alias){
        $scope.entity.alias = alias;
    }

    //选择地址
    $scope.selectedAdress = function(address){
        $scope.address = address;
    }
    //判断是否为当前选择地址
    $scope.isSelected = function(address){
        if (address == $scope.address){
            return true;
        }else{
            return false;
        }
    }

    $scope.findListByUseId = function(){
        addressService.findListByUseId().success(
            function (response) {
                $scope.addressList = response;
                //默认地址
                for (var i = 0; i < $scope.addressList.length; i++){
                    if ($scope.addressList[i].isDefault == "1"){
                        $scope.address = $scope.addressList[i];
                        break;
                    }
                }
            }
        )
    }

   //求总数
    $scope.getTotle = function(cartList){
        var totleValue = {totleNum:0,totleMoney:0.00};
        for (var i = 0; i < cartList.length; i++){
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++){
                totleValue.totleNum += cart.orderItemList[j].num;
                totleValue.totleMoney += cart.orderItemList[j].totalFee;
            }
        }
        return totleValue;
    }
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.list = response;
                $scope.totleValue = $scope.getTotle(response);
            }
        )
    }

    $scope.add = function (itemId,num) {
        cartService.add(itemId,num).success(
            function (response) {
                if (response.success){
                    $scope.findCartList();
                }else {
                    alert(response.message);
                }
            }
        )
    }
})