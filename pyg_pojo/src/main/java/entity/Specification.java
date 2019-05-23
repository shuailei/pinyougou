package entity;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * 前端规格页面Specification.html新建的'规格编辑框' 所需要的所有数据
 * 封装成一个Specification对象，以便实现规格新增的目的！！！
 */
public class Specification implements Serializable {

    private TbSpecification specification;  //规格

    private List<TbSpecificationOption> specOptionList; //该规格下的规格选项，作为一个集合来处理

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getSpecOptionList() {
        return specOptionList;
    }

    public void setSpecOptionList(List<TbSpecificationOption> specOptionList) {
        this.specOptionList = specOptionList;
    }
}
