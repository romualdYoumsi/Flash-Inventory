package com.ry.flashinventory.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ry.flashinventory.R;
import com.ry.flashinventory.database.AppDatabase;
import com.ry.flashinventory.database.entry.ArticleEntry;
import com.ry.flashinventory.listener.ParserWriteListener;
import com.ry.flashinventory.utils.FlashInventoryUtility;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by netserve on 10/12/2018.
 */

public class ParserWriteXlsTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = ParserWriteXlsTask.class.getSimpleName();

    private ParserWriteListener listener;

    private Context context;
    private File mFile;
    private AppDatabase mDb;

    public ParserWriteXlsTask(Context context, ParserWriteListener parserReadListener) {
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

        Log.e(TAG, "doInBackground: Ready to get data from db" );
        ArticleEntry articleEntryCount = mDb.articleDao().getCountArticle();
        Log.e(TAG, "doInBackground: articleCOunt="+articleEntryCount.getQuantite());
        boolean success = false;
        List<ArticleEntry> articleEntriesPresent = mDb.articleDao().getAllArticleByStatutV2(context.getResources().getString(R.string.present));
        List<ArticleEntry> articleEntriesAbsent = mDb.articleDao().getAllArticleByStatutV2(context.getResources().getString(R.string.absent));

        Log.e(TAG, "doInBackground: articleEntriesPresent=" + articleEntriesPresent.size() +
                " articleEntriesAbsent=" + articleEntriesAbsent.size());

        Date today = new Date();
        final SimpleDateFormat nameFormat = new SimpleDateFormat("dd-MM-yyyy_HHmm");
        final String namePresent = String.format("Articles_Presents_%s", nameFormat.format(today));
        final String nameAbsent = String.format("Articles_Absents_%s", nameFormat.format(today));

        //New Workbook
        XSSFWorkbook wbPresent = new XSSFWorkbook();
        XSSFWorkbook wbAbsent = new XSSFWorkbook();

        XSSFCell cPresent = null;
        XSSFCell cAbsent = null;

        //New Sheet  XSSFWorkbook
        XSSFSheet sheetPresent = null;
        sheetPresent = wbPresent.createSheet(namePresent);
        //New Sheet
        XSSFSheet sheetAbsent = null;
        sheetAbsent = wbAbsent.createSheet(nameAbsent);

//        Initialisation des entetes des fichiers
        XSSFRow rowPresent = sheetPresent.createRow(0);
//            Generation des cellules
        cPresent = rowPresent.createCell(0);
        cPresent.setCellValue("Code Article");

        cPresent = rowPresent.createCell(1);
        cPresent.setCellValue("Code Barre");

        cPresent = rowPresent.createCell(2);
        cPresent.setCellValue("Quantité");

        cPresent = rowPresent.createCell(3);
        cPresent.setCellValue("Prix de Revient");

        cPresent = rowPresent.createCell(4);
        cPresent.setCellValue("Zone");

        XSSFRow rowAbsent = sheetAbsent.createRow(0);
//            Generation des cellules
        cAbsent = rowPresent.createCell(0);
        cAbsent.setCellValue("Code Article");

        cAbsent = rowAbsent.createCell(1);
        cAbsent.setCellValue("Code Barre");

        cAbsent = rowAbsent.createCell(2);
        cAbsent.setCellValue("Quantité");

        cAbsent = rowAbsent.createCell(3);
        cAbsent.setCellValue("Prix de Revient");

        cAbsent = rowAbsent.createCell(4);
        cAbsent.setCellValue("Zone");

//        Generation lignes article presents
        for (int i = 0; i < articleEntriesPresent.size(); i++) {
            ArticleEntry articleEntry = articleEntriesPresent.get(i);
            XSSFRow row = sheetPresent.createRow(i+1);
//            Generation des cellules
            cPresent = row.createCell(0);
            cPresent.setCellValue(""+articleEntry.getCode_article());

            cPresent = row.createCell(1);
            cPresent.setCellValue(""+articleEntry.getCode_barre());

            cPresent = row.createCell(2);
            cPresent.setCellValue(""+(articleEntry.getQuantite() == null ? 0 : articleEntry.getQuantite()));

            cPresent = row.createCell(3);
            cPresent.setCellValue(""+articleEntry.getPrix_revient());

//            le statut ici represente la zone de l'article
            cPresent = row.createCell(4);
            cPresent.setCellValue(""+articleEntry.getStatut());
        }
//        Generation lignes article absents
        for (int i = 0; i < articleEntriesAbsent.size(); i++) {
            ArticleEntry articleEntry = articleEntriesAbsent.get(i);
            XSSFRow row = sheetAbsent.createRow(i+1);
//            Generation des cellules
            cAbsent = row.createCell(0);
            cAbsent.setCellValue(""+articleEntry.getCode_article());

            cAbsent = row.createCell(1);
            cAbsent.setCellValue(""+articleEntry.getCode_barre());

            cAbsent = row.createCell(2);
            cAbsent.setCellValue(""+(articleEntry.getQuantite() == null ? 0 : articleEntry.getQuantite()));

            cAbsent = row.createCell(3);
            cAbsent.setCellValue(""+articleEntry.getPrix_revient());

            cAbsent = row.createCell(4);
            cAbsent.setCellValue(""+articleEntry.getStatut());
        }

        // Create a path where we will place our List of objects on external storage
        File filePresent = FlashInventoryUtility.makeLocalFile(namePresent + ".xlsx");
        File fileAbsent = FlashInventoryUtility.makeLocalFile(nameAbsent + ".xlsx");
//        Trash file
        File filePresentTrash = FlashInventoryUtility.makeTrashLocalFile(namePresent + ".xlsx");
        File fileAbsentTrash = FlashInventoryUtility.makeTrashLocalFile(nameAbsent + ".xlsx");
        FileOutputStream osPresent = null;
        FileOutputStream osAbsent = null;
        FileOutputStream osPresentTrash = null;
        FileOutputStream osAbsentTrash = null;

        try {
            osPresent = new FileOutputStream(filePresent);
            osAbsent = new FileOutputStream(fileAbsent);
            osPresentTrash = new FileOutputStream(filePresentTrash);
            osAbsentTrash = new FileOutputStream(fileAbsentTrash);
            wbPresent.write(osPresentTrash);
            wbAbsent.write(osAbsentTrash);
//            Log.e(TAG, "Writing file" + osPresent);
            return "SUCCESS";
        } catch (IOException e) {
            Log.e(TAG, "Error writing ", e);
            return e.getMessage();
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file", e);
            return e.getMessage();
        } finally {
            try {
                if (null != osPresent) osPresent.close();
                if (null != osAbsent) osAbsent.close();

            } catch (Exception ex) {
            }
        }
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        listener.onWriteComplete(s, null);
    }
}
