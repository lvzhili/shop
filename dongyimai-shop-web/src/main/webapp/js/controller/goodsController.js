 //控制层 
app.controller('goodsController' ,function($scope,$location,$controller,uploadService ,itemCatService, typeTemplateService ,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承

	$scope.entity={
		goods:{},
		goodsDesc:{
			itemImages:[],
			specificationItems:[]
		}
	};//定义页面实体结构
    $scope.itemCatList = [];
    //查询所有的itemcat
    $scope.findItemName = function(){
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        )
    }

    //定义数组存放状态
    $scope.statusList=['未审核','已审核','审核失败','已驳回'];

	//创建表格形式的数据
	$scope.createItemList = function(){
		//对应规格列表
		$scope.entity.itemList = [{spec:{},price:0,num:999,status:'0',isDefault:'1'}];
		//选择点击的规格
		var item = $scope.entity.goodsDesc.specificationItems;
		//遍历规格
		//[{"attributeName":"机身内存","attributeValue":["16G","64G"]},{"attributeName":"网络","attributeValue":["移动3G","移动4G","联通3G"]}]
		for (var i = 0; i < item.length;i++){
			$scope.entity.itemList = addColumn($scope.entity.itemList,item[i].attributeName,item[i].attributeValue);
		}
	}
	addColumn = function(list,name,values){
		//方法返回值，返回数据
		var newList = [];
		//[{spec:{},sprice:0,num:999,status:'0',isDefault:'1'}]
		//遍历
		for (var i = 0; i < list.length; i++){
			//克隆
			var oldRow = list[i];
			//["16G","64G"]
			//遍历attributeValue
			for (var j = 0; j < values.length; j++){
				//深克隆
				var newRow = JSON.parse(JSON.stringify(oldRow));
				//将规格列表的attributeName和attributeValue组对
				newRow.spec[name] = values[j];
				newList.push(newRow);
			}
		}
		return newList;
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

	$scope.updateSepcification = function($event,name,value){
		var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		if (obj != null){
			//确认是否被选中
			if ($event.target.checked){
				obj.attributeValue.push(value);
			}else{
				//没有被选中删除
				obj.attributeValue.splice(obj.attributeValue.indexOf(value),1);
				//如果时最后一个将规格对想删除
				if (obj.attributeValue.length == 0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj),1);
				}
			}
		}else{
			//为空时第一次点击存储
			$scope.entity.goodsDesc.specificationItems.push({'attributeName':name,'attributeValue':[value]});

		}
	}

	//一级标题
	$scope.findIteamById = function(id){
		itemCatService.findByParentId(id).success(
			function (response) {
				$scope.iteamCatList = response;
			}
		)
	}
	//二级标题
	$scope.$watch('entity.goods.category1Id',function (newValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.iteamCatList2 = response;
			}
		)
	})
	//三级标题
	$scope.$watch('entity.goods.category2Id',function (newValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.iteamCatList3 = response;
			}
		)
	})
	//模板id
	$scope.$watch('entity.goods.category3Id',function (newValue) {
		itemCatService.findOne(newValue).success(
			function (response) {
				$scope.entity.goods.typeTemplateId =  response.typeId;
			}
		)

	})
	//查询所有品牌
	$scope.$watch('entity.goods.typeTemplateId',function (newValue) {
		typeTemplateService.findOne(newValue).success(
			function (response) {
				$scope.typeTemplate = response;
				$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
				if ($location.search()['id']==null){
					$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
				}
			}
		)
		//获取specificationItems
		typeTemplateService.findSpecList(newValue).success(
			function (response) {
				//{"id":27,"text":"网络","options":[]},{"id":32,"text":"网络","options":[]}
				$scope.specList = response;
			}
		)
	})
    //添加图片列表
    $scope.add_image_entity=function(){
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    //列表中移除图片
    $scope.remove_image_entity=function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }
    $scope.uploadFile=function(){
        uploadService.uploadFile().success(
        	function(response) {
				if(response.success){//如果上传成功，取出url
					$scope.image_entity.url=response.message;//设置文件地址
				}else{
					alert(response.message);
				}
			}).error(function() {
				alert("上传发生错误");
			});
    };

   /* $scope.add = function(){
		$scope.entity.goodsDesc.introduction=editor.html();
		goodsService.add($scope.entity).success(
			function (response) {
				if (response.success){
					alert("添加成功");
					editor.html('');
					$scope.entity={};
				}else {
					alert(response.message);
				}
			}
		)
	}*/
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
	
	//查询实体 
	$scope.findOne=function(){
	    var id = $location.search()['id'];//获取传过来的参数
        if (id == null){
            return null;
        }
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				editor.html(response.goodsDesc.introduction);
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
	
	//保存 
	$scope.save=function(){
		$scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					location.href="goods.html";
					//重新查询 
		        	/*$scope.reloadList();//重新加载
					editor.html('');
					$scope.entity={ goodsDesc:{itemImages:[],specificationItems:[]}};*/
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
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