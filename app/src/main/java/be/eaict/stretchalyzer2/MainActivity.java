package be.eaict.stretchalyzer2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import be.eaict.stretchalyzer2.DOM.FBRepository;
import be.eaict.stretchalyzer2.DOM.GlobalData;
import be.eaict.stretchalyzer2.DOM.User;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

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
        //checkLogin();
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
