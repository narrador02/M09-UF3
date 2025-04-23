import java.io.*;

public class FilServidorXat implements Runnable {
    private ObjectInputStream in;

    public FilServidorXat(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String missatge = (String) in.readObject();
                System.out.println("Rebut: " + missatge);
                
                if (missatge.equals(ServidorXat.MSG_SORTIR)) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Connexió amb el client tancada.");
        }
    }
}