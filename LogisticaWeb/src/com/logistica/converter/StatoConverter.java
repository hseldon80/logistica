package com.logistica.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("statoConverter")
public class StatoConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		String convertedValue = getStato(value.toString());
		
		return convertedValue;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		
		String convertedValue = getStato(value.toString());
		
		return convertedValue;
	}
	
	private String getStato(String sta) {

		String value ="";
		
		 switch (sta) {
         case "A":  
        	 value = "Attesa";
        	 break;
         case "C":  
 	 		value = "Consegnato";
 	 		break;

         case "S":  
 	 		value = "Spedito";
 	 		break;
         
         case "T":  
 	 		value = "Transito";
 	 		break;
		 }	
		
		return value;
	}

}
