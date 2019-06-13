package com.ry.flashinventory.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ry.flashinventory.R;
import com.ry.flashinventory.database.AppDatabase;
import com.ry.flashinventory.database.entry.ArticleEntry;
import com.ry.flashinventory.listener.ParserReadArticleListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by netserve on 08/12/2018.
 */

public class ParserReadArticlesTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = ParserReadArticlesTask.class.getSimpleName();

    private ParserReadArticleListener listener;

    private Context context;
    private String mFile;
    private AppDatabase mDb;

    public ParserReadArticlesTask(Context context, String filePath, ParserReadArticleListener parserReadArticleListener) {
        this.context = context;
        this.mFile = filePath;
        this.listener = parserReadArticleListener;

        mDb = AppDatabase.getInstance(context.getApplicationContext());
    }

    @Override
    protected String doInBackground(Void... voids) {
        /*try {
//            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream myInput = new FileInputStream(mFile);

            // Create a POI File System object
//            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // Create a workbook using the File System
//            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            XSSFWorkbook myWorkBook = new XSSFWorkbook(myInput);
            // Get the first sheet from workbook
//            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);
            XSSFRow myRow;
            XSSFCell myCell;
            // We now need something to iterate through the cells.
//            Iterator<Row> rowIter = mySheet.rowIterator();
            Iterator rowIter = mySheet.rowIterator();

//            Suppression des articles en local
            mDb.articleDao().deleteAllArticles();
            mDb.zoneDao().deleteAllZones();
            mDb.zoneLineDao().deleteAllZoneLines();

            int rowno =0;
//            String textString = "\n";
            while (rowIter.hasNext()) {
                Log.e(TAG, " row no "+ rowno );
//                HSSFRow myRow = (HSSFRow) rowIter.next();
                myRow = (XSSFRow) rowIter.next();
                if(rowno != 0) {
//                    Iterator<Cell> cellIter = myRow.cellIterator();
                    Iterator cellIter = myRow.cellIterator();
                    int colno =0;
                    String codeArticle="", codeBarre="", initule="", prixRevient="";
                    while (cellIter.hasNext()) {
//                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        myCell = (XSSFCell) cellIter.next();
                        if (colno==0){
                            codeArticle = myCell.toString();
                        }else if (colno==1){
                            codeBarre = myCell.toString();
                        }else if (colno==2){
                            initule = myCell.toString();
                        }else if (colno==3){
                            prixRevient = myCell.toString();
                        }
                        colno++;
//                        Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }
//                    textString += String.format("%s -- %s -- %s -- %s\n", codeArticle, codeBarre, initule, prixRevient);

//                    Log.e(TAG, "readExcelFileFromAssets: prixRevient "+prixRevient);
                    ArticleEntry articleEntry = new ArticleEntry();
                    articleEntry.setCode_article(codeArticle);
                    articleEntry.setCode_barre(codeBarre);
                    articleEntry.setIntitule(initule);
                    articleEntry.setPrix_revient(!prixRevient.equals("") ? Double.parseDouble(prixRevient) : 0.00);
                    articleEntry.setQuantite(0);
                    articleEntry.setStatut(context.getString(R.string.present));
                    mDb.articleDao().insertArticle(articleEntry);
                }
                rowno++;
            }

//            Log.e(TAG, "readExcelFileFromAssets: ARTICLES textStringAll "+textString);

            return "SUCCESS";
        } catch (Exception e) {
            Log.e(TAG, "error: "+ e.toString());

            return e.getMessage();
        }*/

//        File fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File fileToGet = new File(mFile);
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToGet));
            String line;
            String textFile = "";
            int i = 0;

//            Suppression des articles en local
            mDb.articleDao().deleteAllArticles();
            mDb.zoneDao().deleteAllZones();
            mDb.zoneLineDao().deleteAllZoneLines();

//            ignore l'entete du fichier
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    try {
                        ArticleEntry articleEntry = new ArticleEntry();
                        articleEntry.setCode_article(tokens[0]);
                        articleEntry.setCode_barre(tokens[1]);
                        articleEntry.setIntitule(tokens[2]);
                        articleEntry.setPrix_revient(!tokens[3].equals("") ? Double.parseDouble(tokens[3]) : 0.00);
                        articleEntry.setQuantite(0);
                        articleEntry.setStatut(context.getString(R.string.present));
                        mDb.articleDao().insertArticle(articleEntry);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                textFile += "\n"+tokens[0].toString() + "," + tokens[1].toString() + "," + tokens[2].toString() + "," + tokens[3].toString();
                i++;
            }
            Log.e(TAG, "doInBackground: textFile=" + i);
            return "SUCCESS";
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return "FILE_NOT_FOUND";
        } catch (IOException exception) {
            exception.printStackTrace();
            return "EXCEPTION";
        }
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        listener.onReadArticlesComplete(s);
    }
}
