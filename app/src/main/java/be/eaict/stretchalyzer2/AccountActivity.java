package be.eaict.stretchalyzer2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import be.eaict.stretchalyzer2.DOM.GlobalData;

public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        CheckLoggedIn();
    }

    private void CheckLoggedIn() {
        TextView txtLoggedIn = findViewById( R.id.txtLoggedIn );
        String user = GlobalData.currentUser.getEmail();
        txtLoggedIn.setText("Logged in as " + user);
    }

    public void onClicklLogOut(View view) {
        Intent intent = new Intent( AccountActivity.this, MainActivity.class );
        startActivity( intent );
        GlobalData.currentUser = null;
        finish();
    }
}
