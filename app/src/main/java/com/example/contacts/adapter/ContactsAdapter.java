package com.example.contacts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contacts.MainActivity;
import com.example.contacts.R;
import com.example.contacts.db.Contact;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    ArrayList<Contact> contacts;
    Context context;
    MainActivity mainActivity;
    public ContactsAdapter(Context context,ArrayList<Contact> contacts, MainActivity mainActivity){
        this.context = context;
        this.contacts = contacts;
        this.mainActivity = mainActivity;
    }
    @NonNull
    @Override
    public ContactsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_item_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.MyViewHolder holder, int position) {
        final Contact contact = contacts.get(position);
        holder.contact_name.setText(contact.getName());
        holder.contact_email.setText(contact.getEmail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.addAndEditContacts(true,contact,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
         ImageView contact_img;
         TextView contact_name;
         TextView contact_email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            contact_img = itemView.findViewById(R.id.image_profile);
            contact_name = itemView.findViewById(R.id.text_name);
            contact_email = itemView.findViewById(R.id.text_email);
        }
    }
}
