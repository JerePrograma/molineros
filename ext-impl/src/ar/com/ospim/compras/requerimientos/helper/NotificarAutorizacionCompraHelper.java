package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativaDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraComparativaServiceUtil;
import com.liferay.documentlibrary.NoSuchFileException;
import com.liferay.portlet.documentlibrary.NoSuchFileEntryException;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.permission.DLFolderPermission;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPedidoCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.helper.CotizacionPrestadorMailHelper.AdjuntoOrdenMedica;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.servlets.PdfServlet;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.permission.DLFileEntryPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.mail.internet.InternetAddress;

/** Correo posterior al guardado, como EditarOrdenesPagoOspimAction de Liquidaciones. */
public class NotificarAutorizacionCompraHelper {
    private static final Log _log = LogFactoryUtil.getLog(NotificarAutorizacionCompraHelper.class);

    public static final String CONFIG_EMAIL_AUTORIZACION = "COMPRAS_EMAIL_AUTORIZACION";

    private final CotizacionPrestadorMailHelper mailHelper = new CotizacionPrestadorMailHelper();

    private String advertenciaNotificacion;
    private boolean correoConfirmado;
    private boolean pedidoActualGenerado;
    private boolean comparativaActualGenerada;
    private RequerimientoCompra requerimientoPreparado;
    private String huellaPreparada;
    private int rpPreparado;
    private int adjudicadoPreparado;
    private ThemeDisplay contextoPreparado;
    private final Map<Long, String> identidadesPreparadas = new LinkedHashMap<Long, String>();

    public String getAdvertenciaNotificacion() { return advertenciaNotificacion; }
    public boolean isCorreoConfirmado() { return correoConfirmado; }

    /** La transicion es autoritativa; el aviso de correo no puede impedirla. */
    public void enviarAAutorizar(int id, String usuario, ThemeDisplay contexto) throws Exception {
        limpiarResultado();
        cambiarEstado(id, usuario);
        notificarConResultado(id, usuario, contexto);
    }

    /** Recuperacion explicita del aviso; no repite ni modifica la transicion. */
    public void reintentarNotificacion(int id, String usuario, ThemeDisplay contexto) throws Exception {
        limpiarResultado();
        RequerimientoCompra r = obtenerRequerimiento(id);
        if (r == null || !r.isActivo() || r.getIdRequerimientoCompra() != id
                || r.getEstado() != ConfiguracionComprasHelper.getEstadoParaAutorizar()
                || r.esSectorSinCotizacionPrestador()) {
            throw new Exception("Solo se puede recuperar el aviso de un requerimiento activo PARA AUTORIZAR con RP.");
        }
        notificarConResultado(id, usuario, contexto);
    }

    private void limpiarResultado() {
        advertenciaNotificacion = null;
        correoConfirmado = false;
        pedidoActualGenerado = false;
        comparativaActualGenerada = false;
        requerimientoPreparado = null;
        huellaPreparada = null;
        rpPreparado = 0;
        adjudicadoPreparado = 0;
        contextoPreparado = null;
        identidadesPreparadas.clear();
    }

    private void notificarConResultado(int id, String usuario, ThemeDisplay contexto) {
        try {
            notificarAutorizacion(id, usuario, contexto);
        } catch (Exception e) {
            if (advertenciaNotificacion == null) {
                String categoria = "configuracion o datos";
                Throwable causa = e;
                for (int i = 0; causa != null && i < 8; i++) {
                    if (causa instanceof com.liferay.portal.security.auth.PrincipalException) {
                        categoria = "permisos";
                        break;
                    }
                    if (causa instanceof java.sql.SQLException) {
                        categoria = "persistencia";
                    } else if (causa instanceof com.liferay.portal.SystemException
                            && "configuracion o datos".equals(categoria)) {
                        categoria = "infraestructura";
                    }
                    Throwable siguiente = causa.getCause();
                    if (siguiente == causa) break;
                    causa = siguiente;
                }
                advertenciaNotificacion = "No se envio el aviso en esta solicitud por un error de "
                        + categoria + ". Actualice la pantalla y revise el registro antes de reintentar.";
            }
            _log.error("No se pudo confirmar el correo de autorizacion. Consulte el estado vigente. id=" + id, e);
        }
    }

    private void notificarAutorizacion(int id, String usuario, ThemeDisplay contexto) throws Exception {
        RequerimientoCompra requerimiento = obtenerRequerimiento(id);
        if (requerimiento == null || !requerimiento.isActivo()
                || requerimiento.getIdRequerimientoCompra() != id) {
            throw falloPreparacion("El requerimiento ya no esta disponible.", null);
        }
        // RRHH/Sistemas no poseen RP: conservar su circuito vigente.
        if (requerimiento.esSectorSinCotizacionPrestador()) return;
        if (id <= 0 || contexto == null || contexto.getCompanyId() <= 0L
                || contexto.getScopeGroupId() <= 0L || contexto.getUser() == null
                || contexto.getPermissionChecker() == null
                || contexto.getPermissionChecker().getUserId() != contexto.getUser().getUserId()) {
            throw falloPreparacion("No se pudo determinar el contexto del envio a autorizar.", null);
        }
        // La huella precede a la relectura: la reserva detecta tambien cambios durante la preparacion.
        huellaPreparada = obtenerHuellaPreparacion(id);
        requerimiento = obtenerRequerimiento(id);
        RequerimientoCompraReclamoPrestacional relacion = obtenerRelacion(id);
        validarDatosAutorizacion(requerimiento, relacion);
        requerimientoPreparado = requerimiento;
        rpPreparado = relacion.getIdReclamoPrestacionalInt();
        adjudicadoPreparado = requerimiento.getIdPrestadorAdjudicadoInt();
        contextoPreparado = contexto;
        String[] destinos = resolverDestinatarios();

        // Primero los originales: un faltante externo nunca se sustituye ni se oculta regenerando.
        List<AdjuntoOrdenMedica> presupuestos = recuperarPresupuestos(id, contexto);
        List<AdjuntoOrdenMedica> ordenes = recuperarOrdenes(id, contexto);
        if (presupuestos == null || presupuestos.isEmpty() || ordenes == null || ordenes.isEmpty()) {
            throw falloPreparacion("Faltan los originales obligatorios de presupuestos u ordenes medicas.", null);
        }
        AdjuntoOrdenMedica pedido = recuperarPedido(id, contexto);
        List<AdjuntoOrdenMedica> adjuntos = recuperarAdjuntos(id, relacion, contexto);
        if (pedido == null || adjuntos == null || adjuntos.size() < 2) {
            throw falloPreparacion("No se recuperaron la comparativa y la ficha del RP " + rpPreparado + ".", null);
        }
        adjuntos.addAll(ordenes);
        adjuntos.addAll(presupuestos);
        String nombre = (requerimiento.getAfiliadoApellidoVisible() + " "
                + requerimiento.getAfiliadoNombreVisible()).trim();
        if (nombre.length() == 0) nombre = requerimiento.getAfiliadoNombreApellidoVisible();
        String asunto = "RP:" + rpPreparado + "-" + textoAsunto(nombre)
                + "-DU:" + textoAsunto(requerimiento.getAfiliadoDocumentoNroVisible())
                + "-" + textoAsunto(requerimiento.getAfiliadoLocalidadVisible())
                + "-" + textoAsunto(requerimiento.getAfiliadoProvinciaVisible())
                + "-ID:" + id + "-COMPARATIVA";
        String cuerpo = "Se env\u00eda a autorizar el requerimiento de compra Nro. " + id
                + ", vinculado al Reclamo Prestacional Nro. " + rpPreparado
                + ".\n\nSe adjuntan la comparativa, la ficha imprimible del RP, las ordenes medicas"
                + ", el pedido de presupuesto y todos los presupuestos cargados.";
        if (pedidoActualGenerado) {
            cuerpo += "\n\nEl pedido adjunto es una COPIA ACTUAL generada para esta autorizacion."
                    + " Refleja los datos vigentes y no acredita un envio historico al prestador.";
        }
        if (comparativaActualGenerada) {
            cuerpo += "\n\nLa comparativa adjunta es una COPIA ACTUAL generada para esta autorizacion."
                    + " No reemplaza la comparativa historica ni el adjunto del RP.";
        }
        mailHelper.construirMultipart(cuerpo, pedido.getContenido(), pedido.getNombre(), adjuntos);
        validarTamanoCorreo(pedido, adjuntos);
        validarConfiguracionCorreo();
        String token = reservarNotificacion(id, usuario);
        try {
            enviarCorreo(destinos, asunto, cuerpo, pedido, adjuntos);
        } catch (Exception e) {
            String resultado = "INCIERTO";
            if (e instanceof CotizacionPrestadorMailHelper.EnvioParcialException) {
                resultado = "PARCIAL";
                advertenciaNotificacion = "El servidor SMTP acepto el aviso para parte de los destinatarios."
                        + " El reenvio queda bloqueado; verifique los destinatarios aceptados antes de continuar.";
            } else if (CotizacionPrestadorMailHelper.esRechazoSmtpConfirmado(e)) {
                resultado = "RECHAZADO";
                advertenciaNotificacion = "El servidor SMTP rechazo el aviso sin aceptarlo."
                        + " El reenvio queda bloqueado hasta revisar el registro y la causa del rechazo.";
            } else {
                advertenciaNotificacion = "No se pudo confirmar el resultado SMTP del aviso."
                        + " El reenvio queda bloqueado; verifique el envio antes de cualquier nueva solicitud.";
            }
            try {
                finalizarNotificacion(id, token, "INCIERTO", resultado);
            } catch (Exception registro) {
                _log.error("No se pudo registrar el resultado SMTP. Reserva bloqueada. id=" + id, registro);
            }
            throw e;
        }
        correoConfirmado = true;
        _log.info("Correo de autorizacion aceptado por el servidor SMTP. id=" + id);
        try {
            finalizarNotificacion(id, token, "ENVIADO", "ACEPTADO");
        } catch (Exception e) {
            advertenciaNotificacion = "El servidor SMTP acepto el aviso, pero no se pudo confirmar su registro."
                    + " No lo reenvie; verifique el registro y el correo.";
            throw e;
        }
    }

    private String textoAsunto(String valor) {
        String texto = valor != null ? valor.replaceAll("\\p{Cntrl}", " ").trim() : "";
        return texto.length() > 0 ? texto.toUpperCase(Locale.ENGLISH) : "S/D";
    }

    private String textoConfiguracion(String clave) {
        String valor = getConfiguracion(clave);
        return valor != null ? valor.trim() : "";
    }

    private void validarTamanoCorreo(AdjuntoOrdenMedica pedido,
            List<AdjuntoOrdenMedica> adjuntos) throws Exception {
        String limiteConfigurado = textoConfiguracion("GMAIL_SIZE_VALIDO_ADJUNTOS");
        if (limiteConfigurado.length() == 0) return;
        long limite;
        try {
            limite = new java.math.BigDecimal(limiteConfigurado).longValueExact();
            if (limite <= 0L) throw new NumberFormatException();
        } catch (Exception e) {
            throw new Exception("El limite GMAIL_SIZE_VALIDO_ADJUNTOS de SystemConfig no es valido.");
        }
        long total = pedido.getContenido().length;
        for (int i = 0; i < adjuntos.size(); i++) total += adjuntos.get(i).getContenido().length;
        if (total >= limite) {
            throw new Exception("Los adjuntos del RP superan el limite permitido para un correo.");
        }
    }
    private String[] resolverDestinatarios() throws Exception {
        String clave = CONFIG_EMAIL_AUTORIZACION;
        if (Boolean.parseBoolean(textoConfiguracion("REQUERIMIENTO_REDIRECCION_QA_HABILITADA"))) {
            clave = "REQUERIMIENTO_EMAIL_QA";
        }
        String valor = WebKeysCompras.trimToNull(getConfiguracion(clave));
        if (valor == null || valor.indexOf('\r') >= 0 || valor.indexOf('\n') >= 0) {
            throw new Exception("Configure un destinatario valido en SystemConfig: " + clave + ".");
        }
        String[] partes = valor.split("[;,]", -1);
        LinkedHashMap<String, String> unicos = new LinkedHashMap<String, String>();
        for (int i = 0; i < partes.length; i++) {
            String email = partes[i].trim();
            if (email.length() == 0) continue;
            try {
                if (!email.matches("^[^@\\s<>;,]+@[^@\\s<>;,]+\\.[^@\\s<>;,]+$")) {
                    throw new Exception("Formato de destinatario invalido.");
                }
                new InternetAddress(email, true).validate();
            } catch (Exception e) {
                throw new Exception("Configure un destinatario valido en SystemConfig: " + clave + ".");
            }
            unicos.put(email.toLowerCase(Locale.ENGLISH), email);
        }
        if (unicos.isEmpty()) {
            throw new Exception("Configure un destinatario valido en SystemConfig: " + clave + ".");
        }
        return unicos.values().toArray(new String[unicos.size()]);
    }

    private Exception falloPreparacion(String mensaje, Exception causa) {
        advertenciaNotificacion = "No se envio el aviso. " + mensaje;
        return causa != null ? new Exception(mensaje, causa) : new Exception(mensaje);
    }

    protected String obtenerHuellaPreparacion(int id) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.getHuellaPreparacionAutorizacion(id);
    }

    protected List<Map<String, Object>> obtenerRelacionesRp(int id) throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx = null;
        try {
            tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            return tx.obtenerRelacionesRp(id);
        } finally {
            if (tx != null) tx.cerrar();
        }
    }

    protected void validarDatosAutorizacion(RequerimientoCompra r,
            RequerimientoCompraReclamoPrestacional relacion) throws Exception {
        if (r == null || !r.isActivo() || r.getEstado() != ConfiguracionComprasHelper.getEstadoParaAutorizar()
                || r.esSectorSinCotizacionPrestador()) {
            throw falloPreparacion("El requerimiento debe estar activo y PARA AUTORIZAR en el circuito con RP.", null);
        }
        if (relacion == null || !relacion.isVinculado() || relacion.getBajaFecha() != null
                || relacion.getIdRequerimientoCompra() != r.getIdRequerimientoCompra()) {
            throw falloPreparacion("Debe existir un vinculo activo con el Reclamo Prestacional.", null);
        }
        validarRequerimientoReconstruible(r);
        validarRpVigente(obtenerRelacionesRp(r.getIdRequerimientoCompra()), relacion.getIdReclamoPrestacionalInt());
    }

    private void validarRequerimientoReconstruible(RequerimientoCompra r) throws Exception {
        if (r == null || !r.isActivo() || r.getIdRequerimientoCompra() <= 0
                || r.getIdPrestadorAdjudicadoInt() <= 0 || r.tienePrestadoresAdjudicadosMixtos()
                || r.getDetalles() == null || r.getDetalles().isEmpty()) {
            throw falloPreparacion("El requerimiento no conserva una adjudicacion unica y prestaciones vigentes.", null);
        }
        int ganador = r.getIdPrestadorAdjudicadoInt();
        boolean cantidadPositiva = false;
        for (int i = 0; i < r.getDetalles().size(); i++) {
            RequerimientoCompraDetalle d = r.getDetalles().get(i);
            if (d == null || d.getCantidad() == null || d.getCantidad().intValue() < 0
                    || (d.getIdPrestadorInt() != ganador
                        && !(d.getIdPrestadorInt() == 0 && d.getCantidad().intValue() == 0
                            && d.getPrecioUnitarioEstimado() != null && d.getPrecioUnitarioEstimado().signum() == 0
                            && (d.getPrecioTotalEstimadoInformado() == null
                                || d.getPrecioTotalEstimadoInformado().signum() == 0)))
                    || WebKeysCompras.isEmpty(d.getDescripcionItemVisible())
                    || (d.getPrecioUnitarioEstimado() != null
                        && d.getPrecioUnitarioEstimado().signum() < 0)
                    || (d.getPrecioTotalEstimadoInformado() != null
                        && d.getPrecioTotalEstimadoInformado().signum() < 0)) {
                throw falloPreparacion("La prestacion " + (i + 1)
                        + " no conserva cantidad, descripcion, importes o adjudicacion coherentes.", null);
            }
            if (d.getCantidad().intValue() > 0) cantidadPositiva = true;
        }
        if (!cantidadPositiva) {
            throw falloPreparacion("El requerimiento no conserva ninguna prestacion adjudicada con cantidad positiva.", null);
        }
    }

    private void validarRpVigente(List<Map<String, Object>> filas, int rp) throws Exception {
        if (rp <= 0 || filas == null || filas.size() != 1) {
            throw falloPreparacion("No se pudo acreditar un unico RP vinculado y activo.", null);
        }
        Map<String, Object> fila = filas.get(0);
        if (fila == null || !WebKeysCompras.VINCULO_RECLAMO_VINCULADO.equals(fila.get("estado"))
                || !Integer.valueOf(rp).equals(fila.get("id"))
                || !Integer.valueOf(rp).equals(fila.get("rp_id"))
                || fila.get("baja") != null || fila.get("vinculo_baja") != null) {
            throw falloPreparacion("El RP " + rp + " vinculado no existe, esta dado de baja o su vinculo es inconsistente.", null);
        }
    }

    protected void validarPreparacionVigente(RequerimientoCompra r,
            List<Map<String, Object>> relaciones, String huella) throws Exception {
        if (huellaPreparada == null || huella == null || !huellaPreparada.equals(huella)) {
            throw falloPreparacion("Los datos o documentos del requerimiento cambiaron durante la preparacion. Actualice y revise antes de reintentar.", null);
        }
        if (r == null || requerimientoPreparado == null || !r.isActivo()
                || r.getIdRequerimientoCompra() != requerimientoPreparado.getIdRequerimientoCompra()
                || r.getEstado() != requerimientoPreparado.getEstado()
                || r.getIdPrestadorAdjudicadoInt() != adjudicadoPreparado) {
            throw falloPreparacion("El estado o la adjudicacion cambiaron antes de reservar el correo.", null);
        }
        validarRequerimientoReconstruible(r);
        validarRpVigente(relaciones, rpPreparado);
    }

    protected List<RequerimientoCompraComparativa> obtenerCotizaciones(int id) throws Exception {
        // La consulta de recuperacion parte de las cotizaciones persistidas, no de los adjuntos que aun existen.
        return RequerimientoCompraComparativaServiceUtil.listarParaRecuperacion(id);
    }

    protected List<RequerimientoCompraPresupuesto> obtenerPresupuestos(int id) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.listarPresupuestos(id);
    }

    protected List<RequerimientoCompraPresupuesto> obtenerOrdenes(int id) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.listarOrdenesMedicas(id);
    }

    protected void validarConfiguracionCorreo() throws Exception {
        try {
            mailHelper.validarConfiguracionEnvio();
        } catch (Exception e) {
            throw falloPreparacion("La configuracion de correo no es valida. Revise servidor, remitente y credenciales antes de reintentar.", e);
        }
    }



    protected RequerimientoCompra obtenerRequerimiento(int id) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(id);
    }

    protected String getConfiguracion(String clave) {
        return TraeListasServiceUtil.getSystemConfig(clave);
    }

    protected RequerimientoCompraReclamoPrestacional obtenerRelacion(int id) throws Exception {
        return RequerimientoCompraReclamoPrestacionalServiceUtil.consultarPorRequerimiento(id);
    }

    protected AdjuntoOrdenMedica recuperarPedido(int id, ThemeDisplay contexto) throws Exception {
        RequerimientoCompraPedidoCotizacion pedido;
        try {
            pedido = obtenerPedidoAdjudicado(id);
        } catch (Exception e) {
            throw falloPreparacion("No se pudo consultar el pedido del adjudicado por un error de persistencia.", e);
        }
        if (pedido == null) return crearPedidoActualParaCorreo(id);
        if (pedido.getIdRequerimiento() == null || pedido.getIdRequerimiento().intValue() != id
                || pedido.getIdPrestador() == null || pedido.getIdPrestador().intValue() != adjudicadoPreparado
                || pedido.getIntento() == null || pedido.getIntento().intValue() <= 0) {
            throw falloPreparacion("La asociacion del pedido del adjudicado es inconsistente.", null);
        }
        try {
            DocumentoLibraryComprasHelper.validarIdentidadAsociacionDocumento(pedido);
        } catch (Exception e) {
            throw falloPreparacion("La identidad DL asociada al pedido del adjudicado es inconsistente.", e);
        }
        if (pedido.getDlGroupId().longValue() != contexto.getScopeGroupId()) {
            throw falloPreparacion("El pedido del adjudicado pertenece a otro sitio.", null);
        }
        DLFileEntry entry;
        try {
            entry = DLFileEntryLocalServiceUtil.getDLFileEntry(pedido.getDlFileEntryId().longValue());
        } catch (NoSuchFileEntryException e) {
            validarCarpetaPedidoAusente(pedido, contexto);
            return crearPedidoActualParaCorreo(id);
        } catch (Exception e) {
            throw falloLecturaDocumento("pedido del adjudicado (DL " + pedido.getDlFileEntryId() + ")", e);
        }
        String referencia = "pedido del adjudicado (DL " + pedido.getDlFileEntryId() + ")";
        if (!DocumentoLibraryComprasHelper.coincideIdentidadAsociacionDocumento(pedido, entry)
                || entry.getTitle() == null
                || !entry.getTitle().startsWith(DocumentoLibraryComprasHelper.TITULO_PEDIDO_COTIZACION
                    + " " + id + " prestador " + adjudicadoPreparado + " ")) {
            throw falloPreparacion("La identidad o pertenencia del " + referencia + " no coincide con la compra.", null);
        }
        validarVistaDocumento(entry, contexto, referencia);
        byte[] contenido;
        try {
            contenido = DocumentoLibraryComprasHelper.leerContenidoDocumentLibrary(entry);
        } catch (NoSuchFileException e) {
            return crearPedidoActualParaCorreo(id);
        } catch (Exception e) {
            throw falloLecturaDocumento(referencia, e);
        }
        try {
            return adjunto(contenido, "RequerimientoCompra_" + id + ".pdf", "application/pdf");
        } catch (Exception e) {
            throw falloPreparacion("El " + referencia + " contiene un PDF invalido; no se reemplaza automaticamente.", e);
        }
    }

    protected void validarCarpetaPedidoAusente(RequerimientoCompraPedidoCotizacion pedido,
            ThemeDisplay contexto) throws Exception {
        String referencia = "carpeta del pedido ausente (DL " + pedido.getDlFileEntryId() + ")";
        try {
            DLFolder folder = DLFolderLocalServiceUtil.getDLFolder(pedido.getDlFolderId().longValue());
            if (folder == null || folder.getCompanyId() != contexto.getCompanyId()
                    || folder.getGroupId() != contexto.getScopeGroupId()) {
                throw falloPreparacion("La " + referencia + " no pertenece al sitio actual.", null);
            }
            DLFolderPermission.check(contexto.getPermissionChecker(), folder, ActionKeys.VIEW);
        } catch (Exception e) {
            if (advertenciaNotificacion != null) throw e;
            throw falloLecturaDocumento(referencia, e);
        }
    }

    private AdjuntoOrdenMedica crearPedidoActualParaCorreo(int id) throws Exception {
        validarRequerimientoReconstruible(requerimientoPreparado);
        try {
            byte[] actual = new PdfServlet().identificarCopiaActualAutorizacion(
                    generarPedidoActual(id), id, "Pedido de cotizacion");
            AdjuntoOrdenMedica adjunto = adjunto(actual,
                    "RequerimientoCompra_" + id + "_copia_actual.pdf", "application/pdf");
            pedidoActualGenerado = true;
            return adjunto;
        } catch (Exception e) {
            throw falloPreparacion("No se pudo reconstruir una copia ACTUAL valida del pedido de la compra " + id + ".", e);
        }
    }

    protected RequerimientoCompraPedidoCotizacion obtenerPedidoAdjudicado(int id) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.getPedidoCotizacionAdjudicado(id);
    }

    protected Map<String, Object> obtenerContextoPedidoAutorizacion(int id) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.getContextoPedidoAutorizacionAdjudicado(id);
    }

    /** Mismo generador que NotificarCotizacionPrestadorHelper; solo bytes actuales, sin alta historica. */
    protected byte[] generarPedidoActual(int id) throws Exception {
        return new PdfServlet().crearRequerimientoCompraComoAdjunto(id);
    }

    protected List<AdjuntoOrdenMedica> recuperarAdjuntos(int id,
            RequerimientoCompraReclamoPrestacional relacion, ThemeDisplay contexto) throws Exception {
        List<AdjuntoOrdenMedica> adjuntos = new ArrayList<AdjuntoOrdenMedica>();
        int rp = relacion.getIdReclamoPrestacionalInt();
        DLFileEntry comparativa;
        try {
            comparativa = obtenerComparativa(id, rp, contexto.getScopeGroupId());
        } catch (NoSuchFileEntryException e) {
            comparativa = null;
        } catch (Exception e) {
            throw falloLecturaDocumento("comparativa de la compra " + id + " y RP " + rp, e);
        }
        byte[] contenidoComparativa = null;
        boolean reconstruir = comparativa == null;
        if (comparativa != null) {
            validarVistaDocumento(comparativa, contexto, "comparativa de la compra " + id + " (DL "
                    + comparativa.getFileEntryId() + ")");
            try {
                contenidoComparativa = DocumentoLibraryComprasHelper.leerContenidoDocumentLibrary(comparativa);
            } catch (NoSuchFileException e) {
                reconstruir = true;
            } catch (Exception e) {
                throw falloLecturaDocumento("comparativa (DL " + comparativa.getFileEntryId() + ")", e);
            }
        }
        String nombreComparativa = rp + "-ComparativaCompra_" + id;
        if (reconstruir) {
            validarComparativaReconstruible(id);
            try {
                contenidoComparativa = new PdfServlet().identificarCopiaActualAutorizacion(
                        generarComparativaActual(id), id, "Comparativa de presupuestos");
                nombreComparativa += "_copia_actual";
                comparativaActualGenerada = true;
            } catch (Exception e) {
                throw falloPreparacion("No se pudo reconstruir una comparativa ACTUAL valida para la compra "
                        + id + " y RP " + rp + ".", e);
            }
        }
        try {
            adjuntos.add(adjunto(contenidoComparativa, nombreComparativa + ".pdf", "application/pdf"));
        } catch (Exception e) {
            throw falloPreparacion("La comparativa de la compra " + id + " y RP " + rp
                    + " no contiene un PDF valido.", e);
        }
        try {
            adjuntos.add(adjunto(generarFichaRp(rp), "RP " + rp + ".pdf", "application/pdf"));
        } catch (Exception e) {
            throw falloPreparacion("No se pudo generar una ficha imprimible valida del RP " + rp + ".", e);
        }
        return adjuntos;
    }

    protected DLFileEntry obtenerComparativa(int id, int rp, long group) throws Exception {
        return DocumentoLibraryComprasHelper.obtenerComparativaReclamoPrestacional(id, rp, group);
    }

    protected byte[] generarFichaRp(int rp) throws Exception {
        HashMap<String, String> parametros = new HashMap<String, String>();
        parametros.put("idreclamo", String.valueOf(rp));
        parametros.put("SUBREPORT_DIR", "jasper/reclamo_prestacionales/");
        parametros.put("pathimage", "jasper/logo_negro.jpg");
        return new PdfServlet().crearPdfComoAdjunto(
                "jasper/reclamo_prestacionales/reclamoprestacional.jasper", parametros, "RP " + rp + ".pdf");
    }

    protected byte[] generarComparativaActual(int id) throws Exception {
        return new PdfServlet().crearComparativaCompraComoAdjunto(id);
    }

    private void validarComparativaReconstruible(int id) throws Exception {
        validarRequerimientoReconstruible(requerimientoPreparado);
        try {
            new RequerimientoCompraComparativaHelper().validarPrestaciones(requerimientoPreparado.getDetalles());
        } catch (Exception e) {
            throw falloPreparacion("La comparativa actual requiere prestaciones de nomenclador vigentes y unicas.", e);
        }
        List<RequerimientoCompraComparativa> cotizaciones = obtenerCotizaciones(id);
        if (cotizaciones == null || cotizaciones.isEmpty()) {
            throw falloPreparacion("No hay cotizaciones persistidas suficientes para reconstruir la comparativa actual.", null);
        }
        Set<Integer> prestaciones = new HashSet<Integer>();
        for (int i = 0; i < requerimientoPreparado.getDetalles().size(); i++) {
            prestaciones.add(requerimientoPreparado.getDetalles().get(i).getIdPrestacion());
        }
        boolean ganadorCotizado = false;
        for (int i = 0; i < cotizaciones.size(); i++) {
            RequerimientoCompraComparativa c = cotizaciones.get(i);
            if (c == null || c.getIdComparativa() <= 0 || c.getIdRequerimiento() != id
                    || c.getIdPrestador() <= 0 || c.getIncompleto() || c.getDetalles() == null) {
                throw falloPreparacion("Hay una cotizacion persistida inconsistente; no se puede reconstruir la comparativa.", null);
            }
            Set<Integer> vistas = new HashSet<Integer>();
            boolean tieneDetalle = false;
            for (int j = 0; j < c.getDetalles().size(); j++) {
                RequerimientoCompraComparativaDetalle d = c.getDetalles().get(j);
                if (d != null && d.isBajaReal()) continue;
                if (d == null || d.getIdDetalle() <= 0 || !prestaciones.contains(Integer.valueOf(d.getIdPrestacion()))
                        || !vistas.add(Integer.valueOf(d.getIdPrestacion()))
                        || (d.getCantidad() != null && d.getCantidad().intValue() < 0)
                        || (d.getImporteUnitario() != null && d.getImporteUnitario().signum() < 0)
                        || (d.getSubtotal() != null && d.getSubtotal().signum() < 0)) {
                    throw falloPreparacion("La cotizacion del prestador " + c.getIdPrestador()
                            + " no conserva detalles actuales suficientes y coherentes.", null);
                }
                tieneDetalle = true;
                // Como calcular de ComparativaHelper: el par vacio representa NO cotizado.
                if (d.getCantidad() == null && d.getImporteUnitario() == null) continue;
                if (d.getCantidad() == null || d.getImporteUnitario() == null) {
                    throw falloPreparacion("La cotizacion del prestador " + c.getIdPrestador()
                            + " conserva una cantidad o importe incompleto.", null);
                }
                if (c.getIdPrestador() == adjudicadoPreparado && d.getCantidad().intValue() > 0) {
                    ganadorCotizado = true;
                }
            }
            if (!tieneDetalle) {
                throw falloPreparacion("La cotizacion del prestador " + c.getIdPrestador()
                        + " no conserva prestaciones cotizadas vigentes.", null);
            }
        }
        if (!ganadorCotizado) {
            throw falloPreparacion("El adjudicado " + adjudicadoPreparado
                    + " no conserva una cotizacion vigente suficiente para reconstruir la comparativa.", null);
        }
    }

    protected List<AdjuntoOrdenMedica> recuperarOrdenes(int id, ThemeDisplay contexto) throws Exception {
        List<RequerimientoCompraPresupuesto> ordenes;
        try {
            ordenes = obtenerOrdenes(id);
        } catch (Exception e) {
            throw falloPreparacion("No se pudieron consultar las ordenes medicas por un error de persistencia.", e);
        }
        if (ordenes == null || ordenes.isEmpty()) {
            throw falloPreparacion("Falta una orden medica original activa para la compra " + id + ".", null);
        }
        List<AdjuntoOrdenMedica> adjuntos = new ArrayList<AdjuntoOrdenMedica>();
        Set<Long> documentos = new HashSet<Long>();
        for (int i = 0; i < ordenes.size(); i++) {
            RequerimientoCompraPresupuesto orden = ordenes.get(i);
            String referencia = "orden medica original " + (orden != null ? orden.getIdRequerimientoPresupuesto() : null)
                    + " (DL " + (orden != null ? orden.getDlFileEntryId() : null) + ")";
            DLFileEntry entry;
            try {
                entry = DocumentoLibraryComprasHelper.obtenerEntradaOrdenMedicaValidada(orden, id, contexto.getCompanyId());
            } catch (Exception e) {
                throw falloLecturaDocumento(referencia, e);
            }
            if (entry.getTitle() == null || !entry.getTitle().startsWith(
                    DocumentoLibraryComprasHelper.TITULO_ORDEN_MEDICA + " " + id + " ")) {
                throw falloPreparacion("La pertenencia de la " + referencia + " no coincide con la compra.", null);
            }
            if (!documentos.add(Long.valueOf(entry.getFileEntryId()))) {
                throw falloPreparacion("La " + referencia + " esta duplicada en Document Library.", null);
            }
            validarVistaDocumento(entry, contexto, referencia);
            DocumentoLibraryComprasHelper.OrdenMedicaContenido contenido;
            try {
                contenido = DocumentoLibraryComprasHelper.leerOrdenMedicaValidada(entry, orden.getNombreOriginal());
            } catch (Exception e) {
                throw falloLecturaDocumento(referencia, e);
            }
            try {
                adjuntos.add(adjunto(contenido.getContenido(), contenido.getNombreOriginal(), contenido.getContentType()));
            } catch (Exception e) {
                throw falloPreparacion("La " + referencia + " tiene contenido invalido.", e);
            }
        }
        return adjuntos;
    }

    protected List<AdjuntoOrdenMedica> recuperarPresupuestos(int id, ThemeDisplay contexto) throws Exception {
        List<RequerimientoCompraPresupuesto> presupuestos;
        List<RequerimientoCompraComparativa> cotizaciones;
        try {
            presupuestos = obtenerPresupuestos(id);
            cotizaciones = obtenerCotizaciones(id);
        } catch (Exception e) {
            throw falloPreparacion("No se pudieron consultar los presupuestos originales y cotizaciones por un error de persistencia.", e);
        }
        if (presupuestos == null || cotizaciones == null) {
            throw falloPreparacion("No se pudo acreditar la lista de presupuestos originales obligatorios de la compra " + id + ".", null);
        }
        Set<Integer> esperados = new HashSet<Integer>();
        esperados.add(Integer.valueOf(adjudicadoPreparado));
        for (int i = 0; i < cotizaciones.size(); i++) {
            RequerimientoCompraComparativa cotizacion = cotizaciones.get(i);
            if (cotizacion == null || cotizacion.getIdRequerimiento() != id || cotizacion.getIdPrestador() <= 0) {
                throw falloPreparacion("Hay una cotizacion asociada a otra compra o a un prestador invalido.", null);
            }
            if (cotizacion.getIdComparativa() > 0) esperados.add(Integer.valueOf(cotizacion.getIdPrestador()));
        }
        Set<Integer> presentes = new HashSet<Integer>();
        for (int i = 0; i < presupuestos.size(); i++) {
            RequerimientoCompraPresupuesto presupuesto = presupuestos.get(i);
            if (presupuesto == null || presupuesto.getIdRequerimientoPresupuesto() == null
                    || presupuesto.getIdRequerimientoPresupuesto().intValue() <= 0
                    || presupuesto.getIdRequerimiento() == null || presupuesto.getIdRequerimiento().intValue() != id
                    || presupuesto.getTipoDocumento() == null
                    || presupuesto.getTipoDocumento().intValue() != RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_PRESUPUESTO
                    || presupuesto.getIdPrestador() == null || presupuesto.getIdPrestador().intValue() <= 0
                    || !presupuesto.isActivo()) {
                throw falloPreparacion("La asociacion de un presupuesto original es inconsistente en la compra " + id + ".", null);
            }
            presentes.add(presupuesto.getIdPrestador());
        }
        for (Integer prestador : esperados) {
            if (!presentes.contains(prestador)) {
                throw falloPreparacion("Falta el presupuesto original activo del prestador " + prestador
                        + (prestador.intValue() == adjudicadoPreparado ? " adjudicado" : " con cotizacion persistida")
                        + " en la compra " + id + ".", null);
            }
        }
        List<AdjuntoOrdenMedica> adjuntos = new ArrayList<AdjuntoOrdenMedica>();
        Set<Long> documentos = new HashSet<Long>();
        for (int i = 0; i < presupuestos.size(); i++) {
            RequerimientoCompraPresupuesto presupuesto = presupuestos.get(i);
            String referencia = "presupuesto original " + presupuesto.getIdRequerimientoPresupuesto()
                    + " del prestador " + presupuesto.getIdPrestador()
                    + " (DL " + presupuesto.getDlFileEntryId() + ")";
            try {
                DocumentoLibraryComprasHelper.validarIdentidadAsociacionDocumento(presupuesto);
            } catch (Exception e) {
                throw falloPreparacion("La identidad asociada al " + referencia + " es inconsistente.", e);
            }
            DLFileEntry entry;
            try {
                entry = DLFileEntryLocalServiceUtil.getDLFileEntry(presupuesto.getDlFileEntryId().longValue());
            } catch (Exception e) {
                throw falloLecturaDocumento(referencia, e);
            }
            if (!DocumentoLibraryComprasHelper.coincideIdentidadAsociacionDocumento(presupuesto, entry)
                    || entry.getTitle() == null
                    || !entry.getTitle().startsWith(WebKeysCompras.getPrefijoDocumentoRequerimientoCompra(id))) {
                throw falloPreparacion("La identidad o pertenencia del " + referencia + " no coincide con la compra.", null);
            }
            if (!documentos.add(Long.valueOf(entry.getFileEntryId()))) {
                throw falloPreparacion("El " + referencia + " esta duplicado en Document Library.", null);
            }
            validarVistaDocumento(entry, contexto, referencia);
            byte[] contenido;
            try {
                contenido = DocumentoLibraryComprasHelper.leerContenidoDocumentLibrary(entry);
            } catch (Exception e) {
                throw falloLecturaDocumento(referencia, e);
            }
            try {
                adjuntos.add(adjunto(contenido, presupuesto.getNombreOriginal(), "application/pdf"));
            } catch (Exception e) {
                throw falloPreparacion("El " + referencia + " tiene un PDF invalido; debe recuperarse el original.", e);
            }
        }
        return adjuntos;
    }

    private Exception falloLecturaDocumento(String documento, Exception causa) {
        String mensaje;
        if (causa instanceof com.liferay.portal.security.auth.PrincipalException) {
            mensaje = "No posee permiso VIEW para el " + documento + ".";
        } else if (causa instanceof NoSuchFileEntryException) {
            mensaje = "Falta el registro de Document Library del " + documento + ".";
        } else if (causa instanceof NoSuchFileException) {
            mensaje = "Falta el archivo fisico original del " + documento + ".";
        } else if (causa instanceof com.liferay.portal.SystemException) {
            mensaje = "Error de infraestructura de Document Library al recuperar el " + documento + ".";
        } else if (causa instanceof java.sql.SQLException) {
            mensaje = "Error de persistencia al consultar el " + documento + ".";
        } else if (causa instanceof java.io.IOException) {
            mensaje = "Error de lectura del " + documento + "; no se acredita que este ausente.";
        } else {
            mensaje = "No se pudo validar o recuperar el " + documento + ". Revise su identidad y contenido.";
        }
        return falloPreparacion(mensaje, causa);
    }

    private void validarVistaDocumento(DLFileEntry entry, ThemeDisplay contexto, String referencia) throws Exception {
        try {
            validarVista(entry, contexto);
        } catch (Exception e) {
            throw falloLecturaDocumento(referencia, e);
        }
        identidadesPreparadas.put(Long.valueOf(entry.getFileEntryId()), identidadDocumento(entry));
    }

    private String identidadDocumento(DLFileEntry entry) {
        return entry.getCompanyId() + "|" + entry.getGroupId() + "|" + entry.getFolderId() + "|"
                + entry.getName() + "|" + entry.getUuid() + "|" + entry.getVersion() + "|"
                + entry.getSize() + "|" + entry.getTitle();
    }

    private void validarVista(DLFileEntry entry, ThemeDisplay contexto) throws Exception {
        if (entry == null || entry.getCompanyId() != contexto.getCompanyId()
                || entry.getGroupId() != contexto.getScopeGroupId()) {
            throw new Exception("Un documento del RP no esta disponible en el sitio actual.");
        }
        DLFileEntryPermission.check(contexto.getPermissionChecker(),
                entry.getFolderId(), entry.getName(), ActionKeys.VIEW);
    }

    private AdjuntoOrdenMedica adjunto(byte[] contenido, String nombre, String tipo) throws Exception {
        String validado = DocumentoLibraryComprasHelper.validarContenidoOrdenMedica(contenido, nombre);
        if (!tipo.equals(validado)) {
            throw new Exception("Un documento del RP no contiene el tipo de archivo esperado.");
        }
        if ("application/pdf".equals(tipo)) {
            DocumentoLibraryComprasHelper.validarPdfParaCorreoAutorizacion(contenido, nombre);
        } else {
            DocumentoLibraryComprasHelper.validarImagenParaCorreoAutorizacion(contenido, nombre, tipo);
        }
        return new AdjuntoOrdenMedica(contenido, nombre, tipo);
    }

    /** Reserva persistida antes de SMTP, como la reserva de notificacion a prestadores. */
    protected String reservarNotificacion(int id, String usuario) throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx = null;
        try {
            tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            RequerimientoCompra r = tx.bloquearYObtenerRequerimiento(id);
            int estado = ConfiguracionComprasHelper.resolverEstado("PARA_AUTORIZAR",
                    tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS));
            if (r == null || !r.isActivo() || r.getIdRequerimientoCompra() != id || r.getEstado() != estado) {
                throw falloPreparacion("El requerimiento cambio antes de reservar el correo.", null);
            }
            validarPreparacionVigente(r, tx.obtenerRelacionesRp(id), tx.getHuellaPreparacionAutorizacion(id));
            for (Map.Entry<Long, String> preparado : identidadesPreparadas.entrySet()) {
                DLFileEntry vigente;
                try {
                    vigente = DLFileEntryLocalServiceUtil.getDLFileEntry(preparado.getKey().longValue());
                    validarVista(vigente, contextoPreparado);
                } catch (Exception e) {
                    throw falloLecturaDocumento("documento preparado (DL " + preparado.getKey() + ")", e);
                }
                if (!preparado.getValue().equals(identidadDocumento(vigente))) {
                    throw falloPreparacion("El documento DL " + preparado.getKey()
                            + " cambio durante la preparacion. Actualice y revise los adjuntos.", null);
                }
            }
            String token = java.util.UUID.randomUUID().toString();
            if (tx.insertarReservaCorreoAutorizacion(id, token, usuario) != 1) {
                Map<String, Object> registro = tx.obtenerReservaCorreoAutorizacion(id);
                if (registro != null && "ENVIADO".equals(registro.get("estado_envio"))) {
                    advertenciaNotificacion = "El aviso ya fue aceptado por el servidor SMTP. No se realizo otro envio.";
                } else if (registro != null && "PARCIAL".equals(registro.get("resultado_smtp"))) {
                    advertenciaNotificacion = "El aviso anterior fue aceptado parcialmente por SMTP."
                            + " No se realizo otro envio; verifique los destinatarios ya aceptados.";
                } else if (registro != null && "RECHAZADO".equals(registro.get("resultado_smtp"))) {
                    advertenciaNotificacion = "El aviso anterior fue rechazado por SMTP."
                            + " No se realizo otro envio; revise la causa y el registro antes de habilitar otra solicitud.";
                } else {
                    advertenciaNotificacion = "El aviso tiene un envio en curso o con resultado incierto."
                            + " No se realizo otro envio. Verifique el registro antes de continuar.";
                }
                throw new Exception("El aviso de autorizacion ya posee una reserva persistida.");
            }
            tx.commit();
            return token;
        } catch (Exception e) {
            if (tx != null) {
                try { tx.rollback(); }
                catch (Exception rollback) { _log.error("No se pudo confirmar rollback de reserva del correo. id=" + id, rollback); }
            }
            throw e;
        } finally { if (tx != null) tx.cerrar(); }
    }

    protected void finalizarNotificacion(int id, String token, String estado) throws Exception {
        finalizarNotificacion(id, token, estado, "ENVIADO".equals(estado) ? "ACEPTADO" : "INCIERTO");
    }

    protected void finalizarNotificacion(int id, String token, String estado, String resultado) throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx = null;
        try {
            tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            if (tx.finalizarCorreoAutorizacion(id, token, estado, resultado) != 1) {
                throw new Exception("La reserva del correo ya no coincide con esta solicitud.");
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                try { tx.rollback(); }
                catch (Exception rollback) { _log.error("No se pudo confirmar rollback del resultado del correo. id=" + id, rollback); }
            }
            throw e;
        } finally { if (tx != null) tx.cerrar(); }
    }

    protected void cambiarEstado(int id, String usuario) throws Exception {
        new EditarRequerimientoCompraHelper().cambiarEstado(
                id, ConfiguracionComprasHelper.getEstadoParaAutorizar(), usuario);
    }

    protected void enviarCorreo(String[] destinos, String asunto, String cuerpo,
            AdjuntoOrdenMedica pedido, List<AdjuntoOrdenMedica> adjuntos) throws Exception {
        mailHelper.enviar(destinos, new String[0], asunto, cuerpo,
                pedido.getContenido(), pedido.getNombre(), adjuntos);
    }
}
