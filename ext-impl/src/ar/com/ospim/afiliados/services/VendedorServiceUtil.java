package ar.com.ospim.afiliados.services;

import java.util.List;
import java.util.Map;

public class VendedorServiceUtil {

    private static VendedorServiceImpl _service = new VendedorServiceImpl();

    public static List<Map<String, Object>> buscarVendedores(
        String nombre,
        String apellido,
        String dni
    ) throws Exception {
        return _service.buscarVendedores(nombre, apellido, dni);
    }

    public static Map<String, Object> getVendedor(Long id) throws Exception {
        return _service.getVendedor(id);
    }

    public static List<Map<String, Object>> getHistorico(Long idVendedor) throws Exception {
        return _service.getHistorico(idVendedor);
    }

    public static Long guardarVendedor(
        Long id,
        String nombre,
        String apellido,
        String dni,
        String email,
        String horaDesde, 
        String horaHasta,
        String usuario
    ) throws Exception {
        return _service.guardarVendedor(id, nombre, apellido, dni, email, horaDesde, horaHasta, usuario);
    }

    public static void darBaja(Long idVendedor, String usuario) throws Exception {
        _service.darBaja(idVendedor, usuario);
    }

    public static Long guardarHistorico(
        Long idHistorico,
        Long idVendedor,
        String fechaDesde,
        String fechaHasta,
        String motivo,
        String observacion,
        String usuario
    ) throws Exception {
        return _service.guardarHistorico(
            idHistorico,
            idVendedor,
            fechaDesde,
            fechaHasta,
            motivo,
            observacion,
            usuario
        );
    }
    
    public static void eliminarHistorico(Long idHistorico, String usuario) throws Exception {
        _service.eliminarHistorico(idHistorico, usuario);
    }
}