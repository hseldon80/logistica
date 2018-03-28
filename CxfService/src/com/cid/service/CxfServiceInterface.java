package com.cid.service;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface CxfServiceInterface {

	@WebMethod
	public String getPirogetto(String progetto);
	@WebMethod
	public Model getLista(String progetto);
	@WebMethod(operationName = "getNello")	
	public String getNello(String valore);
}
