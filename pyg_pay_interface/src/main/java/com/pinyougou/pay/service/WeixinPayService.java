package com.pinyougou.pay.service;

import com.pinyougou.pojo.TbPayLog;

import java.util.Map;

public interface WeixinPayService {

    //参数1：商户订单号，参数2：总金额
    public Map createNative(String out_trade_no, String total_fee);

    //查询订单状态
    public Map queryPayStatus(String out_trade_no);

    //从redis中获取支付对象
    public TbPayLog searchPayLogFromRedis(String userId);


    //修改支付订单状态为1已支付同时更新微信支付订单号
    public void updateOrderStatus(String out_trade_no, String transaction_id);
}
