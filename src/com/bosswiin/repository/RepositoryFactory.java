package com.bosswiin.repository;

import android.content.Context;
import android.util.Log;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class RepositoryFactory {

    private static final String LOGTAG="RepositoryFactory";

    public static IRepository GetRepository(Context context, String dbName, int version, RepositoryEnum chooser, String initString)
    {
        IRepository repository=null;

        if(chooser.equals(RepositoryEnum.SQLite3)){
            repository=new Sqliter(context, dbName, null,version);
        }

        if(repository != null)
        {
            if(repository.OpenDataBase(dbName, initString)){
                Log.v(LOGTAG, "Open DataBase:" + dbName + " is successful");
            }
            else{
                Log.e(LOGTAG, "Open DataBase:" + dbName + " is fail");
            }
        }

        return repository;
    }
}
