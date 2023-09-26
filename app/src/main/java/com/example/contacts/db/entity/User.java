package com.example.contacts.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @ColumnInfo(name = "user_theme")
    private String theme = "white";
    @ColumnInfo(name = "user_order")
    private String contacts_order = "desc";
    @ColumnInfo(name = "user_id")
    @PrimaryKey(autoGenerate = true)
    private int id = 1;
    @Ignore
    public User(){

    }

    public User(String theme, String contacts_order, int id) {
        this.theme = theme;
        this.contacts_order = contacts_order;
        this.id = id;
    }

    public String getTheme() {
        return theme;
    }

    public String getContacts_order() {
        return contacts_order;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setContacts_order(String contacts_order) {
        this.contacts_order = contacts_order;
    }
}
