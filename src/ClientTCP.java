import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class ClientTCP {

    Socket commReq;
    ObjectInputStream oisReq;
    ObjectOutputStream oosReq;

    BufferedReader consoleIn; // flux de lecture lignes depuis clavier

    public ClientTCP(String serverIp, int serverPort) throws IOException {
        commReq = new Socket(serverIp, serverPort);
        oosReq = new ObjectOutputStream(commReq.getOutputStream());
        oisReq = new ObjectInputStream(commReq.getInputStream());
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
    }

    public void initLoop() throws IOException, ClassNotFoundException {

        String line = null;
        boolean ok = false;

        while (!ok) {

	        System.out.println("Donner votre pseudo : ");
	        String pseudo = consoleIn.readLine();
	        oosReq.writeObject(pseudo);
	        ok = oisReq.readBoolean();

	        if (ok) {
	            System.out.println("Merci :)");
            } else {
	            System.out.println("Erreur dans le pseudo");
            }

        }
    }

    public void requestLoop() throws IOException, ClassNotFoundException {

        String reqLine = null;
        String[] reqParts = null;
        boolean stop = false;
        int nbTurn = 0;
        String advName = "";

        while (!stop) {

            System.out.print("Client> ");
            reqLine = consoleIn.readLine();
            reqParts = reqLine.split(" ");

            if (reqParts[0].equals("players")) {

		        oosReq.writeInt(1);
		        oosReq.flush();
		        ArrayList<Player> listJoueur = (ArrayList<Player>) oisReq.readObject();
		        System.out.println("Voici la liste des joueurs : ");
		        for (Player player: listJoueur) {
		            System.out.println(player.name);
                }

            } else if (reqParts[0].equals("wait")) {

		        try {
		            nbTurn = Integer.parseInt(reqParts[1]);
		            oosReq.writeInt(2);
		            oosReq.flush();
		            oosReq.writeInt(nbTurn);
		            oosReq.flush();
		            boolean ok = oisReq.readBoolean();
		            if (ok) {
		                advName = (String) oisReq.readObject();
                        System.out.println("Je joue contre " + advName + " en " + nbTurn + " coups");
                    } else {
		                System.out.println("Pas possible de créer la partie");
		                continue;
                    }
                    partyLoop(nbTurn);
                } catch (NumberFormatException e) {
		            System.out.println("Il faut un nombre");
                }

            } else if (reqParts[0].equals("vs")) {

		        oosReq.writeInt(3);
		        oosReq.flush();

		        if (reqParts[1] != null) {
		            oosReq.writeBoolean(true);
		            oosReq.flush();
		            oosReq.writeObject(reqParts[1]);
                } else {
		            oosReq.writeBoolean(false);
		            oosReq.flush();
                }

                boolean ok = oisReq.readBoolean();

		        if (ok) {
		            advName = (String) oisReq.readObject();
		            nbTurn = oisReq.readInt();
                } else {
		            System.out.println("Pas possible de rejoindre la partie");
		            continue;
                }

                System.out.println("Je joue contre " + advName + " en " + nbTurn + " coups");
                partyLoop(nbTurn);

            } else if (reqParts[0].equals("quit")) {
                stop = true;
            }
        }
    }

    private void partyLoop(int nbTurn) throws IOException, ClassNotFoundException {

        String line = null;
        String solus = null;

        for (int i = 0; i < nbTurn; i++) {

            System.out.println(oisReq.readObject());
            String reponse = consoleIn.readLine();

            try {
                int a = Integer.parseInt(reponse);
                System.out.println(a);
                oosReq.writeInt(a);
                oosReq.flush();
                boolean ok = oisReq.readBoolean();
                int ptJoueur = oisReq.readInt();
                int ptAutreJoueur = oisReq.readInt();
                if (ok) {
                    System.out.println("Vous avez gagné");
                } else {
                    System.out.println("Vous avez perdu");
                }
                System.out.println("Votre score : " + ptJoueur + ", score de l'adversaire : " + ptAutreJoueur);
            } catch (NumberFormatException e) {
                System.out.println("Il faut un entier");
            }

        }
    }
}
