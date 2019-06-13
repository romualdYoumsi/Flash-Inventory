package com.ry.flashinventory.pages.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;
import com.ry.flashinventory.R;
import com.ry.flashinventory.listener.ParserReadArticleListener;
import com.ry.flashinventory.listener.ParserReadZoneListener;
import com.ry.flashinventory.listener.ParserWriteListener;
import com.ry.flashinventory.pages.ajouterinventaire.AjouterInventaireActivity;
import com.ry.flashinventory.pages.article.ArticlesActivity;
import com.ry.flashinventory.task.ParserReadArticlesTask;
import com.ry.flashinventory.task.ParserWriteTxtTask;
import com.ry.flashinventory.task.ParserWriteXlsTask;
import com.ry.flashinventory.utils.FlashInventoryUtility;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ParserReadArticleListener, ParserReadZoneListener, ParserWriteListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ACTIVITY_CHOOSE_FILE = 3;


    private View mModActualiser, mModInventaire, mModArticles, mModExporter;

    private ProgressDialog progressDialog;


    public void readExcelFileFromAssets(String fileArticle) {

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(FlashInventoryUtility.strCapitalize("Importation en cours, veuillez patienter..."));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_view));
        progressDialog.show();

        /*
//        Lecture de la liste des zones
        File fileZone = FlashInventoryUtility.getFileImportZones();
        if (fileZone != null) {
            ParserReadZoneTask taskZone = new ParserReadZoneTask(MainActivity.this, fileZone, MainActivity.this);
            taskZone.execute();
        } else {
            Log.e(TAG, "readExcelFileFromAssets: fileZone not exist");
        } */

//        Lecture de la liste des articles
//        File fileArticle = FlashInventoryUtility.getFileImportArticles();
        if (fileArticle != null) {
            ParserReadArticlesTask taskArticle = new ParserReadArticlesTask(MainActivity.this, fileArticle, MainActivity.this);
            taskArticle.execute();
        } else {
            Log.e(TAG, "readExcelFileFromAssets: fileArticle not exist");
            progressDialog.dismiss();
        }
    }

    public void writeExcelFileToStorage() {
        final int[] exportChoice = {-1};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exporter sous le format ");
        builder.setIcon(R.drawable.ic_file_download_24dp);
        builder.setSingleChoiceItems(FlashInventoryUtility.getExportFormat(), -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                exportChoice[0] = item;
//                Toast.makeText(getApplicationContext(), FlashInventoryUtility.getExportFormat()[item], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("VALIDER",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        test des valeurs du format d'exportation
                        switch (exportChoice[0]) {
                            case 0:
//                                Toast.makeText(MainActivity.this, "Success .txt", Toast.LENGTH_SHORT).show();

                                progressDialog = new ProgressDialog(MainActivity.this);
                                progressDialog.setMessage(FlashInventoryUtility.strCapitalize("Exportation .txt en cours, veuillez patienter..."));
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setCancelable(false);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_view));
                                progressDialog.show();
                                ParserWriteTxtTask taskTxt = new ParserWriteTxtTask(MainActivity.this, MainActivity.this);
                                taskTxt.execute();

                                break;
                            case 1:
//                                Toast.makeText(MainActivity.this, "Success .xls", Toast.LENGTH_SHORT).show();

                                progressDialog = new ProgressDialog(MainActivity.this);
                                progressDialog.setMessage(FlashInventoryUtility.strCapitalize("Exportation .xlsx en cours, veuillez patienter..."));
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setCancelable(false);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_view));
                                progressDialog.show();
                                ParserWriteXlsTask taskXls = new ParserWriteXlsTask(MainActivity.this, MainActivity.this);
                                taskXls.execute();

                                break;
                        }
                    }
                });
        builder.setNegativeButton("ANNULER",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();

        /*
         */
    }

    @Override
    public void onReadArticlesComplete(String statut) {
        Log.e(TAG, "onReadArticlesComplete: statut=" + statut);
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (statut.equals("SUCCESS")) {
            Toast.makeText(MainActivity.this, "Fichier importé avec succès.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Impossible d'importer le fichier, veuillez choisir un autre fichier.", Toast.LENGTH_LONG).show();
        }
        return;
    }

    @Override
    public void onReadZonesComplete(String statut) {
        Log.e(TAG, "onReadZonesComplete: import zone statut=" + statut);

    }

    @Override
    public void onWriteComplete(String statut, List<String> pathFiles) {
        Log.e(TAG, "onReadArticlesComplete: statut=" + statut);
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (statut.equals("SUCCESS")) {
            Toast.makeText(MainActivity.this, "Articles exporté dans \"FlashInventory/FlashInventory Export\" avec succès.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Impossible d'ëxporter les articles, veuillez réessayer plus tard.", Toast.LENGTH_LONG).show();
        }
        return;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FlashInventoryUtility.makeDirsImport();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mModActualiser = findViewById(R.id.view_mod_actualiser);
        mModInventaire = findViewById(R.id.view_mod_inventaire);
        mModArticles = findViewById(R.id.view_mod_articles);
        mModExporter = findViewById(R.id.view_mod_exporter);

        mModActualiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e(TAG, "onClick: Mod Actualiser");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Voulez-vous vraiment Actualiser ?");
                builder.setMessage("Ceci supprimera toutes informations précédemment enreistrées.");
                builder.setIcon(R.drawable.ic_warning_black_24dp);
                builder.setPositiveButton("OUI",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

//                        .withFilter(false, false, "xls", "xlsx")
                                new ChooserDialog().with(MainActivity.this)
                                        .withFilter(false, false, "csv")
                                        .withResources(R.string.title_choose_file, R.string.title_choose, R.string.annuler)
                                        .withChosenListener(new ChooserDialog.Result() {
                                            @Override
                                            public void onChoosePath(String path, File pathFile) {
//                                Log.e(TAG, "onChoosePath: FILE: " + path);

                                                readExcelFileFromAssets(path);
                                            }
                                        })
                                        .build()
                                        .show();
                            }
                        });
                builder.setNegativeButton("NON",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setCancelable(false);
                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
        });

        mModArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ArticlesActivity.class);
                startActivity(intent);
            }
        });

        mModInventaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AjouterInventaireActivity.class);
                startActivity(intent);
            }
        });

        mModExporter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeExcelFileToStorage();
            }
        });

    }
}
