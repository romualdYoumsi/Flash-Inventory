package com.ry.flashinventory.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ry.flashinventory.database.entry.ZoneLineEntry;

import java.util.List;

/**
 * Created by netserve on 08/12/2018.
 */

@Dao
public interface ZoneLineDao {
    @Query("SELECT * FROM zoneline")
    LiveData<List<ZoneLineEntry>> loadAllInventaireLine();

    @Query("SELECT * FROM zoneline")
    List<ZoneLineEntry> getInventaireLine();

    @Query("SELECT * FROM zoneline WHERE zone_id = :idZone ORDER BY id DESC")
    List<ZoneLineEntry> getZoneLinesByInv(Long idZone);

    @Query("SELECT * FROM zoneline WHERE zone_id = :idZone AND id < :entryId ORDER BY id DESC LIMIT :limit")
    List<ZoneLineEntry> getZoneLinesNextByInvLimit(Long idZone, Long entryId, Integer limit);

    @Query("SELECT * FROM zoneline WHERE zone_id = :idZone AND id < :entryId ORDER BY id ASC LIMIT :limit")
    List<ZoneLineEntry> getZoneLinesPrevByInvLimit(Long idZone, Long entryId, Integer limit);

    @Query("SELECT * FROM zoneline WHERE id = :id")
    List<ZoneLineEntry> getInventaireLinesById(Long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertInventaireLine(ZoneLineEntry zoneLineEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateInventaireLine(ZoneLineEntry zoneLineEntry);

    @Delete
    void deleteZoneLine(ZoneLineEntry zoneLineEntry);

    @Query("DELETE FROM zoneline WHERE zone_id = :idzone")
    void deleteZonelinesByIdZone(Long idzone);

    @Query("DELETE FROM zoneline")
    void deleteAllZoneLines();
}
