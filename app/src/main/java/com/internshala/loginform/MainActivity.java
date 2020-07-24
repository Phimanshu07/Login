package com.internshala.loginform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText Name;
    private EditText Password;
    private TextView Info,forgotpassword;
    private Button Login;
    private TextView userregistration;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private int counter=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        getSupportActionBar().setTitle( "LoginForm" );

        Name = findViewById( R.id.etName );
        Password = findViewById( R.id.etPassword );
        Info = findViewById( R.id.info );
        Login = findViewById( R.id.btnlogin );
        userregistration=findViewById(R.id.tvregister);
        forgotpassword=findViewById( R.id.tvforgotpassword );

        Info.setText("Number of Attempt remaining: 5");
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);

        FirebaseUser user=firebaseAuth.getCurrentUser();

        if(user!=null){
            finish();
            startActivity(new Intent(MainActivity.this,SecondActivity.class));

        }


        userregistration.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( MainActivity.this,RegistrationActivity.class ) );
            }
        } );

        Login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(),Password.getText().toString());
            }
        } );


        forgotpassword.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( MainActivity.this,PasswordActivity.class ) );
            }
        } );
    }

    private void validate(String username, String userpassword) {
        if(username.isEmpty() || userpassword.isEmpty()){
            Toast.makeText( MainActivity.this,"Please Enter Email And Password For Login !",Toast.LENGTH_SHORT ).show();
        }
        else {
            progressDialog.setMessage( "Wait" );
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword( username, userpassword ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();

                        checkemailverification();
                    } else {
                        Toast.makeText( MainActivity.this, "Login failed or Enter wrong Email and Password", Toast.LENGTH_SHORT ).show();
                        counter--;
                        Info.setText( "Number of Attempt remaining: " + counter );
                        progressDialog.dismiss();
                        if (counter == 0) {
                            Login.setEnabled( false );
                        }
                    }
                }
            } );
        }
    }

    private void checkemailverification(){
        FirebaseUser firebaseUser=firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag=firebaseUser.isEmailVerified();

        if(emailflag){
            finish();
            startActivity( new Intent( MainActivity.this,SecondActivity.class ) );
        }
        else {
            Toast.makeText( this,"Verify your email",Toast.LENGTH_SHORT ).show();
            firebaseAuth.signOut();
        }
    }


}