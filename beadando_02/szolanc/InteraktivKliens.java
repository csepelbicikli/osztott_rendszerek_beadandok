package szolanc;

import java.net.*;
import java.io.*;
import java.util.*;

public class InteraktivKliens {
    private PrintWriter pw;
    private BufferedReader br;
	private String jatekosNev;
    private Scanner sc = new Scanner(System.in);
	
	private boolean helyes(String kapottSzo, String kovSzo){
		if (! kovSzo.matches("[a-zA-Z]+")){
			return false;
		}
		if (kovSzo.charAt(0)==kapottSzo.charAt(kapottSzo.length()-1)){
			return true;
		}
		return false;
	}
	
	private void kuld(String szo) throws IOException{
		pw.println(szo);
	}
	
    InteraktivKliens(){ 
		String host = "localhost";
		int port = 32123;
		System.out.print("Kerek egy nevet: ");
		String jatekosNev = sc.nextLine();
        try {
            Socket client = new Socket(host, port);
            pw = new PrintWriter(client.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            pw.println(jatekosNev);
			try{Thread.sleep(100);}catch(InterruptedException ex){ex.printStackTrace();}
			String kapottSzo = br.readLine();
			if (kapottSzo.equals("start")){
				String kovSzo;
				boolean ok = false;
				while(!ok){
					System.out.print("Kerek egy szot: ");
					do{
						kovSzo = sc.nextLine();
					}while (kovSzo.equals(""));
					kuld(kovSzo);
					try{Thread.sleep(100);}catch(InterruptedException ex){ex.printStackTrace();}
					String res = br.readLine();
					System.out.println(res);
					if(res.equals("ok")){
						ok=true;
					}else if (res.equals("nok")){
						System.out.println("Tiltott szo!");
					}
				}
				try{Thread.sleep(100);}catch(InterruptedException ex){ex.printStackTrace();}
				kapottSzo= br.readLine();
			}
			
			while((! "exit".equals(kapottSzo)) && null != kapottSzo){
				if (! kapottSzo.equals("nyert")){
					System.out.println("Kapott szo: "+kapottSzo);
					String kovSzo;
					boolean ok = false;
					while(!ok){
						do{
							System.out.print("Kerek egy szot: ");
							kovSzo=sc.nextLine();
							if (kovSzo.equals("exit")){
								pw.println("exit");
								return;
							}
							if (! helyes (kapottSzo, kovSzo)){
								System.out.println("Hibas szo!");
							}
						}while(! helyes(kapottSzo, kovSzo));
						kuld(kovSzo);
						try{Thread.sleep(100);}catch(InterruptedException ex){ex.printStackTrace();}
						String res = br.readLine();
						if(res.equals("ok")){
							ok=true;
						}else if (res.equals("nok")){
							System.out.println("Tiltott szo!");
						}
					}
					try{Thread.sleep(100);}catch(InterruptedException ex){ex.printStackTrace();}
					kapottSzo= br.readLine();
				} else{
					System.out.println(jatekosNev+" nyert");
					return;
				}
			}
			client.close();
        } catch (IOException e) {
            System.err.println("Nem sikerult kapcsolodni a szerverhez.");
            return;
        }
      
    }
        
    public static void main(String[] args) {
        new InteraktivKliens();
    }
}
