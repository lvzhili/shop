app.controller('itempageController',function($http,$scope){


	$scope.addToCart = function(){
		$http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
			+ $scope.defaultSku.id +'&num='+$scope.num,{'withCredentials':true}).success(
			function(response){
				if(response.success){
					location.href='http://localhost:9107/cart.html';//跳转到购物车页面
				}else{
					alert(response.message);
				}
			}
		);
	}



	$scope.loadSku = function(){
		$scope.defaultSku = itemList[0];
		$scope.specification = JSON.parse(JSON.stringify($scope.defaultSku.spec));
	}

	searchSku = function(){
		
		for(var i = 0; i < itemList.length; i++){
			
			if(matchObject($scope.specification,itemList[i].spec)){
				$scope.defaultSku = itemList[i];
				return;
			}
		}
		$scope.defaultSku={id:0,title:"-----",price:0};
	}
	


	matchObject = function(map1,map2){
	
		for(var k in map1){
			if(map1[k] != map2[k]){
				return false;
			}
		}
		for(var k in map2){
			if(map2[k] != map1[k]){
				return false;
			}
		}

		return true;
	}
	

	$scope.specification = {};

	$scope.addSpec = function(name,value){
		
		$scope.specification[name] = value;
		searchSku();
	}

	$scope.isSelected = function(name,value){
		if($scope.specification[name] == value){
			return true;
		}else{
			return false;
		}
	}

	$scope.addNum = function(x){
		$scope.num = $scope.num + x;
		if($scope.num < 1){
			$scope.num = 1;
		}
		if($scope.num > 200){
			$scope.num = 200;
		}
	}
})