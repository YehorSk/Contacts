package com.example.contacts.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.contacts.db.entity.Contact;
import com.example.contacts.db.entity.User;

@Dao
public interface UserDAO extends Dao {

    @Insert
    public long addContact(User user);
    @Query("select user_theme from user")
    public String getTheme();

    @Query("select user_order from user")
    public String getOrder();

    @Update
    public void setTheme(String theme);

    @Update
    public void setOrder(String order);

}
