package global.session;

public class Session {
    private static Long customerId = null;

    public static void setCustomerId(Long id) {
    	customerId = id;
    }

    public static Long getCustomerId() {
        return customerId;
    }

    public static boolean isLoggedIn() {
        return customerId != null;
    }

    public static void logout() {
    	customerId = null;
    }
}
