package com.internshala.loginform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {

    private EditText username,useremail,userpassword,userage;
    private Button regButton;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private ImageView userProfilePic;

    private static  int PICK_IMAGE=123;
    Uri imagepath;
    String name,password,email,age;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK && data.getData()!=null){
            imagepath=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
                userProfilePic.setImageBitmap( bitmap );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult( requestCode, resultCode, data );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_registration );

        getSupportActionBar().setTitle( "RegistrationForm" );
        setUIViews();

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        storageReference=firebaseStorage.getReference();


        userProfilePic.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(  );
                intent.setType( "image/*" );  //application*/ audio*/
                intent.setAction( Intent.ACTION_GET_CONTENT );
                startActivityForResult( Intent.createChooser( intent,"Select Image" ),PICK_IMAGE );
            }
        } );




        regButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vaildate()) {

                    //upload the data in database
                    String user_email=useremail.getText().toString().trim();
                    String user_password=userpassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                             sendemailverificatin();
                            }
                            else {
                                Toast.makeText(RegistrationActivity.this,"Registration Failed!",Toast.LENGTH_SHORT).show();
                            }

                        }
                    } );
                }
            }
        } );
        userLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( RegistrationActivity.this,MainActivity.class ) );
            }
        } );
    }
    private void setUIViews(){
        username=findViewById(R.id.etusername);
        userpassword=findViewById(R.id.etUserPassword);
        useremail=findViewById(R.id.etEmail);
        regButton=findViewById(R.id.btnregister);
        userLogin=findViewById(R.id.etuserlogin);
        userage=findViewById( R.id.etage );
        userProfilePic=findViewById( R.id.ivprofile );

    }
    private Boolean vaildate(){
        Boolean result=false;
         name=username.getText().toString();
         password=userpassword.getText().toString();
         email=useremail.getText().toString();
         age=userage.getText().toString();

        if(name.isEmpty() || password.isEmpty() || email.isEmpty() || age.isEmpty() ||imagepath==null){
            Toast.makeText(this,"Please enter all details",Toast.LENGTH_SHORT).show();

        }else {
            result=true;
        }
        return result;
    }

    private void sendemailverificatin(){
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        senddata();
                        Toast.makeText( RegistrationActivity.this,"Successfully Registered,Verification mail sent!",Toast.LENGTH_SHORT ).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity( new Intent( RegistrationActivity.this,MainActivity.class ) );
                    }
                    else {
                        Toast.makeText( RegistrationActivity.this,"Verification mail has'nt  sent!",Toast.LENGTH_SHORT ).show();
                    }
                }
            } );
        }
    }

    private void senddata(){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myref=firebaseDatabase.getReference(firebaseAuth.getUid());

        StorageReference imageref=storageReference.child( firebaseAuth.getUid() ).child( "Images" ).child( "Profile Pic" );
        UploadTask uploadTask=imageref.putFile( imagepath );
        uploadTask.addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( RegistrationActivity.this,"Upload Failed!",Toast.LENGTH_SHORT ).show();

            }
        } ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText( RegistrationActivity.this,"Upload Successfully!",Toast.LENGTH_SHORT ).show();

            }
        } );
        UserProfile userProfile=new UserProfile( age,email,name);
        myref.setValue( userProfile );
    }
}