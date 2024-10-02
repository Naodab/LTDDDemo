package com.example.contactversion2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContactDAO {
    @Query("Select * from Contact")
    public List<Contact> getAll();

    @Insert
    public void insert(Contact... contacts);

    @Query("SELECT EXISTS(SELECT 1 FROM Contact WHERE name = :existsName)")
    boolean isExistsName(String existsName);

    @Query("SELECT EXISTS(SELECT 1 FROM Contact WHERE phoneNumber = :existsPhone)")
    boolean isExistsPhone(String existsPhone);

    @Query("SELECT * FROM Contact WHERE name LIKE '%' || :searchName || '%'")
    List<Contact> searchContactsByName(String searchName);
}
