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
        System.out.println("Connectat al servidor.");
    }

    public void enviaMissatges() throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        Scanner sc = new Scanner(System.in);
        String missatge;

        System.out.println("Escriu un missatge (escriu 'Adeu!' per sortir):");
        do {
            missatge = sc.nextLine();
            out.println(missatge);
        } while (!missatge.equals("Adeu!"));

        sc.close();
        out.close();
    }

    public void tanca() throws IOException {
        if (socket != null) socket.close();
        System.out.println("Client tancat.");
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