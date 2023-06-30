package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ToDoList extends AppCompatActivity {

    EditText task;
    Button addTask;
    LinearLayout checklistLayout;
    dbInfo database;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        task = findViewById(R.id.task);
        addTask = findViewById(R.id.addTask);
        checklistLayout = findViewById(R.id.checklist_layout);

        database = new dbInfo(this);

        String userEmail = getIntent().getStringExtra("user_email");
        userId = getUserIdFromDatabase(userEmail);

        ArrayList<String> existingTasks = getTasksFromDatabase(userId);
        displayExistingTasks(existingTasks);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemText = task.getText().toString().trim();

                if (!itemText.isEmpty()) {
                    addChecklistItem(itemText, 0);
                    addToDatabase(itemText, 0);
                    task.getText().clear();
                }
            }
        });
    }

    public String getUserIdFromDatabase(String email) {
        SQLiteDatabase db = database.getReadableDatabase();

        String[] columns = {dbInfo.COL_ID};
        String selection = dbInfo.COL_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(dbInfo.TABLE, columns, selection, selectionArgs, null, null, null);

        String userId = "";
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(dbInfo.COL_ID);
            if (columnIndex != -1) {
                userId = cursor.getString(columnIndex);
            }
        }

        cursor.close();
        return userId;
    }

    public ArrayList<String> getTasksFromDatabase(String userId) {
        SQLiteDatabase db = database.getReadableDatabase();

        String[] columns = {dbInfo.COL_NOTE_CONTENT, dbInfo.COL_TASK_STATUS};
        String selection = dbInfo.COL_NOTE_USER_ID + " = ?";
        String[] selectionArgs = {userId};
        Cursor cursor = db.query(dbInfo.TABLE_NOTES, columns, selection, selectionArgs, null, null, null);

        ArrayList<String> tasks = new ArrayList<>();
        int columnIndexNoteContent = cursor.getColumnIndex(dbInfo.COL_NOTE_CONTENT);
        int columnIndexTaskStatus = cursor.getColumnIndex(dbInfo.COL_TASK_STATUS);
        while (cursor.moveToNext()) {
            if (columnIndexNoteContent != -1 && columnIndexTaskStatus != -1) {
                String task = cursor.getString(columnIndexNoteContent);
                int taskStatus = cursor.getInt(columnIndexTaskStatus);
                tasks.add(task);
                updateChecklistItem(task, taskStatus);
            }
        }

        cursor.close();
        return tasks;
    }

    public void updateChecklistItem(String task, int taskStatus) {
        for (int i = 0; i < checklistLayout.getChildCount(); i++) {
            View view = checklistLayout.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.getText().toString().equals(task)) {
                    checkBox.setChecked(taskStatus == 1);
                    break;
                }
            }
        }
    }

    public void displayExistingTasks(ArrayList<String> existingTasks) {
        SQLiteDatabase db = database.getReadableDatabase();

        String[] columns = {dbInfo.COL_NOTE_CONTENT, dbInfo.COL_TASK_STATUS};
        String selection = dbInfo.COL_NOTE_USER_ID + " = ?";
        String[] selectionArgs = {userId};
        Cursor cursor = db.query(dbInfo.TABLE_NOTES, columns, selection, selectionArgs, null, null, null);

        int columnIndexNoteContent = cursor.getColumnIndex(dbInfo.COL_NOTE_CONTENT);
        int columnIndexTaskStatus = cursor.getColumnIndex(dbInfo.COL_TASK_STATUS);

        while (cursor.moveToNext()) {
            if (columnIndexNoteContent != -1 && columnIndexTaskStatus != -1) {
                String task = cursor.getString(columnIndexNoteContent);
                int taskStatus = cursor.getInt(columnIndexTaskStatus);
                addChecklistItem(task, taskStatus);
            }
        }

        cursor.close();
    }

    public void addChecklistItem(String itemText, int taskStatus) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        checkboxParams.weight = 1;

        CheckBox checkBox = new CheckBox(this);
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        checkBox.setText(itemText);
        checkBox.setChecked(taskStatus == 1);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateTaskStatus(itemText, isChecked);
            }
        });

        layout.addView(checkBox, checkboxParams);

        LinearLayout.LayoutParams deleteButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(itemText);
            }
        });

        layout.addView(deleteButton, deleteButtonParams);

        checklistLayout.addView(layout);
    }

    public void deleteTask(String itemText) {
        SQLiteDatabase db = database.getWritableDatabase();

        String selection = dbInfo.COL_NOTE_CONTENT + " = ?";
        String[] selectionArgs = {itemText};

        db.delete(dbInfo.TABLE_NOTES, selection, selectionArgs);

        for (int i = 0; i < checklistLayout.getChildCount(); i++) {
            View view = checklistLayout.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) view;
                CheckBox checkBox = (CheckBox) layout.getChildAt(0);
                if (checkBox.getText().toString().equals(itemText)) {
                    checklistLayout.removeView(layout);
                    break;
                }
            }
        }
    }

    public void updateTaskStatus(String itemText, boolean isChecked) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbInfo.COL_TASK_STATUS, isChecked ? 1 : 0);

        String selection = dbInfo.COL_NOTE_CONTENT + " = ?";
        String[] selectionArgs = {itemText};

        db.update(dbInfo.TABLE_NOTES, values, selection, selectionArgs);
    }

    public void addToDatabase(String itemText, int taskStatus) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbInfo.COL_NOTE_CONTENT, itemText);
        values.put(dbInfo.COL_NOTE_USER_ID, userId);
        values.put(dbInfo.COL_TASK_STATUS, taskStatus);

        db.insert(dbInfo.TABLE_NOTES, null, values);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void logout() {
        Intent intent = new Intent(ToDoList.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}