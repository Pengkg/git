package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	private TbSpecificationOptionMapper optionMapper;


	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize,TbTypeTemplate typeTemplate) {
		PageResult<TbTypeTemplate> result = new PageResult<TbTypeTemplate>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						//如果字段不为空
			if (typeTemplate.getName()!=null && typeTemplate.getName().length()>0) {
				criteria.andLike("name", "%" + typeTemplate.getName() + "%");
			}
			//如果字段不为空
			if (typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0) {
				criteria.andLike("specIds", "%" + typeTemplate.getSpecIds() + "%");
			}
			//如果字段不为空
			if (typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0) {
				criteria.andLike("brandIds", "%" + typeTemplate.getBrandIds() + "%");
			}
			//如果字段不为空
			if (typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0) {
				criteria.andLike("customAttributeItems", "%" + typeTemplate.getCustomAttributeItems() + "%");
			}
	
		}

        //查询数据
        List<TbTypeTemplate> list = typeTemplateMapper.selectByExample(example);
        //返回数据列表
        result.setRows(list);

        //获取总页数
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(list);
        result.setPages((long) info.getPages());

        //数据存入redis
		this.saveToRedis();
		
		return result;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insertSelective(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate getById(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//数组转list
        List longs = Arrays.asList(ids);
        //构建查询条件
        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", longs);

        //跟据查询条件删除数据
        typeTemplateMapper.deleteByExample(example);
	}

	/**
	 * 规格集合
	 * @param id
	 * @return
	 */
	@Override
	public List<Map> findSpecList(Long id) {
		//查询模板
		TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);

		//json转成List<map>
		List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
		TbSpecificationOption where=null;
		for (Map map : list) {

			where = new TbSpecificationOption();
			where.setSpecId(Long.valueOf(map.get("id").toString()));
			List<TbSpecificationOption> options = optionMapper.select(where);
			map.put("options", options);
		}



		return list;



	}

	/**
	 * 修改模板状态
	 * @param ids
	 * @param status
	 */
	@Override
	public void updateStatus(Long[] ids, String status) {
		//要更新的内容
		TbTypeTemplate typeTemplate = new TbTypeTemplate();
		typeTemplate.setStatus(status);
		//更新条件组装
		Example where = new Example(TbTypeTemplate.class);
		Example.Criteria criteria = where.createCriteria();
		List longs = Arrays.asList(ids);
		criteria.andIn("id", longs);
		//执行更新
		typeTemplateMapper.updateByExampleSelective(typeTemplate, where);
	}



	@Autowired
	RedisTemplate redisTemplate;
	/**
	 * 将数据保存redis中
	 */
	public void saveToRedis() {
		//分别将品牌数据和规格数据放入缓存（Hash）。以模板ID作为key,以品牌列表和规格列表作为值。
		List<TbTypeTemplate> templates = findAll();
		for (TbTypeTemplate template : templates) {
			//缓存品牌
			List<Map> brandList = JSON.parseArray(template.getBrandIds(), Map.class);
			redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);

			//缓存规格
			List<Map> specList = findSpecList(template.getId());
			redisTemplate.boundHashOps("specList").put(template.getId(), specList);

		}


	}


}
