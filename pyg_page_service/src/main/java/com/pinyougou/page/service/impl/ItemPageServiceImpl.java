package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import entity.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Override
    public void createItemHtml(Long goodsId) {
        try {
            //准备输出参数
            //先准备spu对象
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            //在准备spu的详情
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //最后封装数据
/*            Goods gs = new Goods();
            gs.setGoods(goods);
            gs.setGoodsDesc(goodsDesc);*/

            //准备sku列表信息
            TbItemExample example = new TbItemExample();
            example.createCriteria().andGoodsIdEqualTo(goodsId);
            example.setOrderByClause("is_default desc");
            List<TbItem> items = itemMapper.selectByExample(example);



            //一级分类，二级分类，三级分类
            String itemCat1Name = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2Name = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3Name = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            Map map = new HashMap();
            map.put("goods", goods);
            map.put("goodsDesc", goodsDesc);
            map.put("itemCat1Name", itemCat1Name);
            map.put("itemCat2Name", itemCat2Name);
            map.put("itemCat3Name", itemCat3Name);
            map.put("itemList", items);

            Configuration configuration = configurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            FileWriter writer = new FileWriter(new File("/Users/wanglei/Desktop/item" + goodsId + ".html"));
            template.process(map, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
