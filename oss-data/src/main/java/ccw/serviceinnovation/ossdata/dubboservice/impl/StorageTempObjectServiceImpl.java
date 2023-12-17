package ccw.serviceinnovation.ossdata.dubboservice.impl;

import ccw.serviceinnovation.common.constant.FileTypeConstant;
import ccw.serviceinnovation.common.constant.SecretEnum;
import ccw.serviceinnovation.common.util.file.JpegCompress;
import ccw.serviceinnovation.common.util.file.VideoCompress;
import ccw.serviceinnovation.common.util.sm4.SM4Utils;
import ccw.serviceinnovation.ossdata.constant.OssDataConstant;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import service.StorageTempObjectService;
import service.bo.FilePrehandleBo;
import java.io.*;
import static ccw.serviceinnovation.ossdata.constant.FilePrefixConstant.FILE_NOR;
import static ccw.serviceinnovation.ossdata.constant.FilePrefixConstant.FILE_TMP_BLOCK;
import static ccw.serviceinnovation.ossdata.constant.OssDataConstant.POSITION;

/**
 * 缓存实现类
 * @author 陈翔
 */
@DubboService(version = "1.0.0", group = "temp",interfaceClass = StorageTempObjectService.class)
@Slf4j
@Service
public class StorageTempObjectServiceImpl implements StorageTempObjectService {
    private String TMP_BLOCK =  POSITION + "\\" + FILE_TMP_BLOCK;
    private String NOR = POSITION + "\\" + FILE_NOR;

    SM4Utils sm4Utils = new SM4Utils();

    public static void main(String[] args) {
        File file = new File("D:\\OSS\\conf.txt");
        //根据文件头获取文件类型
        String type = FileTypeUtil.getType(file);
        System.out.println(type);
    }

    @Override
    public FilePrehandleBo preHandle(String etag,String objectKey,Boolean press,Integer secret) {
        File file = new File(TMP_BLOCK + objectKey);
        //根据文件头获取文件类型
        String type = FileTypeUtil.getType(file);
        FilePrehandleBo fileCompressBo = new FilePrehandleBo();
        //当需要进行etag判断时
        if(etag!=null){
            log.info("需要进行etag判断");
            //设置老的etag
            log.info("正在计算md5:{}",file.getAbsolutePath());
            String backendEtag = SecureUtil.md5(file);
            fileCompressBo.setOldEtag(backendEtag);
            log.info("前端etag:{},后端etag:{}",etag,backendEtag);
            if(!backendEtag.equals(etag)){
                log.info("前后端hash验证失败");
                return null;
            }
        }
        if(secret!=null && secret.equals(SecretEnum.SM4.getCode())){
            try {
                //加密处理
                log.info("正在加密...");
                sm4Utils.encryption(TMP_BLOCK + objectKey);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }else if("mp4".equals(type)){
            fileCompressBo.setFileType(FileTypeConstant.VIDEO);
            if(press){
                String newName = UUID.randomUUID().toString().replace("-", "_");
                log.info("newName:{}",newName);
                //生成mp4压缩文件
                File file1 = VideoCompress.compressionVideo(file, newName);
                System.out.println(POSITION + "\\"+newName);
                //获取这个文件的md5
                String newETag = SecureUtil.md5(file1);
                log.info("原来token:{},的etag:{},压缩完成:{},etag为:{}",objectKey,etag,file1.getAbsolutePath(),newETag);
                fileCompressBo.setNewEtag(newETag);
                file1.renameTo(new File(TMP_BLOCK+objectKey));
            }
        }else if("txt".equals(type)){
            fileCompressBo.setFileType(FileTypeConstant.TEXT);
            return fileCompressBo;
        }else if("jpg".equals(type) || "png".equals(type)){
            fileCompressBo.setFileType(FileTypeConstant.IMG);
            if (press){
                JpegCompress.compress(TMP_BLOCK + objectKey, TMP_BLOCK + objectKey);
                String eTag = SecureUtil.md5(new File(TMP_BLOCK + objectKey));
                log.info("原来的token:{},的etag:{},压缩完成以后的路径:{},新etag为:{}",objectKey,etag,TMP_BLOCK + objectKey,eTag);
                fileCompressBo.setNewEtag(eTag);
            }
        }else if("docx".equals(type)){
            fileCompressBo.setFileType(FileTypeConstant.DOCX);
        }else if("pdf".equals(type)){
            fileCompressBo.setFileType(FileTypeConstant.PDF);
        }else{
            fileCompressBo.setFileType(FileTypeConstant.OTHER);
        }
        log.info("识别为:{}", fileCompressBo.getFileType());
        return fileCompressBo;
    }


    @Override
    public String getPort() {
        return OssDataConstant.PORT;
    }


    private Boolean reName(File oldName,File newName) throws Exception{
        if (newName.exists()) {
            throw new java.io.IOException("file exists");
        }
        if(oldName.renameTo(newName)) {
            return true;
        } else {
            return false;
        }
    }




    private Boolean  deleteFile(File file){
        return file.delete();
    }



    @Override
    public Boolean deleteBlockObject(String objectKey) throws Exception {
        File file = new File(TMP_BLOCK +objectKey);
        if(!file.exists()){
            return true;
        }
        return file.delete();
    }


    @Override
    public Boolean saveBlock(String blockToken, Long targetSize, byte[] bytes, Long srcSize, Integer chunks, Integer chunk,Integer secret) throws IOException {
        RandomAccessFile randomAccessFile;
        randomAccessFile = new RandomAccessFile(TMP_BLOCK + blockToken, "rw");
        randomAccessFile.setLength(targetSize);
        long offset;
        if (chunk == chunks - 1 && chunk != 0) {
            offset = chunk * (targetSize - srcSize) / chunk;
        } else {
            offset = chunk * srcSize;
        }
        randomAccessFile.seek(offset);
        log.info("偏移量:{}", offset);
        randomAccessFile.write(bytes, 0, bytes.length);
        randomAccessFile.close();
        return true;
    }
}
