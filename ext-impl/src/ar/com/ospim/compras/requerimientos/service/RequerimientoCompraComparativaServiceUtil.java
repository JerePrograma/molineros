package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import java.util.List;

public class RequerimientoCompraComparativaServiceUtil {

    private static final RequerimientoCompraComparativaServiceImpl service =
            new RequerimientoCompraComparativaServiceImpl();

    public static List<RequerimientoCompraComparativa> listar(int idRequerimiento)
            throws Exception {
        return service.listar(idRequerimiento);
    }

    public static List<RequerimientoCompraComparativa> listar(int idRequerimiento,
            int idPrestador) throws Exception {
        return service.listar(idRequerimiento, idPrestador);
    }

    public static void guardar(List<RequerimientoCompraComparativa> comparativas,
            List<Integer> detallesVaciados, String usuario) throws Exception {
        service.guardar(comparativas, detallesVaciados, usuario);
    }
}
