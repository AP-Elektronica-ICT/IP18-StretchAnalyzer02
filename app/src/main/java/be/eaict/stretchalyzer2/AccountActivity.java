package be.eaict.stretchalyzer2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
    }

    public void onClicklLogOut(View view) {
        Intent intent = new Intent( AccountActivity.this, MainActivity.class );
        startActivity( intent );
    }
}
