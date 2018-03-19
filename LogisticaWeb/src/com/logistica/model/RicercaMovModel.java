package com.logistica.model;

import java.util.Date;

public class RicercaMovModel {
	
	int movid;
	int detid; 
	Date data;
	int anaid;
	String ragionesociale;
	int artid;
	String artcodice;
	String artdescrizione;
	String movtipo;
	int movqta;
	
	public RicercaMovModel () {
		
		movid =0;
		detid=0; 
		data=null;
		anaid=0;
		ragionesociale="";
		artid=0;
		artcodice="";
		artdescrizione="";
		movtipo="";

	}

	public int getMovid() {
		return movid;
	}

	public void setMovid(int movid) {
		this.movid = movid;
	}

	public int getDetid() {
		return detid;
	}

	public void setDetid(int detid) {
		this.detid = detid;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getAnaid() {
		return anaid;
	}

	public void setAnaid(int anaid) {
		this.anaid = anaid;
	}

	public String getRagionesociale() {
		return ragionesociale;
	}

	public void setRagionesociale(String ragionesociale) {
		this.ragionesociale = ragionesociale;
	}

	public int getArtid() {
		return artid;
	}

	public void setArtid(int artid) {
		this.artid = artid;
	}

	public String getArtcodice() {
		return artcodice;
	}

	public void setArtcodice(String artcodice) {
		this.artcodice = artcodice;
	}

	public String getArtdescrizione() {
		return artdescrizione;
	}

	public void setArtdescrizione(String artdescrizione) {
		this.artdescrizione = artdescrizione;
	}

	public String getMovtipo() {
		return movtipo;
	}

	public void setMovtipo(String movtipo) {
		this.movtipo = movtipo;
	}

	public int getMovqta() {
		return movqta;
	}

	public void setMovqta(int movqta) {
		this.movqta = movqta;
	}
	
}


