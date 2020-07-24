package com.internshala.loginform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePassword extends AppCompatActivity {

    private Button update;
    private EditText newPassword;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_update_password );

        getSupportActionBar().setTitle( "UpdatePassword" );

        newPassword=findViewById( R.id.etnewpassword );
        update=findViewById( R.id.btnUpdatePassword );


        getSupportActionBar().setDisplayHomeAsUpEnabled( true );


        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();


        update.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userpasswordnew=newPassword.getText().toString();
                firebaseUser.updatePassword( userpasswordnew ).addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText( UpdatePassword.this,"Password Changed",Toast.LENGTH_SHORT ).show();
                            finish();
                            startActivity( new Intent( UpdatePassword.this,ProfileActivity.class ) );
                        }
                        else {
                            Toast.makeText( UpdatePassword.this,"Password not Changed",Toast.LENGTH_SHORT ).show();
                            firebaseAuth.signOut();
                            startActivity( new Intent( UpdatePassword.this,MainActivity.class ) );

                        }
                    }
                } );
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