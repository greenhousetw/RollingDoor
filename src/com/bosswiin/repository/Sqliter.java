/**
 * This class only focus on SQLite operations
 * @see SQLiteOpenHelper
 * @see IRepository
 * @author Yu-Hua Tseng
 * @since 0.0
 */
package com.bosswiin.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Sqliter follows IRepository to implement defined operations
 */
public class Sqliter extends SQLiteOpenHelper implements IRepository {

    // prefix string for log method
    private final static String         LOGTAGNAME      = "Sqliter";
    // the instance of SQLiteDatabase
    private              SQLiteDatabase database        = null;
    // table name
    private              String         upgradTableName = "";
    // key for update and delete
    private              String         whereIndex      = "Where";

    // runtime enviroment instance
    private Context context = null;

    // version of table
    private int version = 1;

    // variable for database modification
    private ContentValues valueList = new ContentValues();

    // string for table creation
    private String createTableString = "";

    /**
     * Create a helper object to create, open, and/or manage a database. This method always
     * returns very quickly. The database is not actually created or opened until one of
     * getWritableDatabase() or getReadableDatabase() is called.
     * date: 2014/10/23
     *
     * @param context to use to open or create the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                onUpgrade(SQLiteDatabase, int, int) will be used to upgrade the database;
     *                if the database is newer, onDowngrade(SQLiteDatabase, int, int)
     *                will be used to downgrade the database
     * @author Yu-Hua Tseng
     */
    public Sqliter(Context context, String name, CursorFactory factory,
                   int version) {
        super(context, name, factory, version);
        this.context = context;
        this.version = version;
    }

    /**
     * Called when the database is created for the first time. This is where the creation
     * of tables and the initial population of the tables should happen.
     * date: 2014/10/23
     *
     * @param db The database instance
     * @author Yu-Hua Tseng
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableString);
    }

    /**
     * Called when the database needs to be upgraded.
     * The implementation should use this method to drop tables, add tables,
     * or do anything else it needs to upgrade to the new schema version.
     * The SQLite ALTER TABLE documentation can be found here. If you add new columns you can use
     * ALTER TABLE to insert them into a live table. If you rename or remove columns you can use ALTER TABLE
     * to rename the old table, then create the new table and then populate the new table with the contents of
     * the old table.
     * This method executes within a transaction. If an exception is thrown, all changes will automatically be
     * rolled back.
     * date: 2014/10/23
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     * @author Yu-Hua Tseng
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String DROPTABLE = "delete from sqlite_master where type = 'table'";
        db.execSQL(DROPTABLE);
        onCreate(db);
    }

    /**
     * According to given initialization string to open database
     * date: 2014/10/23
     *
     * @param databaseName name of database, like: info.db
     * @param initString   any initializaiton string for database opening
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    @Override
    public boolean OpenDataBase(String databaseName, String initString) {
        boolean result = false;

        Log.v(LOGTAGNAME, "Open database:" + databaseName);

        if (this.database == null || !this.database.isOpen()) {
            this.database = new Sqliter(this.context, databaseName, null, this.version).getWritableDatabase();
            this.createTableString = initString;
            this.onCreate(this.database);
        }

        result = true;

        return result;
    }

    /**
     * Close database
     * date: 2014/10/23
     *
     * @param databaseName name of database, like: info.db
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    @Override
    public synchronized boolean CloseDataBase(String databaseName) {
        boolean result = false;

        if (this.database.isOpen()) {
            super.close();
        }

        result = true;

        return result;
    }

    /**
     * Insert one tuple into the given table
     * date: 2014/10/23
     *
     * @param tableName the name of table
     * @param data      is a HashMap, key means column's name, value means the content of column
     * @return return value will be encapsulate into ArrayList
     * @author Yu-Hua Tseng
     */
    @Override
    public ArrayList<String> Insert(String tableName, HashMap<String, Object> data) {
        ArrayList<String> executionResult = new ArrayList<String>();

        this.GetContentValues(data);

        executionResult.add(Long.toString(this.database.insert(tableName, null, this.valueList)));

        return executionResult;
    }

    /**
     * Update one tuple into the given table
     * date: 2014/10/23
     *
     * @param tableName the name of table
     * @param data      is a HashMap, key means column's name, value means the content of column
     * @return return value will be encapsulate into ArrayList
     * @author Yu-Hua Tseng
     */
    @Override
    public ArrayList<String> Update(String tableName, HashMap<String, Object> data) {
        ArrayList<String> executionResult = new ArrayList<String>();
        executionResult.add(Long.toString(this.ModifyRecord(SqlOperatin.Update, tableName, data)));
        return executionResult;
    }

    /**
     * Delete one tuple from the given table
     * date: 2014/10/23
     *
     * @param tableName the name of table
     * @param data      is a HashMap, you can give any data that will be used during deletion
     * @return return value will be encapsulate into ArrayList
     * @author Yu-Hua Tseng
     */
    @Override
    public ArrayList<String> Delete(String tableName, HashMap<String, Object> data) {
        ArrayList<String> executionResult = new ArrayList<String>();
        executionResult.add(Long.toString(this.ModifyRecord(SqlOperatin.Delete, tableName, data)));
        return executionResult;
    }

    /**
     * Query data from the given table
     * date: 2014/10/23
     *
     * @param tableName name of table, like: info.db
     * @param data      you can give any data that will be used during query
     * @return return value will be encapsulate into ArrayList
     * @author Yu-Hua Tseng
     */
    @Override
    public ArrayList<String> Query(String tableName, HashMap<String, Object> data) {
        ArrayList<String> executionResult = new ArrayList<String>();

        if (this.IsDataBaseOpen()) {
            for (String key : data.keySet()) {
                executionResult.add(data.get(key).toString());
            }

            String[] column = new String[data.size()];
            executionResult.toArray(column);

            try {
                Cursor cursor = this.database.query(tableName, column, null, null, null, null, null);

                executionResult.clear();
                cursor.moveToFirst();
                int columnSize = cursor.getColumnCount();

                while (cursor.moveToNext()) {

                    String tuple = "";

                    for (int i = 0; i < columnSize; i++) {

                        String columnName = cursor.getColumnName(i);
                        String columnValue = cursor.getString(i);
                        tuple = tuple + "\"" + columnName + "\":\"" + columnValue + "\"";
                        if (i != columnSize - 1) {
                            tuple = tuple + ",";
                        }
                    }

                    tuple = "{" + tuple + "}";
                    Log.v(LOGTAGNAME, tuple);
                    executionResult.add(tuple);
                }
            } catch (SQLException sqlEx) {
                Log.e(LOGTAGNAME, sqlEx.getMessage());
            }
        }

        return executionResult;
    }

    /**
     * Get all tables, which the opened database has.
     * date: 2014/10/23
     *
     * @return return value will be encapsulate into ArrayList
     * @author Yu-Hua Tseng
     */
    @Override
    public ArrayList<String> GetTableList() {
        ArrayList<String> tableList = new ArrayList<String>();

        if (this.IsDataBaseOpen()) {
            try {
                String query = "SELECT name FROM sqlite_master WHERE type='table'";
                Cursor dbPointer = this.database.rawQuery(query, null);

                dbPointer.moveToFirst();
                int columnIndex = 0;

                while (dbPointer.moveToNext()) {

                    tableList.add(dbPointer.getString(columnIndex));
                }
            } catch (SQLException ex) {
                Log.e(LOGTAGNAME, ex.getMessage());
            }
        }

        return tableList;
    }

    /**
     * Accoding to input data to modify tuple of one talbe
     * date: 2014/10/23
     *
     * @param operation enumeration value of SqlOperation
     * @param tableName the table that you want to alter
     * @param data      you can give any data that will be used during modification
     * @return return the number of rows affected
     * @author Yu-Hua Tseng
     */
    private synchronized long ModifyRecord(SqlOperatin operation, String tableName, HashMap<String, Object> data) {
        long result = Long.MIN_VALUE;

        if (this.IsDataBaseOpen() && data.containsKey(whereIndex)) {
            String whereString = data.get(whereIndex).toString();
            data.remove(whereIndex);

            if (operation.equals(SqlOperatin.Update)) {
                this.GetContentValues(data);
                result = this.database.update(tableName, this.valueList, whereString, null);
            }
            else if (operation.equals(SqlOperatin.Delete)) {
                result = this.database.delete(tableName, whereString, null);
            }
        }
        else {
            Log.w(LOGTAGNAME, "DateBase is close or key:" + this.whereIndex + " is not in your system,  so will not execute update");
        }

        return result;
    }

    /**
     * Get content values which will be used by update and insert
     * date: 2014/10/23
     *
     * @param data you can give any data that will be used during modification
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    private synchronized boolean GetContentValues(HashMap<String, Object> data) {
        this.valueList.clear();

        boolean result = false;

        try {
            for (String fieldName : data.keySet()) {
                valueList.put(fieldName, data.get(fieldName).toString());
            }

            result = true;
        } catch (Exception ex) {
            Log.e(LOGTAGNAME, ex.getMessage());
        }

        return result;
    }

    /**
     * Query data from the given table
     * date: 2014/10/23
     *
     * @param tableName name of table, like: info.db
     * @param data      you can give any data that will be used during query
     * @return return value will be encapsulate into ArrayList
     * @author Yu-Hua Tseng
     */
    private boolean IsDataBaseOpen() {
        boolean result = false;

        if (this.database != null && this.database.isOpen()) {
            Log.v(LOGTAGNAME, "Database is opened.");
            result = true;
        }
        else {
            Log.e(LOGTAGNAME, "Database is not opened.");
        }

        return result;
    }

    // the sql operation enumeration
    private enum SqlOperatin {
        // insert
        Insert,

        // update
        Update,

        // delete
        Delete,

        // query
        Query,

        // none
        None
    }
}
