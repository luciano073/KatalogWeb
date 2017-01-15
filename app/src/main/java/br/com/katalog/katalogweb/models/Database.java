package br.com.katalog.katalogweb.models;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.katalog.katalogweb.listeners.OnGetDataListener;

/**
 * Created by luciano on 07/01/2017.
 */

public class Database {
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;
    private DatabaseReference mDatabaseReference;

    public Database() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void setValueEventListener(ValueEventListener valueEventListener) {
        this.valueEventListener = valueEventListener;
    }

    public void setChildEventListener(ChildEventListener childEventListener) {
        this.childEventListener = childEventListener;
    }

    public ValueEventListener getValueEventListener() {
        return valueEventListener;
    }

    public ChildEventListener getChildEventListener() {
        return childEventListener;
    }

    public void mReadDataOnce(String child, final OnGetDataListener listener) {
        listener.onStart();
        this.valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        };
        FirebaseDatabase.getInstance().getReference().child(child)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public void removeValueListener(){
        mDatabaseReference.removeEventListener(this.valueEventListener);
    }

    public void removeChildListener(){
        mDatabaseReference.removeEventListener(this.childEventListener);
    }

}
