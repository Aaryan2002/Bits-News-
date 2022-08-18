package com.example.bits_news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText Username;
    private Button saveButton;
    private CircleImageView ProfileImage;
    private ProgressBar loadingBar;


    private FirebaseAuth mAuth ;
    private StorageReference UserProfileImageRef;
    private Uri ImageUri ;

    String currentUserID;
    final static int Gallery_Pick = 1;

    private StorageReference mFirebaseStorage;
    private DatabaseReference UsersRef;
    private CollectionReference postRef;
    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl;
    private String username;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID= mAuth.getCurrentUser().getUid();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        mFirebaseStorage = FirebaseStorage.getInstance().getReference();
        int TAKE_IMAGE_CODE = 10001;

        Username = (EditText) findViewById(R.id.setup_username);
        saveButton = (Button) findViewById(R.id.setup_save);
        ProfileImage = (CircleImageView) findViewById(R.id.setup_profile_pic);

        loadingBar= new ProgressBar(this);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveAccountSetupInformation();
            }
        });

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
    }
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
            {
                ImageUri = data.getData();
                ProfileImage.setImageURI(ImageUri);
                StoringImageToFirebaseStorage();
            }
        }




    private void SaveAccountSetupInformation()
    {
         username = Username.getText().toString();
         email = mAuth.getCurrentUser().getEmail().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please write your full name...", Toast.LENGTH_SHORT).show();
        }
        if(ImageUri == null)
        {
            Toast.makeText(this, "Please select post image...", Toast.LENGTH_SHORT).show();
        }
        else
        {
         //   loadingBar.setTitle("Saving Information");
           // loadingBar.setMessage("Please wait, while we are creating your new Account...");
          //  loadingBar.show();
           // loadingBar.setCanceledOnTouchOutside(true);


            SavingPostInformationToDatabase();
            Toast.makeText(SetupActivity.this,"Account Created Successfully", Toast.LENGTH_SHORT).show();
        }
    }
    private void StoringImageToFirebaseStorage()
    {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;


        StorageReference filePath = mFirebaseStorage.child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Toast.makeText(SetupActivity.this, "image uploaded successfully to Storage...", Toast.LENGTH_SHORT).show();
                filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadUrl = uri.toString();
                });

            }
            else
            {
                String message = task.getException().getMessage();
                Toast.makeText(SetupActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void SavingPostInformationToDatabase() {
        HashMap userMap = new HashMap();
        userMap.put("email", email);
        userMap.put("name", username);
        userMap.put("photo", downloadUrl);
        userMap.put("uid", currentUserID);
        CollectionReference users = FirebaseFirestore.getInstance().collection("users");
        users.document(currentUserID).set(userMap).addOnSuccessListener(documentReference -> {Toast.makeText(this,"Account Created",Toast.LENGTH_LONG).show();
            SendUserToMainActivity();});;
    }
    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}