package com.dongyimai.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dongyimai.pojo.TbItem;
import com.dongyimai.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    //从redis中查询规格和品牌
    public Map<String,Object> searchSpecAndBrand(String category){
        Map<String,Object> map = new HashMap<>();
        //获得模板Id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        //通过模板ID获取品牌和规格
        System.out.println(typeId);
        List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
        List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
        map.put("brandList",brandList);
        map.put("specList",specList);
        return map;
    }
    @Override
    public Map<String, Object> search(Map searchMap) {
        //关键字空格处理
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace("",""));
        Map<String,Object> map = new HashMap<>();

        /*Query query = new SimpleQuery();
        //查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //添加查询条件
        query.addCriteria(criteria);

        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);

        map.put("rows",page.getContent());
        System.out.println(page.getContent());*/
        map.putAll(searchList(searchMap));
        List categoryList =  searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        //如果用户选择了分类，则查询品牌和规格
        //如果客户没有选择分类。则按照第一个分类查询
        if (!"".equals(searchMap.get("category"))){
            map.putAll(searchSpecAndBrand(searchMap.get("category")+""));
        }else{
            if (categoryList.size()>0){
                map.putAll(searchSpecAndBrand(categoryList.get(0)+""));
            }
        }
        return map;
    }

    /**
     * 更新solr库
     * @param list
     */
    @Override
    public void importList(List<TbItem> list) {
        for (TbItem item : list) {
            Map<String,String> specMap = JSON.parseObject(item.getSpec(), Map.class);
            Map map = new HashMap();

            for (Map.Entry<String,String> entry : specMap.entrySet()){
                map.put("item_spec_"+entry.getKey(),entry.getValue());
            }
            item.setSpecMap(map);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsId(List goodsIdList) {
        System.out.println("删除商品id："+goodsIdList);
        Query query = new SimpleQuery();

        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);

        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //分组查询
    public List searchCategoryList(Map searchMap){
        List list = new ArrayList();
        //构建查询对象
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //创建分组查询条件
        GroupOptions options = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(options);
        //执行查询
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据条件分组的结果
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //将分组后的结果集，返回集合对象
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //分组后结果集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //遍历集合，将符合条件的结果添加到list集合中
        for (GroupEntry<TbItem> groupEntry : content) {
            list.add(groupEntry.getGroupValue());
        }
        return list;
    }
    //高亮查询
    public Map searchList(Map searchMap){
        Map map = new HashMap();
        //创建高亮查询对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //设置高亮属性
        HighlightOptions options = new HighlightOptions();
        options.addField("item_title");
        //设置高亮样式
        options.setSimplePrefix("<em style='color:red'>");
        options.setSimplePostfix("</em>");

        //将样式放入对象中
        query.setHighlightOptions(options);
        //拼接查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //1.1拼接分类模板的过滤条件
        if (!"".equals(searchMap.get("category"))){
            Criteria criteria1 = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //1.2拼接品牌的过滤条件
        if (!"".equals(searchMap.get("brand"))){
            Criteria criteria1 = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //1.3拼接规格的过滤条件
        if (searchMap.get("spec") != null){
            Map<String,String> map1 = (Map) searchMap.get("spec");
            for (Map.Entry<String,String> entry : map1.entrySet()){
                Criteria criteria1 = new Criteria("item_spec_" + entry.getKey()).is(entry.getValue());
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.4拼接价格的过滤条件
        if (!"".equals(searchMap.get("price"))){
           String price = (String) searchMap.get("price");
            String[] split = price.split("-");
            if (split[0].equals("0")){
                Criteria criteria1 = new Criteria("item_price").lessThanEqual(split[1]);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }else if (split[1].equals("*")){
                Criteria criteria1 = new Criteria("item_price").greaterThanEqual(split[0]);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }else {
                //价格大于较小值
                Criteria criteria1 = new Criteria("item_price").greaterThanEqual(split[0]);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
                //价格小于最大值
                Criteria criteria2 = new Criteria("item_price").lessThanEqual(split[1]);
                SimpleFilterQuery filterQuery1 = new SimpleFilterQuery(criteria2);
                query.addFilterQuery(filterQuery1);
            }
        }
        //1.5分页查询
        Integer pageNum = (Integer) searchMap.get("pageNum");
        if (pageNum == null){
            pageNum = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null){
            pageSize = 10;
        }

        //1.6 排序查询
        //获取排序方式
        String sortValue = (String) searchMap.get("sortValue");
        //获取排序字段
        String sortField = (String) searchMap.get("sortField");
        if (sortField != null && sortField.length() > 0){
            if (sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }

        //设置分页
        query.setOffset((pageNum-1)*pageSize);
        query.setRows(pageSize);

        //执行查询
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //获取高亮集合
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {

            TbItem item = highlightEntry.getEntity();

            if (highlightEntry.getHighlights().size() > 0 && highlightEntry.getHighlights().get(0).getSnipplets().size() > 0){
                List<HighlightEntry.Highlight> highlightList  = highlightEntry.getHighlights();
                //高亮结果集合
                List<String> snipplets = highlightList.get(0).getSnipplets();
                //获取第一个高亮字段所对应的高亮结果，设置为商品标题
                item.setTitle(snipplets.get(0));
            }
        }
        map.put("rows",page.getContent());
        //总页数
        map.put("totalPages",page.getTotalPages());
        //总条数
        map.put("totalElements",page.getTotalElements());
        return map;
    }
}
