package ccw.serviceinnovation.common.util.json;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface OssJsonHandler {
    String encode(Object object);

    <T> T decode(String  json, Class<T> cls);
}
