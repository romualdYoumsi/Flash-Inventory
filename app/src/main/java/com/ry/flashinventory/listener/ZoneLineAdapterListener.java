package com.ry.flashinventory.listener;

import com.ry.flashinventory.database.entry.ZoneEntry;
import com.ry.flashinventory.database.entry.ZoneLineEntry;

/**
 * Created by netserve on 16/01/2019.
 */

public interface ZoneLineAdapterListener {
    void onDeleteZoneLine(ZoneEntry zoneEntry, int position);
}
