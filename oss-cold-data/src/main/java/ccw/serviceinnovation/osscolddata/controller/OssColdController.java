package ccw.serviceinnovation.osscolddata.controller;

import ccw.serviceinnovation.common.entity.Api;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.util.http.FileUtil;
import ccw.serviceinnovation.osscolddata.constant.OssColdDataConstant;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static ccw.serviceinnovation.common.constant.ColdDataConstant.COLD;

/**
 * @author 陈翔
 */

@RestController("/cold")
public class OssColdController {
    /**
     * oss-service调用此方法 将oss-data的数据下载(归档)到磁盘
     * @param ip
     * @param port
     * @return
     */
    @PostMapping("/{ip}/{port}")
    public ApiResp<Boolean> cold(@PathVariable String ip, @PathVariable String port) throws Exception{
        //下载文件
        String filePath = "OssColdDataConstant.POSITION+\"\\\\"+COLD;
        FileUtil.saveFile("http://"+ip+":"+port+"/object/cold", filePath);
        return ApiResp.success(true);
    }



}
