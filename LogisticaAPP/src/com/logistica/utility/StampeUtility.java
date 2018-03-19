package com.logistica.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

public class StampeUtility implements Serializable { 

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5174139771094336956L;

	public void PDF(String name,HashMap reportParams,Connection conn,String templatePath) throws JRException, IOException{  		   
		   
		   HttpServletResponse httpServletResponse=(HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		   httpServletResponse.addHeader("Content-disposition", "attachment; filename="+name+".pdf");  

		   FacesContext.getCurrentInstance().responseComplete();
		   InputStream template = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(templatePath);
		   
		   JasperPrint jasperPrint = JasperFillManager.fillReport(template, reportParams, conn);
		   ServletOutputStream servletOutputStream=httpServletResponse.getOutputStream();  
		   JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);  		   
		   
		   servletOutputStream.flush();
		   servletOutputStream.close(); 
		   FacesContext.getCurrentInstance().responseComplete();  
	}   
	
}
