import java.io.*;

public class FilLectorCX implements Runnable {
    public static final String MSG_SORTIR = "sortir";
    private ObjectInputStream in;

    public FilLectorCX(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String missatge = (String) in.readObject();
                System.out.println("Rebut: " + missatge);
                
                if (missatge.equals(MSG_SORTIR)) {
                    System.out.println("Tancant client...");
                    System.out.println("El servidor ha tancat la connexió.");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error en llegir del servidor: " + e.getMessage());
        }
    }
}