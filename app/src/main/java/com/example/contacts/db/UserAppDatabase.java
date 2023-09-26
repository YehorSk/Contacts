package com.example.contacts.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.contacts.db.entity.Contact;
import com.example.contacts.db.entity.User;

@Database(entities = {User.class},version = 1)
public abstract class UserAppDatabase extends RoomDatabase {
    public abstract UserDAO getUserDAO();
}
