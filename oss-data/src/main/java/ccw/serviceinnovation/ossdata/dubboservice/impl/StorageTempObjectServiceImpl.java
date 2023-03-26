package ccw.serviceinnovation.ossdata.dubboservice.impl;

import ccw.serviceinnovation.common.constant.FileTypeConstant;
import ccw.serviceinnovation.common.util.file.JpegCompress;
import ccw.serviceinnovation.common.util.file.VideoCompress;
import ccw.serviceinnovation.common.util.hash.QETag;
import ccw.serviceinnovation.ossdata.constant.OssDataConstant;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import service.StorageTempObjectService;
import service.bo.FilePrehandleBo;

import java.io.*;

import static ccw.serviceinnovation.ossdata.constant.FilePrefixConstant.*;
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

    @Override
    public FilePrehandleBo preHandle(String objectKey) throws Exception{
        File file = new File(TMP_BLOCK + objectKey);
        //获取文件类型
        String type = FileTypeUtil.getType(file);
        FilePrehandleBo fileCompressBo = new FilePrehandleBo();
        String newName = UUID.randomUUID().toString().replace("-", "_");
        File newFile = new File(POSITION+"\\"+newName);
        if("mp4".equals(type)){
            fileCompressBo.setFileType(FileTypeConstant.VIDEO);
            VideoCompress.compressionVideo(file,FILE_TMP_BLOCK+newName);
            String eTag = QETag.calcETag(TMP_BLOCK + newName);
            File file1 = new File(TMP_BLOCK+"\\"+eTag);
            newFile.renameTo(file1);
            fileCompressBo.setEtag(eTag);
        }else if("txt".equals(type)){
            fileCompressBo.setFileType(FileTypeConstant.TEXT);
            return null;
        }else if("jpg".equals(type)){
            fileCompressBo.setFileType(FileTypeConstant.IMG);
            JpegCompress.compress(TMP_BLOCK + objectKey, newName);
            String eTag = QETag.calcETag(TMP_BLOCK + newName);
            File file1 = new File(TMP_BLOCK+"\\"+eTag);
            newFile.renameTo(file1);
            fileCompressBo.setEtag(eTag);
        }else{
            fileCompressBo.setFileType(FileTypeConstant.OTHER);
        }
        return fileCompressBo;
    }

    public static void main(String[] args) {
        File file = new File("D:\\OSS\\test.txt");
        System.out.println(FileTypeUtil.getType(file));
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


    @Override
    public Boolean blockBecomeFullMember(String token,String objectKey) throws Exception {
        return reName(new File(TMP_BLOCK + token),new File(NOR + objectKey));
    }

    private Boolean  deleteFile(File file){
        if(file.exists() && file.isDirectory() && file.list()!=null){
            String[] list = file.list();
            assert list != null;
            if(list.length==0){
                return file.delete();
            }else{
                return false;
            }
        }else{
            return false;
        }
    }



    @Override
    public Boolean deleteBlockObject(String objectKey) throws Exception {
        File file = new File(TMP_BLOCK +objectKey);
        return deleteFile(file);
    }


    @Override
    public Boolean saveBlock(String blockToken, Long targetSize, byte[] bytes, Long srcSize, Integer chunks, Integer chunk) throws IOException {
        RandomAccessFile randomAccessFile;
        randomAccessFile = new RandomAccessFile(TMP_BLOCK + blockToken,"rw");
        randomAccessFile.setLength(targetSize);
        long offset = 0;
        if (chunk == chunks - 1 && chunk != 0) {
            offset = chunk * (targetSize - srcSize) / chunk;
        } else {
            offset = chunk * srcSize;

        }
        randomAccessFile.seek(offset);
        log.info("偏移量:{}",offset);
        randomAccessFile.write(bytes,0,bytes.length);
        randomAccessFile.close();
        return true;
    }




}
