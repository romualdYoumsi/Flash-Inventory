package com.ry.flashinventory.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.ry.flashinventory.database.dao.ArticleDao;
import com.ry.flashinventory.database.dao.ZoneDao;
import com.ry.flashinventory.database.dao.ZoneLineDao;
import com.ry.flashinventory.database.entry.ArticleEntry;
import com.ry.flashinventory.database.entry.ZoneEntry;
import com.ry.flashinventory.database.entry.ZoneLineEntry;

/**
 * Created by netserve on 21/09/2018.
 */

@Database(entities = {ArticleEntry.class, ZoneEntry.class, ZoneLineEntry.class},
        version = 2,
        exportSchema = false)
//@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "flash-inventory_store";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
//                Log.e(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .allowMainThreadQueries() // autorise Room a effectuer les requetes dans le main UI thread
                        .fallbackToDestructiveMigration() // regnere les table apres une incrementation de version
                        .build();
            }
        }
//        Log.e(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    //    Article DAO
    public abstract ArticleDao articleDao();

    //    Inventaire DAO
    public abstract ZoneDao zoneDao();

    //    InventaireLine DAO
    public abstract ZoneLineDao zoneLineDao();

}
