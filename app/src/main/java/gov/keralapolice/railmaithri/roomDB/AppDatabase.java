package gov.keralapolice.railmaithri.roomDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import gov.keralapolice.railmaithri.models.LocationModel;

@Database(entities = {LocationModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskLocation taskLocation();
}
