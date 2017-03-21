import java.io.IOException;

class Client {

    public static void usage() {
        System.err.println("usage : java Client server_ip port");
        System.exit(1);
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            usage();
        }
        int port = Integer.parseInt(args[1]);
        ClientTCP client = null;
        try {
            client = new ClientTCP(args[0], port);
            client.initLoop();
            client.requestLoop();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("cannot communicate with server");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
