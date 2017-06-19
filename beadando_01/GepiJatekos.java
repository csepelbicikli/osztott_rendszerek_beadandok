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
	
	private void torolSzo(String szo){
		for(int i=0;i<szoKincs.size();i++){
			if (szoKincs.get(i).equals(szo)){
				szoKincs.remove(i);
				return;
			}
		}
	}
	
	private void kuld(String szo){
		pw.println(szo);
		torolSzo(szo);
		System.out.println(jatekosNev+": "+szo);
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
			String answer = br.readLine();
			if (answer.equals("start")){
				String kovSzo = szoKincs.get(0);
				kuld(kovSzo);
				answer=br.readLine();
			}
			while((! "exit".equals(answer)) && null != answer){
				if (! answer.equals("nyert")){
					String kuldoNev = answer.split(" ")[0];
					String kapottSzo = answer.substring(kuldoNev.length()+1); //space utani karakter
					if (xstKovSzo(kapottSzo.charAt(kapottSzo.length()-1))){
						String kovSzo = getKovSzo(kapottSzo.charAt(kapottSzo.length()-1));
						kuld(kovSzo);
						answer=br.readLine();
					} else {
						pw.println("exit");
						return;
					}
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
