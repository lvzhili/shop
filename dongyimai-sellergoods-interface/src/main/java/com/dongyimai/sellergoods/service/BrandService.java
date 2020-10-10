package com.dongyimai.sellergoods.service;

import com.dongyimai.entity.PageResult;
import com.dongyimai.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public List<Map> findAll();
    public PageResult findPage(int pageNum, int pageSize);
    public void add(TbBrand brand);
    public TbBrand getById(long id);
    public void update(TbBrand brand);
    public void delete(long[] ids);
    public PageResult search(int pageNum,int pageSize,TbBrand brand);
}
