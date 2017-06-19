import java.net.*;
import java.io.*;
import java.util.*;

public class InteraktivKliens {
    private PrintWriter pw;
    private BufferedReader br;
	private List<String> szoKincs;
	private String jatekosNev;
    private Scanner sc = new Scanner(System.in);
	
	private boolean helyes(String kapottSzo, String kovSzo){
		if (! kovSzo.matches("[a-zA-Z]+")){
			return false;
		}
		//if (kovSzo.length()==0||kapottSzo.length()==0){
		//	return false;
		//}
		if (kovSzo.charAt(0)==kapottSzo.charAt(kapottSzo.length()-1)){
			for (int i=0;i<szoKincs.size();i++){
				if (szoKincs.get(i).equals(kovSzo)){
					return false;
				}
				return true;
			}
		}
		return false;
	}
	
	private void kuld(String szo){
		pw.println(szo);
		szoKincs.add(szo);
	}
	
    InteraktivKliens(){ 
		szoKincs=new LinkedList<>();
		String host = "localhost";
		int port = 32123;
		System.out.print("Kerek egy nevet: ");
		String jatekosNev = sc.nextLine();
        try {
            Socket client = new Socket(host, port);
            pw = new PrintWriter(client.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            pw.println(jatekosNev);
			String answer = br.readLine();
			if (answer.equals("start")){
				System.out.print("Kerek egy szot: ");
				String kovSzo = sc.nextLine();
				kuld(kovSzo);
				answer=br.readLine();
			}
			
			while((! "exit".equals(answer)) && null != answer){
				if (! answer.equals("nyert")){
					//Debug
					System.out.println("DEBUG_answer: "+ answer);
					//
					String kuldoNev = answer.split(" ")[0];
					String kapottSzo = answer.substring(kuldoNev.length()+1); //space utani karakter
					System.out.println(kuldoNev+": "+kapottSzo);
					String kovSzo;
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
					pw.println(kovSzo);
					answer=br.readLine();
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
