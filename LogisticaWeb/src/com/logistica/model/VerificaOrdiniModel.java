package com.logistica.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VerificaOrdiniModel {
	
	private long ordId;
	private Date ordData;
	private String ordStato;
	private long corId;
	private String corNominativo;
	private String corUrl;
	private int corAbilitato;
	private Date ordConsegnato;
	private String ordSpeNumero;
	private Date ordSpeData;
	private List<VerificaOrdiniDettModel> articoli;
	
	public VerificaOrdiniModel () {
		
		ordId =0;
		ordData=null;
		corId=0;
		corNominativo="";
		corUrl="";
		ordConsegnato=null;
		ordSpeNumero="";
		ordSpeData=null;
		articoli = new ArrayList<VerificaOrdiniDettModel>();	
		corAbilitato=0;

	}

	
	public int getCorAbilitato() {
		return corAbilitato;
	}

	public void setCorAbilitato(int corAbilitato) {
		this.corAbilitato = corAbilitato;
	}


	public long getOrdId() {
		return ordId;
	}

	public void setOrdId(long ordId) {
		this.ordId = ordId;
	}

	public Date getOrdData() {
		return ordData;
	}

	public void setOrdData(Date ordData) {
		this.ordData = ordData;
	}

	public String getOrdStato() {
		return ordStato;
	}

	public void setOrdStato(String ordStato) {
		this.ordStato = ordStato;
	}

	public long getCorId() {
		return corId;
	}

	public void setCorId(long corId) {
		this.corId = corId;
	}

	public String getCorNominativo() {
		return corNominativo;
	}

	public void setCorNominativo(String corNominativo) {
		this.corNominativo = corNominativo;
	}

	public String getCorUrl() {
		return corUrl;
	}

	public void setCorUrl(String corUrl) {
		this.corUrl = corUrl;
	}

	public Date getOrdConsegnato() {
		return ordConsegnato;
	}

	public void setOrdConsegnato(Date ordConsegnato) {
		this.ordConsegnato = ordConsegnato;
	}

	public String getOrdSpeNumero() {
		return ordSpeNumero;
	}

	public void setOrdSpeNumero(String ordSpeNumero) {
		this.ordSpeNumero = ordSpeNumero;
	}

	public Date getOrdSpeData() {
		return ordSpeData;
	}

	public void setOrdSpeData(Date ordSpeData) {
		this.ordSpeData = ordSpeData;
	}

	public List<VerificaOrdiniDettModel> getArticoli() {
		return articoli;
	}

	public void setArticoli(List<VerificaOrdiniDettModel> articoli) {
		this.articoli = articoli;
	}

	
}


