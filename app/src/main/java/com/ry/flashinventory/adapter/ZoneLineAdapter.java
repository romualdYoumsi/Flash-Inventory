package com.ry.flashinventory.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ry.flashinventory.R;
import com.ry.flashinventory.database.AppDatabase;
import com.ry.flashinventory.database.entry.ArticleEntry;
import com.ry.flashinventory.database.entry.ZoneEntry;
import com.ry.flashinventory.database.entry.ZoneLineEntry;
import com.ry.flashinventory.listener.InventaireLineAdapterListener;
import com.ry.flashinventory.listener.ZoneLineAdapterListener;

import java.util.List;

/**
 * Created by netserve on 09/01/2019.
 */

public class ZoneLineAdapter extends RecyclerView.Adapter<ZoneLineAdapter.ZoneLineViewHolder> {
    private static final String TAG = ZoneLineAdapter.class.getSimpleName();

    private Context mContext;
    private List<ZoneEntry> zoneList;
    private ZoneLineAdapterListener mListener;

    private AppDatabase mDb;

    //    ViewHolder de l'adapter
    public class ZoneLineViewHolder extends RecyclerView.ViewHolder {
        public TextView code_barre, quantite;
        public ImageButton delete;
        public View lineview;

        public ZoneLineViewHolder(View view) {
            super(view);
            code_barre = view.findViewById(R.id.tv_itemzone_codebarre);
            quantite = view.findViewById(R.id.tv_itemzone_quantite);
            delete = view.findViewById(R.id.ib_itemzone_delete);
            lineview = view;

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDeleteZoneLine(zoneList.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }


    public ZoneLineAdapter(Context context, ZoneLineAdapterListener listener, List<ZoneEntry> zoneEntries) {
        this.mContext = context;
        this.mListener = listener;
        this.zoneList = zoneEntries;

        mDb = AppDatabase.getInstance(mContext.getApplicationContext());
    }

    @Override
    public ZoneLineAdapter.ZoneLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_zone_line, parent, false);

        return new ZoneLineAdapter.ZoneLineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ZoneLineAdapter.ZoneLineViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder: codeBarre="+zoneList.get(position).getCode_barre()+" quantite="+zoneList.get(position).getQuantite() );

//        ArticleEntry articleEntry = mDb.articleDao().getArticleById(zoneList.get(position).getId());
        holder.code_barre.setText(String.format("%s.  %s", zoneList.size()-position, zoneList.get(position).getCode_barre()));
        holder.quantite.setText(String.format("%s", (zoneList.get(position).getQuantite() != null ? zoneList.get(position).getQuantite() : 0)));

        int r = position % 2;
        Log.e(TAG, "InventaireLineViewHolder: getAdapterPosition=" + position + " r=" + r);
        if (r == 1) {
            holder.lineview.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
        } else
            holder.lineview.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));


    }

    @Override
    public int getItemCount() {
        if (zoneList != null) {
            return zoneList.size();
        }
        return 0;
    }
}
