package com.logistica.model;

public class ScaricoDettModel {
	long id;
	String codice;
	String descrizione;
	int qta;
	
	public ScaricoDettModel () {
		id = 0 ;
		codice ="";
		descrizione = "";
		qta = 0;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public int getQta() {
		return qta;
	}

	public void setQta(int qta) {
		this.qta = qta;
	}
	
}
