package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.oss.pojo.vo.FragmentVo;
import ccw.serviceinnovation.oss.service.IFragmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理上传碎片接口
 * @author 陈翔
 */
@RestController
@RequestMapping("/fragment")
public class FragmentController {

    @Autowired
    IFragmentService fragmentService;

    /**
     * 获取一个桶内所有碎片
     * @param bucketName 桶名字
     * @return
     */
    @GetMapping("/list")
    public ApiResp<List<FragmentVo>> listsFragment(String bucketName){
        return ApiResp.success(fragmentService.listFragments(bucketName));
    }

    /**
     * 删除一个上传事件的碎片信息
     * @param bucketName 桶名字
     * @param blockToken 上传事件唯一ID
     * @return
     */
    @DeleteMapping("")
    public ApiResp<Boolean> deleteFragment(String bucketName,String blockToken){
        return ApiResp.success(fragmentService.deleteFragment(bucketName,blockToken));
    }
}
