import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {

    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    private ServerSocket serverSocket;
    private boolean sortir = false;
    private Hashtable<String, GestorClients> clients = new Hashtable<>();

    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);

            while (!sortir) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket);

                GestorClients gestor = new GestorClients(clientSocket, this);
                new Thread(gestor).start();
            }

        } catch (IOException e) {
            System.out.println("Error al escoltar: " + e.getMessage());
        } finally {
            pararServidor();
        }
    }

    public void pararServidor() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error al tancar servidor: " + e.getMessage());
        }
    }

    public void finalitzarXat() {
        enviarMissatgeGrup(MSG_SORTIR);
        clients.clear();
        sortir = true;
        pararServidor();
        System.out.println("DEBUG: multicast sortir");
    }

    public void afegirClient(GestorClients client) {
        String nom = client.getNom();
        if (nom != null && !nom.isEmpty()) {
            clients.put(nom, client);
            enviarMissatgeGrup("Entra: " + nom);
            System.out.println("DEBUG: multicast Entra: " + nom);
        }
    }

    public void eliminarClient(String nom) {
        if (clients.containsKey(nom)) {
            clients.remove(nom);
            enviarMissatgeGrup(nom + " ha sortit.");
        }
    }

    public void enviarMissatgeGrup(String msg) {
        for (GestorClients client : clients.values()) {
            client.enviarMissatge("Servidor", msg);
        }
    }

    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        GestorClients client = clients.get(destinatari);
        if (client != null) {
            client.enviarMissatge(remitent, missatge);
            System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge);
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
    }
}