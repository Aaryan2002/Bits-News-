package com.example.bits_news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class  PostsAdapter extends RecyclerView.Adapter<PostsViewHolder> {
    private Posts[] posts;

    public PostsAdapter(Posts[] posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostsViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_posts_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        holder.bind(posts[position]);
    }

    @Override
    public int getItemCount() {
        return posts.length;
    }
}

class PostsViewHolder extends RecyclerView.ViewHolder {

    private String documentId;
    private final TextView likes, dislikes;
    private final ImageView likeButton, dislikeButton, likeButtonOutline, dislikeButtonOutline;


    public PostsViewHolder(@NonNull View itemView) {
        super(itemView);
        likes = itemView.findViewById(R.id.likes);
        dislikes = itemView.findViewById(R.id.dislikes);
        likeButton = itemView.findViewById(R.id.likeButton);
        dislikeButton = itemView.findViewById(R.id.dislikeButton);
        likeButtonOutline = itemView.findViewById(R.id.likeButtonOutline);
        dislikeButtonOutline = itemView.findViewById(R.id.dislikeButtonOutline);
    }

    public void bind(Posts post) {

        FirebaseFirestore.getInstance().collection("posts").whereEqualTo("imageUrl", post.getImageUrl()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
            }
        });
        //load the url in imageView using firebase storage;
        ImageView postImage = itemView.findViewById(R.id.post_image);
        Picasso.get().load(post.getImageUrl()).into(postImage);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView profileImage = itemView.findViewById(R.id.post_profile_image);
        String ownerUid = post.owner;
        TextView name = itemView.findViewById(R.id.post_user_name);
      FirebaseFirestore.getInstance().collection("users").whereEqualTo("uid", ownerUid).get().addOnSuccessListener(queryDocumentSnapshots -> {
            DocumentSnapshot data = queryDocumentSnapshots.getDocuments().get(0);
            Picasso.get().load(data.get("photo").toString()).into(profileImage);
            name.setText(data.get("name").toString());
        });
      TextView date = itemView.findViewById(R.id.post_date);
        date.setText(post.uploadedOn);

        likes.setText(String.valueOf(post.likes));
        dislikes.setText(String.valueOf(post.dislikes));


        likeButtonOutline.setOnClickListener(view -> {
            if(dislikeButton.getVisibility() == View.VISIBLE) {
                changeDisLike(-1);
            }
            changeLike(1);
            Toast.makeText(itemView.getContext(), "Liked", Toast.LENGTH_SHORT).show();
            likeButton.setVisibility(View.VISIBLE);
            likeButtonOutline.setVisibility(View.INVISIBLE);
            dislikeButtonOutline.setVisibility(View.VISIBLE);
            dislikeButton.setVisibility(View.INVISIBLE);
        });

        dislikeButtonOutline.setOnClickListener(view -> {
            if(likeButton.getVisibility() == View.VISIBLE) {
                changeLike(-1);
            }
            changeDisLike(1);
            Toast.makeText(itemView.getContext(), "Disliked", Toast.LENGTH_SHORT).show();
            likeButton.setVisibility(View.INVISIBLE);
            likeButtonOutline.setVisibility(View.VISIBLE);
            dislikeButtonOutline.setVisibility(View.INVISIBLE);
            dislikeButton.setVisibility(View.VISIBLE);
        });

        likeButton.setOnClickListener(view -> {
            likeButton.setVisibility(View.INVISIBLE);
            likeButtonOutline.setVisibility(View.VISIBLE);
            changeLike(-1);
        });

        dislikeButton.setOnClickListener(view -> {
            dislikeButton.setVisibility(View.INVISIBLE);
            dislikeButtonOutline.setVisibility(View.VISIBLE);
            changeDisLike(-1);
        });


        //load other details in same way
        TextView description = itemView.findViewById(R.id.post_description);
        description.setText(post.getDescription());

        String userUid = post.getOwner();
        //load user Details using this uid
    }

    private void sendDislikes() {
        int dislikes = Integer.parseInt(this.dislikes.getText().toString());
        FirebaseFirestore.getInstance().collection("posts").document(documentId)
                .update("dislikes", dislikes);
    }

    private void sendLikes() {
        int likes = Integer.parseInt(this.likes.getText().toString());
        FirebaseFirestore.getInstance().collection("posts").document(documentId)
                .update("likes", likes);
    }

    private void changeLike(int change) {
        int newLikes = Integer.parseInt(likes.getText().toString()) + change;
        likes.setText(String.valueOf(newLikes));
        sendLikes();
    }

    private void changeDisLike(int change) {
        int newDisLikes = Integer.parseInt(dislikes.getText().toString()) + change;
        dislikes.setText(String.valueOf(newDisLikes));
        sendDislikes();
    }
}
