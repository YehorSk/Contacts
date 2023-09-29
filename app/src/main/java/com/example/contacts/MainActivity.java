package com.example.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contacts.adapter.ContactsAdapter;
import com.example.contacts.db.ContactsAppDatabase;
import com.example.contacts.db.entity.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ContactsAdapter contactsAdapter;
    ArrayList<Contact> contacts = new ArrayList<>();
    RecyclerView recyclerView;
    Button btn;
    private ContactsAppDatabase contactsAppDatabase;
    private final String SHARED_PREFS = "shared_prefs";
    private String RecyclerViewOrder = "asc";
    private String NightMode = "false";
    private String order;
    private Boolean mode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
        if(mode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Contacts");

        //Database
        contactsAppDatabase = Room.databaseBuilder(getApplicationContext(),
                ContactsAppDatabase.class,
                "ContactDB").allowMainThreadQueries().build();

        //RecyclerView
        recyclerView = findViewById(R.id.recycler_view_contacts);
        displayContactsInBg(order);
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
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Contact contact = contacts.get(viewHolder.getAdapterPosition());
                deleteContact(contact,viewHolder.getAdapterPosition());
                Toast.makeText(MainActivity.this,"Contact deleted",Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(recyclerView);


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
        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
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
        contactsAppDatabase.getContactDAO().deleteContact(contact);
        contactsAdapter.notifyDataSetChanged();
    }
    private void updateContact(String name, String email, int position){
        Contact contact =contacts.get(position);
        contact.setName(name);
        contact.setEmail(email);
        contactsAppDatabase.getContactDAO().updateContact(contact);
        contacts.set(position,contact);
        contactsAdapter.notifyDataSetChanged();
    }
    private void createContact(String name, String email){
        long id = contactsAppDatabase.getContactDAO().addContact(new Contact(name,email,0));
        Contact contact = contactsAppDatabase.getContactDAO().getContact(id);
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

    public void changeOrderSaveData(String order){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RecyclerViewOrder, order);
        editor.apply();
    }
    public void modeSaveData(Boolean mode){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NightMode, mode);
        editor.apply();
    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        order = sharedPreferences.getString(RecyclerViewOrder,"");
        mode = sharedPreferences.getBoolean(NightMode,false);
    }

    private void displayContactsInBg(String order){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if(order.equals("desc")){
                    contacts.addAll(contactsAppDatabase.getContactDAO().getContactsDesc());
                }else{
                    contacts.addAll(contactsAppDatabase.getContactDAO().getContactsAsc());
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        contactsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            return true;
        }else if(id == R.id.action_order_asc){
            changeOrderSaveData("asc");
            finish();
            startActivity(getIntent());
        }else if(id == R.id.action_order_desc){
            changeOrderSaveData("desc");
            finish();
            startActivity(getIntent());
        }else if(id == R.id.mode){
            if(mode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            modeSaveData(!mode);
        }
        return super.onOptionsItemSelected(item);
    }
}