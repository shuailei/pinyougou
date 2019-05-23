package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import entity.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;

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
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
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
		//保存规格对象  到数据库
		TbSpecification spec = specification.getSpecification();
		specificationMapper.insert(spec);
		//保存所有的 规格选项
		List<TbSpecificationOption> specOptionList = specification.getSpecOptionList();
		for (TbSpecificationOption option : specOptionList) {
			option.setSpecId(spec.getId());  //维护规格选项和规格的多对一关系，，【如果这个id不设置，能做新增操作，只不过新增数据库的数据该属性为null，但是这样再次做查询时，就会出现问题，数据就对不上了，故而如果不设置这个规格选项id，新增之后的数据肯定是有问题的！！！】
			specificationOptionMapper.insert(option);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){

		TbSpecification spec = specification.getSpecification();//修改规格数据保存数据库
		specificationMapper.updateByPrimaryKey(spec);

		//将所有规格下的规格选项删除后，本次改为新增操作   修改时，混合删除、新增、修改等操作，故而改全删然后新增规格选项集合列表
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		example.createCriteria().andSpecIdEqualTo(spec.getId()); //查询规格id一致的所有规格选项列表
		specificationOptionMapper.deleteByExample(example);  //删除相同规格下的所有规格选项

		//改为新增所有规格选项
		List<TbSpecificationOption> specOptionList = specification.getSpecOptionList();
		for (TbSpecificationOption option : specOptionList) {
			option.setSpecId(spec.getId());  //维护规格选项和规格的多对一关系
			specificationOptionMapper.insert(option);
		}

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){//联合查询，并不仅查询一个规格，而且还要查询到这个规格的所有规格选项！！！他们是一对多的关系
		//封装规格
		Specification specification = new Specification();
		specification.setSpecification(specificationMapper.selectByPrimaryKey(id));

		//封装规格选项集合
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		example.createCriteria().andSpecIdEqualTo(id); //查询规格id一致的所有规格选项列表   创建所有条件的集合，并拼接条件到集合中
		List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
		specification.setSpecOptionList(options);

		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//删除规格  注意维护一对多的关系，所以还要删除规格对应的规格选项！！！
			specificationMapper.deleteByPrimaryKey(id);

			//联动删除规格选项  即删除当前规格下的所有规格选项
			TbSpecificationOptionExample example=new TbSpecificationOptionExample();
			example.createCriteria().andSpecIdEqualTo(id);// 查询规格id一致的所有规格选项
			specificationOptionMapper.deleteByExample(example);

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

	@Override
	public List<Map> selectOptionList() {
		return specificationMapper.selectOptionList();
	}

}
