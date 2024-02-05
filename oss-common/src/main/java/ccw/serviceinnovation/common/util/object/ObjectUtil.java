package ccw.serviceinnovation.common.util.object;

import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author 陈翔
 */
public class ObjectUtil {
    public static String getFileName(String objectName){
        if(objectName==null){
            throw new OssException(ResultCode.OBJECT_NAME_ERROR);
        }
        objectName = objectName.trim();
        if (objectName.length()!=0){
            String substring = objectName.substring(objectName.lastIndexOf('/') + 1);
            if(substring.length()==0){
                throw new OssException(ResultCode.FILE_NAME_IS_NULL);
            }
            return substring;
        }else{
            throw new OssException(ResultCode.FILE_NAME_IS_NULL);
        }
    }

    public static String[] getAllFolder(String objectName){
        if(objectName==null){
            throw new OssException(ResultCode.OBJECT_NAME_ERROR);
        }
        objectName = objectName.trim();
        if (objectName.length()!=0){
            if(objectName.indexOf("/")==-1){
                //该对象命名不包含文件夹
                return null;
            }
            if(objectName.charAt(objectName.length()-1)=='/'){
                return objectName.split("/");
            }
            objectName =  objectName.substring(0,objectName.lastIndexOf("/"));
            return objectName.split("/");
        }else{
            throw new OssException(ResultCode.FILE_NAME_IS_NULL);
        }
    }


    public static void main(String[] args) {
        String path = "file1/file2/file3/123.txt";
        String  str = "123/awdw.txt";
        System.out.println(JSONObject.toJSON(getAllFolder(path)));
    }
}
