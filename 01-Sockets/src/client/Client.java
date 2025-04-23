import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
    final String HOST = "localhost";
    final int PORT = 7777;
    private Socket socket;

    public void connecta() throws IOException {
        InetAddress addr = InetAddress.getByName(HOST);
        socket = new Socket(addr, PORT);
        System.out.println("Connectat a servidor en " + HOST + ":" + PORT);
    }

    public void enviaMissatges() throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        Scanner sc = new Scanner(System.in);
        String missatge;

        while (true) {
            missatge = sc.nextLine();
            if (missatge.equals("")) break;
            out.println(missatge);
            System.out.println("Enviat al servidor: " + missatge);
        }

        System.out.println("Prem Enter per tancar el client...");
        sc.nextLine();

        out.close();
        sc.close();
    }

    public void tanca() throws IOException {
        if (socket != null) socket.close();
        System.out.println("Client tancat");
    }

    public static void main(String[] args) {
        try {
            Client c = new Client();
            c.connecta();
            c.enviaMissatges();
            c.tanca();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}