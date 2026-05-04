package ar.com.ospim.afiliados.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.util.ConnectionHelper;

public class AfiliacionesServiceImpl {

    private static final Log _log = LogFactoryUtil.getLog(AfiliacionesServiceImpl.class);

    public Map<String, Object> guardarSolicitud(
    	    Long idInteresado,
    	    Long idSolicitud,
    	    String nombre,
    	    String apellido,
    	    Integer edad,
    	    String fechaNacimiento,
    	    String dni,
    	    String codigoArea,
    	    String telefono,
    	    String provincia,
    	    String plan,
    	    String email,
    	    Boolean relacionDependencia,
    	    Boolean tienePareja,
    	    Integer edadPareja,
    	    Boolean tieneHijos,
    	    Integer cantidadHijos21,
    	    Integer cantidadHijos25,
    	    BigDecimal sueldoBruto,
    	    String montoEstimado,
    	    Boolean esMolinero,
    	    boolean generarDdjj,
    	    String usuario
    	) throws Exception {

    	    Connection con = null;
    	    CallableStatement cs = null;

    	    Long idDdjj = null;
    	    String token = null;
    	    String ddjjUrl = null;

    	    try {
    	        con = ConnectionHelper.getConnection();
    	        con.setAutoCommit(false);

    	        BigDecimal montoEstimadoBD = null;
    	        if (montoEstimado != null && !montoEstimado.trim().isEmpty()) {
    	            try {
    	                montoEstimadoBD = new BigDecimal(montoEstimado.trim().replace(",", "."));
    	            } catch (Exception e) {
    	                throw new Exception("Monto estimado inválido.");
    	            }
    	        }

    	        boolean esAltaNueva = (idInteresado == null && idSolicitud == null);

    	        if (esAltaNueva) {
    	            // INSERT INTERESADO
    	        	cs = con.prepareCall(
    	        		    "{ ? = call comercial.interesado_insertar(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }"
    	        		);
    	            cs.registerOutParameter(1, Types.BIGINT);

    	            cs.setString(2, nombre);
    	            cs.setString(3, apellido);
    	            setNullableInteger(cs, 4, edad);

    	            if (fechaNacimiento == null || fechaNacimiento.trim().isEmpty()) {
    	                cs.setNull(5, Types.DATE);
    	            } else {
    	                cs.setDate(5, java.sql.Date.valueOf(fechaNacimiento));
    	            }

    	            cs.setString(6, dni);
    	            setNullableString(cs, 7, codigoArea);
    	            cs.setString(8, telefono);
    	            cs.setString(9, email);
    	            cs.setString(10, provincia);
    	            cs.setString(11, plan);
    	            setNullableBoolean(cs, 12, relacionDependencia);
    	            setNullableBoolean(cs, 13, esMolinero);
    	            setNullableBoolean(cs, 14, tienePareja);
    	            setNullableInteger(cs, 15, edadPareja);
    	            setNullableBoolean(cs, 16, tieneHijos);
    	            setNullableInteger(cs, 17, cantidadHijos21);
    	            setNullableInteger(cs, 18, cantidadHijos25);
    	            setNullableBigDecimal(cs, 19, sueldoBruto);
    	            setNullableBigDecimal(cs, 20, montoEstimadoBD);

    	            // campos que existen en la función pero no vienen del formulario corto
    	            setNullableString(cs, 21, null); // estado_civil
    	            setNullableString(cs, 22, null); // calle
    	            setNullableString(cs, 23, null); // numero
    	            setNullableString(cs, 24, null); // piso
    	            setNullableString(cs, 25, null); // dpto
    	            setNullableString(cs, 26, null); // barrio
    	            setNullableString(cs, 27, null); // localidad
    	            setNullableString(cs, 28, null); // cp
    	            setNullableString(cs, 29, null); // cuil
    	            setNullableString(cs, 30, null); // sexo
    	            setNullableString(cs, 31, null); // nacionalidad
    	            setNullableString(cs, 32, null); // laboral_cuit
    	            setNullableString(cs, 33, null); // laboral_razon_social
    	            cs.setNull(34, Types.DATE);      // laboral_fecha_ingreso

    	            if (usuario == null || usuario.trim().isEmpty()) {
    	                cs.setNull(35, Types.VARCHAR);
    	            } else {
    	                cs.setString(35, usuario);
    	            }

    	            cs.execute();
    	            idInteresado = Long.valueOf(cs.getLong(1));
    	            closeQuietly(cs);

    	            // INSERT SOLICITUD
    	            String estadoSolicitudInicial = Boolean.TRUE.equals(esMolinero) ? "pendiente" : "incompleto";

    	            cs = con.prepareCall("{ ? = call comercial.solicitud_insertar(?, ?) }");
    	            cs.registerOutParameter(1, Types.BIGINT);
    	            cs.setLong(2, idInteresado.longValue());
    	            cs.setString(3, estadoSolicitudInicial);
    	            cs.execute();
    	            idSolicitud = Long.valueOf(cs.getLong(1));
    	            closeQuietly(cs);
    	            

    	        } else {
    	            if (idInteresado == null) {
    	                throw new Exception("Falta idInteresado para actualizar la solicitud.");
    	            }
    	            if (idSolicitud == null) {
    	                throw new Exception("Falta idSolicitud para actualizar la solicitud.");
    	            }

    	            // UPDATE INTERESADO
    	            cs = con.prepareCall(
    	            	    "{ call comercial.interesado_actualizar(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }"
    	            	);

    	            	cs.setLong(1, idInteresado);
    	            	cs.setString(2, nombre);
    	            	cs.setString(3, apellido);
    	            	setNullableInteger(cs, 4, edad);

    	            	if (fechaNacimiento == null || fechaNacimiento.trim().isEmpty()) {
    	            	    cs.setNull(5, Types.DATE);
    	            	} else {
    	            	    cs.setDate(5, java.sql.Date.valueOf(fechaNacimiento));
    	            	}

    	            	cs.setString(6, dni);
    	            	setNullableString(cs, 7, codigoArea);
    	            	cs.setString(8, telefono);
    	            	cs.setString(9, email);
    	            	cs.setString(10, provincia);
    	            	cs.setString(11, plan);
    	            	setNullableBoolean(cs, 12, relacionDependencia);
    	            	setNullableBoolean(cs, 13, esMolinero);
    	            	setNullableBoolean(cs, 14, tienePareja);
    	            	setNullableInteger(cs, 15, edadPareja);
    	            	setNullableBoolean(cs, 16, tieneHijos);
    	            	setNullableInteger(cs, 17, cantidadHijos21);
    	            	setNullableInteger(cs, 18, cantidadHijos25);
    	            	setNullableBigDecimal(cs, 19, sueldoBruto);
    	            	setNullableBigDecimal(cs, 20, montoEstimadoBD);

    	            	setNullableString(cs, 21, null); // estado_civil
    	            	setNullableString(cs, 22, null); // calle
    	            	setNullableString(cs, 23, null); // numero
    	            	setNullableString(cs, 24, null); // piso
    	            	setNullableString(cs, 25, null); // dpto
    	            	setNullableString(cs, 26, null); // barrio
    	            	setNullableString(cs, 27, null); // localidad
    	            	setNullableString(cs, 28, null); // cp
    	            	setNullableString(cs, 29, null); // cuil
    	            	setNullableString(cs, 30, null); // sexo
    	            	setNullableString(cs, 31, null); // nacionalidad
    	            	setNullableString(cs, 32, null); // laboral_cuit
    	            	setNullableString(cs, 33, null); // laboral_razon_social
    	            	cs.setNull(34, Types.DATE);      // laboral_fecha_ingreso

    	            	if (usuario == null || usuario.trim().isEmpty()) {
    	            	    cs.setNull(35, Types.VARCHAR);
    	            	} else {
    	            	    cs.setString(35, usuario);
    	            	}

    	            	cs.execute();
    	            	closeQuietly(cs);

    	            	if (!Boolean.TRUE.equals(esMolinero)) {
    	            	    cs = con.prepareCall("{ ? = call comercial.asignar_solicitud_automatica(?, ?, ?) }");
    	            	    cs.registerOutParameter(1, Types.BIGINT);
    	            	    cs.setLong(2, idSolicitud.longValue());
    	            	    cs.setLong(3, idInteresado.longValue());
    	            	    cs.setString(4, dni);
    	            	    cs.execute();
    	            	    closeQuietly(cs);
    	            	} else {
    	            	    cs = con.prepareCall("{ call comercial.solicitud_actualizar(?, ?) }");
    	            	    cs.setLong(1, idSolicitud);
    	            	    cs.setString(2, "pendiente");
    	            	    cs.execute();
    	            	    closeQuietly(cs);
    	            	}
    	        }

    	        // CREAR DDJJ SI CORRESPONDE
    	        if (generarDdjj) {
    	            token = UUID.randomUUID().toString().replace("-", "");
    	            //ddjjUrl = "http://localhost/ospim/alta-online/ddjj_form.php?token=" + token;
    	            //ddjjUrl = "http://localhost:5173/ddjj/formulario?token=" + token;
    	            
    	            ddjjUrl = "https://lumasalud.ar/ddjj/formulario?token=" + token;
    	            
    	            cs = con.prepareCall("{ ? = call comercial.ddjj_insertar(?, ?, ?) }");
    	            cs.registerOutParameter(1, Types.BIGINT);
    	            cs.setLong(2, idSolicitud.longValue());
    	            cs.setString(3, token);
    	            cs.setString(4, ddjjUrl);
    	            cs.execute();
    	            idDdjj = Long.valueOf(cs.getLong(1));
    	            closeQuietly(cs);
    	        }

    	        con.commit();

    	        Map<String, Object> out = new HashMap<String, Object>();
    	        out.put("idInteresado", idInteresado);
    	        out.put("idSolicitud", idSolicitud);
    	        out.put("idDdjj", idDdjj);
    	        out.put("token", token);
    	        out.put("ddjjUrl", ddjjUrl);

    	        _log.info("guardarSolicitud() idInteresado=" + idInteresado + " idSolicitud=" + idSolicitud + " dni=" + dni);
    	        return out;

    	    } catch (Exception e) {
    	        rollbackQuietly(con);
    	        _log.error("Error guardarSolicitud", e);
    	        throw e;
    	    } finally {
    	        closeQuietly(cs);
    	        resetAutoCommitQuietly(con);
    	        closeQuietly(con);
    	    }
    	}

    public void guardarPdfSolicitud(
    	    Long idSolicitud,
    	    String pdfSolicitud,
    	    String urlSolicitud,
    	    String modiUsr
    	) throws Exception {

    	    Connection con = null;
    	    CallableStatement cs = null;

    	    try {
    	        con = ConnectionHelper.getConnection();
    	        cs = con.prepareCall("{ call comercial.solicitud_pdf_guardar(?, ?, ?, ?) }");
    	        cs.setLong(1, idSolicitud);
    	        cs.setString(2, pdfSolicitud);
    	        cs.setString(3, urlSolicitud);
    	        if (modiUsr == null || modiUsr.trim().isEmpty()) {
    	            cs.setNull(4, Types.VARCHAR);
    	        } else {
    	            cs.setString(4, modiUsr);
    	        }
    	        cs.execute();
    	    } finally {
    	        closeQuietly(cs);
    	        closeQuietly(con);
    	    }
    	}
    
    public void guardarPdfDdjj(
    	    String token,
    	    String pdfUrl
    	) throws Exception {

    	    Connection con = null;
    	    CallableStatement cs = null;

    	    try {
    	        con = ConnectionHelper.getConnection();
    	        cs = con.prepareCall("{ call comercial.ddjj_pdf_guardar(?, ?) }");
    	        cs.setString(1, token);
    	        cs.setString(2, pdfUrl);
    	        cs.execute();
    	    } finally {
    	        closeQuietly(cs);
    	        closeQuietly(con);
    	    }
    	}
    
    public void crearDdjjPorSolicitud(
    	    Long idSolicitud,
    	    String token,
    	    String ddjjUrl
    	) throws Exception {
    	    Connection conn = null;
    	    CallableStatement cs = null;

    	    try {
    	        conn = ConnectionHelper.getConnection();
    	        cs = conn.prepareCall("{ call comercial.ddjj_crear_por_solicitud(?, ?, ?) }");
    	        cs.setLong(1, idSolicitud.longValue());
    	        cs.setString(2, token);
    	        cs.setString(3, ddjjUrl);
    	        cs.execute();
    	    } finally {
    	        ConnectionHelper.cerrar(cs);
    	        ConnectionHelper.cerrar(conn);
    	    }
    	}
    
    private void setNullableString(PreparedStatement ps, int idx, String value) throws Exception {
        if (value == null || value.trim().isEmpty()) ps.setNull(idx, Types.VARCHAR);
        else ps.setString(idx, value.trim());
    }

    private void setNullableBoolean(PreparedStatement ps, int idx, Boolean value) throws Exception {
        if (value == null) ps.setNull(idx, Types.BOOLEAN);
        else ps.setBoolean(idx, value.booleanValue());
    }

    private void setNullableInteger(PreparedStatement ps, int idx, Integer value) throws Exception {
        if (value == null) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, value.intValue());
    }

    private void setNullableBigDecimal(PreparedStatement ps, int idx, BigDecimal value) throws Exception {
        if (value == null) ps.setNull(idx, Types.NUMERIC);
        else ps.setBigDecimal(idx, value);
    }

    private void closeQuietly(CallableStatement cs) {
        try { if (cs != null) cs.close(); } catch (Exception ignored) {}
    }

    private void closeQuietly(Connection con) {
        try { if (con != null) con.close(); } catch (Exception ignored) {}
    }

    private void rollbackQuietly(Connection con) {
        try { if (con != null) con.rollback(); } catch (Exception ignored) {}
    }

    private void resetAutoCommitQuietly(Connection con) {
        try { if (con != null) con.setAutoCommit(true); } catch (Exception ignored) {}
    }
}