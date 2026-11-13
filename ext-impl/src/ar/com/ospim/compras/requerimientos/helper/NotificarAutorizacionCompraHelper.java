package ar.com.ospim.compras.requerimientos.helper;

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
import java.util.Set;
import javax.mail.internet.InternetAddress;

/** Correo posterior al guardado, como EditarOrdenesPagoOspimAction de Liquidaciones. */
public class NotificarAutorizacionCompraHelper {
    private static final Log _log = LogFactoryUtil.getLog(NotificarAutorizacionCompraHelper.class);

    public static final String CONFIG_EMAIL_AUTORIZACION = "COMPRAS_EMAIL_AUTORIZACION";

    private final CotizacionPrestadorMailHelper mailHelper = new CotizacionPrestadorMailHelper();

    /** La transicion es autoritativa; el aviso de correo no puede impedirla. */
    public void enviarAAutorizar(int id, String usuario, ThemeDisplay contexto) throws Exception {
        cambiarEstado(id, usuario);
        try {
            notificarAutorizacion(id, usuario, contexto);
        } catch (Exception e) {
            _log.error("No se pudo confirmar el correo de autorizacion. Estado PARA AUTORIZAR confirmado. id=" + id, e);
        }
    }

    private void notificarAutorizacion(int id, String usuario, ThemeDisplay contexto) throws Exception {
        RequerimientoCompra requerimiento = obtenerRequerimiento(id);
        if (requerimiento == null || !requerimiento.isActivo()
                || requerimiento.getIdRequerimientoCompra() != id) {
            throw new Exception("El requerimiento ya no esta disponible.");
        }
        // RRHH/Sistemas no poseen RP: conservar su circuito vigente.
        if (requerimiento.esSectorSinCotizacionPrestador()) {
            return;
        }
        if (id <= 0 || contexto == null || contexto.getCompanyId() <= 0L
                || contexto.getScopeGroupId() <= 0L || contexto.getUser() == null
                || contexto.getPermissionChecker() == null
                || contexto.getPermissionChecker().getUserId() != contexto.getUser().getUserId()) {
            throw new Exception("No se pudo determinar el contexto del envio a autorizar.");
        }
        RequerimientoCompraReclamoPrestacional relacion = obtenerRelacion(id);
        if (relacion == null || !relacion.isVinculado() || relacion.getBajaFecha() != null
                || relacion.getIdRequerimientoCompra() != id) {
            throw new Exception("Debe existir un Reclamo Prestacional vinculado y activo.");
        }
        String[] destinos = resolverDestinatarios();
        AdjuntoOrdenMedica pedido = recuperarPedido(id, contexto);
        List<AdjuntoOrdenMedica> adjuntos = recuperarAdjuntos(id, relacion, contexto);
        if (pedido == null || adjuntos == null || adjuntos.size() < 3) {
            throw new Exception("No se recuperaron todos los documentos del RP para el correo.");
        }
        adjuntos.addAll(recuperarPresupuestos(id, contexto));
        String nombre = (requerimiento.getAfiliadoApellidoVisible() + " "
                + requerimiento.getAfiliadoNombreVisible()).trim();
        if (nombre.length() == 0) nombre = requerimiento.getAfiliadoNombreApellidoVisible();
        String asunto = "RP:" + relacion.getIdReclamoPrestacionalInt() + "-" + textoAsunto(nombre)
                + "-DU:" + textoAsunto(requerimiento.getAfiliadoDocumentoNroVisible())
                + "-" + textoAsunto(requerimiento.getAfiliadoLocalidadVisible())
                + "-" + textoAsunto(requerimiento.getAfiliadoProvinciaVisible())
                + "-ID:" + id + "-COMPARATIVA";
        String cuerpo = "Se envía a autorizar el requerimiento de compra Nro. " + id
                + ", vinculado al Reclamo Prestacional Nro. " + relacion.getIdReclamoPrestacionalInt()
                + ".\n\nSe adjuntan la comparativa, la ficha imprimible del RP, las ordenes medicas"
                + ", el pedido de presupuesto y todos los presupuestos cargados.";
        // Validar el MIME completo antes del correo; no enviar documentos incompletos.
        mailHelper.construirMultipart(cuerpo, pedido.getContenido(), pedido.getNombre(), adjuntos);
        validarTamanoCorreo(pedido, adjuntos);
        enviarCorreo(destinos, asunto, cuerpo, pedido, adjuntos);
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
        RequerimientoCompraPedidoCotizacion pedido =
                BusquedaRequerimientoCompraServiceUtil.getPedidoCotizacionAdjudicado(id);
        if (pedido == null || pedido.getIdRequerimiento() == null
                || pedido.getIdRequerimiento().intValue() != id
                || pedido.getIdPrestador() == null || pedido.getIdPrestador().intValue() <= 0
                || pedido.getIntento() == null || pedido.getIntento().intValue() <= 0) {
            throw new Exception("El pedido de presupuesto adjudicado no esta disponible.");
        }
        DocumentoLibraryComprasHelper.validarIdentidadAsociacionDocumento(pedido);
        DLFileEntry entry = DLFileEntryLocalServiceUtil.getDLFileEntry(pedido.getDlFileEntryId().longValue());
        if (!DocumentoLibraryComprasHelper.coincideIdentidadAsociacionDocumento(pedido, entry)) {
            throw new Exception("La identidad del pedido de presupuesto no coincide con Document Library.");
        }
        validarVista(entry, contexto);
        return adjunto(DocumentoLibraryComprasHelper.leerContenidoDocumentLibrary(entry),
                "RequerimientoCompra_" + id + ".pdf", "application/pdf");
    }

    protected List<AdjuntoOrdenMedica> recuperarAdjuntos(int id,
            RequerimientoCompraReclamoPrestacional relacion, ThemeDisplay contexto) throws Exception {
        List<AdjuntoOrdenMedica> adjuntos = new ArrayList<AdjuntoOrdenMedica>();
        int rp = relacion.getIdReclamoPrestacionalInt();
        DLFileEntry comparativa = DocumentoLibraryComprasHelper.obtenerComparativaReclamoPrestacional(
                id, rp, contexto.getScopeGroupId());
        validarVista(comparativa, contexto);
        adjuntos.add(adjunto(DocumentoLibraryComprasHelper.leerContenidoDocumentLibrary(comparativa),
                rp + "-ComparativaCompra_" + id + ".pdf", "application/pdf"));
        HashMap<String, String> parametros = new HashMap<String, String>();
        parametros.put("idreclamo", String.valueOf(rp));
        parametros.put("SUBREPORT_DIR", "jasper/reclamo_prestacionales/");
        parametros.put("pathimage", "jasper/logo_negro.jpg");
        try {
            adjuntos.add(adjunto(new PdfServlet().crearPdfComoAdjunto(
                    "jasper/reclamo_prestacionales/reclamoprestacional.jasper", parametros,
                    "RP " + rp + ".pdf"), "RP " + rp + ".pdf", "application/pdf"));
        } catch (Exception e) {
            throw new Exception("No se pudo generar la ficha imprimible del RP para adjuntar al correo.", e);
        }
        List<RequerimientoCompraPresupuesto> ordenes =
                BusquedaRequerimientoCompraServiceUtil.listarOrdenesMedicas(id);
        if (ordenes == null || ordenes.isEmpty()) {
            throw new Exception("No se encontro una orden medica activa para adjuntar al correo.");
        }
        Set<Long> documentos = new HashSet<Long>();
        for (int i = 0; i < ordenes.size(); i++) {
            RequerimientoCompraPresupuesto orden = ordenes.get(i);
            DLFileEntry entry = DocumentoLibraryComprasHelper.obtenerEntradaOrdenMedicaValidada(
                    orden, id, contexto.getCompanyId());
            if (!documentos.add(Long.valueOf(entry.getFileEntryId()))) {
                throw new Exception("Hay ordenes medicas duplicadas en Document Library.");
            }
            validarVista(entry, contexto);
            DocumentoLibraryComprasHelper.OrdenMedicaContenido contenido =
                    DocumentoLibraryComprasHelper.leerOrdenMedicaValidada(entry, orden.getNombreOriginal());
            adjuntos.add(adjunto(contenido.getContenido(), contenido.getNombreOriginal(), contenido.getContentType()));
        }
        return adjuntos;
    }

    protected List<AdjuntoOrdenMedica> recuperarPresupuestos(int id, ThemeDisplay contexto) throws Exception {
        List<RequerimientoCompraPresupuesto> presupuestos =
                BusquedaRequerimientoCompraServiceUtil.listarPresupuestos(id);
        if (presupuestos == null) {
            throw new Exception("No se pudieron recuperar los presupuestos cargados de la compra.");
        }
        List<AdjuntoOrdenMedica> adjuntos = new ArrayList<AdjuntoOrdenMedica>();
        Set<Long> documentos = new HashSet<Long>();
        for (int i = 0; i < presupuestos.size(); i++) {
            RequerimientoCompraPresupuesto presupuesto = presupuestos.get(i);
            if (presupuesto == null || presupuesto.getIdRequerimientoPresupuesto() == null
                    || presupuesto.getIdRequerimientoPresupuesto().intValue() <= 0
                    || presupuesto.getIdRequerimiento() == null || presupuesto.getIdRequerimiento().intValue() != id
                    || presupuesto.getTipoDocumento() == null
                    || presupuesto.getTipoDocumento().intValue() != RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_PRESUPUESTO
                    || presupuesto.getIdPrestador() == null || presupuesto.getIdPrestador().intValue() <= 0
                    || !presupuesto.isActivo()) {
                throw new Exception("La asociacion de un presupuesto cargado es inconsistente.");
            }
            DocumentoLibraryComprasHelper.validarIdentidadAsociacionDocumento(presupuesto);
            DLFileEntry entry = DLFileEntryLocalServiceUtil.getDLFileEntry(presupuesto.getDlFileEntryId().longValue());
            if (!DocumentoLibraryComprasHelper.coincideIdentidadAsociacionDocumento(presupuesto, entry)) {
                throw new Exception("La identidad del presupuesto no coincide con Document Library.");
            }
            if (!documentos.add(Long.valueOf(entry.getFileEntryId()))) {
                throw new Exception("Hay presupuestos duplicados en Document Library.");
            }
            validarVista(entry, contexto);
            adjuntos.add(adjunto(DocumentoLibraryComprasHelper.leerContenidoDocumentLibrary(entry),
                    presupuesto.getNombreOriginal(), "application/pdf"));
        }
        return adjuntos;
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
        return new AdjuntoOrdenMedica(contenido, nombre, tipo);
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
