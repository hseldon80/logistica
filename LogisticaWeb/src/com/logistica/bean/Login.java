package com.logistica.bean;


import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

@ManagedBean(name = "Login")
@ViewScoped
public class Login extends Pagina {

	

	private String password;
	private String email;
	
	public Login () {
		
	}
	
	@PostConstruct
    public void init() {
		
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String login() {
	   String ritorno = null;
	   
	   if (email==null || "".equalsIgnoreCase(email)) {
		   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                   "Invalid Login!",
				   "Please Try Again!"));
		   return null;
	   }
	   
	   if (password==null || "".equalsIgnoreCase(password)) {
		   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                   "Invalid Password!",
				   "Please Try Again!"));
		   return null;
	   }
	   
	   // TODO: Implementare autorizzazione da DB 
       if (email.equalsIgnoreCase("business") && password.equalsIgnoreCase("business")){
    	   getSession().setEmail("U:EMAIL");
    	   getSession().setPiva("U:PIVA");
    	   getSession().setUtente(1);
    	   getSession().setRuolo(1);
    	   getSession().setRagionesociale("Ragione Sociale: Business");
    	   ritorno = "Home";
   	   
       } else 
    	   if (email.equalsIgnoreCase("cliente") && password.equalsIgnoreCase("cliente")) {
	    	   getSession().setEmail("C:EMAIL");
	    	   getSession().setPiva("C:PIVA");
	    	   getSession().setUtente(1);
	    	   getSession().setRuolo(0);
	    	   getSession().setRagionesociale("Ragione Sociale: Cliente");
	    	   ritorno = "HomeCliente";
	    	   
    	   } else {
    		   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                   "Invalid Password!",
				   "Please Try Again!"));
    		   ritorno = null;
    	   }

       return ritorno; 

	}
	
	public void logout(ActionEvent actionEvent) { 
		getSession().setUtente(-1);
		getSession().setRuolo(-1);
		
        System.out.println("Logout");
    }
     
	
	
	
	
}