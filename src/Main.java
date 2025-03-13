import uoc.webserver.utils.Database;
import uoc.webserver.utils.Modules;

public class Main {
    public static void main(String[] args) {
        try {
            WebServer apiserver = new WebServer();
            apiserver.start();
            apiserver.addRoute("/api/test", new TestRoute());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}