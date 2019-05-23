package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.ItemCartService;
import entity.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private ItemCartService cartService;

    //获取到当前controller的request
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    //从cookie中获取uuid唯一redis的key值的方法 除了uuid还可以用sessionID
    public String getUuid(){
        String uuid = CookieUtil.getCookieValue(request, "uuid", "utf-8");
        if(uuid == null || uuid.length() < 1){
            uuid = UUID.randomUUID().toString(); //获取uuid
            //再保存到cookie中  这个uuid的保存时间是2天，就是不登录，这个购物车两天之后就失效没了
            CookieUtil.setCookie(request, response, "uuid", uuid, 48*60*60, "utf-8");
        }
        return uuid;
    }


    //添加商品到购物车列表
    @RequestMapping("/addItemToCartList")
    public Result addItemToCartList( Long itemId, Integer num){

        /**
         * 如果登录，则是security的name的购物车，如果没登录则是uuid的name的购物车
         */
        //从springSecurity中获取当前登录用户名  登录与否最大的区别就是这个name，没登录就是uuid，登录就从security中获取name
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        //判断当前用户是否登录  anonymousUser匿名角色代表没登录！
        if("anonymousUser".equals(name)){ //代表未登录
            name = getUuid();
        }

        List<Cart> cartList = null;
        try {
            cartList = cartService.findCartListFromRedis(name);  //从redis中获取当前用户的购物车

            cartList = cartService.addItemToCartList(cartList, itemId, num); //添加商品到购物车

            //保存购物车到redis中 name作为key
            cartService.saveCartListToRedis(name, cartList); //存储添加商品后的购物车

            return new Result(true,"添加商品到购物车成功！！！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加商品到购物车失败！！！");
        }
    }


    //查询购物车  都是根据uuid查询、新增
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){

        //从springSecurity中获取当前登录用户名
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();

        String uuidName = getUuid();

        List<Cart> cartList2 = cartService.findCartListFromRedis(uuidName); //未登录购物车
        //判断当前用户是否登录
        if(!"anonymousUser".equals(loginName)){ //代表登录

            List<Cart> cartList1 = cartService.findCartListFromRedis(loginName); //登录购物车


            //合并购物车,判断未登录购物车是否还有数据,如果未登录购物车还有商品，进行合并，否则跳过该逻辑
            if(cartList2.size() > 0){
                cartList1 = cartService.mergeCartList(cartList1, cartList2);

                //将合并后的购物车保存redis中
                cartService.saveCartListToRedis(loginName, cartList1);

                //清除未登录的购物车数据
                cartService.delCartListToRedis(uuidName);
            }
            return cartList1;
        }
        return cartList2;
    }

}
