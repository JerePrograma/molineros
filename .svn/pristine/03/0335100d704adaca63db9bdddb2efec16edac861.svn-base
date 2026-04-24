package ar.com.ospim.afiliados.services;

import java.sql.Connection;
import java.util.List;

import ar.com.ospim.global.beans.Telefono;

public class TelefonoServiceUtil {

    private static TelefonoServiceImpl instance = null;

    private static TelefonoServiceImpl getInstance() {
        if (instance == null) {
            instance = new TelefonoServiceImpl();
        }
        return instance;
    }

    public static List<Telefono> getTelefonos(String cuilTitular, int inte) throws Exception {
        return getInstance().getTelefonos(cuilTitular, inte);
    }

    public static void insertaTelefono(Connection con, String cuilTitular, int inte, Telefono tel, String user) throws Exception {
        getInstance().insertaTelefono(con, cuilTitular, inte, tel, user);
    }

    public static void actualizaTelefono(Connection con, String cuilTitular, int inte, Telefono tel, String user) throws Exception {
        getInstance().actualizaTelefono(con, cuilTitular, inte, tel, user);
    }
    
    public static void bajaTelefono(int idTelefono, String user) throws Exception {
        getInstance().bajaTelefono(idTelefono, user);
    }
}
