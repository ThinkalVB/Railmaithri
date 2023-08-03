package gov.keralapolice.railmaithri.roomDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
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
