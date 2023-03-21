package ccw.serviceinnovation.oss.pojo.vo;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 陈翔
 */
@Data
public class ObjectVo {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 指向真实数据的key
     */
    private String objectKey;

    /**
     * 对象名字[全称]
     */
    private String name;

    /**
     * 桶ID
     */
    private Long bucketId;

    /**
     * 文件扩展名
     */
    private String ext;

    /**
     * 文件的MD5值
     */
    private String md5;

    /**
     * 文件总大小
     */
    private Long size;

    /**
     * 是否为文件夹
     */
    private Boolean isFolder;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 最近更新时间
     */
    private String lastUpdateTime;

    /**
     * 对象访问控制
     */
    private Integer objectAcl;


    /**
     * 所处文件夹全名
     */
    private String folderName;

    /**
     * 短名字
     */
    private String shortName;


}
