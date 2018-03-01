package be.eaict.stretchalyzer2.DOM;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kevin-Laptop on 1/03/2018.
 */

public class FBRepository implements IRepository {

    private static FBRepository repo = null;
    protected HashMap<Integer, User> userCache;
    protected Integer nextUserId;


    public static FBRepository getInstance() {
        if (repo == null) {
            repo = new FBRepository();
        }
        return repo;
    }

    public FBRepository(){
        userCache = new HashMap<>();
        nextUserId = 0;

        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();


        // fill usercache
        DatabaseReference userRef = fbdb.getReference("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    userCache.put(Integer.valueOf(snap.getKey()), snap.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // add listener to update usercache on change
        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevName) {
                Integer id = Integer.valueOf(dataSnapshot.getKey());
                FBRepository.getInstance().userCache.put(
                        Integer.valueOf(id),
                        dataSnapshot.getValue(User.class));
                if(id >= nextUserId){
                    nextUserId = id+1;
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                FBRepository.getInstance().userCache.remove(Integer.valueOf(dataSnapshot.getKey()));
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String prevName) {
                Integer id = Integer.valueOf(dataSnapshot.getKey());
                FBRepository.getInstance().userCache.put(
                        Integer.valueOf(id),
                        dataSnapshot.getValue(User.class));
                if(id >= nextUserId){
                    nextUserId = id+1;
                }
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String prevName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    // get single user by name
    public User getUser(String userName) {
        for (User user: userCache.values()) {
            if(user.getUsername().equals(userName)){
                return user;
            }
        }
        return null;
    }

    // get single user
    public User getUser(Integer userId) {
        return userCache.get(userId);
    }
    // get all users
    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userCache.values());
    }

    // get users by ID
    @Override
    public List<User> getUsers(List<Integer> idList) {
        ArrayList<User> retList = new ArrayList<>();
        for(Integer id: idList){
            retList.add(userCache.get(id));
        }
        return retList;
    }

    //just use createorupdate instead
    @Override
    public void createUser(User user) {
        createOrUpdateUser(user);
    }

    //just use createorupdate instead
    @Override
    public void updateUser(User user) {
        createOrUpdateUser(user);
    }

    // insert new user or update existing user
    @Override
    public void createOrUpdateUser(User user) {
        if(user.getId() == -1){
            user.setId(nextUserId);
            nextUserId++;
            userCache.put(user.getId(), user);
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(Integer.toString(user.getId())).setValue(user);
    }

}
