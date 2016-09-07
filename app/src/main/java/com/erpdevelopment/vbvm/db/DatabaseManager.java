package com.erpdevelopment.vbvm.db;

import java.util.concurrent.atomic.AtomicInteger;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager {

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static DatabaseManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
    	System.out.println("openDatabase mOpenCounter is: " + mOpenCounter);
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
            System.out.println("opening database:" );
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
    	System.out.println("closeDatabase mOpenCounter is: " + mOpenCounter);
        if(mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();
            System.out.println("closing database:" );
        }
    }
}