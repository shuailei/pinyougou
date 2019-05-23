package com.pinyougou.mapper;

import com.pinyougou.pojo.TbProvinces;
import com.pinyougou.pojo.TbProvincesExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbProvincesMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_provinces
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int countByExample(TbProvincesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_provinces
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int deleteByExample(TbProvincesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_provinces
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_provinces
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int insert(TbProvinces record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_provinces
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int insertSelective(TbProvinces record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_provinces
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    List<TbProvinces> selectByExample(TbProvincesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_provinces
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    TbProvinces selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_provinces
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int updateByExampleSelective(@Param("record") TbProvinces record, @Param("example") TbProvincesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_provinces
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int updateByExample(@Param("record") TbProvinces record, @Param("example") TbProvincesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_provinces
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int updateByPrimaryKeySelective(TbProvinces record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_provinces
     *
     * @mbggenerated Fri Dec 21 12:39:48 CST 2018
     */
    int updateByPrimaryKey(TbProvinces record);
}