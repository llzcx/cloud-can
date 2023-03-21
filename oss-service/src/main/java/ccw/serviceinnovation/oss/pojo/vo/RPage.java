package ccw.serviceinnovation.oss.pojo.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页类
 * @author 陈翔
 */
@Data
public class RPage<T> implements Serializable {
    private static final long serialVersionUID = 5760097915453738435L;  
    public static final int DEFAULT_PAGE_SIZE = 10;  
    /** 
     * 每页显示个数 
     */ 
    private int pageSize;
    /** 
     * 当前页数 
     */ 
    private int currentPage;  
    /** 
     * 总页数 
     */ 
    private long totalPage;
    /** 
     * 总记录数 
     */ 
    private long totalCount;

    /**
     * es 特殊分页机制
     */
    private String LastId;


    /** 
     * 结果列表 
     */ 
    private List<T> rows;


       
    public RPage(){
         this.currentPage = 1;  
         this.pageSize = DEFAULT_PAGE_SIZE;  
    }
    public RPage(Integer currentPage, Integer pageSize, List<T> data){
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.rows = data;
    }



    public RPage(String LastId, Integer pageSize, List<T> data){
        this.LastId = LastId;
        this.pageSize = pageSize;
        this.rows = data;
    }
    public RPage(IPage<T> page){
        this.currentPage=page.getCurrent()<=0?1:currentPage;
        this.pageSize=page.getSize()<=0?1:pageSize;
        this.rows = page.getRecords();
        this.totalPage = page.getPages();
        this.totalCount = page.getTotal();
    }
    public void setTotalCountAndTotalPage(Integer total){
        this.setTotalCount(total);
        //总页数除以每页
        int sum = total%pageSize==0? total/pageSize : total/pageSize+1;
        this.setTotalPage(sum);
    }
}
