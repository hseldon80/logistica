package com.logistica.bean;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;

import com.inn.logistica.dao.AnagraficaDao;
import com.inn.logistica.dao.ArticoloDao;
import com.inn.logistica.dao.MovimentazioniArticoliDao;
import com.inn.logistica.dao.MovimentazioniDao;
import com.inn.logistica.dto.Anagrafica;
import com.inn.logistica.dto.Articolo;
import com.inn.logistica.dto.Movimentazioni;
import com.inn.logistica.dto.MovimentazioniArticoli;
import com.inn.logistica.factory.AnagraficaDaoFactory;
import com.inn.logistica.factory.ArticoloDaoFactory;
import com.inn.logistica.factory.MovimentazioniArticoliDaoFactory;
import com.inn.logistica.factory.MovimentazioniDaoFactory;
import com.inn.logistica.jdbc.ResourceManager;
import com.logistica.utility.StampeUtility;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

@ManagedBean(name = "Carico")
@ViewScoped

public class Carico extends Pagina {
	
	long clienteFk;
	Date data;
	long articoloFk;
    private List<SelectItem> combocliente;
    private List<SelectItem> comboarticolo;
	int qta;
	
	public Carico () {
		
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
	
			// popolamento combo Articoli
			comboarticolo = new ArrayList<SelectItem>();				
			comboarticolo.add(new SelectItem(0," - "));			
					
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

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public long getArticoloFk() {
		return articoloFk;
	}

	public void setArticoloFk(long articoloFk) {
		this.articoloFk = articoloFk;
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

	public int getQta() {
		return qta;
	}

	public void setQta(int qta) {
		this.qta = qta;
	}

	private boolean controllaDati() {
		boolean result =true; 
		if (articoloFk==0) {
			result = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Selezionare l'articolo da caricare.",
					   "Attenzione!"));
		}
		if (qta==0) {
			result = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Inserire la quantità.",
					   "Attenzione!"));
		} 
		if (data.toString().length() ==0 ) {
			result = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                 "Inserire la data.",
					   "Attenzione!"));
		} 
		
		return result;
	}
	
	public String buttonSalva(ActionEvent actionEvent){
		// System.out.println( "salva " );
		
		if (controllaDati() ) {
			
			salvaOnDB();
			
			stampaEtichette();
			
			return "Home.xhtml";
		} 
		else 
			return null;
	}
	
	private void salvaOnDB() {

		int esito = 0;
		DateFormat df = new SimpleDateFormat("h:mm");
		Connection conn = null;
		
		try {
			conn = ResourceManager.getConnection();		
			conn.setAutoCommit(false);
			
			MovimentazioniDao dao = MovimentazioniDaoFactory.create(conn);
			Movimentazioni master = new Movimentazioni();
			
			if (clienteFk>0) {
				master.setAnagraficaAnaId((int) clienteFk);
			}
		
	        master.setMovData(data);
	        
			master.setMovOra(df.format(new Date()));
			
			master.setMovTipo("C");
		
			dao.insert(master);
			
			MovimentazioniArticoli dett;
			MovimentazioniArticoliDao daoDett = MovimentazioniArticoliDaoFactory.create(conn);
			
			dett = new MovimentazioniArticoli();

			dett.setMovimentazioniMovId(master.getMovId());  
			dett.setArticoloArtId((int) articoloFk);
			dett.setMvdQta(qta);

			daoDett.insert(dett);

			// Aggiornamento giacenza articolo
			ArticoloDao daoArt = ArticoloDaoFactory.create(conn);
			Articolo dtoArt = daoArt.findByPrimaryKey((int)articoloFk);
			dtoArt.setArtGiacenza(dtoArt.getArtGiacenza() + qta);
			daoArt.update(dtoArt.createPk() , dtoArt);
						
			conn.commit();
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
	                 "Si è verificato un errore nella registrazione della movimentazione",
					   "Attenzione!"));
		}
		else 
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
	                 "Registrazione effettata con successo",
					   "Informazione!"));
			
			initComponent();
		}
		
	}
	
	private void initComponent() {
		
		clienteFk = 0;
		articoloFk = 0;
		data = new Date();
		qta = 1;
		
	}	
	
	private void stampaEtichette() {
	
		Connection conn;
		try {
			conn = ResourceManager.getConnection();
			
			HashMap<String,Object> reportParams = new HashMap<String,Object>();
			// String templatePath = "/Report/BarCode.jasper";  
			String templatePath = "/TempFolder/RepositoryGIT/logistica/LogisticaAPP/WebContent/Report/BarCode.jasper";
			
			reportParams.put("artId", articoloFk );
			
			JasperPrint jp;

			jp = JasperFillManager.fillReport(templatePath, reportParams, conn);
		   	
			PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
			
		    // printRequestAttributeSet.add(MediaSizeName.ISO_A4); //setting page size
		    printRequestAttributeSet.add(new Copies(qta));

		    PrinterName printerName = new PrinterName("CID Samsung Corridoio", null); //gets printer 

		    PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		    printServiceAttributeSet.add(printerName);

		    JRPrintServiceExporter exporter = new JRPrintServiceExporter();

		    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
		    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
		    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
		    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
		    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
		    exporter.exportReport();
			
			//String printFileName = null;
			// printFileName = JasperFillManager.fillReportToFile(templatePath, reportParams, conn);
			//JasperPrintManager.printReport( printFileName, true);


		} catch (Throwable t){
			t.printStackTrace(); 
		}
	}		
	
	public String buttonStampa(ActionEvent actionEvent) {
		
		stampaEtichette();
		
		return null;
	}	
	

	// Fine classe
}
