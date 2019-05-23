package com.pinyougou.cart.service;

import entity.Cart;

import java.util.List;

public interface ItemCartService {

    // 添加商品到购物车列表(1.要加商品的列表  2.加入商品的id  3.加入商品数量)
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);
    // 根据唯一key值获取redis中的购物车数据
    public List<Cart> findCartListFromRedis(String name);

    // 根据唯一的key值保存购物车
    public void saveCartListToRedis(String name, List<Cart> cartList);

    // 合并购物车，未登录，已登录
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);

    // 根据唯一的key移除掉redis中的购物车数据
    public void delCartListToRedis(String name);
}
