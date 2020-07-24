package com.internshala.loginform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class UpdateProfile extends AppCompatActivity {

    private EditText newusername,newuseremail,newuserage;
    private Button save;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private ImageView updateprofile;
    private FirebaseStorage firebaseStorage;



    private static  int PICK_IMAGE=123;
    Uri imagepath;


    private StorageReference storageReference;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK && data.getData()!=null){
            imagepath=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
                updateprofile.setImageBitmap( bitmap );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult( requestCode, resultCode, data );
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_update_profile );
        getSupportActionBar().setTitle( "UpdateProfile" );

        newusername=findViewById( R.id.etNameupdate );
        newuseremail=findViewById( R.id.etemailupdate );
        newuserage=findViewById( R.id.etageupdate );
        save=findViewById( R.id.btnsaveupdate );
        updateprofile=findViewById( R.id.ivprofileupdate );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();

        final DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        databaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile userProfile=snapshot.getValue(UserProfile.class);
                newuseremail.setText( userProfile.getUserEmail() );

                newuserage.setText( userProfile.getUserAge() );
                newusername.setText(userProfile.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText( UpdateProfile.this,error.getCode(),Toast.LENGTH_SHORT ).show();
            }
        } );

        firebaseStorage=FirebaseStorage.getInstance();

        final     StorageReference storageReference=firebaseStorage.getReference();
        storageReference.child( firebaseAuth.getUid()).child( "Images/Profile Pic" ).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(updateprofile);
            }
        } );

        save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=newusername.getText().toString();
                String email=newuseremail.getText().toString();
                String age=newuserage.getText().toString();

                UserProfile userProfile=new UserProfile( age,email,name );

                databaseReference.setValue( userProfile );
                StorageReference imageref=storageReference.child( firebaseAuth.getUid() ).child( "Images" ).child( "Profile Pic" );
                UploadTask uploadTask=imageref.putFile( imagepath );
                uploadTask.addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( UpdateProfile.this,"Upload Failed!",Toast.LENGTH_SHORT ).show();

                    }
                } ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText( UpdateProfile.this,"Upload Successfully!",Toast.LENGTH_LONG ).show();

                    }
                } );
                finish();
                startActivity( new Intent( UpdateProfile.this,SecondActivity.class ) );
            }
        } );


        updateprofile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(  );
                intent.setType( "image/*" );  //application*/ audio*/
                intent.setAction( Intent.ACTION_GET_CONTENT );
                startActivityForResult( Intent.createChooser( intent,"Select Image" ),PICK_IMAGE );


            }
        } );

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();

        }
        return super.onOptionsItemSelected( item );
    }
}