package com.ry.flashinventory.pages.detailsarticle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.ry.flashinventory.R;
import com.ry.flashinventory.database.AppDatabase;
import com.ry.flashinventory.database.entry.ArticleEntry;
import com.ry.flashinventory.database.entry.ZoneEntry;

import java.util.List;

public class DetailsArticleActivity extends AppCompatActivity {
    private static final String TAG = DetailsArticleActivity.class.getSimpleName();

    private AppDatabase mDb;

    private ArticleEntry mArticleEntry;
    private List<ZoneEntry> mZoneEntries;

    private TextView mIntituleTV, mCodeArticleTV, mCodeBarreTV, mPrixRevientTV, mQuantiteTV, mZonesTV;

    private void initValues() {
        mIntituleTV.setText(this.mArticleEntry.getIntitule());
        mCodeArticleTV.setText(this.mArticleEntry.getCode_article());
        mCodeBarreTV.setText(this.mArticleEntry.getCode_barre());
        mPrixRevientTV.setText(String.format("%s XAF", (this.mArticleEntry.getPrix_revient() != null ? this.mArticleEntry.getPrix_revient() : 0)));
        mQuantiteTV.setText(String.format("%s Unités", (this.mArticleEntry.getQuantite() != null ? this.mArticleEntry.getQuantite() : 0)));

        String str = "";
        for (ZoneEntry zoneEntry : this.mZoneEntries) {
                str += String.format("%s •", zoneEntry.getIntitule());
        }
        if (str.length() >2) {
            mZonesTV.setText(str.substring(0, str.length()-2));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_article);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.mArticleEntry = getIntent().getExtras().getParcelable("articleItem");
        }

        if (this.mArticleEntry == null) {
            finish();
        }

        mDb = AppDatabase.getInstance(getApplicationContext());

        mIntituleTV = findViewById(R.id.tv_detailsarticle_intitule);
        mCodeArticleTV = findViewById(R.id.tv_detailsarticle_codearticle);
        mCodeBarreTV = findViewById(R.id.tv_detailsarticle_codebarre);
        mPrixRevientTV = findViewById(R.id.tv_detailsarticle_prixrevient);
        mQuantiteTV = findViewById(R.id.tv_detailsarticle_quantite);
        mZonesTV = findViewById(R.id.tv_detailsarticle_zones);

        mZoneEntries = mDb.articleDao().getZonesArticle(mArticleEntry.getId());

        initValues();
        Log.e(TAG, "onCreate: intitule="+this.mArticleEntry.getIntitule()+" codeBarre="+this.mArticleEntry.getCode_barre()+" mZoneEntries="+mZoneEntries.size());
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
