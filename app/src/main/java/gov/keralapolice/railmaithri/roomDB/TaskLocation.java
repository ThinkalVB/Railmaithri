package gov.keralapolice.railmaithri.roomDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import gov.keralapolice.railmaithri.models.LocationModel;

@Dao
public interface TaskLocation {

    @Query("SELECT * FROM locationmodel")
    List<LocationModel> getAll();

    @Insert
    void insert(LocationModel task);

    @Delete
    void delete(LocationModel task);

    @Query("DELETE FROM locationmodel")
    public void clearTable();

}
