package ccw.serviceinnovation.common.constant;

/**
 * @author 陈翔
 */
public interface AuthorityConstant {
    /**
     * api的目标
     */
    String API_BUCKET = "api_bucket";
    String API_OBJECT = "api_object";
    String API_USER = "api_user";
    String API_MANAGE = "api_manage";
    String API_OPEN = "api_open";
    String API_OTHER = "api_other";

    /**
     * api的种类
     */
    String API_READ = "api_read";
    String API_WRITER = "api_writer";
    String API_LIST = "api_list";
    String API_BACK_UP = "api_back_up";
    String API_BACKUP_RECOVERY = "api_backup_recovery";
    public static void main(String[] args) {
        switch (1){
            case 1:
                System.out.println("1");
                break;
            case 2:
                break;
            default:

        }
    }
}
