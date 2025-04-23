import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connecta() throws IOException {
        socket = new Socket("localhost", 9999);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Client connectat a localhost:9999");
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatge) throws IOException {
        out.writeObject(missatge);
        out.flush();
        System.out.println("Enviant missatge: " + missatge);
    }

    public void tancarClient() throws IOException {
        if (out != null) out.close();
        if (in != null) in.close();
        if (socket != null) socket.close();
        System.out.println("Client tancat.");
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        try {
            client.connecta();
            
            FilLectorCX fil = new FilLectorCX(client.in);
            new Thread(fil).start();
            System.out.println("Fil de lectura iniciat");

            Scanner scanner = new Scanner(System.in);
            String missatge;
            while (true) {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = scanner.nextLine();
                client.enviarMissatge(missatge);
                
                if (missatge.equals(FilLectorCX.MSG_SORTIR)) {
                    break;
                }
            }
            
            scanner.close();
            client.tancarClient();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}