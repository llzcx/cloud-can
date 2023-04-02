package ccw.serviceinnovation.ossdata.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

/**
 * @author 陈翔
 */
@Component
@Slf4j
public class NoStaticResourceHttpRequestHandler extends ResourceHttpRequestHandler {

    public final static String ATTR_FILE = "NON-STATIC-FILE";

    @Override
    protected Resource getResource(HttpServletRequest request) throws IOException {
        System.out.println("success");
        return new FileSystemResource((String)request.getAttribute(ATTR_FILE));
    }

    @Override
    protected MediaType getMediaType(HttpServletRequest request, Resource resource) {
        MediaType result = null;
        String fileName = resource.getFilename();
        if(!resource.getFilename().endsWith(".mp4")){
            fileName = resource.getFilename()+".mp4";
        }
        System.out.println("video file name:"+fileName);
        String mimeType = request.getServletContext().getMimeType(fileName);
        if (StringUtils.hasText(mimeType)) {
            result = MediaType.parseMediaType(mimeType);
        }
        if (result == null || MediaType.APPLICATION_OCTET_STREAM.equals(result)) {
            MediaType mediaType = null;
            String filename = resource.getFilename();
            String ext = StringUtils.getFilenameExtension(filename);
            if (ext != null) {
                mediaType = super.getMediaTypes().get(ext.toLowerCase(Locale.ENGLISH));
            }
            if (mediaType == null) {
                mediaType = MediaTypeFactory.getMediaType(filename).orElse(null);
            }
            if (mediaType != null) {
                result = mediaType;
            }
        }
        return result;
    }
}
