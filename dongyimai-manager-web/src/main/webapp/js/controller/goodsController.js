 //控制层 
app.controller('goodsController' ,function($scope,$controller ,$location,itemCatService  ,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	$scope.entity={
		goods:{},
		goodsDesc:{
			itemImages:[],
			specificationItems:[{'attributeName':'','attributeValue':''}]
		}
	};//定义页面实体结构

	$scope.updateStatus = function(status){
		goodsService.updateStatus($scope.selectIds,status).success(
			function (response) {
				if (response.success){
					$scope.reloadList();
				}else {
					alert(response.message);
				}
			}
		)
	}

	$scope.itemList = [];
	$scope.findAllItem = function(){
		itemCatService.findAll().success(
			function (response) {
				for (var i = 0; i < response.length; i++){
					$scope.itemList[response[i].id] = response[i].name;
				}
			}
		)
	}

	//定义状态数组
	$scope.statusList = ['未审核','已审核','驳回请求','关闭'];
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//判断规格名是够相同
	$scope.searchObjectByKey = function(list,key,value){
		for (var i = 0;i < list.length; i++){
			if (list[i][key] == value){
				return list[i]
			}
		}
		return null;
	}

	//根据规格名和规格选项判断是否被勾选
	$scope.checkAttributeValue = function(specName,optionName){
		//取得原来的规格集合
		var specificationItems = $scope.entity.goodsDesc.specificationItems;
		//获取该规格选择过的集合
		var obj = $scope.searchObjectByKey(specificationItems,'attributeName',specName)
		//判断数组中是否包含选择的规格
		if(obj != null){
			if (obj.attributeValue.indexOf(optionName) >= 0){
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}


	$scope.findOne=function(){
		var id = $location.search()['id'];//获取传过来的参数
		if (id == null){
			return null;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				$scope.entity.goodsDesc.introduction=JSON.parse($scope.entity.goodsDesc.introduction)
				$scope.entity.goodsDesc.itemImages = JSON.parse(response.goodsDesc.itemImages);
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.goodsDesc.customAttributeItems);
				$scope.entity.goodsDesc.specificationItems = JSON.parse(response.goodsDesc.specificationItems);
				//对sku进行格式化
				for (var i = 0; i < $scope.entity.itemList.length; i++){
					$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
				}

			}
		);
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele = function(){

		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	