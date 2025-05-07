import java.io.*;

public class Fichero {

    private String nom;
    private byte[] contingut;

    public Fichero(String nom) {
        this.nom = nom;
        this.contingut = carregarContingut(nom);
    }

    public byte[] getContingut() {
        return contingut;
    }

    private byte[] carregarContingut(String nom) {
        byte[] data = null;
        try {
            File file = new File(nom);
            FileInputStream fis = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
