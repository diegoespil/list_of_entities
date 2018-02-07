package com.diego.myapplication;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityActivity extends AppCompatActivity {

    private TextView mEntity;
    private RatingBar mRatingBar;
    private Button mButton;
    private View focusView;
    private boolean cancel;
    public final static int REQUEST_CODE = 1;
    private static final String TAG = "EntityActivity";
    private ArrayList<Entity> entityList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity);

        bindViews();

        entityList = (ArrayList<Entity>) getIntent().getExtras().getSerializable("ENTITY_LIST");
    }

    public void bindViews(){
        mEntity = (TextView) findViewById(R.id.entidad);
        mRatingBar = (RatingBar) findViewById(R.id.rating);
        mButton = (Button) findViewById(R.id.add_entity);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempAddEntity();
            }
        });
    }

    public void attempAddEntity(){
        Log.d(TAG, "AttempAddEntity");
        mEntity.setError(null);

        String nameEntity = mEntity.getText().toString();
        Log.d(TAG, "Nombre de entidad "+nameEntity);
        float rating = mRatingBar.getRating();
        Log.d(TAG, "Rating  "+rating);
        cancel = false;
        focusView = null;
        Entity e = new Entity(nameEntity,rating);

        if ((TextUtils.isEmpty(nameEntity)) || (findEntity(e)) || (rating==0.0f)){
            if (TextUtils.isEmpty(nameEntity)){
                mEntity.setError("Este campo es requerido");
                focusView = mEntity;
            } else{
                if (findEntity(e)){
                    Log.d(TAG, "attempAddEntity: La entidad ya esta en la lista");
                    Snackbar.make(getCurrentFocus(), "La entidad ingresada ya existe.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    focusView = mEntity;
                } else{
                    Snackbar.make(getCurrentFocus(), "Por favor da una calificación.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    focusView = mRatingBar;
                }
            }
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            Intent intent = getIntent();
            Log.d(TAG, "Entidad se llama: "+e.getName());
            Log.d(TAG, "Rating: "+e.getRating());
            intent.putExtra("ENTITY", e);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public boolean findEntity(Entity e){
        Log.d(TAG, "findEntity: Buscando entidad");
        boolean find = false;
        String name = e.getName();
        Iterator<Entity> it = entityList.iterator();
        while ((!find) && (it.hasNext())){
            Entity ent = it.next();
            if (name.equals(ent.getName())){
                Log.d(TAG, "findEntity: Encontre entidad");
                find= true;
            }
        }

        return find;
    }
}
