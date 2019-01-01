package com.dataservicios.ttauditalicorptriada.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.dataservicios.ttauditalicorptriada.R;
import com.dataservicios.ttauditalicorptriada.db.DatabaseHelper;
import com.dataservicios.ttauditalicorptriada.db.DatabaseManager;
import com.dataservicios.ttauditalicorptriada.model.Audit;
import com.dataservicios.ttauditalicorptriada.model.AuditRoadStore;
import com.dataservicios.ttauditalicorptriada.model.Company;
import com.dataservicios.ttauditalicorptriada.model.Media;
import com.dataservicios.ttauditalicorptriada.model.Poll;
import com.dataservicios.ttauditalicorptriada.model.PollDetail;
import com.dataservicios.ttauditalicorptriada.model.PollOption;
import com.dataservicios.ttauditalicorptriada.model.Product;
import com.dataservicios.ttauditalicorptriada.model.Route;
import com.dataservicios.ttauditalicorptriada.model.Store;
import com.dataservicios.ttauditalicorptriada.repo.AuditRoadStoreRepo;
import com.dataservicios.ttauditalicorptriada.repo.CompanyRepo;
import com.dataservicios.ttauditalicorptriada.repo.PollOptionRepo;
import com.dataservicios.ttauditalicorptriada.repo.PollRepo;
import com.dataservicios.ttauditalicorptriada.repo.ProductRepo;
import com.dataservicios.ttauditalicorptriada.repo.RouteRepo;
import com.dataservicios.ttauditalicorptriada.repo.StoreRepo;
import com.dataservicios.ttauditalicorptriada.util.AuditUtil;
import com.dataservicios.ttauditalicorptriada.util.GPSTracker;
import com.dataservicios.ttauditalicorptriada.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jaime on 26/10/2016.
 */

public class PresenceProduct  extends AppCompatActivity {
    private static final String LOG_TAG = PresenceProduct.class.getSimpleName();

    private SessionManager          session;
    private Activity activity =  this;
    private ProgressDialog pDialog;
    private TextView tvStoreFullName,tvStoreId,tvAddress ,tvReferencia,tvDistrict,tvAuditoria,tvPoll ;
    private EditText etComent;
    private EditText etCommentOption;
    private Button btSaveGeo;
    private Button btSave;
    private ImageButton imgShared;
    private CheckBox[]              checkBoxArray;
    private RadioButton[]           radioButtonArray;
    private RadioGroup radioGroup;
    private Switch swYesNo;
    private ImageButton btPhoto;
    private LinearLayout lyComment;
    private LinearLayout lyContentOptions;
    private LinearLayout lyOptionComment;
    private int                     user_id;
    private int                     store_id;
    private int                     audit_id;
    private int                     company_id;
    private int                     orderPoll;
    private int                     category_product_id;
    private int                     publicity_id;
    private int                     product_id;
    private RouteRepo routeRepo ;
    private AuditRoadStoreRepo auditRoadStoreRepo ;
    private StoreRepo storeRepo ;
    private CompanyRepo companyRepo ;
    private PollRepo pollRepo ;
    private ProductRepo productRepo;
    private Route route ;
    private Store store ;
    private Poll poll;

    private PollOption pollOption;
    private PollDetail              pollDetail;
    private AuditRoadStore auditRoadStore;
    private PollOptionRepo pollOptionRepo;
    private GPSTracker gpsTracker;
    private int                     isYesNo;
    private String comment;
    private String selectedOptions;
    private String commentOptions;
    private ArrayList<Product> products;
    /**
     * Inicia una nueva instancia de la actividad
     *
     * @param activity Contexto desde donde se lanzará
     * @param company_id
     * @param audit_id
     * @param poll Objeti tipo poll
     */
    public static void createInstance(Activity activity, int company_id, int audit_id, Poll poll) {
        Intent intent = getLaunchIntent(activity, company_id,audit_id,poll);
        activity.startActivity(intent);
    }
    /**
     * Construye un Intent a partir del contexto y la actividad
     * de detalle.
     *
     * @param context Contexto donde se inicia
     * @param store_id
     * @param audit_id
     * @return retorna un Intent listo para usar
     */
    private static Intent getLaunchIntent(Context context, int store_id, int audit_id, Poll poll) {
        Intent intent = new Intent(context, PresenceProduct.class);
        intent.putExtra("store_id"              , store_id);
        intent.putExtra("audit_id"              , audit_id);
        intent.putExtra("orderPoll"             , poll.getOrder());
        intent.putExtra("category_product_id"   , poll.getCategory_product_id());
        intent.putExtra("publicity_id"          , poll.getPublicity_id());
        intent.putExtra("product_id"            , poll.getProduct_id());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presence_product);

        showToolbar(getString(R.string.title_activity_Stores_Audit),true);

        DatabaseManager.init(this);

        gpsTracker = new GPSTracker(activity);
        if(!gpsTracker.canGetLocation()){
            gpsTracker.showSettingsAlert();
        }

        Bundle bundle = getIntent().getExtras();
        store_id            = bundle.getInt("store_id");
        audit_id            = bundle.getInt("audit_id");
        orderPoll           = bundle.getInt("orderPoll");
        category_product_id = bundle.getInt("category_product_id");
        publicity_id        = bundle.getInt("publicity_id");
        product_id          = bundle.getInt("product_id");

        session = new SessionManager(activity);
        HashMap<String, String> userSesion = session.getUserDetails();
        user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;

        routeRepo           = new RouteRepo(activity);
        storeRepo           = new StoreRepo(activity);
        companyRepo         = new CompanyRepo(activity);
        auditRoadStoreRepo  = new AuditRoadStoreRepo(activity);
        pollRepo            = new PollRepo(activity);
        pollOptionRepo      = new PollOptionRepo((activity));
        productRepo         = new ProductRepo(activity);

        etCommentOption     = new EditText(activity);
        etComent            = new EditText(activity);

        ArrayList<Company> companies = (ArrayList<Company>) companyRepo.findAll();
        for (Company c: companies){
            company_id = c.getId();
        }

        tvStoreFullName     = (TextView)    findViewById(R.id.tvStoreFullName) ;
        tvStoreId           = (TextView)    findViewById(R.id.tvStoreId) ;
        tvAddress           = (TextView)    findViewById(R.id.tvAddress) ;
        tvReferencia        = (TextView)    findViewById(R.id.tvReferencia) ;
        tvDistrict          = (TextView)    findViewById(R.id.tvDistrict) ;
        tvAuditoria         = (TextView)    findViewById(R.id.tvAuditoria) ;
        tvPoll              = (TextView)    findViewById(R.id.tvPoll) ;
        btSaveGeo           = (Button)      findViewById(R.id.btSaveGeo);
        btSave              = (Button)      findViewById(R.id.btSave);
        btPhoto             = (ImageButton) findViewById(R.id.btPhoto);
        swYesNo             = (Switch)      findViewById(R.id.swYesNo);
        lyComment           = (LinearLayout)findViewById(R.id.lyComment);
        lyContentOptions           = (LinearLayout)findViewById(R.id.lyContentOptions);
        lyOptionComment     = (LinearLayout)findViewById(R.id.lyOptionComment);
        imgShared           = (ImageButton) findViewById(R.id.imgShared);

        store               = (Store)               storeRepo.findById(store_id);
        route               = (Route)               routeRepo.findById(store.getRoute_id());
        auditRoadStore      = (AuditRoadStore)      auditRoadStoreRepo.findByStoreIdAndAuditId(store_id,audit_id);
        poll                = (Poll)                pollRepo.findByCompanyAuditIdAndOrder(auditRoadStore.getList().getCompany_audit_id(),orderPoll);
        products            = (ArrayList<Product>)  productRepo.findAll();

        poll.setCategory_product_id(category_product_id);
        poll.setProduct_id(product_id);
        poll.setPublicity_id(publicity_id);

        tvStoreFullName.setText(String.valueOf(store.getFullname()));
        tvStoreId.setText(String.valueOf(store.getId()));
        tvAddress.setText(String.valueOf(store.getAddress()));
        tvReferencia.setText(String.valueOf(store.getUrbanization()));
        tvDistrict.setText(String.valueOf(store.getDistrict()));
        tvAuditoria.setText(auditRoadStore.getList().getFullname().toString());
        tvPoll.setText(poll.getQuestion().toString());

        imgShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "ID Store: " + store.getId() + " \nTienda: " + store.getFullname()  ;
                String shareSub = "Ruta";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_TITLE, shareBody);
                activity.startActivity(Intent.createChooser(sharingIntent, "Share using"));

            }
        });

//        btPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                takePhoto();
//            }
//        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedOptions = "";
                int valueChecked = 0;
                if(checkBoxArray != null) {

                    for(CheckBox r: checkBoxArray) {
                        if(r.isChecked()){ valueChecked = 1 ;  } else { valueChecked = 0 ;};
                        selectedOptions += r.getTag().toString() + "-" + String.valueOf(valueChecked) + "|";
                        // counterSelected ++;
                    }
//                    if(counterSelected==0){
//                        Toast.makeText(activity, R.string.message_select_options, Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar la lista de publicidades: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {


//                        Toast.makeText(activity, selectedOptions , Toast.LENGTH_SHORT).show();

                        pollDetail = new PollDetail();
                        pollDetail.setPoll_id(poll.getId());
                        pollDetail.setStore_id(store_id);
                        pollDetail.setSino(1);
                        pollDetail.setOptions(0);
                        pollDetail.setLimits(0);
                        pollDetail.setMedia(0);
                        pollDetail.setComment(0);
                        pollDetail.setResult(0);
                        pollDetail.setLimite("0");
                        pollDetail.setComentario(selectedOptions);
                        pollDetail.setAuditor(user_id);
                        pollDetail.setProduct_id(0);
                        pollDetail.setCategory_product_id(0);
                        pollDetail.setPublicity_id(0);
                        pollDetail.setCompany_id(company_id);
                        pollDetail.setCommentOptions(0);
                        pollDetail.setSelectdOptions("");
                        pollDetail.setSelectedOtionsComment("");
                        pollDetail.setPriority(0);

                        new loadPoll().execute();
                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                builder.setCancelable(false);
            }
        });

        loadCotrolProducts();

    }

    private void loadCotrolProducts() {

        lyContentOptions.removeAllViews();


        if(products.size() > 0) {
            checkBoxArray = new CheckBox[products.size()];

            int counter =0;

            for(Product p: products){
                checkBoxArray[counter] = new CheckBox(activity);
                checkBoxArray[counter].setText(p.getFullname().toString());
                checkBoxArray[counter].setTag(String.valueOf(p.getId()));
//                    if(po.getComment()==1) {
                checkBoxArray[counter].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                lyContentOptions.addView(checkBoxArray[counter]);
                counter ++;
            }
            // lyOptions.addView(radioGroup);

        }
    }

    private void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }

    private void takePhoto() {

        Media media = new Media();
        media.setStore_id(store_id);
        media.setPoll_id(poll.getId());
        media.setCompany_id(company_id);
        media.setType(1);
        AndroidCustomGalleryActivity.createInstance((Activity) activity, media);
    }


    class loadPoll extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando Product...");
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getString(R.string.text_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub


            if(!AuditUtil.insertPollDetailAllProduct(pollDetail)) return false;
            if(!AuditUtil.closeAuditStore(audit_id,store_id,company_id,route.getId())) return  false; // (store_id,audit_id, rout_id)) return false ;



            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){

                AuditRoadStore auditRoadStore = (AuditRoadStore) auditRoadStoreRepo.findByStoreIdAndAuditId(store_id,audit_id);
                auditRoadStore.setAuditStatus(1);
                auditRoadStoreRepo.update(auditRoadStore);
                finish();


            } else {
                Toast.makeText(activity , R.string.saveSuccess, Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
        }
    }

    /**
     * Guarda la pregunta segun el orden en casos
     * @param orderPoll
     * @return
     */
    private boolean logicProcess(int orderPoll) {

        pollDetail = new PollDetail();
        pollDetail.setPoll_id(poll.getId());
        pollDetail.setStore_id(store_id);
        pollDetail.setSino(poll.getSino());
        pollDetail.setOptions(poll.getOptions());
        pollDetail.setLimits(0);
        pollDetail.setMedia(poll.getMedia());
        pollDetail.setComment(0);
        pollDetail.setResult(isYesNo);
        pollDetail.setLimite("0");
        pollDetail.setComentario(comment);
        pollDetail.setAuditor(user_id);
        pollDetail.setProduct_id(poll.getProduct_id());
        pollDetail.setCategory_product_id(poll.getCategory_product_id());
        pollDetail.setPublicity_id(poll.getPublicity_id());
        pollDetail.setCompany_id(company_id);
        pollDetail.setCommentOptions(poll.getComment());
        pollDetail.setSelectdOptions(selectedOptions);
        pollDetail.setSelectedOtionsComment(commentOptions);
        pollDetail.setPriority(0);

        switch (orderPoll) {
            case 1:
                if (isYesNo == 1) {
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                } else if (isYesNo == 0) {
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    if (!AuditUtil.closeAllAuditRoadStore(store_id, company_id)) return false;
                }
                break;
            case 2:
                if (isYesNo == 1) {
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                } else if (isYesNo == 0) {
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    if (!AuditUtil.closeAllAuditRoadStore(store_id, company_id)) return false;
                }
                break;

        }
        return true;
    }




    @Override
    public void onBackPressed() {

        if (poll.getOrder() > 6) {
            alertDialogBasico(getString(R.string.message_audit_init) );

        } else {
            super.onBackPressed();
        }

    }
    private void alertDialogBasico(String message) {

        // 1. Instancia de AlertDialog.Builder con este constructor
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        // 2. Encadenar varios métodos setter para ajustar las características del diálogo
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        builder.show();

    }
}
