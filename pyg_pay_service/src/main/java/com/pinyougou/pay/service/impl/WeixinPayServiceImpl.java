package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import util.HttpClientUtil;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;//公众账号ID

    @Value("${partner}")   //商户号
    private String mch_id;

    @Value("${notifyurl}")
    private String notifyurl;

    @Value("${partnerkey}")
    private String partnerkey;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbPayLogMapper payLogMapper;

    @Autowired
    private TbOrderMapper orderMapper;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {

        //通过httpclient工具类请求微信生成订单的api  根据微信生成订单api需要的参数封装到一个map中，利用httpclient进行交互！！
        Map requestMap = new HashMap<>();
        requestMap.put("appid", appid);    //应用id
        requestMap.put("mch_id", mch_id);  //商户号
        requestMap.put("nonce_str", WXPayUtil.generateNonceStr()); //随机字符串
        requestMap.put("body", "商品描述");
        requestMap.put("out_trade_no", out_trade_no); // 商户支付单号
        requestMap.put("total_fee", total_fee);    //支付金额是分
        requestMap.put("spbill_create_ip", "127.0.0.1");  //终端ip
        requestMap.put("notify_url", notifyurl);  //回调地址必须给，但是没用
        requestMap.put("trade_type", "NATIVE");  //交易类型


        try {
            //通过工具类获取xml格式并带签名的string字符串
            String xmlString = WXPayUtil.generateSignedXml(requestMap, partnerkey);//微信工具类中有map转xml的api,带上签名

            HttpClientUtil client = new HttpClientUtil("https://api.mch.weixin.qq.com/pay/unifiedorder");

            //封装请求内容  第一步
            client.setXmlParam(xmlString);

            //设置请求协议方式https  第二步
            client.setHttps(true);

            //发送post请求  第三步
            client.post();

            //获取xml的返回内容   第四步
            String content = client.getContent();

            System.out.println("微信发送=="+xmlString);
            System.out.println("微信返回=="+content);

            //再转成map
            Map<String, String> wxMap = WXPayUtil.xmlToMap(content);

            //封装返回值
            Map responseMap = new HashMap<>();
            responseMap.put("out_trade_no", out_trade_no);   //支付单号
            responseMap.put("total_fee", total_fee);         //支付金额
            responseMap.put("code_url", wxMap.get("code_url"));  //支付地址

            return responseMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {

        try {
            //1.map封装请求参数
            Map requestMap = new HashMap<>();
            requestMap.put("appid", appid);
            requestMap.put("mch_id", mch_id);
            requestMap.put("out_trade_no", out_trade_no);
            requestMap.put("nonce_str", WXPayUtil.generateNonceStr());

            String xmlString = WXPayUtil.generateSignedXml(requestMap, partnerkey);

            //2.通过微信工具类将map转成带签名的xml
            HttpClientUtil clientUtil = new HttpClientUtil("https://api.mch.weixin.qq.com/pay/orderquery");

            //3.通过httpClient工具类，发送xml格式的请求，设置请求协议的方式
            clientUtil.setXmlParam(xmlString);
            clientUtil.setHttps(true);
            clientUtil.post();

            //4.获取返回值，通过工具类将xml的字符串转成map
            String content = clientUtil.getContent();

            System.out.println("微信请求="+xmlString);
            System.out.println("微信返回="+content);

            return WXPayUtil.xmlToMap(content);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());
        payLog.setTradeState("1"); //已支付
        payLog.setTransactionId(transaction_id); //微信业务代码
        payLogMapper.updateByPrimaryKey(payLog);

        String[] orderIds = payLog.getOrderList().split(",");
        //修改订单状态
        for (String orderId : orderIds) {
            TbOrder order = orderMapper.selectByPrimaryKey(new Long(orderId));
            order.setPaymentTime(new Date());
            order.setStatus("2");//'状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价',
            orderMapper.updateByPrimaryKey(order);
        }

        //清空redis中的支付单信息
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }
}
