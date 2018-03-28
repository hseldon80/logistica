package com.logistica.utility;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Grippo
 */
public class PropertyReader {
    private Properties messages;
    private static PropertyReader pr = null;
    private final Logger log = Logger.getLogger(PropertyReader.class.getName());

    private PropertyReader()
    {
        messages = new Properties();
        try
        {
            messages.load(getClass().getClassLoader().getResourceAsStream("Logistica.properties"));
        }
        catch(IOException e)
        {
            log.log(Level.SEVERE, e.getMessage());
            log.log(Level.SEVERE, "Errore nel caricamento file init.properties");
        }
    }

    public static PropertyReader getInstance()
    {
        if(pr == null)
            synchronized(com.logistica.utility.PropertyReader.class)
            {
                if(pr == null)
                    pr = new PropertyReader();
            }
        return pr;
    }

    public String getProperty(String key)
    {
        String mesg = messages.getProperty(key);
        return mesg;
    }
}