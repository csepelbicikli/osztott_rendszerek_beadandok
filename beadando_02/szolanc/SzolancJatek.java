package szolanc;


import java.rmi.server.*;
import java.rmi.*;

public class SzolancJatek{
	public static void main(String[] args){
		Runnable task0 = () -> {       
			try{
				TiltottDeploy.main(new String[]{1+""});
			}catch(Exception e){
				e.printStackTrace();
			}
		};
		new Thread(task0).start();
		try {Thread.sleep(1500);}catch (Exception e){}
		Runnable task1 = () -> {       
			JatekSzerver szerver = new JatekSzerver();
			if (szerver != null) szerver.handleClients();
		};
		new Thread(task1).start();
		try {Thread.sleep(500);}catch (Exception e){}
		Runnable task2 = () -> { new GepiJatekos("Robot","szokincs1.txt");};
		new Thread(task2).start();
		try {Thread.sleep(500);}catch (Exception e){}
		Runnable task3 = () -> { new InteraktivKliens();};
		new Thread(task3).start();
	}
}
