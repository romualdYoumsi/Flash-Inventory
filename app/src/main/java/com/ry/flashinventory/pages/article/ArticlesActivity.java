package com.ry.flashinventory.pages.article;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ry.flashinventory.R;
import com.ry.flashinventory.adapter.ArticlesAdapter;
import com.ry.flashinventory.database.AppDatabase;
import com.ry.flashinventory.database.entry.ArticleEntry;
import com.ry.flashinventory.listener.ArticlesAdapterListener;
import com.ry.flashinventory.pages.article.viewmodel.ArticlesViewModel;
import com.ry.flashinventory.pages.detailsarticle.DetailsArticleActivity;
import com.ry.flashinventory.pages.home.MainActivity;
import com.ry.flashinventory.utils.FlashInventoryUtility;

import java.util.ArrayList;
import java.util.List;

public class ArticlesActivity extends AppCompatActivity implements ArticlesAdapterListener {

    private EditText mSearchET;
    private RecyclerView mRecyclerView;
    private ImageView mScanCodeBarre;
    private ProgressBar mProgressBar;

    private AppDatabase mDb;

    //    Adapter des articles
    private ArticlesAdapter mArticlesAdapter;
    //    liste des articles affichés sur la vue
    private List<ArticleEntry> mArticleEntryList;



    //    recuperation des produits du panier
    private void loadArticles() {
        mProgressBar.setVisibility(View.VISIBLE);

        final ArticlesViewModel viewModel = ViewModelProviders.of(this).get(ArticlesViewModel.class);
        viewModel.getAllPanierEntries().observe(this, new Observer<List<ArticleEntry>>() {
            @Override
            public void onChanged(@Nullable List<ArticleEntry> panierEntries) {

//        ajout des clientParcelables dans la liste
                if (mArticleEntryList != null) {
                    mArticleEntryList.clear();
                }

                mArticleEntryList.addAll(panierEntries);
                // rafraichissement du recyclerview
                mArticlesAdapter.notifyDataSetChanged();

                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onArticleClickListener(ArticleEntry articleEntry, int position) {
//        Toast.makeText(ArticlesActivity.this, "Supprimer "+articleEntry.getIntitule(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ArticlesActivity.this, DetailsArticleActivity.class);
        intent.putExtra("articleItem", articleEntry);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        mDb = AppDatabase.getInstance(getApplicationContext());


        mRecyclerView = (RecyclerView) findViewById(R.id.recylerview_articles);
        mScanCodeBarre = (ImageView) findViewById(R.id.iv_articles_search_scan);
        mSearchET = (EditText) findViewById(R.id.et_articles_search);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_articles);


        mArticleEntryList = new ArrayList<>();

        mArticlesAdapter = new ArticlesAdapter(ArticlesActivity.this, mArticleEntryList, ArticlesActivity.this);

        GridLayoutManager mLayoutManager = new GridLayoutManager(ArticlesActivity.this, FlashInventoryUtility.calculateNoOfColumns(ArticlesActivity.this));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mArticlesAdapter);



//        ecoute de la recherche d'un article
        mSearchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchString = charSequence.toString();
//                Log.e(TAG, "onTextChanged: searchString="+searchString);
                mArticlesAdapter.performFiltering(searchString);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loadArticles();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
