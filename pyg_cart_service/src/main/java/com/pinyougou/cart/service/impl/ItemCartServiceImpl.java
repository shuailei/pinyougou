package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.ItemCartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemCartServiceImpl implements ItemCartService {

    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 购物车存进redis中，有了redisTemplate就能搞到redis了
     */
    @Autowired
    private RedisTemplate redisTemplate;

    /*
    需求：往购物车列表增加商品
    先判断商家是否有购物车，如果没有，则直接new，如果有购物车，没有商品则添加商品；如果有购物车，有商品则将商品数量对应增加；
     */
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //通过itemId获取item对象  根据产品item，可获取产品所属的商家id以及商家名称
        TbItem item = itemMapper.selectByPrimaryKey(itemId);

        //先判断该商家是否已经存在购物车
        Cart cart = searchCartListBySellerId(cartList, item.getSellerId());

        if(cart == null){ //该商家没有购物车

            //通过skuid获取item对象，并向购物车对象添加具体orderItem对象
            TbOrderItem orderItem = createOrderItem(item,num);

            List<TbOrderItem> orderItems = new ArrayList<>();
            orderItems.add(orderItem); //将本次购买的订单明细增加到明细列表中

            //创建购物车添加到购物车列表
            cart = new Cart();
            cart.setSellerId(item.getSellerId()); //设置购物车商家id
            cart.setSellerName(item.getSeller()); //设置购物车商家名称
            cart.setOrderItemList(orderItems);
            cartList.add(cart);

        }else{ //找到该商家

            //再次判断是否该购物车中已经存在该订单明细商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if(orderItem == null){

                //直接将商品添加到购物车明细列表中
                orderItem = createOrderItem(item,num);
                cart.getOrderItemList().add(orderItem);
            }else{ //如果已经存在该商品
                //直接累加数量
                orderItem.setNum(orderItem.getNum() + num); //设置商品数量
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));

                if(orderItem.getNum() < 1){ //数量已经小于1，不再购买
                    cart.getOrderItemList().remove(orderItem); //将当前商品从购物车明细列表中移除
                }

                //如果该购物车中的商品明细列表无任何商品，将购物车从购物车列表中移除
                if(cart.getOrderItemList().size() < 1){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    //判断该商家是否已经存在购物车列表中
    private Cart searchCartListBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){ //判断购物车中商家的id是否跟传入的id一致，代表已经存在购物车
                return cart;
            }
        }
        return null;
    }


    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
             if(orderItem.getItemId().equals(itemId)){ //判断该购物车已经存在该商品
                 return orderItem;
             }
        }

        return null;
    }

    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num)); //总金额
        orderItem.setPrice(item.getPrice()); //单价
        orderItem.setNum(num);  //订单明细数量
        orderItem.setTitle(item.getTitle()); //商品明细标题
        orderItem.setPicPath(item.getImage());  //商品图片
        orderItem.setSellerId(item.getSellerId());  //商家
        orderItem.setItemId(item.getId());  //订单明细对应的skuid;
        orderItem.setGoodsId(item.getGoodsId());
        return orderItem;
    }


    //从redis中返回当前购物车
    public List<Cart> findCartListFromRedis(String name){
        //根据唯一的key值获取redis中的购物车数据   登录用户名name
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(name);
        if(cartList == null){ //防止取出空数据
            cartList = new ArrayList<Cart>();
        }

        return cartList;
    }

    @Override
    public void saveCartListToRedis(String name, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(name, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList1) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                cartList2 = addItemToCartList(cartList2, orderItem.getItemId(), orderItem.getNum());
            }

        }
        return cartList2;
    }

    @Override
    public void delCartListToRedis(String name) {
        redisTemplate.boundHashOps("cartList").delete(name);
    }
}
