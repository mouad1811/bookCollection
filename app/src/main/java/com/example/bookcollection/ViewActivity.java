package com.example.bookcollection;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView view_title;
    private TextView view_author;
    private TextView view_year;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        books model = (books) getIntent().getSerializableExtra("model");

        imageView = (ImageView) findViewById(R.id.image);
        view_title = (TextView) findViewById(R.id.view_title);
        view_author = (TextView) findViewById(R.id.view_author);
        view_year = (TextView) findViewById(R.id.view_year);

        view_title.setText(model.getTitle());
        view_author.setText(model.getAuthor());
        view_year.setText(model.getYear());
        Picasso.with(ViewActivity.this).load(model.getUrl()).fit().into(imageView);

    }
}
