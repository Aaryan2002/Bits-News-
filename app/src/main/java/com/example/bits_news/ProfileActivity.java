package com.example.bits_news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String currentUserID;
    private CircleImageView ProfileImage;
    private TextView ProfileUserName;
    private  TextView ProfileEmail;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        ProfileEmail = (TextView) findViewById(R.id.profile_email);
        ProfileUserName = (TextView) findViewById(R.id.profile_username);
        ProfileImage = (CircleImageView) findViewById(R.id.profile_image);

        FirebaseFirestore.getInstance().collection("users").whereEqualTo("uid", currentUserID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            DocumentSnapshot data = queryDocumentSnapshots.getDocuments().get(0);
            Picasso.get().load(data.get("photo").toString()).into(ProfileImage);
            ProfileUserName.setText(data.get("name").toString());
            ProfileEmail.setText(data.get("email").toString());

        });
        mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Posts");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            SendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

}