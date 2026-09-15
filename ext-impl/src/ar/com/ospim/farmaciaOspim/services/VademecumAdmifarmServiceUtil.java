package ar.com.ospim.farmaciaOspim.services;

import java.sql.Timestamp;
import java.util.List;

import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Registro;

public class VademecumAdmifarmServiceUtil {

    private static final VademecumAdmifarmService service =
            new VademecumAdmifarmServiceImpl();

    private VademecumAdmifarmServiceUtil() {
    }

    public static List<Object[]> getImportaciones(String tipo) throws Exception {
        return service.getImportaciones(tipo);
    }

    public static ImportacionVademecumAdmifarm importar(String tipo,
            List<Registro> registros) throws Exception {
        return service.importar(tipo, registros);
    }

    public static ImportacionVademecumAdmifarm getImportacion(String tipo,
            Timestamp fecha) throws Exception {
        return service.getImportacion(tipo, fecha);
    }
}
