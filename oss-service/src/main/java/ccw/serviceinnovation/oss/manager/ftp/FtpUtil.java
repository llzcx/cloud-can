package ccw.serviceinnovation.oss.manager.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author 陈翔
 */
@Component
@Slf4j
public class FtpUtil {

    //ftp服务器ip地址
    @Value("${myoss.ftp.ip}")
    private String FTP_ADDRESS;

    //端口号
    @Value("${myoss.ftp.port}")
    private int FTP_PORT;
    //用户名
    @Value("${myoss.ftp.username}")
    private String FTP_USERNAME;
    //密码
    @Value("${myoss.ftp.password}")
    private String FTP_PASSWORD;

    //路径都是/home/加上用户名
    @Value("${myoss.ftp.base-path}")
    public String FTP_BASEPATH;


    /**
     * 上传文件
     * @param originFileName 文件名字
     * @param input 输入流
     * @return
     */
    public boolean uploadFile(String originFileName, InputStream input) {
        boolean success = false;
        //这是最开始引入的依赖里的方法
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("utf-8");
        try {
            int reply;
            // 连接FTP服务器
            ftp.connect(FTP_ADDRESS, FTP_PORT);
            // 登录
            ftp.login(FTP_USERNAME, FTP_PASSWORD);
            //连接成功会的到一个返回状态码
            reply = ftp.getReplyCode();
            //可以输出看一下是否连接成功
            System.out.println(reply);
            //设置文件类型
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            //修改操作空间
            ftp.changeWorkingDirectory(FTP_BASEPATH);
            //对了这里说明一下你所操作的文件夹必须要有可读权限，chomd 777 文件夹名//这里我就是用的我的home文件夹
            //这里开始上传文件
            ftp.storeFile(originFileName, input);
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                log.error("连接失败");
                return success;
            }
            log.info("连接成功");

            input.close();
            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
        return success;
    }


    /**
     * 下载文件
     * @param filepathAndName 文件路径
     * @param localPath 下载到哪个路径
     * @return
     */
    public boolean downloadFile(String filepathAndName, String localPath) {
        boolean flag = false;
        FTPClient ftp = new FTPClient();
        OutputStream os = null;
        try {
            System.out.println("");
            // 连接FTP服务器
            ftp.connect(FTP_ADDRESS, FTP_PORT);
            // 登录
            ftp.login(FTP_USERNAME, FTP_PASSWORD);
            //得到连接成功的返回状态码
            int reply = ftp.getReplyCode();
            System.out.println(reply);
            //主动，一定要加上这几句设置为主动
            ftp.enterLocalActiveMode();
            //下面是将这个文件夹的所有文件都取出来放在ftpFiles这个文件数组里面
            FTPFile[] ftpFiles = ftp.listFiles();
            //然后便利这个数组找出和我们要下载的文件的文件名一样的文件
            for (FTPFile file : ftpFiles) {
                byte[] bytes = file.getName().getBytes(StandardCharsets.ISO_8859_1);
                file.setName(new String(bytes, StandardCharsets.UTF_8));
                //判断找到所下载的文件，file.getName就是服务器上对应的文件
                if (filepathAndName.equalsIgnoreCase(file.getName())) {
                    //下面就是通过文件id再去数据库查找文件的中文名，将这个作为文件名下载到本地目录
                    String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
                    File localFile = new File( localPath + filepathAndName);
                    //得到文件的输出流
                    os = new FileOutputStream(localFile);
                    //开始下载文件
                    ftp.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            ftp.logout();
            flag = true;
            log.info("下载文件成功");
        } catch (Exception e) {
            log.info("下载文件失败");
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

}

