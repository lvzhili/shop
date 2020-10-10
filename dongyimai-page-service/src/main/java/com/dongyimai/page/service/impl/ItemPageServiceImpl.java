package com.dongyimai.page.service.impl;

import com.dongyimai.mapper.TbGoodsDescMapper;
import com.dongyimai.mapper.TbGoodsMapper;
import com.dongyimai.mapper.TbItemCatMapper;
import com.dongyimai.mapper.TbItemMapper;
import com.dongyimai.page.service.ItemPageService;
import com.dongyimai.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    private String pagedir = "f:/item/";

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean getItemHtml(Long goodsId) {
        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();

            Template template = configuration.getTemplate("item.ftl");

            //创建数据模型
            HashMap map = new HashMap();
            //加载数据
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            map.put("goods",tbGoods);
            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            map.put("goodsDesc",tbGoodsDesc);
            //读取面包屑
            TbItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());

            map.put("itemCat1",itemCat1.getName());
            map.put("itemCat2",itemCat2.getName());
            map.put("itemCat3",itemCat3.getName());

            //读取sku
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            example.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            map.put("itemList",itemList);
            //创建write对象
            Writer out = new FileWriter(pagedir+tbGoods.getId()+".html");
            template.process(map,out);
            out.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteItemHtml(Long[] goodsId) {

        try {
            for (Long id : goodsId) {
                new File(pagedir+id+".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
