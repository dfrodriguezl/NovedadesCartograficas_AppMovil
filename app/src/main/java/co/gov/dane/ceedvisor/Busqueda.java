package co.gov.dane.ceedvisor;

public class Busqueda {

    private String nombre;
    private String centroide;
    private int tipo_geometria;

    Busqueda(String nombre, String centroide,int tipo_geometria){

        this.centroide=centroide;
        this.nombre=nombre;
        this.tipo_geometria=tipo_geometria;


    }
    public String get_nombre(){return nombre;}

    public String get_centroide(){return centroide;}

    public int getTipo_geometria(){return tipo_geometria;}




}
