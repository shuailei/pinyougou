package com.pinyougou.mapper;

import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbSeckillOrderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_seckill_order
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int countByExample(TbSeckillOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_seckill_order
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int deleteByExample(TbSeckillOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_seckill_order
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_seckill_order
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int insert(TbSeckillOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_seckill_order
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int insertSelective(TbSeckillOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_seckill_order
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    List<TbSeckillOrder> selectByExample(TbSeckillOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_seckill_order
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    TbSeckillOrder selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_seckill_order
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int updateByExampleSelective(@Param("record") TbSeckillOrder record, @Param("example") TbSeckillOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_seckill_order
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int updateByExample(@Param("record") TbSeckillOrder record, @Param("example") TbSeckillOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_seckill_order
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int updateByPrimaryKeySelective(TbSeckillOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_seckill_order
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int updateByPrimaryKey(TbSeckillOrder record);
}