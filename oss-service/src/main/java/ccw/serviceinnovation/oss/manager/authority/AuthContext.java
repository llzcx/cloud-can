package ccw.serviceinnovation.oss.manager.authority;

public class AuthContext {
    private static ThreadLocal<AuthInfo> context = new ThreadLocal<>();

    public static ThreadLocal<AuthInfo> get(){
        return context;
    }

    public static void remove(){
        context.remove();
    }

    public static void set(AuthInfo authInfo){
        context.set(authInfo);
    }
}
