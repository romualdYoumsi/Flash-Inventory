package com.ry.flashinventory.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ry.flashinventory.database.entry.ZoneEntry;

import java.util.List;

/**
 * Created by netserve on 08/12/2018.
 */

@Dao
public interface ZoneDao {
    @Query("SELECT id, code_barre, intitule, (SELECT sum(zl.quantite_line) FROM zoneline zl) quantite FROM zone ORDER By id DESC")
    LiveData<List<ZoneEntry>> loadAllZone();

    @Query("SELECT z.id id, z.code_barre code_barre, z.intitule intitule, (SELECT SUM(zl.quantite_line) FROM zoneline zl WHERE zl.zone_id=z.id) quantite FROM zone z ORDER By z.id DESC")
    List<ZoneEntry> getAllZone();

//    @Query("SELECT * FROM zone")
//    List<ZoneEntry> getAllZone();

    @Query("SELECT * FROM zone WHERE code_barre = :code_barre")
    ZoneEntry getZoneByCodebarre(String code_barre);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertZone(ZoneEntry zoneEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateInventaire(ZoneEntry articleEntry);

    @Delete
    void deleteZone(ZoneEntry zoneEntry);

    @Query("DELETE FROM zone")
    void deleteAllZones();
}
