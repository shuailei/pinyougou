package com.pinyougou.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

//基于角色的访问控制 rbac（role base access controller）
// 表结构关系：用户表 角色表 模块表（权限表）这三张表，另外还有俩张中间表，用于维护用户与角色的一对多关系，以及角色与权限的一对多关系
public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //根据sellerId获取seller对象   用户表
        TbSeller seller = sellerService.findOne(username);

        //数据库查询到当前用户，并且运营商审核该商家后
        if(seller!=null && "1".equals(seller.getStatus())){
            //声明角色列表
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

            //参数1：用户名  参数2：数据库中的用户密码  参数3：角色列表
            // 这个User对象返给Springsecurity框架了，这样security框架就有用户名，用户密码，以及角色列表了，
            //有了这些东西，框架就可以再根据你输入的用户名和密码来进行比较，尤其是密码，当你输入的密码与当前从数据库中查到的密码一致时放行！！！
            return new User(username,seller.getPassword(),authorities);
        }
        return null;
    }
}
