import java.net.*;
import java.io.*;
import java.util.*;
/*
A játékszerver

A játékszervert a 32123 porthoz rendeljük. A szerver egy játékmenetet a következőképpen bonyolít le:

    Várakozik két játékos csatlakozására, akik a csatlakozás után megküldik a szervernek nevüket.

    A szerver létrehoz egy fájlt, amibe játékmenet során összeálló szóláncot fogja rögzíteni, a következő névvel: <jatekos1>_<jatekos2>_<idobelyeg>.txt

    Amint a második játékos is csatlakozott, egy speciális start üzenettel jelzi az először csatlakozottnak, hogy ő a kezdőjátékos, tehát először neki kell egy tetszőleges szót mondania.

    A szerver innentől kezdve mindig fogad egy egy szavas üzenetet az egyik játékostól, majd (ellenőrzés nélkül) továbbítja a másik játékos felé, aki válaszként elküldi a szólánc következő elemét, amit a szerver továbbít, stb.

    A szerver rögzíti a játék során összeálló szóláncot a játékmenethez létrehozott fájlba. Egy sorban a beküldő játékos neve, majd attól szóközzel elválasztva az általa beküldött szó szerepeljen.

    Ha valamelyik játékos az exit üzenetet küldi (ez a szóláncban tiltott szó lesz), vagy váratlanul lecsatlakozik, a játékmenet véget ér, és a másik játékos nyert. A nyertest a szerver a nyert üzenettel értesítse, majd mindkét játékossal bontsa a kapcsolatot.

A szervert készítsük fel több játékmenet egy időben történő kezelésére: tehát minden két, egymás után csatlakozott játékoshoz indítson el egy játékmenetet, majd azonnal legyen képes újabb két játékos fogadására. A szerver álljon le, ha 30 másodpercen keresztül nem csatlakozik egy játékos sem (tipp: ServerSocket osztály setSoTimout metódusa), és már nincsen folyamatban lévő játék.
*/
public class JatekSzerver {
	
	

    private final int port;
    private ServerSocket server;
    
    private Map<String, PrintWriter> clients = new HashMap<String, PrintWriter>();
    
    public int getPort() {
        return port;
    }
    
    JatekSzerver() {
        this.port = 32123;
        try {
            server = new ServerSocket(port);
            System.out.println("A jatekszerver elindult.");
        } catch (IOException e) {
            System.err.println("Hiba a jatekszerver inditasanal.");
            e.printStackTrace();
        }
    }
        
    public void handleClients() {
        while (true) {
            try {
				Socket client1 = server.accept();
				Socket client2 = server.accept();
                new ClientHandler(client1, client2).start();
            } catch (IOException e) {
                System.err.println("Hiba a kliensek fogadasakor.");
                e.printStackTrace();
            }
        }
    }
        
    private synchronized boolean addClient(String name, PrintWriter pw) {
        if (clients.get(name) != null) return false;
        clients.put(name, pw);
        //send(name, "csatlakozott.");
        return true;
    }
        
    private synchronized void removeClient(String name) {
        clients.remove(name);
    }
        
    private synchronized void send(String name, String message) {
        System.out.println(name + "<- " + message);
        for (String n : clients.keySet()) {
            if (n.equals(name)) {
                clients.get(n).println(message);
            }
        }
    }
        
    class ClientHandler extends Thread {
                
        PrintWriter pw1, pw2;
        BufferedReader br1, br2;
        String name1, name2;
                
        ClientHandler(Socket s1, Socket s2) {
            try {
                pw1 = new PrintWriter(s1.getOutputStream(), true);
                br1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
				pw2 = new PrintWriter(s2.getOutputStream(), true);
                br2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
                boolean ok1 = false;
                while (! ok1) {
                    name1 = br1.readLine();
                    if (name1 == null) return;
                    ok1 = addClient(name1, pw1);
                }
				boolean ok2=false;
				while (! ok2) {
                    name2 = br2.readLine();
                    if (name2 == null) return;
                    ok2 = addClient(name2, pw2);
					//amikor a masodik csatlakozott az elsonek jelzunk
                    if (ok2) pw1.println("start");
                }
            } catch (IOException e) {
                System.err.println("Inicializalasi problema egy kliensnel. ");//Nev: " + name);
            }
        }
                
        @Override
        public void run() {
            String message1 = null;
			String message2 = null;
            try {
                while (true) {
                    message1 = br1.readLine();
                    if (message1 == null||message1.equals("exit")){
						send(name2,"nyert");
						break;
					}else{
						send(name2, name2+" "+message1);
					
						message2 = br2.readLine();
						if (message2 == null||message2.equals("exit")){
							send(name1,"nyert");
							break;
						}else{
						send(name1, name2+" "+message2);
						}
					}
                }
            } catch (IOException e) {
                System.err.println("Kommunikacios problema egy kliensnel.");// Nev: " + name);
            } finally {
                //if (name != null) send(name, "kilepett.");
                removeClient(name1);
				removeClient(name2);
                try {pw1.close(); br1.close();pw2.close(); br2.close();} catch (IOException e) {}
            }
        }
    }
        
    public static void main(String[] args) {
        JatekSzerver szerver = new JatekSzerver();
        if (szerver != null) szerver.handleClients();
    }
        
}
