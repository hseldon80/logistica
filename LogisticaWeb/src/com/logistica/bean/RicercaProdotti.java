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

@ManagedBean(name = "RicercaProdotti")
@ViewScoped

public class RicercaProdotti extends Pagina {
	
	String codArticolo;
    private List<ScaricoDettModel> lstArticoli;
    
	public RicercaProdotti () {
		 
	}
	
	@PostConstruct
    public void init() {
		
		initComponent();

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

	public String buttonRicerca(ActionEvent actionEvent) {
		
		// System.out.println("Ricerco articolo:" + codArticolo);
		lstArticoli = new ArrayList<ScaricoDettModel>();
		ScaricoDettModel det = new ScaricoDettModel();
		
		try {
				ArticoloDao dao = ArticoloDaoFactory.create();
				
				Articolo[] dto = dao.findByDynamicWhere("art_barcode like '%" + codArticolo + "%' or lower(art_descrizione) like '%" + codArticolo.toLowerCase() + "%' order by art_descrizione ", null);
				
				for (Articolo art : dto) {

					det = new ScaricoDettModel();
					
					det.setId(art.getArtId() );
					det.setCodice(art.getArtCodice() );
					det.setDescrizione(art.getArtDescrizione() );
					det.setQta(art.getArtGiacenza() );
					
					lstArticoli.add(det);
				}
				
				
		} catch (Throwable t) {
				t.printStackTrace();
		} 
			
		return null;
	}
	
	private void initComponent() {
		
		lstArticoli = new ArrayList<ScaricoDettModel>();		
		codArticolo = "";
	
	}	
	
}
