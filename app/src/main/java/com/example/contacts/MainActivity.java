package com.example.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contacts.adapter.ContactsAdapter;
import com.example.contacts.db.Contact;
import com.example.contacts.db.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ContactsAdapter contactsAdapter;
    ArrayList<Contact> contacts = new ArrayList<>();
    RecyclerView recyclerView;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Contacts");
        //RecyclerView
        recyclerView = findViewById(R.id.recycler_view_contacts);
        db = new DatabaseHelper(this);
        contacts.addAll(db.getAllContacts());
        contactsAdapter = new ContactsAdapter(this,contacts,MainActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditContacts(false,null,-1);
            }
        });
    }
    public void addAndEditContacts(boolean isUpdated, Contact contact, int position){
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.add_contact,null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(view);
        TextView contactTitle = view.findViewById(R.id.contactTitle);
        final EditText name_field = view.findViewById(R.id.name_field);
        final EditText email_field = view.findViewById(R.id.email_field);
        contactTitle.setText(!isUpdated ? "Add new Contact" : "Edit Contact");
        if(isUpdated && contact !=null){
            name_field.setText(contact.getName());
            email_field.setText(contact.getEmail());
        }
        alertDialogBuilder.setCancelable(false).setPositiveButton(isUpdated ? "Update" : "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(isUpdated){
                    deleteContact(contact,position);
                }else{
                    dialogInterface.cancel();
                }
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(name_field.getText().toString())){
                    Toast.makeText(MainActivity.this,"Please enter the name",Toast.LENGTH_LONG).show();
                    return;
                }else{
                    alertDialog.dismiss();
                }
                if(isUpdated && contact !=null){
                    updateContact(name_field.getText().toString(), email_field.getText().toString(), position );
                }else{
                    createContact(name_field.getText().toString(), email_field.getText().toString());
                }

            }
        });

    }
    private void deleteContact(Contact contact, int position){
        contacts.remove(position);
        db.deleteContact(contact);
        contactsAdapter.notifyDataSetChanged();
    }
    private void updateContact(String name, String email, int position){
        Contact contact =contacts.get(position);
        contact.setName(name);
        contact.setEmail(email);
        db.updateContact(contact);
        contacts.set(position,contact);
        contactsAdapter.notifyDataSetChanged();
    }
    private void createContact(String name, String email){
        long id = db.insertContact(name,email);
        Contact contact = db.getContact(id);
        if(contact != null){
            contacts.add(0, contact);
            contactsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}