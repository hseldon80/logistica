package com.logistica.bean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.context.RequestContext;

import com.inn.logistica.factory.AnagraficaDaoFactory;
import com.inn.logistica.factory.CorrieriDaoFactory;
import com.inn.logistica.factory.OrdiniDaoFactory;
import com.inn.logistica.jdbc.ResourceManager;
import com.logistica.model.RicercaMovModel;
import com.logistica.model.VerificaOrdiniDettModel;
import com.logistica.model.VerificaOrdiniModel;
import com.inn.logistica.dao.AnagraficaDao;
import com.inn.logistica.dao.CorrieriDao;
import com.inn.logistica.dao.OrdiniDao;
import com.inn.logistica.dto.Anagrafica;
import com.inn.logistica.dto.Corrieri;
import com.inn.logistica.dto.Ordini;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "VerificaOrdini")
@ViewScoped
public class VerificaOrdini extends Pagina {
	
	private long clienteFk;
	private String periodo;
	private String stato;
    private List<VerificaOrdiniModel> lstOrdini;
    
	@ManagedProperty (value="#{sessionBean1}")
    private SessionBean session = null; 	
    
	public VerificaOrdini () {
		 
	}
	
	@PostConstruct
    public void init() {
		
		initComponent();

	}
	
	public List<VerificaOrdiniModel> getLstOrdini() {
		return lstOrdini;
	}

	public void setLstOrdini(List<VerificaOrdiniModel> lstOrdini) {
		this.lstOrdini = lstOrdini;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String buttonRicerca(ActionEvent actionEvent) {
		
		RicercaMovModel det;
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		
		DateFormat dfAnno = new SimpleDateFormat("yyyy");
		DateFormat dfMese = new SimpleDateFormat("MM");
		
		Connection conn;
		Statement stmt = null;
		
		lstOrdini = new ArrayList<VerificaOrdiniModel>();	
		
		try {
				conn = ResourceManager.getConnection();
				stmt = conn.createStatement();
				
				OrdiniDao dao = OrdiniDaoFactory.create();
				
				String sWhere = "anagrafica_ana_id = " + clienteFk + " ";
			
				Date datadal=null;
				Date dataal=null;
				Date oggi=new Date();

				Calendar cal = Calendar.getInstance();
				cal.setTime(oggi);
				int year = cal.get(Calendar.YEAR);
				int month = cal.get(Calendar.MONTH)+1;
				
				if (!periodo.equals("T")) {
						if (periodo.equals("AC")) {
							datadal= df.parse( year + "/01/01");
							dataal = oggi;
						}
						if (periodo.equals("MC")) {
							datadal= df.parse(year + "/" + month + "/01");
							dataal = oggi;
						}
						if (periodo.equals("MP")) {
							
							dataal = DateUtils.addDays( df.parse(year + "/" + month + "/01"), -1);

							cal.setTime(DateUtils.addMonths(oggi, -1));
							year = cal.get(Calendar.YEAR);
							month = cal.get(Calendar.MONTH)+1;
							
							datadal = df.parse(year + "/" + month + "/01");
						}
						
						sWhere = sWhere + "and ord_data between '" + df.format(datadal) + "' and '" + df.format(dataal) + "' "; 
				}
			
				if (!stato.equals("")) {
					sWhere = sWhere + "and ord_stato = '" + stato + "' "; 
				}

				Ordini[] dto = dao.findByDynamicWhere(sWhere, null);
				
				VerificaOrdiniModel  md; 
				
				CorrieriDao corDao = CorrieriDaoFactory.create();
				
				for (Ordini ordine : dto) {
					
					md = new VerificaOrdiniModel();
					
					md.setCorId(ordine.getCorrieriCorId());
					
					Corrieri corDto = corDao.findByPrimaryKey(ordine.getCorrieriCorId());
							
					md.setCorNominativo(corDto.getCorNominativo()); 
					md.setCorAbilitato(corDto.getCorAbilTracking());
					
					String urlCorriere = corDto.getCorUrlTracking();
					
					urlCorriere = urlCorriere.replaceAll("#PNR#", ordine.getOrdSpeNumero() );
					
					md.setCorUrl(urlCorriere); 
					
					md.setOrdConsegnato(ordine.getOrdConsegnato());
					md.setOrdData(ordine.getOrdData());
					md.setOrdId(ordine.getOrdId());
					md.setOrdSpeData(ordine.getOrdSpedData());
					md.setOrdSpeNumero(ordine.getOrdSpeNumero());
					md.setOrdStato(ordine.getOrdStato()); 

					// Inserimento dell'elenco degli articoli nell'ordine
				
					String query = "select art_id, art_descrizione, ode_qta " + 
							   "from ordini_articoli inner join articolo on art_id = articolo_art_id " + 
							   "where ordini_ord_id = " + ordine.getOrdId() ;
					
					ResultSet rs = stmt.executeQuery(query);
					
					List<VerificaOrdiniDettModel> articoli = new ArrayList<VerificaOrdiniDettModel>();
					VerificaOrdiniDettModel dem;	

			        while (rs.next()) {
			        	

						dem = new VerificaOrdiniDettModel();
						
						dem.setArtId(rs.getInt("art_id"));
						dem.setArtDescrizione(rs.getString("art_descrizione"));
						dem.setArtQta(rs.getInt("ode_qta"));
						
						articoli.add(dem);
						
			        }

					md.setArticoli(articoli);
					
					
					lstOrdini.add(md);

				}
				
		        				
			} catch (Throwable t) {
				t.printStackTrace();
		    } finally {
		        if (stmt != null) { 
		        		try {
		        				stmt.close();
		        			} catch (SQLException e) {
		        					e.printStackTrace();
		        				} 
		        }
		    }
			
		return null;
		
	}
	
	private void initComponent() {
		
		lstOrdini = new ArrayList<VerificaOrdiniModel>();		
		
		// TODO: clienteFk = session.getUtente();
		clienteFk = 1;
				
		periodo = "T";
		stato = "";
	}	
	
	public String buttonSalvaDettaglio(VerificaOrdiniModel ordine) {	
		int esito  = 0;
		Date dtCons;
		try {			
			
				OrdiniDao ordDao = OrdiniDaoFactory.create();
				
				Ordini ordDto = ordDao.findByPrimaryKey(ordine.getOrdId());
				
				dtCons = new Date();
				
				ordDto.setOrdConsegnato(dtCons);
				ordDto.setOrdStato("C");
							
				ordDao.update(ordDto.createPk() , ordDto );

				int  bkm = lstOrdini.indexOf(ordine);
				ordine.setOrdStato("C");
				ordine.setOrdConsegnato(dtCons);

				lstOrdini.set(bkm, ordine);
			
			esito = 1;
			
		} catch (Throwable t) { 
			t.printStackTrace();
		}
				
		if (esito != 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
	                 "Grazie per aver utilizzato il nostro servizio",
					   "Informazione!"));
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
