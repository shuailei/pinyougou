package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import entity.Cart;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private TbPayLogMapper payLogMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		/*orderMapper.insert(order);		*/
		//生成订单
		//获取购物车列表
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

		List ids = new ArrayList<>();

		double payTotalFee = 0.0; //支付总金额
		//循环所有购物车转成订单对象并保存到数据库
		for (Cart cart : cartList) {
			TbOrder dbOrder = new TbOrder();
			dbOrder.setUserId(order.getUserId()); //设置下单人
			dbOrder.setStatus("1");//'状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价',
			dbOrder.setSourceType("2");//'订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端',
			dbOrder.setReceiverMobile(order.getReceiverMobile());
			dbOrder.setReceiver(order.getReceiver());
			dbOrder.setReceiverAreaName(order.getReceiverAreaName());
			dbOrder.setCreateTime(new Date()); //订单生成时间
			dbOrder.setPaymentType(order.getPaymentType()); //支付方式
			dbOrder.setUpdateTime(new Date());
			dbOrder.setSellerId(cart.getSellerId());  //设置商家id

			long orderId = idWorker.nextId();
			ids.add(orderId);
			dbOrder.setOrderId(orderId);  //采用雪花算法

			double orderTotalFee = 0.0;
			//取出每个购物车中的所有商品明细保存到数据库中
			List<TbOrderItem> orderItemList = cart.getOrderItemList();
			for (TbOrderItem orderItem : orderItemList) {
				orderItem.setOrderId(dbOrder.getOrderId());  //维护多对一关系
				orderItem.setId(idWorker.nextId());        //设置订单明细也采用雪花id算法
				orderTotalFee += orderItem.getTotalFee().doubleValue();
				orderItemMapper.insert(orderItem); //将订单明细保存到数据库中
			}

			dbOrder.setPayment(new BigDecimal(orderTotalFee)); //订单总金额

			payTotalFee += orderTotalFee;  //付款总金额
			orderMapper.insert(dbOrder); //保存到数据库中
		}

		//清空购物车
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());

		//生成支付单对象 tb_pay_log表与tb_order表之间是一种"打断设计"
		TbPayLog payLog = new TbPayLog();
		payLog.setTradeState("0"); //0未支付  1已支付
		payLog.setOutTradeNo(idWorker.nextId()+"");  //支付单号
		//设置支付单中对应的订单id，string类型中间用，号分割
		payLog.setOrderList(ids.toString().replaceAll(" ", "").replace("[","" ).replace("]", ""));
		payLog.setCreateTime(new Date()); //创建时间
		payLog.setPayType("1"); //微信支付
		payLog.setTotalFee((long)(payTotalFee * 100)); //设置支付总金额;
		payLog.setUserId(order.getUserId());  //支付单用户

		payLogMapper.insert(payLog); //数据库存入payLog对象

		//将当前的支付单对象存到redis
		redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
