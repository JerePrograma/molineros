package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.compras.requerimientos.helper.NotificarCotizacionPrestadorHelper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia del proceso de notificación de cotizaciones.
 *
 * Esta clase no genera PDF, no accede a Document Library, no valida emails,
 * no construye correos y no decide estados funcionales.
 */
public class NotificarCotizacionPrestadorServiceImpl {







    private static final String SQL_RESERVAR_COPIA_COTIZACION =
            "{ ? = call compras.reservar_copia_cotizacion_requerimiento(?) }";

    private static final String SQL_LIBERAR_COPIA_COTIZACION =
            "{call compras.liberar_copia_cotizacion_requerimiento(?)}";

    public Transaccion abrirTransaccion() throws Exception {
        Connection con = ConnectionHelper.getConnectionForTransaction();
        if (con == null) throw new SQLException("No se obtuvo conexion transaccional de notificaciones.");
        return new Transaccion(this, con);
    }

    public Transaccion usarTransaccion(Connection con) throws Exception {
        if (con == null) throw new SQLException("La conexion de la operacion es obligatoria.");
        return new Transaccion(this, con, false);
    }

    private List<PrestadorCotizacion> listarPrestadoresNotificados(Connection con,int id) throws Exception {
        PreparedStatement stmt=con.prepareStatement("SELECT DISTINCT p.id_prestador,p.descripcion,p.cuit,NULL::VARCHAR AS email,"
                + "p.id_tipo_prestador,tp.descripcion AS tipo_prestador,p.baja_fecha,p.solicitar_cotizacion,"
                + "rcp.estado_envio,rcp.email_destino FROM compras.requerimiento_cotizacion_prestador rcp "
                + "JOIN public.prestador p ON p.id_prestador=rcp.id_prestador "
                + "LEFT JOIN trae_tipos_prestadores() tp ON tp.id_tipo_prestador=p.id_tipo_prestador "
                + "WHERE rcp.id_requerimiento=? ORDER BY p.descripcion,p.cuit,p.id_prestador");
        try{stmt.setInt(1,id);ResultSet rs=stmt.executeQuery();try{
            List<PrestadorCotizacion> filas=new ArrayList<PrestadorCotizacion>();
            while(rs.next()){
                PrestadorCotizacion p=mapPrestadorCotizacion(rs);p.setBajaFecha(rs.getTimestamp("baja_fecha"));
                boolean solicitar=rs.getBoolean("solicitar_cotizacion");p.setSolicitarCotizacion(rs.wasNull()?null:Boolean.valueOf(solicitar));
                p.setEstadoEnvio(rs.getString("estado_envio"));p.setEmailDestino(rs.getString("email_destino"));filas.add(p);
            }return filas;
        }finally{rs.close();}}finally{stmt.close();}
    }

    private List<Map<String, Object>> listarRubrosPrestadores(Connection con, Integer id)
            throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT id_prestador, rubro FROM public.prestador_rubro"
                        + (id == null ? "" : " WHERE id_prestador = ?"));
        try {
            if (id != null) stmt.setInt(1, id.intValue());
            ResultSet rs = stmt.executeQuery();
            try {
                List<Map<String, Object>> filas = new ArrayList<Map<String, Object>>();
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<String, Object>();
                    fila.put("id_prestador", Integer.valueOf(rs.getInt("id_prestador")));
                    fila.put("rubro", rs.getString("rubro")); filas.add(fila);
                }
                return filas;
            } finally { rs.close(); }
        } finally { stmt.close(); }
    }

    private List<Map<String, Object>> listarRubrosRequerimiento(Connection con, int id)
            throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT d.id_tipo_prestacion, t.descripcion, d.baja_fecha "
                        + "FROM compras.requerimiento_detalle d LEFT JOIN compras.tipo_prestacion t "
                        + "ON t.id_tipo_prestacion = d.id_tipo_prestacion WHERE d.id_requerimiento = ?");
        try {
            stmt.setInt(1, id); ResultSet rs = stmt.executeQuery();
            try {
                List<Map<String, Object>> filas = new ArrayList<Map<String, Object>>();
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<String, Object>();
                    fila.put("descripcion", rs.getString("descripcion"));
                    fila.put("baja_fecha", rs.getTimestamp("baja_fecha")); filas.add(fila);
                }
                return filas;
            } finally { rs.close(); }
        } finally { stmt.close(); }
    }

    private List<PrestadorCotizacion> listarPrestadoresCrudos(Connection con,
            List<Integer> ids, int idRequerimiento) throws Exception {
        List<PrestadorCotizacion> filas = new ArrayList<PrestadorCotizacion>();
        if (ids.isEmpty()) return filas;
        StringBuilder parametros = new StringBuilder("{");
        for (int i = 0; i < ids.size(); i++) { if (i > 0) parametros.append(','); parametros.append(ids.get(i).intValue()); }
        parametros.append('}');
        PreparedStatement stmt = con.prepareStatement(
                "SELECT p.id_prestador, p.descripcion, p.cuit, NULL::VARCHAR AS email, "
                        + "p.id_tipo_prestador, tp.descripcion AS tipo_prestador, "
                        + "p.baja_fecha, p.solicitar_cotizacion, rcp.estado_envio, rcp.email_destino "
                        + "FROM public.prestador p LEFT JOIN trae_tipos_prestadores() tp "
                        + "ON tp.id_tipo_prestador = p.id_tipo_prestador "
                        + "LEFT JOIN compras.requerimiento_cotizacion_prestador rcp "
                        + "ON rcp.id_prestador = p.id_prestador AND rcp.id_requerimiento = ? "
                        + "WHERE p.id_prestador = ANY(?::INTEGER[]) "
                        + "ORDER BY p.descripcion, p.id_prestador");
        try {
            stmt.setInt(1, idRequerimiento);
            stmt.setString(2, parametros.toString());
            ResultSet rs = stmt.executeQuery();
            try {
                while (rs.next()) {
                    PrestadorCotizacion p = mapPrestadorCotizacion(rs);
                    p.setBajaFecha(rs.getTimestamp("baja_fecha"));
                    boolean solicitar = rs.getBoolean("solicitar_cotizacion");
                    p.setSolicitarCotizacion(rs.wasNull() ? null : Boolean.valueOf(solicitar));
                    p.setEstadoEnvio(rs.getString("estado_envio"));
                    p.setEmailDestino(rs.getString("email_destino")); filas.add(p);
                }
                return filas;
            } finally { rs.close(); }
        } finally { stmt.close(); }
    }

    private List<Map<String, Object>> listarContactosPrestador(Connection con, int id)
            throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT ce.id_contacto_e, ce.tipo_contacto_e, ce.contacto, lower(btrim(ce.contacto)) AS contacto_clave, ce.baja_fecha, "
                        + "ce.modi_fecha, ce.alta_fecha, ce.vigen_desde AS contacto_desde, "
                        + "pce.vigen_desde AS relacion_desde "
                        + "FROM public.prestad_contacto_e pce JOIN public.contacto_e ce "
                        + "ON ce.id_contacto_e = pce.id_contacto_e WHERE pce.id_prestador = ?");
        try {
            stmt.setInt(1, id); ResultSet rs = stmt.executeQuery();
            try {
                List<Map<String, Object>> filas = new ArrayList<Map<String, Object>>();
                while (rs.next()) {
                    Map<String, Object> f = new HashMap<String, Object>();
                    f.put("id", Integer.valueOf(rs.getInt("id_contacto_e")));
                    f.put("tipo", rs.getString("tipo_contacto_e")); f.put("contacto", rs.getString("contacto"));
                    f.put("clave", rs.getString("contacto_clave"));
                    f.put("baja", rs.getTimestamp("baja_fecha")); f.put("modi", rs.getTimestamp("modi_fecha"));
                    f.put("alta", rs.getTimestamp("alta_fecha")); f.put("contacto_desde", rs.getTimestamp("contacto_desde"));
                    f.put("relacion_desde", rs.getTimestamp("relacion_desde")); filas.add(f);
                }
                return filas;
            } finally { rs.close(); }
        } finally { stmt.close(); }
    }

    private ReservaCotizacionPrestador obtenerNotificacion(Connection con, int id,
            int prestador, boolean bloquear) throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT estado_envio, intentos, email_destino "
                        + "FROM compras.requerimiento_cotizacion_prestador "
                        + "WHERE id_requerimiento = ? AND id_prestador = ?"
                        + (bloquear ? " FOR UPDATE" : ""));
        try {
            stmt.setInt(1, id); stmt.setInt(2, prestador); ResultSet rs = stmt.executeQuery();
            try {
                if (!rs.next()) return null;
                ReservaCotizacionPrestador r = new ReservaCotizacionPrestador();
                r.setEstadoEnvio(rs.getString("estado_envio")); r.setEmailDestino(rs.getString("email_destino"));
                int intentos = rs.getInt("intentos"); r.setIntentos(rs.wasNull() ? null : Integer.valueOf(intentos));
                return r;
            } finally { rs.close(); }
        } finally { stmt.close(); }
    }

    private int insertarNotificacion(Connection con, int id, int prestador,
            String estado, String email, String usuario) throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO compras.requerimiento_cotizacion_prestador "
                        + "(id_requerimiento, id_prestador, estado_envio, intentos, email_destino, fecha_creacion, alta_usr) "
                        + "VALUES (?, ?, ?, 0, ?, clock_timestamp(), ?) "
                        + "ON CONFLICT (id_requerimiento, id_prestador) DO NOTHING");
        try {
            stmt.setInt(1, id); stmt.setInt(2, prestador); stmt.setString(3, estado);
            stmt.setString(4, email); stmt.setString(5, usuario); return stmt.executeUpdate();
        } finally { stmt.close(); }
    }

    private int actualizarReserva(Connection con, int id, int prestador, String estado,
            int intento, String email, String usuario, String estadoEsperado, int intentoEsperado)
            throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "UPDATE compras.requerimiento_cotizacion_prestador SET estado_envio = ?, intentos = ?, "
                        + "email_destino = ?, fecha_ultimo_intento = clock_timestamp(), fecha_envio = NULL, "
                        + "ultimo_error = NULL, modi_fecha = clock_timestamp(), modi_usr = ? "
                        + "WHERE id_requerimiento = ? AND id_prestador = ? AND estado_envio = ? AND intentos = ?");
        try {
            stmt.setString(1, estado); stmt.setInt(2, intento); stmt.setString(3, email); stmt.setString(4, usuario);
            stmt.setInt(5, id); stmt.setInt(6, prestador); stmt.setString(7, estadoEsperado); stmt.setInt(8, intentoEsperado);
            return stmt.executeUpdate();
        } finally { stmt.close(); }
    }

    private int persistirFinalizacion(Connection con, int id, int prestador, String estado,
            Timestamp fechaEnvio, String error, String usuario, String estadoEsperado) throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "UPDATE compras.requerimiento_cotizacion_prestador SET estado_envio = ?, fecha_envio = ?, "
                        + "ultimo_error = ?, modi_fecha = clock_timestamp(), modi_usr = ? "
                        + "WHERE id_requerimiento = ? AND id_prestador = ? AND estado_envio = ?");
        try {
            stmt.setString(1, estado); stmt.setTimestamp(2, fechaEnvio); stmt.setString(3, error); stmt.setString(4, usuario);
            stmt.setInt(5, id); stmt.setInt(6, prestador); stmt.setString(7, estadoEsperado); return stmt.executeUpdate();
        } finally { stmt.close(); }
    }

    private RequerimientoCompraPedidoCotizacion obtenerPedido(Connection con, int id,
            int prestador, int intento) throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT * FROM compras.requerimiento_pedido_cotizacion "
                        + "WHERE id_requerimiento = ? AND id_prestador = ? AND intento = ?");
        try {
            stmt.setInt(1, id); stmt.setInt(2, prestador); stmt.setInt(3, intento); ResultSet rs = stmt.executeQuery();
            try {
                if (!rs.next()) return null;
                RequerimientoCompraPedidoCotizacion d = new RequerimientoCompraPedidoCotizacion();
                d.setIdRequerimiento(Integer.valueOf(id)); d.setIdPrestador(Integer.valueOf(prestador)); d.setIntento(Integer.valueOf(intento));
                d.setDlGroupId(Long.valueOf(rs.getLong("dl_group_id"))); d.setDlFolderId(Long.valueOf(rs.getLong("dl_folder_id")));
                d.setDlFileEntryId(Long.valueOf(rs.getLong("dl_file_entry_id"))); d.setDlFileUuid(rs.getString("dl_file_uuid"));
                d.setNombreOriginal(rs.getString("nombre_original")); d.setNombrePersistido(rs.getString("nombre_persistido"));
                d.setTitulo(rs.getString("titulo")); return d;
            } finally { rs.close(); }
        } finally { stmt.close(); }
    }

    private int insertarPedido(Connection con, RequerimientoCompraPedidoCotizacion d,
            String usuario) throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO compras.requerimiento_pedido_cotizacion "
                        + "(id_requerimiento, id_prestador, intento, dl_group_id, dl_folder_id, dl_file_entry_id, "
                        + "dl_file_uuid, nombre_original, nombre_persistido, titulo, alta_usr) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        try {
            stmt.setInt(1, d.getIdRequerimiento().intValue()); stmt.setInt(2, d.getIdPrestador().intValue());
            stmt.setInt(3, d.getIntento().intValue()); stmt.setLong(4, d.getDlGroupId().longValue());
            stmt.setLong(5, d.getDlFolderId().longValue()); stmt.setLong(6, d.getDlFileEntryId().longValue());
            stmt.setString(7, d.getDlFileUuid()); stmt.setString(8, d.getNombreOriginal());
            stmt.setString(9, d.getNombrePersistido()); stmt.setString(10, d.getTitulo()); stmt.setString(11, usuario);
            return stmt.executeUpdate();
        } finally { stmt.close(); }
    }

    public static final class Transaccion {
        private final NotificarCotizacionPrestadorServiceImpl service;
        private Connection con;
        private final boolean propia;
        private Transaccion(NotificarCotizacionPrestadorServiceImpl s, Connection c) { this(s,c,true); }
        private Transaccion(NotificarCotizacionPrestadorServiceImpl s, Connection c, boolean propia) { service=s;con=c;this.propia=propia; }
        public RequerimientoCompra getRequerimiento(int id, boolean bloquear) throws Exception {
            if (bloquear) {
                PreparedStatement stmt = con.prepareStatement("SELECT id_requerimiento FROM compras.requerimiento WHERE id_requerimiento = ? FOR UPDATE");
                try { stmt.setInt(1, id); ResultSet rs = stmt.executeQuery(); try { while (rs.next()) rs.getInt(1); } finally { rs.close(); } } finally { stmt.close(); }
            }
            return BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(con, id);
        }
        public String getConfiguracionCompra(String clave) throws Exception { return BusquedaRequerimientoCompraServiceUtil.getConfiguracionCompra(clave, con); }
        public List<Map<String, Object>> listarHabilitacionPrestadores(int id) throws Exception {
            PreparedStatement stmt = con.prepareStatement("SELECT p.id_prestador, p.baja_fecha, p.solicitar_cotizacion, rcp.estado_envio FROM public.prestador p LEFT JOIN compras.requerimiento_cotizacion_prestador rcp ON rcp.id_prestador = p.id_prestador AND rcp.id_requerimiento = ?");
            try {
                stmt.setInt(1, id); ResultSet rs = stmt.executeQuery();
                try {
                    List<Map<String, Object>> filas = new ArrayList<Map<String, Object>>();
                    while (rs.next()) {
                        Map<String, Object> f = new HashMap<String, Object>();
                        f.put("id", Integer.valueOf(rs.getInt("id_prestador"))); f.put("baja", rs.getTimestamp("baja_fecha"));
                        boolean solicitar = rs.getBoolean("solicitar_cotizacion");
                        f.put("solicitar", rs.wasNull() ? null : Boolean.valueOf(solicitar));
                        f.put("estado", rs.getString("estado_envio")); filas.add(f);
                    }
                    return filas;
                } finally { rs.close(); }
            } finally { stmt.close(); }
        }
        public Map<String, Object> obtenerRubro(int id) throws Exception {
            PreparedStatement stmt = con.prepareStatement("SELECT t.descripcion, s.activo, s.baja_fecha FROM compras.tipo_prestacion t JOIN compras.sector_requerimiento s ON s.id_sector = t.id_sector WHERE t.id_tipo_prestacion = ?");
            try {
                stmt.setInt(1, id); ResultSet rs = stmt.executeQuery();
                try {
                    if (!rs.next()) return null;
                    Map<String, Object> f = new HashMap<String, Object>();
                    f.put("descripcion", rs.getString("descripcion")); f.put("activo", Boolean.valueOf(rs.getBoolean("activo")));
                    f.put("baja", rs.getTimestamp("baja_fecha")); return f;
                } finally { rs.close(); }
            } finally { stmt.close(); }
        }
        public String getUsuarioActual() throws Exception {
            PreparedStatement stmt = con.prepareStatement("SELECT CURRENT_USER");
            try { ResultSet rs = stmt.executeQuery(); try { return rs.next() ? rs.getString(1) : null; } finally { rs.close(); } }
            finally { stmt.close(); }
        }
        public Timestamp getFechaEvento() throws Exception {
            PreparedStatement stmt = con.prepareStatement("SELECT clock_timestamp()");
            try { ResultSet rs = stmt.executeQuery(); try { if (!rs.next()) throw new SQLException("No se obtuvo la fecha DB del evento."); return rs.getTimestamp(1); } finally { rs.close(); } } finally { stmt.close(); }
        }
        public Timestamp getAhora() throws Exception {
            PreparedStatement stmt = con.prepareStatement("SELECT LOCALTIMESTAMP");
            try { ResultSet rs = stmt.executeQuery(); try { if (!rs.next()) throw new SQLException("No se obtuvo la fecha DB."); return rs.getTimestamp(1); } finally { rs.close(); } } finally { stmt.close(); }
        }
        public List<PrestadorCotizacion> listarPrestadoresNotificados(int id) throws Exception {return service.listarPrestadoresNotificados(con,id);}
        public List<String> listarEstadosNotificaciones(int id) throws Exception {
            PreparedStatement stmt=con.prepareStatement("SELECT estado_envio FROM compras.requerimiento_cotizacion_prestador WHERE id_requerimiento=? ORDER BY id_prestador");
            try{stmt.setInt(1,id);ResultSet rs=stmt.executeQuery();try{List<String> estados=new ArrayList<String>();while(rs.next())estados.add(rs.getString(1));return estados;}finally{rs.close();}}finally{stmt.close();}
        }
        public int cambiarEstadoRequerimiento(int id,int anterior,int nuevo,String usuario)throws Exception {
            PreparedStatement stmt=con.prepareStatement("UPDATE compras.requerimiento SET estado=?,modi_fecha=now(),modi_usr=? WHERE id_requerimiento=? AND estado=? AND baja_fecha IS NULL");
            try{stmt.setInt(1,nuevo);stmt.setString(2,usuario);stmt.setInt(3,id);stmt.setInt(4,anterior);return stmt.executeUpdate();}finally{stmt.close();}
        }
        public List<Map<String, Object>> listarRubrosPrestadores(Integer id) throws Exception { return service.listarRubrosPrestadores(con, id); }
        public List<Map<String, Object>> listarRubrosRequerimiento(int id) throws Exception { return service.listarRubrosRequerimiento(con, id); }
        public List<PrestadorCotizacion> listarPrestadores(List<Integer> ids, int id) throws Exception { return service.listarPrestadoresCrudos(con, ids, id); }
        public List<Map<String, Object>> listarContactos(int id) throws Exception { return service.listarContactosPrestador(con, id); }
        public ReservaCotizacionPrestador obtenerNotificacion(int id, int p, boolean b) throws Exception { return service.obtenerNotificacion(con, id, p, b); }
        public int insertarNotificacion(int id, int p, String e, String m, String u) throws Exception { return service.insertarNotificacion(con, id, p, e, m, u); }
        public int actualizarReserva(int id, int p, String e, int i, String m, String u, String previo, int intento) throws Exception { return service.actualizarReserva(con, id, p, e, i, m, u, previo, intento); }
        public int finalizar(int id, int p, String e, Timestamp f, String error, String u, String previo) throws Exception { return service.persistirFinalizacion(con, id, p, e, f, error, u, previo); }
        public RequerimientoCompraPedidoCotizacion obtenerPedido(int id, int p, int i) throws Exception { return service.obtenerPedido(con, id, p, i); }
        public int insertarPedido(RequerimientoCompraPedidoCotizacion d, String u) throws Exception { return service.insertarPedido(con, d, u); }
        public void commit() throws Exception { if(!propia)throw new SQLException("La transaccion pertenece al caller.");con.commit(); }
        public void rollback() throws Exception { if(!propia)throw new SQLException("La transaccion pertenece al caller.");con.rollback(); }
        public void cerrar() { Connection actual = con; con = null; if(propia)ConnectionHelper.cerrar(actual); }
    }

    /** Compatibilidad legacy: el Helper coordina y valida; la TX conserva JDBC. */
    @Deprecated
    public List<PrestadorCotizacion> listarPrestadoresCandidatos(int idRequerimientoCompra) throws Exception {
        return new NotificarCotizacionPrestadorHelper().listarPrestadoresCandidatosJava(idRequerimientoCompra);
    }

    /** Compatibilidad legacy: el Helper coordina y valida; la TX conserva JDBC. */
    @Deprecated
    public List<PrestadorCotizacion> listarPrestadoresConfiguracionCorreosPorRubro(int idTipoPrestacion) throws Exception {
        return new NotificarCotizacionPrestadorHelper().listarPrestadoresConfiguracionCorreosPorRubroJava(idTipoPrestacion);
    }

    /** Compatibilidad legacy: el Helper coordina y valida; la TX conserva JDBC. */
    @Deprecated
    public CotizacionPrestadorDiagnostico diagnosticarPrestadores(int idRequerimientoCompra) throws Exception {
        return new NotificarCotizacionPrestadorHelper().diagnosticarPrestadoresJava(idRequerimientoCompra);
    }

    /** Compatibilidad legacy: el Helper coordina y valida; la TX conserva JDBC. */
    @Deprecated
    public ReservaCotizacionPrestador reservarCotizacionPrestador(int idRequerimientoCompra, int idPrestador, String usuario) throws Exception {
        return new NotificarCotizacionPrestadorHelper().reservarCotizacionPrestadorJava(idRequerimientoCompra, idPrestador, usuario);
    }

    /** Compatibilidad legacy: el Helper coordina y valida; la TX conserva JDBC. */
    @Deprecated
    public FinalizacionCotizacionPrestador finalizarCotizacionPrestador(int idRequerimiento, int idPrestador, String estado, String error, String usuario) throws Exception {
        return new NotificarCotizacionPrestadorHelper().finalizarCotizacionPrestadorJava(idRequerimiento, idPrestador, estado, error, usuario);
    }

    private PrestadorCotizacion mapPrestadorCotizacion(
            ResultSet rs) throws Exception {

        PrestadorCotizacion prestador = new PrestadorCotizacion();
        prestador.setIdPrestador(rs.getInt("id_prestador"));
        prestador.setDescripcion(rs.getString("descripcion"));
        prestador.setCuit(rs.getString("cuit"));
        prestador.setEmail(rs.getString("email"));
        prestador.setIdTipoPrestador(rs.getInt("id_tipo_prestador"));
        prestador.setTipoPrestador(rs.getString("tipo_prestador"));
        return prestador;
    }


    /** Compatibilidad legacy: el Helper coordina y valida; la TX conserva JDBC. */
    @Deprecated
    public int registrarPedidoCotizacionDocumento(RequerimientoCompraPedidoCotizacion documento, String usuario) throws Exception {
        return new NotificarCotizacionPrestadorHelper().registrarPedidoCotizacionDocumentoJava(documento, usuario);
    }

    public boolean reservarCopiaCotizacion(
            int idRequerimiento) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();

            stmt = con.prepareCall(
                    SQL_RESERVAR_COPIA_COTIZACION
            );

            stmt.registerOutParameter(
                    1,
                    java.sql.Types.BOOLEAN
            );

            stmt.setInt(
                    2,
                    idRequerimiento
            );

            stmt.execute();

            return stmt.getBoolean(1);

        } finally {
            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
        }
    }


    public void liberarCopiaCotizacion(
            int idRequerimiento) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();

            stmt = con.prepareCall(
                    SQL_LIBERAR_COPIA_COTIZACION
            );

            stmt.setInt(
                    1,
                    idRequerimiento
            );

            stmt.execute();

        } finally {
            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
        }
    }
}
