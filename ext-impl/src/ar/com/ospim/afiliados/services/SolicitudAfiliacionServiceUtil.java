package ar.com.ospim.afiliados.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SolicitudAfiliacionServiceUtil {

    private static SolicitudAfiliacionServiceUtil instance = null;

    private static SolicitudAfiliacionServiceUtil getInstance() {
        if (instance == null) {
            instance = new SolicitudAfiliacionServiceUtil();
        }
        return instance;
    }

    private SolicitudAfiliacionServiceUtil() {
    }

    public static Map<String, Object> getSolicitudById(long idSolicitud) throws Exception {
        getInstance();
        return SolicitudAfiliacionServiceImpl.getSolicitudById(idSolicitud);
    }

    public static List<Map<String, Object>> getHistorialBySolicitudId(long idSolicitud) throws Exception {
        getInstance();
        return SolicitudAfiliacionServiceImpl.getHistorialBySolicitudId(idSolicitud);
    }

    public static List<Map<String, Object>> buscarSolicitudesComercial(
            String desde,
            String hasta,
            String nombre,
            String dni,
            String estadoSolicitud,
            String provincia,
            String molinero,
            String ddjj,
            String vendedor
    ) throws Exception {
        getInstance();
        return SolicitudAfiliacionServiceImpl.buscarSolicitudesComercial(
                desde,
                hasta,
                nombre,
                dni,
                estadoSolicitud,
                provincia,
                molinero,
                ddjj,
                vendedor
        );
    }

    public static void guardarSeguimientoSolicitud(
            long idSolicitud,
            String estado,
            String nota,
            String usuario
    ) throws Exception {
        getInstance();
        SolicitudAfiliacionServiceImpl.guardarSeguimientoSolicitud(idSolicitud, estado, nota, usuario);
    }

    public static List<Map<String, Object>> getVendedoresActivos() throws Exception {
        getInstance();
        return SolicitudAfiliacionServiceImpl.getVendedoresActivos();
    }

    public static void derivarSolicitud(
            long idSolicitud,
            long idVendedorDestino,
            String usuario,
            String nota
    ) throws Exception {
        getInstance();
        SolicitudAfiliacionServiceImpl.derivarSolicitud(idSolicitud, idVendedorDestino, usuario, nota);
    }

    public static void desasignarSolicitud(long idSolicitud, String usuario, String nota) throws Exception {
        getInstance();
        SolicitudAfiliacionServiceImpl.desasignarSolicitud(idSolicitud, usuario, nota);
    }
    
    public static List<Map<String, Object>> getTodosLosVendedores() throws Exception {
        getInstance();
        return SolicitudAfiliacionServiceImpl.getTodosLosVendedores();
    }
    
    public static Long getIdVendedorByEmail(String email) throws Exception {
        getInstance();
        return SolicitudAfiliacionServiceImpl.getIdVendedorByEmail(email);
    }
    
    public static void actualizarFormularioAfiliado(
    	    Long idSolicitud,
    	    String nombre,
    	    String apellido,
    	    String dni,
    	    String email,
    	    String codigoArea,
    	    String telefono,
    	    String provincia,
    	    String plan,
    	    BigDecimal sueldoBruto,
    	    Boolean relacionDependencia,
    	    Boolean tienePareja,
    	    Integer edadPareja,
    	    Boolean tieneHijos,
    	    Integer cantidadHijos21,
    	    Integer cantidadHijos25,
    	    Boolean esMolinero,
    	    String usuario
    	) throws Exception {
    	    getInstance();
    	    SolicitudAfiliacionServiceImpl.actualizarFormularioAfiliado(
    	        idSolicitud,
    	        nombre,
    	        apellido,
    	        dni,
    	        email,
    	        codigoArea,
    	        telefono,
    	        provincia,
    	        plan,
    	        sueldoBruto,
    	        relacionDependencia,
    	        tienePareja,
    	        edadPareja,
    	        tieneHijos,
    	        cantidadHijos21,
    	        cantidadHijos25,
    	        esMolinero,
    	        usuario
    	    );
    	}
    
    public static void generarLinkDdjjSolicitud(Long idSolicitud) throws Exception {
        getInstance();
        SolicitudAfiliacionServiceImpl.generarLinkDdjjSolicitud(idSolicitud);
    }
}