package entity;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable {
    //因为前端angular的分页构件需要这两个数据，故而后端可以将这两个数据封装到一个对象中！！！
    private long total;
    private List rows;  //当前页的记录

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }
}
