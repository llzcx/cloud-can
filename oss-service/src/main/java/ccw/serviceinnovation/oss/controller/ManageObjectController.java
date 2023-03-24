package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IManageObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理对象接口
 * @author 杨世博
 *
 */
@RestController
@RequestMapping("/manageObject")
public class ManageObjectController {

    @Autowired
    IManageObjectService manageObjectService;

    /**
     * 获取Object列表
     * 1-根据用户Id筛选
     * 2-根据bucketId，bucketName筛选
     * @param userId
     * @param bucketId
     * @param bucketName
     * @param pageNum
     * @param size
     * @return
     */
    @GetMapping("/listObjects")
    public ApiResp<RPage<OssObject>> listObject(@RequestParam("userId")Long userId,
                                                @RequestParam("bucketId")Long bucketId,
                                                @RequestParam("bucketName")String bucketName,
                                                @RequestParam("pageNum")Integer pageNum,
                                                @RequestParam("size")Integer size) throws Exception{
        RPage<OssObject> ossObjectRPage = manageObjectService.getObjectList(userId,bucketId,bucketName,pageNum,size);
        return null;
    }

    /**
     * 删除Object及其相关信息
     * 1-标签
     * @param id
     * @return
     */
    @DeleteMapping("/deleteObject")
    public ApiResp<Boolean> deleteObject(@RequestParam("id")Long id) throws Exception{
        Boolean flag = manageObjectService.deleteObject(id);
        return ApiResp.ifResponse(flag,flag, ResultCode.COMMON_FAIL);
    }
}
