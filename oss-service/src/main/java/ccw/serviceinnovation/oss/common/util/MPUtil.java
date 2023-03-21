package ccw.serviceinnovation.oss.common.util;

import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 陈翔
 */
public class MPUtil {
    /**
     * 简化利用mp查询操作
     * @param object
     * @return
     */
    public static Map<String,Object> getMap(Object ...object) {
        if(object.length%2==1) {
            throw new OssException(ResultCode.PARAM_NOT_COMPLETE);
        }else{
            Map<String,Object> mp = new HashMap<>();
            String key = null;
            for (int i = 0; i < object.length; i++) {
                if(i%2==0){
                    if(object[i] instanceof String){
                        key = (String) object[i];
                    }else{
                        throw new OssException(ResultCode.PARAM_NOT_VALID);
                    }
                }else{
                    mp.put(key, object[i]);
                }
            }
            return mp;
        }
    }

    /**
     * 简化利用QueryWrapperEq查询操作
     * @param object
     * @return
     */
    public static <T> QueryWrapper<T> queryWrapperEq(Object ...object) {
        if(object.length%2==1) {
            throw new OssException(ResultCode.PARAM_NOT_COMPLETE);
        }else{
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            String key = null;
            for (int i = 0; i < object.length; i++) {
                if(i%2==0){
                    if(object[i] instanceof String){
                        key = (String) object[i];
                    }else{
                        throw new OssException(ResultCode.PARAM_NOT_VALID);
                    }
                }else{
                    queryWrapper.eq(key, object[i]);
                }
            }
            return queryWrapper;
        }
    }

}
