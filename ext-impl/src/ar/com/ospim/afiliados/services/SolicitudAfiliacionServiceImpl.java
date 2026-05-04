package ar.com.ospim.afiliados.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.ospim.util.ConnectionHelper;

import org.apache.log4j.Logger;

public class SolicitudAfiliacionServiceImpl {

    private static final Logger _log = Logger.getLogger(SolicitudAfiliacionServiceImpl.class);

    public static Map<String, Object> getSolicitudById(long idSolicitud) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionHelper.getConnection();

            ps = conn.prepareStatement("SELECT * FROM comercial.get_solicitud_by_id(?)");
            ps.setLong(1, idSolicitud);

            rs = ps.executeQuery();
            if (!rs.next()) return null;

            Map<String, Object> m = new HashMap<String, Object>();

            m.put("id_solicitud", rs.getLong("id_solicitud"));
            m.put("estado", rs.getString("estado_solicitud"));
            m.put("fecha_ingreso", rs.getTimestamp("fecha_ingreso"));

            m.put("id_interesado", rs.getLong("id_interesado"));
            m.put("nombre", rs.getString("nombre"));
            m.put("apellido", rs.getString("apellido"));
            m.put("plan", rs.getString("plan"));
            m.put("dni", rs.getString("dni"));

            m.put("fecha_nacimiento", rs.getObject("fecha_nacimiento"));
            m.put("edad", rs.getObject("edad"));

            m.put("codigo_area", rs.getString("codigo_area"));
            m.put("telefono", rs.getString("telefono"));
            m.put("email", rs.getString("email"));
            m.put("provincia", rs.getString("provincia"));

            m.put("relacion_dependencia", rs.getObject("relacion_dependencia"));
            m.put("es_molinero", rs.getObject("es_molinero"));
            m.put("tiene_pareja", rs.getObject("tiene_pareja"));
            m.put("edad_pareja", rs.getObject("edad_pareja"));
            m.put("tiene_hijos", rs.getObject("tiene_hijos"));
            m.put("cantidad_hijos21", rs.getObject("cantidad_hijos21"));
            m.put("cantidad_hijos25", rs.getObject("cantidad_hijos25"));
            m.put("sueldo_bruto", rs.getObject("sueldo_bruto"));

            m.put("id_vendedor", rs.getObject("id_vendedor"));
            m.put("vendedor", rs.getString("vendedor"));

            m.put("ddjj_id", rs.getObject("ddjj_id"));
            m.put("ddjj_token", rs.getString("ddjj_token"));
            m.put("ddjj_estado", rs.getString("ddjj_estado"));
            m.put("ddjj_url", rs.getString("ddjj_url"));
            m.put("pdf_url", rs.getString("pdf_url"));
            m.put("tiene_ddjj", rs.getObject("tiene_ddjj"));

            m.put("url_solicitud", rs.getString("url_solicitud"));
            m.put("pdf_solicitud", rs.getString("pdf_solicitud"));
            m.put("contrato_estado", rs.getString("contrato_estado"));
            m.put("contrato_pdf", rs.getString("contrato_pdf"));
            m.put("contrato_url", rs.getString("contrato_url"));

            return m;

        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            ConnectionHelper.cerrar(ps);
            ConnectionHelper.cerrar(conn);
        }
    }
    

    public static List<Map<String, Object>> getHistorialBySolicitudId(long idSolicitud) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionHelper.getConnection();

            ps = conn.prepareStatement("SELECT * FROM comercial.get_historial_by_solicitud_id(?)");
            ps.setLong(1, idSolicitud);

            rs = ps.executeQuery();

            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            while (rs.next()) {
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("id", rs.getLong("id"));
                m.put("id_solicitud", rs.getLong("id_solicitud"));
                m.put("id_interesado", rs.getLong("id_interesado"));
                m.put("id_vendedor", rs.getObject("id_vendedor"));
                m.put("vendedor", rs.getString("vendedor"));
                m.put("usuario", rs.getString("usuario"));
                m.put("estado", rs.getString("estado_solicitud"));
                m.put("nota", rs.getString("nota"));
                m.put("fecha_creacion", rs.getTimestamp("alta_fecha"));
                list.add(m);
            }

            return list;

        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            ConnectionHelper.cerrar(ps);
            ConnectionHelper.cerrar(conn);
        }
    }

    private static String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
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

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionHelper.getConnection();

            ps = conn.prepareStatement("SELECT * FROM comercial.buscar_solicitudes_comercial(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, emptyToNull(desde));
            ps.setString(2, emptyToNull(hasta));
            ps.setString(3, emptyToNull(nombre));
            ps.setString(4, emptyToNull(dni));
            ps.setString(5, emptyToNull(estadoSolicitud));
            ps.setString(6, emptyToNull(provincia));
            ps.setString(7, emptyToNull(molinero));
            ps.setString(8, emptyToNull(ddjj));
            ps.setString(9, emptyToNull(vendedor));

            rs = ps.executeQuery();

            List<Map<String, Object>> resultados = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<String, Object>();

                m.put("id_solicitud", rs.getLong("id_solicitud"));
                m.put("id_interesado", rs.getLong("id_interesado"));
                m.put("fecha_ingreso", rs.getTimestamp("fecha_ingreso"));

                m.put("nombre", rs.getString("nombre"));
                m.put("dni", rs.getString("dni"));
                m.put("telefono", rs.getString("telefono"));
                m.put("email", rs.getString("email"));
                m.put("provincia", rs.getString("provincia"));
                m.put("es_molinero", rs.getObject("es_molinero"));

                m.put("estado", rs.getString("estado_solicitud"));

                m.put("id_vendedor", rs.getObject("id_vendedor"));
                m.put("vendedor", rs.getString("vendedor"));

                m.put("ddjj_id", rs.getObject("ddjj_id"));
                m.put("ddjj_token", rs.getString("ddjj_token"));
                m.put("ddjj_estado", rs.getString("ddjj_estado"));
                m.put("tiene_ddjj", rs.getObject("tiene_ddjj"));

                resultados.add(m);
            }

            return resultados;

        } catch (Exception e) {
            _log.error("Error buscarSolicitudesComercial", e);
            throw e;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            ConnectionHelper.cerrar(ps);
            ConnectionHelper.cerrar(conn);
        }
    }
    
    public static void guardarSeguimientoSolicitud(long idSolicitud, String estado, String nota, String usuario) throws Exception {
        Connection conn = null;
        CallableStatement cs = null;

        try {
            conn = ConnectionHelper.getConnection();

            String sql = "{? = call comercial.guardar_seguimiento_solicitud(?, ?, ?, ?)}";
            cs = conn.prepareCall(sql);
            cs.registerOutParameter(1, Types.BOOLEAN);
            cs.setLong(2, idSolicitud);
            cs.setString(3, estado);
            cs.setString(4, emptyToNull(nota));
            cs.setString(5, usuario);

            cs.execute();

            boolean ok = cs.getBoolean(1);
            if (!ok) {
                throw new RuntimeException("No se pudo guardar seguimiento de solicitud. id=" + idSolicitud);
            }

        } catch (Exception e) {
            _log.error("Error guardarSeguimientoSolicitud", e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(cs);
            ConnectionHelper.cerrar(conn);
        }
    }
    
    public static List<Map<String, Object>> getTodosLosVendedores() throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionHelper.getConnection();

            ps = conn.prepareStatement("SELECT * FROM comercial.get_todos_los_vendedores()");
            rs = ps.executeQuery();

            List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("id", rs.getLong("id"));
                m.put("nombre", rs.getString("nombre"));
                m.put("apellido", rs.getString("apellido"));
                m.put("dni", rs.getString("dni"));
                m.put("baja_fecha", rs.getTimestamp("baja_fecha"));

                out.add(m);
            }
            
            return out;

        } catch (Exception e) {
            e.printStackTrace();
            _log.error("Error getTodosLosVendedores", e);
            throw e;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            ConnectionHelper.cerrar(ps);
            ConnectionHelper.cerrar(conn);
        }
    }
    
    public static List<Map<String, Object>> getVendedoresActivos() throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionHelper.getConnection();

            ps = conn.prepareStatement("SELECT * FROM comercial.get_vendedores_disponibles()");
            rs = ps.executeQuery();

            List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("id", rs.getLong("id"));
                m.put("nombre", rs.getString("nombre"));
                m.put("apellido", rs.getString("apellido"));
                m.put("dni", rs.getString("dni"));
                out.add(m);
            }

            return out;

        } catch (Exception e) {
            _log.error("Error getVendedoresActivos", e);
            throw e;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            ConnectionHelper.cerrar(ps);
            ConnectionHelper.cerrar(conn);
        }
    }
    
    
    
    public static void derivarSolicitud(long idSolicitud, long idVendedorDestino, String usuario, String nota) throws Exception {
        Connection conn = null;
        CallableStatement cs = null;

        try {
            conn = ConnectionHelper.getConnection();

            String sql = "{? = call comercial.derivar_solicitud(?, ?, ?, ?)}";
            cs = conn.prepareCall(sql);
            cs.registerOutParameter(1, Types.BOOLEAN);
            cs.setLong(2, idSolicitud);
            cs.setLong(3, idVendedorDestino);
            cs.setString(4, usuario);
            cs.setString(5, emptyToNull(nota));
            cs.execute();

            boolean ok = cs.getBoolean(1);
            if (!ok) {
                throw new RuntimeException("No se pudo derivar la solicitud. id=" + idSolicitud);
            }

        } catch (Exception e) {
            _log.error("Error derivarSolicitud", e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(cs);
            ConnectionHelper.cerrar(conn);
        }
    }
    
    public static void desasignarSolicitud(long idSolicitud, String usuario, String nota) throws Exception {
        Connection conn = null;
        CallableStatement cs = null;

        try {
            conn = ConnectionHelper.getConnection();

            String sql = "{? = call comercial.desasignar_solicitud(?, ?, ?)}";
            cs = conn.prepareCall(sql);
            cs.registerOutParameter(1, Types.BOOLEAN);
            cs.setLong(2, idSolicitud);
            cs.setString(3, usuario);
            cs.setString(4, emptyToNull(nota));
            cs.execute();

            boolean ok = cs.getBoolean(1);
            if (!ok) {
                throw new RuntimeException("No se pudo desasignar la solicitud. id=" + idSolicitud);
            }

        } catch (Exception e) {
            _log.error("Error desasignarSolicitud", e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(cs);
            ConnectionHelper.cerrar(conn);
        }
    }

    public static Long getIdVendedorByEmail(String email) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionHelper.getConnection();

            ps = conn.prepareStatement(
                "SELECT id FROM comercial.vendedor WHERE upper(email) = upper(?) AND baja_fecha IS NULL"
            );
            ps.setString(1, email);

            rs = ps.executeQuery();

            if (rs.next()) {
                return Long.valueOf(rs.getLong("id"));
            }

            return null;

        } catch (Exception e) {
            _log.error("Error getIdVendedorByEmail", e);
            throw e;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            ConnectionHelper.cerrar(ps);
            ConnectionHelper.cerrar(conn);
        }
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

    	    Connection conn = null;
    	    PreparedStatement ps = null;
    	    CallableStatement cs = null;
    	    ResultSet rs = null;

    	    try {
    	        conn = ConnectionHelper.getConnection();
    	        conn.setAutoCommit(false);

    	        Long idInteresado = null;

    	        ps = conn.prepareStatement(
    	            "select id_interesado from comercial.solicitud_afiliacion where id = ?"
    	        );
    	        ps.setLong(1, idSolicitud.longValue());
    	        rs = ps.executeQuery();

    	        if (rs.next()) {
    	            idInteresado = Long.valueOf(rs.getLong(1));
    	        }

    	        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
    	        ConnectionHelper.cerrar(ps);
    	        rs = null;
    	        ps = null;

    	        if (idInteresado == null || idInteresado.longValue() <= 0) {
    	            throw new Exception("No se encontró el interesado para la solicitud.");
    	        }

    	        cs = conn.prepareCall(
    	        	    "{ call comercial.interesado_actualizar_datos(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }"
    	        	);
    	        	cs.setLong(1, idInteresado.longValue());
    	        	cs.setString(2, nombre);
    	        	cs.setString(3, apellido);
    	        	cs.setString(4, dni);
    	        	cs.setString(5, email);
    	        	cs.setString(6, codigoArea);
    	        	cs.setString(7, telefono);
    	        	cs.setString(8, provincia);
    	        	cs.setString(9, plan);

    	        	if (sueldoBruto == null) cs.setNull(10, Types.NUMERIC);
    	        	else cs.setBigDecimal(10, sueldoBruto);

    	        	if (relacionDependencia == null) cs.setNull(11, Types.BOOLEAN);
    	        	else cs.setBoolean(11, relacionDependencia.booleanValue());

    	        	if (tienePareja == null) cs.setNull(12, Types.BOOLEAN);
    	        	else cs.setBoolean(12, tienePareja.booleanValue());

    	        	if (edadPareja == null) cs.setNull(13, Types.INTEGER);
    	        	else cs.setInt(13, edadPareja.intValue());

    	        	if (tieneHijos == null) cs.setNull(14, Types.BOOLEAN);
    	        	else cs.setBoolean(14, tieneHijos.booleanValue());

    	        	if (cantidadHijos21 == null) cs.setNull(15, Types.INTEGER);
    	        	else cs.setInt(15, cantidadHijos21.intValue());

    	        	if (cantidadHijos25 == null) cs.setNull(16, Types.INTEGER);
    	        	else cs.setInt(16, cantidadHijos25.intValue());

    	        	if (esMolinero == null) cs.setNull(17, Types.BOOLEAN);
    	        	else cs.setBoolean(17, esMolinero.booleanValue());

    	        	if (usuario == null || usuario.trim().isEmpty()) {
    	        	    cs.setNull(18, Types.VARCHAR);
    	        	} else {
    	        	    cs.setString(18, usuario);
    	        	}
    	        	
    	        cs.execute();

    	        conn.commit();

    	    } catch (Exception e) {
    	        ConnectionHelper.rollback(conn);
    	        _log.error("Error actualizarFormularioAfiliado", e);
    	        throw e;
    	    } finally {
    	        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
    	        ConnectionHelper.cerrar(ps);
    	        ConnectionHelper.cerrar(cs);
    	        try { if (conn != null) conn.setAutoCommit(true); } catch (Exception ignored) {}
    	        ConnectionHelper.cerrar(conn);
    	    }
    	}
    
    public static void generarLinkDdjjSolicitud(Long idSolicitud) throws Exception {
        Connection conn = null;
        CallableStatement cs = null;

        try {
            conn = ConnectionHelper.getConnection();
            cs = conn.prepareCall("{ call comercial.ddjj_generar_link_por_solicitud(?) }");
            cs.setLong(1, idSolicitud.longValue());
            cs.execute();
        } finally {
            ConnectionHelper.cerrar(cs);
            ConnectionHelper.cerrar(conn);
        }
    }
}
