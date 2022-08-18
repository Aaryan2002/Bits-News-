package com.example.bits_news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle menuToggle;
    private FloatingActionButton new_post;
    private RecyclerView feed;
    private Toolbar mToolbar;
    private DatabaseReference Usersref, PostsRef;
    private ImageButton AddNewPostButton;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        Usersref = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("News");

        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);

        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawable);
        menuToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(menuToggle);
        menuToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.main_navView);

        // RENDERING ALL THE POST IN A RECYCLER VIEW
        feed = (RecyclerView) findViewById(R.id.main_feed);         // ALL USERS POST LIST
        feed.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);         // TO DISPLAY NEWER POSTS AT THE TOP
        linearLayoutManager.setStackFromEnd(true);
        feed.setLayoutManager(linearLayoutManager);

        FirebaseFirestore.getInstance().collection("posts").orderBy("uploadedOn", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.getDocuments().size();
                if (size == 0) {
                    Toast.makeText(MainActivity.this, "No posts yet", Toast.LENGTH_SHORT).show();
                } else {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    Posts[] posts = new Posts[size];
                    for (int i = 0; i < size; i++) {
                        posts[i] = docs.get(i).toObject(Posts.class);
                    }
                    feed.setAdapter(new PostsAdapter(posts));
                }
            }
        });

        View nav_header = navigationView.inflateHeaderView(R.layout.navigation_header);
        CircleImageView profileImage = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_profile_pic);
        TextView UserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username);
        if(profileImage != null) {
            FirebaseFirestore.getInstance().collection("users").whereEqualTo("uid", currentUserID).get().addOnSuccessListener(queryDocumentSnapshots -> {
                DocumentSnapshot data = queryDocumentSnapshots.getDocuments().get(0);
                Picasso.get().load(data.get("photo").toString()).into(profileImage);
                UserName.setText(data.get("name").toString());

            });
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });


        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPostActivity();
            }
        });



    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFullname(String fullname) {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileimage(Context ctx, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
//            Picasso.get(ctx).load(profileimage).into(image);
        }


        public void setDate(String date) {
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("    " + date);
        }

        public void setDescription(String description) {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(description);
        }

        public void setPostimage(Context ctx1, String postimage) {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
//            Picasso.get(ctx1).load(postimage).into(PostImage);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (menuToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                sendToProfile();
                break;

            case R.id.nav_myPosts:
                //Toast.makeText(this,"My Posts",Toast.LENGTH_SHORT).show();
                SendUserToMyPostsActivity();
                break;

            case R.id.nav_developers:
                sendToDevelopers();
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                sendToLogin();
                break;
        }
    }

    private void sendToProfile() {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
    }

    private void sendToDevelopers() {
        Intent developersIntent = new Intent(MainActivity.this, DeveloperActivity.class);
        developersIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(developersIntent);
    }

    private void SendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }
    private void SendUserToMyPostsActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, MyPostsActivity.class);
        startActivity(addNewPostIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        } else {
            CheckUserExistence();
        }
    }

    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(Objects.requireNonNull(current_user_id))
                .get()
                .addOnCompleteListener(task -> {
                    if(!task.getResult().exists())
                        SendUserToSetupActivity();
                });
   }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}