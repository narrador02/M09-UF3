import java.io.*;
import java.net.*;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";
    private ServerSocket serverSocket;

    public void iniciarService() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }

    public void pararService() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public String getNom(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.writeObject("Escriu el teu nom:");
        return (String) in.readObject();
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        try {
            servidor.iniciarService();
            Socket clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            String nomClient = servidor.getNom(in, out);
            System.out.println("Nom rebut: " + nomClient);

            FilServidorXat fil = new FilServidorXat(in);
            new Thread(fil).start();
            System.out.println("Fil de xat creat.");
            System.out.println("Fil de " + nomClient + " iniciat");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String missatge;
            while (true) {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = reader.readLine();
                out.writeObject(missatge);
                out.flush();
                
                if (missatge.equals(MSG_SORTIR)) {
                    System.out.println("Fil de xat finalitzat.");
                    break;
                }
            }
            
            clientSocket.close();
            servidor.pararService();
            System.out.println("Servidor aturat.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}