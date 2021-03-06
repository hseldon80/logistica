/*
 * This source file was generated by FireStorm/DAO.
 * 
 * If you purchase a full license for FireStorm/DAO you can customize this header file.
 * 
 * For more information please visit http://www.codefutures.com/products/firestorm
 */

package com.inn.logistica.factory;

import java.sql.Connection;
import com.inn.logistica.dao.*;
import com.inn.logistica.jdbc.*;

public class ArticoloDaoFactory
{
	/**
	 * Method 'create'
	 * 
	 * @return ArticoloDao
	 */
	public static ArticoloDao create()
	{
		return new ArticoloDaoImpl();
	}

	/**
	 * Method 'create'
	 * 
	 * @param conn
	 * @return ArticoloDao
	 */
	public static ArticoloDao create(Connection conn)
	{
		return new ArticoloDaoImpl( conn );
	}

}
