package ccw.serviceinnvation.sdk;

public class CloudCanClientBuilder implements CloudCanBuilder{



    public CloudCanClientBuilder() {
    }

    @Override
    public CloudCan build() {
        return null;
    }

    @Override
    public CloudCan build(String var1) {
        return null;
    }

    @Override
    public CloudCan build(String var1, String var2) {
        return new CloudCanClient(var1, var2);
    }

    public CloudCan build(String endpoint, String username, String password) {
        return new CloudCanClient(endpoint, username,password);
    }

    @Override
    public CloudCan build(String var1, String var2, String var3, String var5) {
        return null;
    }

}
