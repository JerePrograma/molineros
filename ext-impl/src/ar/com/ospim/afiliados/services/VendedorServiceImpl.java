package ar.com.ospim.afiliados.services;

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

public class VendedorServiceImpl {

	public List<Map<String, Object>> buscarVendedores(String nombre, String apellido, String dni) throws Exception {
	    List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
	    Connection con = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
	        con = ConnectionHelper.getConnection();
	        ps = con.prepareStatement("SELECT * FROM comercial.buscar_vendedores(?, ?, ?)");

	        setNullableString(ps, 1, emptyToNull(nombre));
	        setNullableString(ps, 2, emptyToNull(apellido));
	        setNullableString(ps, 3, emptyToNull(dni));

	        rs = ps.executeQuery();

	        while (rs.next()) {
	            Map<String, Object> row = new HashMap<String, Object>();
	            row.put("id", rs.getLong("id"));
	            row.put("apellido", rs.getString("apellido"));
	            row.put("nombre", rs.getString("nombre"));
	            row.put("dni", rs.getString("dni"));
	            row.put("hora_desde", rs.getString("hora_desde"));
	            row.put("hora_hasta", rs.getString("hora_hasta"));
	            row.put("motivo", rs.getString("motivo"));
	            row.put("baja_fecha", rs.getTimestamp("baja_fecha"));
	            out.add(row);
	        }

	        return out;
	    } finally {
	        closeQuietly(rs);
	        closeQuietly(ps);
	        closeQuietly(con);
	    }
	}

    public Map<String, Object> getVendedor(Long id) throws Exception {
        Map<String, Object> out = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            ps = con.prepareStatement("SELECT * FROM comercial.get_vendedor(?)");
            ps.setLong(1, id.longValue());
            rs = ps.executeQuery();

            if (rs.next()) {
                out = new HashMap<String, Object>();
                out.put("id", rs.getLong("id"));
                out.put("nombre", rs.getString("nombre"));
                out.put("apellido", rs.getString("apellido"));
                out.put("dni", rs.getString("dni"));
                out.put("email", rs.getString("email"));
                out.put("hora_desde", rs.getString("hora_desde"));
                out.put("hora_hasta", rs.getString("hora_hasta"));
                out.put("alta_fecha", rs.getTimestamp("alta_fecha"));
                out.put("alta_usr", rs.getString("alta_usr"));
                out.put("modi_fecha", rs.getTimestamp("modi_fecha"));
                out.put("modi_usr", rs.getString("modi_usr"));
                out.put("baja_fecha", rs.getTimestamp("baja_fecha"));
                out.put("baja_usr", rs.getString("baja_usr"));
            }

            return out;
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(con);
        }
    }

    public List<Map<String, Object>> getHistorico(Long idVendedor) throws Exception {
        List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement psEditable = null;
        ResultSet rsEditable = null;

        try {
            con = ConnectionHelper.getConnection();
            ps = con.prepareStatement("SELECT * FROM comercial.get_historico_vendedor(?)");
            ps.setLong(1, idVendedor.longValue());
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<String, Object>();
                Long idHistorico = Long.valueOf(rs.getLong("id"));

                row.put("id", idHistorico);
                row.put("fecha_desde", rs.getDate("fecha_desde"));
                row.put("fecha_hasta", rs.getDate("fecha_hasta"));
                row.put("motivo", rs.getString("motivo"));
                row.put("observacion", rs.getString("observacion"));
                row.put("alta_fecha", rs.getTimestamp("alta_fecha"));
                row.put("alta_usr", rs.getString("alta_usr"));
                row.put("baja_fecha", rs.getTimestamp("baja_fecha"));
                row.put("baja_usr", rs.getString("baja_usr"));

                psEditable = con.prepareStatement("SELECT comercial.puede_editar_historico_vendedor(?)");
                psEditable.setLong(1, idHistorico.longValue());
                rsEditable = psEditable.executeQuery();

                boolean editable = false;
                if (rsEditable.next()) {
                    editable = rsEditable.getBoolean(1);
                }

                row.put("editable", Boolean.valueOf(editable));
                out.add(row);

                closeQuietly(rsEditable);
                closeQuietly(psEditable);
                rsEditable = null;
                psEditable = null;
            }

            return out;
        } finally {
            closeQuietly(rsEditable);
            closeQuietly(psEditable);
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(con);
        }
    }

    public Long guardarVendedor(
    	    Long id,
    	    String nombre,
    	    String apellido,
    	    String dni,
    	    String email,
    	    String horaDesde,
    	    String horaHasta,
    	    String usuario
    	) throws Exception {
    	    Connection con = null;
    	    CallableStatement cs = null;

    	    try {
    	        con = ConnectionHelper.getConnection();

    	        if (id == null) {
    	            cs = con.prepareCall("{ ? = call comercial.vendedor_insertar(?, ?, ?, ?, ?, ?, ?) }");
    	            cs.registerOutParameter(1, Types.BIGINT);

    	            cs.setString(2, nombre);
    	            cs.setString(3, apellido);
    	            cs.setString(4, dni);
    	            cs.setString(5, email);
    	            cs.setString(6, horaDesde);
    	            cs.setString(7, horaHasta);
    	            cs.setString(8, usuario);
    	            cs.execute();

    	            return Long.valueOf(cs.getLong(1));
    	        } else {
    	            cs = con.prepareCall("{ ? = call comercial.vendedor_actualizar(?, ?, ?, ?, ?, ?, ?, ?) }");
    	            cs.registerOutParameter(1, Types.BOOLEAN);

    	            cs.setLong(2, id.longValue());
    	            cs.setString(3, nombre);
    	            cs.setString(4, apellido);
    	            cs.setString(5, dni);
    	            cs.setString(6, email);
    	            cs.setString(7, horaDesde);
    	            cs.setString(8, horaHasta);
    	            cs.setString(9, usuario);
    	            cs.execute();

    	            return id;
    	        }
    	    } finally {
    	        closeQuietly(cs);
    	        closeQuietly(con);
    	    }
    	}

    public void darBaja(Long idVendedor, String usuario) throws Exception {
        Connection con = null;
        CallableStatement cs = null;

        try {
            con = ConnectionHelper.getConnection();
            cs = con.prepareCall("{ ? = call comercial.dar_baja_vendedor(?, ?) }");
            cs.registerOutParameter(1, Types.BOOLEAN);
            cs.setLong(2, idVendedor.longValue());
            cs.setString(3, usuario);
            cs.execute();
        } finally {
            closeQuietly(cs);
            closeQuietly(con);
        }
    }

    public Long guardarHistorico(
    	    Long idHistorico,
    	    Long idVendedor,
    	    String fechaDesde,
    	    String fechaHasta,
    	    String motivo,
    	    String observacion,
    	    String usuario
    	) throws Exception {
    	    Connection con = null;
    	    CallableStatement cs = null;

    	    try {
    	        con = ConnectionHelper.getConnection();

    	        if (idHistorico == null) {
    	            cs = con.prepareCall("{ ? = call comercial.vendedor_historico_insertar(?, ?, ?, ?, ?, ?) }");
    	            cs.registerOutParameter(1, Types.BIGINT);
    	            cs.setLong(2, idVendedor.longValue());
    	            cs.setDate(3, java.sql.Date.valueOf(fechaDesde));
    	            cs.setDate(4, java.sql.Date.valueOf(fechaHasta));
    	            setNullableString(cs, 5, motivo);
    	            setNullableString(cs, 6, observacion);
    	            setNullableString(cs, 7, usuario);
    	            cs.execute();

    	            return Long.valueOf(cs.getLong(1));
    	        } else {
    	            cs = con.prepareCall("{ ? = call comercial.vendedor_historico_nueva_version(?, ?, ?, ?, ?, ?) }");
    	            cs.registerOutParameter(1, Types.BIGINT);
    	            cs.setLong(2, idHistorico.longValue());
    	            cs.setDate(3, java.sql.Date.valueOf(fechaDesde));
    	            cs.setDate(4, java.sql.Date.valueOf(fechaHasta));
    	            setNullableString(cs, 5, motivo);
    	            setNullableString(cs, 6, observacion);
    	            setNullableString(cs, 7, usuario);
    	            cs.execute();

    	            return Long.valueOf(cs.getLong(1));
    	        }
    	    } finally {
    	        closeQuietly(cs);
    	        closeQuietly(con);
    	    }
    	}

    public void eliminarHistorico(Long idHistorico, String usuario) throws Exception {
        Connection con = null;
        CallableStatement cs = null;

        try {
            con = ConnectionHelper.getConnection();
            cs = con.prepareCall("{ ? = call comercial.vendedor_historico_eliminar(?, ?) }");
            cs.registerOutParameter(1, Types.BOOLEAN);
            cs.setLong(2, idHistorico.longValue());
            cs.setString(3, usuario);
            cs.execute();
        } finally {
            closeQuietly(cs);
            closeQuietly(con);
        }
    }
    
    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
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
}