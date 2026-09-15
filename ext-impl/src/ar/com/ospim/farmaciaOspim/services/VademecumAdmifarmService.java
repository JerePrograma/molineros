package ar.com.ospim.farmaciaOspim.services;

import java.sql.Timestamp;
import java.util.List;

import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Registro;

public interface VademecumAdmifarmService {

    // Cada elemento contiene Timestamp fecha y Long cantidad, en ese orden.
    public List<Object[]> getImportaciones(String tipo) throws Exception;

    // Conserva el historico y reemplaza el vigente en una sola transaccion.
    public ImportacionVademecumAdmifarm importar(String tipo,
            List<Registro> registros) throws Exception;

    public ImportacionVademecumAdmifarm getImportacion(String tipo,
            Timestamp fecha) throws Exception;
}
