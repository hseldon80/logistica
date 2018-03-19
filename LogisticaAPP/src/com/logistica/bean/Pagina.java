package com.logistica.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedProperty;

public class Pagina implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3862381860873301937L;
	@ManagedProperty (value="#{sessionBean1}")
	
    private SessionBean session = null; 
	private String hidden = "";
	
	public Pagina() {
		
	}

	public SessionBean getSession() {
		return session;
	}

	public void setSession(SessionBean session) {
		this.session = session;
	}
	
	public String getHidden() {
		return hidden;
	}

	public void setHidden(String hidden) {
		this.hidden = hidden;
	}
	
	
}
