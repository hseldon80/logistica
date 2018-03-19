package com.logistica.bean;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
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
import com.inn.logistica.factory.AnagraficaDaoFactory;
import com.inn.logistica.factory.ArticoloDaoFactory;
import com.inn.logistica.factory.MovimentazioniArticoliDaoFactory;
import com.inn.logistica.factory.MovimentazioniDaoFactory;
import com.inn.logistica.factory.UtenteDaoFactory;
import com.inn.logistica.jdbc.ResourceManager;
import com.logistica.utility.RandomString;
import javax.mail.*;
import javax.mail.internet.*;

import org.apache.commons.lang3.RandomStringUtils;

@ManagedBean(name = "RegistrazioneCliente")
@ViewScoped

public class RegistrazioneCliente extends Pagina {
	
	boolean consenso;
	String email;
	String piva;
	
	public RegistrazioneCliente () {
		
	}
	
	@PostConstruct
    public void init() {

			initComponent();		
	}

	public boolean getConsenso() {
		return consenso;
	}

	public void setConsenso(boolean consenso) {
		this.consenso = consenso;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPiva() {
		return piva;
	}

	public void setPiva(String piva) {
		this.piva = piva;
	}

	private boolean controllaDati() {
		boolean result =true; 
		
		if (piva.length() ==0 ) {
			result = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Inserire la partita IVA.",
					   "Attenzione!"));
		} 
		if (email.length()==0) {
			result = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Inserire l'indirizzo email.",
					   "Attenzione!"));
		} 
		if (!consenso) {
			result = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Inserire il consenso.",
					   "Attenzione!"));
		}
		
		return result;
	}
	
	public String buttonSalva(ActionEvent actionEvent){
		
		if (controllaDati() ) {
			
			if ( accountPresente() ) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
		                 "Esiste già un account con i parametri inseriti",
						   "Attenzione!"));
			} else {

				// recupero la ragione sociale del cliente per quella PIVA
				// se è vuota vuol dire che non ci sono clienti e l'account non va creato
				
				String ragSoc = getRagSocCliente();
				if (ragSoc.length()>0) {
					
					salvaOnDB( ragSoc );
					
				}  else {
					
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
			                 "Non esiste un cliente con questa partita IVA",
							   "Attenzione!"));					
				}
			} 
			

		}
		
		return null;
		
	}
	
	private void salvaOnDB(String ragSoc) {

		int esito = 0;
		
		Connection conn = null;
		
		try {
			
			String pwd = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
			
			conn = ResourceManager.getConnection();		
			conn.setAutoCommit(false);
			
			UtenteDao dao =UtenteDaoFactory.create(conn);
			Utente master = new Utente();
			
			master.setUteCfPiva(piva);
			master.setUteEmail(email);
			master.setUteNominativo(ragSoc);
			master.setUtePassword( pwd );
			master.setUteProfilo("C");		// C = Cliente
			master.setUteStato("A");		// A = Attivo
			master.setUteTentativi(0);
			
			dao.insert(master);

			conn.commit();
			
			sendMail ( email, pwd.toString() ) ;
			
			esito = 1;
			
		} catch (Throwable t) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			t.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (conn!=null) ResourceManager.close(conn);
		}	
				
		if (esito == 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Si è verificato un errore nella registrazione",
					   "Attenzione!"));
		}
		else 
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
	                 "Registrazione effettata con successo, a breve riceverà la password al proprio indirizzo email",
					   "Informazione!"));
			
			initComponent();
		}
		
	}
	
	private void initComponent() {
		
		consenso = false;
		piva ="";
		email = "";
		
	}	
	
	private boolean accountPresente() {
		boolean result=false; 
		
		try {
			UtenteDao dao =UtenteDaoFactory.create();

			Utente[] dto = dao.findByDynamicWhere(" lower(ute_email) = '"  + email.toLowerCase() + "' or lower(ute_cf_piva) = '" + piva.toLowerCase() + "'", null);
			
			for (Utente ut: dto) {
				result = true;
			}
			
		} catch (Throwable t) {
			t.printStackTrace();
			
			result = true;
		} 
		
		return result;
	}

	private String getRagSocCliente() {
		String result=""; 
		
		try {
			AnagraficaDao dao = AnagraficaDaoFactory.create();

			Anagrafica[] dto = dao.findByDynamicWhere(" lower(ana_cf_piva) = '"  + piva.toLowerCase() + "' and ana_tipologia in ('C','E') ", null);
			
			for (Anagrafica cliente : dto) {
				result = cliente.getAnaRagioneSociale();
			}
			
		} catch (Throwable t) {
			t.printStackTrace();
		} 
		
		return result;
	}
	
	public void sendMail ( String email, String pwd) {
        String from = "noreplay@cidsoftware.it";
        String host = "mail.cidsoftware.it";
        // Get system properties
        Properties properties = System.getProperties();      
        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.user", "noreplay@cidsoftware.it");
        properties.setProperty("mail.password", "cambiami");
        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);
        try {
		        MimeMessage message = new MimeMessage(session);
		
		        // Set From: header field of the header.
		        message.setFrom(new InternetAddress(from));
		
		        // Set To: header field of the header.
		        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		
		        // Set Subject: header field
		        message.setSubject("Nuova Piattaforma PSODIT – Primo accesso");
		        
		        String messaggio;  
		        String username;
		        String mail;
		
		        messaggio = "Le inviamo la password personale di primo accesso, da utilizzare insieme alla USERNAME fornita in precedenza ";
		        messaggio = messaggio + "<h4>"+ pwd + "</h4>";         
		        messaggio = messaggio + "<br><br>";  
		        
		        // Send the actual HTML message, as big as you like
		        message.setContent( messaggio, "text/html" );
		
		        // Send message
		        Transport.send(message);
		        
        } catch (Exception e ) {
            e.printStackTrace();
        }
		      
	}
}
