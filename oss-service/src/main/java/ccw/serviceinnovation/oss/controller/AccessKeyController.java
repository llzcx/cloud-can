package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.dto.AccessKeyDto;
import ccw.serviceinnovation.oss.pojo.dto.MessageDto;
import ccw.serviceinnovation.oss.service.IAccessKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static ccw.serviceinnovation.common.constant.AuthorityConstant.API_OBJECT;

/**
 * 对象的AccessKey 接口
 * @author 杨世博
 */
@RestController
@RequestMapping("/accessKey")
public class AccessKeyController {

    @Autowired
    IAccessKeyService accessKeyService;

    /**
     * 生成AccessKey
     * @return 返回AccessKey => AccessKeyInfo
     */
    @PostMapping("/createAccessKey")
    @OssApi(target = API_OBJECT,type = AuthorityConstant.API_WRITER, name = "createAccessKey",description = "生成AccessKey")
    public ApiResp<Map<String, MessageDto>> createAccessKey(@RequestParam("objectId")Long objectId, @RequestParam("survivalTime")Long survivalTime){
        Map<String, MessageDto> accessKeys = accessKeyService.createAccessKey(objectId, survivalTime);
        return ApiResp.success(accessKeys);
    }

    /**
     * 获取该对象的 全部AccessKey
     * @param objectId 对象的id
     * @return 返回AccessKey => AccessKeyInfo
     */
    @GetMapping("/getAccessKeys")
    @OssApi(target = API_OBJECT,type = AuthorityConstant.API_LIST, name = "getAccessKeys",description = "获取该对象的 全部AccessKey")
    public ApiResp<Map<String, MessageDto>> getAccessKeys(@RequestParam("objectId")Long objectId){
        Map<String, MessageDto> accessKeys = accessKeyService.getAccessKeys(objectId);
        return ApiResp.success(accessKeys);
    }

    /**
     * 删除对象的 AccessKey
     * @return 返回AccessKey => AccessKeyInfo
     */
    @DeleteMapping("/deleteAccessKey")
    @OssApi(target = API_OBJECT,type = AuthorityConstant.API_WRITER, name = "deleteAccessKey",description = "删除对象的 AccessKey")
    public ApiResp<Map<String, MessageDto>> deleteAccessKey(@RequestBody AccessKeyDto accessKeyDto){
        Map<String, MessageDto> accessKeyMap = accessKeyService.deleteAccessKey(accessKeyDto);
        return ApiResp.success(accessKeyMap);
    }
}
