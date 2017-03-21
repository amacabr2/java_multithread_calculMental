import java.util.*;

class Game {

    private static Random loto = new Random(Calendar.getInstance().getTimeInMillis());

    private Set<Party> lstParties; // la liste de toutes les parties, commencées ou non
    private Set<Player> lstPlayers; // la liste de tous les joueurs
    private Map<Player, Party> mapParties; // le map associant une partie au joueur l'ayant créé.

    public Game() {
        lstParties = new HashSet<Party>();
        lstPlayers = new HashSet<Player>();
        mapParties = new HashMap<Player, Party>();
    }

    public Set<Party> getLstParties() {
        return lstParties;
    }

    public Set<Player> getLstPlayers() {
        return lstPlayers;
    }

    public Map<Player, Party> getMapParties() {
        return mapParties;
    }

    public synchronized Player addPlayer(String pseudo) {

	/* NB : si un joueur avec le même pseudo existe déjà, retourne null 
       sinon, crée et retourne un nouveau joueur avec le pseudo
	 */
        Player player = isPlayerConnected(pseudo);
        if (player != null) return null;

        Player p = new Player(pseudo);
        lstPlayers.add(p);
        return p;
    }

    public synchronized void removePlayer(Player p) {
        lstPlayers.remove(p);
    }

    public synchronized Party createParty(Player player, int nbTurn) {

	/* NB : si le joueur a déjà créé une partie en attente ou en cours, retourne null 
	   sinon, crée et retourne une nouvel partie, et met à jour le map
	 */

        if (mapParties.containsKey(player)) return null;
        Party p = new Party(player, nbTurn);
        mapParties.put(player, p);
        return p;
    }

    public synchronized void removeParty(Party p) {
        mapParties.remove(p.player1);
        lstParties.remove(p);
    }

    public synchronized String getWaitingPlayers() {
        String lst = "";
        Set<Player> set = mapParties.keySet();
        for (Player p : set) {
            lst = lst + p.name + ",";
        }
        return lst;
    }

    public synchronized Party chooseRandomParty() {

        Set<Player> set = mapParties.keySet();
        int index = loto.nextInt(set.size());
        int i = 0;
        for (Player p : set) {
            if (i == index) return mapParties.get(p);
            i++;
        }
        return null;
    }

    public synchronized Player isPlayerConnected(String pseudo) {
        for (Player p : lstPlayers) {
            if (p.name.equals(pseudo)) return p;
        }
        return null;
    }

    public synchronized Party isPlayerInParty(String pseudo) {
        Set<Player> set = mapParties.keySet();
        for (Player p : set) {
            if (p.name.equals(pseudo)) return mapParties.get(p);
        }
        return null;
    }



}
