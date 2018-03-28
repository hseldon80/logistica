package com.logistica.filter;

import java.io.Serializable;
import java.util.Objects;

public class Model implements Comparable<Model>, Serializable
{
    private final int ruolo;
    private final String pageRelativeUrl;
	private String description;

	public Model(int ruolo, String pageRelativeUrl)
	{
		this(ruolo, pageRelativeUrl, null);
	}
			
    public Model(int ruolo, String pageRelativeUrl, String menuLabel)
    {
        this.ruolo = ruolo;
        this.pageRelativeUrl = pageRelativeUrl;
		this.description = menuLabel;
    }

    public int getRuolo()
    {
        return ruolo;
    }

    public String getPageRelativeUrl()
    {
        return pageRelativeUrl;
    }

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

    // Le ricerche avverrano per pagina
    @Override
    public int compareTo(Model o)
    {
        int cmp = (this.pageRelativeUrl == null) ?
                ( o.getPageRelativeUrl() == null ? 0 : 1 ) :
                ( o.getPageRelativeUrl() == null ? 1 : this.pageRelativeUrl.compareTo(o.getPageRelativeUrl()) );
        
        if ( cmp == 0 )
        {
            cmp = (this.getRuolo() == o.getRuolo()) ? 0 :
                  ((this.getRuolo() > o.getRuolo()) ? 1 : -1);
        }
        
        return cmp;
    }

	@Override
	public boolean equals(Object obj)
	{
		return compareTo( (Model) obj) == 0;
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 71 * hash + this.ruolo;
		hash = 71 * hash + Objects.hashCode(this.pageRelativeUrl);
		hash = 71 * hash + Objects.hashCode(this.description);
		return hash;
	}

}
