package com.madlab.miniproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class SecondActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBHandler dbHandler = new DBHandler(this);
        ArrayList<String> databaseItems = dbHandler.readReminders();
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, databaseItems));
    }
}