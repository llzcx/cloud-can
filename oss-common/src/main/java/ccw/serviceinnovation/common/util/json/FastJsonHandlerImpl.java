package ccw.serviceinnovation.common.util.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class FastJsonHandlerImpl implements OssJsonHandler{
    @Override
    public String encode(Object object) {
        return JSON.toJSONString(object);
    }

    @Override
    public byte[] encodeByte(Object bytes) {
        return JSON.toJSONBytes(bytes, SerializerFeature.DisableCircularReferenceDetect);
    }
    @Override
    public <T> T decodeByte(byte[]  bytes, Class<T> cls) {
        return JSON.parseObject(bytes,cls);
    }

    @Override
    public <T> T decode(String  json, Class<T> cls) {
        return JSON.parseObject(json,cls);
    }

    public JSONObject toJSONObject(String json){
        return JSONObject.parseObject(json);
    }

}
