package com.ry.flashinventory.pages.ajouterinventaire;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.ry.flashinventory.R;
import com.ry.flashinventory.adapter.InventaireLineAdapter;
import com.ry.flashinventory.adapter.ZoneLineAdapter;
import com.ry.flashinventory.database.AppDatabase;
import com.ry.flashinventory.database.entry.ArticleEntry;
import com.ry.flashinventory.database.entry.ZoneEntry;
import com.ry.flashinventory.database.entry.ZoneLineEntry;
import com.ry.flashinventory.listener.InventaireLineAdapterListener;
import com.ry.flashinventory.listener.ZoneLineAdapterListener;
import com.ry.flashinventory.utils.FlashInventoryUtility;

import java.util.ArrayList;
import java.util.List;

public class AjouterInventaireActivity extends AppCompatActivity implements ZoneLineAdapterListener, InventaireLineAdapterListener {
    private static final String TAG = AjouterInventaireActivity.class.getSimpleName();

    private EditText mCodeBarreET, mCodeBarreZoneET, mQuantiteET;
    private ImageView mScanBarcodeIV;
    private RecyclerView mRecyclerViewArticles, mRecyclerViewZones;
    private Button mModifierBTN, mAjouterBTN, mAjouterZoneBTN, mNouvelleZoneBTN, mPagePrecBTN, mPageSvtBTN;
    private TextView mNouvelleZoneTV;

    private AlertDialog mAlertDialogZone;

    private int PAGE_LIMIT = 10;
    //    Adapter des articles
    private InventaireLineAdapter mInvLineAdapter;
    private ZoneLineAdapter mZoneLineAdapter;
    //    liste des articles affichés sur la vue
    private List<ZoneLineEntry> mInvLineEntryList;
    private List<ZoneEntry> mZoneEntryList;

    private ZoneEntry mZoneEntry;

    private AppDatabase mDb;

    public void requestActionUnExistArticle(final String barCodeArticle) {
        Log.e(TAG, "requestActionUnExistArticle: barcode="+barCodeArticle );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Article Absent")
                .setMessage("Cet article est absent. Que souhaitez-vous faire ?")
                .setCancelable(false)
                .setPositiveButton("ENREGISTRER", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        showDialogNewArticle(barCodeArticle);
                    }
                })
                .setNegativeButton("CONTINUER", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        final EditText codeET = new EditText(AjouterInventaireActivity.this);
                        final AlertDialog alertDialog = new AlertDialog.Builder(AjouterInventaireActivity.this).create();
                        alertDialog.setMessage("Veuillez entrer le mot de passe");
                        alertDialog.setView(codeET);
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "VALIDER", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Reset errors.
                                mCodeBarreZoneET.setError(null);

                                // Store values at the time of the login attempt.
                                String codeBarre = mCodeBarreZoneET.getText().toString();

                                boolean cancel = false;
                                View focusView = null;

                                // Teste de validité du login
                                if (!isCodeBarreValid(codeBarre)) {
                                    mCodeBarreZoneET.setError(getString(R.string.champs_invalide));
                                    focusView = mCodeBarreZoneET;
                                    cancel = true;
                                }

                                if (cancel) {
                                    // There was an error; don't attempt login and focus the first
                                    // form field with an error.
                                    focusView.requestFocus();
                                } else {
                                    // Show a progress spinner, and kick off a background task to
                                    // perform the user login attempt.

                                    String passwordCode = codeET.getText().toString();
                                    if (passwordCode.equals(FlashInventoryUtility.PASSWORD_ADD_ZONE)) {
//                                        startPdaScanZone(codeBarre);
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(AjouterInventaireActivity.this, "Mot de passe incorrect.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Annuler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                requestActionUnExistArticle(barCodeArticle);
                            }
                        });
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
    public void showDialogNewArticle(final String barCodeArticle) {
        View alertLayout = getLayoutInflater().inflate(R.layout.dialog_request_unexist_article, null);

        EditText codeBarreET = (EditText) alertLayout.findViewById(R.id.et_newarticle_codebarre);
        final EditText codeET = (EditText) alertLayout.findViewById(R.id.et_newarticle_code);
        final EditText desgnationET = (EditText) alertLayout.findViewById(R.id.et_newarticle_designation);
        final EditText marqueET = (EditText) alertLayout.findViewById(R.id.et_newarticle_marque);
        final EditText prixRevientET = (EditText) alertLayout.findViewById(R.id.et_newarticle_prixrevient);
        final EditText quantiteET = (EditText) alertLayout.findViewById(R.id.et_newarticle_quantite);
        Button annulerBTN = (Button) alertLayout.findViewById(R.id.btn_newarticle_annuler);
        Button enregitrerBTN = (Button) alertLayout.findViewById(R.id.btn_newarticle_enregistrer);

//        Initialisation du contenu de la vu
        codeBarreET.setFocusable(false);
        codeBarreET.setEnabled(false);
        codeBarreET.setCursorVisible(false);
        codeBarreET.setText(barCodeArticle);
        codeET.requestFocus();

        final AlertDialog alertDialog = new AlertDialog.Builder(AjouterInventaireActivity.this).create();

        alertDialog.setCancelable(false);
        alertDialog.setView(alertLayout);
        alertDialog.setTitle("Enregistrer Article");
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        annulerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                requestActionUnExistArticle(barCodeArticle);
            }
        });
        enregitrerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: desgnationET="+desgnationET.getText().toString() );

                int quantite = 1;
                double prix_revient = 0;
                ArticleEntry articleEntryInsert = new ArticleEntry();
                if (!quantiteET.getText().toString().equals("")) {
                    quantite = Integer.parseInt(quantiteET.getText().toString());
                }
                articleEntryInsert.setQuantite(quantite);

                if (!prixRevientET.getText().toString().equals("")) {
                    prix_revient = Double.parseDouble(prixRevientET.getText().toString());
                }
                articleEntryInsert.setPrix_revient(prix_revient);

                articleEntryInsert.setStatut(getResources().getString(R.string.absent));
                articleEntryInsert.setCode_article(codeET.getText().toString());
                articleEntryInsert.setCode_barre(barCodeArticle);
                articleEntryInsert.setIntitule(desgnationET.getText().toString());
                articleEntryInsert.setMarque(marqueET.getText().toString());

                Long articleId = mDb.articleDao().insertArticle(articleEntryInsert);
                ZoneLineEntry zoneLineEntry = new ZoneLineEntry();

                zoneLineEntry.setArticle_id(articleId);
                zoneLineEntry.setQuantite_line(quantite);
                zoneLineEntry.setZone_id(mZoneEntry.getId());

                addLineInventaire(zoneLineEntry);

                alertDialog.dismiss();
            }
        });
    }

    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(AjouterInventaireActivity.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withText("Scan du code barre...")
                .withBackfacingCamera()
                .withCenterTracker()
                .withTrackerColor(R.color.colorPrimary)
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
//                        barcodeResult = barcode;
                        String barCodeString = barcode.rawValue;
                        Log.e(TAG, "onResult: displayValue" + barcode.displayValue + " rawValue=" + barcode.rawValue);

                        ArticleEntry articleEntry = mDb.articleDao().getArticleByCodebarre(barCodeString);
                        if (articleEntry == null) {
                            ArticleEntry articleEntryInsert = new ArticleEntry();
                            articleEntryInsert.setQuantite(0);
                            articleEntry.setPrix_revient((double) 0);
                            articleEntry.setStatut(getResources().getString(R.string.absent));
                            articleEntry.setCode_article("");
                            articleEntry.setCode_barre(barCodeString);
                            articleEntry.setIntitule("");

                            Long articleId = mDb.articleDao().insertArticle(articleEntryInsert);
                            ZoneLineEntry zoneLineEntry = new ZoneLineEntry();

                            zoneLineEntry.setArticle_id(articleId);
                            zoneLineEntry.setQuantite_line(1);
                            zoneLineEntry.setZone_id(mZoneEntry.getId());
                            zoneLineEntry.setId((long) -1);

                            addLineInventaire(zoneLineEntry);
                        } else {
                            ZoneLineEntry zoneLineEntry = new ZoneLineEntry();

                            zoneLineEntry.setArticle_id(articleEntry.getId());
                            zoneLineEntry.setQuantite_line(1);
                            zoneLineEntry.setZone_id(mZoneEntry.getId());
                            zoneLineEntry.setId((long) -1);

                            addLineInventaire(zoneLineEntry);
                        }


                        mCodeBarreET.setText(barcode.rawValue);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    private void startPdaScan() {
        String pdaCodeBarre = mCodeBarreET.getText().toString();
        int pdaQuantite = mQuantiteET.getText().toString().equals("") ? 1 : Integer.parseInt(mQuantiteET.getText().toString());

        if (pdaCodeBarre == null || pdaCodeBarre.equals("")) {
            return;
        }

        String barCodeString = pdaCodeBarre.split("&")[0];
        Log.e(TAG, "startPdaScan: barCodeString=" + barCodeString +" pdaQuantite="+pdaQuantite);

        ArticleEntry articleEntry = mDb.articleDao().getArticleByCodebarre(barCodeString);
        if (articleEntry == null) {
            FlashInventoryUtility.playAssetSoundScanFail(AjouterInventaireActivity.this);
            Log.e(TAG, "startPdaScan: articleEntry not exist");

            requestActionUnExistArticle(barCodeString);
        } else {
            Log.e(TAG, "startPdaScan: articleEntry exist intitule="+articleEntry.getIntitule());
            if (articleEntry.getStatut().equals(getResources().getString(R.string.absent))) {
                FlashInventoryUtility.playAssetSoundScanFail(AjouterInventaireActivity.this);
            } else {
                FlashInventoryUtility.playAssetSoundScanSuccess(AjouterInventaireActivity.this);
            }
            ZoneLineEntry zoneLineEntry = new ZoneLineEntry();

            zoneLineEntry.setArticle_id(articleEntry.getId());
            zoneLineEntry.setQuantite_line(pdaQuantite);
            zoneLineEntry.setZone_id(mZoneEntry.getId());

            addLineInventaire(zoneLineEntry);
        }

        mCodeBarreET.setText("");
        mQuantiteET.setText("");
    }
    private void startPdaScanZone(String pdaCodeBarre) {
        if (pdaCodeBarre == null || pdaCodeBarre.equals("")) {
            return;
        }

        String barCodeString = pdaCodeBarre.split("&")[0];
        Log.e(TAG, "startPdaScanZone: barCodeString=" + barCodeString);

        ZoneEntry zoneEntry = mDb.zoneDao().getZoneByCodebarre(barCodeString);
        if (zoneEntry == null) {
            FlashInventoryUtility.playAssetSoundScanFail(AjouterInventaireActivity.this);

            ZoneEntry zoneEntryInsert = new ZoneEntry();
            zoneEntryInsert.setIntitule(barCodeString);
            zoneEntryInsert.setCode_barre(barCodeString);
            zoneEntryInsert.setQuantite(0);

            long zoneId = mDb.zoneDao().insertZone(zoneEntryInsert);
            zoneEntryInsert.setId(zoneId);
            mZoneEntry = zoneEntryInsert;
            Log.e(TAG, "startPdaScanZone: zoneEntry null id="+mZoneEntry.getId()+" codeBarre="+mZoneEntry.getCode_barre() );
            /*
            final AlertDialog.Builder builder = new AlertDialog.Builder(AjouterInventaireActivity.this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setTitle("Erreur Zone");
            alertDialog.setMessage("La zone scannée n'est pas valide.");
            alertDialog.setIcon(R.drawable.ic_qrcode);
            alertDialog.show(); */
        } else {
            mZoneEntry = zoneEntry;
            FlashInventoryUtility.playAssetSoundScanSuccess(AjouterInventaireActivity.this);

        }

        if (mAlertDialogZone != null) {
            mAlertDialogZone.cancel();
            mAlertDialogZone = null;
        }

        mNouvelleZoneTV.setText(mZoneEntry.getIntitule());

        initInventairelines();
        enableInput();
    }

    private void initInventairelines() {
        List<ZoneLineEntry> lineEntries = mDb.zoneLineDao().getZoneLinesByInv(mZoneEntry.getId());
        Log.e(TAG, "initInventairelines: lineEntries=" + lineEntries.size());


//        mInvLineEntryList.clear();
//        mInvLineEntryList.addAll(lineEntries);
        mInvLineAdapter.releaseData(lineEntries);
    }

    private void addLineInventaire(ZoneLineEntry lineEntry) {
        long lineId = mDb.zoneLineDao().insertInventaireLine(lineEntry);

//        lineEntry.setId(lineId);

        List<ZoneLineEntry> lineEntries = mDb.zoneLineDao().getZoneLinesByInv(mZoneEntry.getId());
        Log.e(TAG, "addLineInventaire: Id="+lineEntry.getId()+" Article_id="+lineEntry.getArticle_id()+" Quantite_line="+lineEntry.getQuantite_line()+" Zone_id="+lineEntry.getZone_id()+" linesEntires="+lineEntries.size() );
        mInvLineEntryList.clear();
        mInvLineEntryList.addAll(lineEntries);
        mInvLineAdapter.releaseData(lineEntries);
    }

    private void gotoPagePrec(){
        if (mZoneEntry == null) {
            Toast.makeText(AjouterInventaireActivity.this, getResources().getString(R.string.veuillez_scanner_zone), Toast.LENGTH_SHORT).show();
            return;
        }
        List<ZoneLineEntry> lineEntries = mDb.zoneLineDao().getZoneLinesByInv(mZoneEntry.getId());
        mInvLineAdapter.releasePrecData(lineEntries);
    }

    private void gotoPageSvt(){
        if (mZoneEntry == null) {
            Toast.makeText(AjouterInventaireActivity.this, getResources().getString(R.string.veuillez_scanner_zone), Toast.LENGTH_SHORT).show();
            return;
        }
        List<ZoneLineEntry> lineEntries = mDb.zoneLineDao().getZoneLinesByInv(mZoneEntry.getId());
        mInvLineAdapter.releaseNextData(lineEntries);
    }

    private void removeLineInventaire(ZoneLineEntry zoneLineEntry, int position) {
        mDb.zoneLineDao().deleteZoneLine(zoneLineEntry);
        initInventairelines();

//        if (mInvLineEntryList.size() > position) {
//            initInventairelines();
//        }
    }

    private void removeZone(ZoneEntry zoneEntry, int position) {
        mDb.zoneDao().deleteZone(zoneEntry);
        mDb.zoneLineDao().deleteZonelinesByIdZone(zoneEntry.getId());
        mZoneEntryList.remove(position);
        mZoneLineAdapter.notifyDataSetChanged();

//        if (mInvLineEntryList.size() > position) {
//            initInventairelines();
//        }
    }

    private void validateForm() {
        if (mZoneEntry == null) {
            Toast.makeText(AjouterInventaireActivity.this, getResources().getString(R.string.veuillez_scanner_zone), Toast.LENGTH_SHORT).show();
            return;
        }

        // Reset errors.
        mCodeBarreET.setError(null);
        mQuantiteET.setError(null);

        // Store values at the time of the login attempt.
        String codeBarre = mCodeBarreET.getText().toString();
        String quantite = mQuantiteET.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Teste de validité du login
        if (!isCodeBarreValid(codeBarre)) {
            mCodeBarreET.setError(getString(R.string.champs_invalide));
            focusView = mCodeBarreET;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!isQuantiteValid(quantite) && !cancel) {
            mQuantiteET.setError(getString(R.string.champs_invalide));
            focusView = mQuantiteET;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            startPdaScan();
        }
    }

    private void insertInvLine(String codebarre, String quantite) {
        ArticleEntry articleEntry = mDb.articleDao().getArticleByCodebarre(codebarre);
        if (articleEntry == null) {
            ArticleEntry articleEntryInsert = new ArticleEntry();
            articleEntryInsert.setQuantite(0);
            articleEntryInsert.setPrix_revient((double) 0);
            articleEntryInsert.setStatut(getResources().getString(R.string.absent));
            articleEntryInsert.setCode_article("");
            articleEntryInsert.setCode_barre(codebarre);
            articleEntryInsert.setIntitule("");

            Long articleId = mDb.articleDao().insertArticle(articleEntryInsert);
            ZoneLineEntry zoneLineEntry = new ZoneLineEntry();

            zoneLineEntry.setArticle_id(articleId);
            zoneLineEntry.setQuantite_line(1);
            zoneLineEntry.setZone_id(mZoneEntry.getId());

            addLineInventaire(zoneLineEntry);
        } else {
            ZoneLineEntry zoneLineEntry = new ZoneLineEntry();

            zoneLineEntry.setArticle_id(articleEntry.getId());
            zoneLineEntry.setQuantite_line(1);
            zoneLineEntry.setZone_id(mZoneEntry.getId());

            addLineInventaire(zoneLineEntry);
        }
    }

    private boolean isCodeBarreValid(String codebarre) {
        if (TextUtils.isEmpty(codebarre)) {
            return false;
        }
        return true;
    }

    private boolean isQuantiteValid(String quantite) {
        if (TextUtils.isEmpty(quantite)) {
            return false;
        }
        return true;
    }

    private void annulerinventaire(ZoneEntry zoneEntry) {
        mDb.zoneDao().deleteZone(zoneEntry);
        FlashInventoryUtility.playAssetSoundCancelledInventaire(AjouterInventaireActivity.this);
        Toast.makeText(AjouterInventaireActivity.this, "Inventaire annulé.", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void disableInput() {
        mCodeBarreET.setFocusable(false);
        mCodeBarreET.setEnabled(false);
        mCodeBarreET.setCursorVisible(false);

        mQuantiteET.setFocusable(false);
        mQuantiteET.setEnabled(false);
        mQuantiteET.setCursorVisible(false);
    }
    private void enableInput() {
        mCodeBarreET.setFocusable(true);
        mCodeBarreET.setFocusableInTouchMode(true);
        mCodeBarreET.setEnabled(true);
        mCodeBarreET.setCursorVisible(true);

        mQuantiteET.setFocusable(true);
        mQuantiteET.setEnabled(true);
        mQuantiteET.setCursorVisible(true);
        mQuantiteET.setFocusableInTouchMode(true);
    }

    @Override
    public void onDeleteInventaireLine(final ZoneLineEntry zoneLineEntry, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Voulez-vous vraiment supprimer cette ligne inventaire ?")
                .setCancelable(false)
                .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeLineInventaire(zoneLineEntry, position);
                    }
                })
                .setNegativeButton("NON", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }

    @Override
    public void onDeleteZoneLine(final ZoneEntry zoneEntry, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Voulez-vous vraiment supprimer cette zone ?")
                .setCancelable(false)
                .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeZone(zoneEntry, position);
                    }
                })
                .setNegativeButton("NON", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_inventaire);

        mDb = AppDatabase.getInstance(getApplicationContext());

//        Activer/Desactive le bouton retour sur le navbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRecyclerViewArticles = (RecyclerView) findViewById(R.id.recyclerview_ajouterinv);
        mScanBarcodeIV = (ImageView) findViewById(R.id.iv_ajouterinv_scan_barcode);
        mCodeBarreET = (EditText) findViewById(R.id.et_ajouterinv_codebarre);
        mQuantiteET = (EditText) findViewById(R.id.et_ajouterinv_quantite);
        mModifierBTN = (Button) findViewById(R.id.btn_ajouterinv_modifier);
        mAjouterBTN = (Button) findViewById(R.id.btn_ajouterinv_ajouter);
        mNouvelleZoneBTN = (Button) findViewById(R.id.btn_ajouterinv_ajouterzone);
        mNouvelleZoneTV = (TextView) findViewById(R.id.tv_ajouterinv_ajouterzone);
        mPagePrecBTN = (Button) findViewById(R.id.btn_page_precedente);
        mPageSvtBTN = (Button) findViewById(R.id.btn_page_suivante);

        disableInput();

        mInvLineEntryList = new ArrayList<>();
        mZoneEntryList = new ArrayList<>();

        mInvLineAdapter = new InventaireLineAdapter(AjouterInventaireActivity.this, AjouterInventaireActivity.this, PAGE_LIMIT);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AjouterInventaireActivity.this);
        mRecyclerViewArticles.setLayoutManager(mLayoutManager);
        mRecyclerViewArticles.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewArticles.setAdapter(mInvLineAdapter);

        mNouvelleZoneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View alertLayout = getLayoutInflater().inflate(R.layout.dialog_nouvellezone_layout, null);

                mRecyclerViewZones = (RecyclerView) alertLayout.findViewById(R.id.recyclerview_nouvellezone);
                mCodeBarreZoneET = (EditText) alertLayout.findViewById(R.id.et_nouvellezone);
                mAjouterZoneBTN = (Button) alertLayout.findViewById(R.id.btn_nouvellezone);

                mAjouterZoneBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final EditText codeET = new EditText(AjouterInventaireActivity.this);
                        final AlertDialog alertDialog = new AlertDialog.Builder(AjouterInventaireActivity.this).create();
                        alertDialog.setMessage("Veuillez entrer le mot de passe");
                        alertDialog.setView(codeET);
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "VALIDER", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Reset errors.
                                mCodeBarreZoneET.setError(null);

                                // Store values at the time of the login attempt.
                                String codeBarre = mCodeBarreZoneET.getText().toString();

                                boolean cancel = false;
                                View focusView = null;

                                // Teste de validité du login
                                if (!isCodeBarreValid(codeBarre)) {
                                    mCodeBarreZoneET.setError(getString(R.string.champs_invalide));
                                    focusView = mCodeBarreZoneET;
                                    cancel = true;
                                }

                                if (cancel) {
                                    // There was an error; don't attempt login and focus the first
                                    // form field with an error.
                                    focusView.requestFocus();
                                } else {
                                    // Show a progress spinner, and kick off a background task to
                                    // perform the user login attempt.

                                    String passwordCode = codeET.getText().toString();
                                    if (passwordCode.equals(FlashInventoryUtility.PASSWORD_ADD_ZONE)) {
                                        startPdaScanZone(codeBarre);
                                    } else {
                                        Toast.makeText(AjouterInventaireActivity.this, "Mot de passe incorrect.", Toast.LENGTH_SHORT).show();
                                    }

                                    dialog.cancel();
                                }
                            }
                        });
                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Annuler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();

                    }
                });
                mZoneEntryList = mDb.zoneDao().getAllZone();
                mZoneLineAdapter = new ZoneLineAdapter(AjouterInventaireActivity.this, AjouterInventaireActivity.this, mZoneEntryList);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AjouterInventaireActivity.this);
                mRecyclerViewZones.setLayoutManager(mLayoutManager);
                mRecyclerViewZones.setItemAnimator(new DefaultItemAnimator());
                mRecyclerViewZones.setAdapter(mZoneLineAdapter);

                mAlertDialogZone = new AlertDialog(AjouterInventaireActivity.this) {
                    @Override
                    public boolean onKeyUp(int keyCode, KeyEvent event) {
//                        Log.e(TAG, "onKey: keyCode=" + keyCode + " event=" + event.toString());
                        if (keyCode == 102) {
                            startPdaScanZone(mCodeBarreZoneET.getText().toString());
                            mCodeBarreZoneET.setText("");

                        } else {
                            mCodeBarreZoneET.setText("");
                        }
                        return super.onKeyUp(keyCode, event);
                    }
                };
//                alertDialog = builder.create();
                mAlertDialogZone.setButton(DialogInterface.BUTTON_NEGATIVE, "Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialogZone = null;
                    }
                });
                mAlertDialogZone.setCancelable(false);
                mAlertDialogZone.setView(alertLayout);
                mAlertDialogZone.setTitle("Nouvelle Zone");
                mAlertDialogZone.setCanceledOnTouchOutside(false);
                mAlertDialogZone.show();
            }
        });

//        initInventairelines();

        mCodeBarreET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mScanBarcodeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

//        ecoute click page precedente
        mPagePrecBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPagePrec();
            }
        });
//        ecoute click page suivante
        mPageSvtBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPageSvt();
            }
        });

        mAjouterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateForm();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        Log.e(TAG, "onSaveInstanceState: " + mZoneEntry.getId());

        outState.putParcelable("inventory", mZoneEntry);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ajouterinventaire_menu, menu); */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_save_inventaire:
//                saveInventaire();
                break;
            case R.id.action_delete_inventaire:
                annulerinventaire(mZoneEntry);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e(TAG, "onKeyUp: keyCode=" + keyCode + " getCharacters=" + event.getCharacters());
        if (keyCode == 102) {
//            String pdaCodeBarre = mCodeBarreET.getText().toString();
//            Log.e(TAG, "onKeyUp: pdaCodeBarre="+pdaCodeBarre+" toString="+event.toString()+" getKeyCharacterMap="+event.getKeyCharacterMap().toString()+
//            " getKeyCode="+event.getKeyCode()+
//            " getScanCode="+event.getScanCode()+
//            " getSource="+event.getSource()+
//            " getDisplayLabel="+event.getDisplayLabel()+
//            " getNumber="+event.getNumber()+
//            " getCharacters="+event.getCharacters());
            startPdaScan();
        } else {
//            mQuantiteET.setText("");
//            mCodeBarreET.setText("");
        }
        return super.onKeyUp(keyCode, event);
    }
}
