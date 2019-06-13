package com.ry.flashinventory.listener;

import java.util.List;

/**
 * Created by netserve on 10/12/2018.
 */

public interface ParserWriteListener {
    void onWriteComplete(String statut, List<String> pathFiles);
}
