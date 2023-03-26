package ccw.serviceinnovation.common.constant;

/**
 * @author 陈翔
 */
public interface FileTypeConstant {
    /**
     * 文本类型
     */
    public static int TEXT = 1;

    /**
     * 视频类型
     */
    public static int VIDEO = 2;

    /**
     * 音频
     */
    public static int AUDIO = 3;

    /**
     * 图片
     */
    public static int IMG = 4;

    /**
     * 其他
     */
    public static int OTHER = -1;

    /**
     * 获取类型
     * @param type
     * @return
     */
    public static int getType(String type) {
        if(".mp4".equals(type)){
            return FileTypeConstant.VIDEO;
        }else if(".mp3".equals(type)){
            return AUDIO;
        }else if(".png".equals(type) || ".jpg".equals(type)){
            return IMG;
        }else if(".txt".equals(type)){
            return TEXT;
        }

        return OTHER;
    }
}
