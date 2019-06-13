package com.ry.flashinventory.listener;

import com.ry.flashinventory.database.entry.ArticleEntry;

/**
 * Created by netserve on 09/12/2018.
 */

public interface ArticlesAdapterListener {
    void onArticleClickListener(ArticleEntry articleEntry, int position);
}
