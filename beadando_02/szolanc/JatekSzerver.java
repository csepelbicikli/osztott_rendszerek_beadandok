package szolanc;

import java.rmi.registry.*;
import java.rmi.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
/*
A játékszerver

A játékszervert a 32123 porthoz rendeljük. A szerver egy játékmenetet a következőképpen bonyolít le:

    Várakozik két játékos csatlakozására, akik a csatlakozás után megküldik a szervernek nevüket.
    A szerver létrehoz egy fájlt, amibe játékmenet során összeálló szóláncot fogja rögzíteni, a következő névvel:
    <jatekos1>_<jatekos2>_<idobelyeg>.txt
    Amint a második játékos is csatlakozott, egy speciális start üzenettel
    jelzi az először csatlakozottnak, hogy ő a kezdőjátékos, tehát először neki kell egy tetszőleges szót mondania.
    A szerver innentől kezdve mindig fogad egy egy szavas üzenetet az egyik játékostól, majd (ellenőrzés nélkül) 
    továbbítja a másik játékos felé, aki válaszként elküldi a szólánc következő elemét, amit a szerver továbbít, stb.
    A szerver rögzíti a játék során összeálló szóláncot a játékmenethez létrehozott fájlba. 
    Egy sorban a beküldő játékos neve, majd attól szóközzel elválasztva az általa beküldött szó szerepeljen.
    Ha valamelyik játékos az exit üzenetet küldi (ez a szóláncban tiltott szó lesz),
    vagy váratlanul lecsatlakozik, a játékmenet véget ér, és a másik játékos nyert. 
    A nyertest a szerver a nyert üzenettel értesítse,
    majd mindkét játékossal bontsa a kapcsolatot.
    A szervert készítsük fel több játékmenet egy időben történő kezelésére: 
    tehát minden két, egymás után csatlakozott játékoshoz indítson el egy játékmenetet,
    majd azonnal legyen képes újabb két játékos fogadására. 
    A szerver álljon le, ha 30 másodpercen keresztül nem csatlakozik egy játékos sem 
    (tipp: ServerSocket osztály setSoTimout metódusa), és már nincsen folyamatban lévő játék.
*/
public class JatekSzerver {
	
	private static int sc = 1;
	private static Registry r ;
    private final int port;
    private ServerSocket server;
    
    private static String[] ts;
    
    private Map<String, PrintWriter> clients = new HashMap<>();

    public int getPort() {
        return port;
    }
    
    JatekSzerver(){
		/*
		try{
		r = LocateRegistry.getRegistry("localhost",12345);
        ts = r.list();
        }catch(Exception e){
			e.printStackTrace();
		}
        if (! vanIlyen("tiltott1")){
			System.out.println("Valami gond van az rmi szerverekkel, még a tiltott1 sincs meg.");
		}
		*/
        this.port = 32123;
        try {
            server = new ServerSocket(port);
			server.setSoTimeout(30000);
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
            } catch (SocketTimeoutException e) {
				if(clients.size()==0){
					System.err.println("30 mp-ig nem jelentkezett kliens. A szerver leall...");
					return;
				}
            }catch (IOException e) {
                System.err.println("Hiba a kliensek fogadasakor.");
                e.printStackTrace();
            }
        }
    }
        
    private synchronized boolean addClientPair(String name1, String name2,PrintWriter pw1,PrintWriter pw2) {
        if (clients.get(name1) != null||clients.get(name2) != null) return false; 
        clients.put(name1, pw1);
		clients.put(name2, pw2);
        return true;
    }
        
    private synchronized void removeClientPair(String name1, String name2) {
		clients.remove(name1);        
		clients.remove(name2);
    }
        
    private synchronized void send(String nameSender,PrintWriter pwRecipient, String msg, PrintWriter fpw) {
		if(! msg.equals("nyert")){
			fpw.println(nameSender + " " + msg);
			fpw.flush();
		}
		pwRecipient.println(msg);
	}
        
    class ClientHandler extends Thread {
                
        PrintWriter pw1, pw2;
        BufferedReader br1, br2;
        String name1, name2;
        PrintWriter fpw;
        TiltottInterface s;
                
        ClientHandler(Socket s1, Socket s2) {
            try {
				
				
                pw1 = new PrintWriter(s1.getOutputStream(), true);
                br1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
				pw2 = new PrintWriter(s2.getOutputStream(), true);
                br2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
                boolean ok = false;
                while (! ok) {
                    name1 = br1.readLine();
                    if (name1 == null) return;
                    name2 = br2.readLine();
                    if (name2 == null) return;
                    ok = addClientPair(name1,name2, pw1, pw2);
                }
                Date now = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss.S");
				String fn = name1+"_"+name2+"_"+sdf.format(now)+".txt";
				File fout = new File(fn);
				fpw = new PrintWriter(fout);
				pw1.println("start");
            } catch (IOException e) {
                System.err.println("Inicializalasi problema egy kliensnel. ");//Nev: " + name);
            }
            ++sc;
        }
                
        @Override
        public void run() {
			System.out.println(s);
            String message1 = null;
			String message2 = null;
			//System.out.println(Arrays.toString(ts));
			//int akt = sc%ts.length+1;
			try{
				s = (TiltottInterface) Naming.lookup("rmi://localhost:8080/tiltott1");//+akt);
			}catch(NotBoundException e){
				System.out.println("NotBoundException: nem sikerult kapcsolodni");
				e.printStackTrace();
				return;
			}
			catch(IOException e){
				e.printStackTrace();
				return;
			}
            try {
				while (true) {
					boolean ok=false;
					while(!ok){
						System.out.println("Server loop pt1");
						message1 = br1.readLine();
						if (message1==null||message1.equals("nyert")) {break;}
						System.out.println("Server loop pt1 b");
						if(! s.tiltottE(message1)){
							ok=true;
							System.out.println("Server ok");
							pw1.println("ok");
						}else{
							System.out.println("Server nok");
							pw1.println("nok");
						}
						System.out.println("Server loop pt1 end");
					}
					if (message1 == null||message1.equals("exit")){
						send(name1,pw2,"nyert",fpw);
						break;
					}else{
						send(name1,pw2,message1,fpw);
						ok=false;
						while(!ok){
							System.out.println("Server loop pt2");
							message2 = br2.readLine();
							System.out.println("Message2: "+message2);
							if (message2==null||message2.equals("nyert")) {break;}
							if(! s.tiltottE(message2)){
								ok=true;
								System.out.println("Server ok");
								pw2.println("ok");
							}else{
								System.out.println("Server nok");
								pw2.println("nok");
							}
							System.out.println("Server loop pt2 end");
						}
						if (message2 == null||message2.equals("exit")){
							send(name2,pw1,"nyert",fpw);
							break;
						}else{
							send(name2,pw1,message2,fpw);
						}
					}
				}
            } catch (IOException e) {
                System.err.println("Kommunikacios problema egy kliensnel.");
                e.printStackTrace();
            } finally {
                removeClientPair(name1, name2);
                try {fpw.close();pw1.close(); br1.close();pw2.close(); br2.close();} catch (IOException e) {}
            }
        }
        

    }
    
    public static boolean vanIlyen(String x){
		for(String t:ts){
			if (x.equals(t)){
				return true;
			}
		}
		return false;
	}
        
    public static void main(String[] args)  {
		
        JatekSzerver szerver;
        try{
			szerver = new JatekSzerver();
			if (szerver != null) szerver.handleClients();
		}catch(Exception e){e.printStackTrace();}
        
    }
        
}
