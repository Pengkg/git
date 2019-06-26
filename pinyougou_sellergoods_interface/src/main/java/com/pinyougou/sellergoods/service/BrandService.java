package com.pinyougou.sellergoods.service;

import entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

/**
 * @BelongsProject: pinyougou_parent
 * @BelongsPackage: PACKAGE_NAME
 * @Author: pinyougou
 * @CreateTime: 2019-05-21 19:22
 * @Description: ${Description}
 */
public interface BrandService {
    public List<TbBrand> findAll();

    public PageResult<TbBrand> findByPage(int pageNum, int pageSize,TbBrand tbBrand);

    public void add(TbBrand tbBrand);

    public TbBrand getById(Long id);

    public void update(TbBrand tbBrand);

    public void delete(Long[] ids);
    /**
     * 跟据id列表，更新状态
     * @param ids
     * @param status
     */
    public void updateStatus(Long[] ids,String status);

}
