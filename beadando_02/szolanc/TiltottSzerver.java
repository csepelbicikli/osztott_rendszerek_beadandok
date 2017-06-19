package szolanc;

import java.rmi.server.*;
import java.rmi.*;
import java.util.*;

public class TiltottSzerver
        extends UnicastRemoteObject
        implements TiltottInterface {
    
    private String f;

    private List<String> szoKincs;
    
    public TiltottSzerver(String f) throws RemoteException {
		super();		
		szoKincs=new LinkedList<>();        
        this.f=f;
    }
    
    public synchronized boolean tiltottE(String szo) throws RemoteException {
	for(String kszo:szoKincs){
		if(kszo.equals(szo)){return true;}	
	}
		szoKincs.add(szo);	
		return false;
    }
    
}
