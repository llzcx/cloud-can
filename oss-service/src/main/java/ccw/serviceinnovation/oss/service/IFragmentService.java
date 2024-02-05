package ccw.serviceinnovation.oss.service;


import ccw.serviceinnovation.oss.pojo.vo.FragmentVo;

import java.util.List;

/**
 * 管理桶内上传的碎片
 * @author 陈翔
 */
public interface IFragmentService {

    /**
     * 查询碎片
     * @param bucketName 桶名
     * @return
     */
    List<FragmentVo> listFragments(String bucketName);


    /**
     * 删除碎片
     * @param bucketName 桶名
     * @param blockToken
     * @return
     */
    Boolean deleteFragment(String bucketName,String blockToken);
}
