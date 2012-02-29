/**
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 Copyright (c) 2010, R2AD, LLC
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of the R2AD, LLC nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.r2ad.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CloudServiceFavoritesDbAdapter {
	
    public static final String KEY_ROWID = "_id";
    private static final String TAG = "CloudServicesDbAdapter";
    private static final String DATABASE_NAME = "cloud_r2ad";
    private static final String TABLE_NAME = "cloudfavorites";
    private static final int DATABASE_VERSION = 2;  
      
    private ServiceDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "serviceurl";  

    /**
     * Database creation sql statement
     */
    private static final String TABLE_CREATE =
        "create table " + TABLE_NAME + " (_id integer primary key autoincrement, "
        + "title text not null, serviceurl text not null);";   

    private final Context mCtx;

    private static class ServiceDatabaseHelper extends SQLiteOpenHelper {

    	ServiceDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //@Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);          
            onCreate(db);
        }        
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created.
     * 
     * @param ctx the Context within which to work
     */
    public CloudServiceFavoritesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public CloudServiceFavoritesDbAdapter open() throws SQLException {
        mDbHelper = new ServiceDatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        //mDbHelper.onUpgrade(mDb, 2, 3); 
        return this;
    }

    public void close() {
        mDbHelper.close();
    }   
    
    /**
     * Convenience method to build a valid ContentValues entry. 
     * @param title must not be NULL. This will be the display name for the service
     * @param url must not be NULL. This is the service URL as a String.
     * @return the valid ContentValues object
     */
    public ContentValues buildContent(String title, String url) {
    	ContentValues temp = new ContentValues();
    	temp.put(KEY_TITLE, title);
    	temp.put(KEY_URL, url); 
    	return temp;
    }
       
    /**
     * Create a new servers entry in the database.
     * A successful create return the new rowId for that entry, otherwise return
     * a -1 to indicate failure.
     * 
     * @param values the ContentValues to be inserted into the db.
     * @return rowId or -1 if failed
     */
    public long createEntry(ContentValues values) {
        return mDb.insert(TABLE_NAME, null, values);
    }

    /**
     * Delete the server entry with the given rowId
     * 
     * @param rowId id of server to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteEntry(long rowId) {
    	Log.d(TAG, "Deleting Entry " + rowId);
    	return mDb.delete(TABLE_NAME, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all server entries in the database
     * 
     * @return Cursor over all server entries
     */
    public Cursor fetchAllEntries() {
        return mDb.query(TABLE_NAME, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_URL}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of server entry to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if server entry could not be found/retrieved
     */
    public Cursor fetchEntry(long rowId) throws SQLException {
        Cursor mCursor =
            mDb.query(true, TABLE_NAME, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_URL}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update a server entry using the details provided. The entry to be updated is
     * specified using the rowId, and it is modified using the provided ContentValues.
     * 
     * @param values the ContentValues object containing the updates
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateEntry(long rowId, ContentValues values) {
        return mDb.update(TABLE_NAME, values, KEY_ROWID + "=" + rowId, null) > 0;
    }		

}
