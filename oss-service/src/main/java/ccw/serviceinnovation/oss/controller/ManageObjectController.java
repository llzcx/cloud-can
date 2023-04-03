package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.pojo.vo.ManageObjectDetailedVo;
import ccw.serviceinnovation.oss.pojo.vo.ManageObjectListVo;
import ccw.serviceinnovation.oss.pojo.vo.ObjectVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IManageObjectService;
import ccw.serviceinnovation.oss.service.IObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    IObjectService objectService;

    /**
     * 获取Object列表
     * 1-根据用户Id筛选
     * 2-根据bucketId，bucketName筛选
     * @param keyword
     * @param pageNum
     * @param size
     * @return
     */
    @GetMapping("/listObjects")
    public ApiResp<RPage<ManageObjectListVo>> listObject(@RequestParam("keyword")String keyword,
                                                         @RequestParam("pageNum")Integer pageNum,
                                                         @RequestParam("size")Integer size) throws Exception{
        RPage<ManageObjectListVo> ossObjectRPage = manageObjectService.getObjectList(keyword,pageNum,size);
        return ApiResp.success(ossObjectRPage);
    }

    /**
     * 删除Object及其相关信息
     * 1-标签
     * @param objectIdList
     * @return
     */
    @DeleteMapping("/deleteObject")
    public ApiResp<Boolean> deleteObject(@RequestBody List<Long> objectIdList) throws Exception{
        Boolean flag = manageObjectService.deleteObject(objectIdList);
        return ApiResp.ifResponse(flag,flag, ResultCode.COMMON_FAIL);
    }

    /**
     * 获取这个对象的详细信息
     * @param id
     * @return
     */
    @GetMapping("/getObject")
    public ApiResp<ManageObjectDetailedVo> getObject(@RequestParam("id")Long id){
        ManageObjectDetailedVo objectVo = manageObjectService.getObject(id);
        return ApiResp.success(objectVo);
    }

    /**
     * 获取文件夹中的对象
     * @param keyword 搜索的关键词：文件前缀名
     * @param parent 父文件夹
     * @param pageNum
     * @param size
     * @return
     */
    @GetMapping("/getSubObjects")
    public ApiResp<RPage<ManageObjectListVo>> listSubObject(@RequestParam("keyword")String keyword,
                                                            @RequestParam("parent")String parent,
                                                            @RequestParam("pageNum")Integer pageNum,
                                                            @RequestParam("size")Integer size){
        return ApiResp.success(manageObjectService.getSubObjects(keyword,parent,pageNum,size));
    }
}
