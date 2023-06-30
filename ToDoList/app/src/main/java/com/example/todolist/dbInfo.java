package com.example.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class dbInfo extends SQLiteOpenHelper {

    private static final String DB_NAME = "ToDoList";
    private static final int DB_VERSION = 1;
    public static final String TABLE = "Users";
    public static final String TABLE_NOTES = "Notes";
    public static final String COL_ID = "uid";
    public static final String COL_FNAME = "fname";
    public static final String COL_LNAME = "lname";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASS = "password";
    public static final String COL_NOTE_ID = "note_id";
    public static final String COL_NOTE_CONTENT = "note_content";
    public static final String COL_NOTE_USER_ID = "user_id";
    public static final String COL_TASK_STATUS = "task_status";

    public dbInfo(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create Users (uid INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, password TEXT);
        sqLiteDatabase.execSQL("Create table " + TABLE +
                "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FNAME + " TEXT, " +
                COL_LNAME + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PASS + " TEXT " + ")"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NOTES +
                "(" + COL_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOTE_CONTENT + " TEXT, " +
                COL_NOTE_USER_ID + " INTEGER, " +
                COL_TASK_STATUS + " INTEGER DEFAULT 0, " +
                " FOREIGN KEY( " + COL_NOTE_USER_ID + " ) REFERENCES " + TABLE + "(" + COL_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" Drop table if exists " + TABLE);
        sqLiteDatabase.execSQL(" Drop table if exists " + TABLE_NOTES);

        onCreate(sqLiteDatabase);
    }

    public void addData(String fname, String lname, String email, String pass){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_FNAME, fname);
        values.put(COL_LNAME, lname);
        values.put(COL_EMAIL, email);
        values.put(COL_PASS, pass);

        long userId = db.insert(TABLE, null, values);
    }

    public void addNote(String noteContent, long userId, int task_status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NOTE_CONTENT, noteContent);
        values.put(COL_NOTE_USER_ID, userId);
        values.put(COL_TASK_STATUS, task_status);

        db.insert(TABLE_NOTES, null, values);
    }

    public void searchUser(String email, String password, Context context){

        SQLiteDatabase db = this.getReadableDatabase();

        //Cursor cur = db.rawQuery(" Select * from " + TABLE + " where COL_EMAIL = '" + email + "'", null);

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE + " WHERE " + COL_EMAIL + " = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex(COL_PASS);
            if (passwordIndex != -1) {
                String storedPassword = cursor.getString(passwordIndex);
                if (storedPassword.equals(password)) {
                    Intent list = new Intent(context, ToDoList.class);
                    list.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    list.putExtra("user_email", email);
                    context.startActivity(list);
                    ((Activity) context).finish();
                    return;
                }
            }
        }

        Toast.makeText(context, "Invalid email or password.", Toast.LENGTH_SHORT).show();

        cursor.close();
    }
}
