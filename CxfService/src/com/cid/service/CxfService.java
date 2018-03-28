package com.cid.service;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService; 

@WebService(endpointInterface = "com.cid.service.CxfServiceInterface", serviceName = "CxfService")
public class CxfService implements CxfServiceInterface {
	
	@WebMethod(operationName = "getPirogetto")
	@WebResult(name="NomeProgetto")
	public String getPirogetto(String progetto) {      
		System.out.print("");
		return "Progetto  "+progetto; 
	}
	
	@WebMethod(operationName = "getLista")
	@WebResult(name="Modello")
	public Model getLista(String progetto) {
		return new Model();
	}

	@WebMethod(operationName = "getNello")
	@WebResult(name="Modello")
	public String getNello(String valore) {
		return "Hello Nello";
	}
	
	
}
