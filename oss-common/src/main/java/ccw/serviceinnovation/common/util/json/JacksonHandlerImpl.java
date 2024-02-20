package ccw.serviceinnovation.common.util.json;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JacksonHandlerImpl implements OssJsonHandler{

    @Override
    public String encode(Object object)  {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T decode(String json, Class<T> cls) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json,cls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T decodeByte(byte[] bytes, Class<T> cls) {
        return null;
    }

    @Override
    public byte[] encodeByte(Object bytes) {
        return new byte[0];
    }

}
