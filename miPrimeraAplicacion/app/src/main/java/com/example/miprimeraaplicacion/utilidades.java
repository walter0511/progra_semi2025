package com.example.miprimeraaplicacion;
import java.util.Base64;

public class utilidades {
    static String url_consulta = "http://192.168.1.14:5984/walter/_design/walter/_view/steven";
    static String url_mto = "http://192.168.1.14:5984/walter";
    static String user = "walter";
    static String passwd = "walcy";
    static String credencialesCodificadas = Base64.getEncoder().encodeToString((user + ":" + passwd).getBytes());
    public String generarUnicoId(){
        return java.util.UUID.randomUUID().toString();
    }
}