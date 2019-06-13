package com.ry.flashinventory.database.entry;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by netserve on 08/12/2018.
 */

@Entity(tableName = "zone")
public class ZoneEntry implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String code_barre;
    private String intitule;
    private Integer quantite;

    public ZoneEntry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.code_barre);
        dest.writeString(this.intitule);
        dest.writeValue(this.quantite);
    }

    protected ZoneEntry(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.code_barre = in.readString();
        this.intitule = in.readString();
        this.quantite = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<ZoneEntry> CREATOR = new Creator<ZoneEntry>() {
        @Override
        public ZoneEntry createFromParcel(Parcel source) {
            return new ZoneEntry(source);
        }

        @Override
        public ZoneEntry[] newArray(int size) {
            return new ZoneEntry[size];
        }
    };
}
