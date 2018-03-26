package com.cid.service;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "CxfServiceInterface", targetNamespace = "http://service.cid.com/", wsdlLocation = "http://localhost:8080/CxfService/services/cxfservice?wsdl")
public class Client_Service extends Service{

	

	private final static URL CLIENT_WSDL_LOCATION;
    private final static WebServiceException CLIENT_EXCEPTION;
    private final static QName CLIENT_QNAME = new QName("http://service.cid.com/", "CxfService");
    private final static String wsdl_url = "http://localhost:8080/CxfService/services/cxfservice?wsdl"; 
	
    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL(wsdl_url);
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        CLIENT_WSDL_LOCATION = url;
        CLIENT_EXCEPTION = e;
    }
    
    public Client_Service() {
        super(__getWsdlLocation(), CLIENT_QNAME);
    }

    public Client_Service(WebServiceFeature... features) {
        super(__getWsdlLocation(), CLIENT_QNAME, features);
    }

    public Client_Service(URL wsdlLocation) {
        super(wsdlLocation, CLIENT_QNAME);
    }

    public Client_Service(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, CLIENT_QNAME, features);
    }

    public Client_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public Client_Service(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }
    
    /**
     * 
     * @return
     *     returns Query
     */
    @WebEndpoint(name = "CxfServicePort")
    public CxfServiceInterface getCxfServicePort() {
        return super.getPort(new QName("http://service.cid.com/", "CxfServicePort"), CxfServiceInterface.class);
    }
    
    @WebEndpoint(name = "CxfServicePort")
    public CxfServiceInterface getCxfServicePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://service.cid.com/", "CxfServicePort"), CxfServiceInterface.class, features);
    }
    
    private static URL __getWsdlLocation() {
        if (CLIENT_EXCEPTION!= null) {
            throw CLIENT_EXCEPTION;
        }
        return CLIENT_WSDL_LOCATION;
    }
}
