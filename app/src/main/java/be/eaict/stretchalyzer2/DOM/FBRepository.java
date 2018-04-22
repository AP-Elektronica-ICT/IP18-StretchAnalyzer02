package be.eaict.stretchalyzer2.DOM;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kevin-Laptop on 1/03/2018.
 */

public class FBRepository {
    DatabaseReference databaseFXDatapoint;
    List<Double> angles = new ArrayList<>();
    List<fxDatapoint> datapointList = new ArrayList<>();
    Boolean canceled;

    public DatabaseReference instantiate(){
        databaseFXDatapoint = FirebaseDatabase.getInstance().getReference( "fxdatapoint" );
        return databaseFXDatapoint;
    }

    public void SaveToDatabase(int mSec, List<Double> angles) {
        String id = databaseFXDatapoint.push().getKey();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat( "dd-MMM-yyyy HH:mm:ss" );
        String datum = df.format( c );

        fxDatapoint datapoint = new fxDatapoint( id, mSec, angles, datum, GlobalData.currentUser.getEmail() );
        databaseFXDatapoint.child( id ).setValue( datapoint );
    }


    public List<fxDatapoint> ReadFromDatabase() {
        // Read from the database
        databaseFXDatapoint.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children) {
                    fxDatapoint retDatapoint = child.getValue( fxDatapoint.class );
                    datapointList.add( retDatapoint );
                }
                canceled = false;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w( "database", "Failed to read data.", error.toException() );
                canceled = true;
            }
        } );
        if(canceled){
            return null;
        }
        else{
            return datapointList;
        }
    }
}
