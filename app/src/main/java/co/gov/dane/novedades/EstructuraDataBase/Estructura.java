package co.gov.dane.novedades.EstructuraDataBase;

import android.provider.BaseColumns;

public class Estructura {

    public static abstract class UsuarioEntry implements BaseColumns {
        public static final String TABLE_NAME ="usuarios";
        public static final String ID = "id";
        public static final String USUARIO = "usuario";
        public static final String CLAVE = "clave";
        public static final String NOMBRE = "nombre";
        public static final String CORREO = "correo";
        public static final String VIGENCIA = "vigencia";
        public static final String ROL = "rol";

    }

    public static abstract class GeometriaEntry implements BaseColumns {
        public static final String TABLE_NAME ="geometria";
        public static final String ID_PADRE = "id_padre";
        public static final String ID_HIJO = "id_hijo";
        public static final String NOMBRE_ENCUESTA = "nombre_encuesta";
        public static final String NOMBRE_CAPA = "nombre_capa";
        public static final String ESTADO = "estado";
        public static final String OBSERVACIONES = "observaciones";
        public static final String GEOMETRIA_INI = "geometria_ini";
        public static final String USUARIO_ASIGNADO = "usuario_asignado";
    }

    public static abstract class NovedadEntry implements BaseColumns {
        public static final String TABLE_NAME ="novedad";
        public static final String ID_DISPOSITIVO ="id_dispositivo";
        public static final String ID = "id";
        public static final String TIPO_GEOMETRIA = "tipo_geometria";
        public static final String WKT = "wkt";
        public static final String TIPO = "tipo";
        public static final String DESCRIPCION = "descripcion";
        public static final String FECHA = "fecha";
        public static final String LAT_GPS = "lat_gps";
        public static final String LON_GPS = "lon_gps";
    }
    public static abstract class ObrasEntry implements BaseColumns {
        public static final String TABLE_NAME ="obras";
        public static final String SERIAL ="serial";
        public static final String FINICIO = "finicio";
        public static final String NOFORMULAR = "noformular";
        public static final String NOMBREOBRA = "nombreobra";
        public static final String DIREOBRA = "direobra";
        public static final String BARRIO = "barrio";
        public static final String GEOMETRIA = "geometria";


    }



}
