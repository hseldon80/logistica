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

	private int utente = 0;
	private String ragionesociale ="Ragione Sociale 1"; 
	private String piva ="PIVA";
	private String email ="email@email.it";
	
	public SessionBean() {
		
	}
	
	@PostConstruct
    private void init()
    { 
		
    }

	public int getUtente() {
		return utente;
	}

	public void setUtente(int utente) {
		this.utente = utente;
	}

	public String getRagionesociale() {
		return ragionesociale;
	}

	public void setRagionesociale(String ragionesociale) {
		this.ragionesociale = ragionesociale;
	}

	public String getPiva() {
		return piva;
	}

	public void setPiva(String piva) {
		this.piva = piva;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}



	// fine classe
	
}
