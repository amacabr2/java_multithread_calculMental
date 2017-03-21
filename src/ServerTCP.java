import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class ServerTCP {

    private ServerSocket conn;
    private Socket comm;
    private Game game;

    public ServerTCP(int serverPort) throws IOException {
        conn = new ServerSocket(serverPort);
        game = new Game();
    }

    public void mainLoop() throws IOException, ClassNotFoundException {
        int id =1;
        while (true) {
	        comm = conn.accept();
	        ServerThread t = new ServerThread(id, comm, game);
	        t.start();
        }
    }
}
