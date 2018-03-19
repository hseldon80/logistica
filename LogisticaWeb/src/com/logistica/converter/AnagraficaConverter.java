package com.logistica.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.inn.logistica.dao.AnagraficaDao;
import com.inn.logistica.dto.Anagrafica;
import com.inn.logistica.exceptions.AnagraficaDaoException;
import com.inn.logistica.factory.AnagraficaDaoFactory;

@FacesConverter("anagraficaConverter")
public class AnagraficaConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		String convertedValue = getRagioneSociale(value.toString());
		
		return convertedValue;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		
		String convertedValue = getRagioneSociale(value.toString());
		
		return convertedValue;
	}
	
	private String getRagioneSociale(String anaId) {

		String value="";
		
		try {

			Anagrafica dto = null;
			
			int anaPk = Integer.parseInt(anaId);
			
			AnagraficaDao dao = AnagraficaDaoFactory.create();
			
			dto = dao.findByPrimaryKey(anaPk);
			
			System.out.println("DTO:" + dto.toString());
			
			if ( dto != null && anaPk > 0 ) 
				value = dto.getAnaRagioneSociale();
			
			
		} catch (AnagraficaDaoException e) {

			e.printStackTrace();
		}
		
		return value;
	}

}
