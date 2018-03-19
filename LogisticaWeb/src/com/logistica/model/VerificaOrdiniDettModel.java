package com.logistica.model;

public class VerificaOrdiniDettModel {
	
	long artId;
	String artDescrizione;
	long artQta;

	public VerificaOrdiniDettModel () {
		
		artId=0;
		artDescrizione="";
		artQta=0;
		
	}

	public long getArtId() {
		return artId;
	}

	public void setArtId(long artId) {
		this.artId = artId;
	}

	public String getArtDescrizione() {
		return artDescrizione;
	}

	public void setArtDescrizione(String artDescrizione) {
		this.artDescrizione = artDescrizione;
	}

	public long getArtQta() {
		return artQta;
	}

	public void setArtQta(long artQta) {
		this.artQta = artQta;
	}

}


