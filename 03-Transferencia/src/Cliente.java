import java.io.*;
import java.net.*;

public class Cliente {

    private static final String DIR_ARRIBADA = "C:\\temp\\";
    private static final String HOST = "localhost";
    private static final int PORT = 8888;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(HOST, PORT);
            System.out.println("Conectando a: " + HOST + ":" + PORT);

            enviarFicheros(socket);
            recibirFicheros(socket);
            cerrarConexion(socket);
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void enviarFicheros(Socket socket) throws IOException {
        String ficheroNombre = "C:\\Users\\jairo\\M09-UF3\\02-Xat_a_dos\\src\\FilLectorCX.java";
        File file = new File(ficheroNombre);

        if (file.exists()) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(file.getName());   

            FileInputStream fis = new FileInputStream(ficheroNombre);
            byte[] buffer = new byte[2048];
            int bytesLeidos;
            
            while ((bytesLeidos = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesLeidos);  
            }
            
            fis.close();
        } else {
            System.out.println("El archivo no existe.");
        }
    }

    public static void recibirFicheros(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        String ficheroNombre = DIR_ARRIBADA + "received_file.jpg";
        FileOutputStream fos = new FileOutputStream(ficheroNombre);

        byte[] buffer = new byte[2048];
        int bytesLeidos;

        while ((bytesLeidos = dis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesLeidos); 
        }

        fos.close();
    }

    public static void cerrarConexion(Socket socket) throws IOException {
        socket.close();
        System.out.println("Conexión cerrada");
    }
}