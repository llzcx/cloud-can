package ccw.serviceinnovation.ossdata.controller;

import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.hash.QETag;
import ccw.serviceinnovation.common.util.http.FileUtil;
import ccw.serviceinnovation.ossdata.bo.ChunkBo;
import ccw.serviceinnovation.ossdata.constant.OssDataConstant;
import ccw.serviceinnovation.ossdata.manager.redis.ChunkRedisService;
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
import java.io.IOException;

import static ccw.serviceinnovation.ossdata.constant.FilePrefixConstant.FILE_NOR;
import static ccw.serviceinnovation.ossdata.constant.FilePrefixConstant.FILE_TMP_BLOCK;
import static ccw.serviceinnovation.ossdata.constant.OssDataConstant.POSITION;

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


    private String TMP_BLOCK =  POSITION + "/" + FILE_TMP_BLOCK;
    private String NOR = POSITION + "/" + FILE_NOR;

    /**
     * 上传分片文件
     * @param file 文件数据
     * @param chunk 第几块
     * @param blockToken 上传事件的id
     * @return 该分块是否上传成功
     * @throws Exception
     */
    @PostMapping("/append_file")
    public Boolean appendChunk(MultipartFile file, Integer chunk, String blockToken, String bucketName) throws Exception {
        log.info("当前:{},为第:{}块分片",blockToken,chunk);
        ChunkBo chunkBo = chunkRedisService.getChunkBo(bucketName,blockToken);
        long size = chunkBo.getSize();
        int chunks = QETag.getChunks(size);
        byte[] bytes = file.getBytes();
        //向磁盘服务器存储该分块
        storageTempObjectService.saveBlock(blockToken,size,file.getBytes(),file.getSize(),chunks,chunk,chunkBo.getSecret());
        //redis保存该分块信息
        log.info("第:{}块",chunk);
        chunkRedisService.saveChunkBit(blockToken,chunk);
        return true;
    }


    /**
     * 下载文件
     * @param etag /卷名/.../对象etag
     * @param response
     * @throws Exception
     */
    @GetMapping("/download/{group}/{etag}")
    public void download(Integer secret,String name, @PathVariable String etag, HttpServletResponse response, @PathVariable String group) throws Exception {
        //先检查group是否正确...
        FileInputStream fis = new FileInputStream(OssDataConstant.POSITION +"\\"+FILE_NOR+etag);
        ControllerUtils.loadResource(response, fis,name,true,
                secret);
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
    public void previewVideo(String name, @PathVariable String etag,HttpServletRequest request ,HttpServletResponse response, @PathVariable String group) throws Exception {
        ControllerUtils.previewVideo(request,response ,resourceHttpRequestHandler,OssDataConstant.POSITION +"\\"+FILE_NOR+etag);
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
        ControllerUtils.loadResource(response,new FileInputStream(path),name,false, null);
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

    @GetMapping("/provider")
    public String provider() {
        System.out.println(OssDataConstant.PROVIDE_PORT);
        return OssDataConstant.PROVIDE_PORT;
    }

    @GetMapping("/unfreeze")
    public Boolean unfreeze(String etag,String ip,String port) {
        String url = "http://"+ip+":"+port +"/cold/download?etag="+etag+"&name="+FILE_NOR+etag;
        try {
            return FileUtil.saveFile(url,NOR+etag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}

