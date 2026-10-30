package ar.com.ospim.compras.requerimientos.helper;

import java.util.Locale;
import ar.com.ospim.compras.requerimientos.service.NotificarCotizacionPrestadorServiceUtil.Transaccion;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Collections;
import java.sql.Timestamp;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoComprasCreado;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.NotificarCotizacionPrestadorServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.servlets.PdfServlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class NotificarCotizacionPrestadorHelper {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    NotificarCotizacionPrestadorHelper.class
            );

    private static final String CONFIG_REQUERIMIENTO_REDIRECCION_QA_HABILITADA =
            "REQUERIMIENTO_REDIRECCION_QA_HABILITADA";

    private static final String CONFIG_REQUERIMIENTO_EMAIL_CC =
            "REQUERIMIENTO_EMAIL_CC";

    private static final String CONFIG_REQUERIMIENTO_EMAIL_QA =
            "REQUERIMIENTO_EMAIL_QA";

    private static final String CONFIG_REQUERIMIENTO_EMAIL_RESPUESTA =
            "REQUERIMIENTO_EMAIL_RESPUESTA";

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"
            );

    private static final Pattern SENSITIVE_DETAIL_PATTERN =
            Pattern.compile(
                    "(?i)(password|passwd|pwd|token|secret|"
                            + "api[_-]?key|authorization)"
                            + "\\s*[:=]\\s*\\S+"
            );

    private final CotizacionPrestadorMailHelper mailHelper =
            new CotizacionPrestadorMailHelper();

    private static String btrimEspacios(String valor) {
        int inicio = 0;
        int fin = valor.length();
        while (inicio < fin && valor.charAt(inicio) == ' ') inicio++;
        while (fin > inicio && valor.charAt(fin - 1) == ' ') fin--;
        return valor.substring(inicio, fin);
    }

    public static String normalizarRubroCotizacion(String valor) {
        String r = valor == null ? "" : btrimEspacios(valor.replace('_', ' ')).toUpperCase(Locale.ROOT);
        String origen = "\u00c1\u00c0\u00c4\u00c2\u00c9\u00c8\u00cb\u00ca\u00cd\u00cc\u00cf\u00ce\u00d3\u00d2\u00d6\u00d4\u00da\u00d9\u00dc\u00db";
        String destino = "AAAAEEEEIIIIOOOOUUUU";
        for (int i = 0; i < origen.length(); i++) r = r.replace(origen.charAt(i), destino.charAt(i));
        return r;
    }

    private static Set<String> rubrosActivos(List<Map<String, Object>> detalles) {
        Set<String> rubros = new HashSet<String>();
        for (Map<String, Object> f : detalles) {
            if (f.get("baja_fecha") == null && f.get("descripcion") != null) rubros.add((String) f.get("descripcion"));
        }
        return rubros;
    }

    public static boolean esCompatibleRubros(List<Map<String, Object>> detalles,
            List<Map<String, Object>> asociaciones) {
        Set<String> rubros = rubrosActivos(detalles);
        for (Map<String, Object> f : asociaciones) {
            if (rubros.contains(normalizarRubroCotizacion((String) f.get("rubro")))) return true;
        }
        return false;
    }

    private List<Integer> idsPrestadoresCompatibles(Set<String> rubros,
            List<Map<String, Object>> asociaciones) {
        Set<Integer> ids = new LinkedHashSet<Integer>();
        for (Map<String, Object> f : asociaciones) {
            if (rubros.contains(normalizarRubroCotizacion((String) f.get("rubro")))) {
                ids.add((Integer) f.get("id_prestador"));
            }
        }
        return new ArrayList<Integer>(ids);
    }

    private String resolverEmailsCrudos(Transaccion tx, int prestador) throws Exception {
        List<String> tipos = ConfiguracionComprasHelper.resolverTiposContactoEmail(
                tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_TIPOS_CONTACTO_EMAIL));
        Map<String, Integer> prioridades = ConfiguracionComprasHelper.resolverPrioridadesContactoEmail(
                tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_PRIORIDADES_CONTACTO_EMAIL));
        return resolverEmailsCrudos(tx, prestador, tipos, prioridades, tx.getAhora());
    }

    protected String resolverEmailsCrudos(Transaccion tx, int prestador,
            List<String> tipos, Map<String, Integer> prioridades, Timestamp ahora) throws Exception {
        for (String tipo : tipos) {
            if (!prioridades.containsKey(tipo)) throw new IllegalStateException("Falta prioridad configurada para un tipo de contacto permitido.");
        }
        List<Map<String, Object>> validos = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> crudo : tx.listarContactos(prestador)) {
            String tipo = crudo.get("tipo") == null ? "" : btrimEspacios((String) crudo.get("tipo")).toUpperCase(Locale.ROOT);
            String email = crudo.get("contacto") == null ? null : btrimEspacios((String) crudo.get("contacto"));
            Timestamp ce = (Timestamp) crudo.get("contacto_desde");
            Timestamp pce = (Timestamp) crudo.get("relacion_desde");
            if (!tipos.contains(tipo) || crudo.get("baja") != null || email == null
                    || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
                    || (ce != null && ce.compareTo(ahora) > 0) || (pce != null && pce.compareTo(ahora) > 0)) continue;
            Map<String, Object> f = new HashMap<String, Object>(crudo);
            Timestamp referencia = (Timestamp) f.get("modi");
            if (referencia == null) referencia = (Timestamp) f.get("alta");
            if (referencia == null) referencia = ce;
            if (referencia == null) referencia = pce;
            f.put("fecha", referencia); f.put("prioridad", prioridades.get(tipo));
            f.put("email", email); validos.add(f);
        }
        Collections.sort(validos, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> a, Map<String, Object> b) {
                int prioridad = ((Integer) a.get("prioridad")).compareTo((Integer) b.get("prioridad"));
                if (prioridad != 0) return prioridad;
                Timestamp fa = (Timestamp) a.get("fecha"); Timestamp fb = (Timestamp) b.get("fecha");
                if (fa != null || fb != null) {
                    if (fa == null) return 1; if (fb == null) return -1;
                    int fecha = fb.compareTo(fa); if (fecha != 0) return fecha;
                }
                int id = ((Integer) b.get("id")).compareTo((Integer) a.get("id"));
                if (id != 0) return id;
                return ((String) a.get("clave")).compareTo((String) b.get("clave"));
            }
        });
        Set<String> incluidos = new HashSet<String>(); StringBuilder salida = new StringBuilder();
        for (Map<String, Object> f : validos) {
            String email = (String) f.get("email");
            String clave = (String) f.get("clave");
            if (clave == null) throw new IllegalStateException("Falta la clave normalizada del contacto.");
            if (incluidos.add(clave)) {
                if (salida.length() > 0) salida.append(';'); salida.append(email);
            }
        }
        return salida.length() == 0 ? null : salida.toString();
    }

    private String normalizarUsuarioNotificacion(Transaccion tx,String usuario) throws Exception {
        String valor = usuario == null ? null : btrimEspacios(usuario);
        if (valor == null || valor.length() == 0) valor = ConfiguracionComprasHelper.exigirValor(
                "COMPRAS_USUARIO_DEFECTO_NOTIFICACION",tx.getConfiguracionCompra("COMPRAS_USUARIO_DEFECTO_NOTIFICACION"));
        return truncarErrorNotificacion(valor,100);
    }

    private String normalizarUsuarioPedido(Transaccion tx,String usuario) throws Exception {
        String valor = usuario == null ? null : btrimEspacios(usuario);
        if (valor == null || valor.length() == 0) valor = tx.getUsuarioActual();
        if (valor == null || valor.length() == 0 || valor.codePointCount(0,valor.length()) > 100)
            throw new IllegalArgumentException("No se pudo determinar un usuario de auditoria valido.");
        return valor;
    }

    public List<PrestadorCotizacion> listarPrestadoresCandidatosJava(int id) throws Exception {
        Transaccion tx=null;
        try{tx=abrirTransaccionNotificacion();return listarCandidatosEnTransaccion(tx,id);}
        finally{if(tx!=null)try{tx.rollback();}finally{tx.cerrar();}}
    }

    private List<PrestadorCotizacion> listarCandidatosEnTransaccion(Transaccion tx,int id) throws Exception {
        Map<String, String> estadosEnvio = leerEstadosEnvio(tx);
        RequerimientoCompra r = tx.getRequerimiento(id, false);
        if (r == null || !r.isActivo() || !ConfiguracionComprasHelper.contieneEntero(CONFIG_ESTADOS_NOTIFICAR,
                tx.getConfiguracionCompra(CONFIG_ESTADOS_NOTIFICAR),r.getEstado())) return new ArrayList<PrestadorCotizacion>();
        List<Integer> ids = idsPrestadoresCompatibles(rubrosActivos(tx.listarRubrosRequerimiento(id)),
                tx.listarRubrosPrestadores(null));
        List<String> reintentos = ConfiguracionComprasHelper.resolverEstadosReintentoNotificacion(
                tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_REINTENTO_NOTIFICACION));
        boolean todos = ConfiguracionComprasHelper.contieneEntero(CONFIG_ESTADOS_NOTIFICAR_TODOS,
                tx.getConfiguracionCompra(CONFIG_ESTADOS_NOTIFICAR_TODOS), r.getEstado());
        List<String> tipos = ConfiguracionComprasHelper.resolverTiposContactoEmail(
                tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_TIPOS_CONTACTO_EMAIL));
        Map<String, Integer> prioridades = ConfiguracionComprasHelper.resolverPrioridadesContactoEmail(
                tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_PRIORIDADES_CONTACTO_EMAIL));
        Timestamp ahora = tx.getAhora();
        List<PrestadorCotizacion> salida = new ArrayList<PrestadorCotizacion>();
        for (PrestadorCotizacion p : tx.listarPrestadores(ids, id)) {
            if (p.getBajaFecha() != null || !Boolean.TRUE.equals(p.getSolicitarCotizacion())) continue;
            if (!todos && p.getEstadoEnvio() != null && !reintentos.contains(p.getEstadoEnvio())) continue;
            p.setEmail(resolverEmailsCrudos(tx, p.getIdPrestador(), tipos, prioridades, ahora)); salida.add(p);
        }
        return salida;
    }

    public boolean hayPrestadoresPendientesNotificacionJava(int id) throws Exception {
        if(id<=0)throw new IllegalArgumentException("Debe informar el requerimiento de compra.");
        Transaccion tx=null;
        try{tx=abrirTransaccionNotificacion();return !listarCandidatosEnTransaccion(tx,id).isEmpty();}
        finally{if(tx!=null)try{tx.rollback();}finally{tx.cerrar();}}
    }

    /** Lectura con la conexion del caller: no inicia/finaliza ni cierra su TX. */
    public List<PrestadorCotizacion> listarPrestadoresEnviados(java.sql.Connection con,int id,int limite) throws Exception {
        if(id<=0)throw new IllegalArgumentException("Debe informar el requerimiento de compra.");
        if(limite<=0)throw new IllegalArgumentException("El limite de prestadores debe ser mayor que cero.");
        Transaccion tx=NotificarCotizacionPrestadorServiceUtil.usarTransaccion(con);
        RequerimientoCompra r=tx.getRequerimiento(id,false);
        List<PrestadorCotizacion> salida=new ArrayList<PrestadorCotizacion>();
        if(r==null)return salida;
        String ids=tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS);
        String permitidos=tx.getConfiguracionCompra("COMPRAS_ESTADOS_PRESTADORES_ENVIADOS");
        validarEstadosEnviados(permitidos,ids);
        if(!ConfiguracionComprasHelper.resolverEstadoPermitido("PRESTADORES_ENVIADOS",r.getEstado(),permitidos))return salida;
        int aCotizar=ConfiguracionComprasHelper.resolverEstado("A_COTIZAR",ids);
        Map<String,String> envios=leerEstadosEnvio(tx);
        List<String> tipos=ConfiguracionComprasHelper.resolverTiposContactoEmail(tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_TIPOS_CONTACTO_EMAIL));
        Map<String,Integer> prioridades=ConfiguracionComprasHelper.resolverPrioridadesContactoEmail(tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_PRIORIDADES_CONTACTO_EMAIL));
        Timestamp ahora=tx.getAhora();
        List<Map<String,Object>> rubros= r.getEstado()==aCotizar?tx.listarRubrosRequerimiento(id):null;
        String excepciones=r.getEstado()==aCotizar?tx.getConfiguracionCompra("COMPRAS_EXCEPCIONES_PRESTADOR_REQUERIMIENTO"):null;
        for(PrestadorCotizacion p:tx.listarPrestadoresNotificados(id)){
            if(!envios.get("ENVIADO").equals(p.getEstadoEnvio())&&!envios.get("COTIZADO").equals(p.getEstadoEnvio()))continue;
            if(r.getEstado()==aCotizar){
                Map<String,Object> f=new HashMap<String,Object>();f.put("baja",p.getBajaFecha());f.put("solicitar",p.getSolicitarCotizacion());f.put("cuit",p.getCuit());
                if(!esCompatiblePrestadorCotizacion(r,f,rubros,tx.listarRubrosPrestadores(Integer.valueOf(p.getIdPrestador())),excepciones))continue;
            }
            p.setEmail(resolverEmailsCrudos(tx,p.getIdPrestador(),tipos,prioridades,ahora));
            String destino=p.getEmailDestino();if(destino!=null){destino=btrimEspacios(destino);if(destino.length()==0)destino=null;}p.setEmailDestino(destino);
            salida.add(p);if(salida.size()==limite)break;
        }
        return salida;
    }

    private void validarEstadosEnviados(String lista,String ids) {
        ConfiguracionComprasHelper.resolverEstado("A_COTIZAR",ids);
        Set<Integer> conocidos=new HashSet<Integer>();
        for(String valor:ConfiguracionComprasHelper.parsearPares(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS,ids).values())
            conocidos.add(Integer.valueOf(ConfiguracionComprasHelper.parsearEntero(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS,valor,false)));
        String clave="COMPRAS_ESTADOS_PRESTADORES_ENVIADOS";
        for(String valor:ConfiguracionComprasHelper.exigirValor(clave,lista).split(";",-1))
            if(!conocidos.contains(Integer.valueOf(ConfiguracionComprasHelper.parsearEntero(clave,valor,false))))
                throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException(clave);
    }

    public static boolean esCompatiblePrestadorCotizacion(RequerimientoCompra r,Map<String,Object> p,
            List<Map<String,Object>> rubrosRequerimiento,List<Map<String,Object>> rubrosPrestador,String excepcionesRaw) {
        if(r==null||!r.isActivo()||p==null||p.get("baja")!=null)return false;
        String clave="COMPRAS_EXCEPCIONES_PRESTADOR_REQUERIMIENTO";
        String excepciones=ConfiguracionComprasHelper.exigirValor(clave,excepcionesRaw);
        if(!"NINGUNA".equals(excepciones)){
            Map<String,String> configuradas=ConfiguracionComprasHelper.parsearPares(clave,excepciones);
            boolean excepcion=false;
            for(Map.Entry<String,String> fila:configuradas.entrySet()){
                int id=ConfiguracionComprasHelper.parsearEntero(clave,fila.getKey(),false);
                if(!fila.getValue().matches("[0-9]{11}"))throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException(clave);
                String cuit=p.get("cuit")==null?"":((String)p.get("cuit")).replaceAll("[^0-9]","");
                if(id==r.getIdRequerimientoCompra()&&fila.getValue().equals(cuit))excepcion=true;
            }
            if(excepcion)return true;
        }
        return Boolean.TRUE.equals(p.get("solicitar"))&&esCompatibleRubros(rubrosRequerimiento,rubrosPrestador);
    }

    public int confirmarEnvioACotizarJava(int id,String usuario) throws Exception {
        if(id<=0)throw new IllegalArgumentException("Debe informar el requerimiento de compra.");
        Transaccion tx=null;boolean commitInvocado=false;
        try{
            tx=abrirTransaccionNotificacion();RequerimientoCompra r=tx.getRequerimiento(id,true);
            if(r==null||!r.isActivo())throw new IllegalArgumentException("No se encontro el requerimiento activo.");
            String ids=tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS);
            int pendiente=ConfiguracionComprasHelper.resolverEstado("PENDIENTE",ids),aCotizar=ConfiguracionComprasHelper.resolverEstado("A_COTIZAR",ids);
            if(r.getEstado()==aCotizar){commitInvocado=true;tx.commit();return aCotizar;}
            if(r.getEstado()!=pendiente)throw new IllegalArgumentException("Solo un requerimiento PENDIENTE puede pasar a A COTIZAR.");
            String enviado=leerEstadosEnvio(tx).get("ENVIADO");
            int resultado=pendiente;
            if(tx.listarEstadosNotificaciones(id).contains(enviado)){
                if(!ConfiguracionComprasHelper.resolverTransicionEstado(pendiente,aCotizar,tx.getConfiguracionCompra("COMPRAS_TRANSICIONES_ESTADO"),ids))
                    throw new IllegalStateException("La transicion de envio a cotizar no esta habilitada.");
                if(tx.cambiarEstadoRequerimiento(id,pendiente,aCotizar,normalizarUsuarioPedido(tx,usuario))!=1)
                    throw new IllegalStateException("El requerimiento fue modificado por otro proceso.");
                resultado=aCotizar;
            }
            commitInvocado=true;tx.commit();return resultado;
        }catch(Exception e){boolean rollbackConfirmado=revertirNotificacion(tx);if(commitInvocado||!rollbackConfirmado)throw new OperacionNotificacionAmbiguaException(e);throw e;}
        finally{if(tx!=null)tx.cerrar();}
    }

    public List<PrestadorCotizacion> listarPrestadoresConfiguracionCorreosPorRubroJava(int rubro) throws Exception {
        Transaccion tx = null;
        try {
            tx = abrirTransaccionNotificacion();
            Map<String, String> estadosEnvio = leerEstadosEnvio(tx);
            Map<String, Object> r = tx.obtenerRubro(rubro);
            List<PrestadorCotizacion> salida = new ArrayList<PrestadorCotizacion>();
            if (r == null || !Boolean.TRUE.equals(r.get("activo")) || r.get("baja") != null) return salida;
            Set<String> rubros = new HashSet<String>(); rubros.add((String) r.get("descripcion"));
            List<Integer> ids = idsPrestadoresCompatibles(rubros, tx.listarRubrosPrestadores(null));
            List<String> tipos = ConfiguracionComprasHelper.resolverTiposContactoEmail(
                    tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_TIPOS_CONTACTO_EMAIL));
            Map<String, Integer> prioridades = ConfiguracionComprasHelper.resolverPrioridadesContactoEmail(
                    tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_PRIORIDADES_CONTACTO_EMAIL));
            Timestamp ahora = tx.getAhora();
            for (PrestadorCotizacion p : tx.listarPrestadores(ids, 0)) {
                if (p.getBajaFecha() != null || !Boolean.TRUE.equals(p.getSolicitarCotizacion())) continue;
                p.setEmail(resolverEmailsCrudos(tx, p.getIdPrestador(), tipos, prioridades, ahora)); salida.add(p);
            }
            return salida;
        } finally { if (tx != null) { try { tx.rollback(); } finally { tx.cerrar(); } } }
    }

    public CotizacionPrestadorDiagnostico diagnosticarPrestadoresJava(int id) throws Exception {
        Transaccion tx = null;
        try {
            tx = abrirTransaccionNotificacion();
            Map<String, String> estadosEnvio = leerEstadosEnvio(tx);
            RequerimientoCompra r = tx.getRequerimiento(id, false);
            CotizacionPrestadorDiagnostico d = new CotizacionPrestadorDiagnostico();
            if (r == null || r.getBajaFecha() != null) return d;
            List<Map<String, Object>> asociaciones = tx.listarRubrosPrestadores(null);

            Set<Integer> compatibles = new HashSet<Integer>(idsPrestadoresCompatibles(
                    rubrosActivos(tx.listarRubrosRequerimiento(id)), asociaciones));
            int habilitados = 0; int compatiblesCantidad = 0; int bloqueados = 0;
            for (Map<String, Object> p : tx.listarHabilitacionPrestadores(id)) {
                if (p.get("baja") != null || !Boolean.TRUE.equals(p.get("solicitar"))) continue;
                habilitados++;
                if (compatibles.contains((Integer) p.get("id"))) {
                    compatiblesCantidad++;
                    String estado = (String) p.get("estado");
                    if (estadosEnvio.get("ENVIADO").equals(estado)
                            || estadosEnvio.get("COTIZADO").equals(estado)
                            || estadosEnvio.get("PROCESANDO").equals(estado)) bloqueados++;
                }
            }
            d.setPrestadoresHabilitados(habilitados); d.setPrestadoresCompatiblesSector(compatiblesCantidad);
            d.setPrestadoresBloqueadosEstadoPrevio(bloqueados); return d;
        } finally { if (tx != null) { try { tx.rollback(); } finally { tx.cerrar(); } } }
    }

    private Map<String, String> leerEstadosEnvio(Transaccion tx) throws Exception {
        String valor = tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_ENVIO);
        Map<String, String> estados = new HashMap<String, String>();
        String[] codigos = { "PENDIENTE", "PROCESANDO", "ENVIADO", "ERROR", "EMAIL_INVALIDO", "COTIZADO" };
        for (int i = 0; i < codigos.length; i++) {
            estados.put(codigos[i], ConfiguracionComprasHelper.resolverEstadoEnvio(codigos[i], valor));
        }
        return estados;
    }

    private static final String CONFIG_ESTADOS_NOTIFICAR = "COMPRAS_ESTADOS_NOTIFICAR_PRESTADORES";
    private static final String CONFIG_ESTADOS_NOTIFICAR_TODOS = "COMPRAS_ESTADOS_NOTIFICAR_TODOS";

    private static final class OperacionNotificacionAmbiguaException extends Exception {
        private OperacionNotificacionAmbiguaException(Exception causa) {
            super("No se pudo confirmar el resultado de la notificacion. Consulte antes de reintentar; se conservan los documentos.", causa);
        }
    }

    protected Transaccion abrirTransaccionNotificacion() throws Exception {
        return NotificarCotizacionPrestadorServiceUtil.abrirTransaccion();
    }

    private boolean revertirNotificacion(Transaccion tx) {
        if (tx != null) {
            try { tx.rollback(); }
            catch (Exception e) {
                _log.error("No se pudo confirmar rollback de notificacion.", e);
                return false;
            }
        }
        return true;
    }

    private void validarCabeceraNotificacion(RequerimientoCompra r, Transaccion tx) throws Exception {
        if (r == null || r.getIdRequerimientoCompra() <= 0 || r.getBajaFecha() != null) {
            throw new Exception("No existe el requerimiento activo para notificar.");
        }
        if (!ConfiguracionComprasHelper.contieneEntero(CONFIG_ESTADOS_NOTIFICAR,
                tx.getConfiguracionCompra(CONFIG_ESTADOS_NOTIFICAR), r.getEstado())) {
            throw new Exception("El estado actual no permite notificar prestadores.");
        }
    }

    public ReservaCotizacionPrestador reservarCotizacionPrestadorJava(
            int id, int prestador, String usuario) throws Exception {
        if (id <= 0 || prestador <= 0) throw new Exception("Debe informar requerimiento y prestador validos.");
        Transaccion tx = null; boolean commitInvocado = false;
        try {
            tx = abrirTransaccionNotificacion();
            Map<String, String> estadosEnvio = leerEstadosEnvio(tx);
            RequerimientoCompra r = tx.getRequerimiento(id, true);
            validarCabeceraNotificacion(r, tx);
            List<Integer> ids = new ArrayList<Integer>(); ids.add(Integer.valueOf(prestador));
            List<PrestadorCotizacion> encontrados = tx.listarPrestadores(ids, id);
            if (encontrados.size() != 1 || encontrados.get(0).getBajaFecha() != null
                    || !Boolean.TRUE.equals(encontrados.get(0).getSolicitarCotizacion())) {
                throw new Exception("El prestador no existe, esta de baja o no esta habilitado para cotizar.");
            }
            if (!esCompatibleRubros(tx.listarRubrosRequerimiento(id),
                    tx.listarRubrosPrestadores(Integer.valueOf(prestador)))) {
                throw new Exception("El prestador no posee un rubro compatible con los detalles activos.");
            }
            List<String> reintentos = ConfiguracionComprasHelper.resolverEstadosReintentoNotificacion(
                    tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_REINTENTO_NOTIFICACION));
            String emails = resolverEmailsCrudos(tx, prestador);
            String auditor = normalizarUsuarioNotificacion(tx,usuario);
            tx.insertarNotificacion(id, prestador, estadosEnvio.get("PENDIENTE"), emails, auditor);
            ReservaCotizacionPrestador actual = tx.obtenerNotificacion(id, prestador, true);
            if (actual == null) throw new Exception("No se pudo localizar la fila de notificacion.");
            ReservaCotizacionPrestador salida = new ReservaCotizacionPrestador();
            salida.setEstadoEnvio(actual.getEstadoEnvio()); salida.setEmailDestino(actual.getEmailDestino());
            salida.setIntentos(actual.getIntentos());
            if (estadosEnvio.get("ENVIADO").equals(actual.getEstadoEnvio())) {
                salida.setMotivoCodigo("YA_ENVIADO"); salida.setMotivoDescripcion("El prestador ya se encontraba ENVIADO. No se realizo un reenvio.");
            } else if (estadosEnvio.get("COTIZADO").equals(actual.getEstadoEnvio())) {
                salida.setMotivoCodigo("YA_COTIZADO"); salida.setMotivoDescripcion("El prestador ya se encontraba COTIZADO. No se realizo un reenvio.");
            } else if (estadosEnvio.get("PROCESANDO").equals(actual.getEstadoEnvio())) {
                salida.setMotivoCodigo("YA_PROCESANDO"); salida.setMotivoDescripcion("El prestador ya se encontraba PROCESANDO, posiblemente por otra ejecucion concurrente.");
            } else if (!reintentos.contains(actual.getEstadoEnvio())) {
                throw new Exception("El estado de notificacion no esta configurado como reintentable.");
            } else {
                if (actual.getIntentos() == null || actual.getIntentos().intValue() < 0
                        || actual.getIntentos().intValue() == Integer.MAX_VALUE) {
                    throw new Exception("La notificacion no posee un numero de intento valido.");
                }
                int intento = actual.getIntentos().intValue() + 1;
                if (tx.actualizarReserva(id, prestador, estadosEnvio.get("PROCESANDO"),
                        intento, emails, auditor, actual.getEstadoEnvio(), actual.getIntentos().intValue()) != 1) {
                    throw new Exception("La notificacion cambio antes de reservar. Actualice la pantalla.");
                }
                salida.setReservado(true); salida.setEstadoEnvio(estadosEnvio.get("PROCESANDO"));
                salida.setEmailDestino(emails); salida.setIntentos(Integer.valueOf(intento));
                salida.setMotivoCodigo("RESERVA_OTORGADA");
                salida.setMotivoDescripcion("La ejecucion obtuvo la reserva exclusiva y la fila quedo PROCESANDO.");
            }
            commitInvocado = true; tx.commit(); return salida;
        } catch (Exception e) {
            boolean rollbackConfirmado = revertirNotificacion(tx);
            if (commitInvocado || !rollbackConfirmado) throw new OperacionNotificacionAmbiguaException(e);
            throw e;
        } finally { if (tx != null) tx.cerrar(); }
    }

    private String truncarErrorNotificacion(String valor, int maximo) {
        if (valor == null || valor.codePointCount(0, valor.length()) <= maximo) return valor;
        return valor.substring(0, valor.offsetByCodePoints(0, maximo));
    }

    public FinalizacionCotizacionPrestador finalizarCotizacionPrestadorJava(
            int id, int prestador, String estado, String error, String usuario) throws Exception {
        if (id <= 0 || prestador <= 0) throw new Exception("Debe informar requerimiento y prestador validos.");
        Transaccion tx = null; boolean commitInvocado = false;
        try {
            tx = abrirTransaccionNotificacion();
            Map<String, String> estadosEnvio = leerEstadosEnvio(tx);
            String solicitado = estado == null ? null : btrimEspacios(estado);
            if (solicitado != null) solicitado = solicitado.toUpperCase(java.util.Locale.ROOT);
            List<String> permitidos = ConfiguracionComprasHelper.resolverEstadosFinalesNotificacion(
                    tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_FINALES_NOTIFICACION));
            if (solicitado == null || !permitidos.contains(solicitado)) throw new Exception("Estado final de notificacion no permitido.");
            int maximo = ConfiguracionComprasHelper.resolverMaxErrorNotificacion(
                    tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_MAX_ERROR_NOTIFICACION));
            String detalle = truncarErrorNotificacion(error, maximo);
            ReservaCotizacionPrestador actual = tx.obtenerNotificacion(id, prestador, true);
            FinalizacionCotizacionPrestador salida = new FinalizacionCotizacionPrestador();
            if (actual == null) {
                salida.setMotivo("No existe una fila de notificacion para el requerimiento y prestador.");
            } else {
                salida.setEstadoAnterior(actual.getEstadoEnvio()); salida.setEstadoActual(actual.getEstadoEnvio());
                if (!estadosEnvio.get("PROCESANDO").equals(actual.getEstadoEnvio())) {
                    salida.setMotivo("La fila no se encontraba PROCESANDO. No se modifico el estado.");
                } else {
                    boolean enviado = estadosEnvio.get("ENVIADO").equals(solicitado);
                    if (!enviado && detalle == null) detalle = "Error sin detalle informado.";
                    Timestamp fecha = enviado ? tx.getFechaEvento() : null;
                    int cantidad = tx.finalizar(id, prestador, solicitado, fecha, detalle,
                            normalizarUsuarioNotificacion(tx,usuario), actual.getEstadoEnvio());
                    if (cantidad != 1) {
                        salida.setMotivo("La fila cambio de estado antes de completar la finalizacion.");
                    } else {
                        salida.setActualizado(true); salida.setEstadoActual(solicitado);
                        salida.setMotivo("El estado final fue persistido correctamente.");
                    }
                }
            }
            commitInvocado = true; tx.commit(); return salida;
        } catch (Exception e) {
            boolean rollbackConfirmado = revertirNotificacion(tx);
            if (commitInvocado || !rollbackConfirmado) throw new OperacionNotificacionAmbiguaException(e);
            throw e;
        } finally { if (tx != null) tx.cerrar(); }
    }

    public int registrarPedidoCotizacionDocumentoJava(
            RequerimientoCompraPedidoCotizacion d, String usuario) throws Exception {
        if (d == null || d.getIdRequerimiento() == null || d.getIdRequerimiento().intValue() <= 0
                || d.getIdPrestador() == null || d.getIdPrestador().intValue() <= 0) {
            throw new Exception("Debe informar requerimiento y prestador del pedido.");
        }
        if (d.getDlGroupId() == null || d.getDlGroupId().longValue() <= 0
                || d.getDlFolderId() == null || d.getDlFolderId().longValue() <= 0
                || d.getDlFileEntryId() == null || d.getDlFileEntryId().longValue() <= 0
                || (d.getDlFileUuid() == null || btrimEspacios(d.getDlFileUuid()).length() == 0) || (d.getNombreOriginal() == null || btrimEspacios(d.getNombreOriginal()).length() == 0)
                || (d.getNombrePersistido() == null || btrimEspacios(d.getNombrePersistido()).length() == 0) || (d.getTitulo() == null || btrimEspacios(d.getTitulo()).length() == 0)) {
            throw new Exception("La identidad y los datos documentales del pedido no son validos.");
        }
        d.setDlFileUuid(btrimEspacios(d.getDlFileUuid())); d.setNombreOriginal(btrimEspacios(d.getNombreOriginal()));
        d.setNombrePersistido(btrimEspacios(d.getNombrePersistido())); d.setTitulo(btrimEspacios(d.getTitulo()));
        Transaccion tx = null; boolean commitInvocado = false;
        try {
            tx = abrirTransaccionNotificacion();
            Map<String, String> estadosEnvio = leerEstadosEnvio(tx);
            ReservaCotizacionPrestador actual = tx.obtenerNotificacion(
                    d.getIdRequerimiento().intValue(), d.getIdPrestador().intValue(), true);
            if (actual == null || !estadosEnvio.get("PROCESANDO").equals(actual.getEstadoEnvio())
                    || actual.getIntentos() == null || actual.getIntentos().intValue() <= 0) {
                throw new Exception("El pedido solo puede registrarse durante un envio PROCESANDO con intento valido.");
            }
            d.setIntento(actual.getIntentos());
            RequerimientoCompraPedidoCotizacion previo = tx.obtenerPedido(
                    d.getIdRequerimiento().intValue(), d.getIdPrestador().intValue(), d.getIntento().intValue());
            if (previo != null) {
                if (!mismoPedido(previo, d)) throw new Exception("El intento actual ya posee otro pedido de cotizacion registrado.");
            } else if (tx.insertarPedido(d, normalizarUsuarioPedido(tx,usuario)) != 1) {
                throw new Exception("No se pudo persistir el pedido de cotizacion.");
            }
            commitInvocado = true; tx.commit(); return d.getIntento().intValue();
        } catch (Exception e) {
            boolean rollbackConfirmado = revertirNotificacion(tx);
            if (commitInvocado || !rollbackConfirmado) throw new OperacionNotificacionAmbiguaException(e);
            throw e;
        } finally { if (tx != null) tx.cerrar(); }
    }

    private boolean mismoPedido(RequerimientoCompraPedidoCotizacion a, RequerimientoCompraPedidoCotizacion b) {
        return b.getDlGroupId().equals(a.getDlGroupId()) && b.getDlFolderId().equals(a.getDlFolderId())
                && b.getDlFileEntryId().equals(a.getDlFileEntryId()) && b.getDlFileUuid().equals(a.getDlFileUuid())
                && b.getNombreOriginal().equals(a.getNombreOriginal()) && b.getNombrePersistido().equals(a.getNombrePersistido())
                && b.getTitulo().equals(a.getTitulo());
    }

    public NotificacionCotizacionResultado notificarPrestadores(
            int idRequerimientoCompra,
            String usuario,
            long companyId,
            ServiceContext serviceContext)
            throws Exception {

        validarParametros(
                idRequerimientoCompra,
                companyId
        );

        RequerimientoCompra requerimiento =
                getRequerimientoCompra(
                        idRequerimientoCompra
                );

        validarRequerimiento(
                requerimiento
        );

        List<PrestadorCotizacion> candidatos =
                listarPrestadoresCandidatos(
                        idRequerimientoCompra
                );

        NotificacionCotizacionResultado resultado =
                new NotificacionCotizacionResultado();

        resultado.setTotalCandidatos(
                candidatos.size()
        );

        cargarDiagnosticoCandidatosConControl(
                requerimiento,
                resultado
        );

        if (candidatos.isEmpty()) {

            if (_log.isInfoEnabled()) {
                _log.info(
                        "No hay prestadores pendientes de notificación. "
                                + "idRequerimiento="
                                + idRequerimientoCompra
                                + ", habilitados="
                                + resultado.getPrestadoresHabilitados()
                                + ", compatiblesRubro="
                                + resultado
                                .getPrestadoresCompatiblesSector()
                                + ", bloqueadosEstadoPrevio="
                                + resultado
                                .getPrestadoresBloqueadosEstadoPrevio()
                );
            }

            return resultado;
        }

        DocumentoLibraryComprasHelper
                .validarContextoDocumentLibrary(
                        serviceContext
                );

        byte[] pedidoPresupuestoPdf =
                generarPedidoPresupuestoPdf(
                        idRequerimientoCompra
                );

        byte[] pedidoPresupuestoFarmaciaPdf = pedidoPresupuestoPdf;
        for (int i = 0; i < candidatos.size(); i++) {
            if (esFarmaciaDestinataria(candidatos.get(i))) {
                pedidoPresupuestoFarmaciaPdf =
                        generarPedidoPresupuestoFarmaciaPdf(
                                requerimiento, pedidoPresupuestoPdf);
                break;
            }
        }

        String nombrePedidoPresupuestoPdf =
                "PedidoPresupuesto_"
                        + idRequerimientoCompra
                        + ".pdf";

        List<OrdenMedicaAdjunta> ordenesMedicasAdjuntas =
                recuperarOrdenesMedicasAdjuntas(
                        idRequerimientoCompra,
                        companyId
                );

        boolean copiaCotizacionEnviada =
                false;

        for (int i = 0;
             i < candidatos.size();
             i++) {

            copiaCotizacionEnviada =
                    procesarPrestador(
                            requerimiento,
                            candidatos.get(i),
                            usuario,
                            companyId,
                            serviceContext,
                            resultado,
                            esFarmaciaDestinataria(candidatos.get(i))
                                    ? pedidoPresupuestoFarmaciaPdf
                                    : pedidoPresupuestoPdf,
                            nombrePedidoPresupuestoPdf,
                            ordenesMedicasAdjuntas,
                            copiaCotizacionEnviada
                    );
        }

        if (resultado.getPendientesSinClasificar() > 0) {

            _log.error(
                    "El proceso de notificación finalizo "
                            + "con candidatos sin clasificar. "
                            + "idRequerimiento="
                            + idRequerimientoCompra
                            + ", candidatos="
                            + resultado.getTotalCandidatos()
                            + ", procesados="
                            + resultado.getTotalProcesados()
                            + ", pendientesSinClasificar="
                            + resultado.getPendientesSinClasificar()
            );
        }

        return resultado;
    }

    private boolean procesarPrestador(
            RequerimientoCompra requerimiento,
            PrestadorCotizacion prestador,
            String usuario,
            long companyId,
            ServiceContext serviceContext,
            NotificacionCotizacionResultado resultado,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            List<OrdenMedicaAdjunta> ordenesMedicasAdjuntas,
            boolean copiaCotizacionEnviada) {

        if (prestador == null) {

            _log.error(
                    "La consulta de prestadores candidatos "
                            + "devolvio un elemento nulo. "
                            + "idRequerimiento="
                            + requerimiento
                            .getIdRequerimientoCompra()
            );

            registrarResultado(
                    resultado,
                    null,
                    null,
                    null,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "VALIDACION",
                    "No se pudo preparar la notificacion. "
                            + "Contacte a Sistemas.",
                    false
            );

            return copiaCotizacionEnviada;
        }

        int idRequerimiento =
                requerimiento
                        .getIdRequerimientoCompra();

        int idPrestador =
                prestador
                        .getIdPrestador();

        if (_log.isDebugEnabled()) {

            _log.debug(
                    "Procesando candidato de cotizacion. "
                            + "idRequerimiento="
                            + idRequerimiento
                            + ", sector="
                            + requerimiento.getIdSector()
                            + ", idPrestador="
                            + idPrestador
                            + ", idTipoPrestador="
                            + prestador.getIdTipoPrestador()
            );
        }

        ReservaCotizacionPrestador reserva;

        /*
         * ==========================================================
         * 1. RESERVA EXCLUSIVA
         * ==========================================================
         */
        try {

            reserva =
                    reservarCotizacionPrestador(
                            idRequerimiento,
                            idPrestador,
                            usuario
                    );

        } catch (Exception e) {

            _log.error(
                    "No se pudo reservar la notificacion "
                            + "de cotizacion. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    e
            );

            registrarResultado(
                    resultado,
                    prestador,
                    null,
                    null,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "RESERVA",
                    "No se pudo iniciar el envio. "
                            + "Contacte a Sistemas antes de reintentar.",
                    false
            );

            return copiaCotizacionEnviada;
        }

        if (reserva == null) {

            _log.error(
                    "La funcion de reserva no devolvio resultado. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
            );

            registrarResultado(
                    resultado,
                    prestador,
                    null,
                    null,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "RESERVA",
                    "No se pudo iniciar el envio. "
                            + "Contacte a Sistemas antes de reintentar.",
                    false
            );

            return copiaCotizacionEnviada;
        }

        if (!reserva.isReservado()) {

            String motivo =
                    construirMotivoReservaNoOtorgada(
                            reserva
                    );

            if (_log.isDebugEnabled()) {

                _log.debug(
                        "Prestador omitido porque no se obtuvo "
                                + "la reserva exclusiva. "
                                + "idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                                + ", estadoObservado="
                                + reserva.getEstadoEnvio()
                                + ", motivoCodigo="
                                + reserva.getMotivoCodigo()
                                + ", motivo="
                                + motivo
                );
            }

            registrarResultado(
                    resultado,
                    prestador,
                    reserva.getEmailDestino(),
                    null,
                    NotificacionCotizacionDetalle.RESULTADO_OMITIDO,
                    "RESERVA",
                    motivo,
                    false
            );

            return copiaCotizacionEnviada;
        }

        /*
         * Desde aca la fila queda PROCESANDO y esta ejecucion
         * posee la reserva exclusiva.
         */
        String emailsReservados =
                normalizarEmail(
                        reserva.getEmailDestino()
                );

        String[] emailsReales =
                resolverEmailsPrestador(
                        emailsReservados
                );

        boolean modoTemporal =
                redireccionQaHabilitada();

        boolean prestadorSinEmail =
                emailsReales == null
                        || emailsReales.length == 0;

        boolean emailRealInvalidoAdvertido =
                modoTemporal
                        && prestadorSinEmail;

        /*
         * ==========================================================
         * 2. VALIDACION DE LOS DESTINATARIOS REALES
         * ==========================================================
         */
        if (!modoTemporal
                && prestadorSinEmail) {

            String errorTecnico =
                    "El prestador no posee contactos electronicos "
                            + "vigentes y validos asociados mediante "
                            + "prestad_contacto_e.";

            _log.warn(
                    "No se intentara enviar la cotizacion porque "
                            + "el prestador no posee emails habilitados "
                            + "obtenidos mediante prestad_contacto_e. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
            );

            boolean persistido =
                    finalizarConControl(
                            idRequerimiento,
                            idPrestador,
                            ConfiguracionComprasHelper.getEstadoEnvio("EMAIL_INVALIDO"),
                            errorTecnico,
                            usuario
                    );

            String motivoUsuario;

            if (persistido) {

                motivoUsuario =
                        "El prestador no tiene un email "
                                + "habilitado registrado.";

            } else {

                motivoUsuario =
                        "El prestador no tiene un email habilitado "
                                + "y el resultado no pudo registrarse. "
                                + "Contacte a Sistemas.";
            }

            registrarResultado(
                    resultado,
                    prestador,
                    null,
                    null,
                    persistido
                            ? NotificacionCotizacionDetalle
                            .RESULTADO_EMAIL_INVALIDO
                            : NotificacionCotizacionDetalle
                            .RESULTADO_ERROR,
                    persistido
                            ? "VALIDACION_EMAIL"
                            : "PERSISTENCIA",
                    motivoUsuario,
                    false
            );

            return copiaCotizacionEnviada;
        }

        if (modoTemporal
                && prestadorSinEmail) {

            _log.warn(
                    "El prestador no posee emails habilitados "
                            + "asociados mediante prestad_contacto_e. "
                            + "El envio continuara exclusivamente "
                            + "al destinatario temporal de QA. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
            );
        }

        String[] emailsDestino =
                resolverEmailsDestino(
                        emailsReales,
                        modoTemporal
                );

        boolean destinoEfectivoValido =
                emailsDestino != null
                        && emailsDestino.length > 0;

        if (destinoEfectivoValido) {

            for (int i = 0;
                 i < emailsDestino.length;
                 i++) {

                if (!esEmailValido(
                        emailsDestino[i]
                )) {

                    destinoEfectivoValido =
                            false;

                    break;
                }
            }
        }

        String emailsRealesDetalle =
                unirEmails(
                        emailsReales
                );

        String emailsDestinoDetalle =
                unirEmails(
                        emailsDestino
                );

        /*
         * ==========================================================
         * 2.1 VALIDACION DEL DESTINO EFECTIVO
         * ==========================================================
         */
        if (!destinoEfectivoValido) {

            String errorTecnico;

            if (modoTemporal) {

                errorTecnico =
                        "El email destino temporal de QA "
                                + "es inexistente o invalido.";

            } else {

                errorTecnico =
                        "Uno o mas emails destino efectivos "
                                + "del prestador son inexistentes "
                                + "o invalidos.";
            }

            _log.warn(
                    "No se intentara enviar la cotizacion "
                            + "porque el conjunto de destinatarios "
                            + "efectivos es invalido. "
                            + "modoTemporal="
                            + modoTemporal
                            + ", cantidadDestinatarios="
                            + (
                            emailsDestino != null
                                    ? emailsDestino.length
                                    : 0
                    )
                            + ", idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
            );

            boolean persistido =
                    finalizarConControl(
                            idRequerimiento,
                            idPrestador,
                            ConfiguracionComprasHelper.getEstadoEnvio("EMAIL_INVALIDO"),
                            errorTecnico,
                            usuario
                    );

            String motivoUsuario;

            if (modoTemporal) {

                motivoUsuario =
                        "El destinatario configurado para las pruebas "
                                + "no es valido. Contacte a Sistemas.";

            } else {

                motivoUsuario =
                        "El prestador no tiene destinatarios "
                                + "de email validos registrados.";
            }

            if (!persistido) {

                motivoUsuario +=
                        " El resultado tampoco pudo registrarse.";
            }

            registrarResultado(
                    resultado,
                    prestador,
                    emailsRealesDetalle,
                    emailsDestinoDetalle,
                    persistido
                            ? NotificacionCotizacionDetalle
                            .RESULTADO_EMAIL_INVALIDO
                            : NotificacionCotizacionDetalle
                            .RESULTADO_ERROR,
                    persistido
                            ? "VALIDACION_EMAIL"
                            : "PERSISTENCIA",
                    motivoUsuario,
                    emailRealInvalidoAdvertido
            );

            return copiaCotizacionEnviada;
        }

        if (modoTemporal
                && _log.isDebugEnabled()) {

            _log.debug(
                    "Modo temporal de notificacion activo. "
                            + "El correo sera redirigido "
                            + "exclusivamente al destinatario fijo de QA. "
                            + "cantidadEmailsReales="
                            + (
                            emailsReales != null
                                    ? emailsReales.length
                                    : 0
                    )
                            + ", idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
            );
        }

        /*
         * ==========================================================
         * 3. CONSERVAR EL PEDIDO EXACTO ANTES DEL ENVIO
         * ==========================================================
         */
        try {

            registrarPedidoCotizacionActual(
                    idRequerimiento,
                    idPrestador,
                    pedidoPresupuestoPdf,
                    nombrePedidoPresupuestoPdf,
                    usuario,
                    serviceContext
            );

        } catch (Exception e) {

            String detalleError =
                    construirDetalleError(
                            e
                    );

            boolean persistido =
                    finalizarConControl(
                            idRequerimiento,
                            idPrestador,
                            ConfiguracionComprasHelper.getEstadoEnvio("ERROR"),
                            detalleError,
                            usuario
                    );

            _log.error(
                    "No se pudo conservar el pedido de cotizacion "
                            + "antes de enviar el correo. "
                            + "El envio fue cancelado. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
                            + ", estadoErrorPersistido="
                            + persistido,
                    e
            );

            registrarResultado(
                    resultado,
                    prestador,
                    emailsRealesDetalle,
                    emailsDestinoDetalle,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "DOCUMENTO_PEDIDO_COTIZACION",
                    "No se pudo conservar el pedido de cotizacion. "
                            + "No se envio el correo. "
                            + "Contacte a Sistemas.",
                    emailRealInvalidoAdvertido
            );

            return copiaCotizacionEnviada;
        }

        /*
         * La copia BCC se reserva por requerimiento.
         *
         * El boolean local evita consultas innecesarias durante
         * la misma ejecución. La base garantiza la exclusividad
         * entre ejecuciones y reintentos.
         */
        boolean incluirCopiaCotizacion =
                false;

        if (!copiaCotizacionEnviada) {

            try {

                incluirCopiaCotizacion =
                        NotificarCotizacionPrestadorServiceUtil
                                .reservarCopiaCotizacion(
                                        idRequerimiento
                                );

            } catch (Exception e) {

                _log.error(
                        "No se pudo reservar el envio de la copia "
                                + "de cotizacion. "
                                + "idRequerimiento="
                                + idRequerimiento,
                        e
                );
            }
        }

        /*
         * ==========================================================
         * 4. ENVIO DEL CORREO
         * ==========================================================
         */
        try {

            String asunto =
                    construirAsunto(
                            requerimiento
                    );

            String cuerpo =
                    construirCuerpo(
                            requerimiento,
                            prestador
                    );

            enviarMail(
                    companyId,
                    emailsDestino,
                    asunto,
                    cuerpo,
                    pedidoPresupuestoPdf,
                    nombrePedidoPresupuestoPdf,
                    ordenesMedicasAdjuntas,
                    incluirCopiaCotizacion
            );

            if (incluirCopiaCotizacion) {

                copiaCotizacionEnviada =
                        true;
            }

        } catch (
                CotizacionPrestadorMailHelper
                        .EnvioParcialException e) {

            /*
             * Si el SMTP alcanzó a aceptar la copia BCC,
             * la reserva queda consumida.
             *
             * Si no fue aceptada, se libera para que el próximo
             * prestador o reintento pueda volver a intentar la copia.
             */
            if (incluirCopiaCotizacion) {

                if (e.isCopiaEnviada()) {

                    copiaCotizacionEnviada =
                            true;

                } else {

                    try {

                        NotificarCotizacionPrestadorServiceUtil
                                .liberarCopiaCotizacion(
                                        idRequerimiento
                                );

                    } catch (Exception liberarError) {

                        _log.error(
                                "No se pudo liberar la reserva "
                                        + "de copia de cotizacion. "
                                        + "idRequerimiento="
                                        + idRequerimiento,
                                liberarError
                        );
                    }
                }
            }

            String advertencia =
                    "ADVERTENCIA: envio parcial. "
                            + e.getDestinatariosEnviados()
                            + " de "
                            + e.getDestinatariosTotales()
                            + " destinatarios fueron aceptados.";

            boolean persistido =
                    finalizarConControl(
                            idRequerimiento,
                            idPrestador,
                            ConfiguracionComprasHelper.getEstadoEnvio("ENVIADO"),
                            advertencia,
                            usuario
                    );

            registrarResultado(
                    resultado,
                    prestador,
                    emailsRealesDetalle,
                    emailsDestinoDetalle,
                    persistido
                            ? NotificacionCotizacionDetalle
                            .RESULTADO_ENVIADO
                            : NotificacionCotizacionDetalle
                            .RESULTADO_ERROR,
                    persistido
                            ? "ENVIO_PARCIAL"
                            : "PERSISTENCIA",
                    persistido
                            ? advertencia
                            : "El envio parcial no pudo registrarse.",
                    emailRealInvalidoAdvertido
            );

            return copiaCotizacionEnviada;

        } catch (Exception e) {

            /*
             * El envío no fue confirmado.
             *
             * Si esta ejecución había reservado la copia BCC,
             * se libera para permitir otro intento.
             */
            if (incluirCopiaCotizacion) {

                try {

                    NotificarCotizacionPrestadorServiceUtil
                            .liberarCopiaCotizacion(
                                    idRequerimiento
                            );

                } catch (Exception liberarError) {

                    _log.error(
                            "No se pudo liberar la reserva "
                                    + "de copia de cotizacion. "
                                    + "idRequerimiento="
                                    + idRequerimiento,
                            liberarError
                    );
                }
            }

            String detalleError =
                    construirDetalleError(
                            e
                    );

            boolean persistido =
                    finalizarConControl(
                            idRequerimiento,
                            idPrestador,
                            ConfiguracionComprasHelper.getEstadoEnvio("ERROR"),
                            detalleError,
                            usuario
                    );

            String motivoUsuario;

            if (persistido) {

                motivoUsuario =
                        "El correo no pudo enviarse. "
                                + "Contacte a Sistemas "
                                + "antes de reintentar.";

            } else {

                motivoUsuario =
                        "El correo no pudo enviarse y el resultado "
                                + "no pudo registrarse. "
                                + "Contacte a Sistemas "
                                + "antes de reintentar.";
            }

            _log.error(
                    "Fallo el envio de la cotizacion. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
                            + ", cantidadDestinatarios="
                            + emailsDestino.length
                            + ", estadoErrorPersistido="
                            + persistido,
                    e
            );

            registrarResultado(
                    resultado,
                    prestador,
                    emailsRealesDetalle,
                    emailsDestinoDetalle,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "ENVIO",
                    motivoUsuario,
                    emailRealInvalidoAdvertido
            );

            return copiaCotizacionEnviada;
        }

        /*
         * ==========================================================
         * 5. CONFIRMACION DEL ENVIO
         * ==========================================================
         */
        boolean enviadoPersistido =
                finalizarConControl(
                        idRequerimiento,
                        idPrestador,
                        ConfiguracionComprasHelper.getEstadoEnvio("ENVIADO"),
                        null,
                        usuario
                );

        if (!enviadoPersistido) {

            String motivoUsuario =
                    "El correo fue aceptado, pero el resultado "
                            + "del envio no pudo confirmarse. "
                            + "No reintente hasta verificarlo "
                            + "con Sistemas.";

            _log.error(
                    "El servicio de correo acepto el mensaje, "
                            + "pero no se pudo persistir ENVIADO. "
                            + "La fila puede permanecer PROCESANDO. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
                            + ", cantidadDestinatarios="
                            + emailsDestino.length
            );

            registrarResultado(
                    resultado,
                    prestador,
                    emailsRealesDetalle,
                    emailsDestinoDetalle,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "PERSISTENCIA",
                    motivoUsuario,
                    emailRealInvalidoAdvertido
            );

            return copiaCotizacionEnviada;
        }

        /*
         * ==========================================================
         * 6. RESULTADO EXITOSO
         * ==========================================================
         */
        String motivoExito;

        if (modoTemporal) {

            motivoExito =
                    "Correo enviado al destinatario temporal "
                            + "de QA y resultado confirmado.";

            if (emailRealInvalidoAdvertido) {

                motivoExito +=
                        " El prestador no posee un email "
                                + "habilitado y debe revisarse.";
            }

        } else {

            motivoExito =
                    "Correo enviado a todos los emails habilitados "
                            + "del prestador y resultado confirmado.";
        }

        registrarResultado(
                resultado,
                prestador,
                emailsRealesDetalle,
                emailsDestinoDetalle,
                NotificacionCotizacionDetalle.RESULTADO_ENVIADO,
                "FINALIZADO",
                motivoExito,
                emailRealInvalidoAdvertido
        );

        if (_log.isInfoEnabled()) {

            _log.info(
                    "Cotizacion enviada y finalizada. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
                            + ", sector="
                            + requerimiento.getIdSector()
                            + ", idTipoPrestador="
                            + prestador.getIdTipoPrestador()
                            + ", modoTemporal="
                            + modoTemporal
                            + ", cantidadEmailsReales="
                            + (
                            emailsReales != null
                                    ? emailsReales.length
                                    : 0
                    )
                            + ", cantidadDestinatariosEfectivos="
                            + emailsDestino.length
                            + ", estadoEnvio=ENVIADO"
                            + ", ordenesMedicasAdjuntas="
                            + (
                            ordenesMedicasAdjuntas != null
                                    ? ordenesMedicasAdjuntas.size()
                                    : 0
                    )
            );
        }

        return copiaCotizacionEnviada;
    }

    protected RequerimientoCompraPedidoCotizacion
    registrarPedidoCotizacionActual(
            int idRequerimiento,
            int idPrestador,
            byte[] contenido,
            String nombreOriginal,
            String usuario,
            ServiceContext serviceContext)
            throws Exception {

        if (idRequerimiento <= 0) {

            throw new Exception(
                    "Debe informar el requerimiento "
                            + "del pedido de cotización."
            );
        }

        if (idPrestador <= 0) {

            throw new Exception(
                    "Debe informar el prestador "
                            + "del pedido de cotización."
            );
        }

        if (contenido == null
                || contenido.length == 0) {

            throw new Exception(
                    "El pedido de cotización generado "
                            + "no contiene datos."
            );
        }

        if (WebKeysCompras.isEmpty(
                nombreOriginal
        )) {

            throw new Exception(
                    "El pedido de cotización generado "
                            + "no posee un nombre válido."
            );
        }

        DocumentoLibraryComprasHelper
                .validarContextoDocumentLibrary(
                        serviceContext
                );

        DocumentoComprasCreado documento =
                null;

        try {

            documento =
                    DocumentoLibraryComprasHelper
                            .crearPedidoCotizacion(
                                    idRequerimiento,
                                    idPrestador,
                                    contenido,
                                    nombreOriginal,
                                    serviceContext
                            );

            if (documento == null
                    || documento.getGroupId() <= 0L
                    || documento.getFolderId() <= 0L
                    || documento.getFileEntryId() <= 0L
                    || WebKeysCompras.isEmpty(
                    documento.getUuid()
            )
                    || WebKeysCompras.isEmpty(
                    documento.getNombrePersistido()
            )
                    || WebKeysCompras.isEmpty(
                    documento.getTitulo()
            )) {

                throw new Exception(
                        "Document Library no devolvió una identidad "
                                + "válida para el pedido de cotización."
                );
            }

            RequerimientoCompraPedidoCotizacion asociacion =
                    new RequerimientoCompraPedidoCotizacion();

            asociacion.setIdRequerimiento(
                    Integer.valueOf(
                            idRequerimiento
                    )
            );

            asociacion.setIdPrestador(
                    Integer.valueOf(
                            idPrestador
                    )
            );

            asociacion.setDlGroupId(
                    Long.valueOf(
                            documento.getGroupId()
                    )
            );

            asociacion.setDlFolderId(
                    Long.valueOf(
                            documento.getFolderId()
                    )
            );

            asociacion.setDlFileEntryId(
                    Long.valueOf(
                            documento.getFileEntryId()
                    )
            );

            asociacion.setDlFileUuid(
                    documento.getUuid()
            );

            asociacion.setNombreOriginal(
                    nombreOriginal
            );

            asociacion.setNombrePersistido(
                    documento.getNombrePersistido()
            );

            asociacion.setTitulo(
                    documento.getTitulo()
            );

            int intento =
                    NotificarCotizacionPrestadorServiceUtil
                            .registrarPedidoCotizacionDocumento(
                                    asociacion,
                                    normalizarUsuario(
                                            usuario
                                    )
                            );

            if (intento <= 0) {

                throw new Exception(
                        "No se obtuvo el intento asociado "
                                + "al pedido de cotización."
                );
            }

            asociacion.setIntento(
                    Integer.valueOf(
                            intento
                    )
            );

            return asociacion;

        } catch (Exception errorRegistro) {

            if (errorRegistro instanceof OperacionNotificacionAmbiguaException) {
                _log.error("Resultado de asociacion del pedido no confirmado; se conserva el documento.", errorRegistro);
                throw errorRegistro;
            }
            if (documento != null) {

                try {

                    DocumentoLibraryComprasHelper
                            .eliminarDocumentoCreado(
                                    documento
                            );

                } catch (Exception cleanupError) {

                    _log.error(
                            "No se pudo compensar el pedido "
                                    + "de cotización creado "
                                    + "antes de fallar su asociación. "
                                    + "idRequerimiento="
                                    + idRequerimiento
                                    + ", idPrestador="
                                    + idPrestador
                                    + ", fileEntryId="
                                    + documento.getFileEntryId(),
                            cleanupError
                    );
                }
            }

            throw errorRegistro;
        }
    }

    private boolean esFarmaciaDestinataria(PrestadorCotizacion prestador) {
        String tipo = WebKeysCompras.normalizarClaveTexto(
                prestador != null ? prestador.getTipoPrestadorVisible() : "");
        return "FARMACIA".equals(tipo) || "FARMACIAS".equals(tipo);
    }

    protected byte[] generarPedidoPresupuestoFarmaciaPdf(
            RequerimientoCompra requerimiento,
            byte[] pdfNormal) throws Exception {

        if (requerimiento == null || !"FARMACIA".equals(
                WebKeysCompras.normalizarSectorCompra(
                        requerimiento.getSectorDescripcion()))) {
            return pdfNormal;
        }

        Set<Integer> idsMedicamentos = new HashSet<Integer>();
        List<RequerimientoCompraDetalle> detalles = requerimiento.getDetalles();
        for (int i = 0; detalles != null && i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle = detalles.get(i);
            if (detalle != null && detalle.getIdInt() > 0
                    && "MEDICAMENTOS".equals(WebKeysCompras.normalizarClaveTexto(
                            detalle.getTipoPrestacionDescripcion()))) {
                idsMedicamentos.add(detalle.getId());
            }
        }
        if (idsMedicamentos.isEmpty()) {
            return pdfNormal;
        }

        List<Map<String, Object>> filas = getFilasPedidoPresupuestoPdf(
                requerimiento.getIdRequerimientoCompra());
        if (filas == null || filas.isEmpty()) {
            throw new Exception("No se pudieron cargar los datos del pedido de presupuesto.");
        }

        // Consultar la droga del medicamento que efectivamente aparece en el PDF.
        List<RequerimientoCompraDetalle> medicamentosReporte =
                new ArrayList<RequerimientoCompraDetalle>();
        Map<Integer, Integer> troquelPorDetalle = new HashMap<Integer, Integer>();
        for (int i = 0; i < filas.size(); i++) {
            Map<String, Object> fila = filas.get(i);
            Integer idDetalle = (Integer) fila.get("detalle_id");
            if (!idsMedicamentos.contains(idDetalle)) {
                continue;
            }
            RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();
            detalle.setId(idDetalle);
            detalle.setTipoItem((String) fila.get("tipo_item"));
            detalle.setTroquel((Integer) fila.get("troquel"));
            detalle.setIdTipoNomenclador((Integer) fila.get("id_tipo_nomenclador"));
            detalle.setCodigoNomenclador((String) fila.get("codigo_nomenclador"));

            int troquel = 0;
            if (detalle.esMedicamento()) {
                troquel = detalle.getTroquelInt();
            } else if (detalle.esNomenclador()
                    && detalle.getIdTipoNomencladorInt()
                    == ConfiguracionComprasHelper.getTipoNomencladorFarmacia()) {
                String codigo = detalle.getCodigoNomencladorVisible().trim();
                if (codigo.matches("^[0-9]+$")) {
                    troquel = GetterUtil.getInteger(codigo, 0);
                }
            }
            if (troquel > 0) {
                medicamentosReporte.add(detalle);
                troquelPorDetalle.put(idDetalle, Integer.valueOf(troquel));
            }
        }
        if (medicamentosReporte.isEmpty()) {
            return pdfNormal;
        }

        Map<Integer, String> drogas = obtenerDrogasDetalleFarmacia(medicamentosReporte);
        if (drogas == null) {
            throw new Exception("No se pudo consultar la droga de los medicamentos.");
        }
        List<Map<String, Object>> filasFarmacia =
                new ArrayList<Map<String, Object>>();
        boolean hayReemplazo = false;
        for (int i = 0; i < filas.size(); i++) {
            Map<String, Object> fila = new HashMap<String, Object>(filas.get(i));
            Integer troquel = troquelPorDetalle.get(fila.get("detalle_id"));
            String droga = troquel != null ? drogas.get(troquel) : null;
            if (!WebKeysCompras.isEmpty(droga)) {
                fila.put("descripcion_item", droga.trim());
                hayReemplazo = true;
            }
            filasFarmacia.add(fila);
        }
        return hayReemplazo
                ? generarPedidoPresupuestoPdf(
                        requerimiento.getIdRequerimientoCompra(), filasFarmacia)
                : pdfNormal;
    }

    protected List<Map<String, Object>> getFilasPedidoPresupuestoPdf(
            int idRequerimientoCompra) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil
                .getFilasPedidoPresupuestoPdf(idRequerimientoCompra);
    }

    protected Map<Integer, String> obtenerDrogasDetalleFarmacia(
            List<RequerimientoCompraDetalle> detalles) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil
                .obtenerDrogasDetalleFarmacia(detalles);
    }

    protected byte[] generarPedidoPresupuestoPdf(
            int idRequerimientoCompra,
            List<Map<String, Object>> filas) throws Exception {
        return new PdfServlet().crearRequerimientoCompraComoAdjunto(
                idRequerimientoCompra, filas);
    }

    protected byte[] generarPedidoPresupuestoPdf(
            int idRequerimientoCompra)
            throws Exception {

        return new PdfServlet()
                .crearRequerimientoCompraComoAdjunto(
                        idRequerimientoCompra
                );
    }

    protected List<OrdenMedicaAdjunta>
    recuperarOrdenesMedicasAdjuntas(
            int idRequerimientoCompra,
            long companyId)
            throws Exception {

        List<OrdenMedicaAdjunta> resultado =
                new ArrayList<OrdenMedicaAdjunta>();

        List<RequerimientoCompraPresupuesto> ordenesMedicas =
                getOrdenesMedicas(
                        idRequerimientoCompra
                );

        if (ordenesMedicas == null
                || ordenesMedicas.isEmpty()) {

            if (_log.isDebugEnabled()) {

                _log.debug(
                        "El requerimiento no posee un adjunto activo; "
                                + "se conserva el envío histórico con PDF. "
                                + "idRequerimiento="
                                + idRequerimientoCompra
                );
            }

            return resultado;
        }

        Set<Long> fileEntryIds =
                new HashSet<Long>();

        for (int i = 0;
             i < ordenesMedicas.size();
             i++) {

            RequerimientoCompraPresupuesto ordenMedica =
                    ordenesMedicas.get(i);

            DocumentoLibraryComprasHelper
                    .validarRelacionOrdenMedica(
                            ordenMedica,
                            idRequerimientoCompra
                    );

            long fileEntryId =
                    ordenMedica
                            .getDlFileEntryId()
                            .longValue();

            if (!fileEntryIds.add(
                    Long.valueOf(
                            fileEntryId
                    )
            )) {

                throw new Exception(
                        "El requerimiento contiene más de un "
                                + "adjunto activo asociado al mismo "
                                + "documento de Document Library."
                );
            }

            DLFileEntry entry =
                    getFileEntryOrdenMedica(
                            fileEntryId
                    );

            DocumentoLibraryComprasHelper
                    .validarIdentidadOrdenMedicaPersistida(
                            ordenMedica,
                            entry,
                            companyId
                    );

            DocumentoLibraryComprasHelper
                    .OrdenMedicaContenido documento =
                    DocumentoLibraryComprasHelper
                            .leerOrdenMedicaValidada(
                                    entry,
                                    ordenMedica
                                            .getNombreOriginal()
                            );

            resultado.add(
                    crearOrdenMedicaAdjunta(
                            documento.getContenido(),
                            documento.getNombreOriginal(),
                            documento.getContentType()
                    )
            );
        }

        return resultado;
    }

    protected OrdenMedicaAdjunta crearOrdenMedicaAdjunta(
            byte[] contenido,
            String nombreOriginal,
            String contentType) {

        return new OrdenMedicaAdjunta(
                contenido,
                nombreOriginal,
                contentType
        );
    }

    protected List<RequerimientoCompraPresupuesto>
    getOrdenesMedicas(
            int idRequerimientoCompra)
            throws Exception {

        return BusquedaRequerimientoCompraServiceUtil
                .listarOrdenesMedicas(
                        idRequerimientoCompra
                );
    }

    protected DLFileEntry getFileEntryOrdenMedica(
            long fileEntryId)
            throws Exception {

        return DLFileEntryLocalServiceUtil
                .getDLFileEntry(
                        fileEntryId
                );
    }

    protected RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra)
            throws Exception {

        return BusquedaRequerimientoCompraServiceUtil
                .getRequerimientoCompra(
                        idRequerimientoCompra
                );
    }

    protected List<PrestadorCotizacion>
    listarPrestadoresCandidatos(
            int idRequerimientoCompra)
            throws Exception {

        return NotificarCotizacionPrestadorServiceUtil
                .listarPrestadoresCandidatos(
                        idRequerimientoCompra
                );
    }

    private void cargarDiagnosticoCandidatosConControl(
            RequerimientoCompra requerimiento,
            NotificacionCotizacionResultado resultado) {

        try {

            cargarDiagnosticoCandidatos(
                    requerimiento,
                    resultado
            );

        } catch (Exception e) {

            _log.warn(
                    "No se pudo calcular el diagnóstico "
                            + "de prestadores candidatos. "
                            + "idRequerimiento="
                            + (
                            requerimiento != null
                                    ? requerimiento
                                    .getIdRequerimientoCompra()
                                    : 0
                    ),
                    e
            );
        }
    }

    protected void cargarDiagnosticoCandidatos(
            RequerimientoCompra requerimiento,
            NotificacionCotizacionResultado resultado)
            throws Exception {

        if (requerimiento == null
                || resultado == null) {

            return;
        }

        CotizacionPrestadorDiagnostico diagnostico =
                NotificarCotizacionPrestadorServiceUtil
                        .diagnosticarPrestadores(
                                requerimiento
                                        .getIdRequerimientoCompra()
                        );

        if (diagnostico != null) {

            resultado.setPrestadoresHabilitados(
                    diagnostico
                            .getPrestadoresHabilitados()
            );

            resultado.setPrestadoresCompatiblesSector(
                    diagnostico
                            .getPrestadoresCompatiblesSector()
            );

            resultado.setPrestadoresBloqueadosEstadoPrevio(
                    diagnostico
                            .getPrestadoresBloqueadosEstadoPrevio()
            );
        }

        if (_log.isDebugEnabled()) {

            _log.debug(
                    "Diagnóstico de prestadores candidatos. "
                            + "idRequerimiento="
                            + requerimiento
                            .getIdRequerimientoCompra()
                            + ", sector="
                            + requerimiento.getIdSector()
                            + ", candidatos="
                            + resultado.getTotalCandidatos()
                            + ", habilitados="
                            + resultado.getPrestadoresHabilitados()
                            + ", compatiblesRubro="
                            + resultado
                            .getPrestadoresCompatiblesSector()
                            + ", bloqueadosEstadoPrevio="
                            + resultado
                            .getPrestadoresBloqueadosEstadoPrevio()
            );
        }
    }

    protected ReservaCotizacionPrestador
    reservarCotizacionPrestador(
            int idRequerimientoCompra,
            int idPrestador,
            String usuario)
            throws Exception {

        return NotificarCotizacionPrestadorServiceUtil
                .reservarCotizacionPrestador(
                        idRequerimientoCompra,
                        idPrestador,
                        normalizarUsuario(
                                usuario
                        )
                );
    }

    protected FinalizacionCotizacionPrestador
    finalizarCotizacionPrestadorConDetalle(
            int idRequerimiento,
            int idPrestador,
            String estado,
            String error,
            String usuario)
            throws Exception {

        return NotificarCotizacionPrestadorServiceUtil
                .finalizarCotizacionPrestador(
                        idRequerimiento,
                        idPrestador,
                        estado,
                        error,
                        normalizarUsuario(
                                usuario
                        )
                );
    }

    private void enviarMail(
            long companyId,
            String[] emails,
            String asunto,
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            List<OrdenMedicaAdjunta> ordenesMedicas,
            boolean incluirCopiaCotizacion)
            throws Exception {

        List<CotizacionPrestadorMailHelper
                .AdjuntoOrdenMedica> adjuntos =
                new ArrayList<CotizacionPrestadorMailHelper
                        .AdjuntoOrdenMedica>();

        for (int i = 0;
             ordenesMedicas != null
                     && i < ordenesMedicas.size();
             i++) {

            OrdenMedicaAdjunta ordenMedica =
                    ordenesMedicas.get(i);

            if (ordenMedica == null) {

                throw new Exception(
                        "Se encontró un adjunto inválido."
                );
            }

            adjuntos.add(
                    new CotizacionPrestadorMailHelper
                            .AdjuntoOrdenMedica(
                            ordenMedica.getContenido(),
                            ordenMedica.getNombreOriginal(),
                            ordenMedica.getContentType()
                    )
            );
        }

        mailHelper.enviar(
                emails,
                incluirCopiaCotizacion
                        ? resolverEmailsCopiaCotizacion()
                        : new String[0],
                asunto,
                cuerpo,
                pedidoPresupuestoPdf,
                nombrePedidoPresupuestoPdf,
                adjuntos
        );
    }

    private boolean finalizarConControl(
            int idRequerimiento,
            int idPrestador,
            String estado,
            String error,
            String usuario) {

        try {

            FinalizacionCotizacionPrestador finalizacion =
                    finalizarCotizacionPrestadorConDetalle(
                            idRequerimiento,
                            idPrestador,
                            estado,
                            error,
                            usuario
                    );

            if (finalizacion == null) {

                _log.error(
                        "La finalización no devolvió resultado. "
                                + "estadoSolicitado="
                                + estado
                                + ", idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                );

                return false;
            }

            if (!finalizacion.isActualizado()) {

                _log.error(
                        "No se pudo persistir el estado final "
                                + "de la cotización. "
                                + "estadoSolicitado="
                                + estado
                                + ", estadoAnterior="
                                + finalizacion.getEstadoAnterior()
                                + ", estadoActual="
                                + finalizacion.getEstadoActual()
                                + ", motivo="
                                + finalizacion.getMotivo()
                                + ", idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                );

                return false;
            }

            return true;

        } catch (Exception persistenciaError) {

            _log.error(
                    "Error persistiendo el estado final "
                            + "de la cotización. "
                            + "estadoSolicitado="
                            + estado
                            + ", idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    persistenciaError
            );

            return false;
        }
    }

    private void registrarResultado(
            NotificacionCotizacionResultado resultado,
            PrestadorCotizacion prestador,
            String emailReal,
            String emailDestino,
            String tipoResultado,
            String etapa,
            String motivo,
            boolean emailRealInvalidoAdvertido) {

        if (resultado == null) {
            return;
        }

        NotificacionCotizacionDetalle detalle =
                new NotificacionCotizacionDetalle();

        if (prestador != null) {

            detalle.setIdPrestador(
                    prestador.getIdPrestador()
            );

            detalle.setPrestador(
                    prestador.getDescripcion()
            );
        }

        detalle.setEmailReal(
                normalizarEmail(
                        emailReal
                )
        );

        detalle.setEmailDestino(
                normalizarEmail(
                        emailDestino
                )
        );

        detalle.setResultado(
                tipoResultado
        );

        detalle.setEtapa(
                etapa
        );

        detalle.setMotivo(
                truncar(
                        motivo,
                        1000
                )
        );

        detalle.setEmailRealInvalidoAdvertido(
                emailRealInvalidoAdvertido
        );

        resultado.agregarDetalle(
                detalle
        );
    }

    private String construirMotivoReservaNoOtorgada(
            ReservaCotizacionPrestador reserva) {

        if (reserva == null) {

            return "No se obtuvo la reserva de procesamiento.";
        }

        if (ConfiguracionComprasHelper.getEstadoEnvio("ENVIADO").equals(
                reserva.getEstadoEnvio()
        )) {

            return "El prestador ya habia sido notificado. "
                    + "No se realizo un reenvio.";
        }

        if (ConfiguracionComprasHelper.getEstadoEnvio("PROCESANDO").equals(
                reserva.getEstadoEnvio()
        )) {

            return "El prestador ya estaba siendo procesado "
                    + "por otra ejecución.";
        }

        if (!WebKeysCompras.isEmpty(
                reserva.getMotivoDescripcion()
        )) {

            return truncar(
                    reserva.getMotivoDescripcion(),
                    1000
            );
        }

        return "No se obtuvo la reserva de procesamiento.";
    }

    protected boolean redireccionQaHabilitada() {

        String valor =
                TraeListasServiceUtil.getSystemConfig(
                        CONFIG_REQUERIMIENTO_REDIRECCION_QA_HABILITADA
                );

        if (valor == null) {

            return false;
        }

        return Boolean.parseBoolean(
                valor.trim()
        );
    }

    private String[] resolverEmailsCopiaCotizacion() {

        String configuracion =
                TraeListasServiceUtil.getSystemConfig(
                        CONFIG_REQUERIMIENTO_EMAIL_CC
                );

        String[] emails =
                resolverEmails(
                        configuracion
                );

        if (_log.isDebugEnabled()) {

            _log.debug(
                    "Destinatarios BCC de cotización resueltos. "
                            + "cantidadBcc="
                            + emails.length
                            + ", modoTemporal="
                            + redireccionQaHabilitada()
            );
        }

        return emails;
    }

    private String construirAsunto(
            RequerimientoCompra requerimiento) {

        return "Solicitud de cotización #"
                + requerimiento
                .getIdRequerimientoCompra()
                + " - " + requerimiento.getAfiliadoNombre()
                + " " + requerimiento.getAfiliadoApellido();
    }

    private String construirCuerpo(
            RequerimientoCompra requerimiento,
            PrestadorCotizacion prestador) {

        String emailRespuesta = resolverEmailRespuestaCotizacion();
        StringBuilder sb = new StringBuilder();

        sb.append("Estimado prestador");

        if (!WebKeysCompras.isEmpty(prestador.getDescripcion())) {
            sb.append(" ");
            sb.append(prestador.getDescripcionVisible());
        }

        sb.append(",\n\n");

        sb.append(
                "OSPIM solicita cotización para el "
                        + "pedido de presupuesto adjunto, "
                        + "cotizar según orden médica."
        );

        if (!WebKeysCompras.isEmpty(requerimiento.getObservaciones())) {
            sb.append("\n\nDetalle / observaciones:\n");
            sb.append(requerimiento.getObservacionesVisible());
        }

        sb.append(
                "\n\nPor favor responder este correo "
                        + "informando disponibilidad, "
                        + "plazo de entrega "
                        + "e importe de cotización"
        );

        if (!WebKeysCompras.isEmpty(emailRespuesta)) {
            sb.append(" a ");
            sb.append(emailRespuesta);
        }

        sb.append(".");

        sb.append(
                "\n\nLos presupuestos se deben presentar en formato .PDF, "
                        + "fijando como plazo límite de entrega "
                        + "las próximas 48 horas, "
                        + "con horario tope de recepción a las 18:00 hs."
        );

        return sb.toString();
    }

    private void validarParametros(
            int idRequerimientoCompra,
            long companyId)
            throws Exception {

        if (idRequerimientoCompra <= 0) {

            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        if (companyId <= 0) {

            throw new Exception(
                    "No se pudo determinar la empresa del portal."
            );
        }
    }

    private void validarRequerimiento(
            RequerimientoCompra requerimiento)
            throws Exception {

        if (requerimiento == null) {

            throw new Exception(
                    "No se encontró el requerimiento de compra."
            );
        }

        if (requerimiento.getIdSector() == null
                || requerimiento
                .getIdSector()
                .intValue() <= 0) {

            throw new Exception(
                    "El requerimiento no tiene sector informado."
            );
        }

        if (requerimiento.getEstado()
                != ConfiguracionComprasHelper.getEstadoPendiente()
                && requerimiento.getEstado()
                != ConfiguracionComprasHelper.getEstadoACotizar()) {

            throw new Exception(
                    "El requerimiento no se encuentra "
                            + "en estado PENDIENTE o A COTIZAR."
            );
        }
    }

    private boolean esEmailValido(
            String email) {

        String emailNormalizado =
                normalizarEmail(
                        email
                );

        return emailNormalizado != null
                && EMAIL_PATTERN
                .matcher(
                        emailNormalizado
                )
                .matches();
    }

    private String normalizarEmail(
            String email) {

        if (email == null) {

            return null;
        }

        String resultado =
                email.trim();

        return resultado.length() > 0
                ? resultado
                : null;
    }

    private String construirDetalleError(
            Exception e) {

        if (e == null) {

            return "Error no informado.";
        }

        String mensaje =
                e.getMessage();

        if (mensaje == null
                || mensaje.trim().length() == 0) {

            mensaje =
                    e.getClass()
                            .getName();

        } else {

            mensaje =
                    e.getClass()
                            .getName()
                            + ": "
                            + mensaje.trim();
        }

        mensaje =
                SENSITIVE_DETAIL_PATTERN
                        .matcher(
                                mensaje
                        )
                        .replaceAll(
                                "$1=<omitido>"
                        );

        mensaje =
                mensaje
                        .replace(
                                '\r',
                                ' '
                        )
                        .replace(
                                '\n',
                                ' '
                        )
                        .replace(
                                '\t',
                                ' '
                        );

        return truncar(
                mensaje,
                4000
        );
    }

    private String truncar(
            String value,
            int maxLength) {

        if (value == null) {

            return null;
        }

        if (value.length()
                <= maxLength) {

            return value;
        }

        return value.substring(
                0,
                maxLength
        );
    }

    private String normalizarUsuario(String usuario) {
        String valor=usuario==null?null:btrimEspacios(usuario);
        if(valor==null || valor.length()==0)valor=ConfiguracionComprasHelper.leerObligatoria("COMPRAS_USUARIO_DEFECTO_NOTIFICACION");
        return truncarErrorNotificacion(valor,100);
    }

    protected static final class OrdenMedicaAdjunta {

        private final byte[] contenido;
        private final String nombreOriginal;
        private final String contentType;

        protected OrdenMedicaAdjunta(
                byte[] contenido,
                String nombreOriginal,
                String contentType) {

            this.contenido =
                    contenido;

            this.nombreOriginal =
                    nombreOriginal;

            this.contentType =
                    contentType;
        }

        protected byte[] getContenido() {

            return contenido;
        }

        protected String getNombreOriginal() {

            return nombreOriginal;
        }

        protected String getContentType() {

            return contentType;
        }
    }

    private String[] resolverEmailsPrestador(
            String emailsConcatenados) {

        return resolverEmails(
                emailsConcatenados
        );
    }

    private String[] resolverEmailsDestino(
            String[] emailsReales,
            boolean modoTemporal) {

        if (modoTemporal) {

            String configuracionQa =
                    TraeListasServiceUtil
                            .getSystemConfig(
                                    CONFIG_REQUERIMIENTO_EMAIL_QA
                            );

            return resolverEmails(
                    configuracionQa
            );
        }

        return resolverEmails(
                emailsReales
        );
    }

    private String unirEmails(
            String[] emails) {

        String[] emailsValidos =
                resolverEmails(
                        emails
                );

        if (emailsValidos.length == 0) {

            return null;
        }

        StringBuilder sb =
                new StringBuilder();

        for (int i = 0;
             i < emailsValidos.length;
             i++) {

            if (i > 0) {

                sb.append(
                        ";"
                );
            }

            sb.append(
                    emailsValidos[i]
            );
        }

        return sb.toString();
    }

    private String resolverEmailRespuestaCotizacion() {

        String configuracion =
                TraeListasServiceUtil
                        .getSystemConfig(
                                CONFIG_REQUERIMIENTO_EMAIL_RESPUESTA
                        );

        String[] emails =
                resolverEmails(
                        configuracion
                );

        if (emails.length == 0) {

            _log.warn(
                    "No hay un email válido configurado en "
                            + CONFIG_REQUERIMIENTO_EMAIL_RESPUESTA
                            + ". Se continuará el envío "
                            + "sin informar un email de respuesta."
            );

            return null;
        }

        if (emails.length > 1) {

            _log.warn(
                    "Existe más de un email configurado en "
                            + CONFIG_REQUERIMIENTO_EMAIL_RESPUESTA
                            + ". Se utilizará únicamente el primero."
            );
        }

        return emails[0];
    }

    private String[] resolverEmails(
            String emailsConcatenados) {

        String normalizado =
                normalizarEmail(
                        emailsConcatenados
                );

        if (normalizado == null) {

            return new String[0];
        }

        return resolverEmails(
                normalizado.split(
                        ";",
                        -1
                )
        );
    }

    private String[] resolverEmails(
            String[] candidatos) {

        if (candidatos == null
                || candidatos.length == 0) {

            return new String[0];
        }

        List<String> resultado =
                new ArrayList<String>();

        Set<String> encontrados =
                new HashSet<String>();

        for (int i = 0;
             i < candidatos.length;
             i++) {

            String email =
                    normalizarEmail(
                            candidatos[i]
                    );

            /*
             * Tolera:
             *
             * correo@dominio.com;
             * correo@dominio.com;;
             * ;correo@dominio.com
             * espacios entre separadores
             */
            if (email == null) {

                continue;
            }

            /*
             * Un email inválido dentro de una lista no debe
             * inutilizar los demás destinatarios válidos.
             */
            if (!esEmailValido(
                    email
            )) {

                continue;
            }

            String clave =
                    email.toLowerCase();

            if (encontrados.add(
                    clave
            )) {

                resultado.add(
                        email
                );
            }
        }

        return resultado.toArray(
                new String[
                        resultado.size()
                        ]
        );
    }
}
