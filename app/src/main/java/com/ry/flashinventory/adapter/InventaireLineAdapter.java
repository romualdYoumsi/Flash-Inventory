package com.ry.flashinventory.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ry.flashinventory.R;
import com.ry.flashinventory.database.AppDatabase;
import com.ry.flashinventory.database.entry.ArticleEntry;
import com.ry.flashinventory.database.entry.ZoneLineEntry;
import com.ry.flashinventory.listener.InventaireLineAdapterListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by netserve on 09/12/2018.
 */

public class InventaireLineAdapter extends RecyclerView.Adapter<InventaireLineAdapter.InventaireLineViewHolder> {
    private static final String TAG = InventaireLineAdapter.class.getSimpleName();

    private int mCurrentPage;
    private int mPageLimit;
    private int mCountLines;
    private Context mContext;
    private List<ZoneLineEntry> zoneLinesDisplayed;
    private InventaireLineAdapterListener mListener;

    private AppDatabase mDb;

    //    ViewHolder de l'adapter
    public class InventaireLineViewHolder extends RecyclerView.ViewHolder {
        public TextView code_barre, quantite, statut;
        public ImageView delete;
        public View lview;

        public InventaireLineViewHolder(View view) {
            super(view);
            code_barre = view.findViewById(R.id.tv_iteminventaireline_codebarre);
            quantite = view.findViewById(R.id.tv_iteminventaireline_quantite);
            statut = view.findViewById(R.id.tv_iteminventaireline_statut);
            delete = view.findViewById(R.id.iv_iteminventaireline_delete);
            lview = view;

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    mListener.onDeleteInventaireLine(zoneLinesDisplayed.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    //    Filtre la liste des produits
    public void releaseData(List<ZoneLineEntry> zoneLines) {
        this.zoneLinesDisplayed.clear();
        if (zoneLines.size() <= mPageLimit) {
            this.zoneLinesDisplayed.addAll(zoneLines);
        } else {
            this.zoneLinesDisplayed.addAll(zoneLines.subList(0, mPageLimit));
        }
        this.mCountLines = zoneLines.size();
        this.mCurrentPage = 1;
        Log.e(TAG, "releaseData: zoneLinesDisplayed size=" + zoneLinesDisplayed.size());
        notifyDataSetChanged();
    }

    //    Filtre la liste des produits
    public void releaseNextData(List<ZoneLineEntry> zoneLines) {
//        Log.e(TAG, "releasePrecData: zoneLines size=" + zoneLines.size() + " mCurrentPage=" + this.mCurrentPage);
        if (zoneLines.size() <= 0) {
            return;
        }

        this.mCountLines = zoneLines.size();
        if (zoneLines.size() <= mPageLimit) {
            this.zoneLinesDisplayed.clear();
            this.zoneLinesDisplayed.addAll(zoneLines);
        } else {
            int fromIndex = this.mPageLimit * this.mCurrentPage;
            int toIndex = fromIndex + this.mPageLimit;
//            Log.e(TAG, "releaseNextData: fromIndex=" + fromIndex + " toIndex=" + toIndex);
            if (fromIndex >= 0 && fromIndex < zoneLines.size()) {
                if (toIndex < zoneLines.size()) {
                    this.zoneLinesDisplayed.clear();
                    this.zoneLinesDisplayed.addAll(zoneLines.subList(fromIndex, toIndex));

                    this.mCurrentPage += 1;
                } else {
                    toIndex = zoneLines.size();
//                    Log.e(TAG, "releaseNextData: zoneLines second size=" + zoneLines.size() + " fromIndex=" + fromIndex + " toIndex=" + toIndex);
                    this.zoneLinesDisplayed.clear();
                    this.zoneLinesDisplayed.addAll(zoneLines.subList(fromIndex, toIndex));

                    this.mCurrentPage += 1;
                }
            } else {
//                this.mCurrentPage -= 1;
//                this.zoneLinesDisplayed.clear();
                Toast.makeText(mContext, "Fin de la liste", Toast.LENGTH_SHORT).show();
            }
        }
//        Log.e(TAG, "releaseNextData: size=" + this.zoneLinesDisplayed.size());
        notifyDataSetChanged();
    }

    //    Filtre la liste des produits
    public void releasePrecData(List<ZoneLineEntry> zoneLines) {
//        Log.e(TAG, "releasePrecData: zoneLines size=" + zoneLines.size() + " mCurrentPage=" + this.mCurrentPage);
        if (zoneLines.size() <= 0) {
            return;
        }

        this.mCountLines = zoneLines.size();
        if (zoneLines.size() <= mPageLimit) {
            this.zoneLinesDisplayed.clear();
            this.zoneLinesDisplayed.addAll(zoneLines);
        } else {
            this.mCurrentPage -= 1;
            int fromIndex = this.mCountLines - (this.mPageLimit * this.mCurrentPage);
            int toIndex = fromIndex + this.mPageLimit;
            Log.e(TAG, "releasePrecData: fromIndex=" + fromIndex + " toIndex=" + toIndex);
            if (fromIndex >= 0 && fromIndex < zoneLines.size()) {
                if (toIndex < zoneLines.size()) {
                    this.zoneLinesDisplayed.clear();
                    this.zoneLinesDisplayed.addAll(zoneLines.subList(fromIndex, toIndex));
                } else {
                    toIndex = zoneLines.size();
                    Log.e(TAG, "releasePrecData: zoneLines second size=" + zoneLines.size() + " fromIndex=" + fromIndex + " toIndex=" + toIndex);
                    this.zoneLinesDisplayed.clear();
                    this.zoneLinesDisplayed.addAll(zoneLines.subList(fromIndex, toIndex));
                }
            } else {
                this.mCurrentPage += 1;
//                this.zoneLinesDisplayed.clear();
                Toast.makeText(mContext, "Tête de la liste", Toast.LENGTH_SHORT).show();
            }
        }
        Log.e(TAG, "releasePrecData: size=" + this.zoneLinesDisplayed.size());
        notifyDataSetChanged();
    }

    public InventaireLineAdapter(Context context, InventaireLineAdapterListener listener, int pageLimit) {
        this.mContext = context;
        this.zoneLinesDisplayed = new ArrayList<>();
        this.mListener = listener;
        this.mPageLimit = pageLimit;
        this.mCurrentPage = 1;


        mDb = AppDatabase.getInstance(mContext.getApplicationContext());
    }

    @Override
    public InventaireLineAdapter.InventaireLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_inventaire_line, parent, false);

        return new InventaireLineAdapter.InventaireLineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final InventaireLineAdapter.InventaireLineViewHolder holder, int position) {

//        Log.e(TAG, "onBindViewHolder: position=" + position + " size=" + zoneLinesDisplayed.size());
        if (position < zoneLinesDisplayed.size()) {

            int rowNum = (this.mCountLines - (this.mPageLimit * (this.mCurrentPage - 1))) - position;
            if (rowNum > 0) {
                ArticleEntry articleEntry = mDb.articleDao().getArticleById(this.zoneLinesDisplayed.get(position).getArticle_id());
                holder.code_barre.setText(String.format("%s.  %s", rowNum, articleEntry.getCode_barre()));
                holder.quantite.setText("" + Integer.valueOf(this.zoneLinesDisplayed.get(position).getQuantite_line()));
                holder.statut.setText(articleEntry.getStatut());

                int r = position % 2;
//            Log.e(TAG, "onBindViewHolder: getAdapterPosition=" + position + " r=" + r);
                if (r == 1) {
                    holder.lview.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
                } else {
                    holder.lview.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
                }

                if (articleEntry.getStatut().toLowerCase().equals("absent")) {
                    holder.statut.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                } else {
                    holder.statut.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (zoneLinesDisplayed != null) {
            return zoneLinesDisplayed.size();
        }
        return 0;
    }
}
