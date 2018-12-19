package com.example.irfan.squarecamera;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class RecycleView extends AppCompatActivity {

    private static final String TAG = "RecycleActivity" ;
    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private AdapterTabelFoto mAdapter;
    private ArrayList<Calculate> listData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
        setContentView(R.layout.activity_recycle_view);
        recyclerView = findViewById(R.id.rv_foto);

        listData = dbHelper.getData();
        mAdapter = new AdapterTabelFoto(listData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }
}
