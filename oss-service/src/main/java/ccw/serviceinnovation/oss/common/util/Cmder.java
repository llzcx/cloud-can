package ccw.serviceinnovation.oss.common.util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author 陈翔
 */
public class Cmder {
    /**
     * 执行一个cmd命令
     *
     * @param cmdCommand cmd命令
     * @return 命令执行结果字符串，如出现异常返回null
     */
    public static String executeCmdCommand(String cmdCommand) {
        StringBuilder stringBuilder = new StringBuilder();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmdCommand);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(" ");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行bat文件，
     *
     * @param file          bat文件路径
     * @param isCloseWindow 执行完毕后是否关闭cmd窗口
     * @return bat文件输出log
     */
    public static String executeBatFile(String file, boolean isCloseWindow) {
        String cmdCommand = null;
        if (isCloseWindow) {
            cmdCommand = "cmd.exe /c " + file;
        } else {
            cmdCommand = "cmd.exe /k " + file;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmdCommand);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(" ");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行bat文件,新开窗口
     *
     * @param file          bat文件路径
     * @param isCloseWindow 执行完毕后是否关闭cmd窗口
     * @return bat文件输出log
     */
    public static String executeBatFileWithNewWindow(String file, boolean isCloseWindow) {
        String cmdCommand;
        if (isCloseWindow) {
            cmdCommand = "cmd.exe /c start" + file;
        } else {
            cmdCommand = "cmd.exe /k start" + file;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmdCommand);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(" ");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行bat脚本
     *
     * @param batScript 脚本内容
     * @param location  脚本存储路径
     * @return 结果
     */
    public static String executeBatScript(String batScript, String location) {
        StringBuilder stringBuilder = new StringBuilder();
        FileWriter fw = null;
        try {
            //生成bat文件
            fw = new FileWriter(location);
            fw.write(batScript);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Process process;
        try {
            process = Runtime.getRuntime().exec(location);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(" ");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行脚本,不停止,并输出执行结果
     *
     * @param batScript 脚本内容
     * @param location  bat文件生成地址
     */
    public void executeBatScriptAlways(String batScript, String location) {
        FileWriter fw = null;
        try {
            //生成bat文件
            fw = new FileWriter(location);
            fw.write(batScript);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder();
        //运行bat文件
        Process process;
        try {
            process = Runtime.getRuntime().exec(location);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
