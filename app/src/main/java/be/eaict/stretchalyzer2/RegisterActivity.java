package be.eaict.stretchalyzer2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import be.eaict.stretchalyzer2.DOM.GlobalData;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private String repeatPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );
        this.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();

        TextView txtAlreadyAcc = findViewById( R.id.txtAlreadyAcc );
        txtAlreadyAcc.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );


    }

    public void onClickConfirm(View view) {
        if (checkFields()) {
            if (password == repeatPassword) {
                Toast.makeText( RegisterActivity.this, "Passwords do not match",
                        Toast.LENGTH_SHORT ).show();
            } else {
                if (checkPassword()) {
                    if(isEmailValid(email)){
                        createAccount();
                    }
                }
            }
        } else {
            Toast.makeText( RegisterActivity.this, "Please fill out all fields",
                    Toast.LENGTH_SHORT ).show();
        }

    }

    private boolean checkFields() {
        boolean checked = true;

        TextView txtEmail = findViewById( R.id.txtName );
        email = txtEmail.getText().toString().trim();

        TextView txtPassword = findViewById( R.id.txtPassword );
        password = txtPassword.getText().toString().trim();

        TextView txtRepeatPassword = findViewById( R.id.txtRepeatPassword );
        repeatPassword = txtRepeatPassword.getText().toString().trim();

        if (email == "" || password == "" || repeatPassword == "") {
            checked = false;
        }

        return checked;
    }

    private boolean checkPassword() {
        boolean checked = false;

        if (password.length() < 6) {
            Toast.makeText( RegisterActivity.this, "Password needs at least 6 characters.",
                    Toast.LENGTH_SHORT ).show();
        } else {
            checked = true;
        }
        return checked;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher( email ).matches();
    }

    private void createAccount() {
        mAuth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d( "register", "createUserWithEmail:success" );
                            FirebaseUser user = mAuth.getCurrentUser();
                            GlobalData.currentUser = user;
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w( "register", "createUserWithEmail:failure", task.getException() );
                            Toast.makeText( RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT ).show();
                        }

                        // ...
                    }
                } );
    }

    private void updateUI() {
        openHomeActivity();
    }

    private void openHomeActivity() {
        Intent intent = new Intent( RegisterActivity.this, HomeActivity.class );
        startActivity( intent );
    }
}
