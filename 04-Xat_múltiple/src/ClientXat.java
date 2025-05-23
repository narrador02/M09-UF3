import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private boolean sortir = false;

    public void connecta() throws IOException {
        socket = new Socket("localhost", 9999);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Client connectat a localhost:9999");
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatge) throws IOException {
        if (outputStream != null) {
            outputStream.writeObject(missatge);
            outputStream.flush();
            System.out.println("Enviant missatge: " + missatge);
        }
    }

    public void tancarClient() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
            System.out.println("Flux d'entrada tancat.");
            System.out.println("Flux de sortida tancat.");
            System.out.println("Tancant client...");
        } catch (IOException e) {
            System.out.println("Error tancant client: " + e.getMessage());
        }
    }

    public void ajuda() {
        System.out.println("---");
        System.out.println("Comandes disponibles:");
        System.out.println("  1.- Conectar al servidor (primer pas obligatori)");
        System.out.println("  2.- Enviar missatge personal");
        System.out.println("  3.- Enviar missatge al grup");
        System.out.println("  4.- (o línia en blanc)-> Sortir del client");
        System.out.println("  5.- Finalitzar tothom");
        System.out.println("---");
    }

    private String getLinea(Scanner scanner, String missatge, boolean obligatori) {
        String linia;
        do {
            System.out.print(missatge);
            linia = scanner.nextLine().trim();
            if (!obligatori && linia.isEmpty()) {
                return linia;
            }
        } while (linia.isEmpty());
        return linia;
    }

    public void executa() {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            
            while (!sortir) {
                try {
                    String missatgeCru = (String) inputStream.readObject();
                    String codi = Missatge.getCodiMissatge(missatgeCru);
                    String[] parts = Missatge.getPartsMissatge(missatgeCru);

                    if (codi == null || parts == null) {
                        System.out.println("Error: missatge incorrecte rebut");
                        continue;
                    }

                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            sortir = true;
                            System.out.println("Tancant tots els clients.");
                            break;
                        case Missatge.CODI_MSG_PERSONAL:
                            if (parts.length >= 3) {
                                String remitent = parts[1];
                                String missatge = parts[2];
                                System.out.println("Missatge de (" + remitent + "): " + missatge);
                            }
                            break;
                        case Missatge.CODI_MSG_GRUP:
                            if (parts.length >= 2) {
                                System.out.println("Missatge al grup: " + parts[1]);
                            }
                            break;
                        default:
                            System.out.println("Error: codi de missatge no reconegut");
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println("Error rebent missatge. Sortint...");
                    sortir = true;
                } catch (IOException e) {
                    System.out.println("Error rebent missatge. Sortint...");
                    sortir = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error inicialitzant flux d'entrada: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        Scanner scanner = new Scanner(System.in);
        
        try {
            client.connecta();
            
            Thread filRebre = new Thread(() -> client.executa());
            filRebre.start();
            
            client.ajuda();
            
            while (!client.sortir) {
                String linia = client.getLinea(scanner, "", false);
                if (linia.isEmpty()) {
                    client.sortir = true;
                    client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                } else {
                    try {
                        int opcio = Integer.parseInt(linia);
                        switch (opcio) {
                            case 1:
                                String nom = client.getLinea(scanner, "Introdueix el nom: ", true);
                                client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                                break;
                            case 2:
                                String destinatari = client.getLinea(scanner, "Destinatari: ", true);
                                String missatgePersonal = client.getLinea(scanner, "Missatge a enviar: ", true);
                                client.enviarMissatge(Missatge.getMissatgePersonal(destinatari, missatgePersonal));
                                break;
                            case 3:
                                String missatgeGrup = client.getLinea(scanner, "Missatge al grup: ", true);
                                client.enviarMissatge(Missatge.getMissatgeGrup(missatgeGrup));
                                break;
                            case 4:
                                client.sortir = true;
                                client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                                break;
                            case 5:
                                client.sortir = true;
                                client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                                break;
                            default:
                                System.out.println("Opció no vàlida");
                        }
                        client.ajuda();
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada no vàlida. Introdueix un número.");
                    } catch (IOException e) {
                        System.out.println("Error enviant missatge: " + e.getMessage());
                        client.sortir = true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error connectant al servidor: " + e.getMessage());
        } finally {
            client.tancarClient();
            scanner.close();
        }
    }
}