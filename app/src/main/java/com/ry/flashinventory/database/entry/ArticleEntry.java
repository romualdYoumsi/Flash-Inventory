package com.ry.flashinventory.database.entry;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by netserve on 08/12/2018.
 */

@Entity(tableName = "article")
public class ArticleEntry implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String code_article;
    private String code_barre;
    private String intitule;
    private String marque;
    private String statut;
    private Double prix_revient;
    private Integer quantite;

    @Ignore
    private String intitule_zone;

    public ArticleEntry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode_article() {
        return code_article;
    }

    public void setCode_article(String code_article) {
        this.code_article = code_article;
    }

    public String getCode_barre() {
        return code_barre;
    }

    public void setCode_barre(String code_barre) {
        this.code_barre = code_barre;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Double getPrix_revient() {
        return prix_revient;
    }

    public void setPrix_revient(Double prix_revient) {
        this.prix_revient = prix_revient;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public String getIntitule_zone() {
        return intitule_zone;
    }

    public void setIntitule_zone(String intitule_zone) {
        this.intitule_zone = intitule_zone;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.code_article);
        dest.writeString(this.code_barre);
        dest.writeString(this.intitule);
        dest.writeString(this.marque);
        dest.writeString(this.statut);
        dest.writeValue(this.prix_revient);
        dest.writeValue(this.quantite);
        dest.writeString(this.intitule_zone);
    }

    protected ArticleEntry(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.code_article = in.readString();
        this.code_barre = in.readString();
        this.intitule = in.readString();
        this.marque = in.readString();
        this.statut = in.readString();
        this.prix_revient = (Double) in.readValue(Double.class.getClassLoader());
        this.quantite = (Integer) in.readValue(Integer.class.getClassLoader());
        this.intitule_zone = in.readString();
    }

    public static final Creator<ArticleEntry> CREATOR = new Creator<ArticleEntry>() {
        @Override
        public ArticleEntry createFromParcel(Parcel source) {
            return new ArticleEntry(source);
        }

        @Override
        public ArticleEntry[] newArray(int size) {
            return new ArticleEntry[size];
        }
    };
}
