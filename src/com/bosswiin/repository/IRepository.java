/**
 * This interface is a standard for database operation
 * @author Yu-Hua Tseng
 * @version     0.1
 */
package com.bosswiin.repository;

import java.util.*;

/**
 * IRepository  is a interface for various database operations
 */
public interface IRepository {

    /**
     * According to given initialization string to open database
     * date: 2014/10/22
     * @author Yu-Hua Tseng
     * @param  databaseName  name of database, like: info.db
     * @param  initString any initializaiton string for database opening
     * @return true for successful and false for fail
     */
    boolean OpenDataBase(String databaseName, String initString);

    /**
     * Close database
     * date: 2014/10/22
     * @author Yu-Hua Tseng
     * @param  databaseName  name of database, like: info.db
     * @return true for successful and false for fail
     */
    boolean CloseDataBase(String databaseName);

    /**
     * Insert one tuple into the given table
     * date: 2014/10/22
     * @author Yu-Hua Tseng
     * @param  tableName the name of table
     * @param  data is a HashMap, key means column's name, value means the content of column
     * @return return value will be encapsulate into ArrayList
     */
    ArrayList<String> Insert(String tableName,HashMap<String, Object> data);

    /**
     * Update one tuple into the given table
     * date: 2014/10/22
     * @author Yu-Hua Tseng
     * @param  tableName the name of table
     * @param  data is a HashMap, key means column's name, value means the content of column
     * @return return value will be encapsulate into ArrayList
     */
    ArrayList<String> Update(String tableName,HashMap<String, Object> data);

    /**
     * Delete one tuple from the given table
     * date: 2014/10/22
     * @author Yu-Hua Tseng
     * @param  tableName the name of table
     * @param  data is a HashMap, you can give any data that will be used during deletion
     * @return return value will be encapsulate into ArrayList
     */
    ArrayList<String> Delete(String tableName,HashMap<String, Object> data);

    /**
     * Query data from the given table
     * date: 2014/10/22
     * @author Yu-Hua Tseng
     * @param  tableName  name of table, like: info.db
     * @param  data you can give any data that will be used during query
     * @return return value will be encapsulate into ArrayList
     */
    ArrayList<String> Query(String tableName, HashMap<String, Object> data);

    /**
     * Get all tables, which the opened database has
     * date: 2014/10/22.
     * @author Yu-Hua Tseng
     * @return return value will be encapsulate into ArrayList
     */
    ArrayList<String> GetTableList();
}
