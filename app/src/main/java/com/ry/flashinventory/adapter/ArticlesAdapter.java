package com.ry.flashinventory.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ry.flashinventory.R;
import com.ry.flashinventory.database.entry.ArticleEntry;
import com.ry.flashinventory.listener.ArticlesAdapterListener;
import com.ry.flashinventory.utils.FlashInventoryUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by netserve on 09/12/2018.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticlesViewHolder> {
    private static final String TAG = ArticlesAdapter.class.getSimpleName();

    private Context mContext;
    private List<ArticleEntry> produitsList;
    private List<ArticleEntry> produitsListFiltered;
    private ArticlesAdapterListener mListener;

    //    ViewHolder de l'adapter
    public class ArticlesViewHolder extends RecyclerView.ViewHolder {
        public TextView intitule, codeBarre, description, statut;
        public ImageView colorShow;
        public Button details, supprimer;
        public View mView;

        public ArticlesViewHolder(View view) {
            super(view);
            intitule = view.findViewById(R.id.tv_itemarticle_intitule);
            description = view.findViewById(R.id.tv_itemarticle_description);
            statut = view.findViewById(R.id.tv_itemarticle_statut);
            colorShow = view.findViewById(R.id.iv_itemarticle_color_show);
            mView = view;

//            ecoute du clique sur le bouton de shopping(ajout dans le panier)
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    mListener.onArticleClickListener(produitsListFiltered.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    //    Filtre la liste des produits
    public void performFiltering(String searchString) {
        if (searchString.isEmpty()) {
            produitsListFiltered = produitsList;
        } else {
            ArrayList<ArticleEntry> filteredList = new ArrayList<>();
            for (ArticleEntry row : produitsList) {

                // name match condition. this might differ depending on your requirement
                // here we are looking for name or phone number match
                if (row.getIntitule() != null) {
                    if (row.getIntitule().toLowerCase().contains(searchString.toLowerCase())
                            || row.getCode_barre().toLowerCase().contains(searchString.toLowerCase())) {
                        filteredList.add(row);
                    }
                } else {
                    if (row.getCode_barre().toLowerCase().contains(searchString.toLowerCase())) {
                        filteredList.add(row);
                    }
                }
            }

            produitsListFiltered = filteredList;
        }

        notifyDataSetChanged();
    }


    public ArticlesAdapter(Context context, List<ArticleEntry> articleEntries, ArticlesAdapterListener listener) {
        this.mContext = context;
        this.produitsList = articleEntries;
        this.mListener = listener;
        this.produitsListFiltered = articleEntries;
    }

    @Override
    public ArticlesAdapter.ArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_articles, parent, false);

        return new ArticlesAdapter.ArticlesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ArticlesAdapter.ArticlesViewHolder holder, int position) {

        holder.intitule.setText(produitsListFiltered.get(position).getIntitule());

        holder.description.setText(String.format("%s unités en stock  •  Prix de Revient: %s XAF",
                produitsListFiltered.get(position).getQuantite() != null ? produitsListFiltered.get(position).getQuantite() : "0",
                produitsListFiltered.get(position).getPrix_revient() != null ? FlashInventoryUtility.amountFormat2(""+produitsListFiltered.get(position).getPrix_revient()) : ""));
        holder.colorShow.setBackgroundColor(FlashInventoryUtility.getRandomColor());
        holder.statut.setText(produitsListFiltered.get(position).getStatut() != null ? produitsListFiltered.get(position).getStatut().toUpperCase() : mContext.getString(R.string.absent));


        if (produitsListFiltered.get(position).getStatut().toLowerCase().equals("absent")) {
            holder.statut.setTextColor(mContext.getResources().getColor(R.color.colorRed));
        } else holder.statut.setTextColor(mContext.getResources().getColor(R.color.colorGreen));

    }

    @Override
    public int getItemCount() {
        if (produitsListFiltered != null) {
            return produitsListFiltered.size();
        }
        return 0;
    }
}
