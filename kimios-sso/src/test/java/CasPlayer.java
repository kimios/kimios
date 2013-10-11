import org.kimios.kernel.security.sso.CasUtils;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 10/10/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class CasPlayer {


    public static void main(String[] args) throws Exception{
        String username = "admin";
        String password = "admin";
        new CasUtils("https://localhost:9011").validateAuthentication(username, password, "http://localhost:8080");
    }
}
