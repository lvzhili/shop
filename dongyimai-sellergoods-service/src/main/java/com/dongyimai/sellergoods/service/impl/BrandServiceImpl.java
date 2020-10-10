package com.dongyimai.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dongyimai.entity.PageResult;
import com.dongyimai.mapper.TbBrandMapper;
import com.dongyimai.pojo.TbBrand;
import com.dongyimai.pojo.TbBrandExample;
import com.dongyimai.sellergoods.service.BrandService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    /**
     * 查询所有
     * @return
     */
    @Override
    public List<Map> findAll() {
        return brandMapper.selectOptionList();
    }
    /**
     * 分页查询
     * @return
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum,pageSize);

        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);

        return new PageResult(page.getTotal(),page.getResult());
    }
    /**
     * 添加
     * @return
     */
    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }
    /**
     * 通过id获取
     * @return
     */
    @Override
    public TbBrand getById(long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
    /**
     * 修改
     * @return
     */
    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }
    /**
     * 批量删除
     * @return
     */
    @Override
    public void delete(long[] ids) {
        for (long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }
    /**
     * 分页模糊搜索
     * @return
     */
    @Override
    public PageResult search(int pageNum, int pageSize, TbBrand brand) {
        PageHelper.startPage(pageNum,pageSize);
        //拼接条件
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if (brand != null){
            if (brand.getName() != null && brand.getName().length() > 0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar() != null && brand.getFirstChar().length() > 0){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }
}
