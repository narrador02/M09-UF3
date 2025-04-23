import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    final int PORT = 7777;
    final String HOST = "localhost";
    private ServerSocket srvSocket;
    private Socket clientSocket;

    public void connecta() throws IOException {
        System.out.println("Servidor en marxa a " + HOST + ":" + PORT);
        srvSocket = new ServerSocket(PORT);
        System.out.println("Esperant connexions a " + HOST + ":" + PORT);
        clientSocket = srvSocket.accept();
        System.out.println("Client connectat: " + clientSocket.getInetAddress());
    }

    public void repDades() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String linia;
        while ((linia = in.readLine()) != null) {
            if (linia.equals("")) break;
            System.out.println("Rebut: " + linia);
        }
        in.close();
    }

    public void tanca() throws IOException {
        if (clientSocket != null) clientSocket.close();
        if (srvSocket != null) srvSocket.close();
        System.out.println("Servidor tancat.");
    }

    public static void main(String[] args) {
        try {
            Servidor s = new Servidor();
            s.connecta();
            s.repDades();
            s.tanca();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}