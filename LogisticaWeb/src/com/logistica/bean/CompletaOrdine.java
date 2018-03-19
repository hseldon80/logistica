package com.logistica.bean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;

import com.inn.logistica.factory.AnagraficaDaoFactory;
import com.inn.logistica.factory.CorrieriDaoFactory;
import com.inn.logistica.factory.OrdiniDaoFactory;
import com.logistica.model.RicercaMovModel;
import com.inn.logistica.dao.AnagraficaDao;
import com.inn.logistica.dao.CorrieriDao;
import com.inn.logistica.dao.OrdiniDao;
import com.inn.logistica.dto.Anagrafica;
import com.inn.logistica.dto.Corrieri;
import com.inn.logistica.dto.Ordini;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "CompletaOrdine")
@ViewScoped
public class CompletaOrdine extends Pagina {
	
	long clienteFk;
	long corriereFk;
	
	Date datadal;
	Date dataal;

	String stato;
	
    private List<Ordini> lstOrdini;

    private Ordini ordineSelezionato; 

    private List<SelectItem> combocliente;
    private List<SelectItem> combocorriere;
    
	public CompletaOrdine () {
		 
	}
	
	@PostConstruct
    public void init() {
		
		if ( !FacesContext.getCurrentInstance().isPostback() ) {

			// popolamento combo Clienti
			combocliente = new ArrayList<SelectItem>();				
			combocliente.add(new SelectItem(0," Tutti "));			
					
			try {
				AnagraficaDao dao = AnagraficaDaoFactory.create();
				
				Anagrafica[] dto = dao.findByDynamicWhere("ana_tipologia in ('C','E') order by ana_ragione_sociale", null);
				
				for (Anagrafica cliente : dto) {
					combocliente.add(new SelectItem(cliente.getAnaId() , cliente.getAnaRagioneSociale()));
				}
				
			} catch (Throwable t) {
				t.printStackTrace();
			} 
			
			// popolamento combo Corrieri
			combocorriere = new ArrayList<SelectItem>();	
			combocorriere.add(new SelectItem(0," "));		
					
			try {
				CorrieriDao dao = CorrieriDaoFactory.create();
				
				Corrieri[] dto = dao.findByDynamicWhere("1=1 order by cor_nominativo", null);
				
				for (Corrieri cor : dto) {
					combocorriere.add(new SelectItem(cor.getCorId() , cor.getCorNominativo()));
				}
				
			} catch (Throwable t) {
				t.printStackTrace();
			} 

			initComponent();
		}

	}
	
	public long getCorriereFk() {
		return corriereFk;
	}

	public void setCorriereFk(long corriereFk) {
		this.corriereFk = corriereFk;
	}

	public List<SelectItem> getCombocorriere() {
		return combocorriere;
	}

	public void setCombocorriere(List<SelectItem> combocorriere) {
		this.combocorriere = combocorriere;
	}

	public Ordini getOrdineSelezionato() {
		return ordineSelezionato;
	}

	public void setOrdineSelezionato(Ordini ordineSelezionato) {
		this.ordineSelezionato = ordineSelezionato;
	}

	public List<Ordini> getLstOrdini() {
		return lstOrdini;
	}

	public void setLstOrdini(List<Ordini> lstOrdini) {
		this.lstOrdini = lstOrdini;
	}

	public long getClienteFk() {
		return clienteFk;
	}

	public void setClienteFk(long clienteFk) {
		this.clienteFk = clienteFk;
	}

	public Date getDatadal() {
		return datadal;
	}

	public void setDatadal(Date datadal) {
		this.datadal = datadal;
	}

	public Date getDataal() {
		return dataal;
	}

	public void setDataal(Date dataal) {
		this.dataal = dataal;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public List<SelectItem> getCombocliente() {
		return combocliente;
	}

	public void setCombocliente(List<SelectItem> combocliente) {
		this.combocliente = combocliente;
	}

	public String buttonRicerca(ActionEvent actionEvent) {
		
		RicercaMovModel det;
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		
		lstOrdini = new ArrayList<Ordini>();	
		
		try {
				
				OrdiniDao dao = OrdiniDaoFactory.create();
				
				
				String sWhere = "";
	
				if (clienteFk>0) {
					sWhere = sWhere + "anagrafica_ana_id = " + clienteFk+ " and "; 
				}
				
				if (datadal != null ) {
						sWhere = sWhere + "ord_data >= '" + df.format(datadal) + "' and "; 
				}

				if (dataal != null ) {
					sWhere = sWhere + "ord_data <= '" + df.format(dataal) + "' and "; 
				}

				if (!stato.equals("")) {
					sWhere = sWhere + "ord_stato = '" + stato + "' and "; 
				}

				if (sWhere.length()> 0){
					
					sWhere = sWhere.substring(0, sWhere.length()-5);
				}
				
				Ordini[] dto = dao.findByDynamicWhere(sWhere, null);
				
				for (Ordini ordine : dto) {
					
					lstOrdini.add(ordine);
					
				}
				
		        				
			} catch (Throwable t) {
				t.printStackTrace();
		    }
			
		return null;
		
	}
	
	private void initComponent() {
		
		lstOrdini = new ArrayList<Ordini>();		
		clienteFk = 0;
		
		datadal = null;
		dataal = new Date();
		stato = "";
		ordineSelezionato=null;
		
		corriereFk=0;
	}	
	
	public String buttonDettaglio(Ordini selected) {      
		
		ordineSelezionato = selected;
		corriereFk = ordineSelezionato.getCorrieriCorId();
		
		RequestContext.getCurrentInstance().execute("PF('dlgOrdiniUpdate').show()");
		
		return null;
	}

	public String buttonRifiutaDettaglio() {		

		ordineSelezionato = null;
		corriereFk = 0;
		
		RequestContext.getCurrentInstance().execute("PF('dlgOrdiniUpdate').hide()");	
		return null;
    }
	
	public String buttonSalvaDettaglio() {	
		int esito  = 0;
				
		try {			
			
				OrdiniDao dao = OrdiniDaoFactory.create();
				
				Ordini temp = new Ordini();
	
				if (corriereFk == 0) 
					ordineSelezionato.setCorrieriCorIdNull(true);
				else
					ordineSelezionato.setCorrieriCorId(corriereFk);
			
				/* 
				temp.setAnagraficaAnaId(ordineSelezionato.getAnagraficaAnaId() );
				temp.setOrdConsegnato(ordineSelezionato.getOrdConsegnato() );
				temp.setOrdData(ordineSelezionato.getOrdData() );
				temp.setOrdId(ordineSelezionato.getOrdId() ); 
				temp.setOrdNumero(ordineSelezionato.getOrdNumero() );
				temp.setOrdSpedData(ordineSelezionato.getOrdSpedData() );
				temp.setOrdSpeNumero(ordineSelezionato.getOrdSpeNumero() );
				temp.setOrdStato(ordineSelezionato.getOrdStato());  
				*/
								
				dao.update(ordineSelezionato.createPk() , ordineSelezionato );
			
			esito = 1;
			
		} catch (Throwable t) { 
			t.printStackTrace();
		}
				
		if (esito != 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
	                 "Salvataggio avvenuto con successo",
					   "Informazione!"));
			
			RequestContext.getCurrentInstance().execute("PF('dlgOrdiniUpdate').hide()");				
			return null;
			
		} else {
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Si è verificato un errore",
					   "Attenzione!"));
			
			return null;
		}
				
	}
	
	// Fine Classe
}
