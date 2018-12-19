package com.example.irfan.squarecamera;


import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdapterTabelFoto extends RecyclerView.Adapter<AdapterTabelFoto.ViewHolder> {

    private static final String TAG = " AdapterTabelFoto";
    private DBHelper dbHelper;
    private ArrayList<Calculate> listFoto;

    public AdapterTabelFoto(ArrayList<Calculate> listFoto) {
        this.listFoto = listFoto;
    }

    @Override
    //create tampilan item
    public AdapterTabelFoto.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foto, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    //looping posisi penempatan pada list
    public void onBindViewHolder(AdapterTabelFoto.ViewHolder holder, int position) {
        Calculate object = listFoto.get(position);
        holder.idRecycleView.setText(object.getId());
        holder.startRecycleView.setText("start: "+ object.getStartTime());
        holder.endRecycleView.setText("end: "+ object.getEndTime());
        holder.totalRecycleView.setText("total: "+ object.getTotalTime()+ " detik");

    }

    @Override
    //nyari ukuran datanya
    public int getItemCount() {
        return listFoto.size();
    }

    //tampilan isi
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView idRecycleView;
        TextView startRecycleView;
        TextView endRecycleView;
        TextView totalRecycleView;

        public ViewHolder(View itemView) {
            super(itemView);
            idRecycleView = itemView.findViewById(R.id.idRecycleView);
            startRecycleView = itemView.findViewById(R.id.startRecycleView);
            endRecycleView = itemView.findViewById(R.id.endRecycleView);
            totalRecycleView = itemView.findViewById(R.id.totalRecycleView);
        }
    }

    //    public void initDummyData() {
//        listFoto.clear();
//        for(int i=0; i<listFoto.size(); i++){
//            Calculate temp = new Calculate(i, "test ke " + i, "test ke " + i, "test ke " + i);
//            listFoto.add(temp);
//        }
//        //update
//        notifyDataSetChanged();
//    }
}
