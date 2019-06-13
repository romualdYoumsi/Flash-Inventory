package com.ry.flashinventory.pages.article.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.ry.flashinventory.database.AppDatabase;
import com.ry.flashinventory.database.entry.ArticleEntry;

import java.util.List;

/**
 * Created by netserve on 24/09/2018.
 */

public class ArticlesViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = ArticlesViewModel.class.getSimpleName();

    private LiveData<List<ArticleEntry>> articleEntries;

    public ArticlesViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
//        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        articleEntries = database.articleDao().loadAllArticle();
    }

    public LiveData<List<ArticleEntry>> getAllPanierEntries() {
        return articleEntries;
    }
}
