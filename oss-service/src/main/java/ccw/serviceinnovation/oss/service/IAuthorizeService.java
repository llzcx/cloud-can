package ccw.serviceinnovation.oss.service;


import ccw.serviceinnovation.common.entity.Authorize;
import ccw.serviceinnovation.oss.pojo.dto.PutAuthorizeDto;
import ccw.serviceinnovation.oss.pojo.vo.AuthorizeVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**bucket授权策略业务类
 * @author 陈翔
 */

public interface IAuthorizeService {

    /**
     * 拉取一个桶的所有授权策略
     * @param bucketName
     * @return
     */
    RPage<AuthorizeVo> listAuthorizes(String bucketName, Integer pageNum, Integer pageSize);

    /**
     *为bucket添加一个授权策略(当authorizeId为null)
     * 为bucket的授权策略id为authorizeId更新(当authorizeId不为null)
     * @param putAuthorizeDto
     * @param bucketName
     * @param authorizeId
     * @return
     */
    Boolean putAuthorize(PutAuthorizeDto putAuthorizeDto,String bucketName,Long authorizeId);


    /**
     *删除一个桶里面的授权策略
     * @param bucketName
     * @param authorizeId
     * @return
     */
    Boolean deleteAuthorize(String bucketName,Long authorizeId);

}
