import java.io.IOException;

class Server {

    public static void usage() {
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }
        int port = Integer.parseInt(args[0]);
        ServerTCP server = null;
        try {
            server = new ServerTCP(port);
            server.mainLoop();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("cannot communicate with client");
            System.exit(1);
        }
    }
}
