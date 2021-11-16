package com.kotlinjava.myapplication.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kotlinjava.myapplication.R;
import com.kotlinjava.myapplication.model.ContactDetails;
import com.kotlinjava.myapplication.presenter.ContactsPresenter;
import com.kotlinjava.myapplication.presenter.MyBgService;
import com.kotlinjava.myapplication.utils.Constant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ContactsRecyclerAdapter contactsRecyclerAdapter;
    private Context mContext = MainActivity.this;
    private ContactsPresenter contactsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsPresenter = new ContactsPresenter(MainActivity.this);
        contactsPresenter.initialize();

        Intent backgroundService = new Intent(MainActivity.this, MyBgService.class);
        startService(backgroundService);

        Log.v("Call", ".....................................MainActivity onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("Call", ".......................................MainActivity onDestroy");
        Intent backgroundService = new Intent(MainActivity.this, MyBgService.class);
        startService(backgroundService);
    }

    //----------------------------------------------------------------------------------------------
    public void checkPermissions() {

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.MODIFY_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                ) {

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.READ_PHONE_STATE, Manifest.permission.MODIFY_PHONE_STATE
                            },
                    Constant.ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            Log.v("Call", "if....requestPermissions()................................................");
        } else {
            Log.v("Call", "else....requestPermissions()................................................");
            //Toast.makeText(mContext, "until you get the permission,will not allow contacts", Toast.LENGTH_LONG).show();
            contactsPresenter.prepareData();
        }
    }

    //----------------------------------------------------------------------------------------------
    public void initializePageSetup() {

        recyclerView = (RecyclerView) findViewById(R.id.contacts_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

    }

    //----------------------------------------------------------------------------------------------
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constant.ASK_MULTIPLE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactsPresenter.prepareData();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constant.ASK_MULTIPLE_PERMISSION_REQUEST_CODE) {
            contactsPresenter.prepareData();
        } else {
            Toast.makeText(this, "Until you grant the permission of phone no", Toast.LENGTH_SHORT).show();
        }
    }

    //..............................................................................................
    public void removeDuplicateContacts(ArrayList<ContactDetails> selectedUsers) {

        ArrayList<ContactDetails> removed = new ArrayList<>();
        ArrayList<ContactDetails> contacts = new ArrayList<>();
        for (int i = 0; i < selectedUsers.size(); i++) {
            ContactDetails inviteFriendsProjo = selectedUsers.get(i);
            if (inviteFriendsProjo.getName().matches("\\d+(?:\\.\\d+)?") || inviteFriendsProjo.getName().trim().length() == 0) {
                removed.add(inviteFriendsProjo);
            } else {
                contacts.add(inviteFriendsProjo);
            }
        }
        contacts.addAll(removed);
        Set<ContactDetails> uniqueContacts = new HashSet<>(contacts);
        selectedUsers.clear();
        //After removing duplicates
        for (Iterator iterator = uniqueContacts.iterator(); iterator.hasNext(); ) {
            ContactDetails cont = (ContactDetails) iterator.next();
            cont.getName();
            cont.getPhone();
            selectedUsers.add(cont);
        }
    }

    //..............................................................................................
    public void showContactsRecyclerView(ArrayList<ContactDetails> selectedUsers) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contactsRecyclerAdapter = new ContactsRecyclerAdapter(inflater, mContext, selectedUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(contactsRecyclerAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
    }

    //..............................................................................................

}