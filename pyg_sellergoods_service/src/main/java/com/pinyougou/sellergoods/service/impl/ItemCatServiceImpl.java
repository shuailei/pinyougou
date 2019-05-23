package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.pojo.TbItemCatExample.Criteria;
import com.pinyougou.sellergoods.service.ItemCatService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//删除当前分类对象删除，还需要处理下一级分类对象
		/*for(Long id:ids){
			itemCatMapper.deleteByPrimaryKey(id);
		}*/

		//开发思路是：增加一个字段status，业务逻辑不做物理删除delete，而是进行update处理，修改状态，
		// 查询之后，查询的时候只显示状态没删除的数据，而查询状态为'删除'的数据则不显示，这样做到实现一个逻辑删除

		//下面演示的是物理删除,使用递归处理遍历删除父类下的所有子级
		for (Long id:ids){
			deleItemCatByParentId(id);
		}

	}
	
	public void deleItemCatByParentId(Long id){
		TbItemCatExample example = new TbItemCatExample();
		example.createCriteria().andParentIdEqualTo(id);
		List<TbItemCat> itemCats = itemCatMapper.selectByExample(example);
		if (itemCats.size()>0){
			for (TbItemCat itemCat:itemCats){
				deleItemCatByParentId(itemCat.getId());  //递归处理，删除了所有下级分类
			}
		}
		//删除了下级分类后，最后再删除传入的当前父类id的分类
		itemCatMapper.deleteByPrimaryKey(id);

	}

	@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
						if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}
	
		}
		
		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbItemCat> findListByParentId(Long parentId) {
		TbItemCatExample example = new TbItemCatExample();
		example.createCriteria().andParentIdEqualTo(parentId);
		return itemCatMapper.selectByExample(example);
	}

}
