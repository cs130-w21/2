package com.example.pathways;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    @Query("SELECT * FROM userentity WHERE email LIKE :email")
    UserEntity findByEmail(String email);

    @Update
    public void updateUser(UserEntity user);

    @Insert
    void insertAll(UserEntity... users);

    @Delete
    void delete(UserEntity user);
}
