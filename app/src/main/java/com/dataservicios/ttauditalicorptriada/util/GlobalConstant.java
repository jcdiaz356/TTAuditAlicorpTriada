package com.dataservicios.ttauditalicorptriada.util;
/**
 * Created by usuario on 11/11/2014.
 */
public final class GlobalConstant {

    public static String dominio = "http://ttaudit.com";
    //public static String dominio = "http://192.168.1.73";
    // public static String dominio = "http://192.168.1.73/ttaudit.com/ttauditSystemAuditor";
    //public static String dominio = "http://local.ttaudit.com";
    public static final String URL_USER_IMAGES = dominio + "/media/users/";
    public static final String URL_PUBLICITY_IMAGES = dominio + "/media/images/alicorp/publicities/";

    //  Prefijos para las imagenes
    public static final String JPEG_FILE_PREFIX = "_triada_";
    public static final String JPEG_FILE_SUFFIX = ".jpg";

    //  Directorios para  almacenamiento
    public static final String ALBUN_NAME = "triada_Photo";
    public static final String ALBUN_NAME_TEMP = "triada_Photo_Temp";
    public static final String ALBUN_NAME_BACKUP = "triada_Photo_Backup";
    public static final String DATA_BASE_DIR_NAME_BACKUP = "triada_db_Backup";

    //   Variable  apps externos
    public static final String MARKET_OPEN_APP_ESFILEEXPLORE = "market://details?id=com.estrongs.android.pop&hl=es";
    public static final String DATABASE_PATH_DIR ="/data/com.dataservicios.ttauditalicorptriada/databases/db_triada";
}