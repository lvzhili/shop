 //控制层 
app.controller('addressController' ,function($scope,$controller ,cartService  ,addressService){
	
	$controller('baseController',{$scope:$scope});//继承

	//选择支付方式
	$scope.order ={paymentType:1};


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


    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		addressService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		addressService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		addressService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=addressService.update( $scope.entity ); //修改  
		}else{
			serviceObject=addressService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
					$scope.findListByUseId()//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		addressService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		addressService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	