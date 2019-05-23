package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService payService;

    //native表示微信的支付方式
    @RequestMapping("/createNative")
    public Map createNative(){

        //String uuid = UUID.randomUUID().toString().replaceAll("-", "");'
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        TbPayLog payLog = payService.searchPayLogFromRedis(userId);

        //payLog.getTotalFee() + ""   支付时，需要订单号以及支付的金额，这里指定了是2分钱
        return payService.createNative(payLog.getOutTradeNo(),  "2");
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){

        int timeout = 1;

        //循环请求支付状态   页面关了，请求还会一直请
        while (true){
            Map map = payService.queryPayStatus(out_trade_no);

            if(map==null){
                return new Result(false, "支付失败！！");
            }

            //支付成功逻辑
            if("SUCCESS".equals(map.get("trade_state"))){
                //支付成功后，修改支付单状态，修改微信的单号，并且修改支付单对应的订单状态及订单支付时间
                payService.updateOrderStatus(out_trade_no,map.get("transaction_id").toString());

                return new Result(true, "支付成功！！");
            }

            try {
                Thread.sleep(3000);  //3秒一请求 查询支付状态
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //处理请求超时 20秒没支付，就会超时，让上面的请求停止
            if(timeout > 20){
                return new Result(false, "timeout");
            }
            timeout++;
        }


    }
}
