package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;



/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.manager.controller
 * @date 2019-5-21
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }


    @RequestMapping("/findPage")
    public PageResult<TbBrand> findPage(Integer pageNum,Integer pageSize,@RequestBody TbBrand tbBrand){
        PageResult<TbBrand> result = brandService.findByPage(pageNum, pageSize,tbBrand);
        return result;
    }

    Logger logger = Logger.getLogger(BrandController.class);

    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand) {
        try {
            brandService.add(tbBrand);
            return new Result(true, "成功保存");
        } catch (Exception e) {
            logger.error("保存品牌出现异常",e);
            return new Result(false, "失败保存");
        }
    }


    @RequestMapping("/getById")
    public TbBrand getById(Long id) {
        TbBrand brand = brandService.getById(id);
        return brand;
    }


    @RequestMapping("/update")
    public Result updateBrand(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand);
            return new Result(true, "成功修改");
        } catch (Exception e) {
            logger.error("修改品牌出现异常", e);
            return new Result(false, "修改失败");
        }

    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            brandService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }

    }

    /**
     * 跟据id列表，更新状态
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            brandService.updateStatus(ids, status);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"操作失败!");
        }
    }

}
