package ccw.serviceinnovation.oss.common.util;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnvation.encryption.sm4.SM4Utils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

import static ccw.serviceinnvation.encryption.consant.FileConstant.READ_WRITER_SIZE;

/**
 * @author 陈翔
 */
@Component
public class ControllerUtils {
    /**
     * 设置响应头的文件名
     *
     * @param response {@link HttpServletResponse}
     * @param fileName 文件名
     */
    public static void setResponseFileName(HttpServletResponse response, String fileName,Boolean isOnline) throws
            UnsupportedEncodingException {
        if(isOnline){
            // 在线打开方式 文件名应该编码成UTF-8
            response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        }else{
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        }

    }
    /**
     * 写回数据
     * @param httpServletResponse
     * @param resultCode
     */
    public static void writeReturn(HttpServletResponse httpServletResponse, ResultCode resultCode){
        try {
            httpServletResponse.getWriter().print(JSONObject.toJSON(ApiResp.fail(resultCode)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写回数据
     * @param httpServletResponse
     * @param resultCode
     */
    public static Boolean writeIfReturn(HttpServletResponse httpServletResponse, ResultCode resultCode, Boolean flag)throws IOException{
        if (flag) {
            return true;
        } else {
            httpServletResponse.getWriter().print(ApiResp.fail(resultCode));
            return false;
        }
    }

    /**
     * 加载资源到response
     * @param response 返回的Response
     * @param in 输入流
     * @param fileName 资源路径
     * @param download 直接下载
     */
    public static void loadResource(HttpServletResponse response, FileInputStream in,String fileName, boolean download,Integer secret) throws IOException {
        ServletOutputStream os = response.getOutputStream();
        response.reset();
        ControllerUtils.setResponseFileName(response, fileName, !download);
        int len;
        if(secret != null){
            //不需要加密
            byte[] b = new byte[READ_WRITER_SIZE];
            while (-1 != (len = in.read(b))) {
                os.write(b,0,len);
            }
        }else if(Objects.equals(secret, EncryptionEnum.SM4.getCode())){
            //需要加密SM4
            SM4Utils sm4Utils = new SM4Utils();
            //每次写入的大小改变
            int byteSize = (int) sm4Utils.getAfterSecretLength(READ_WRITER_SIZE);
            byte[] b = new byte[byteSize];
            while (-1 != (len = in.read(b))) {
                //SM4加密
                byte[] newBytes = sm4Utils.encryptData_ECB(b);
                os.write(newBytes,0,len);
            }
        }
        in.close();
        os.flush();
        os.close();
    }
}
