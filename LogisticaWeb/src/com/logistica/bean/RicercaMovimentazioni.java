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
import com.logistica.model.RicercaMovModel;
import com.inn.logistica.dao.AnagraficaDao;
import com.inn.logistica.dao.ArticoloDao;
import com.inn.logistica.dao.MovimentazioniArticoliDao;
import com.inn.logistica.dao.MovimentazioniDao;
import com.inn.logistica.dto.Anagrafica;
import com.inn.logistica.dto.Articolo;
import com.inn.logistica.dto.Movimentazioni;
import com.inn.logistica.dto.MovimentazioniArticoli;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "RicercaMovimentazioni")
@ViewScoped

public class RicercaMovimentazioni extends Pagina {
	
	long clienteFk;
	long articoloFk;
	
	Date datadal;
	Date dataal;

	String tipo;
	
    private List<SelectItem> combocliente;
    private List<SelectItem> comboarticolo;	
    private List<RicercaMovModel> lstMovimenti;
    
	public RicercaMovimentazioni () {
		 
	}
	
	@PostConstruct
    public void init() {
		
		if ( !FacesContext.getCurrentInstance().isPostback() ) {

			// popolamento combo Clienti
			combocliente = new ArrayList<SelectItem>();				
			combocliente.add(new SelectItem(0," Tutti "));			
					
			try {
				AnagraficaDao dao = AnagraficaDaoFactory.create();
				
				Anagrafica[] dto = dao.findByDynamicWhere(" 1 = 1 order by ana_ragione_sociale", null);
				
				for (Anagrafica cliente : dto) {
					combocliente.add(new SelectItem(cliente.getAnaId() , cliente.getAnaRagioneSociale()));
				}
				
			} catch (Throwable t) {
				t.printStackTrace();
			} 
		
			// popolamento combo Articoli
			comboarticolo = new ArrayList<SelectItem>();				
			comboarticolo.add(new SelectItem(0," Tutti "));			
					
			try {
				ArticoloDao dao = ArticoloDaoFactory.create();
				
				Articolo[] dto = dao.findByDynamicWhere("1 = 1 order by art_descrizione", null);
				
				for (Articolo art : dto) {
					comboarticolo.add(new SelectItem(art.getArtId() , art.getArtDescrizione()));
				}
				
			} catch (Throwable t) {
				t.printStackTrace();
			} 
			
			initComponent();
		}

	}
	
	public long getClienteFk() {
		return clienteFk;
	}

	public void setClienteFk(long clienteFk) {
		this.clienteFk = clienteFk;
	}

	public long getArticoloFk() {
		return articoloFk;
	}

	public void setArticoloFk(long articoloFk) {
		this.articoloFk = articoloFk;
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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public List<SelectItem> getCombocliente() {
		return combocliente;
	}

	public void setCombocliente(List<SelectItem> combocliente) {
		this.combocliente = combocliente;
	}

	public List<SelectItem> getComboarticolo() {
		return comboarticolo;
	}

	public void setComboarticolo(List<SelectItem> comboarticolo) {
		this.comboarticolo = comboarticolo;
	}

	public List<RicercaMovModel> getLstMovimenti() {
		return lstMovimenti;
	}

	public void setLstMovimenti(List<RicercaMovModel> lstMovimenti) {
		this.lstMovimenti = lstMovimenti;
	}

	public String buttonRicerca(ActionEvent actionEvent) {
		
		RicercaMovModel det;
		Connection conn;
		Statement stmt = null;
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		
		String query = "select mov_id, mov_data, mov_ora, mov_tipo, ana_id , mvd_id, art_id, art_codice, mvd_qta, art_descrizione, ana_ragione_sociale " + 
					   "from movimentazioni inner join movimentazioni_articoli on movimentazioni_mov_id = mov_id " + 
					   "inner join articolo on art_id = articolo_art_id " + 
					   "left join anagrafica on ana_id = anagrafica_ana_id ";
		
		lstMovimenti = new ArrayList<RicercaMovModel>();	
		
		try {
				String sWhere = "";

				if (datadal != null ) {
						sWhere = sWhere + "mov_data >= '" + df.format(datadal) + "' and "; 
				}
				if (dataal != null ) {
					sWhere = sWhere + "mov_data <= '" + df.format(dataal) + "' and "; 
				}
				
				if (clienteFk>0) {
					sWhere = sWhere + "anagrafica_ana_id = " + clienteFk+ " and "; 
				}
				if (articoloFk>0) {
					sWhere = sWhere + "articolo_art_id = " + articoloFk+ " and "; 
				}
				if (!tipo.equals("E")) {
					sWhere = sWhere + "mov_tipo = '" + tipo + "' and "; 
				}
			
				
				if (sWhere.length()> 0){
					
					sWhere = sWhere.substring(0, sWhere.length()-5);
					
					query = query +  " where " + sWhere;
					
				}
				
				conn = ResourceManager.getConnection();
				stmt = conn.createStatement();

				ResultSet rs = stmt.executeQuery(query);
				
		        while (rs.next()) {
		        	
					det = new RicercaMovModel();
					
					det.setAnaid(rs.getInt("ana_id"));
					det.setArtcodice(rs.getString("art_codice"));
					det.setArtdescrizione(rs.getString("art_descrizione"));
					det.setArtid(rs.getInt("art_id"));
					det.setData(rs.getDate("mov_data"));
					det.setDetid(rs.getInt("mvd_id"));
					det.setMovid(rs.getInt("mov_id"));
					if (rs.getString("mov_tipo").equals("C")) {
						det.setMovtipo("Carico");
					}
					else {
						det.setMovtipo("Scarico");
					}
							
					det.setRagionesociale(rs.getString("ana_ragione_sociale"));
					det.setMovqta(rs.getInt("mvd_qta"));
					
					lstMovimenti.add(det);

				}
				
		        				
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
		        if (stmt != null) { try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} }
		    }
			
		return null;
		
	}
	
	private void initComponent() {
		
		lstMovimenti = new ArrayList<RicercaMovModel>();		
		clienteFk = 0;
		articoloFk = 0;
		
		datadal = null;
		dataal = new Date();
		tipo = "E";
	
	}	
	

}
