package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 业务逻辑实现
 *
 * @author Steven
 */
@Service(interfaceClass = GoodsService.class)
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;


    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;


    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.select(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize, TbGoods goods) {
        PageResult<TbGoods> result = new PageResult<TbGoods>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

        if (goods != null) {
            //如果字段不为空
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                //criteria.andLike("sellerId", "%" + goods.getSellerId() + "%");
                criteria.andEqualTo("sellerId", goods.getSellerId());
            }
            //如果字段不为空
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
            }
            //如果字段不为空
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andLike("auditStatus", "%" + goods.getAuditStatus() + "%");
            }
            //如果字段不为空
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andLike("isMarketable", "%" + goods.getIsMarketable() + "%");
            }
            //如果字段不为空
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andLike("caption", "%" + goods.getCaption() + "%");
            }
            //如果字段不为空
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andLike("smallPic", "%" + goods.getSmallPic() + "%");
            }
            //如果字段不为空
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andLike("isEnableSpec", "%" + goods.getIsEnableSpec() + "%");
            }
            //如果字段不为空
           /* if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andLike("isDelete", "%" + goods.getIsDelete() + "%");
            }*/
           //逻辑删除
            criteria.andIsNull("isDelete");

        }

        //查询数据
        List<TbGoods> list = goodsMapper.selectByExample(example);
        //返回数据列表
        result.setRows(list);

        //获取总页数
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(list);
        result.setPages((long) info.getPages());

        return result;
    }


    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        //设置状态为未审核
        goods.getGoods().setAuditStatus("0");
        goodsMapper.insertSelective(goods.getGoods());


        //设置商品id
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());

        saveItemList(goods);


    }

    private void saveItemList(Goods goods) {
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            //保存sku
            for (TbItem item : goods.getItemList()) {
                //标题sku+spu
                String title = goods.getGoods().getGoodsName();
                Map<String, Object> skuMap = JSON.parseObject(item.getSpec());
                for (String key : skuMap.keySet()) {
                    title = title + " " + skuMap.get(key);

                }
                item.setTitle(title);
                setItemValus(goods, item);
                //保存SKU
                itemMapper.insertSelective(item);


            }
        } else {
            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
            item.setPrice(goods.getGoods().getPrice());//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(99999);//库存数量
            item.setSpec("{}");
            setItemValus(goods, item);
            //保存sku
            itemMapper.insertSelective(item);

        }
    }

    /**
     * 设置sku信息
     *
     * @param goods
     * @param item
     */

    private void setItemValus(Goods goods, TbItem item) {
        //商品图片取SPU的第一张
        List<Map> imgList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imgList.size() > 0) {
            item.setImage(imgList.get(0).get("url").toString());
        }
        //商品类目id
        item.setCategoryid(goods.getGoods().getCategory3Id());

        //查询商品类目内容
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(item.getCategoryid());
        item.setCategory(itemCat.getName());

        //创建日期
        item.setCreateTime(new Date());

        //更新日期
        item.setUpdateTime(item.getCreateTime());


        //所属SPU-id
        item.setGoodsId(goods.getGoods().getId());

        //所属商家
        item.setSellerId(goods.getGoods().getSellerId());

        TbSeller seller = sellerMapper.selectByPrimaryKey(item.getSellerId());
        item.setSeller(seller.getNickName());

        //品牌信息
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(tbBrand.getName());


    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        //修改过的商品，状态设置为未审核，重新审核一次
        goods.getGoods().setAuditStatus("0");
        //更新商品基本信息
        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());
        //更新商品扩展信息
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());
        //更新sku信息，更新前先删除原来的sku
        TbItem where=new TbItem();
        where.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(where);
        //更新sku
        saveItemList(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods getById(Long id) {
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);


        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);

        // 查询商品SKU信息


        TbItem where = new TbItem();
        where.setGoodsId(id);
        List<TbItem> items = itemMapper.select(where);
        goods.setItemList(items);

        return goods;
    }

    /**
     * 批量逻辑删除(不是真删除)
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods tbGoods = new TbGoods();
            tbGoods.setId(id);
            tbGoods.setIsDelete("1");
            //不是真删除
            goodsMapper.updateByPrimaryKeySelective(tbGoods);
        }



    }

    /**
     * 批量修改
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        TbGoods tbGoods = new TbGoods();
        tbGoods.setAuditStatus(status);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        List longs = Arrays.asList(ids);
        criteria.andIn("id", longs);
        goodsMapper.updateByExampleSelective(tbGoods, example);

    }

    @Override
    public void updateMarketable(Long id, String marketable) {
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsMarketable(marketable);
        goodsMapper.updateByPrimaryKeySelective(tbGoods);

    }

    @Override
    public List<TbItem> findItemListByGoodsIdsAndStatus(Long[] goodsIds, String status) {
        Example example = new Example(TbItem.class);
        Example.Criteria criteria = example.createCriteria();

        List longs=Arrays.asList(goodsIds);
        criteria.andIn("goodsId",longs);
        criteria.andEqualTo("status", status);

        //查询结果
        List<TbItem> tbItems = itemMapper.selectByExample(example);

        return tbItems;
    }




}
