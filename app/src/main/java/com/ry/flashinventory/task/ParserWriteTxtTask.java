package com.ry.flashinventory.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ry.flashinventory.R;
import com.ry.flashinventory.database.AppDatabase;
import com.ry.flashinventory.database.entry.ArticleEntry;
import com.ry.flashinventory.listener.ParserWriteListener;
import com.ry.flashinventory.utils.FlashInventoryUtility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by netserve on 23/12/2018.
 */

public class ParserWriteTxtTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = ParserWriteXlsTask.class.getSimpleName();

    private ParserWriteListener listener;

    private Context context;
    private File mFile;
    private AppDatabase mDb;

    public ParserWriteTxtTask(Context context, ParserWriteListener parserReadListener) {
        this.context = context;
        this.listener = parserReadListener;

        mDb = AppDatabase.getInstance(context.getApplicationContext());
    }

    @Override
    protected String doInBackground(Void... voids) {
        // check if available and not read only
        if (!FlashInventoryUtility.isExternalStorageAvailable() || FlashInventoryUtility.isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return "STORAGE_NOT_AVAILABLE";
        }

        boolean success = false;
//        List<ArticleEntry> articleEntriesPresent = mDb.articleDao().getAllArticleByStatut(context.getResources().getString(R.string.present));
//        List<ArticleEntry> articleEntriesAbsent = mDb.articleDao().getAllArticleByStatut(context.getResources().getString(R.string.absent));
        List<ArticleEntry> articleEntriesPresent = mDb.articleDao().getAllArticleByStatutV2(context.getResources().getString(R.string.present));
        List<ArticleEntry> articleEntriesAbsent = mDb.articleDao().getAllArticleByStatutV2(context.getResources().getString(R.string.absent));

        Log.e(TAG, "doInBackground: articleEntriesPresent=" + articleEntriesPresent.size() +
                " articleEntriesAbsent=" + articleEntriesAbsent.size());

        //Cell style for header row
//        CellStyle csPresent = wbPresent.createCellStyle();
//        csPresent.setFillForegroundColor(HSSFColor.LIME.index);
//        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        CellStyle csAbsent = wbAbsent.createCellStyle();
//        csAbsent.setFillForegroundColor(HSSFColor.RED.index);
//        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        Date today = new Date();
        final SimpleDateFormat nameFormat = new SimpleDateFormat("dd-MM-yyyy_HHmm");
        final String namePresent = String.format("Articles_Presents_%s", nameFormat.format(today));
        final String nameAbsent = String.format("Articles_Absents_%s", nameFormat.format(today));


        StringBuilder bodyPresent = new StringBuilder();
        StringBuilder bodyAbsent = new StringBuilder();

//        Initialisation des entetes des fichiers
//        bodyPresent.append("Code Article\t\t\t\tQuantite\tPrix de Revient\t\t\n");
//        bodyAbsent.append("Code Article\t\t\t\tQuantite\tPrix de Revient\t\t\n");

//        Generation lignes article presents
        for (int i = 0; i < articleEntriesPresent.size(); i++) {
            ArticleEntry articleEntry = articleEntriesPresent.get(i);
//            Log.e(TAG, "doInBackground: statut:"+articleEntry.getStatut());
            bodyPresent.append(String.format("%s;%s;%s;%s;%s;%s;%s", articleEntry.getCode_article(), articleEntry.getCode_barre(), (articleEntry.getQuantite() == null ? 0 : articleEntry.getQuantite()), articleEntry.getPrix_revient(), articleEntry.getStatut(), articleEntry.getIntitule(), articleEntry.getMarque()));
            bodyPresent.append("\n\r");
        }
//        Generation lignes article absents
        for (int i = 0; i < articleEntriesAbsent.size(); i++) {
            ArticleEntry articleEntry = articleEntriesAbsent.get(i);
            bodyAbsent.append(String.format("%s;%s;%s;%s;%s;%s;%s\n\r", articleEntry.getCode_article(), articleEntry.getCode_barre(), (articleEntry.getQuantite() == null ? 0 : articleEntry.getQuantite()), articleEntry.getPrix_revient(), articleEntry.getStatut(), articleEntry.getIntitule(), articleEntry.getMarque()));
            bodyAbsent.append("\n\r");
        }

        Log.e(TAG, "doInBackground: bodyPresent = \n"+bodyPresent);
        Log.e(TAG, "doInBackground: bodyAbsent = \n"+bodyAbsent);

        // Create a path where we will place our List of objects on external storage
        File filePresent = FlashInventoryUtility.makeLocalFile(namePresent + ".txt");
        File fileAbsent = FlashInventoryUtility.makeLocalFile(nameAbsent + ".txt");
//        trash files
        File filePresentTrash = FlashInventoryUtility.makeTrashLocalFile(namePresent + ".txt");
        File fileAbsentTrash = FlashInventoryUtility.makeTrashLocalFile(nameAbsent + ".txt");

        try {
            FileWriter writerPresent = new FileWriter(filePresent,true);
            FileWriter writerAbsent = new FileWriter(fileAbsent,true);
            FileWriter writerPresentTrash = new FileWriter(filePresentTrash,true);
            FileWriter writerAbsentTrash = new FileWriter(fileAbsentTrash,true);
            writerPresent.write(bodyPresent+"\n");
            writerAbsent.write(bodyAbsent+"\n");
            writerPresentTrash.write(bodyPresent+"\n");
            writerAbsentTrash.write(bodyAbsent+"\n");
            writerPresent.flush();
            writerAbsent.flush();
            writerPresentTrash.flush();
            writerAbsentTrash.flush();
            writerPresent.close();
            writerAbsent.close();
            writerPresentTrash.close();
            writerAbsentTrash.close();

            return "SUCCESS";
        }
        catch(IOException e) {
            Log.e(TAG, "Error writing ", e);
            return e.getMessage();

        }
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        listener.onWriteComplete(s, null);
    }
}
