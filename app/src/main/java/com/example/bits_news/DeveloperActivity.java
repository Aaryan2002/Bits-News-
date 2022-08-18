package com.example.bits_news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeveloperActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        mToolbar = (Toolbar) findViewById(R.id.developer_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Developers");
        CircleImageView profileImage = (CircleImageView)findViewById(R.id.developer_profile_image);
        CircleImageView profileImage2 = (CircleImageView)findViewById(R.id.developer_profile_image2);
        CircleImageView profileImage3 = (CircleImageView)findViewById(R.id.developer_profile_image3);
        Picasso.get().load("https://lh3.googleusercontent.com/a-/AFdZucrmpxxW9-VhTkollR_305BUbbrz96Xy1pIkRdH6=s96-c").into(profileImage);
        Picasso.get().load("https://lh3.googleusercontent.com/a-/AFdZucpuVxVE-eBXvjb1lfqPzqF15yGiHOaX_zofbn1M=s96-c").into(profileImage2);
        Picasso.get().load("https://lh3.googleusercontent.com/a/AItbvmnM7HdMaX7BFQ1wPxNl9IVcfu37oPuvYVfviKbQ=s96-c").into(profileImage3);
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
        Intent mainIntent = new Intent(DeveloperActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}