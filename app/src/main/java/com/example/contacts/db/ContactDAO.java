package com.example.contacts.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.contacts.db.entity.Contact;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ContactDAO {
    @Insert
    public long addContact(Contact contact);
    @Update
    public void updateContact(Contact contact);
    @Query("DELETE FROM contacts")
    public void deleteAll();
    @Delete
    public void deleteContact(Contact contact);
    @Query("select * from contacts order by contact_name ASC")
    public List<Contact> getContactsAsc();
    @Query("select * from contacts order by contact_name DESC")
    public List<Contact> getContactsDesc();
    @Query("select * from contacts where contact_id ==:id")
    public Contact getContact(long id);
}
