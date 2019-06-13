package com.ry.flashinventory.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ry.flashinventory.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by netserve on 30/08/2018.
 */

public final class FlashInventoryUtility {
    private static final String TAG = FlashInventoryUtility.class.getSimpleName();
    public static String ENCODE_IMG = "&img";
    public static String ENCODE_DESC = "&desc";
    private static String ENCODE_CAROUSEL = "&amp;carousel;";

    public static final String PASSWORD_ADD_ZONE = "RY_FI19";

    public static String CURRENCY = "€";
    public static String ISALES_PATH_FOLDER = "iSales";
    public static String ISALES_PRODUCTS_IMAGESPATH_FOLDER = "i-Sales/i-Sales Produits";
    public static String FLASHINV_EXPORT_FOLDER = "FlashInventory/FlashInventory Export";
    public static String FLASHINV_IMPORT_FOLDER = "FlashInventory/FlashInventory Import";
    public static String FLASHINV_TRASH_FOLDER = "FlashInventory/FlashInventory Export/trash";
    public static String FLASHINV_IMPORT_FILE_ARTICLE = "fi_articles_import.xls";
    public static String FLASHINV_IMPORT_FILE_ZONE = "fi_zones_import.xls";

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 300);
        return noOfColumns;
    }

    //    Arromdi les bord d'une image bitmap
    public static Bitmap getRoundedCornerBitmap(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    public static String strCapitalize(String str) {
        String name = str;
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

        return name;
    }

    public static String amountFormat2(String value) {

        double valueDouble = Double.parseDouble(value);
        String str = String.format(Locale.FRANCE,
                "%,-10.2f", valueDouble);
        return String.valueOf(str).replace(" ", "");
    }

    public static String roundOffTo2DecPlaces(String value) {
        double valueDouble = Double.parseDouble(value);
        return String.format("%.2f", valueDouble);
    }

    /**
     * validate your email address format. Ex-akhi@mani.com
     */
    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    /**
     * returns the bytesize of the give bitmap
     */
    public static int bitmapByteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    public static final void makeSureFileWasCreatedThenMakeAvailable(Context context, File file) {
        MediaScannerConnection.scanFile(context,
                new String[]{file.toString()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
//                        Log.e(TAG, "onScanCompleted: Scanned=" + path);
//                        Log.e(TAG, "onScanCompleted: uri=" + uri);
                    }
                });
    }

    private static final String getCurrentDateAndTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatted = simpleDateFormat.format(calendar.getTime());
        return formatted;
    }

//    enregistre la photo d'un produit en loca
    public static final String saveProduitImage(Context context, Bitmap imageToSave, String filename) {
        String currentDateAndTime = getCurrentDateAndTime();
        File dir = new File(Environment.getExternalStorageDirectory(), ISALES_PRODUCTS_IMAGESPATH_FOLDER);
        if (!dir.exists()) {
            if (dir.mkdirs()){
                Log.e(TAG, "saveProduitImage: folder created" );
            }
        }

        File file = new File(dir, String.format("%s.jpg", filename, currentDateAndTime));

        if (file.exists ()) file.delete();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();
            makeSureFileWasCreatedThenMakeAvailable(context, file);

            return file.getAbsolutePath();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "saveProduitImage:FileNotFoundException "+e.getMessage() );
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "saveProduitImage:IOException "+e.getMessage() );
            return null;
        }

    }

//    enregistre la photo d'un client en loca
    public static final File makeLocalFile(String filename) {
        String currentDateAndTime = getCurrentDateAndTime();
        File dir = new File(Environment.getExternalStorageDirectory(), FLASHINV_EXPORT_FOLDER);
        if (!dir.exists()) {
            if (dir.mkdirs()){
//                Log.e(TAG, "saveClientImage: folder created" );
            }
        }

        File file = new File(dir, filename);

        if (file.exists ()) file.delete();
        return file;

    }

//    enregistre la photo d'un client en loca
    public static final File makeTrashLocalFile(String filename) {
        String currentDateAndTime = getCurrentDateAndTime();
        File dir = new File(Environment.getExternalStorageDirectory(), FLASHINV_TRASH_FOLDER);
        if (!dir.exists()) {
            if (dir.mkdirs()){
//                Log.e(TAG, "saveClientImage: folder created" );
            }
        }

        File file = new File(dir, filename);

        if (file.exists ()) file.delete();
        return file;

    }

//    Renvoit le fichier d'importation des articles
    public static final File getFileImportArticles() {
        String currentDateAndTime = getCurrentDateAndTime();
        File dir = new File(Environment.getExternalStorageDirectory(), FLASHINV_IMPORT_FOLDER);
        if (!dir.exists()) {
            if (dir.mkdirs()){
//                Log.e(TAG, "saveClientImage: folder created" );
            }
        }

        File file = new File(dir, FLASHINV_IMPORT_FILE_ARTICLE);

        if (file.exists()) return file;
        return null;
    }

//    Renvoit le fichier d'importation des zones
    public static final File getFileImportZones() {
        String currentDateAndTime = getCurrentDateAndTime();
        File dir = new File(Environment.getExternalStorageDirectory(), FLASHINV_IMPORT_FOLDER);
        if (!dir.exists()) {
            if (dir.mkdirs()){
//                Log.e(TAG, "saveClientImage: folder created" );
            }
        }

        File file = new File(dir, FLASHINV_IMPORT_FILE_ZONE);

        if (file.exists()) return file;
        return null;
    }

    //    Cree le dossier d'importation des données
    public static final void makeDirsImport() {
        File dir = new File(Environment.getExternalStorageDirectory(), FlashInventoryUtility.FLASHINV_IMPORT_FOLDER);
        if (!dir.exists()) {
            if (dir.mkdirs()){
//                Log.e(TAG, "saveClientImage: folder created" );
            }
        }

    }

    public static final void deleteProduitsImgFolder() {
        File myDir = new File(Environment.getExternalStorageDirectory(), ISALES_PRODUCTS_IMAGESPATH_FOLDER);
        if (myDir.isDirectory() && myDir.list() != null) {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(myDir, children[i]).delete();
            }
        }
    }

    public static final void deleteClientsImgFolder() {
        File myDir = new File(Environment.getExternalStorageDirectory(), FLASHINV_EXPORT_FOLDER);
        if (myDir.isDirectory() && myDir.list() != null) {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(myDir, children[i]).delete();
            }
        }
    }

    public static CharSequence[] getExportFormat() {
        CharSequence[] items = {"Fichier Texte (.txt)", "Fichier Exel (.xlsx)"};
        return items;
    }

    public static void playAssetSoundScanSuccess(Context context) {
        MediaPlayer mp = MediaPlayer.create(context, R.raw.scan_article_success); // sound is inside res/raw/mysound
        mp.start();
    }

    public static void playAssetSoundScanFail(Context context) {
        MediaPlayer mp = MediaPlayer.create(context, R.raw.scan_article_fail); // sound is inside res/raw/mysound
        mp.start();
    }

    public static void playAssetSoundCancelledInventaire(Context context) {
        MediaPlayer mp = MediaPlayer.create(context, R.raw.inventaire_cancelled); // sound is inside res/raw/mysound
        mp.start();
    }

    public static void playAssetSoundSavedInventaire(Context context) {
        MediaPlayer mp = MediaPlayer.create(context, R.raw.inventaire_saved); // sound is inside res/raw/mysound
        mp.start();
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}