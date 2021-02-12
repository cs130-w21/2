package com.example.pathways;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    @Query("SELECT * FROM userentity WHERE email LIKE :email")
    LiveData<UserEntity> findByEmail(String email);

    @Update
    void updateUser(UserEntity user);

    @Insert
    void insert(UserEntity user);

    @Delete
    void delete(UserEntity user);
}
