package com.tusar.creativeitem.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import static com.tusar.creativeitem.helper.SessionManager.TAG;

/**
 * Created by Farruck Ahmed Tusar on 16-Jun-17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "doctor";

    // Contacts table name
    private static final String TABLE_USERS = "user";
    private static final String TABLE_PATIENTS = "patients";
    private static final String TABLE_CHAMBERS = "chambers";
    private static final String TABLE_SCHEDULE = "schedule";

    // User Table Columns Names
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_TYPE = "user_type";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_CHAMBER = "chamber_id";
    private static final String KEY_ACC_ID = "account_id";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_LAST_LOGIN = "last_login";

    //Patient Table Columns Names
    private static final String KEY_PATIENT_ID = "patient_id";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_ABOUT = "about";
    private static final String KEY_AGE = "age";
    private static final String KEY_GENDER = "gender";

    //Chamber Table Columns Names
    private static final String KEY_CHAMBER_ID = "chamber_id";

    //Schedule Table Columns Names
    private static final String KEY_DAY = "day";
    private static final String KEY_KEY = "key";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MORNING_OPEN = "morning_open";
    private static final String KEY_MORNING_CLOSE = "morning_close";
    private static final String KEY_MORNING = "morning";
    private static final String KEY_AFTERNOON_OPEN = "afternoon_open";
    private static final String KEY_AFTERNOON_CLOSE = "afternoon_close";
    private static final String KEY_AFTERNOON = "afternoon";
    private static final String KEY_EVENING_OPEN = "evening_open";
    private static final String KEY_EVENING_CLOSE = "evening_close";
    private static final String KEY_EVENING = "evening";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER_ID + " TEXT,"
                + KEY_USER_TYPE + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_CHAMBER + " TEXT,"
                + KEY_ACC_ID + " TEXT,"
                + KEY_TOKEN + " TEXT,"
                + KEY_LAST_LOGIN + " TEXT" + ")";

        String CREATE_PATIENTS_TABLE = "CREATE TABLE " + TABLE_PATIENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_PATIENT_ID + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_ABOUT + " TEXT,"
                + KEY_AGE + " TEXT,"
                + KEY_GENDER + " TEXT" + ")";

        String CREATE_CHAMBER_TABLE = "CREATE TABLE " + TABLE_CHAMBERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CHAMBER_ID + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_ABOUT + " TEXT,"
                + KEY_ACC_ID + " TEXT,"
                + KEY_STATUS + " TEXT" + ")";

        String CREATE_SCHEDULE_TABLE = "CREATE TABLE " + TABLE_SCHEDULE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CHAMBER_ID + " TEXT,"
                + KEY_DAY + " TEXT,"
                + KEY_KEY + " TEXT,"
                + KEY_STATUS + " TEXT,"
                + KEY_MORNING_OPEN + " TEXT,"
                + KEY_MORNING_CLOSE + " TEXT,"
                + KEY_MORNING + " TEXT,"
                + KEY_AFTERNOON_OPEN + " TEXT,"
                + KEY_AFTERNOON_CLOSE + " TEXT,"
                + KEY_AFTERNOON + " TEXT,"
                + KEY_EVENING_OPEN + " TEXT,"
                + KEY_EVENING_CLOSE + " TEXT,"
                + KEY_EVENING + " TEXT" + ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PATIENTS_TABLE);
        db.execSQL(CREATE_CHAMBER_TABLE);
        db.execSQL(CREATE_SCHEDULE_TABLE);
        //Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);

        // Create tables again
        onCreate(db);
    }



    // Adding new user
    public void addUser(String user_id, String user_type, String name, String email, String phone, String chamber_id, String account_id, String token, String last_login) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user_id);
        values.put(KEY_USER_TYPE, user_type);
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PHONE, phone);
        values.put(KEY_CHAMBER, chamber_id);
        values.put(KEY_ACC_ID, account_id);
        values.put(KEY_TOKEN, token);
        values.put(KEY_LAST_LOGIN, last_login);

        // Inserting Row
        db.insert(TABLE_USERS, null, values);
        db.close(); // Closing database connection
    }
    public HashMap<String, String> getUser(){
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor= db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        if(cursor.getCount() >0)
        {
            user.put("id",cursor.getString(0));
            user.put("user_id",cursor.getString(1));
            user.put("user_type",cursor.getString(2));
            user.put("name",cursor.getString(3));
            user.put("email",cursor.getString(4));
            user.put("phone",cursor.getString(5));
            user.put("chamber_id",cursor.getString(6));
            user.put("account_id",cursor.getString(7));
            user.put("token",cursor.getString(8));
            user.put("last_login",cursor.getString(9));
        }
        cursor.close();
        //Log.d(TAG,"Fetching Data from Sqlite:" + user.toString());
        return user;
    }
    public void updateUser(String user_id,String name,String phone,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("Print from sqlite: Update User");
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USER_ID,user_id );
        contentValues.put(KEY_NAME,name );
        contentValues.put(KEY_PHONE,phone );
        contentValues.put(KEY_EMAIL,email );
        //update patient
        String strFilter = "user_id=" + user_id;
        long id = db.update(TABLE_USERS, contentValues, strFilter, null);

        Log.d(TAG, "Updated user into sqlite: " + id);
    }

    public void addPatient(String patient_id, String name, String phone, String address, String about, String age, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PATIENT_ID, patient_id);
        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_ABOUT, about);
        values.put(KEY_AGE, age);
        values.put(KEY_GENDER, gender);

        // Inserting Row
        db.insert(TABLE_PATIENTS, null, values);
        System.out.println("-->> Add Patient Successfully");
        db.close(); // Closing database connection
    }
    public ArrayList<HashMap<String, String>> getAllPatient(){

        ArrayList<HashMap<String, String>> patient_list = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_PATIENTS;
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor= db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id",cursor.getString(0));
                hashMap.put("patient_id",cursor.getString(1));
                hashMap.put("name",cursor.getString(2));
                hashMap.put("phone",cursor.getString(3));
                hashMap.put("address",cursor.getString(4));
                hashMap.put("about",cursor.getString(5));
                hashMap.put("age",cursor.getString(6));
                hashMap.put("gender",cursor.getString(7));

                patient_list.add(hashMap);
            } while(cursor.moveToNext());
        }
        Log.v("All Patient", DatabaseUtils.dumpCursorToString(cursor));
        cursor.close();
        // return user
        //Log.d(TAG,"Fetching All Patient from Sqlite:" + patient_list.toString());
        return patient_list;
    }


    // Chambers table crud
    public void addChamber(String chamber_id, String name, String address, String about, String account_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CHAMBER_ID, chamber_id);
        values.put(KEY_NAME, name);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_ABOUT, about);
        values.put(KEY_ACC_ID, account_id);
        values.put(KEY_STATUS, "null");

        // Inserting Row
        db.insert(TABLE_CHAMBERS, null, values);
        System.out.println("-->> Add Chamber Successfully");
        db.close(); // Closing database connection
    }
    public ArrayList<HashMap<String, String>> getAllChamber(){

        ArrayList<HashMap<String, String>> chamber_list = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_CHAMBERS;
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor= db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id",cursor.getString(0));
                hashMap.put("chamber_id",cursor.getString(1));
                hashMap.put("name",cursor.getString(2));
                hashMap.put("address",cursor.getString(3));
                hashMap.put("about",cursor.getString(4));
                hashMap.put("account_id",cursor.getString(5));
                hashMap.put("status",cursor.getString(6));

                chamber_list.add(hashMap);
            } while(cursor.moveToNext());
        }
        Log.v("All Chamber List", DatabaseUtils.dumpCursorToString(cursor));
        cursor.close();
        // return user
        //Log.d(TAG,"Fetching All Patient from Sqlite:" + patient_list.toString());
        return chamber_list;
    }
    public HashMap<String, String> getChamberById(String id){
        HashMap<String, String> chamber = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_CHAMBERS + " WHERE chamber_id="+id;
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor= db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() >0)
        {
            chamber.put("id",cursor.getString(0));
            chamber.put("chamber_id",cursor.getString(1));
            chamber.put("name",cursor.getString(2));
            chamber.put("address",cursor.getString(3));
            chamber.put("about",cursor.getString(4));
            chamber.put("account_id",cursor.getString(5));

        }
        cursor.close();
        // return user
        Log.d(TAG,"Fetching Chamber from Sqlite:" + chamber.toString());
        return chamber;
    }
    public HashMap<String, String> getChamberByName(String name){
        HashMap<String, String> chamber = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_CHAMBERS + " WHERE name='"+name+"'";
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor= db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        if(cursor.getCount() >0)
        {
            chamber.put("id",cursor.getString(0));
            chamber.put("chamber_id",cursor.getString(1));
            chamber.put("name",cursor.getString(2));
            chamber.put("address",cursor.getString(3));
            chamber.put("about",cursor.getString(4));
            chamber.put("account_id",cursor.getString(5));

        }
        cursor.close();
        // return user
        Log.d(TAG,"Fetching Chamber from Sqlite:" + chamber.toString());
        return chamber;
    }
    public void updateChamber(String chamber_id){
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("Print from sqlite: Update Chamber");
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CHAMBER_ID,chamber_id );
        contentValues.put(KEY_STATUS,"Selected" );
        //update patient
        String strFilter = "chamber_id=" + chamber_id;
        long id = db.update(TABLE_CHAMBERS, contentValues, strFilter, null);

        //Set null to other chambers
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(KEY_STATUS,"null" );
        //update patient
        String strFilter1 = "chamber_id!=" + chamber_id;
        db.update(TABLE_CHAMBERS, contentValues1, strFilter1, null);
        Log.d(TAG, "Updated chamber into sqlite: " + id);
    }

    //SCHEDULE TABLE CRUD
    public void addSchedule(String chamber_id, String day, String key, String status, String morning_open, String morning_close, String morning, String afternoon_open, String afternoon_close, String afternoon, String evening_open, String evening_close, String evening) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CHAMBER_ID, chamber_id);
        values.put(KEY_DAY, day);
        values.put(KEY_KEY, key);
        values.put(KEY_STATUS, status);
        values.put(KEY_MORNING_OPEN, morning_open);
        values.put(KEY_MORNING_CLOSE, morning_close);
        values.put(KEY_MORNING, morning);
        values.put(KEY_AFTERNOON_OPEN, afternoon_open);
        values.put(KEY_AFTERNOON_CLOSE, afternoon_close);
        values.put(KEY_AFTERNOON, afternoon);
        values.put(KEY_EVENING_OPEN, evening_open);
        values.put(KEY_EVENING_CLOSE, evening_close);
        values.put(KEY_EVENING, evening);

        // Inserting Row
        db.insert(TABLE_SCHEDULE, null, values);
        System.out.println("-->> Add Schedule Successfully");
        db.close(); // Closing database connection
    }
    public ArrayList<HashMap<String, String>> getScheduleById(String chamber_id){

        ArrayList<HashMap<String, String>> schedule_list = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_SCHEDULE+ " WHERE chamber_id="+chamber_id;;
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor= db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                HashMap<String, String> schedule = new HashMap<String, String>();
                schedule.put("id",cursor.getString(0));
                schedule.put("chamber_id",cursor.getString(1));
                schedule.put("day",cursor.getString(2));
                schedule.put("key",cursor.getString(3));
                schedule.put("status",cursor.getString(4));
                schedule.put("morning_open",cursor.getString(5));
                schedule.put("morning_close",cursor.getString(6));
                schedule.put("morning",cursor.getString(7));
                schedule.put("afternoon_open",cursor.getString(8));
                schedule.put("afternoon_close",cursor.getString(9));
                schedule.put("afternoon",cursor.getString(10));
                schedule.put("evening_open",cursor.getString(11));
                schedule.put("evening_close",cursor.getString(12));
                schedule.put("evening", cursor.getString(13));

                schedule_list.add(schedule);
            } while(cursor.moveToNext());
        }
        Log.v("All Schedule List", DatabaseUtils.dumpCursorToString(cursor));
        cursor.close();
        return schedule_list;
    }

    //Delete Tables
    public void deleteTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USERS, null, null);
        db.delete(TABLE_PATIENTS, null, null);
        db.delete(TABLE_CHAMBERS,null,null);
        db.delete(TABLE_SCHEDULE,null,null);
        //Log.d(TAG, "Deleted User from sqlite");
    }

}
