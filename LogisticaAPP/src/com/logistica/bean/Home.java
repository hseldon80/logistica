package com.logistica.bean;

import java.net.URL;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

@ManagedBean(name = "Home")
@ViewScoped
public class Home extends Pagina {
	
	public Home () {
		
	}
	
	@PostConstruct
    public void init() {
		
	}
	
	
	
}
