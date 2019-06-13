package com.ry.flashinventory.database.entry;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by netserve on 08/12/2018.
 */

@Entity(tableName = "zoneline")
public class ZoneLineEntry implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long zone_id;
    private Long article_id;
    private Integer quantite_line;

    public ZoneLineEntry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getZone_id() {
        return zone_id;
    }

    public void setZone_id(Long zone_id) {
        this.zone_id = zone_id;
    }

    public Long getArticle_id() {
        return article_id;
    }

    public void setArticle_id(Long article_id) {
        this.article_id = article_id;
    }

    public Integer getQuantite_line() {
        return quantite_line;
    }

    public void setQuantite_line(Integer quantite_line) {
        this.quantite_line = quantite_line;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.zone_id);
        dest.writeValue(this.article_id);
        dest.writeValue(this.quantite_line);
    }

    protected ZoneLineEntry(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.zone_id = (Long) in.readValue(Long.class.getClassLoader());
        this.article_id = (Long) in.readValue(Long.class.getClassLoader());
        this.quantite_line = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<ZoneLineEntry> CREATOR = new Creator<ZoneLineEntry>() {
        @Override
        public ZoneLineEntry createFromParcel(Parcel source) {
            return new ZoneLineEntry(source);
        }

        @Override
        public ZoneLineEntry[] newArray(int size) {
            return new ZoneLineEntry[size];
        }
    };
}
