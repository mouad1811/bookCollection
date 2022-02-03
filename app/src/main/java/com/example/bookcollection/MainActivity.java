package com.example.bookcollection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private FirebaseFirestore firebase;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.bottom_nav_home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.bottom_nav_my_collection:
                        startActivity(new Intent(getApplicationContext(),MyCollection.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.bottom_nav_add:
                        startActivity(new Intent(getApplicationContext(),AddActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        firebase = FirebaseFirestore.getInstance();
        rv = (RecyclerView) findViewById(R.id.rv_books);

        Query query = firebase.collection("book");

        FirestoreRecyclerOptions<books> options = new FirestoreRecyclerOptions.Builder<books>()
                .setQuery(query, books.class).build();

        adapter = new FirestoreRecyclerAdapter<books, booksViewHolder>(options) {
            @NonNull
            @Override
            public booksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View hView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
                return new booksViewHolder(hView);
            }

            @Override
            protected void onBindViewHolder(@NonNull booksViewHolder holder, int position, @NonNull books model) {
                holder.title.setText(model.getTitle());
                holder.author.setText(model.getAuthor());
                Picasso.with(MainActivity.this).load(model.getUrl()).fit().centerCrop().into(holder.image);

                int f = Integer.parseInt(model.getFav())==1 ? R.drawable.ic_baseline_favorite_purple_24 : R.drawable.ic_baseline_favorite_white_24;
                holder.btn_fav.setImageResource(f);

                holder.btn_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getFav().equals("0")) {
                            String id = model.getId();
                            String t = model.getTitle();
                            String a = model.getAuthor();
                            String y = model.getYear();
                            String url = model.getUrl();

                            Map<String,String> userMap = new HashMap<>();

                            userMap.put("id", id);
                            userMap.put("title", t);
                            userMap.put("author", a);
                            userMap.put("year", y);
                            userMap.put("fav", "1");
                            userMap.put("url", url);
                            firebase.collection("book").document(id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this,"Book Added to Favorites", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    String error = e.getMessage();
                                    Toast.makeText(MainActivity.this,"Error : "+error, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else if (model.getFav().equals("1")) {
                            String id = model.getId();
                            String t = model.getTitle();
                            String a = model.getAuthor();
                            String y = model.getYear();
                            String url = model.getUrl();

                            Map<String,String> userMap = new HashMap<>();

                            userMap.put("id", id);
                            userMap.put("title", t);
                            userMap.put("author", a);
                            userMap.put("year", y);
                            userMap.put("fav", "0");
                            userMap.put("url", url);

                            firebase.collection("book").document(id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this,"Book Deleted from Favorites", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    String error = e.getMessage();
                                    Toast.makeText(MainActivity.this,"Error : "+error, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                        intent.putExtra("model",model);
                        startActivity(intent);
                    }
                });
            }
        };

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManagerWrapper(this));
        rv.setAdapter(adapter);

    }

    class booksViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView author;
        private ImageButton btn_fav;
        private ImageView image;
        View v;

        public booksViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.textView_title);
            author = (TextView) itemView.findViewById(R.id.textView_author);
            btn_fav = (ImageButton) itemView.findViewById(R.id.fav_button);
            image = (ImageView) itemView.findViewById(R.id.imageView);
            v = itemView;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public class LinearLayoutManagerWrapper extends LinearLayoutManager {

        public LinearLayoutManagerWrapper(Context context) {
            super(context);
        }

        public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public LinearLayoutManagerWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

}



