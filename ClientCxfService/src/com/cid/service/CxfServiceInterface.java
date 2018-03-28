package com.cid.service;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(name = "CxfServicePort", targetNamespace = "http://service.cid.com/")
public interface CxfServiceInterface {
	@WebMethod
	public String getPirogetto(String progetto);
	@WebMethod
	public Model getLista(String progetto);
	@WebMethod	
	public String getNello(String valore);
}
