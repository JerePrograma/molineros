package ar.com.ospim.desarrolloAppMobile.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.autorizaciones.action.AutorizacionPrestacionalEmail;
import ar.com.ospim.autorizaciones.beans.Estado;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.reportes.action.ReporteReclamosPrestacionales;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.desarrolloAppMobile.beans.ClienteAppMobile;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;
import ar.com.ospim.util.ConnectionHelper;

public class ClienteAppMobileServiceUtil {

    private static final Logger _log = Logger.getLogger(ClienteAppMobileServiceUtil.class);

    public static List<Comprobante> procesarPreautorizaciones(List<PreAutorizacion> lista) throws Exception {
        Integer idPreautorizacion = 0;
        List<Comprobante> comprobantesProcesados = new ArrayList<Comprobante>();
        List<Comprobante> comprobantesErroneos = new ArrayList<Comprobante>();

        Connection conn = null;
        try {
            conn = ConnectionHelper.getConnection();

            for (PreAutorizacion p : lista) {
                try {
                	
                	Afiliado afiliado = (p.getAfiliado() != null) ? p.getAfiliado() : new Afiliado();
                    String cuil = afiliado.getCuil_titular();
                    Integer inte = afiliado.getInte();
                    String nombre = afiliado.getNombre();
                    String apellido = afiliado.getApellido();
                    
                    if (yaExistePedidoApp(conn, p.getIdPedidoApp())) {
                        Integer idExistente = obtenerIdPreautorizacionExistente(conn, p.getIdPedidoApp());
                        p.setId(idExistente);

                        Afiliado a = null;

                        // 1) Intentar levantar afiliado de la pre existente
                        try {
                            PreAutorizacion preExist = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(idExistente);
                            if (preExist != null) a = preExist.getAfiliado();
                        } catch (Exception ex) {
                            _log.warn("[PRE] No se pudo leer pre existente id=" + idExistente, ex);
                        }
                        
                        if (a == null) a = EditarAfiliadoServiceUtil.getAfiliadoDadoBaja(cuil, inte);
                        
                        if (a == null) {
                            a = new Afiliado();
                            a.setCuil_titular(cuil);
                            a.setInte(inte);
                            a.setApellido(apellido);
                            a.setNombre(nombre);
                        } else {
                            if (a.getCuil_titular() == null) a.setCuil_titular(cuil);
                            if (inte != null) a.setInte(inte);
                            if ((a.getApellido() == null || a.getApellido().trim().isEmpty()) && apellido != null && !apellido.trim().isEmpty())
                                a.setApellido(apellido.trim());
                            if ((a.getNombre() == null || a.getNombre().trim().isEmpty()) && nombre != null && !nombre.trim().isEmpty())
                                a.setNombre(nombre.trim());
                        }
                        
                        p.setAfiliado(a);
                        
                        Comprobante comp = construirComprobante(p, "Ya existe ID");
                        comprobantesErroneos.add(comp);
                        continue;
                    }

                    Afiliado a = EditarAfiliadoServiceUtil.getAfiliadoDadoBaja(cuil, inte);
                    
                    if (a == null) {
                        a = new Afiliado();
                        a.setCuil_titular(cuil);
                        a.setApellido(apellido);
                        a.setNombre(nombre);
                        if (inte != null) a.setInte(inte);
                    }
                    p.setAfiliado(a);
                    Estado es = new Estado("AP", "");
                    p.setUltimoEstado(es);

                    idPreautorizacion = PreAutorizacionServiceUtil.insertaPreAutorizacion(
                            p, "AppMobile", "", a.getSeccional().getId_seccional());

                    p.setId(idPreautorizacion);
                    
                    String token = ClienteAppMobile.obtenerToken();
                    if (token != null) {
                        ClienteAppMobile.actualizarEstadoPedidoAutorizacion(p.getIdPedidoApp(), "CA", token);
                    } else {
                        _log.warn("No se pudo actualizar el estado del pedido " + p.getIdPedidoApp() + " porque el token es null");
                    }
                    
                    if (idPreautorizacion != null && p.getIdPedidoApp() > 0) {
                        CallableStatement stmt = null;
                        try {
                        	//actualiza id_preautorizaciones_app y la fecha actual.
                            String sqlUpdate = "{call autorizaciones.actualiza_id_pedidoapp(?, ?, CAST(? AS timestamp), ?)}";
                            stmt = conn.prepareCall(sqlUpdate);
                            stmt.setInt(1, idPreautorizacion);
                            stmt.setInt(2, p.getIdPedidoApp());
                            stmt.setTimestamp(3, p.getAlta_fecha());
                            stmt.setBoolean(4, true); 
                            stmt.executeUpdate();
                        } finally {
                            ConnectionHelper.cerrar(stmt);
                        }
                    }

                    Comprobante comp = construirComprobante(p, "INSERTADO");
                    comprobantesProcesados.add(comp);

                } catch (Exception e) {
                    _log.error("Error procesando preautorización", e);
                    Comprobante compError = construirComprobante(p, "ERROR");
                    comprobantesErroneos.add(compError);
                }
            }

        } finally {
            ConnectionHelper.cerrar(conn);
        }

        List<Comprobante> resultadoFinal = new ArrayList<Comprobante>();
        resultadoFinal.addAll(comprobantesProcesados);
        resultadoFinal.addAll(comprobantesErroneos);
        return resultadoFinal;
    }

    public static boolean yaExistePedidoApp(Connection conn, int idPedidoApp) {
        CallableStatement cs = null;
        boolean cerrarConexion = false;

        try {
            if (conn == null) {
                conn = ConnectionHelper.getConnection();
                cerrarConexion = true;
            }
            //existe una preautorización con ese id_preautorizaciones_app
            String sql = "{? = call autorizaciones.existe_id_pedido_app(?)}";
            cs = conn.prepareCall(sql);
            cs.registerOutParameter(1, Types.BOOLEAN);
            cs.setInt(2, idPedidoApp);
            cs.execute();

            return cs.getBoolean(1);

        } catch (Exception e) {
            _log.error("Error verificando existencia de id_preautorizaciones_app", e);
            return false;

        } finally {
            ConnectionHelper.cerrar(cs);
            if (cerrarConexion) {
                ConnectionHelper.cerrar(conn);
            }
        }
    }

    private static Comprobante construirComprobante(PreAutorizacion p, String estado) {
        Comprobante c = new Comprobante();

        // Siempre setear estado y tipo para que la vista pueda renderizar algo
        c.setEstado(estado);
        c.setTipoComprobante("Preautorización");

        if (p == null) {
            _log.error("PreAutorizacion es null al construir comprobante.");
            return c;
        }
        
        c.setPreAutorizacion(p);
        c.setIdPreautorizacion(p.getId());
        c.setAlta_fecha(p.getAlta_fecha());

        Afiliado a = p.getAfiliado();
        if (a != null) {
            c.setAfiliado(a);

            String cuil = a.getCuil_titular();
            if (cuil != null && !cuil.trim().isEmpty()) {
                Empresa emp = c.getAcreedorEmpresa();
                if (emp == null) emp = new Empresa();
                emp.setCuit(cuil);
                c.setAcreedorEmpresa(emp);
            } else {
                _log.warn("Afiliado sin CUIL al construir comprobante (idPre=" 
                          + p.getId() + ", pedidoApp=" + p.getIdPedidoApp() + ")");
            }
        } else {
            _log.error("Afiliado es null en PreAutorizacion (idPre=" 
                       + p.getId() + ", pedidoApp=" + p.getIdPedidoApp() + ")");
        }

        return c;
    }


    private static Integer obtenerIdPreautorizacionExistente(Connection conn, int idPedidoApp) {
        CallableStatement cs = null;
        boolean cerrarConexion = false;

        try {
            if (conn == null) {
                conn = ConnectionHelper.getConnection();
                cerrarConexion = true;
            }
            //devuelve id de la preautorizacion
            String sql = "{? = call autorizaciones.obtener_id_preautorizacion(?)}";
            cs = conn.prepareCall(sql);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, idPedidoApp);
            cs.execute();

            int id = cs.getInt(1);
            return cs.wasNull() ? null : id;

        } catch (Exception e) {
            _log.error("Error recuperando ID existente para idPedidoApp=" + idPedidoApp, e);
            return null;

        } finally {
            ConnectionHelper.cerrar(cs);
            if (cerrarConexion) {
                ConnectionHelper.cerrar(conn);
            }
        }
    }
    
    public static Integer getIdPreautorizacionPorPedidoApp(int idPedidoApp) {
        Connection conn = null;
        try {
            conn = ConnectionHelper.getConnection();
            return obtenerIdPreautorizacionExistente(conn, idPedidoApp);
        } catch (Exception e) {
            _log.error("No se pudo obtener ID de preautorización para pedidoApp=" + idPedidoApp, e);
            return null;
        } finally {
            ConnectionHelper.cerrar(conn);
        }
    }
    
    public static Integer getIdPedidoAppPorPreautorizacion(int idPreautorizacion) {
        Connection conn = null;
        CallableStatement cs = null;

        try {
        	//devuelve id de la preautorizacion APP
            conn = ConnectionHelper.getConnection();
            String sql = "{? = call autorizaciones.obtener_id_pedido_app(?)}";
            cs = conn.prepareCall(sql);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, idPreautorizacion);
            cs.execute();

            int id = cs.getInt(1);
            return cs.wasNull() ? null : id;

        } catch (Exception e) {
            _log.error("No se pudo obtener idPedidoApp para preautorización=" + idPreautorizacion, e);
            return null;

        } finally {
            ConnectionHelper.cerrar(cs, conn);
        }
    }

    public static List<Comprobante> procesarReintegros(List<ReclamoPrestacional> lista) throws Exception {
        Integer idReintegro = 0;
        List<Comprobante> comprobantesProcesados = new ArrayList<Comprobante>();
        List<Comprobante> comprobantesErroneos = new ArrayList<Comprobante>();

        Connection conn = null;
        try {
            conn = ConnectionHelper.getConnection();

            for (ReclamoPrestacional r : lista) {
                try {
                    if (yaExisteReintegroApp(conn, r.getIdReintegroApp())) {
                        Integer idExistente = obtenerIdReintegroExistente(conn, r.getIdReintegroApp());
                        r.setId(idExistente);

                        Afiliado a = EditarAfiliadoServiceUtil.getAfiliadoDadoBaja(
                                r.getAfiliado().getCuil_titular(), r.getAfiliado().getInte());
                        r.setAfiliado(a);
                        r.setSeccional(a.getSeccional());
                        
                        Integer idSeccional = (a.getSeccional() != null ? a.getSeccional().getId() : null);
                        r.setIdSeccional(idSeccional);
                        r.setTipoPedido("REINTEGRO");
                        r.setSector("PRESTACIONES MEDICAS");
                        
                        Comprobante comp = construirComprobanteReintegro(r, "Ya existe ID");
                        comprobantesErroneos.add(comp);

                        continue;
                    }

                    Afiliado afiliado = (r.getAfiliado() != null) ? r.getAfiliado() : new Afiliado();
                    String cuil = afiliado.getCuil_titular();
                    Integer inte = afiliado.getInte();

                    Afiliado a = EditarAfiliadoServiceUtil.getAfiliadoDadoBaja(cuil, inte);
                    if (a == null) {
                        a = new Afiliado();
                        a.setCuil_titular(cuil);
                        if (inte != null) a.setInte(inte);
                    }
                    r.setAfiliado(a);
                    
                    //se debe agregar estado AP
                    r.setEstado(6);
                    
                    r.setSeccional(a.getSeccional());
                    
                    Integer idSeccional = (a.getSeccional() != null ? a.getSeccional().getId() : null);
                    r.setIdSeccional(idSeccional);
                    r.setTipoPedido("REINTEGRO");
                    r.setSector("PRESTACIONES MEDICAS");
                    long companyId = PortalUtil.getDefaultCompanyId();
                    User user = UserLocalServiceUtil.getDefaultUser(companyId);
                    user.setScreenName("AppMobile");
                    
                    idReintegro = ReclamosPrestacionesServiceUtil.insertar(r, user);                    
                    r.setId(idReintegro);
                    
                    String token = ClienteAppMobile.obtenerToken();
                    if (token != null) {
                    	ClienteAppMobile.actualizarEstadoReintegro(r.getIdReintegroApp(), "CA", token);
                    } else {
                        _log.warn("No se pudo actualizar el estado del reintegro " + r.getIdReintegroApp() + " porque el token es null");
                    }
                    
                    if (idReintegro != null && r.getIdReintegroApp() > 0) {
                        CallableStatement stmt = null;
                        try {
                        	//actualiza id_reintegro_app y la fecha actual.
                        	String sqlUpdate = "{call actualiza_id_reintegroapp(?, ?, CAST(? AS timestamp))}";
                        	stmt = conn.prepareCall(sqlUpdate);
                        	stmt.setInt(1, idReintegro);
                        	stmt.setInt(2, r.getIdReintegroApp());
                        	Timestamp tsParam = (r.getAlta_fecha() != null) ? new Timestamp(r.getAlta_fecha().getTime()) : null;
                        	stmt.setTimestamp(3, tsParam);
                        	stmt.executeUpdate();
                        } finally {
                            ConnectionHelper.cerrar(stmt);
                        }
                    }
                    Comprobante comp = construirComprobanteReintegro(r, "INSERTADO");
                    comprobantesProcesados.add(comp);
                    
                } catch (Exception e) {
                    _log.error("Error procesando reintegro", e);
                    Comprobante compError = construirComprobanteReintegro(r, "ERROR");
                    comprobantesErroneos.add(compError);
                }
            }

        } finally {
            ConnectionHelper.cerrar(conn);
        }

        List<Comprobante> resultadoFinal = new ArrayList<Comprobante>();
        resultadoFinal.addAll(comprobantesProcesados);
        resultadoFinal.addAll(comprobantesErroneos);

        return resultadoFinal;
    }
    
    private static Integer obtenerIdReintegroExistente(Connection conn, int idReintegroApp) {
        CallableStatement cs = null;
        boolean cerrarConexion = false;

        try {
            if (conn == null) {
                conn = ConnectionHelper.getConnection();
                cerrarConexion = true;
            }
            //devuelve id del reintegro
            String sql = "{? = call obtener_id_reintegro(?)}";
            cs = conn.prepareCall(sql);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, idReintegroApp);
            cs.execute();

            int id = cs.getInt(1);
            return cs.wasNull() ? null : id;

        } catch (Exception e) {
            _log.error("Error recuperando ID existente para reintegro=" + idReintegroApp, e);
            return null;

        } finally {
            ConnectionHelper.cerrar(cs);
            if (cerrarConexion) {
                ConnectionHelper.cerrar(conn);
            }
        }
    }

    private static Comprobante construirComprobanteReintegro(ReclamoPrestacional r, String estado) {
        Comprobante c = new Comprobante();

        if (r == null) {
            _log.error("Reintegro es nulo al construir comprobante.");
            return c;
        }

        if (r.getAfiliado() == null) {
            _log.error("Afiliado es nulo en Reintegro con ID Pedido");
        } else {
            Afiliado a = r.getAfiliado();
            c.setAcreedorEmpresa(new Empresa(a.getCuil_titular()));
            c.setAfiliado(a);
        }

        c.setTipoComprobante("RP Reintegro");
        c.setAlta_fecha(r.getAlta_fecha());
        c.setIdReintegro(r.getId_reclamo());
        c.setReintegro(r);
        c.setEstado(estado);
        c.setUrlComprobante(r.getUrlComprobante());
        
        
        String tipoCode = r.getTipoComprobante();
        String tipoAbreviado = "OTR";
        if ("TIPO_FACTURA".equalsIgnoreCase(tipoCode)) {
            tipoAbreviado = "FCP";
        } else if ("TIPO_RECIBO".equalsIgnoreCase(tipoCode)) {
            tipoAbreviado = "RCB";
        }

        c.setTipoComprobante(tipoAbreviado);
        c.setLetraComprobante(r.getLetraComprobante());
        c.setSucuComprobante(r.getSucuComprobante());
        c.setNroComprobante(r.getNroComprobante());
        c.setImporteComprobante(r.getImporteComprobante());
        c.setCuitEmisor(r.getCuitPrestador());      


        return c;
    }
    
    public static Integer getIdReintegroPorPedidoApp(int idReintegroApp) {
        Connection conn = null;
        try {
            conn = ConnectionHelper.getConnection();
            return obtenerIdReintegroExistente(conn, idReintegroApp);
        } catch (Exception e) {
            _log.error("No se pudo obtener ID del reintegro para =" + idReintegroApp, e);
            return null;
        } finally {
            ConnectionHelper.cerrar(conn);
        }
    }
    
    public static boolean yaExisteReintegroApp(Connection conn, int idReintegroApp) {
        CallableStatement cs = null;
        boolean cerrarConexion = false;

        try {
            if (conn == null) {
                conn = ConnectionHelper.getConnection();
                cerrarConexion = true;
            }
            //existe una preautorización con ese id_preautorizaciones_app
            String sql = "{? = call autorizaciones.existe_id_reintegro_app(?)}";
            cs = conn.prepareCall(sql);
            cs.registerOutParameter(1, Types.BOOLEAN);
            cs.setInt(2, idReintegroApp);
            cs.execute();

            return cs.getBoolean(1);

        } catch (Exception e) {
            _log.error("Error verificando existencia de id_reintegro_app", e);
            return false;

        } finally {
            ConnectionHelper.cerrar(cs);
            if (cerrarConexion) {
                ConnectionHelper.cerrar(conn);
            }
        }
    }
}
