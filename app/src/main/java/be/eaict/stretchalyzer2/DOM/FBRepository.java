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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kevin-Laptop on 1/03/2018.
 */

public class FBRepository implements IRepository {

    private static FBRepository repo = null;
    protected HashMap<Integer, User> userCache;
    protected Integer nextUserId;
    protected HashMap<Integer, fxDatapoint> fxDataPointCache;
    protected Integer nextfxDataPointId;

    public static FBRepository getInstance() {
        if (repo == null) {
            repo = new FBRepository();
        }
        return repo;
    }

    public FBRepository() {
        userCache = new HashMap<>();
        fxDataPointCache = new HashMap<>();
        nextUserId = 0;
        nextfxDataPointId = 0;

        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();


        //fill fxDataPointCache
        final DatabaseReference fxDataPointRef = fbdb.getReference("fxDataPoint");
        fxDataPointRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    fxDataPointCache.put(Integer.valueOf(snap.getKey()), snap.getValue(fxDatapoint.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        //add listener to update fxDataPointCache on change
        fxDataPointRef.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Integer id = Integer.valueOf( dataSnapshot.getKey());
                FBRepository.getInstance().fxDataPointCache.put( id, dataSnapshot.getValue(fxDatapoint.class));
                if(id>=nextfxDataPointId){
                    nextfxDataPointId = id+1;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FBRepository.getInstance().fxDataPointCache.put(
                        Integer.valueOf( dataSnapshot.getKey()),
                        dataSnapshot.getValue(fxDatapoint.class));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                FBRepository.getInstance().fxDataPointCache.remove( Integer.valueOf(dataSnapshot.getKey()));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );


        // fill usercache
        DatabaseReference userRef = fbdb.getReference( "users" );
        userRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    userCache.put( Integer.valueOf( snap.getKey() ), snap.getValue( User.class ) );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );
        // add listener to update usercache on change
        userRef.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevName) {
                Integer id = Integer.valueOf( dataSnapshot.getKey() );
                FBRepository.getInstance().userCache.put(
                        Integer.valueOf( id ),
                        dataSnapshot.getValue( User.class ) );
                if (id >= nextUserId) {
                    nextUserId = id + 1;
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                FBRepository.getInstance().userCache.remove( Integer.valueOf( dataSnapshot.getKey() ) );
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String prevName) {
                Integer id = Integer.valueOf( dataSnapshot.getKey() );
                FBRepository.getInstance().userCache.put(
                        Integer.valueOf( id ),
                        dataSnapshot.getValue( User.class ) );
                if (id >= nextUserId) {
                    nextUserId = id + 1;
                }
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String prevName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );
    }

    //get fxDataPoint by ID
    public fxDatapoint getFxDataPoint(int id){
        return fxDataPointCache.get(id);
    }

    //get all fxDataPoints
    @Override
    public List<fxDatapoint> getFxDataPoints(){
        return new ArrayList<>( fxDataPointCache.values() );
    }

    //get fxDatapoints per date
    @Override
    public List<fxDatapoint> getFxDataPoints(Date datum){
        ArrayList<fxDatapoint> pointList = new ArrayList<>();
        pointList.add(fxDataPointCache.get(datum));
        return pointList;
    }


    // get single user by name
  /*  public User getUser(String userName) {
        for (User user : userCache.values()) {
            if (user.getUsername().equals( userName )) {
                return user;
            }
        }
        return null;
    }*/

    // get single user
    public User getUser(Integer userId) {
        return userCache.get( userId );
    }

    // get all users
    @Override
    public List<User> getUsers() {
        return new ArrayList<>( userCache.values() );
    }

    // get users by ID
    @Override
    public List<User> getUsers(List<Integer> idList) {
        ArrayList<User> retList = new ArrayList<>();
        for (Integer id : idList) {
            retList.add( userCache.get( id ) );
        }
        return retList;
    }

    public void createFxDataPoint(fxDatapoint fxdatapoint){
        createOrUpdateFxDataPoint(fxdatapoint);
    }

    public void updateFxDataPoint(fxDatapoint fxdatapoint){
        createOrUpdateFxDataPoint(fxdatapoint);
    }

    public void createOrUpdateFxDataPoint(fxDatapoint fxdataPoint){
        if(fxdataPoint.getId() == -1){
            fxdataPoint.setId( nextfxDataPointId );
            nextfxDataPointId++;
            fxDataPointCache.put( fxdataPoint.getId(), fxdataPoint );
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("FxDataPoint");
        ref.child( Integer.toString( fxdataPoint.getId())).setValue(fxdataPoint);
    }


    //just use createorupdate instead
    @Override
    public void createUser(User user) {
        createOrUpdateUser( user );
    }

    //just use createorupdate instead
    @Override
    public void updateUser(User user) {
        createOrUpdateUser( user );
    }

    // insert new user or update existing user
    @Override
    public void createOrUpdateUser(User user) {
        if (user.getId() == -1) {
            user.setId( nextUserId );
            nextUserId++;
            userCache.put( user.getId(), user );
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "users" );
        ref.child( Integer.toString( user.getId() ) ).setValue( user );
    }

}
