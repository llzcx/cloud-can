package ccw.serviceinnovation.ossdata.dubboservice.impl;

import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.util.IpUtils;
import ccw.serviceinnovation.common.util.http.FileUtil;
import ccw.serviceinnovation.common.util.net.NetUtil;
import ccw.serviceinnovation.ossdata.manager.raft.server.DataStateMachine;
import cn.hutool.core.io.FileTypeUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import service.StorageObjectService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static ccw.serviceinnovation.ossdata.constant.FilePrefixConstant.FILE_NOR;
import static ccw.serviceinnovation.ossdata.constant.FilePrefixConstant.FILE_TMP_BLOCK;
import static ccw.serviceinnovation.ossdata.constant.OssDataConstant.POSITION;
import static ccw.serviceinnovation.ossdata.constant.OssDataConstant.RPC_ADDR;

/**
 * 本地实现对象底层操作的业务类
 * @author 陈翔
 */
@Service
@DubboService(version = "1.0.0", group = "object",interfaceClass = StorageObjectService.class)
public class StorageObjectServiceImpl implements StorageObjectService {
    private String TMP_BLOCK =  POSITION + "/" + FILE_TMP_BLOCK;
    private String NOR = POSITION + "/" + FILE_NOR;

    @Value("${server.port}")
    private String port;

    @Override
    public String getExt(String objectKey) {
        return FileTypeUtil.getType(NOR+objectKey);
    }

    @Override
    public byte[] getCompleteObject(String etag) throws FileNotFoundException{
        return FileUtil.readFile(NOR+etag);
    }

    @Override
    public Boolean save(byte[] bytes, String etag) throws Exception{
        FileOutputStream fos = new FileOutputStream(NOR+etag);
        fos.write(bytes);
        fos.close();
        return null;
    }

    @Override
    public Boolean deleteObject(String objectKey){
        File file = new File(POSITION +"/"+FILE_NOR+objectKey);
        if(!file.exists()){
            return true;
        }else{
            return file.delete();
        }
    }


    @Override
    public LocationVo location(String etag){
        String path = DataStateMachine.dataMap.get(etag);
        if(path!=null){
            try {
                return new LocationVo(IpUtils.getIp(RPC_ADDR), Integer.valueOf(port));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            System.out.println(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
