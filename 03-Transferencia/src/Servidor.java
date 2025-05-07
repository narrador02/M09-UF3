import java.io.*;
import java.net.*;

public class Servidor {

    private static final int PORT = 8888;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Esperando conexión...");
            Socket socket = serverSocket.accept(); 
            
            System.out.println("Conexión aceptada: " + socket.getInetAddress());

            recibirFicheros(socket);

            socket.close(); 
            System.out.println("Conexión cerrada");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void recibirFicheros(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        String ficheroNombre = dis.readUTF();
        System.out.println("Recibiendo fichero: " + ficheroNombre);

        FileOutputStream fos = new FileOutputStream(ficheroNombre);
        byte[] buffer = new byte[2048];
        int bytesLeidos;
        
        while ((bytesLeidos = dis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesLeidos);
        }

        fos.close();
    }
}