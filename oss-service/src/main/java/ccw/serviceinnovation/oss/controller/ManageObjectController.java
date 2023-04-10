package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
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
     * @param keyword 用户id，桶id，桶名
     * @param pageNum 当前页数
     * @param size 每页大小
     * @return 筛选后的object列表
     */
    @GetMapping("/listObjects")
    @OssApi(target = AuthorityConstant.API_MANAGE,type = AuthorityConstant.API_READ, name = "listObject",description = "获取Object列表")
    public ApiResp<RPage<ManageObjectListVo>> listObject(@RequestParam(value = "keyword", required = false)String keyword,
                                                         @RequestParam("pageNum")Integer pageNum,
                                                         @RequestParam("size")Integer size) throws Exception{
        RPage<ManageObjectListVo> ossObjectRPage = manageObjectService.getObjectList(keyword,pageNum,size);
        return ApiResp.success(ossObjectRPage);
    }

    /**
     * 删除Object及其相关信息
     * @param objectIdList 删除对象的id
     * @return 删除是否成功
     */
    @DeleteMapping("/deleteObject")
    @OssApi(target = AuthorityConstant.API_MANAGE,type = AuthorityConstant.API_WRITER, name = "deleteObject",description = "删除Object及其相关信息")
    public ApiResp<Boolean> deleteObject(@RequestBody List<Long> objectIdList) throws Exception{
        Boolean flag = manageObjectService.deleteObject(objectIdList);
        return ApiResp.ifResponse(flag,flag, ResultCode.COMMON_FAIL);
    }

    /**
     * 获取这个对象的详细信息
     * @param id 对象id
     * @return 对象的星系详细信息
     */
    @GetMapping("/getObject")
    @OssApi(target = AuthorityConstant.API_MANAGE,type = AuthorityConstant.API_READ, name = "getObject",description = "获取这个对象的详细信息")
    public ApiResp<ManageObjectDetailedVo> getObject(@RequestParam("id")Long id){
        ManageObjectDetailedVo objectVo = manageObjectService.getObject(id);
        return ApiResp.success(objectVo);
    }

    /**
     * 获取文件夹中的对象
     * @param keyword 搜索的关键词：文件前缀名
     * @param parent 父文件夹
     * @param pageNum 当前页数
     * @param size 每页数据条数
     * @return 对象列表
     */
    @GetMapping("/getSubObjects")
    @OssApi(target = AuthorityConstant.API_MANAGE,type = AuthorityConstant.API_READ, name = "listSubObject",description = "获取文件夹中的对象")
    public ApiResp<RPage<ManageObjectListVo>> listSubObject(@RequestParam(value = "keyword", required = false)String keyword,
                                                            @RequestParam("parent")String parent,
                                                            @RequestParam("pageNum")Integer pageNum,
                                                            @RequestParam("size")Integer size){
        return ApiResp.success(manageObjectService.getSubObjects(keyword,parent,pageNum,size));
    }
}
