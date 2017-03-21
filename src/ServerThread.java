import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

class ServerThread extends Thread {

    Socket commReq;
    ObjectInputStream oisReq;
    ObjectOutputStream oosReq;
    int id;
    Game game;
    Player myPlayer; // l'objet player associé à ce thread
    Party currentParty; // l'objet party associé à player

    public ServerThread(int id, Socket commReq, Game game) {
        this.id = id;
        this.commReq = commReq;
        this.game = game;
        myPlayer = null;
        currentParty = null; // reste à null tant que le joueur n'a pas crée ou rejoint une partie
    }

    public void run() {
        try {
            oisReq = new ObjectInputStream(commReq.getInputStream());
            oosReq = new ObjectOutputStream(commReq.getOutputStream());
            initLoop();
            requestLoop();
        } catch (Exception e) {
            System.out.println(id + " - client disconnected");
            if (myPlayer != null) game.removePlayer(myPlayer);
        }
    }

    public void initLoop() throws IOException, ClassNotFoundException {

        String pseudo = (String) oisReq.readObject();

        while ((myPlayer = game.addPlayer(pseudo)) == null) {
            oosReq.writeBoolean(false);
            oosReq.flush();
            pseudo = (String) oisReq.readObject();
        }
        oosReq.writeBoolean(true);
        oosReq.flush();

    }

    public void requestLoop() throws IOException, ClassNotFoundException {
	    while (true) {
	        int id = oisReq.readInt();
	        if (id == 1) requestListPlayers();
	        else if (id == 2) requestWaitPlayer();
	        else if (id == 3) requestContestPlayer();
        }
    }

    private void requestListPlayers() throws IOException, ClassNotFoundException {
        oosReq.writeObject(new ArrayList<>(game.getMapParties().keySet()));
    }

    private void requestWaitPlayer() throws IOException, ClassNotFoundException {

	    if ((currentParty = game.createParty(myPlayer, oisReq.readInt())) == null) {
	        oosReq.writeBoolean(false);
	        oosReq.flush();
	        return;
        }

        oosReq.writeBoolean(true);
	    oosReq.flush();
        currentParty.waitforStart();
	    oosReq.writeObject(currentParty.player2.name);
	    partyLoop();
        game.removeParty(currentParty);

    }

    private void requestContestPlayer() throws IOException, ClassNotFoundException {

	    if (oisReq.readBoolean()) {
	        String pseudo = (String) oisReq.readObject();
	        if ((currentParty = game.isPlayerInParty(pseudo)) == null) {
	            oosReq.writeBoolean(false);
	            oosReq.flush();
	            return;
            }
        } else {
	        currentParty = game.chooseRandomParty();
        }

        oosReq.writeBoolean(true);
	    oosReq.flush();
	    oosReq.writeObject(currentParty.player1.name);
	    oosReq.writeInt(currentParty.nbTurn);
	    oosReq.flush();
	    currentParty.setSecondPlayer(myPlayer);
	    partyLoop();

    }

    private void partyLoop() throws IOException, ClassNotFoundException {

        for (int i = 0; i < currentParty.nbTurn; i++) {

	        oosReq.writeObject(currentParty.getCalcul());
	        currentParty.integrateResult(oisReq.readInt(), myPlayer);
            oosReq.writeBoolean(myPlayer.equals(currentParty.winner));
            oosReq.flush();

            if (myPlayer.equals(currentParty.player1)) {
                oosReq.writeInt(currentParty.ptsPlayer1);
                oosReq.writeInt(currentParty.ptsPlayer2);
            } else {
                oosReq.writeInt(currentParty.ptsPlayer2);
                oosReq.writeInt(currentParty.ptsPlayer1);
            }

            oosReq.flush();

        }

    }
}
