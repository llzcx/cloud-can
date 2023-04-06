package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.oss.pojo.dto.AccessKeyDto;
import ccw.serviceinnovation.oss.pojo.dto.MessageDto;

import java.util.Map;

/**
 * @author Joy Yang
 */
public interface IAccessKeyService{

    /**
     * 创建 AccessKeyDto
     * @param objectId
     * @param survivalTime
     * @return
     */
    Map<String, MessageDto> createAccessKey(Long objectId, Long survivalTime);

    /**
     * 获取这个对象的 AccessKeyDto
     * @param objectId
     * @return
     */
    Map<String, MessageDto> getAccessKeys(Long objectId);

    /**
     * 删除对象的这个 AccessKeyDto
     * @param accessKeyDto
     * @return
     */
    Map<String, MessageDto> deleteAccessKey(AccessKeyDto accessKeyDto);
}
