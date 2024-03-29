package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.manager.mq.MessageSender;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.EsItem;
import entity.MessageInfo;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 请求处理器
 * @author Steven
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	/*@Reference
	private ItemSearchService searchService;*/

	/*@Reference
	private ItemPageService itemPageService;*/


	@Autowired
	private MessageSender messageSender;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 分页查询数据
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int pageNo,int pageSize,@RequestBody TbGoods goods){			
		return goodsService.findPage(pageNo, pageSize,goods);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/getById")
	public Goods getById(Long id){
		return goodsService.getById(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids){
		try {
			goodsService.delete(ids);
			//删除索引库
			//删除索引库
			//searchService.deleteByGoodsId(ids);
			//发送MQ消息
			MessageInfo info = new MessageInfo(
					MessageInfo.METHOD_DELETE,  //指定删除索引标识
					ids, //内容
					"topic-goods",
					"tag-goods-delete",
					"key-goods-delete"
			);
			messageSender.sendMessage(info);

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
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long [] ids,String status){
		try {
			goodsService.updateStatus(ids,status);

			//如果是审核通过
			if ("1".equals(status)) {
				//先查询SKU列表
				List<TbItem> items = goodsService.findItemListByGoodsIdsAndStatus(ids, status);
				if (items!=null&&items.size()>0) {
					//es数据实体列表
					List<EsItem> esItemList = new ArrayList<>();
					EsItem esItem = null;
					for (TbItem item : items) {
						esItem = new EsItem();
						//使用spring的BeanUtils深克隆对象
		 				//相当于把TbItem所有属性名与数据类型相同的属性值设置到EsItem中
						BeanUtils.copyProperties(item,esItem);
						//注意，这里价格的类型不一样需要单独设置
						esItem.setPrice(item.getPrice().doubleValue());
						//嵌套域-规格数据绑定
						Map specMap = JSON.parseObject(item.getSpec(), Map.class);
						esItem.setSpec(specMap);
						//组装es实体列表
						esItemList.add(esItem);
					}
					/*//导入索引库
					searchService.importList(esItemList);*/

					//发送MQ消息
					MessageInfo info = new MessageInfo(
							MessageInfo.METHOD_ADD, //指定增加索引标识
							esItemList,//内容
							"topic-goods",
							"tag-goods-add",
							"key-goods-add"
					);
					messageSender.sendMessage(info);

                    /*//2.生成商品静态化
                    for (Long goodsId : ids) {
                        itemPageService.genItemHtml(goodsId);
                    }*/


                }
			} else {
				System.out.println("没有找到SKU数据");
			}







			return new Result(true, "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "操作失败");
		}
	}



	/*@RequestMapping("/genHtml")
	public Boolean genHtml(Long goodsId) {

		return itemPageService.genItemHtml(goodsId);


	}*/


}
