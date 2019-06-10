package co.gov.dane.danevisor.EstructuraDataBase;

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

    }

    public static abstract class NovedadEntry implements BaseColumns {
        public static final String TABLE_NAME ="novedad";
        public static final String ID = "id";
        public static final String TIPO_GEOMETRIA = "tipo_geometria";
        public static final String WKT = "wkt";
        public static final String TIPO = "tipo";
        public static final String DESCRIPCION = "descripcion";

    }



}
