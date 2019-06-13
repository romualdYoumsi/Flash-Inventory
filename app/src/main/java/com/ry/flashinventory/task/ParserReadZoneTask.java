package com.ry.flashinventory.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ry.flashinventory.database.AppDatabase;
import com.ry.flashinventory.database.entry.ZoneEntry;
import com.ry.flashinventory.listener.ParserReadZoneListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by netserve on 06/01/2019.
 */

public class ParserReadZoneTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = ParserReadArticlesTask.class.getSimpleName();

    private ParserReadZoneListener listener;

    private Context context;
    private File mFile;
    private AppDatabase mDb;

    public ParserReadZoneTask(Context context, File file, ParserReadZoneListener parserReadZoneListener) {
        this.context = context;
        this.mFile = file;
        this.listener = parserReadZoneListener;

        mDb = AppDatabase.getInstance(context.getApplicationContext());
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream myInput = new FileInputStream(mFile);

            // Create a POI File System object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            // We now need something to iterate through the cells.
            Iterator<Row> rowIter = mySheet.rowIterator();

//            Suppression des zones et zonesLines en local
            mDb.zoneDao().deleteAllZones();
            mDb.zoneLineDao().deleteAllZoneLines();

            int rowno =0;
            String textString = "\n";
            while (rowIter.hasNext()) {
                Log.e(TAG, " row no "+ rowno );
                HSSFRow myRow = (HSSFRow) rowIter.next();
                if(rowno !=0) {
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno =0;
                    String codeBarre="", intitule="";
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno == 0){
                            codeBarre = myCell.toString();
                        }
                        if (colno == 1){
                            intitule = myCell.toString();
                        }
                        colno++;
//                        Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }
                    textString += String.format("%s ; %s ;\n", codeBarre, intitule);

//                    Log.e(TAG, "readExcelFileFromAssets: prixRevient "+prixRevient);
                    ZoneEntry zoneEntry = new ZoneEntry();
                    zoneEntry.setCode_barre(codeBarre);
                    zoneEntry.setIntitule(intitule);
                    zoneEntry.setQuantite(0);
                    mDb.zoneDao().insertZone(zoneEntry);
                }
                rowno++;
            }

            Log.e(TAG, "readExcelFileFromAssets: ZONES textStringAll "+textString);

            return "SUCCESS";
        } catch (Exception e) {
            Log.e(TAG, "error "+ e.toString());

            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        listener.onReadZonesComplete(s);
    }
}
