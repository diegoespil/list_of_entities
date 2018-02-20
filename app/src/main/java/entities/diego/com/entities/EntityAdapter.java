package entities.diego.com.entities;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHolder");
        Log.d(TAG, "Posicion "+position);
        Log.d(TAG, "Tamaño: "+getItemCount());
        Log.d(TAG, "Nombre de entidad en onbind.. "+listEntity.get(position).getName());
        Log.d(TAG, "Rating de entidad: "+listEntity.get(position).getRating());
        holder.timestamp.setText(listEntity.get(position).getTimestamp());
        holder.name.setText(listEntity.get(position).getName());
        holder.rating.setRating(listEntity.get(position).getRating());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listEntity.size();
    }

    public void removeItem(int position) {
        Log.d(TAG,"removeItem...");
        mainActivity.removeItemDatabase(listEntity.get(position));
        listEntity.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position,listEntity.size());
        notifyDataSetChanged();
    }

    public void editItem(int position){
        Log.d(TAG, "editItem: editar item");
        mainActivity.editItem(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView timestamp;
        private TextView name;
        private RatingBar rating;
        private CardView cardView;

        public ViewHolder(View itemView) {

            super(itemView);
            Log.d(TAG,"ViewHolder");
            timestamp = itemView.findViewById(R.id.timestamp);
            name = itemView.findViewById(R.id.name_entity);
            rating = itemView.findViewById(R.id.ratingBar);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }


}