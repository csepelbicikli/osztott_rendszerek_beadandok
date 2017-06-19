package szolanc;

import java.rmi.registry.*;
import java.rmi.*;

public class TiltottDeploy {
    
    public static void main(String[] args) throws RemoteException {
        Registry reg = LocateRegistry.createRegistry(8080);
        reg.rebind("tiltott1", new TiltottSzerver("vmi"));
        System.out.println("Egy tiltott szerver elindult.");
    }
}
