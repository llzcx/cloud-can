package ccw.serviceinnovation.node.util;

import ccw.serviceinnovation.node.bo.ObjectMeta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializeUtil {
    ObjectMapper objectMapper = new ObjectMapper();


    public String Serialize(ObjectMeta objectMeta) throws JsonProcessingException {
        return objectMapper.writeValueAsString(objectMeta);
    }

    public Object DeSerialize(String json, Class cls) throws JsonProcessingException {
        return objectMapper.readValue(json, cls);
    }
}
