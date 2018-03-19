package com.logistica.bean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.inn.logistica.dao.AnagraficaDao;
import com.inn.logistica.dao.ArticoloDao;
import com.inn.logistica.dao.MovimentazioniArticoliDao;
import com.inn.logistica.dao.MovimentazioniDao;
import com.inn.logistica.dao.UtenteDao;
import com.inn.logistica.dto.Anagrafica;
import com.inn.logistica.dto.Articolo;
import com.inn.logistica.dto.Movimentazioni;
import com.inn.logistica.dto.MovimentazioniArticoli;
import com.inn.logistica.dto.Utente;
import com.inn.logistica.dto.UtentePk;
import com.inn.logistica.factory.AnagraficaDaoFactory;
import com.inn.logistica.factory.ArticoloDaoFactory;
import com.inn.logistica.factory.MovimentazioniArticoliDaoFactory;
import com.inn.logistica.factory.MovimentazioniDaoFactory;
import com.inn.logistica.factory.UtenteDaoFactory;
import com.inn.logistica.jdbc.ResourceManager;

@ManagedBean(name = "DatiPersonali")
@ViewScoped

public class DatiPersonali extends Pagina {
	
	String oldpassword;
	String newpassword;
	String confpassword;
	
	@ManagedProperty (value="#{sessionBean1}")
    private SessionBean session = null; 	
	
	public DatiPersonali () {
		
	}
	
	@PostConstruct
    public void init() {

			initComponent();		
	}

	public SessionBean getSession() {
		return session;
	}

	public void setSession(SessionBean session) {
		this.session = session;
	}
	
	public String getOldpassword() {
		return oldpassword;
	}

	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String getConfpassword() {
		return confpassword;
	}

	public void setConfpassword(String confpassword) {
		this.confpassword = confpassword;
	}

	private boolean controllaDati() {
		boolean result =true; 
		
		if (oldpassword.length() ==0 ) {
			result = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Inserire la vecchia password",
					   "Attenzione!"));
		} 
		if (newpassword.length()==0) {
			result = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Inserire la nuova password",
					   "Attenzione!"));
		} 
		
		if (confpassword.length()==0) {
			result = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Inserire la conferma della password",
					   "Attenzione!"));
		} 	
		
		if (result && !newpassword.equals(confpassword) ) {
			result = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "La nuova password e la conferma non corrispondono",
					   "Attenzione!"));
		}
		
		return result;
	}
	
	public String buttonSalva(ActionEvent actionEvent){
		
		if (controllaDati() ) {
			
			salvaOnDB();

		}
		
		return null;
		
	}
	
	private void salvaOnDB() {

		int esito = 0;
	
		try {
			
			UtenteDao dao = UtenteDaoFactory.create();
			
			Utente[] dto = dao.findByDynamicWhere("ute_id = " + getSession().getUtente() , null);
			
			for (Utente ut: dto) {
				
				System.out.println(ut.getUtePassword() + "   " + oldpassword);
				
				if (ut.getUtePassword().equals(oldpassword)) {
				
					UtentePk utP = ut.createPk();
					
					ut.setUtePassword(newpassword);
					
					dao.update(utP, ut);
					
					esito = 1;
					
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
			                 "La password inserita non è corretta",
							   "Attenzione!"));
					esito = 2;
				}
			}

			
			
		} catch (Throwable t) {
				t.printStackTrace();
				esito=0;
		}	
				
		if (esito == 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Si è verificato un errore nella registrazione",
					   "Attenzione!"));
		}
		else if (esito == 1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
	                 "Registrazione effettata con successo",
					   "Informazione!"));
			
			initComponent();
		}
		
	}
	
	private void initComponent() {
		
		oldpassword = "";
		newpassword = "";
		confpassword = "";
		
	}	
	

	// Fine Classe
}
