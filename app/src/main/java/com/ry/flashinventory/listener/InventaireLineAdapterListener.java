package com.ry.flashinventory.listener;

import com.ry.flashinventory.database.entry.ZoneLineEntry;

/**
 * Created by netserve on 09/12/2018.
 */

public interface InventaireLineAdapterListener {
    void onDeleteInventaireLine(ZoneLineEntry zoneLineEntry, int position);
}
