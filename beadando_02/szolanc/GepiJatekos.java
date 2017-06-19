package szolanc;

import java.net.*;
import java.io.*;
import java.util.*;

public class GepiJatekos {
    private PrintWriter pw;
    private BufferedReader br;
	private List<String> szoKincs;
	private String jatekosNev;
	private String szoKincsFajlNev;
	
    private Scanner sc = new Scanner(System.in);

	private String getKovSzo(char betu){
		for(int i=0;i<szoKincs.size();i++){
			if (szoKincs.get(i).charAt(0)==betu){
				return szoKincs.get(i);
			}
		}
		return "";
	}
	private boolean xstKovSzo(char betu){
		for(int i=0;i<szoKincs.size();i++){
			if (szoKincs.get(i).charAt(0)==betu){
				return true;
			}
		}
		return false;
	}
	
	private String kuld(String szo) throws IOException{
		pw.println(szo);
		System.out.println(jatekosNev+": "+szo);
		return br.readLine();
	}
	
    GepiJatekos(String jatekosNev, String szoKincsFajlNev) {
		String host = "localhost";
		int port = 32123;
		this.szoKincsFajlNev = szoKincsFajlNev;
		this.jatekosNev = jatekosNev;
		
		try {
			Scanner szoKincsFajl = new Scanner(new File(szoKincsFajlNev));
			szoKincs = new ArrayList<String>();
			while (szoKincsFajl.hasNext()) {
				szoKincs.add(szoKincsFajl.nextLine());
			}
		}
		catch(IOException e){
			System.err.println("Szokincs fajl megnyitasa sikertelen");
			return;
		}
        try {
            Socket client = new Socket(host, port);
            pw = new PrintWriter(client.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            pw.println(jatekosNev);
			String kapottSzo = br.readLine();
			if (kapottSzo.equals("start")){
				int c=0;
				boolean ok=false;
				while(!ok){
					String kovSzo = szoKincs.get(c);
					kuld(kovSzo);
					String res = br.readLine();
					System.out.println(res);
					if(res.equals("ok")){
						ok=true;
					}
					++c;
				}
				kapottSzo=br.readLine();
			}
			while((! "exit".equals(kapottSzo)) && null != kapottSzo){
				if (! kapottSzo.equals("nyert")){
					boolean ok=false;
					while(!ok){
						if (xstKovSzo(kapottSzo.charAt(kapottSzo.length()-1))){
							String kovSzo = getKovSzo(kapottSzo.charAt(kapottSzo.length()-1));
							kuld(kovSzo);
							String res=br.readLine();
							if(res.equals("ok")){
								ok=true;
							}
						
						} else {
							pw.println("exit");
							return;
						}
					}
					kapottSzo=br.readLine();
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
		String jNev=args[0];
		String szNev=args[1];
        new GepiJatekos(jNev,szNev);
    }
}
