import java.io.*;
import java.net.Socket;

public class GestorClients implements Runnable {

    private Socket client;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket client, ServidorXat servidor) {
        this.client = client;
        this.servidor = servidor;

        try {
            oos = new ObjectOutputStream(client.getOutputStream());
            ois = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creant fluxos del client: " + e.getMessage());
            sortir = true;
        }
    }

    public String getNom() {
        return nom;
    }

    public void run() {
        try {
            while (!sortir) {
                Object obj = ois.readObject();
                if (obj instanceof String) {
                    String missatge = (String) obj;
                    processaMissatge(missatge);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error rebent missatge: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.out.println("Error tancant client: " + e.getMessage());
            }
        }
    }

    public void enviarMissatge(String remitent, String missatge) {
        if (oos != null) {
            try {
                String msg = Missatge.getMissatgePersonal(remitent, missatge);
                oos.writeObject(msg);
                oos.flush();
            } catch (IOException e) {
                System.out.println("Error enviant missatge a " + nom + ": " + e.getMessage());
            }
        }
    }

    private void processaMissatge(String missatgeRaw) {
        String codi = Missatge.getCodiMissatge(missatgeRaw);
        String[] parts = Missatge.getPartsMissatge(missatgeRaw);

        if (codi == null || parts == null) return;

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                if (parts.length >= 2) {
                    nom = parts[1];
                    servidor.afegirClient(this);
                    System.out.println(nom + " connectat.");
                }
                break;

            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidor.eliminarClient(nom);
                break;

            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidor.finalitzarXat();
                break;

            case Missatge.CODI_MSG_PERSONAL:
                if (parts.length >= 3) {
                    String destinatari = parts[1];
                    String text = parts[2];
                    servidor.enviarMissatgePersonal(destinatari, nom, text);
                }
                break;

            case Missatge.CODI_MSG_GRUP:
                if (parts.length >= 2) {
                    String text = parts[1];
                    servidor.enviarMissatgeGrup(nom + ": " + text);
                }
                break;

            default:
                System.out.println("Codi incorrecte rebut: " + codi);
        }
    }
}