package com.dongyimai.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.dongyimai.group.Specification;
import com.dongyimai.mapper.TbSpecificationOptionMapper;
import com.dongyimai.pojo.TbSpecificationOption;
import com.dongyimai.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.dongyimai.mapper.TbSpecificationMapper;
import com.dongyimai.pojo.TbSpecification;
import com.dongyimai.pojo.TbSpecificationExample;
import com.dongyimai.pojo.TbSpecificationExample.Criteria;
import com.dongyimai.sellergoods.service.SpecificationService;

import com.dongyimai.entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper optionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<Map> findAll() {
		return specificationMapper.selectOptionList();
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		//添加规格返回主键
		specificationMapper.insert(specification.getTbSpecification());

		for (TbSpecificationOption tbSpecificationOption : specification.getOptionList()) {
			tbSpecificationOption.setSpecId(specification.getTbSpecification().getId());
			optionMapper.insert(tbSpecificationOption);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//修改规格表
		specificationMapper.updateByPrimaryKey(specification.getTbSpecification());
		//先删除规格选项，再添加
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(specification.getTbSpecification().getId());

		optionMapper.deleteByExample(example);
		//添加

		for (TbSpecificationOption option : specification.getOptionList()) {
			optionMapper.insert(option);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		Specification spec = new Specification();

		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		spec.setTbSpecification(tbSpecification);

		TbSpecificationOptionExample exmple = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = exmple.createCriteria();
		criteria.andSpecIdEqualTo(id);

		List<TbSpecificationOption> optionList = optionMapper.selectByExample(exmple);

		spec.setOptionList(optionList);

		return spec;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//先删除规格选项，再删除规格表
		for (Long id : ids) {
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			optionMapper.deleteByExample(example);

			//删除规格表
			specificationMapper.deleteByPrimaryKey(id);
		}
	}
	
	
	@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
			if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}	
		}

		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
