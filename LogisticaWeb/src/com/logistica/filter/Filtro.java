package com.logistica.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.logistica.bean.SessionBean;



/**
 * Servlet Filter implementation class Filtro
 */
/*@WebFilter("/Filtro")*/
public class Filtro implements Filter {

	private FilterConfig _filterConfig;
	private String PAGE_HOME;
	private String PAGE_LOGIN;
	private String PAGE_NOT_FOUND;
	private String PAGE_UNAUTHORIZED;
	private String SESSION_USERNAME_ATTR;
	private String SESSION_USERDATA_ATTR;
	private String APPLICATION_AUTHDATA_ATTR;
	private boolean PAGE_SKIP_XHTML_REQUESTS;
	private Map<String, List<Model>> autorizzazioniMap;
	private final String AUTORIZZAZIONI_PROPERTIES = "/com/logistica/filter/autorizzazioni.mysecret";
    /**
     * Default constructor. 
     */
    public Filtro() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		_filterConfig = null;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
				HttpServletRequest req =(HttpServletRequest)request;
		        HttpSession session = req.getSession();		        
		        /*FacesContext context = getFacesContext(request,response);
		        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);*/
		        String reqURI = req.getRequestURI();
		        boolean autorizzato = false;
				try {
					if (req.getRequestURI().indexOf("/LogisticaWeb/javax.faces.resource/")!=-1) {
						autorizzato = true;
					} else {			
						SessionBean utente_business = (session == null) ? null : (SessionBean) session.getAttribute(SESSION_USERDATA_ATTR);
						autorizzato = checkAutorizzazione(utente_business, req.getContextPath(), reqURI);
					}
				} catch (Throwable t) {			
					t.printStackTrace();
				}
		        
		        if(request != null && response != null) 
		        { 	
		          if (autorizzato) {  
		        	  chain.doFilter(request, response);
		          } else {
		        	  HttpServletResponse resp =(HttpServletResponse)response;
		              resp.sendRedirect(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/LogisticaWeb/notAuthorized.xhtml");
		          }
		        }
	}

	private boolean checkAutorizzazione(SessionBean utente, String contextPath, String reqURI)
	{
		if (publicPage(contextPath, reqURI))
			return true;

		if (autorizzazioniMap == null || autorizzazioniMap.isEmpty()
				  || utente == null /*|| reqURI == null*/)
		{
			return false;
		}

		// Recupera autorizzazioni
		List<Model> autorizzazioni = null; 
		
		if ( utente !=null )
			autorizzazioni = autorizzazioniMap.get( ""+utente.getRuolo());
		
		if ( autorizzazioni == null || autorizzazioni.isEmpty() )
			return false;
		
		if (reqURI != null)
		{
			reqURI = reqURI.replace(contextPath, "");
		}

		if (reqURI != null && reqURI.startsWith("/faces/"))
		{
			reqURI = reqURI.replaceFirst("/faces/", "/");
		}

		Model authModel = new Model(utente.getRuolo() , reqURI);
		int nPos = Collections.binarySearch(autorizzazioni, authModel);

		return (nPos >= 0);
	}
	
	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		_filterConfig = filterConfig;
		// Recupera URL relativi (TODO: Gestire fail-over, i.e. NVL)
		PAGE_HOME = "/Login.xhtml";
		PAGE_LOGIN = "/Login.xhtml";
		PAGE_NOT_FOUND = "/notFound.xhtml";
		PAGE_UNAUTHORIZED = "/notAuthorized.xhtml";
		SESSION_USERNAME_ATTR = "email";
		SESSION_USERDATA_ATTR = "sessionBean1";	
		APPLICATION_AUTHDATA_ATTR = "";
		PAGE_SKIP_XHTML_REQUESTS = false;
		//
		loadAuthorizationData();
	}

	private void loadAuthorizationData()
	{
		InputStream is = Filtro.class.getResourceAsStream(AUTORIZZAZIONI_PROPERTIES);

		try
		{
			autorizzazioniMap = new Parser().parse(is, false);

			// Memorizza, al livello di contesto, l'elenco delle autorizzazioni
			// Sara' utilizzato anche per la costruzione del menu di navigazione.
			this._filterConfig.getServletContext().setAttribute(APPLICATION_AUTHDATA_ATTR, autorizzazioniMap);
		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
		}
	}
	
	private boolean publicPage(String contextPath, String reqURI)
	{
		return reqURI != null && (reqURI.equals(contextPath)
				  || reqURI.endsWith(PAGE_LOGIN)
				  || reqURI.contains(PAGE_LOGIN + ";jsessionid=")
				  || reqURI.endsWith(PAGE_NOT_FOUND)
				  || reqURI.endsWith(PAGE_UNAUTHORIZED)
				  || reqURI.contains("/public/")
				  || reqURI.contains("/help/")
				  || reqURI.contains("javax.faces.resource")
              || reqURI.endsWith(".kjb")
              || reqURI.endsWith(".ktr")
				  /* Font per Bar Code */
				  || (reqURI.contains("/resources/font/") && 
						(reqURI.endsWith(".woff") || reqURI.endsWith(".eot") || reqURI.endsWith(".svg") || reqURI.endsWith(".ttf") ) ) 
				  );
	}
	
	
	private static abstract class InnerFacesContext extends FacesContext {
        protected static void setFacesContextAsCurrentInstance(FacesContext facesContext) {
            FacesContext.setCurrentInstance(facesContext);
        }
    }
	
	@SuppressWarnings("unused")
	private FacesContext getFacesContext(ServletRequest request, ServletResponse response) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null)
			return facesContext;
	
		FacesContextFactory contextFactory = (FacesContextFactory)FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		LifecycleFactory lifecycleFactory =	(LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		Lifecycle lifecycle =	lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		ServletContext servletContext = ((HttpServletRequest)request).getSession().getServletContext();
		facesContext =	contextFactory.getFacesContext(servletContext, request, response,  lifecycle);
		InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);
		return facesContext; 
	}
	
}
