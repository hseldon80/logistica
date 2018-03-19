package com.logistica.bean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.inn.logistica.factory.AnagraficaDaoFactory;
import com.inn.logistica.factory.ArticoloDaoFactory;
import com.inn.logistica.factory.MovimentazioniArticoliDaoFactory;
import com.inn.logistica.factory.MovimentazioniDaoFactory;
import com.inn.logistica.jdbc.ResourceManager;
import com.logistica.model.ScaricoDettModel;
import com.inn.logistica.dao.AnagraficaDao;
import com.inn.logistica.dao.ArticoloDao;
import com.inn.logistica.dao.MovimentazioniArticoliDao;
import com.inn.logistica.dao.MovimentazioniDao;
import com.inn.logistica.dto.Anagrafica;
import com.inn.logistica.dto.Articolo;
import com.inn.logistica.dto.Movimentazioni;
import com.inn.logistica.dto.MovimentazioniArticoli;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "Scarico")
@ViewScoped

public class Scarico extends Pagina {
	
	long clienteFk;
	Date data;
	String codArticolo;
    private List<ScaricoDettModel> lstArticoli;
    
    private List<SelectItem> combocliente;
	
	public Scarico () {
		 
	}
	
	@PostConstruct
    public void init() {
		
		if ( !FacesContext.getCurrentInstance().isPostback() ) {

			// popolamento combo Clienti
			combocliente = new ArrayList<SelectItem>();				
			combocliente.add(new SelectItem(0," - "));			
					
			try {
				AnagraficaDao dao = AnagraficaDaoFactory.create();
				
				Anagrafica[] dto = dao.findByDynamicWhere("ana_tipologia in ('C','E') order by ana_ragione_sociale", null);
				
				for (Anagrafica cliente : dto) {
					combocliente.add(new SelectItem(cliente.getAnaId() , cliente.getAnaRagioneSociale()));
				}
				
			} catch (Throwable t) {
				t.printStackTrace();
			} 
		
			initComponent();
		}

	}
	
	public List<ScaricoDettModel> getLstArticoli() {
		return lstArticoli;
	}

	public void setLstArticoli(List<ScaricoDettModel> lstArticoli) {
		this.lstArticoli = lstArticoli;
	}

	public String getCodArticolo() {
		return codArticolo;
	}

	public void setCodArticolo(String codArticolo) {
		this.codArticolo = codArticolo;
	}

	public long getClienteFk() {
		return clienteFk;
	}

	public void setClienteFk(long clienteFk) {
		this.clienteFk = clienteFk;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public List<SelectItem> getCombocliente() {
		return combocliente;
	}

	public void setCombocliente(List<SelectItem> combocliente) {
		this.combocliente = combocliente;
	}

	private boolean controllaDati() {
		boolean esito = true;
		
		/* if (data.length()==0) {
			esito = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Inserire la data.",
					   "Attenzione!"));
		}*/
		
		if (lstArticoli.isEmpty()) {
			esito = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Inserire almeno un articolo ",
					   "Attenzione!"));
		}
		
		return esito;
		
	}
	
	public String buttonSalva(ActionEvent actionEvent){
		System.out.println( "salva " );
		
		if (controllaDati() ) {
			
			salvaOnDB();
			
			return "Home.xhtml";
		} 
		else 
			return null;
	}
	
	private void salvaOnDB() {
		// TODO : Da implementare
	
		int esito = 0;
		
		Connection conn = null;
		
		try {
			conn = ResourceManager.getConnection();		
			conn.setAutoCommit(false);
			
			MovimentazioniDao dao = MovimentazioniDaoFactory.create(conn);
			Movimentazioni master = new Movimentazioni();
			
			ArticoloDao daoArt = ArticoloDaoFactory.create(conn);
		
			if (clienteFk>0) {
				System.out.println("Cliente:" + clienteFk);
				master.setAnagraficaAnaId((int) clienteFk);
			}
		
	        master.setMovData(data);
			master.setMovOra("00:00");
			master.setMovTipo("S");
		
			dao.insert(master);
			
			MovimentazioniArticoli dett;
			MovimentazioniArticoliDao daoDett = MovimentazioniArticoliDaoFactory.create(conn);
			
			ScaricoDettModel sdm=null;
			
			for (int i=0; i<lstArticoli.size();i++) {
				sdm = lstArticoli.get(i);
				
				dett = new MovimentazioniArticoli();

				// dett.setMvdId(mvdId);		//IdPK
				dett.setMovimentazioniMovId(master.getMovId()); // IDMaster

				dett.setArticoloArtId(sdm.getId());
				dett.setMvdQta(sdm.getQta());
				
				daoDett.insert(dett);
				
				// Aggiornamento giacenza articolo
				Articolo dtoArt = daoArt.findByPrimaryKey((int)sdm.getId());
				dtoArt.setArtGiacenza(dtoArt.getArtGiacenza() + sdm.getQta());
				daoArt.update(dtoArt.createPk() , dtoArt);
				
			}
			conn.commit();
			esito = 1;
			
		} catch (Throwable t) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (conn!=null) ResourceManager.close(conn);
		}	
		
		if (esito == 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Si è verificato un errore nella registrazione della movimentazione",
					   "Attenzione!"));
		}
		else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
	                 "Registrazione effettata con successo",
					   "Informazione!"));
			initComponent();
		}
		
	}
	
	public void testEnter() {
		
		// System.out.println("Ricerco articolo:" + codArticolo);
		
		ScaricoDettModel det = new ScaricoDettModel();
		boolean trovato = false;
		
		for (int i=0; i<lstArticoli.size();i++) {
			det = lstArticoli.get(i);
			
			if ( det.getCodice().equals(codArticolo)) {
				det.setQta( det.getQta() + 1 );
				lstArticoli.set(i, det);
				trovato = true;
			}
		}
		
		if (!trovato) {
			
			trovato = false;
			try {
				ArticoloDao dao = ArticoloDaoFactory.create();
				
				Articolo[] dto = dao.findByDynamicWhere("art_barcode = '" + codArticolo + "' ", null);
				
				for (Articolo art : dto) {

					det = new ScaricoDettModel();
					
					det.setId(art.getArtId());
					det.setCodice(codArticolo);
					det.setDescrizione(art.getArtDescrizione());
					det.setQta(1);
					
					lstArticoli.add(det);
					trovato = true;
					break;
				}
				
				
			} catch (Throwable t) {
				t.printStackTrace();
			} 
			
			if (!trovato) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
		                 "Articolo " + codArticolo + " non trovato.",
						   "Attenzione!"));
			}
			
		}

		codArticolo ="";		
	
	}
	
	private void initComponent() {
		
		lstArticoli = new ArrayList<ScaricoDettModel>();		
		clienteFk = 0;
		codArticolo = "";
		
		// Data Odierna
		data = new Date();
	
	}	
	
	public String buttonDelete(ScaricoDettModel selected) {       
		
		ScaricoDettModel det=null;
		for (int i=0; i<lstArticoli.size();i++) {
			
			det = lstArticoli.get(i);
			if ( det.getCodice().equals(selected.getCodice()) ) {
				lstArticoli.remove(i);
			}
		}

		return null;
    }

	public String buttonQta(ScaricoDettModel selected, String act) {       
		int lQta = selected.getQta();

		// se sottrae verifico che non vada a 0
		if ( (act.equals("S") && selected.getQta() > 1) ) {
			lQta--;
		}
			
		if (act.equals("A")) lQta++;
		
		// effettuo la modifica solo se è cambiata la quantità
		if (lQta != selected.getQta() ) {
			
			ScaricoDettModel det=null;
			
			for (int i=0; i<lstArticoli.size();i++) {
			    det = lstArticoli.get(i);
				if ( det.getCodice().equals(selected.getCodice()) ) {
					det.setQta(lQta);
					lstArticoli.set(i, det);
				}
			}
		}

		return null;
    }

}
