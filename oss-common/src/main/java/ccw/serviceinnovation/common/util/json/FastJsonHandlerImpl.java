package ccw.serviceinnovation.common.util.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class FastJsonHandlerImpl implements OssJsonHandler{
    @Override
    public String encode(Object object) {
        return JSON.toJSONString(object);
    }

    @Override
    public <T> T decode(String  json, Class<T> cls) {
        return JSON.parseObject(json,cls);
    }

    public JSONObject toJSONObject(String json){
        return JSONObject.parseObject(json);
    }

}
