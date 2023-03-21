package ccw.serviceinnovation.ossdata.dubboservice.impl;

import ccw.serviceinnovation.ossdata.constant.OssDataConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import service.StorageTempObjectService;

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
    private String TMP_BLOCK =  POSITION + "/" + FILE_TMP_BLOCK;
    private String NOR = POSITION + "/" + FILE_NOR;

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
