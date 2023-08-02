package gov.keralapolice.railmaithri.roomDB;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import gov.keralapolice.railmaithri.models.LocationModel;

@Database(entities = {LocationModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskLocation taskLocation();
}
