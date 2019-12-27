package trisc.ondrej.oraculum;

public class Main {

    public static void main(String[] args) {

        MyServer server = new MyServer();
        server.startServer(4242);
    }
}
