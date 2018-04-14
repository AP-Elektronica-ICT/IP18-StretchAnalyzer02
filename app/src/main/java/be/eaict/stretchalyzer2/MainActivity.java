package be.eaict.stretchalyzer2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import be.eaict.stretchalyzer2.DOM.FBRepository;
import be.eaict.stretchalyzer2.DOM.GlobalData;
import be.eaict.stretchalyzer2.DOM.User;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        FirebaseApp.initializeApp(this);
        FBRepository.getInstance();


        mAuth = FirebaseAuth.getInstance();



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        GlobalData.currentUser = currentUser;
    }

    public void onClickRegister(View view){
        openRegisterActivity();
    }

    private void openRegisterActivity() {
        Intent intent = new Intent( MainActivity.this, RegisterActivity.class );
        startActivity( intent );
    }

    public void onClickLogin(View view) {
        checkLogin();

        //WORK IN PROGRESS
        /*
        TextView txtEmail = findViewById( R.id.txtName );
        TextView txtPassword = findViewById( R.id.txtPassword );
        email = txtEmail.getText().toString().trim();
        password = txtPassword.getText().toString().trim();
        if (checkLogin()) {
            signIn();
        }else{
            Toast.makeText(MainActivity.this, "Please fill in all forms.",
                    Toast.LENGTH_SHORT).show();
        }
        */

    }

    private boolean checkLogin() {
        boolean checked = false;

        if(email == "" || password == ""){
            checked = true;
        }

        return checked;
    }

    private void signIn(){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            GlobalData.currentUser = user;
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        }

                        // ...
                    }
                });
    }

    private void updateUI() {
        openHomeActivity();
    }

    private void openHomeActivity() {
        Intent intent = new Intent( MainActivity.this, HomeActivity.class );
        startActivity( intent );
    }

   /* private void checkLogin() {
        List<User> users = FBRepository.getInstance().getUsers();
        String name = ((EditText)findViewById(R.id.txtName)).getText().toString();
        String pass = ((EditText)findViewById(R.id.txtPassword)).getText().toString();
        for(User user : users){
            // find user with correct name
            if (user.getUsername().equals(name)){
                // if pass is correct login is ok
                if(user.getPassword().equals(pass)){
                    GlobalData.currentUser = user;

                }
                break;
            }
        }
        if(GlobalData.currentUser == null){
            Toast.makeText(this, "invalid login", Toast.LENGTH_SHORT).show();
        }
        else {
            openHomeActivity();
            finish();
        }
    }*/
}
