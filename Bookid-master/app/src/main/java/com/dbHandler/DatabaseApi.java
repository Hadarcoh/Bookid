package com.dbHandler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DatabaseApi {

    private static DatabaseApi instance;
    private DatabaseReference mDatabase;
    private static final String mainTblName = "booksproject-41fe3";
    private static final String booksTblName = "books";

    private DatabaseApi(){
        //get database ref
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized DatabaseApi getInstance(){
        if(instance == null){
            instance = new DatabaseApi();
        }
        return instance;
    }

    public boolean checkIfUserExistInDb(String uid, DataSnapshot dataSnapshot){
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            if(ds.getKey().equals(uid)) {
                return true;
            }
        }
        return false;
    }
}