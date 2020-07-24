package com.internshala.loginform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilepic;
    private TextView profilename,profileage,profileemail;
    private Button profileUpdate,changepassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile );

        getSupportActionBar().setTitle( "Profile" );


        profilepic=findViewById( R.id.ivprofilepic );
        profilename=findViewById( R.id.tvprofileName );
        profileage=findViewById( R.id.tvprofileAge );
        profileemail=findViewById( R.id.tvProfileEmail );
        profileUpdate=findViewById( R.id.btnprofileupdate );
        changepassword=findViewById( R.id.btnchangepassword );





        getSupportActionBar().setDisplayHomeAsUpEnabled( true );


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        StorageReference storageReference=firebaseStorage.getReference();
        storageReference.child( firebaseAuth.getUid()).child( "Images/Profile Pic" ).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(profilepic);
            }
        } );
        databaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile userProfile=snapshot.getValue(UserProfile.class);
                profileemail.setText( "Email:"+ userProfile.getUserEmail() );

                profileage.setText( "Age:"+ userProfile.getUserAge() );
                profilename.setText("Name:"+ userProfile.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText( ProfileActivity.this,error.getCode(),Toast.LENGTH_SHORT ).show();
            }
        } );

        profileUpdate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( ProfileActivity.this,UpdateProfile.class ) );
            }
        } );

        changepassword.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                startActivity( new Intent( ProfileActivity.this,UpdatePassword.class ) );
            }
        } );




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected( item );
    }
}