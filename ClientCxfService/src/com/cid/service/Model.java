package com.cid.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "modelloResponse", namespace = "service.cid.com")
public class Model implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3536142933488491186L;
	List<String> lista = new ArrayList<String>();
	
	public Model() {
		// TODO Auto-generated constructor stub
	}
	
	public List<String> getLista() {	
		return lista;
	}

	public void setLista(List<String> lista) {
		this.lista = lista;
	}

	
}
