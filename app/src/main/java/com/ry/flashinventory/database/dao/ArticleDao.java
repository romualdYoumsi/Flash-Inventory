package com.ry.flashinventory.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ry.flashinventory.database.entry.ArticleEntry;
import com.ry.flashinventory.database.entry.ZoneEntry;

import java.util.List;

/**
 * Created by netserve on 08/12/2018.
 */

@Dao
public interface ArticleDao {
    @Query("SELECT at.id id, at.code_article code_article, at.code_barre code_barre, at.intitule intitule, at.marque marque, at.statut statut, at.prix_revient prix_revient, (SELECT SUM(zl.quantite_line) FROM zoneline zl WHERE zl.article_id=at.id) quantite FROM article at ORDER By at.intitule ASC")
    LiveData<List<ArticleEntry>> loadAllArticle();

    @Query("SELECT at.id id, at.code_article code_article, at.code_barre code_barre, at.intitule intitule, at.marque marque, at.statut statut, at.prix_revient prix_revient, (SELECT SUM(zl.quantite_line) FROM zoneline zl WHERE zl.article_id=at.id) quantite FROM article at ORDER By at.intitule ASC LIMIT :limit")
    List<ArticleEntry> getArticleLimit(int limit);

    @Query("SELECT at.id id, at.code_article code_article, at.code_barre code_barre, at.intitule intitule, at.marque marque, at.statut statut, at.prix_revient prix_revient, (SELECT SUM(zl.quantite_line) FROM zoneline zl WHERE zl.article_id=at.id) quantite FROM article at WHERE at.id > :id_limit ORDER By at.intitule ASC LIMIT :limit")
    List<ArticleEntry> getArticleLimitById(long id_limit, int limit);

    @Query("SELECT * FROM article ORDER By id DESC")
    List<ArticleEntry> getAllArticle();

    @Query("SELECT COUNT(id) quantite FROM zoneline")
    ArticleEntry getCountArticle();

    @Query("SELECT at.id id, at.code_article code_article, at.code_barre code_barre, at.intitule intitule, at.marque marque, at.statut statut, at.prix_revient prix_revient, (SELECT SUM(zl.quantite_line) FROM zoneline zl WHERE zl.article_id=at.id) quantite FROM article at WHERE at.statut=:statut")
    List<ArticleEntry> getAllArticleByStatut(String statut);

    @Query("SELECT at.id id, at.code_article code_article, at.code_barre code_barre, at.intitule intitule, at.marque marque, at.prix_revient prix_revient, z.code_barre statut, (SELECT SUM(zl2.quantite_line) FROM zoneline zl2 WHERE zl2.zone_id=z.id AND zl2.article_id=at.id) quantite FROM (SELECT * FROM zoneline GROUP BY article_id, zone_id) zl, article at, zone z WHERE zl.zone_id=z.id AND zl.article_id=at.id AND at.statut=:statut")
    List<ArticleEntry> getAllArticleByStatutV2(String statut);

    @Query("SELECT at.id id, at.code_article code_article, at.code_barre code_barre, at.intitule intitule, at.marque marque, at.statut statut, at.prix_revient prix_revient, (SELECT SUM(zl.quantite_line) FROM zoneline zl WHERE zl.article_id=at.id) quantite FROM article at WHERE at.id = :id")
    ArticleEntry getArticleById(Long id);

    @Query("SELECT at.id id, at.code_article code_article, at.code_barre code_barre, at.intitule intitule, at.marque marque, at.statut statut, at.prix_revient prix_revient, (SELECT SUM(zl.quantite_line) FROM zoneline zl WHERE zl.article_id=at.id) quantite FROM article at WHERE at.code_barre = :code_barre")
    ArticleEntry getArticleByCodebarre(String code_barre);

//    Renvoit la liste des zones d'un article
    @Query("SELECT z.id id, z.code_barre code_barre, z.intitule intitule, z.quantite quantite FROM zoneline zl, zone z WHERE zl.article_id = :article_id AND zl.zone_id = z.id GROUP BY z.id")
    List<ZoneEntry> getZonesArticle(Long article_id);

    @Query("UPDATE article SET quantite = quantite + :quantite WHERE code_barre = :code_barre")
    void updateStockArticleByCodeBarre(double quantite, String code_barre);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertArticle(ArticleEntry articleEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateArticle(ArticleEntry articleEntry);

    @Delete
    void deleteArticle(ArticleEntry articleEntry);

    @Query("DELETE FROM article")
    void deleteAllArticles();
}
