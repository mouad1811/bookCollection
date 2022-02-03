package com.example.bookcollection;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button btn;
    private EditText title;
    private EditText author;
    private EditText year;
    private Button btn_image;
    private ImageView image;
    private Uri mImageUri;

    private StorageReference mStorageRef;
    private FirebaseFirestore firestore;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_add);

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

        firestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("book");

        btn = (Button) findViewById(R.id.add_button);
        btn_image = (Button) findViewById(R.id.image_button);
        title = (EditText) findViewById(R.id.title_input);
        author = (EditText) findViewById(R.id.author_input);
        year = (EditText) findViewById(R.id.year_input);
        image = (ImageView) findViewById(R.id.img_add);

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(title.getText().toString())) {
                    title.setError("Title is Required");
                }
                if (TextUtils.isEmpty(author.getText().toString())) {
                    author.setError("Author is Required");
                }
                if (TextUtils.isEmpty(year.getText().toString())) {
                    year.setError("Year is Required");
                }
                if (!(TextUtils.isEmpty(title.getText().toString()) || TextUtils.isEmpty(author.getText().toString()) || TextUtils.isEmpty(year.getText().toString()))) {
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    String id = db.push().getKey();
                    String t = title.getText().toString();
                    String a = author.getText().toString();
                    String y = year.getText().toString();
                    String f = "0";
                    String uri = System.currentTimeMillis()+"."+getFileExtension(mImageUri);

                    StorageReference fileReference = mStorageRef.child(uri);
                    fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String,String> userMap = new HashMap<>();
                                    userMap.put("id", id);
                                    userMap.put("title", t);
                                    userMap.put("author", a);
                                    userMap.put("year", y);
                                    userMap.put("fav", f);
                                    userMap.put("url",uri.toString());
                                    firestore.collection("book").document(id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(AddActivity.this,"Book Added to Firestore", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            String error = e.getMessage();
                                            Toast.makeText(AddActivity.this,"Error : "+error, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                            Toast.makeText(AddActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(image);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}