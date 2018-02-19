package entities.diego.com.entities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.facebook.AccessToken;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.annotation.Target;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import static entities.diego.com.entities.EntityActivity.REQUEST_CODE;


public class MainActivity extends AppCompatActivity  implements
        NavigationView.OnNavigationItemSelectedListener{


    private RecyclerView dataSource;
    private EntityAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Entity> entityList;
    private FirebaseAuth auth;
    private boolean ascending = true;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ONCREATE");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.navigation_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        LinearLayout salir = findViewById(R.id.layout_salir);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Sesion cerrada", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                goLoginScreen();
            }
        });

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        if (auth != null){
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null){
                String name = currentUser.getDisplayName();
                Toast.makeText(MainActivity.this, "Hola "+name, Toast.LENGTH_LONG).show();
            }
        } else {
            goLoginScreen();
        }

        final ImageView imageProfile= navigationView.getHeaderView(0).findViewById(R.id.imageProfile);
        Glide.with(getApplicationContext())
                .load(auth.getCurrentUser().getPhotoUrl())
                .asBitmap()
                .into(new BitmapImageViewTarget(imageProfile){

                    @Override
                    protected void setResource(Bitmap resource){
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageProfile.setImageDrawable(circularBitmapDrawable);
                    }
                });
        TextView nameProfile = navigationView.getHeaderView(0).findViewById(R.id.nameProfile);
        nameProfile.setText(auth.getCurrentUser().getDisplayName());
        TextView emailProfile = navigationView.getHeaderView(0).findViewById(R.id.emailProfile);
        emailProfile.setText(auth.getCurrentUser().getEmail());

        bindViews();

        entityList = new ArrayList<>();

        adapter = new EntityAdapter(this,entityList);

        linearLayoutManager = new LinearLayoutManager(this);
        dataSource.setLayoutManager(linearLayoutManager);
        dataSource.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback iTouch = new EntityTouchHelper(adapter,dataSource);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(iTouch);
        itemTouchHelper.attachToRecyclerView(dataSource);


        if ((getParent() == null) || (getParent().equals(LoginActivity.class))){
            Log.d(TAG, "onCreate: Login and read from database");
            readDatabase();
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EntityActivity.class);
                intent.putExtra("ENTITY_LIST", entityList);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

            if ( item.getItemId() == R.id.order_by_name)
                Toast.makeText(MainActivity.this, "Order", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = getLayoutInflater().inflate(R.layout.orderby, null);
                builder.setView(view);
                final RadioButton ord_name = view.findViewById(R.id.order_by_name);
                final RadioButton ord_rating = view.findViewById(R.id.order_by_rating);
                final RadioButton ord_asc = view.findViewById(R.id.order_asc);
                final RadioButton ord_desc = view.findViewById(R.id.order_desc);
                Button btnOk = view.findViewById(R.id.aceptar);
                Button btnCancel = view.findViewById(R.id.cancel);
                final AlertDialog dialog = builder.create();
                dialog.show();
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean by_name = ord_name.isChecked();
                        boolean by_rating = ord_rating.isChecked();
                        boolean asc = ord_asc.isChecked();
                        boolean desc = ord_desc.isChecked();
                        if ((by_name) && (asc)){
                            orderByName(asc);
                        } else
                            if ((by_name) && (desc)){
                                orderByName(asc);
                            } else
                                if ((by_rating) && (asc)){
                                    orderByRating(asc);
                                } else
                                    orderByRating(asc);

                        dialog.cancel();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goLoginScreen() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected void bindViews() {
        dataSource = findViewById(R.id.lista);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Luego de obtener resultados");
        switch (requestCode) {
            case EntityActivity.REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    Entity createdEntity = (Entity) data.getSerializableExtra("ENTITY");
                    Log.d(TAG, "Timestamp: "+createdEntity.getTimestamp());
                    Log.d(TAG, "Nombre: "+createdEntity.getName());
                    Log.d(TAG, "Rating: "+createdEntity.getRating());
                    entityList.add(0, createdEntity);
                    adapter.notifyItemInserted(0);
                    adapter.notifyItemRangeChanged(0,entityList.size());
                    linearLayoutManager.scrollToPositionWithOffset(0,0);
                    writeDatabase(createdEntity);
                }
                break;
            }
        }
    }


    private void writeDatabase(Entity e){
        Log.d(TAG,"Escribir en la base de datos");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String current = auth.getCurrentUser().getUid();
        DatabaseReference myRef = database.getReference(current);
        //myRef.setValue("Nueva entidad");
        //Creating a new entity node, which returns the unique key value
        //new entity node would be /entities/$entityId/
        String entityId = myRef.push().getKey();

        //pushing entity to 'entities' node using the entityId
        myRef.child(entityId).setValue(e);
    }

    private void readDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String current = auth.getCurrentUser().getUid();
        Log.d(TAG, "Uid: "+current);
        DatabaseReference myRef = database.getReference(current);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"Leyendo de la database");

                for (DataSnapshot entity: dataSnapshot.getChildren()){
                    Entity e = new Entity(entity.child("timestamp").getValue(String.class)
                            ,entity.child("name").getValue(String.class),
                            entity.child("rating").getValue(Float.class));
                    Log.d(TAG,"Nombre "+e.getName()+" Rating "+e.getRating());

                        entityList.add(0, e);
                        adapter.notifyItemInserted(0);
                        adapter.notifyItemRangeChanged(0,entityList.size());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Fallo al leer valor");
            }
        });
    }

    public void removeItemDatabase(Entity e){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(auth.getCurrentUser().getUid());
        Query query = ref.orderByChild("name").equalTo(e.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot entitySnapshot: dataSnapshot.getChildren()){
                    entitySnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
        Snackbar.make(getCurrentFocus(),"Entidad eliminada",Snackbar.LENGTH_SHORT).show();
    }

    public void updateDatabase(Entity old, final Entity newEntity){
        Log.d(TAG, "updateDatabase: ");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String current = auth.getCurrentUser().getUid();
        DatabaseReference myRef = database.getReference(current);
        Query query = myRef.orderByChild("name").equalTo(old.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot entitySnapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: "+entitySnapshot.getRef());
                    entitySnapshot.getRef().setValue(newEntity);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    public void editItem(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_update, null);
        builder.setView(view);
        final Entity ent = entityList.get(position);
        final Entity old = new Entity(ent.getTimestamp(),ent.getName(),ent.getRating());
        final EditText name_entity = view.findViewById(R.id.entidad);
        name_entity.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        final RatingBar rating = view.findViewById(R.id.rating);
        Button update = view.findViewById(R.id.edit_entity);
        name_entity.setText(ent.getName());
        rating.setRating(ent.getRating());
        final AlertDialog dialog = builder.create();
        dialog.show();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timestamp = DateFormat.getDateTimeInstance().format(new Date());
                String updateEntity = name_entity.getText().toString();
                updateEntity = updateEntity.substring(0,1).toUpperCase()+updateEntity.substring(1);
                Float updateRating = rating.getRating();
                if (findEntity(updateEntity,position)){
                    Toast.makeText(getApplicationContext(),"La entidad ingresada ya exite",Toast.LENGTH_SHORT).show();
                }else{
                    ent.setTimestamp(timestamp);
                    ent.setName(updateEntity);
                    ent.setRating(updateRating);
                    adapter.notifyItemChanged(position);
                    adapter.notifyItemRangeChanged(position,entityList.size());
                    dialog.cancel();
                    Log.d(TAG, "onClick: old: name "+old.getName()+" rating "+old.getRating());
                    Log.d(TAG, "onClick: update: name "+ent.getName()+" rating "+ent.getRating());
                    updateDatabase(old, ent);
                }
            }
        });

    }

    private boolean findEntity(String name, int pos){
        Log.d(TAG, "findEntity: Buscando entidad");
        boolean find = false;
        int i = 0;
        Entity ent;
        Iterator<Entity> it = entityList.iterator();
        while ((!find) && (i!=pos) && (it.hasNext())){
            ent = it.next();
            if (name.equals(ent.getName())){
                Log.d(TAG, "findEntity: Encontre entidad");
                find= true;
            }
            i++;
        }
        if ((i==pos) && it.hasNext()){
            it.next();
            while ((!find) && (it.hasNext())){
                ent = it.next();
                if (name.equals(ent.getName())){
                    Log.d(TAG, "findEntity: Encontre entidad");
                    find= true;
                }
            }
        }

        return find;
    }

    protected void orderByName(boolean order){
        if (order){
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        } else {
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    return o2.getName().compareTo(o1.getName());
                }
            });
        }

        adapter = new EntityAdapter(this, entityList);
        dataSource.setLayoutManager(new LinearLayoutManager(this));
        dataSource.setAdapter(adapter);
    }

    protected void orderByRating(boolean order){
        if (order){
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    Float r1 = o1.getRating();
                    Float r2 = o2.getRating();
                    return r1.compareTo(r2);
                }
            });
        } else {
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    Float r1 = o1.getRating();
                    Float r2 = o2.getRating();
                    return r2.compareTo(r1);
                }
            });
        }
        adapter = new EntityAdapter(this, entityList);
        dataSource.setLayoutManager(new LinearLayoutManager(this));
        dataSource.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*switch (item.getItemId()){
            case R.id.sign_out:{
                Toast.makeText(MainActivity.this, "Sesion cerrada", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                goLoginScreen();
                break;
            }
            case R.id.sort_name:{
                Toast.makeText(MainActivity.this, "Ordenado por nombre", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onOptionsItemSelected: ascending "+ascending);
                orderByName(ascending);
                if (ascending) ascending = false;
                else ascending = true;
                break;
            }
            case R.id.sort_rating:{
                Toast.makeText(MainActivity.this, "Ordenado por rating", Toast.LENGTH_SHORT).show();
                orderByRating(ascending);
                if (ascending) ascending = false;
                else ascending = true;
                break;
            }
        }*/


        return super.onOptionsItemSelected(item);
    }
}