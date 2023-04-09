package ccw.serviceinnovation.oss.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取权限策略的Vo
 * @author 陈翔
 */
@Data
public class AuthorizeVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 授权策略唯一ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 该Authorize的授权用户是否为所有人[包括了子用户和外用户]
     */
    private Boolean userIsAll;

    /**
     * 该Authorize的目标路径是桶内所有对象
     */
    private Boolean pathIsAll;

    /**
     * 操作效力的编码值
     */
    private Integer operation;

    /**
     * 路径列表 路径前缀匹配 比如 folder1/folder2/folder3/demo.mp4 代表某一个对象
     * 如果需要指定多个对象可以使用路径通配符,比如folder1/folder2/folder3/* 代表这一文件夹下下所有对象
     */
    String[] paths;

    /**
     * 子用户Id列表 该用户的子用户id列表
     */
    String[] sonUser;

    /**
     * 其他用户的id列表[其他用户可以是子用户,也可以是主用户]
     */
    String[] otherUser;

}
