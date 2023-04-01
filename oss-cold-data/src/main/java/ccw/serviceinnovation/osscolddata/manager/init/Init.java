package ccw.serviceinnovation.osscolddata.manager.init;

import ccw.serviceinnovation.common.constant.StorageTypeEnum;
import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.entity.bo.ColdMqMessage;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.common.nacos.TrackerService;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.file.ZipUtil;
import ccw.serviceinnovation.common.util.http.FileUtil;
import ccw.serviceinnovation.common.util.http.HttpUtils;
import ccw.serviceinnovation.osscolddata.constant.FilePrefixConstant;
import ccw.serviceinnovation.osscolddata.constant.OssColdDataConstant;
import ccw.serviceinnovation.osscolddata.controller.OssColdController;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.jraft.error.RemotingException;
import service.raft.client.RaftRpcRequest;
import service.raft.rpc.DataGrpcHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static ccw.serviceinnovation.osscolddata.constant.FilePrefixConstant.FILE_COLD;
import static ccw.serviceinnovation.osscolddata.constant.FilePrefixConstant.FILE_COLD_TMP;
import static ccw.serviceinnovation.osscolddata.constant.OssColdDataConstant.POSITION;

/**
 * @author 陈翔
 */
public class Init {

    public static void initFileKey() {
        System.out.println("position:"+ POSITION);
        File fileDir = new File(POSITION);
        File[] files = fileDir.listFiles();
        for (File file : files) {
            String name = file.getName();
            if(name.startsWith(FILE_COLD)){
                String etag = name.substring(FILE_COLD.length());
                System.out.println("加入了etag:"+etag);
                OssColdController.data.put(etag,file.getAbsolutePath());
            }
        }
    }

    public static void fileInit() {

    }


}
