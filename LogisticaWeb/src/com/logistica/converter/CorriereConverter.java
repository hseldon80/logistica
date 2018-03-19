package com.logistica.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.inn.logistica.dao.CorrieriDao;
import com.inn.logistica.dto.Corrieri;
import com.inn.logistica.factory.CorrieriDaoFactory;

@FacesConverter("corriereConverter")
public class CorriereConverter implements Converter {

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

		String value ="";
		
		try {

			Corrieri dto = null;
			
			int anaPk = Integer.parseInt(anaId);
			
			CorrieriDao dao = CorrieriDaoFactory.create();
			
			dto = dao.findByPrimaryKey(anaPk);
			
			if ( dto != null && anaPk > 0 ) 
				value = dto.getCorNominativo();
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		return value;
	}

}
