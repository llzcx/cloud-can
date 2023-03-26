package ccw.serviceinnovation.ossdata.controller;

import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.hash.QETag;
import ccw.serviceinnovation.ossdata.bo.ChunkBo;
import ccw.serviceinnovation.ossdata.constant.OssDataConstant;
import ccw.serviceinnovation.ossdata.manager.redis.ChunkRedisService;
import ccw.serviceinnovation.ossdata.mapper.OssObjectMapper;
import ccw.serviceinnovation.ossdata.util.ControllerUtils;
import ccw.serviceinnovation.ossdata.util.NoStaticResourceHttpRequestHandler;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.StorageObjectService;
import service.StorageTempObjectService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

import static ccw.serviceinnovation.ossdata.constant.FilePrefixConstant.FILE_NOR;
import static ccw.serviceinnovation.ossdata.constant.FilePrefixConstant.FILE_TMP_BLOCK;

/**
 * 对象接口
 * @author 陈翔
 * @since 2023-01-20
 */
@RestController
@RequestMapping("/object")
@Slf4j
public class OssObjectController {


    @Autowired(required = false)
    HttpServletRequest request;



    @GetMapping("/123")
    public String getRequest() {
        return OssDataConstant.POSITION;
    }

    @Autowired
    ChunkRedisService chunkRedisService;

    @Autowired
    StorageTempObjectService storageTempObjectService;

    @Autowired
    StorageObjectService storageObjectService;

    @Autowired
    OssObjectMapper ossObjectMapper;


    /**
     * 上传分片文件
     * @param file
     * @param chunk
     * @param blockToken
     * @return
     * @throws Exception
     */
    @PostMapping("/append_file")
    public Boolean addObjectChunk(MultipartFile file, Integer chunk, String blockToken) throws Exception {
        log.info("当前为第:{}块分片",chunk);
        ChunkBo chunkBo = chunkRedisService.getObjectPosition(blockToken);
        long size = chunkBo.getSize();
        int chunks = QETag.getChunks(size);
        byte[] bytes = file.getBytes();
        //向磁盘服务器存储该分块
        storageTempObjectService.saveBlock(blockToken,size,file.getBytes(),file.getSize(),chunks,chunk);
        //redis保存该分块信息
        String blockSha1 = Base64.getEncoder().encodeToString(QETag.sha1(bytes));
        log.info("第{}块sha1为{}",chunk,blockSha1);
        chunkRedisService.saveChunk(blockToken,chunk, blockSha1);
        return true;
    }


    /**
     * 下载文件
     * @param etag /卷名/.../对象etag
     * @param response
     * @throws Exception
     */
    @GetMapping("/download/{group}/{etag}")
    public void download(String name, @PathVariable String etag, HttpServletResponse response, @PathVariable String group) throws Exception {
        FileInputStream fis = new FileInputStream(OssDataConstant.POSITION +"\\"+FILE_NOR+etag);
        ControllerUtils.loadResource(response, fis,name,true,
                null);
    }


    @Autowired
    NoStaticResourceHttpRequestHandler resourceHttpRequestHandler;

    /**
     * 预览视频
     * @param etag /卷名/.../对象etag
     * @param response
     * @throws Exception
     */
    @GetMapping("/preview-video/{group}/{etag}")
    public void previewVideo(String name, @PathVariable String etag, HttpServletResponse response, @PathVariable String group) throws Exception {
        FileInputStream fis = new FileInputStream(OssDataConstant.POSITION +"\\"+FILE_NOR+etag);
        ControllerUtils.loadResource(response, fis,name,false,
                null);
    }


    /**
     * 预览图片
     * @param etag /卷名/.../对象etag
     * @param response
     * @throws Exception
     */
    @GetMapping("/preview-image/{group}/{etag}")
    public void previewImage(String name, @PathVariable String etag, HttpServletResponse response, @PathVariable String group) throws Exception {
        String path = OssDataConstant.POSITION +"\\"+FILE_NOR+etag;
        ControllerUtils.previewVideo(request,response,resourceHttpRequestHandler, path);
    }

    /**
     * 下载文件
     * @param token /卷名/.../对象etag
     * @param response
     * @throws Exception
     */
    @GetMapping("/download_temp/{token}")
    public void downloadTemp(@PathVariable String token,HttpServletResponse response) throws Exception {
        File file = new File(OssDataConstant.POSITION +"\\"+FILE_TMP_BLOCK+token);
        if(!file.exists()){
            System.out.println("文件未找到:"+OssDataConstant.POSITION +"\\"+FILE_TMP_BLOCK+token);
            throw new OssException(ResultCode.FILE_IS_EMPTY);
        }else{
            System.out.println("文件已经找到:"+OssDataConstant.POSITION +"\\"+FILE_TMP_BLOCK+token);
        }
        FileInputStream fis = new FileInputStream(file);
        ControllerUtils.loadResource(response, fis,file.getName(),true,
                null);
    }


    @GetMapping("/location/{etag}")
    public String location(@PathVariable String etag) throws Exception {
        LocationVo location = storageObjectService.location(etag);
        System.out.println("返回地址=>"+location.getIp()+":"+location.getPort());
        return JSONObject.toJSONString(location);
    }

    @GetMapping("/compress/{etag}")
    public String compress(@PathVariable String etag) throws Exception {
        LocationVo location = storageObjectService.location(etag);
        System.out.println("返回地址=>"+location.getIp()+":"+location.getPort());
        return JSONObject.toJSONString(location);
    }


}

