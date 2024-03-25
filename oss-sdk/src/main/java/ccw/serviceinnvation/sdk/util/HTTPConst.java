package ccw.serviceinnvation.sdk.util;

import okhttp3.MediaType;

public interface HTTPConst {
    MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    MediaType STREAM = MediaType.parse("application/octet-stream");
}
