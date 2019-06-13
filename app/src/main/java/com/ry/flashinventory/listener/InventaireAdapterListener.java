package com.ry.flashinventory.listener;

import com.ry.flashinventory.database.entry.ZoneEntry;

/**
 * Created by netserve on 09/12/2018.
 */

public interface InventaireAdapterListener {
    void onInventaireClick(ZoneEntry zoneEntry, int position);
}
