package com.dataservicios.ttauditalicorptriada.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dataservicios.ttauditalicorptriada.app.AppController;
import com.dataservicios.ttauditalicorptriada.model.Media;
import com.dataservicios.ttauditalicorptriada.repo.MediaRepo;
import com.dataservicios.ttauditalicorptriada.util.AuditUtil;
import com.dataservicios.ttauditalicorptriada.util.BitmapLoader;
import com.dataservicios.ttauditalicorptriada.util.Connectivity;

import java.io.File;
import java.util.ArrayList;

public class UpdateService extends Service {


    private final String LOG_TAG = UpdateService.class.getSimpleName();
    private final Integer contador = 0;

    private Context context = this;

    static final int DELAY =  60000; //2 minutos de espera
    //static final int DELAY = 9000; //9 segundo de espera
    private boolean runFlag = false;
    private Updater updater;

    private AppController application;

    private MediaRepo mediaRepo;
    private Media media;
    private AuditUtil auditUtil;
    private ArrayList<Media> medias;
    private File file;

    public UpdateService() {
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = (AppController) getApplication();
        updater = new Updater();
        mediaRepo = new MediaRepo(this);
        media = new Media();
        auditUtil = new AuditUtil(context);
        Log.d(LOG_TAG, "onCreated");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        runFlag = false;
        application.setServiceRunningFlag(false);
        updater.interrupt();
        updater = null;
        Log.d(LOG_TAG, "onDestroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!runFlag){
            runFlag = true;
            application.setServiceRunningFlag(true);
            updater.start();
        }

        Log.d(LOG_TAG, "onStarted");
        return START_STICKY;
    }

//    private class Updater extends Thread {
//        public Updater(){
//            super("UpdaterService-UpdaterThread");
//        }
//
//
//        @Override
//        public void run() {
//
//            UpdateService updaterService = UpdateService.this;
//            while (updaterService.runFlag) {
//                Log.d(TAG, "UpdaterThread running");
//                try{
//
//                    if(Connectivity.isConnected(context)) {
//                        if (Connectivity.isConnectedFast(context)) {
//
//                            Log.i(TAG," Conexión rápida" );
//                            if (mediaRepo.countReg() >0 ) {
//                                media = (Media) mediaRepo.findFirstReg();
//                                boolean response = auditUtil.uploadMedia(media,1);
//                                if (response) {
//                                    mediaRepo.delete(media);
//                                    Log.i(TAG," Send success images database server and delete local database and file " );
//                                }
//                            } else{
//                                Log.i(TAG, "No found records in media table for send");
//                            }
//
//                        }else {
//                            Log.i(TAG," Connectivity slow" );
//                        }
//                    } else {
//                        Log.i(TAG," No internet connection" );
//                    }
//                    Thread.sleep(DELAY);
//                }catch(InterruptedException e){
//                    updaterService.runFlag = false;
//                    application.setServiceRunningFlag(true);
//                }
//
//            }
//        }
//
//
//    }


    private class Updater extends Thread {
        public Updater(){
            super("UpdaterService-UpdaterThread");
        }

        @Override
        public void run() {

            UpdateService updaterService = UpdateService.this;
            while (updaterService.runFlag) {
                Log.d(LOG_TAG, "UpdaterThread running");
                try{
                    if(Connectivity.isConnected(context)) {
                        if (Connectivity.isConnectedFast(context)) {
                            Log.i(LOG_TAG," Conexión rápida" );
                            if (mediaRepo.countReg() >0 ) {
                                media.setId(0);
                                medias = (ArrayList<Media>) mediaRepo.findAll();
                                for (Media m: medias){
                                    file = null;
                                    file = new File(BitmapLoader.getAlbumDirTemp(context).getAbsolutePath() + "/" + m.getFile());
                                    if(file.exists()){
                                        media = m;
                                        break;
                                    }
                                }
//
                                if (media.getId() != 0){
                                    // NOTA eliminar  de "auditUtil.uploadMedia"
                                    // la eliminación de archivos
                                    //  file.delete() para controlar la eliminación en base de datos
                                    //boolean response = auditUtil.uploadMedia(media,1);
                                    boolean response = auditUtil.sendUploadPhotoServer(media);
                                    if (response) {
                                        file = null;
                                        file = new File(BitmapLoader.getAlbumDirTemp(context).getAbsolutePath() + "/" + media.getFile());
                                        if(file.exists()){
                                            file.delete();
                                            mediaRepo.delete(media);
                                        }
                                        Log.i(LOG_TAG," Se envió correctamente los datos al servidor, se eliminó el registro en la base de datos, y se eliminó  el archivo " );
                                    } else {
                                        Log.i(LOG_TAG," El servidor responde falso, no se pudo enviar el archivo al servidor " );
                                    }
                                }
                            } else{
                                Log.i(LOG_TAG, "No se encontró registros media para el envío");
                            }

                        }else {
                            Log.i(LOG_TAG," COnexión a internet Lenta" );
                        }
                    } else {
                        Log.i(LOG_TAG,"No hay conexión a internet" );
                    }
                    Thread.sleep(DELAY);
                }catch(InterruptedException e){
                    updaterService.runFlag = false;
                    application.setServiceRunningFlag(true);
                }
            }
        }
    }
}
