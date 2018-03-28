package com.logistica.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser
{

	private final String INCLUDE_KEY = "$INCLUDE";
	
	private final File authFile;
	private boolean resultListSorted;

	public Parser()
	{
		this(null);
	}

	public Parser(File authFile)
	{
		this.authFile = authFile;
		resultListSorted = true;		// Default
	}

	public boolean isResultListSorted()
	{
		return resultListSorted;
	}

	public Parser setResultListSorted(boolean resultListSorted)
	{
		this.resultListSorted = resultListSorted;
		return this;		// consente catena di chiamate
	}

	public Map<String, List<Model>> parse()
	{
		return parse(false);
	}

	public Map<String, List<Model>> parse(boolean parseAsMenu)
	{
		Map<String, List<Model>> authListMap = null;
		InputStream is = null;

		if (authFile != null)
		{
			try
			{
				is = new FileInputStream(authFile);
				authListMap = parse(is, parseAsMenu);
			}
			catch (FileNotFoundException notFoundEx)
			{
				throw new IllegalArgumentException("File specificato non trovato");
			}
			finally
			{
				try
				{
					if (is != null)
					{
						is.close();
					}
				}
				catch (IOException ex)
				{
					ex.printStackTrace();   // TODO: Rimuovere
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("Nessun file specificato");
		}

		return authListMap;
	}

	public Map<String, List<Model>> parse(InputStream authPropertiesStream, boolean parseAsMenu)
	{
		/*- Per ciascun include del file specificato, esegue parseSingle
			e memorizza in mappa con il codice profilo specificato nell'include stessa*/

		Map<String, List<Model>> authListMap = new HashMap<>();
		List<Model> authList;

		BufferedReader bReader;
		String sLine;
		int idxLine = 0;

		try
		{
			bReader = new BufferedReader(new InputStreamReader(authPropertiesStream, "UTF-8"));

			while ((sLine = bReader.readLine()) != null)
			{
				++idxLine;

				// Salta righe di commento
				if (sLine.trim().startsWith("#"))
					continue;

				// Tratta solo INCLUDE
				if ( ! sLine.trim().startsWith(INCLUDE_KEY) )
					continue;
				
				InputStream is = null;
				
				try
				{
					String[] includeLine = sLine.trim().split("=");
					String[] imported = includeLine[1].split(",");
					
					String codProfilo = imported[0].trim();
					String resourceName = imported[1].trim();
					
					if ( codProfilo != null && codProfilo.length() > 0 )
					{
						// Parsing del singolo file
						is = getClass().getResourceAsStream(resourceName);					
						authList = parseSingle(is, parseAsMenu);

						if ( authList != null )
							authListMap.put(codProfilo, authList);
					}
				}
				catch (Exception ex) 
				{
					System.err.println("Skipping invalid line nr." + idxLine + ": \"" + sLine + "\"");
				}
				finally
				{
					try
					{
						if ( is != null )
							is.close();
					}
					catch(Exception ignored) {}
				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();       // TODO: Rimuovere
			throw new IllegalArgumentException("Stream di input specificato non valido");
		}
		finally
		{
			//System.err.println("... done.\n***\n" );
		}

		return authListMap;


	}

	public List<Model> parseSingle(boolean parseAsMenu)
	{
		List<Model> authList = null;
		InputStream is = null;

		if (authFile != null)
		{
			try
			{
				is = new FileInputStream(authFile);
				authList = parseSingle(is, parseAsMenu);
			}
			catch (FileNotFoundException notFoundEx)
			{
				throw new IllegalArgumentException("File specificato non trovato");
			}
			finally
			{
				try
				{
					if (is != null)
					{
						is.close();
					}
				}
				catch (IOException ex)
				{
					ex.printStackTrace();   // TODO: Rimuovere
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("Nessun file specificato");
		}

		return authList;
	}

	public List<Model> parseSingle(InputStream authPropertiesStream, boolean parseAsMenu)
	{
		List<Model> authList = new ArrayList<>();

		BufferedReader bReader;
		String sLine;
		int idxLine = 0;

		try
		{
			//System.err.println("\n***\nParsing page authorization(s) ..." );

			bReader = new BufferedReader(new InputStreamReader(authPropertiesStream, "UTF-8"));

			while ((sLine = bReader.readLine()) != null)
			{
//            if (idxLine==185){
//               idxLine= 185;
//            }
				++idxLine;

				// Salta righe di commento
				if (sLine.trim().startsWith("#"))
				{
					continue;
				}

				String[] ruoloAndUrl = sLine.trim().split("\t");

				try
				{
					Model authModel = new Model(Integer.parseInt(ruoloAndUrl[0]), ruoloAndUrl[1]);
					authList.add(authModel);

					// Verifica se fornita la descrizione
					if (ruoloAndUrl != null && ruoloAndUrl.length > 2)
					{
						authModel.setDescription(ruoloAndUrl[2]);
					}

					if (resultListSorted)
					{
						Collections.sort(authList);
					}
				}
				catch (Exception ex)
				{
					System.err.println("Skipping invalid line nr." + idxLine + ": \"" + sLine + "\"" + ex.getMessage());
				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();       // TODO: Rimuovere
			throw new IllegalArgumentException("Stream di input specificato non valido");
		}
		finally
		{
			//System.err.println("... done.\n***\n" );
		}

		return authList;
	}

}
