package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @BelongsProject: pinyougou_parent
 * @BelongsPackage: com.pinyougou.sellergoods.service.impl
 * @Author: pinyougou
 * @CreateTime: 2019-05-21 19:30
 * @Description: ${Description}
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.select(null);
    }

    @Override
    public PageResult<TbBrand> findByPage(int pageNum, int pageSize,TbBrand tbBrand) {
        PageResult<TbBrand> result = new PageResult<>();
        PageHelper.startPage(pageNum, pageSize);

        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();

        if (tbBrand.getName()!=null&&tbBrand.getName().trim().length()>0) {
            criteria.andLike("name", "%" + tbBrand.getName() + "%");
        }
        if (tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().trim().length()>0) {
            criteria.andEqualTo("firstChar", tbBrand.getFirstChar());
        }

        //List<TbBrand> list = brandMapper.select(null);
        List<TbBrand> list = brandMapper.selectByExample(example);
        result.setRows(list);
        PageInfo<TbBrand> info = new PageInfo<>(list);
        result.setPages((long) info.getPages());
        return result;

    }

    @Override
    public void add(TbBrand tbBrand) {
        brandMapper.insertSelective(tbBrand);
    }

    @Override
    public TbBrand getById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand tbBrand) {
        brandMapper.updateByPrimaryKeySelective(tbBrand);
    }

    @Override
    public void delete(Long[] ids) {
        List longs = Arrays.asList(ids);
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", longs);
        brandMapper.deleteByExample(example);
    }

    /**
     * 跟据id列表，更新状态
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        //更新内容
        TbBrand brand = new TbBrand();
        brand.setStatus(status);
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        List longs =Arrays.asList(ids);
        criteria.andIn("id", longs);
        //执行更新
        brandMapper.updateByExampleSelective(brand, example);

    }


}
