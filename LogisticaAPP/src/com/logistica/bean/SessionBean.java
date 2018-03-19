package com.logistica.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "sessionBean1")
@SessionScoped
public class SessionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8648296423980941950L;

	private int utente_business = 0;
	private String nomeStruttura ="Nome Struttura"; 
	private String nomeUtente =""; 
	
	public SessionBean() {
		
	}
	
	@PostConstruct
    private void init()
    { 
		
    }

	public int getUtente_business() {
		return utente_business;
	}

	public void setUtente_business(int utente_business) {
		this.utente_business = utente_business;
	}

	public String getNomeStruttura() {
		return nomeStruttura;
	}

	public void setNomeStruttura(String nomeStruttura) {
		this.nomeStruttura = nomeStruttura;
	}

	public String getNomeUtente() {
		return nomeUtente;
	}

	public void setNomeUtente(String nomeUtente) {
		this.nomeUtente = nomeUtente;
	}

	
	
	
	
	
}
