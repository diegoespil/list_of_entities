package com.diego.myapplication;

import android.app.UiAutomation;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;



/**
 * Created by diego on 12/12/17.
 */

public class EntityAdapter extends RecyclerView.Adapter<EntityAdapter.ViewHolder> {

    private List<Entity> listEntity;
    private MainActivity mainActivity;
    private static final String TAG = "EntityAdapter";

    public EntityAdapter(MainActivity mainActivity,List<Entity> dataArgs){
        this.mainActivity = mainActivity;
        listEntity = dataArgs;
        Log.d(TAG,"Creando adaptador");
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder");
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder");
        Log.d(TAG, "Posicion "+position);
        Log.d(TAG, "Tamaño: "+getItemCount());
        Log.d(TAG, "Nombre de entidad en onbind.. "+listEntity.get(position).getName());
        Log.d(TAG, "Rating de entidad: "+listEntity.get(position).getRating());
        holder.name.setText(listEntity.get(position).getName());
        holder.rating.setRating(listEntity.get(position).getRating());
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return listEntity.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CardView card;
        private TextView name;
        private RatingBar rating;
        private ImageButton delete;
        private ImageButton edit;

        public ViewHolder(View itemView) {

            super(itemView);
            Log.d(TAG,"ViewHolder");
            card = itemView.findViewById(R.id.card_view);
            name = itemView.findViewById(R.id.name_entity);
            rating = itemView.findViewById(R.id.ratingBar);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);

        }

        public void setListeners() {
            Log.d(TAG,"setListeners");
            delete.setOnClickListener(ViewHolder.this);
            edit.setOnClickListener(ViewHolder.this);
        }

        public void onClick(View view) {
            Log.d(TAG,"onClick");
            Log.d(TAG,"position "+getAdapterPosition());
            switch (view.getId()) {
                case R.id.delete:
                    Log.d(TAG,"is delete");
                    removeItem(getAdapterPosition());
                    break;
                case R.id.edit:
                    Log.d(TAG, "onClick: is edit");
                    editItem(getAdapterPosition());
                    break;
            }
        }

        public void removeItem(int position) {
            Log.d(TAG,"removeItem...");
            mainActivity.removeItem(listEntity.get(position));
            listEntity.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,listEntity.size());
        }

        public void editItem(int position){
            Log.d(TAG, "editItem: editar item");
            mainActivity.editItem(position);
        }
    }


}
