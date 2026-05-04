package ar.com.ospim.afiliados.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.util.ConnectionHelper;

public class DDJJServiceImpl {

    private static final Log _log = LogFactoryUtil.getLog(DDJJServiceImpl.class);

    public void cambiarEstado(String token, String nuevoEstado) throws Exception {
        Connection con = null;
        CallableStatement cs = null;

        try {
            con = ConnectionHelper.getConnection();

            cs = con.prepareCall("{ ? = call comercial.ddjj_cambiar_estado(?, ?) }");
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, token);
            cs.setString(3, nuevoEstado);
            cs.execute();

            int rows = cs.getInt(1);
            if (rows < 1) {
                throw new RuntimeException("No se pudo cambiar el estado de la DDJJ");
            }

        } finally {
            closeQuietly(cs);
            closeQuietly(con);
        }
    }

    public Map<String, Object> getByToken(String token) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();

            ps = con.prepareStatement("SELECT * FROM comercial.ddjj_get_token(?)");
            ps.setString(1, token);

            rs = ps.executeQuery();
            if (!rs.next()) return null;

            Map<String, Object> datos = mapCurrentRow(rs);
            closeQuietly(rs);
            closeQuietly(ps);

            Long idDdjj = Long.valueOf(((Number) datos.get("ddjj_id")).longValue());

            // Grupo familiar
            List<Map<String, Object>> familiares = new ArrayList<Map<String, Object>>();
            ps = con.prepareStatement("SELECT * FROM comercial.familiares_ddjj(?)");
            ps.setLong(1, idDdjj.longValue());

            rs = ps.executeQuery();
            while (rs.next()) {
                familiares.add(mapCurrentRow(rs));
            }
            datos.put("grupo_familiar", familiares);

            closeQuietly(rs);
            closeQuietly(ps);

            // Salud
            Map<String, Map<String, String>> respuestas = new LinkedHashMap<String, Map<String, String>>();
            Map<String, Map<String, String>> biometricos = new LinkedHashMap<String, Map<String, String>>();
            List<Map<String, Object>> saludRespuestas = new ArrayList<Map<String, Object>>();

            ps = con.prepareStatement("SELECT * FROM comercial.salud_ddjj(?)");
            ps.setLong(1, idDdjj.longValue());

            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = mapCurrentRow(rs);
                saludRespuestas.add(row);

                String inte = row.get("inte") == null ? "" : String.valueOf(row.get("inte"));
                String col = mapInteFront(inte);
                String idEnfermedad = row.get("id_enfermedad") == null ? "" : String.valueOf(row.get("id_enfermedad"));
                String respuesta = row.get("respuesta") == null ? "" : String.valueOf(row.get("respuesta"));
                String itemTexto = row.get("item_texto") == null ? "" : String.valueOf(row.get("item_texto"));
                Object valorNumeroObj = row.get("valor_numero");

                if (col.isEmpty() || idEnfermedad.isEmpty()) {
                    continue;
                }

                if ("Altura".equalsIgnoreCase(itemTexto) || "Peso".equalsIgnoreCase(itemTexto)) {
                    Map<String, String> bio = biometricos.get(col);
                    if (bio == null) {
                        bio = new LinkedHashMap<String, String>();
                        biometricos.put(col, bio);
                    }

                    String valorNumero = valorNumeroObj == null ? "" : String.valueOf(valorNumeroObj);

                    if ("Altura".equalsIgnoreCase(itemTexto)) {
                        bio.put("altura", valorNumero);
                    } else if ("Peso".equalsIgnoreCase(itemTexto)) {
                        bio.put("peso", valorNumero);
                    }
                } else {
                    Map<String, String> fila = respuestas.get(idEnfermedad);
                    if (fila == null) {
                        fila = new LinkedHashMap<String, String>();
                        respuestas.put(idEnfermedad, fila);
                    }
                    fila.put(col, "si".equalsIgnoreCase(respuesta) ? "si" : "no");
                }
            }

            closeQuietly(rs);
            closeQuietly(ps);

            // Observaciones
            Map<String, String> observacionesSeccion = new LinkedHashMap<String, String>();

            ps = con.prepareStatement("SELECT * FROM comercial.salud_observacion_ddjj(?)");
            ps.setLong(1, idDdjj.longValue());

            rs = ps.executeQuery();
            while (rs.next()) {
                int sec = rs.getInt("seccion_nro");
                String obs = rs.getString("observacion");
                observacionesSeccion.put(String.valueOf(sec), obs == null ? "" : obs);
            }

            datos.put("salud_respuestas", saludRespuestas);

            Map<String, Object> salud = new LinkedHashMap<String, Object>();
            salud.put("respuestas", respuestas);
            salud.put("observaciones_seccion", observacionesSeccion);
            salud.put("biometricos", biometricos);

            datos.put("salud", salud);

            return datos;

        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(con);
        }
    }

    private String mapInteFront(String inteDb) {
        inteDb = safe(inteDb).trim();
        if ("0".equals(inteDb)) return "tit";
        if ("1".equals(inteDb)) return "01";
        if ("2".equals(inteDb)) return "02";
        if ("3".equals(inteDb)) return "03";
        if ("4".equals(inteDb)) return "04";
        if ("5".equals(inteDb)) return "05";
        return "";
    }

    public long guardarPaso1(
        String token,
        String plan,
        String nombre,
        String apellido,
        String email,
        String dni,
        String cuil,
        String codArea,
        String telefono,
        String fechaNacimiento,
        String sexo,
        String nacionalidad,
        String estadoCivil,
        String calle,
        String numero,
        String piso,
        String dpto,
        String barrio,
        String localidad,
        String provincia,
        String cp,
        String montoEstimado,
        BigDecimal sueldoBruto,
        String laboralCuit,
        String laboralRazonSocial,
        String laboralFechaIngreso,
        String grupoFamiliarJson,
        String usuario
    ) throws Exception {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        CallableStatement cs = null;

        Long idDdjj = null;
        Long idSolicitud = null;
        Long idInteresado = null;
        Integer edadCalculada = calcularEdadDesdeFecha(fechaNacimiento);

        BigDecimal montoEstimadoBD = null;
        if (montoEstimado != null && !montoEstimado.trim().isEmpty()) {
            try {
                montoEstimadoBD = new BigDecimal(montoEstimado.trim().replace(",", "."));
            } catch (Exception e) {
                throw new Exception("Monto estimado inválido.");
            }
        }
        
        try {
            con = ConnectionHelper.getConnection();
            con.setAutoCommit(false);

            
            //resolver ids por token
            ps = con.prepareStatement("SELECT * FROM comercial.ddjj_resolver_ids_por_token(?)");
            ps.setString(1, token);
            rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("DDJJ no encontrada para el token indicado");
            }

            idDdjj = Long.valueOf(rs.getLong("id_ddjj"));
            idSolicitud = Long.valueOf(rs.getLong("id_solicitud"));
            idInteresado = Long.valueOf(rs.getLong("id_interesado"));

            closeQuietly(rs);
            closeQuietly(ps);

            //actualizar interesado
            cs = con.prepareCall("{ ? = call comercial.interesado_actualizar_paso1(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");
            cs.registerOutParameter(1, Types.INTEGER);

            cs.setLong(2, idInteresado.longValue());
            cs.setString(3, nombre);
            cs.setString(4, apellido);
            cs.setString(5, email);
            cs.setString(6, dni);
            setNullableString(cs, 7, cuil);
            setNullableString(cs, 8, codArea);
            setNullableString(cs, 9, telefono);
            setNullableInteger(cs, 10, edadCalculada);

            if (fechaNacimiento == null || fechaNacimiento.trim().isEmpty()) {
                cs.setNull(11, Types.DATE);
            } else {
                cs.setDate(11, java.sql.Date.valueOf(fechaNacimiento));
            }

            setNullableString(cs, 12, sexo);
            setNullableString(cs, 13, nacionalidad);
            setNullableString(cs, 14, estadoCivil);
            setNullableString(cs, 15, calle);
            setNullableString(cs, 16, numero);
            setNullableString(cs, 17, piso);
            setNullableString(cs, 18, dpto);
            setNullableString(cs, 19, barrio);
            setNullableString(cs, 20, localidad);
            setNullableString(cs, 21, provincia);
            setNullableString(cs, 22, cp);
            setNullableString(cs, 23, plan);
            setNullableBigDecimal(cs, 24, montoEstimadoBD);
            setNullableBigDecimal(cs, 25, sueldoBruto);
            setNullableString(cs, 26, laboralCuit);
            setNullableString(cs, 27, laboralRazonSocial);

            if (laboralFechaIngreso == null || laboralFechaIngreso.trim().isEmpty()) {
                cs.setNull(28, Types.DATE);
            } else {
                cs.setDate(28, java.sql.Date.valueOf(laboralFechaIngreso));
            }
            
            if (usuario == null || usuario.trim().isEmpty()) {
        	    cs.setNull(29, Types.VARCHAR);
        	} else {
        	    cs.setString(29, usuario);
        	}
            
            cs.execute();

            int rowsUpdate = cs.getInt(1);
            if (rowsUpdate < 1) {
                throw new RuntimeException("No se pudo actualizar el interesado");
            }
            closeQuietly(cs);

            //borrar familiares
            cs = con.prepareCall("{ ? = call comercial.familiar_eliminar_ddjj(?) }");
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(2, idDdjj.longValue());
            cs.execute();
            closeQuietly(cs);

            //insertar familiares
            JSONArray arr = new JSONArray(grupoFamiliarJson == null ? "[]" : grupoFamiliarJson);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject f = arr.getJSONObject(i);

                cs = con.prepareCall("{ ? = call comercial.familiar_insertar(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");
                cs.registerOutParameter(1, Types.BIGINT);

                cs.setLong(2, idDdjj.longValue());
                cs.setInt(3, i + 1);
                cs.setString(4, trimJson(f, "parentesco"));
                cs.setString(5, trimJson(f, "nombre"));
                cs.setString(6, trimJson(f, "apellido"));
                cs.setString(7, onlyDigits(trimJson(f, "dni")));
                setNullableString(cs, 8, onlyDigits(trimJson(f, "cuil")));

                String fn = trimJson(f, "fecha_nacimiento");
                if (fn.isEmpty()) cs.setNull(9, Types.DATE);
                else cs.setDate(9, java.sql.Date.valueOf(fn));

                cs.setString(10, trimJson(f, "sexo"));
                cs.setString(11, trimJson(f, "nacionalidad"));
                setNullableString(cs, 12, trimJson(f, "email"));
                setNullableString(cs, 13, onlyDigits(trimJson(f, "codigo_area")));
                setNullableString(cs, 14, onlyDigits(trimJson(f, "telefono")));

                cs.execute();
                closeQuietly(cs);
            }

            //actualiza fecha ddjj
            cs = con.prepareCall("{ ? = call comercial.ddjj_actualizar_fecha_modificacion(?) }");
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(2, idDdjj.longValue());
            cs.execute();
            closeQuietly(cs);

            //actualiza fecha solicitud
            cs = con.prepareCall("{ ? = call comercial.solicitud_actualizar_fecha_modificacion(?) }");
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(2, idSolicitud.longValue());
            cs.execute();
            closeQuietly(cs);

            con.commit();
            
            return idDdjj.longValue();

        } catch (Exception e) {
            rollbackQuietly(con);
            _log.error("Error guardarPaso1 DDJJ", e);
            throw e;
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(cs);
            resetAutoCommitQuietly(con);
            closeQuietly(con);
        }
    }

    public long guardarSalud(
        String token,
        String saludJson,
        String obsSintomas,
        String obsFechaAprox,
        String obsDetalleTratamiento,
        String obsOtras,
        String obsInstituciones,
        boolean finalizar
    ) throws Exception {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        CallableStatement cs = null;
        Long idDdjj = null;

        try {
            con = ConnectionHelper.getConnection();
            con.setAutoCommit(false);

            //buscar ddjj por token
            ps = con.prepareStatement("SELECT * FROM comercial.ddjj_resolver_ids_por_token(?)");
            ps.setString(1, token);
            rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("DDJJ no encontrada para el token indicado");
            }

            idDdjj = Long.valueOf(rs.getLong("id_ddjj"));
            closeQuietly(rs);
            closeQuietly(ps);

            //borrar salud previa
            cs = con.prepareCall("{ ? = call comercial.salud_eliminar_ddjj(?) }");
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(2, idDdjj.longValue());
            cs.execute();
            closeQuietly(cs);

            //insertar filas normales
            JSONArray arr = new JSONArray(saludJson == null ? "[]" : saludJson);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject row = arr.getJSONObject(i);

                cs = con.prepareCall("{ ? = call comercial.salud_insertar(?, ?, ?, ?, ?, ?, ?, ?, ?) }");
                cs.registerOutParameter(1, Types.BIGINT);

                cs.setLong(2, idDdjj.longValue());
                cs.setString(3, trimJson(row, "inte"));

                String idEnf = trimJson(row, "id_enfermedad");
                if (idEnf.isEmpty()) cs.setNull(4, Types.BIGINT);
                else cs.setLong(4, Long.parseLong(idEnf));

                setNullableString(cs, 5, trimJson(row, "respuesta"));
                setNullableString(cs, 6, trimJson(row, "obs_sintomas"));
                setNullableString(cs, 7, trimJson(row, "obs_fecha_aprox"));
                setNullableString(cs, 8, trimJson(row, "obs_detalle_tratamiento"));
                setNullableString(cs, 9, trimJson(row, "obs_otras"));
                setNullableString(cs, 10, trimJson(row, "obs_instituciones"));

                cs.execute();
                closeQuietly(cs);
            }

            //fila OBS
            boolean hayObs =
                !safe(obsSintomas).isEmpty() ||
                !safe(obsFechaAprox).isEmpty() ||
                !safe(obsDetalleTratamiento).isEmpty() ||
                !safe(obsOtras).isEmpty() ||
                !safe(obsInstituciones).isEmpty();

            if (hayObs) {
                cs = con.prepareCall("{ ? = call comercial.salud_insertar_obs(?, ?, ?, ?, ?, ?) }");
                cs.registerOutParameter(1, Types.BIGINT);
                cs.setLong(2, idDdjj.longValue());
                setNullableString(cs, 3, obsSintomas);
                setNullableString(cs, 4, obsFechaAprox);
                setNullableString(cs, 5, obsDetalleTratamiento);
                setNullableString(cs, 6, obsOtras);
                setNullableString(cs, 7, obsInstituciones);
                cs.execute();
                closeQuietly(cs);
            }

            //actualizar estado ddjj
            cs = con.prepareCall("{ ? = call comercial.ddjj_actualizar_estado_por_id(?, ?) }");
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(2, idDdjj.longValue());
            cs.setString(3, finalizar ? "en_revision" : "pendiente_completar");
            cs.execute();
            closeQuietly(cs);

            con.commit();
            
            return idDdjj.longValue();

        } catch (Exception e) {
            rollbackQuietly(con);
            _log.error("Error guardarSalud DDJJ", e);
            throw e;
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(cs);
            resetAutoCommitQuietly(con);
            closeQuietly(con);
        }
    }

    public List<Map<String, Object>> getEnfermedadesActivas() throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();

            ps = con.prepareStatement("SELECT * FROM comercial.enfermedades_activas()");
            rs = ps.executeQuery();

            List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<String, Object>();
                row.put("id", rs.getLong("id"));
                row.put("seccion_nro", rs.getInt("seccion_nro"));
                row.put("seccion_titulo", rs.getString("seccion_titulo"));
                row.put("seccion_orden", rs.getInt("seccion_orden"));
                row.put("item_texto", rs.getString("item_texto"));
                row.put("item_orden", rs.getInt("item_orden"));
                out.add(row);
            }

            return out;

        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(con);
        }
    }

    public void setEnvelopeId(String token, String envelopeId) throws Exception {
        Connection con = null;
        CallableStatement cs = null;

        try {
            con = ConnectionHelper.getConnection();

            cs = con.prepareCall("{ ? = call comercial.ddjj_set_envelope_id(?, ?) }");
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, token);
            cs.setString(3, envelopeId);
            cs.execute();

            int rows = cs.getInt(1);
            if (rows < 1) {
                throw new RuntimeException("No se pudo actualizar envelope_id");
            }

        } finally {
            closeQuietly(cs);
            closeQuietly(con);
        }
    }

    public Map<String, Object> getByEnvelopeId(String envelopeId) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();

            ps = con.prepareStatement("SELECT * FROM comercial.ddjj_get_envelope_id(?)");
            ps.setString(1, envelopeId);

            rs = ps.executeQuery();
            if (!rs.next()) return null;

            return mapCurrentRow(rs);

        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(con);
        }
    }

    public void setDocumentoFirmado(String token, String pdfDdjj, String urlDdjj) throws Exception {
        Connection con = null;
        CallableStatement cs = null;

        try {
            con = ConnectionHelper.getConnection();

            cs = con.prepareCall("{ ? = call comercial.ddjj_set_documento_firmado(?, ?, ?) }");
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, token);
            setNullableString(cs, 3, pdfDdjj);
            setNullableString(cs, 4, urlDdjj);
            cs.execute();

            int rows = cs.getInt(1);
            if (rows < 1) {
                throw new RuntimeException("No se pudo marcar documento como firmado");
            }

        } finally {
            closeQuietly(cs);
            closeQuietly(con);
        }
    }

    public void guardarMontoFinal(String token, String montoFinal, String actor) throws Exception {
        Connection con = null;
        CallableStatement cs = null;

        try {
            token = token == null ? "" : token.trim();
            montoFinal = montoFinal == null ? "" : montoFinal.trim();
            actor = actor == null ? "" : actor.trim();

            if (token.isEmpty()) throw new Exception("Token requerido.");
            if (montoFinal.isEmpty()) throw new Exception("Monto final requerido.");
            if (actor.isEmpty()) actor = "asesor";

            BigDecimal monto;
            try {
                monto = new BigDecimal(montoFinal.replace(",", "."));
            } catch (Exception e) {
                throw new Exception("Monto final inválido.");
            }

            con = ConnectionHelper.getConnection();

            cs = con.prepareCall("{ ? = call comercial.ddjj_monto_final_guardar(?, ?, ?) }");
            cs.registerOutParameter(1, Types.BOOLEAN);
            cs.setString(2, token);
            cs.setBigDecimal(3, monto);
            cs.setString(4, actor);
            cs.execute();

        } finally {
            closeQuietly(cs);
            closeQuietly(con);
        }
    }
    
    /*
    public Map<String, Object> responderResolucion(String tokenRespuesta, String respuesta) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            tokenRespuesta = tokenRespuesta == null ? "" : tokenRespuesta.trim();
            respuesta = respuesta == null ? "" : respuesta.trim().toLowerCase();

            if (tokenRespuesta.isEmpty()) throw new Exception("token_respuesta requerido.");
            if (!"aceptada".equals(respuesta) && !"rechazada".equals(respuesta)) {
                throw new Exception("Respuesta inválida.");
            }

            con = ConnectionHelper.getConnection();

            ps = con.prepareStatement("SELECT * FROM comercial.ddjj_resolucion_responder(?, ?)");
            ps.setString(1, tokenRespuesta);
            ps.setString(2, respuesta);

            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new Exception("No se pudo registrar la respuesta.");
            }

            Map<String, Object> out = new HashMap<String, Object>();
            out.put("token", rs.getString("token"));
            out.put("respuesta", rs.getString("respuesta"));
            out.put("estado_respuesta", rs.getString("estado_respuesta"));
            return out;

        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(con);
        }
    }
    
    public Map<String, Object> consultarResolucionPorTokenRespuesta(String tokenRespuesta) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            tokenRespuesta = tokenRespuesta == null ? "" : tokenRespuesta.trim();
            if (tokenRespuesta.isEmpty()) throw new Exception("token_respuesta requerido.");

            con = ConnectionHelper.getConnection();

            ps = con.prepareStatement("SELECT * FROM comercial.ddjj_resolucion_consultar(?)");
            ps.setString(1, tokenRespuesta);

            rs = ps.executeQuery();
            if (!rs.next()) return null;

            Map<String, Object> out = new HashMap<String, Object>();
            out.put("token", rs.getString("token"));
            out.put("token_respuesta", rs.getString("token_respuesta"));
            out.put("estado_respuesta", rs.getString("estado_respuesta"));
            return out;

        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(con);
        }
    }
    
    */
    public void guardarContrato(
    	    String token,
    	    String estado,
    	    String envelopeId,
    	    String pdfContrato,
    	    String urlContrato
    	) throws Exception {
    	    Connection con = null;
    	    CallableStatement cs = null;

    	    try {
    	        token = token == null ? "" : token.trim();
    	        estado = estado == null ? "" : estado.trim();
    	        envelopeId = envelopeId == null ? "" : envelopeId.trim();
    	        pdfContrato = pdfContrato == null ? "" : pdfContrato.trim();
    	        urlContrato = urlContrato == null ? "" : urlContrato.trim();

    	        if (token.isEmpty()) throw new Exception("Token requerido.");
    	        if (estado.isEmpty()) throw new Exception("Estado requerido.");

    	        con = ConnectionHelper.getConnection();

    	        cs = con.prepareCall("{ ? = call comercial.contrato_guardar(?, ?, ?, ?, ?) }");
    	        cs.registerOutParameter(1, Types.BOOLEAN);
    	        cs.setString(2, token);
    	        cs.setString(3, estado);
    	        setNullableString(cs, 4, envelopeId);
    	        setNullableString(cs, 5, pdfContrato);
    	        setNullableString(cs, 6, urlContrato);
    	        cs.execute();

    	    } finally {
    	        closeQuietly(cs);
    	        closeQuietly(con);
    	    }
    	}
    
    public void marcarContratoFirmado(
    	    String envelopeId,
    	    String pdfContrato,
    	    String urlContrato
    	) throws Exception {
    	    Connection con = null;
    	    CallableStatement cs = null;

    	    try {
    	        envelopeId = envelopeId == null ? "" : envelopeId.trim();
    	        pdfContrato = pdfContrato == null ? "" : pdfContrato.trim();
    	        urlContrato = urlContrato == null ? "" : urlContrato.trim();

    	        if (envelopeId.isEmpty()) throw new Exception("Envelope requerido.");

    	        con = ConnectionHelper.getConnection();

    	        cs = con.prepareCall("{ ? = call comercial.contrato_marcar_firmado(?, ?, ?) }");
    	        cs.registerOutParameter(1, Types.BOOLEAN);
    	        cs.setString(2, envelopeId);
    	        setNullableString(cs, 3, pdfContrato);
    	        setNullableString(cs, 4, urlContrato);
    	        cs.execute();

    	    } finally {
    	        closeQuietly(cs);
    	        closeQuietly(con);
    	    }
    	}
    
    public List<Map<String, Object>> getSaludItemsActivos() throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();

            ps = con.prepareStatement(
                "SELECT id, codigo, seccion_nro, seccion_titulo, seccion_orden, item_texto, item_orden, tipo " +
                "FROM comercial.enfermedades_activas()"
            );

            rs = ps.executeQuery();

            List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<String, Object>();
                row.put("id", rs.getLong("id"));
                row.put("codigo", rs.getString("codigo"));
                row.put("seccion_nro", rs.getInt("seccion_nro"));
                row.put("seccion_titulo", rs.getString("seccion_titulo"));
                row.put("seccion_orden", rs.getInt("seccion_orden"));
                row.put("item_texto", rs.getString("item_texto"));
                row.put("item_orden", rs.getInt("item_orden"));
                row.put("tipo", rs.getString("tipo"));
                out.add(row);
            }

            return out;

        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(con);
        }
    }
    
    public long guardarSaludV2(
    	    String token,
    	    String saludJson,
    	    String observacionesJson,
    	    boolean finalizar
    	) throws Exception {
    	    Connection con = null;
    	    PreparedStatement ps = null;
    	    ResultSet rs = null;

    	    try {
    	        con = ConnectionHelper.getConnection();
    	        con.setAutoCommit(false);

    	        long idDdjj = getIdDdjjByToken(con, token);
    	        if (idDdjj <= 0) {
    	            throw new Exception("DDJJ no encontrada para token");
    	        }

    	        ps = con.prepareStatement("DELETE FROM comercial.salud WHERE id_ddjj = ?");
    	        ps.setLong(1, idDdjj);
    	        ps.executeUpdate();
    	        closeQuietly(ps);

    	        ps = con.prepareStatement("DELETE FROM comercial.salud_observacion WHERE id_ddjj = ?");
    	        ps.setLong(1, idDdjj);
    	        ps.executeUpdate();
    	        closeQuietly(ps);

    	        JSONArray saludArr = new JSONArray(saludJson == null ? "[]" : saludJson);

    	        ps = con.prepareStatement(
    	            "INSERT INTO comercial.salud " +
    	            "(id_ddjj, inte, id_enfermedad, respuesta, valor_numero) " +
    	            "VALUES (?, ?, ?, ?, ?)"
    	        );

    	        for (int i = 0; i < saludArr.length(); i++) {
    	            JSONObject o = saludArr.getJSONObject(i);

    	            String inte = safe(o.optString("inte")).trim();
    	            long idEnfermedad = parseLong(o.optString("id_enfermedad"));
    	            String respuesta = safe(o.optString("respuesta")).trim().toLowerCase();
    	            String valorNumero = safe(o.optString("valor_numero")).trim();

    	            if (inte.isEmpty() || idEnfermedad <= 0) continue;

    	            ps.setLong(1, idDdjj);
    	            ps.setString(2, inte);
    	            ps.setLong(3, idEnfermedad);

    	            if (!respuesta.isEmpty()) {
    	                if (!"si".equals(respuesta)) respuesta = "no";

    	                ps.setString(4, respuesta);
    	                ps.setNull(5, Types.NUMERIC);
    	            } else {
    	                ps.setNull(4, Types.VARCHAR);

    	                if (valorNumero.isEmpty()) {
    	                    ps.setNull(5, Types.NUMERIC);
    	                } else {
    	                    ps.setBigDecimal(5, new BigDecimal(valorNumero.replace(",", ".")));
    	                }
    	            }

    	            ps.addBatch();
    	        }

    	        ps.executeBatch();
    	        closeQuietly(ps);

    	        JSONArray obsArr = new JSONArray(
    	            observacionesJson == null || observacionesJson.trim().isEmpty() ? "[]" : observacionesJson
    	        );

    	        if (obsArr.length() > 0) {
    	            ps = con.prepareStatement(
    	                "INSERT INTO comercial.salud_observacion " +
    	                "(id_ddjj, seccion_nro, observacion) VALUES (?, ?, ?)"
    	            );

    	            for (int i = 0; i < obsArr.length(); i++) {
    	                JSONObject o = obsArr.getJSONObject(i);

    	                int seccionNro = parseInt(o.optString("seccion_nro"));
    	                String observacion = safe(o.optString("observacion")).trim();

    	                if (seccionNro <= 0 || observacion.isEmpty()) continue;

    	                ps.setLong(1, idDdjj);
    	                ps.setInt(2, seccionNro);
    	                ps.setString(3, observacion);
    	                ps.addBatch();
    	            }

    	            ps.executeBatch();
    	            closeQuietly(ps);
    	        }

    	        if (finalizar) {
    	            cambiarEstado(token, "en_revision");
    	        }

    	        con.commit();
    	        return idDdjj;

    	    } catch (Exception e) {
    	        rollbackQuietly(con);
    	        throw e;

    	    } finally {
    	        resetAutoCommitQuietly(con);
    	        closeQuietly(rs);
    	        closeQuietly(ps);
    	        closeQuietly(con);
    	    }
    	}

    	private long getIdDdjjByToken(Connection con, String token) throws Exception {
    	    PreparedStatement ps = null;
    	    ResultSet rs = null;
    	    try {
    	        ps = con.prepareStatement("SELECT id FROM comercial.declaracion_jurada WHERE token = ?");
    	        ps.setString(1, token);
    	        rs = ps.executeQuery();
    	        if (rs.next()) return rs.getLong("id");
    	        return 0L;
    	    } finally {
    	        closeQuietly(rs);
    	        closeQuietly(ps);
    	    }
    	}

    	private String mapInte(String col) {
    	    col = safe(col).trim().toLowerCase();
    	    if ("tit".equals(col)) return "0";
    	    if ("01".equals(col)) return "1";
    	    if ("02".equals(col)) return "2";
    	    if ("03".equals(col)) return "3";
    	    if ("04".equals(col)) return "4";
    	    return "";
    	}

    	private long parseLong(String s) {
    	    try { return Long.parseLong(s); } catch (Exception e) { return 0L; }
    	}

    	private int parseInt(String s) {
    	    try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    	}
    
    private Integer calcularEdadDesdeFecha(String fechaNacimiento) {
        if (fechaNacimiento == null || fechaNacimiento.trim().isEmpty()) return null;
        LocalDate fn = LocalDate.parse(fechaNacimiento);
        return Integer.valueOf(Period.between(fn, LocalDate.now()).getYears());
    }

    private Map<String, Object> mapCurrentRow(ResultSet rs) throws Exception {
        Map<String, Object> datos = new LinkedHashMap<String, Object>();
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();

        for (int i = 1; i <= cols; i++) {
            String col = md.getColumnLabel(i);
            Object val = rs.getObject(i);

            if (val instanceof java.sql.Date) {
                val = val.toString();
            }
            if (val instanceof java.sql.Timestamp) {
                val = val.toString();
            }

            datos.put(col, val);
        }

        return datos;
    }

    private String trimJson(JSONObject obj, String key) {
        if (!obj.has(key) || obj.isNull(key)) return "";
        return safe(obj.optString(key, "")).trim();
    }

    private String onlyDigits(String s) {
        return s == null ? "" : s.replaceAll("\\D+", "");
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private void setNullableString(PreparedStatement ps, int idx, String value) throws Exception {
        if (value == null || value.trim().isEmpty()) ps.setNull(idx, Types.VARCHAR);
        else ps.setString(idx, value.trim());
    }

    private void closeQuietly(ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
    }

    private void closeQuietly(PreparedStatement ps) {
        try { if (ps != null) ps.close(); } catch (Exception ignored) {}
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

    private void setNullableInteger(PreparedStatement ps, int idx, Integer value) throws Exception {
        if (value == null) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, value.intValue());
    }
    
    private void setNullableBigDecimal(PreparedStatement ps, int idx, BigDecimal value) throws Exception {
        if (value == null) ps.setNull(idx, Types.NUMERIC);
        else ps.setBigDecimal(idx, value);
    }
}