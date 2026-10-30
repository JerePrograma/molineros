package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.GuardadoCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.SectorNomencladorCompra;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra;
import ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper.PresupuestoEntrada;
import ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper.DocumentoPresupuestoCreado;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoComprasCreado;
import ar.com.ospim.compras.requerimientos.documentos.GestorOrdenMedicaDocumento;
import ar.com.ospim.compras.requerimientos.documentos.OrdenMedicaValidada;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Domicilio;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.ServiceContext;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Fuente canónica de reglas funcionales para alta, edición, detalles,
 * estados, cotización y asociaciones documentales del requerimiento.
 *
 * No abre conexiones ni conoce JDBC. Toda persistencia se delega en
 * EditarRequerimientoCompraServiceUtil.
 */
public class EditarRequerimientoCompraHelper {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    EditarRequerimientoCompraHelper.class
            );

    @Deprecated
    public static final int MAX_ORDENES_MEDICAS_POR_CARGA = 20;

    /**
     * @deprecated Usar MAX_ORDENES_MEDICAS_POR_CARGA.
     */
    @Deprecated
    public static final int MAX_ORDENES_MEDICAS_POR_ALTA =
            MAX_ORDENES_MEDICAS_POR_CARGA;

    private static final Pattern DIACRITICOS =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private final NotificarCotizacionPrestadorHelper notificacionHelper =
            new NotificarCotizacionPrestadorHelper();

    private static final class MensajeUsuarioException extends Exception {

        private MensajeUsuarioException(String mensaje) {
            super(mensaje);
        }

        private MensajeUsuarioException(
                String mensaje,
                Throwable causa) {

            super(mensaje, causa);
        }
    }

    public int guardarObservacionInterna(int id,String observacion,String usuario)throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx=null;
        try{
            if(id<=0)throw errorUsuario("Debe informar el requerimiento de compra que desea editar.");
            tx=EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            RequerimientoCompra r=tx.bloquearYObtenerRequerimiento(id);
            if(r==null)throw errorUsuario("El requerimiento ya no esta disponible.");
            String estados=tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS);
            int anulado=ConfiguracionComprasHelper.resolverEstado("ANULADO",estados);
            if((!r.isActivo()&&r.getEstado()!=anulado)
                    || !ConfiguracionComprasHelper.resolverEstadoPermitido("EDITAR_OBSERVACION_INTERNA",r.getEstado(),
                    tx.getConfiguracionCompra("COMPRAS_ESTADOS_EDITAR_OBSERVACION_INTERNA")))
                throw errorUsuario("La observacion interna no puede modificarse en el estado actual. Actualice la pantalla.");
            if(tx.persistirObservacionInterna(id,r.getEstado(),emptyToNull(observacion),normalizarUsuario(usuario))!=1)
                throw errorUsuario("El requerimiento cambio mientras se guardaba la observacion interna.");
            tx.commit();return id;
        }catch(Exception e){
            if(tx!=null)try{tx.rollback();}catch(Exception fallo){e.addSuppressed(fallo);}
            throw manejarErrorOperacion("guardar la observacion interna","No se pudo guardar la observacion interna. Actualice la pantalla e intente nuevamente.",e,"idRequerimiento="+id);
        }finally{if(tx!=null)tx.cerrar();}
    }

    public int guardarRequerimientoCompra(
            RequerimientoCompra requerimiento,
            String usuario) throws Exception {

        EditarRequerimientoCompraServiceUtil.Transaccion transaccion = null;
        try {
            if (requerimiento == null
                    || requerimiento.getIdRequerimientoCompra() <= 0) {

                throw errorUsuario(
                        "Debe informar el requerimiento de compra que desea editar."
                );
            }

            transaccion = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            RequerimientoCompra actual = transaccion.bloquearYObtenerRequerimiento(
                    requerimiento.getIdRequerimientoCompra());

            if (actual == null) {
                throw errorUsuario(
                        "El requerimiento que intenta editar ya no está disponible."
                );
            }

            /*
             * Una vez que el requerimiento deja de estar PENDIENTE,
             * ninguno de sus datos estructurales puede modificarse.
             *
             * El flujo de cotización utiliza sus operaciones específicas
             * y no debe ingresar por este guardado de cabecera.
             */
            if (!actual.isActivo() || actual.getEstado() != estadoEnTransaccion(transaccion, "PENDIENTE")) {
                throw errorUsuario(
                        "El requerimiento solo puede modificarse mientras "
                                + "se encuentra PENDIENTE."
                );
            }


            /*
             * ==========================================================
             * SECTOR INMUTABLE POST-ALTA
             * ==========================================================
             *
             * Si HTTP envia explicitamente otro sector, se rechaza.
             * Si no lo envia, o envia el mismo, siempre se restaura
             * el valor persistido como fuente autoritativa.
             */

            Integer idSectorActual =
                    actual.getIdSector();

            Integer idSectorRecibido =
                    requerimiento.getIdSector();

            if (idSectorRecibido != null
                    && idSectorRecibido.intValue() > 0
                    && !mismoInteger(
                    idSectorActual,
                    idSectorRecibido
            )) {

                throw errorUsuario(
                        "El sector del requerimiento no puede modificarse una vez creado."
                );
            }

            requerimiento.setIdSector(
                    idSectorActual
            );


            /*
             * ==========================================================
             * AFILIADO INMUTABLE POST-ALTA
             * ==========================================================
             *
             * CUIL e integrante conforman la identidad del afiliado
             * asociado al requerimiento.
             *
             * Los campos pueden no llegar desde HTTP porque la pantalla
             * los renderiza en solo lectura. Por eso solamente se compara
             * cada dato cuando fue efectivamente informado.
             *
             * Independientemente de lo recibido, antes de guardar se
             * restauran siempre los valores persistidos.
             */

            String afiliadoCuilActual =
                    actual.getAfiliadoCuilTitular();

            Integer afiliadoIntActual =
                    actual.getAfiliadoInt();

            String afiliadoCuilRecibido =
                    requerimiento.getAfiliadoCuilTitular();

            Integer afiliadoIntRecibido =
                    requerimiento.getAfiliadoInt();


            if (!WebKeysCompras.isEmpty(
                    afiliadoCuilRecibido
            )
                    && !mismoTexto(
                    afiliadoCuilActual,
                    afiliadoCuilRecibido
            )) {

                throw errorUsuario(
                        "El afiliado del requerimiento no puede modificarse una vez creado."
                );
            }


            if (afiliadoIntRecibido != null
                    && !mismoInteger(
                    afiliadoIntActual,
                    afiliadoIntRecibido
            )) {

                throw errorUsuario(
                        "El afiliado del requerimiento no puede modificarse una vez creado."
                );
            }


            requerimiento.setAfiliadoCuilTitular(
                    afiliadoCuilActual
            );

            requerimiento.setAfiliadoInt(
                    afiliadoIntActual
            );

            /*
             * El snapshot completo del afiliado también permanece inmutable.
             *
             * No se vuelve a consultar ni reconstruir desde los datos actuales
             * del padrón durante una edición del requerimiento.
             */
            requerimiento.setAfiliadoIdOspim(
                    actual.getAfiliadoIdOspim()
            );

            requerimiento.setAfiliadoNombre(
                    actual.getAfiliadoNombre()
            );

            requerimiento.setAfiliadoApellido(
                    actual.getAfiliadoApellido()
            );

            requerimiento.setAfiliadoDocumentoTipo(
                    actual.getAfiliadoDocumentoTipo()
            );

            requerimiento.setAfiliadoDocumentoNro(
                    actual.getAfiliadoDocumentoNro()
            );

            requerimiento.setAfiliadoDireccion(
                    actual.getAfiliadoDireccion()
            );

            requerimiento.setAfiliadoLocalidad(
                    actual.getAfiliadoLocalidad()
            );

            requerimiento.setAfiliadoProvincia(
                    actual.getAfiliadoProvincia()
            );

            requerimiento.setAfiliadoCelular(
                    actual.getAfiliadoCelular()
            );

            requerimiento.setAfiliadoTelefono(
                    actual.getAfiliadoTelefono()
            );

            requerimiento.setAfiliadoEmail(
                    actual.getAfiliadoEmail()
            );

            if (requerimiento.isLegales() != actual.isLegales()) {
                throw errorUsuario(
                        "La marca LEGALES sólo puede definirse durante el alta."
                );
            }

            requerimiento.setLegales(actual.getLegales());

            /*
             * Sector y afiliado ya fueron restaurados desde persistencia.
             *
             * El resto de los datos estructurales permanece editable
             * porque el requerimiento sigue PENDIENTE.
             */
            prepararRequerimientoParaGuardar(requerimiento, transaccion);
            requerimiento.setEstado(actual.getEstado());

            validarRequerimientoParaGuardar(
                    requerimiento
            );


            int idGuardado =
                    transaccion.guardarRequerimientoCompra(
                            requerimiento,
                            normalizarUsuario(usuario)
                    );

            if (idGuardado <= 0) {
                throw new IllegalStateException(
                        "El guardado no devolvió un identificador válido."
                );
            }

            transaccion.commit();
            return idGuardado;

        } catch (Exception e) {
            if (transaccion != null) {
                try {
                    transaccion.rollback();
                } catch (Exception rollbackError) {
                    e.addSuppressed(rollbackError);
                }
            }
            throw manejarErrorOperacion(
                    "guardar el requerimiento de compra",
                    "No se pudo guardar el requerimiento de compra. "
                            + "Revise los datos e intente nuevamente.",
                    e,
                    "idRequerimiento="
                            + obtenerIdRequerimientoSeguro(requerimiento)
                            + ", usuario=" + usuario
            );
        } finally {
            if (transaccion != null) {
                transaccion.cerrar();
            }
        }
    }

    public int guardarNuevoRequerimientoCompraConOrdenMedica(
            RequerimientoCompra requerimiento,
            OrdenMedicaValidada ordenMedica,
            GestorOrdenMedicaDocumento gestorDocumento,
            String usuario) throws Exception {

        List<OrdenMedicaValidada> ordenesMedicas =
                new ArrayList<OrdenMedicaValidada>(1);

        ordenesMedicas.add(ordenMedica);

        return guardarNuevoRequerimientoCompraConOrdenesMedicas(
                requerimiento,
                ordenesMedicas,
                gestorDocumento,
                usuario
        );
    }

    public int guardarNuevoRequerimientoCompraConOrdenesMedicas(
            RequerimientoCompra requerimiento,
            List<OrdenMedicaValidada> ordenesMedicas,
            GestorOrdenMedicaDocumento gestorDocumento,
            String usuario) throws Exception {

        return guardarNuevoRequerimientoCompraConCotizacionesEmpresa(
                requerimiento, ordenesMedicas, gestorDocumento, usuario,
                null, null, null, null, null);
    }

    public int guardarNuevoRequerimientoCompraConCotizacionesEmpresa(
            RequerimientoCompra requerimiento,
            List<OrdenMedicaValidada> ordenesMedicas,
            GestorOrdenMedicaDocumento gestorDocumento,
            String usuario,
            List<RequerimientoCompraDetalle> detalles,
            List<PresupuestoEntrada> cotizaciones,
            ServiceContext serviceContext,
            String empresaCuit,
            String empresaSucursal) throws Exception {
        return guardarNuevoRequerimientoCompraConCotizacionesEmpresa(
                requerimiento, ordenesMedicas, gestorDocumento, usuario,
                detalles, cotizaciones, serviceContext, empresaCuit, empresaSucursal, -1);
    }

    public int guardarNuevoRequerimientoCompraConCotizacionesEmpresa(
            RequerimientoCompra requerimiento,
            List<OrdenMedicaValidada> ordenesMedicas,
            GestorOrdenMedicaDocumento gestorDocumento,
            String usuario,
            List<RequerimientoCompraDetalle> detalles,
            List<PresupuestoEntrada> cotizaciones,
            ServiceContext serviceContext,
            String empresaCuit,
            String empresaSucursal,
            int indiceEmpresaAdjudicada) throws Exception {

        EditarRequerimientoCompraServiceUtil.Transaccion transaccion = null;
        boolean commitInvocado = false;
        List<DocumentoComprasCreado> documentosCreados =
                new ArrayList<DocumentoComprasCreado>();
        List<DocumentoPresupuestoCreado> cotizacionesCreadas =
                new ArrayList<DocumentoPresupuestoCreado>();
        PresupuestoCompraHelper presupuestoHelper = new PresupuestoCompraHelper();

        try {
            if (detalles != null) {
                validarNuevoRequerimientoConCotizacionesEmpresa(
                        requerimiento, detalles, cotizaciones, serviceContext,
                        empresaCuit, empresaSucursal, indiceEmpresaAdjudicada);
            }
            prepararRequerimientoParaGuardar(requerimiento);
            validarRequerimientoParaGuardar(requerimiento);

            if (requerimiento.getIdRequerimientoCompra() > 0) {
                throw errorUsuario(
                        "El adjunto obligatorio solo se registra "
                                + "durante el alta de un requerimiento nuevo."
                );
            }

            if (!requerimiento.esSectorSinCotizacionPrestador()
                    || ordenesMedicas == null
                    || !ordenesMedicas.isEmpty()) {

                validarOrdenesMedicasParaAlta(ordenesMedicas);
            }

            if (!ordenesMedicas.isEmpty()
                    && gestorDocumento == null) {

                throw new IllegalStateException(
                        "No se obtuvo el gestor documental del adjunto."
                );
            }

            transaccion = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            prepararRequerimientoParaGuardar(requerimiento, transaccion);
            validarRequerimientoParaGuardar(requerimiento);
            requerimiento.setEstado(estadoEnTransaccion(transaccion, "PENDIENTE"));

            int idRequerimiento =
                    transaccion.guardarRequerimientoCompra(
                            requerimiento,
                            normalizarUsuario(usuario)
                    );

            if (idRequerimiento <= 0) {
                throw new IllegalStateException(
                        "El alta no devolvió un identificador válido."
                );
            }

            asegurarConceptoAutomatico(requerimiento, idRequerimiento, transaccion,
                    normalizarUsuario(usuario));

            for (int i = 0; i < ordenesMedicas.size(); i++) {
                OrdenMedicaValidada ordenMedica =
                        ordenesMedicas.get(i);

                DocumentoComprasCreado documento =
                        gestorDocumento.crearOrdenMedica(
                                idRequerimiento,
                                ordenMedica
                        );

                documentosCreados.add(documento);

                int idDocumento =
                        DocumentoLibraryComprasHelper.registrarOrdenMedicaEnTransaccion(
                                transaccion,
                                idRequerimiento,
                                ordenMedica,
                                documento,
                                normalizarUsuario(usuario)
                        );

                if (idDocumento <= 0) {
                    throw new IllegalStateException(
                            "El registro del adjunto no devolvió "
                                    + "un identificador válido."
                    );
                }
            }

            if (detalles != null) {
                for (int i = 0; i < detalles.size(); i++) {
                    RequerimientoCompraDetalle detalle = detalles.get(i);
                    detalle.setIdRequerimientoCompra(idRequerimiento);
                    RequerimientoCompra vigente = transaccion.bloquearYObtenerRequerimiento(idRequerimiento);
                    prepararDetalleParaGuardar(vigente, null, detalle, transaccion);
                    normalizarDetalleNuevo(detalle);
                    validarTipoPrestacionParaGuardar(vigente, null, detalle, transaccion);
                    validarDetalleParaGuardar(vigente, null, detalle, true, transaccion);
                    validarPrestacionNoRepetida(vigente, null, detalle, transaccion);
                    if (transaccion.guardarDetalle(detalle, normalizarUsuario(usuario)) <= 0) {
                        throw new IllegalStateException("No se obtuvo el identificador del detalle.");
                    }
                }
                List<RequerimientoCompraPresupuesto> guardadas =
                        presupuestoHelper.guardarCotizacionesEmpresa(
                                transaccion, idRequerimiento, cotizaciones, serviceContext,
                                normalizarUsuario(usuario), cotizacionesCreadas);
                if (indiceEmpresaAdjudicada >= 0) {
                    transaccion.guardarEmpresaAdjudicada(idRequerimiento,
                            guardadas.get(indiceEmpresaAdjudicada).getIdRequerimientoPresupuesto(),
                            normalizarUsuario(usuario));
                } else {
                    transaccion.guardarEmpresaAdjudicada(
                            idRequerimiento, empresaCuit, empresaSucursal,
                            normalizarUsuario(usuario));
                }
            }

            commitInvocado = true;
            transaccion.commit();
            return idRequerimiento;

        } catch (Exception e) {
            boolean rollbackConfirmado = transaccion == null;

            if (transaccion != null) {
                try {
                    transaccion.rollback();
                    rollbackConfirmado = !commitInvocado || detalles == null;
                } catch (Exception rollbackError) {
                    _log.error(
                            "No se pudo confirmar el rollback del alta "
                                    + "con adjuntos. No se eliminarán "
                                    + "documentos ante un estado transaccional ambiguo.",
                            rollbackError
                    );
                }
            }

            if (detalles != null && (commitInvocado || !rollbackConfirmado)) {
                _log.error("Resultado transaccional ambiguo del alta de Empresas; se conservan los documentos.", e);
                throw errorUsuario("No se pudo confirmar el resultado del guardado. "
                        + "Consulte el requerimiento antes de reintentar; sus documentos se conservaron.", e);
            }
            boolean compensacionCompleta = true;
            if (rollbackConfirmado
                    && gestorDocumento != null
                    && !documentosCreados.isEmpty()) {
                compensacionCompleta = compensarOrdenesMedicasCreadas(
                        documentosCreados, gestorDocumento);
            }
            if (rollbackConfirmado && !cotizacionesCreadas.isEmpty()) {
                compensacionCompleta = presupuestoHelper.compensarDocumentosAlta(cotizacionesCreadas)
                        && compensacionCompleta;
            }
            if (detalles != null && !compensacionCompleta) {
                _log.error("El alta fue revertida pero no se pudieron compensar todos sus documentos.", e);
                throw errorUsuario("El alta fue revertida. No se pudieron retirar todos los documentos; "
                        + "informe a Sistemas antes de reintentar.", e);
            }

            throw manejarErrorOperacion(
                    "guardar el requerimiento nuevo con adjuntos",
                    "No se pudo guardar el requerimiento con sus adjuntos. "
                            + "Vuelva a seleccionar los archivos e intente nuevamente.",
                    e,
                    "idRequerimiento="
                            + obtenerIdRequerimientoSeguro(requerimiento)
                            + ", usuario=" + usuario
                            + ", cantidadOrdenesMedicas="
                            + (ordenesMedicas != null
                            ? ordenesMedicas.size()
                            : 0)
            );

        } finally {
            if (transaccion != null) {
                transaccion.cerrar();
            }
        }
    }

    public void validarNuevoRequerimientoConCotizacionesEmpresa(
            RequerimientoCompra requerimiento,
            List<RequerimientoCompraDetalle> detalles,
            List<PresupuestoEntrada> cotizaciones,
            ServiceContext serviceContext,
            String empresaCuit,
            String empresaSucursal) throws Exception {
        validarNuevoRequerimientoConCotizacionesEmpresa(requerimiento, detalles,
                cotizaciones, serviceContext, empresaCuit, empresaSucursal, -1);
    }

    public void validarNuevoRequerimientoConCotizacionesEmpresa(
            RequerimientoCompra requerimiento,
            List<RequerimientoCompraDetalle> detalles,
            List<PresupuestoEntrada> cotizaciones,
            ServiceContext serviceContext,
            String empresaCuit,
            String empresaSucursal,
            int indiceEmpresaAdjudicada) throws Exception {

        prepararRequerimientoParaGuardar(requerimiento);
        validarRequerimientoParaGuardar(requerimiento);
        if (requerimiento.getIdRequerimientoCompra() > 0
                || !requerimiento.esSectorSinCotizacionPrestador()) {
            throw errorUsuario("Las cotizaciones del alta corresponden solamente a RRHH o Sistemas.");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw errorUsuario("Debe informar al menos un detalle.");
        }
        for (int i = 0; i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle = detalles.get(i);
            if (detalle == null || detalle.getIdInt() > 0) {
                throw errorUsuario("El alta contiene un detalle persistido o invalido.");
            }
            prepararDetalleParaGuardar(requerimiento, null, detalle);
            validarTipoPrestacionParaGuardar(requerimiento, null, detalle);
            normalizarDetalleNuevo(detalle);
            validarDetalleParaGuardar(requerimiento, null, detalle, false);
        }
        PresupuestoCompraHelper presupuestoHelper = new PresupuestoCompraHelper();
        presupuestoHelper.validarCotizacionesEmpresaAlta(cotizaciones);
        if (cotizaciones != null && !cotizaciones.isEmpty()) {
            DocumentoLibraryComprasHelper.validarContextoDocumentLibrary(serviceContext);
        }
        if (indiceEmpresaAdjudicada < -1
                || (indiceEmpresaAdjudicada >= 0
                    && (cotizaciones == null || indiceEmpresaAdjudicada >= cotizaciones.size()))) {
            throw errorUsuario("La cotizacion adjudicada del alta no es valida.");
        }
        if (indiceEmpresaAdjudicada >= 0) {
            return;
        }
        String cuit = WebKeysCompras.trimToNull(empresaCuit);
        String sucursal = WebKeysCompras.trimToNull(empresaSucursal);
        if (cuit == null && sucursal == null) {
            return;
        }
        presupuestoHelper.obtenerEmpresaActiva(cuit, sucursal, 1);
        boolean encontrada = false;
        for (int i = 0; cotizaciones != null && i < cotizaciones.size(); i++) {
            PresupuestoEntrada entrada = cotizaciones.get(i);
            if (cuit.equals(WebKeysCompras.trimToNull(entrada.getEmpresaCuit()))
                    && sucursal.equals(WebKeysCompras.trimToNull(entrada.getEmpresaSucursal()))) {
                encontrada = true;
            }
        }
        if (!encontrada) {
            throw errorUsuario("La Empresa adjudicada debe tener una cotizacion en este requerimiento.");
        }
    }

    public void guardarEmpresaAdjudicada(
            int idRequerimiento, int idPresupuesto, String usuario) throws Exception {

        validarEmpresaAdjudicada(idRequerimiento, idPresupuesto);
        EditarRequerimientoCompraServiceUtil.guardarEmpresaAdjudicada(
                idRequerimiento, idPresupuesto, normalizarUsuario(usuario));
    }

    public void validarEmpresaAdjudicada(int idRequerimiento, int idPresupuesto) throws Exception {
        RequerimientoCompra actual =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idRequerimiento);
        if (actual == null || !actual.isActivo() || !actual.isPendiente()
                || !actual.esSectorSinCotizacionPrestador()) {
            throw errorUsuario("La adjudicacion solo puede cambiarse en RRHH o Sistemas PENDIENTE.");
        }
        if (idPresupuesto < 0) {
            throw errorUsuario("La cotizacion adjudicada no es valida.");
        }
        if (idPresupuesto == 0) {
            return;
        }
        RequerimientoCompraPresupuesto cotizacion =
                BusquedaRequerimientoCompraServiceUtil.getCotizacionEmpresa(idPresupuesto, idRequerimiento);
        if (cotizacion == null || !cotizacion.isActivo() || !cotizacion.isCotizacionEmpresa()) {
            throw errorUsuario("La Empresa adjudicada debe tener una cotizacion activa en este requerimiento.");
        }
        new PresupuestoCompraHelper().validarDatosEmpresaCotizacion(
                cotizacion.getEmpresaCuit(), cotizacion.getEmpresaSucursal(),
                cotizacion.getDescripcionEmpresa(), 1);
    }

    public void guardarEmpresaAdjudicada(
            int idRequerimiento, String cuit, String sucursal, String usuario) throws Exception {

        validarEmpresaAdjudicada(idRequerimiento, cuit, sucursal);
        EditarRequerimientoCompraServiceUtil.guardarEmpresaAdjudicada(
                idRequerimiento, WebKeysCompras.trimToNull(cuit),
                WebKeysCompras.trimToNull(sucursal), normalizarUsuario(usuario));
    }

    public void validarEmpresaAdjudicada(
            int idRequerimiento, String cuit, String sucursal) throws Exception {

        RequerimientoCompra actual =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idRequerimiento);
        if (actual == null || !actual.isActivo() || !actual.isPendiente()
                || !actual.esSectorSinCotizacionPrestador()) {
            throw errorUsuario("La adjudicacion solo puede cambiarse en RRHH o Sistemas PENDIENTE.");
        }
        cuit = WebKeysCompras.trimToNull(cuit);
        sucursal = WebKeysCompras.trimToNull(sucursal);
        if (cuit == null && sucursal == null) {
            return;
        }
        new PresupuestoCompraHelper().obtenerEmpresaActiva(cuit, sucursal, 1);
        List<RequerimientoCompraPresupuesto> cotizaciones =
                BusquedaRequerimientoCompraServiceUtil.listarCotizacionesEmpresa(idRequerimiento);
        int coincidencias = 0;
        for (int i = 0; cotizaciones != null && i < cotizaciones.size(); i++) {
            RequerimientoCompraPresupuesto cotizacion = cotizaciones.get(i);
            if (cotizacion != null && cotizacion.isActivo() && cotizacion.isCotizacionEmpresa()
                    && cuit.equals(cotizacion.getEmpresaCuit())
                    && sucursal.equals(cotizacion.getEmpresaSucursal())) {
                coincidencias++;
            }
        }
        if (coincidencias != 1) {
            throw errorUsuario("La Empresa adjudicada debe tener una unica cotizacion activa "
                    + "en este requerimiento.");
        }
    }

    public void validarOrdenesMedicasParaAlta(
            List<OrdenMedicaValidada> ordenesMedicas) throws Exception {

        validarOrdenesMedicasParaCarga(
                ordenesMedicas
        );
    }

    public void validarOrdenesMedicasParaCarga(
            List<OrdenMedicaValidada> ordenesMedicas) throws Exception {

        if (ordenesMedicas == null
                || ordenesMedicas.isEmpty()) {

            throw errorUsuario(
                    "Debe seleccionar al menos un adjunto "
                            + "e informar su fecha."
            );
        }

        int maxOrdenes = ConfiguracionComprasHelper.getMaxOrdenesMedicasPorCarga();
        if (ordenesMedicas.size()
                > maxOrdenes) {

            throw errorUsuario(
                    "Se pueden registrar hasta "
                            + maxOrdenes
                            + " adjuntos por operación."
            );
        }

        for (int i = 0;
             i < ordenesMedicas.size();
             i++) {

            OrdenMedicaValidada ordenMedica =
                    ordenesMedicas.get(i);

            if (ordenMedica == null
                    || ordenMedica.getFechaDocumento() == null) {

                throw errorUsuario(
                        "Cada adjunto debe tener "
                                + "informada su fecha."
                );
            }
        }
    }

    public void validarNuevoRequerimientoNoDuplicado(
            RequerimientoCompra requerimiento,
            List<Integer> idsPrestaciones,
            List<OrdenMedicaValidada> ordenesMedicas)
            throws Exception {

        /*
         * Regla exclusiva de alta.
         */
        if (requerimiento == null
                || requerimiento
                .getIdRequerimientoCompra() > 0) {

            return;
        }

        String cuilTitular =
                WebKeysCompras.trimToNull(
                        requerimiento
                                .getAfiliadoCuilTitular()
                );

        Integer integrante =
                requerimiento.getAfiliadoInt();

        /*
         * La ausencia del afiliado se valida posteriormente
         * mediante las reglas generales del requerimiento.
         *
         * Este método solamente controla duplicidad.
         */
        if (cuilTitular == null
                || integrante == null
                || integrante.intValue() < 0) {

            return;
        }

        if (idsPrestaciones == null
                || idsPrestaciones.isEmpty()) {

            return;
        }

        if (ordenesMedicas == null
                || ordenesMedicas.isEmpty()) {

            return;
        }

        validarOrdenesMedicasParaCarga(ordenesMedicas);

        Set<Integer> prestacionesUnicas =
                new HashSet<Integer>();

        for (int i = 0;
             i < idsPrestaciones.size();
             i++) {

            Integer idPrestacion =
                    idsPrestaciones.get(i);

            if (idPrestacion != null
                    && idPrestacion.intValue() > 0) {

                prestacionesUnicas.add(
                        idPrestacion
                );
            }
        }

        if (prestacionesUnicas.isEmpty()) {
            return;
        }

        /*
         * java.sql.Date hereda de java.util.Date.
         *
         * Usando java.util.Date acá aceptamos tanto la fecha
         * validada de la Orden médica como las fechas leídas
         * posteriormente desde otros beans.
         */
        Set<java.util.Date> fechasUnicas =
                new HashSet<java.util.Date>();

        for (int i = 0;
             i < ordenesMedicas.size();
             i++) {

            OrdenMedicaValidada ordenMedica =
                    ordenesMedicas.get(i);

            if (ordenMedica == null
                    || ordenMedica.getFechaDocumento() == null) {

                continue;
            }

            fechasUnicas.add(
                    ordenMedica.getFechaDocumento()
            );
        }

        if (fechasUnicas.isEmpty()) {
            return;
        }

        for (Integer idPrestacion
                : prestacionesUnicas) {

            for (java.util.Date fechaOrdenMedica
                    : fechasUnicas) {

                boolean existeDuplicado =
                        BusquedaRequerimientoCompraServiceUtil
                                .existeRequerimientoDuplicado(
                                        cuilTitular,
                                        integrante.intValue(),
                                        idPrestacion.intValue(),
                                        fechaOrdenMedica,
                                        0
                                );

                if (existeDuplicado) {
                    throw errorUsuario(
                            "Ya existe un requerimiento de compra "
                                    + "para el mismo afiliado, la misma "
                                    + "prestación y la misma fecha "
                                    + "del adjunto."
                    );
                }
            }
        }
    }


    private void asegurarConceptoAutomatico(RequerimientoCompra requerimiento,
            int idRequerimiento, EditarRequerimientoCompraServiceUtil.Transaccion transaccion,
            String usuario) throws Exception {
        int sector = requerimiento.getIdSector().intValue();
        Integer idConcepto = ConfiguracionComprasHelper.resolverConceptoAutomatico(sector,
                transaccion.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_CONCEPTOS_AUTOMATICOS));
        List<RequerimientoCompraDetalle> existentes = transaccion.getDetalles(idRequerimiento);
        if (idConcepto == null) {
            if (existentes != null && !existentes.isEmpty()) {
                throw errorUsuario("La configuracion del concepto automatico no coincide con el SQL instalado.");
            }
            return;
        }
        Integer rubro = ConfiguracionComprasHelper.resolverRubroConceptoAutomatico(sector,
                transaccion.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_RUBROS_CONCEPTO_AUTOMATICO));
        int cantidad = ConfiguracionComprasHelper.resolverCantidadConceptoAutomatico(
                transaccion.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_CANTIDAD_CONCEPTO_AUTOMATICO));
        Nomenclador canonico = transaccion.obtenerNomencladorCanonico(idConcepto.intValue());
        if (canonico == null || canonico.getId_prestacion() != idConcepto.intValue()
                || canonico.getId_tipo_nomenclador() <= 0 || canonico.getBaja_fecha() != null
                || WebKeysCompras.isEmpty(canonico.getCodigo())
                || WebKeysCompras.isEmpty(canonico.getDescripcion())) {
            throw errorUsuario("El concepto automatico configurado no conserva una prestacion activa valida.");
        }
        if (rubro != null) {
            boolean valido = false;
            List<TipoPrestacionCompra> tipos = transaccion.listarTiposPrestacion();
            for (int i = 0; i < tipos.size(); i++) {
                TipoPrestacionCompra tipo = tipos.get(i);
                if (tipo != null && tipo.getIdInt() == rubro.intValue()
                        && tipo.getIdSectorInt() == sector) {
                    valido = true;
                }
            }
            if (!valido) {
                throw errorUsuario("El rubro del concepto automatico no pertenece al sector.");
            }
        }
        if (existentes != null && !existentes.isEmpty()) {
            if (existentes.size() == 1) {
                RequerimientoCompraDetalle existente = existentes.get(0);
                if (existente != null && existente.getIdPrestacionInt() == idConcepto.intValue()
                        && existente.getIdTipoNomencladorInt() == canonico.getId_tipo_nomenclador()
                        && mismoInteger(existente.getIdTipoPrestacion(), rubro)
                        && existente.getCantidad() != null
                        && existente.getCantidad().intValue() == cantidad) {
                    return;
                }
            }
            throw errorUsuario("El SQL instalado genero un concepto automatico diferente al configurado.");
        }
        RequerimientoCompraDetalle automatico = new RequerimientoCompraDetalle();
        automatico.setIdRequerimientoCompra(idRequerimiento);
        automatico.setTipoItem(RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR);
        automatico.setIdTipoPrestacion(rubro);
        automatico.setIdPrestacion(idConcepto);
        automatico.setIdTipoNomenclador(Integer.valueOf(canonico.getId_tipo_nomenclador()));
        automatico.setCodigoNomenclador(canonico.getCodigo().trim());
        automatico.setDescripcionNomenclador(canonico.getDescripcion().trim());
        automatico.setCantidad(Integer.valueOf(cantidad));
        if (transaccion.guardarConceptoAutomatico(automatico, usuario) <= 0) {
            throw new IllegalStateException("El concepto automatico no devolvio un identificador valido.");
        }
    }

    private boolean compensarOrdenesMedicasCreadas(
            List<DocumentoComprasCreado> documentosCreados,
            GestorOrdenMedicaDocumento gestorDocumento) {

        boolean completa = true;
        for (int i = documentosCreados.size() - 1; i >= 0; i--) {
            DocumentoComprasCreado documento = documentosCreados.get(i);

            if (documento == null) {
                continue;
            }

            try {
                gestorDocumento.eliminarDocumento(documento);
            } catch (Exception cleanupError) {
                completa = false;
                _log.error(
                        "No se pudo compensar un adjunto creado "
                                + "después del rollback. fileEntryId="
                                + documento.getFileEntryId(),
                        cleanupError
                );
            }
        }
        return completa;
    }

    public int corregirItem(
            RequerimientoCompraDetalle detalle,
            String usuario) throws Exception {
        Integer idRequerimiento = null;
        EditarRequerimientoCompraServiceUtil.Transaccion transaccion = null;
        try {
            if (detalle == null || detalle.getIdInt() <= 0) {
                throw errorUsuario("Debe seleccionar un detalle existente del requerimiento.");
            }
            idRequerimiento = getIdRequerimientoDetalle(detalle);
            if (idRequerimiento == null || idRequerimiento.intValue() <= 0) {
                throw errorUsuario("Debe informar el requerimiento de compra.");
            }
            transaccion = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            RequerimientoCompra requerimiento = transaccion.bloquearYObtenerRequerimiento(
                    idRequerimiento.intValue());
            if (requerimiento == null || !requerimiento.isActivo()
                    || !ConfiguracionComprasHelper.resolverEstadoPermitido("CORREGIR_ITEM", requerimiento.getEstado(),
                    transaccion.getConfiguracionCompra("COMPRAS_ESTADOS_CORREGIR_ITEM"))) {
                throw errorUsuario("El medicamento o prestación solo puede corregirse antes de generar el RP.");
            }
            if (transaccion.tieneCreacionReclamoPrestacional(requerimiento.getIdRequerimientoCompra())) {
                throw errorUsuario("El requerimiento ya tiene un RP o una creacion de RP en curso.");
            }
            RequerimientoCompraDetalle persistido = obtenerDetallePersistido(
                    requerimiento, detalle.getIdInt());
            if ((!persistido.esNomenclador() && !persistido.esMedicamento())
                    || !RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR.equals(
                            detalle.getTipoItemNormalizado())) {
                throw errorUsuario("Seleccione el medicamento o prestación desde el nomenclador.");
            }
            validarTipoPrestacionParaGuardar(requerimiento, persistido, detalle, transaccion);
            if (detalle.getCantidad() == null || detalle.getCantidad().intValue() <= 0) {
                throw errorUsuario("La cantidad debe ser mayor que cero.");
            }
            detalle.setObservaciones(persistido.getObservaciones());
            detalle.setPrecioUnitarioEstimado(persistido.getPrecioUnitarioEstimado());
            detalle.setPrecioTotalEstimado(persistido.getPrecioTotalEstimadoInformado());
            detalle.setIdPrestador(persistido.getIdPrestador());
            detalle.setTipoItem(RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR);
            detalle.setIdMedicamento(null);
            detalle.setTroquel(null);
            detalle.setNombreMedicamento(null);
            validarDetalleNomencladorParaGuardar(requerimiento, persistido, detalle, transaccion);
            boolean cambio = !persistido.esNomenclador()
                    || !mismoInteger(persistido.getIdPrestacion(), detalle.getIdPrestacion())
                    || !mismoInteger(persistido.getIdTipoPrestacion(), detalle.getIdTipoPrestacion())
                    || !mismoInteger(persistido.getCantidad(), detalle.getCantidad())
                    || !mismoInteger(persistido.getIdTipoNomenclador(), detalle.getIdTipoNomenclador())
                    || !WebKeysCompras.normalizarTexto(persistido.getCodigoNomenclador()).equals(
                            WebKeysCompras.normalizarTexto(detalle.getCodigoNomenclador()))
                    || !WebKeysCompras.normalizarTexto(persistido.getDescripcionNomenclador()).equals(
                            WebKeysCompras.normalizarTexto(detalle.getDescripcionNomenclador()));
            if (!cambio) {
                transaccion.commit();
                return persistido.getIdInt();
            }
            validarPrestacionNoRepetida(requerimiento, persistido, detalle, transaccion);
            List<Integer> comparativa = transaccion.bloquearPrestacionesComparativaCorreccion(
                    requerimiento.getIdRequerimientoCompra());
            if (!mismoInteger(persistido.getIdPrestacion(), detalle.getIdPrestacion())) {
                if (comparativa.contains(detalle.getIdPrestacion())) {
                    throw errorUsuario("La comparativa ya contiene la prestacion de destino. No se modificaron importes.");
                }
                boolean compartida = false;
                for (int i = 0; i < requerimiento.getDetalles().size(); i++) {
                    RequerimientoCompraDetalle otro = requerimiento.getDetalles().get(i);
                    if (otro != null && otro.getIdInt() != persistido.getIdInt()
                            && persistido.getIdPrestacion() != null
                            && mismoInteger(otro.getIdPrestacion(), persistido.getIdPrestacion())) {
                        compartida = true;
                    }
                }
                if (compartida && comparativa.contains(persistido.getIdPrestacion())) {
                    throw errorUsuario("La prestacion anterior esta compartida. No se pueden reasignar sus importes.");
                }
            }
            detalle.setPrecioTotalEstimado(WebKeysCompras.calcularPrecioTotal(
                    detalle.getCantidad(), persistido.getPrecioUnitarioEstimado()));
            transaccion.actualizarReferenciaComparativaCorreccion(
                    requerimiento.getIdRequerimientoCompra(), persistido.getIdPrestacion(),
                    detalle.getIdPrestacionInt(), normalizarUsuario(usuario));
            int idGuardado = transaccion.corregirItem(
                    detalle, normalizarUsuario(usuario));
            if (idGuardado != persistido.getIdInt()) {
                throw new IllegalStateException("La correccion no devolvio el mismo detalle del requerimiento.");
            }
            if (persistido.getIdPrestador() != null) {
                validarPrestadorCorreccion(transaccion.bloquearYObtenerRequerimiento(
                        requerimiento.getIdRequerimientoCompra()), persistido.getIdPrestador().intValue(), transaccion);
            }
            transaccion.commit();
            return idGuardado;
        } catch (Exception e) {
            if (transaccion != null) {
                try {
                    transaccion.rollback();
                } catch (Exception rollbackError) {
                    e.addSuppressed(rollbackError);
                }
            }
            throw manejarErrorOperacion("corregir el medicamento o prestación",
                    "No se pudo corregir el medicamento o prestación. Actualice la pantalla e intente nuevamente.",
                    e, construirContextoDetalle(detalle, idRequerimiento, usuario));
        } finally {
            if (transaccion != null) {
                transaccion.cerrar();
            }
        }
    }


    private static final String CONFIG_EXCEPCIONES_PRESTADOR_REQUERIMIENTO =
            "COMPRAS_EXCEPCIONES_PRESTADOR_REQUERIMIENTO";

    private void validarPrestadorCorreccion(RequerimientoCompra requerimiento, int idPrestador,
            EditarRequerimientoCompraServiceUtil.Transaccion transaccion) throws Exception {
        java.util.Map<String,Object> prestador=transaccion.consultarPrestadorCorreccion(idPrestador);
        List<Map<String,Object>> rubrosReq=new ArrayList<Map<String,Object>>();
        List<TipoPrestacionCompra> tipos=transaccion.listarTiposPrestacion();
        for(int i=0;i<requerimiento.getDetalles().size();i++){
            RequerimientoCompraDetalle d=requerimiento.getDetalles().get(i);
            for(int j=0;j<tipos.size();j++)if(mismoInteger(d.getIdTipoPrestacion(),tipos.get(j).getId())){
                Map<String,Object> fila=new HashMap<String,Object>();fila.put("descripcion",tipos.get(j).getDescripcion());
                // getDetalles retorna exclusivamente detalles activos.
                fila.put("baja_fecha",null);rubrosReq.add(fila);
            }
        }
        List<Map<String,Object>> rubrosPrestador=new ArrayList<Map<String,Object>>();
        List<String> rubros=transaccion.listarRubrosPrestadorCorreccion(idPrestador);
        for(int i=0;i<rubros.size();i++){Map<String,Object> f=new HashMap<String,Object>();f.put("rubro",rubros.get(i));rubrosPrestador.add(f);}
        if(!NotificarCotizacionPrestadorHelper.esCompatiblePrestadorCotizacion(requerimiento,prestador,rubrosReq,rubrosPrestador,
                transaccion.getConfiguracionCompra(CONFIG_EXCEPCIONES_PRESTADOR_REQUERIMIENTO)))
            throw errorUsuario("El prestador adjudicado no es compatible con las prestaciones activas. No se modificaron importes.");
    }

    public int guardarDetalle(RequerimientoCompraDetalle detalle, String usuario) throws Exception {
        Integer idRequerimiento = null;
        EditarRequerimientoCompraServiceUtil.Transaccion transaccion = null;
        try {
            if (detalle == null) {
                throw errorUsuario("Debe informar el detalle del requerimiento.");
            }
            idRequerimiento = getIdRequerimientoDetalle(detalle);
            if (idRequerimiento == null || idRequerimiento.intValue() <= 0) {
                throw errorUsuario("Primero debe guardar los datos generales del requerimiento.");
            }
            transaccion = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            RequerimientoCompra requerimiento = transaccion.bloquearYObtenerRequerimiento(
                    idRequerimiento.intValue());
            if (requerimiento == null || !requerimiento.isActivo()
                    || requerimiento.getEstado() != estadoEnTransaccion(transaccion, "PENDIENTE")) {
                throw errorUsuario("Los detalles solo pueden modificarse en estado PENDIENTE.");
            }
            RequerimientoCompraDetalle persistido = obtenerDetallePersistido(requerimiento, detalle.getIdInt());
            prepararDetalleParaGuardar(requerimiento, persistido, detalle, transaccion);
            validarTipoPrestacionParaGuardar(requerimiento, persistido, detalle, transaccion);
            normalizarDetalleNuevo(detalle);
            validarDetalleParaGuardar(requerimiento, persistido, detalle, true, transaccion);
            validarPrestacionNoRepetida(requerimiento, persistido, detalle, transaccion);
            if (persistido == null) {
                validarDetalleNuevoNoGeneraDuplicado(requerimiento, detalle, transaccion);
            }
            detalle.setObservaciones(emptyToNull(detalle.getObservaciones()));
            int idGuardado = transaccion.guardarDetalle(detalle, normalizarUsuario(usuario));
            if (idGuardado <= 0) {
                throw new IllegalStateException("El guardado del detalle no devolvio un ID valido.");
            }
            transaccion.commit();
            return idGuardado;
        } catch (Exception e) {
            if (transaccion != null) {
                try {
                    transaccion.rollback();
                } catch (Exception rollbackError) {
                    e.addSuppressed(rollbackError);
                }
            }
            throw manejarErrorOperacion("guardar el detalle del requerimiento",
                    "No se pudo guardar el detalle. Revise la informacion e intente nuevamente.",
                    e, construirContextoDetalle(detalle, idRequerimiento, usuario));
        } finally {
            if (transaccion != null) {
                transaccion.cerrar();
            }
        }
    }

    private void validarDetalleNuevoNoGeneraDuplicado(RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle) throws Exception {
        validarDetalleNuevoNoGeneraDuplicado(requerimiento, detalle, null);
    }

    private void validarDetalleNuevoNoGeneraDuplicado(
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle,
            EditarRequerimientoCompraServiceUtil.Transaccion transaccion) throws Exception {

        if (requerimiento == null
                || detalle == null) {

            return;
        }

        Integer idPrestacion =
                detalle.getIdPrestacion();

        /*
         * MEDICAMENTO y OBSERVACION no participan
         * en esta regla.
         */
        if (idPrestacion == null
                || idPrestacion.intValue() <= 0) {

            return;
        }

        if (!requerimiento
                .tieneAfiliadoInformado()) {

            return;
        }

        String cuilTitular =
                WebKeysCompras.trimToNull(
                        requerimiento
                                .getAfiliadoCuilTitular()
                );

        Integer integrante =
                requerimiento.getAfiliadoInt();

        if (cuilTitular == null
                || integrante == null
                || integrante.intValue() < 0) {

            return;
        }

        int idRequerimientoCompra =
                requerimiento
                        .getIdRequerimientoCompra();

        if (idRequerimientoCompra <= 0) {
            return;
        }

        List<RequerimientoCompraPresupuesto> ordenesMedicas = transaccion != null
                ? transaccion.listarOrdenesMedicas(idRequerimientoCompra)
                : BusquedaRequerimientoCompraServiceUtil.listarOrdenesMedicas(idRequerimientoCompra);

        if (ordenesMedicas == null
                || ordenesMedicas.isEmpty()) {

            return;
        }

        /*
         * RequerimientoCompraPresupuesto#getFechaDocumento()
         * devuelve java.util.Date.
         *
         * Por eso deliberadamente NO usamos java.sql.Date
         * en la capa funcional.
         */
        Set<java.util.Date> fechasUnicas =
                new HashSet<java.util.Date>();

        for (int i = 0;
             i < ordenesMedicas.size();
             i++) {

            RequerimientoCompraPresupuesto ordenMedica =
                    ordenesMedicas.get(i);

            if (ordenMedica == null
                    || ordenMedica.getBajaFecha() != null
                    || ordenMedica.getFechaDocumento() == null) {

                continue;
            }

            fechasUnicas.add(
                    ordenMedica.getFechaDocumento()
            );
        }

        for (java.util.Date fechaOrdenMedica
                : fechasUnicas) {

            boolean existeDuplicado = transaccion != null
                    ? transaccion.existeRequerimientoDuplicado(cuilTitular, integrante.intValue(),
                            idPrestacion.intValue(), fechaOrdenMedica, idRequerimientoCompra)
                    : BusquedaRequerimientoCompraServiceUtil.existeRequerimientoDuplicado(
                            cuilTitular, integrante.intValue(), idPrestacion.intValue(),
                            fechaOrdenMedica, idRequerimientoCompra);

            if (existeDuplicado) {
                throw errorUsuario(
                        "Ya existe otro requerimiento de compra "
                                + "para el mismo afiliado, la misma "
                                + "prestación y la misma fecha "
                                + "del adjunto."
                );
            }
        }
    }

    /**
     * Contrato canónico utilizado por los Actions: valida estado y pertenencia
     * antes de ejecutar el borrado persistente.
     */
    public void borrarDetalle(int id,int detalle,String usuario)throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx=null;
        try{
            if(id<=0||detalle<=0)throw errorUsuario("Debe informar el detalle del requerimiento.");
            tx=EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            RequerimientoCompra r=tx.bloquearYObtenerRequerimiento(id);
            if(r==null||!r.isActivo()||!ConfiguracionComprasHelper.resolverEstadoPermitido("ELIMINAR_DETALLE",r.getEstado(),
                    tx.getConfiguracionCompra("COMPRAS_ESTADOS_ELIMINAR_DETALLE")))
                throw errorUsuario("Los detalles solo pueden quitarse en estado PENDIENTE o ENVIADO A COTIZAR.");
            obtenerDetallePersistido(r,detalle);
            if(r.getEstado()==estadoEnTransaccion(tx,"A_COTIZAR")&&r.getDetalles().size()<=1)
                throw errorUsuario("El requerimiento ENVIADO A COTIZAR debe conservar al menos una prestacion.");
            if(tx.persistirBajaDetalle(id,detalle,normalizarUsuario(usuario))!=1)
                throw errorUsuario("El detalle cambio mientras se quitaba. Actualice la pantalla.");
            tx.commit();
        }catch(Exception e){
            if(tx!=null)try{tx.rollback();}catch(Exception fallo){e.addSuppressed(fallo);}
            throw manejarErrorOperacion("quitar el detalle del requerimiento","No se pudo quitar el detalle. Actualice la pantalla e intente nuevamente.",e,"idRequerimiento="+id+", idDetalle="+detalle);
        }finally{if(tx!=null)tx.cerrar();}
    }

    public void cambiarEstado(int id, int nuevo, String usuario) throws Exception {
        if (nuevo == ConfiguracionComprasHelper.getEstadoOrdenCompra()) {
            pasarAOrdenCompra(id, usuario);
            return;
        }
        EditarRequerimientoCompraServiceUtil.Transaccion tx = null;
        try {
            tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            cambiarEstadoEnTransaccion(tx, id, nuevo, normalizarUsuario(usuario));
            tx.commit();
        } catch (Exception e) {
            if (tx != null) try { tx.rollback(); } catch (Exception fallo) { e.addSuppressed(fallo); }
            throw manejarErrorOperacion("actualizar el estado del requerimiento",
                    "No se pudo actualizar el estado del requerimiento. Actualice la pantalla e intente nuevamente.",
                    e, "idRequerimiento=" + id + ", idEstadoNuevo=" + nuevo);
        } finally { if (tx != null) tx.cerrar(); }
    }

    public void cambiarEstadoEnTransaccion(EditarRequerimientoCompraServiceUtil.Transaccion tx,
            int id, int nuevo, String usuario) throws Exception {
        if (id <= 0 || tx == null) throw errorUsuario("Debe informar el requerimiento de compra.");
        RequerimientoCompra r = tx.bloquearYObtenerRequerimiento(id);
        if (r == null || !r.isActivo()) throw errorUsuario("El requerimiento ya no esta disponible.");
        String estados = tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS);
        if (nuevo == ConfiguracionComprasHelper.resolverEstado("ANULADO", estados)) {
            anularEnTransaccion(tx, r, "", usuario, estados);
            return;
        }
        boolean transicionPermitida = ConfiguracionComprasHelper.resolverTransicionEstado(r.getEstado(), nuevo,
                tx.getConfiguracionCompra("COMPRAS_TRANSICIONES_ESTADO"), estados);
        boolean autorizacionEmpresaPendiente = false;
        if (nuevo == ConfiguracionComprasHelper.resolverEstado("ORDEN_COMPRA", estados)
                || nuevo == ConfiguracionComprasHelper.resolverEstado("RECLAMO_RP", estados)
                || nuevo == ConfiguracionComprasHelper.resolverEstado("A_COTIZAR", estados)
                || nuevo == ConfiguracionComprasHelper.resolverEstado("COTIZADO", estados)) {
            throw errorUsuario("El estado solicitado requiere completar su operacion especifica.");
        }
        boolean autorizacion = nuevo == ConfiguracionComprasHelper.resolverEstado("PARA_AUTORIZAR", estados)
                || nuevo == ConfiguracionComprasHelper.resolverEstado("AUTORIZADO", estados);
        if (autorizacion) {
            java.util.Map<String,Object> sector = tx.obtenerSectorCatalogo(r.getIdSector().intValue());
            if (sector == null || !Boolean.TRUE.equals(sector.get("activo")) || sector.get("baja") != null) {
                throw errorUsuario("El sector del requerimiento ya no esta disponible.");
            }
            boolean empresa = ConfiguracionComprasHelper.resolverSectorSinCotizacionPrestador(
                    (String) sector.get("descripcion"), tx.getConfiguracionCompra("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR"));
            int esperado = nuevo == ConfiguracionComprasHelper.resolverEstado("AUTORIZADO", estados)
                    ? ConfiguracionComprasHelper.resolverEstado("PARA_AUTORIZAR", estados)
                    : ConfiguracionComprasHelper.resolverEstado(empresa ? "PENDIENTE" : "RECLAMO_RP", estados);
            if (r.getEstado() != esperado) throw errorUsuario("El requerimiento cambio de estado. Actualice la pantalla.");
            if (empresa) {
                validarEmpresaAdjudicadaParaConfirmacion(r, tx);
                autorizacionEmpresaPendiente = nuevo == ConfiguracionComprasHelper.resolverEstado("PARA_AUTORIZAR", estados)
                        && r.getEstado() == ConfiguracionComprasHelper.resolverEstado("PENDIENTE", estados);
            } else {
                java.util.List<java.util.Map<String,Object>> relaciones = tx.obtenerRelacionesRp(id);
                if (relaciones.size() != 1) throw errorUsuario("Debe existir un Reclamo Prestacional vinculado y activo.");
                java.util.Map<String,Object> f = relaciones.get(0);
                if (!WebKeysCompras.VINCULO_RECLAMO_VINCULADO.equals(f.get("estado"))
                        || ((Integer) f.get("id")).intValue() <= 0 || !f.get("id").equals(f.get("rp_id"))
                        || f.get("baja") != null || f.get("vinculo_baja") != null) {
                    throw errorUsuario("Debe existir un Reclamo Prestacional vinculado y activo.");
                }
            }
        }
        if (!transicionPermitida && !autorizacionEmpresaPendiente) {
            throw errorUsuario("El requerimiento no puede pasar al estado seleccionado desde su estado actual.");
        }
        if (tx.persistirEstado(id, r.getEstado(), nuevo, usuario, null, false) != 1) {
            throw errorUsuario("El estado cambio mientras se guardaba. Actualice la pantalla.");
        }
    }

    public void anularRequerimiento(int id, String motivo, String usuario) throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx = null;
        try {
            if (id <= 0) throw errorUsuario("Debe informar el requerimiento de compra.");
            tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            RequerimientoCompra r = tx.bloquearYObtenerRequerimiento(id);
            if (r == null || !r.isActivo()) throw errorUsuario("El requerimiento ya no esta disponible.");
            anularEnTransaccion(tx, r, motivo, normalizarUsuario(usuario),
                    tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS));
            tx.commit();
        } catch (Exception e) {
            if (tx != null) try { tx.rollback(); } catch (Exception fallo) { e.addSuppressed(fallo); }
            throw manejarErrorOperacion("anular el requerimiento",
                    "No se pudo anular el requerimiento. Actualice la pantalla e intente nuevamente.", e, "idRequerimiento=" + id);
        } finally { if (tx != null) tx.cerrar(); }
    }

    private void anularEnTransaccion(EditarRequerimientoCompraServiceUtil.Transaccion tx,
            RequerimientoCompra r, String motivo, String usuario, String estados) throws Exception {
        int anulado = ConfiguracionComprasHelper.resolverEstado("ANULADO", estados);
        boolean transicionPermitida = ConfiguracionComprasHelper.resolverTransicionEstado(r.getEstado(), anulado,
                tx.getConfiguracionCompra("COMPRAS_TRANSICIONES_ESTADO"), estados);
        boolean estadoPermitido = ConfiguracionComprasHelper.resolverEstadoPermitido("ANULAR", r.getEstado(),
                tx.getConfiguracionCompra("COMPRAS_ESTADOS_ANULAR"));
        boolean requiereBajaRp = r.getEstado() == ConfiguracionComprasHelper.resolverEstado("RECLAMO_RP", estados);
        java.util.List<java.util.Map<String,Object>> relaciones = tx.obtenerRelacionesRp(r.getIdRequerimientoCompra());
        if (requiereBajaRp && relaciones.isEmpty()) throw errorUsuario("Debe dar de baja el Reclamo Prestacional antes de anular.");
        for (int i = 0; i < relaciones.size(); i++) {
            java.util.Map<String,Object> f = relaciones.get(i);
            if (!WebKeysCompras.VINCULO_RECLAMO_BAJA.equals(f.get("estado")) || f.get("vinculo_baja") == null
                    || ((Integer) f.get("id")).intValue() <= 0 || !f.get("id").equals(f.get("rp_id")) || f.get("baja") == null) {
                throw errorUsuario("Debe dar de baja el Reclamo Prestacional antes de anular.");
            }
        }
        if (!requiereBajaRp && (!transicionPermitida || !estadoPermitido)) {
            throw errorUsuario("El requerimiento no puede anularse desde su estado actual.");
        }
        if (tx.persistirEstado(r.getIdRequerimientoCompra(), r.getEstado(), anulado, usuario,
                motivo != null ? motivo : "", true) != 1) {
            throw errorUsuario("El requerimiento cambio mientras se anulaba. Actualice la pantalla.");
        }
    }

    private int estadoEnTransaccion(EditarRequerimientoCompraServiceUtil.Transaccion tx, String codigo) throws Exception {
        return ConfiguracionComprasHelper.resolverEstado(codigo,
                tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS));
    }

    public int pasarAOrdenCompra(int id, String usuario) throws Exception {
        new RequerimientoCompraOrdenHelper().crearDirecta(id, usuario, normalizarUsuario(usuario));
        return ConfiguracionComprasHelper.getEstadoOrdenCompra();
    }

    private void validarEmpresaAdjudicadaParaConfirmacion(RequerimientoCompra requerimiento) throws Exception {
        validarEmpresaAdjudicadaParaConfirmacion(requerimiento, null);
    }

    private void validarEmpresaAdjudicadaParaConfirmacion(RequerimientoCompra requerimiento,
            EditarRequerimientoCompraServiceUtil.Transaccion tx) throws Exception {

        if (!requerimiento.tieneDetalles()) {
            throw errorUsuario("Debe agregar al menos un detalle antes de enviar "
                    + "a autorizar o Crear Orden de Compra.");
        }
        List<RequerimientoCompraPresupuesto> cotizaciones =
                tx != null ? tx.listarDocumentos(requerimiento.getIdRequerimientoCompra(), 3)
                        : BusquedaRequerimientoCompraServiceUtil.listarCotizacionesEmpresa(requerimiento.getIdRequerimientoCompra());
        RequerimientoCompraPresupuesto adjudicada = null;
        for (int i = 0; cotizaciones != null && i < cotizaciones.size(); i++) {
            RequerimientoCompraPresupuesto cotizacion = cotizaciones.get(i);
            if (cotizacion != null && cotizacion.isActivo()
                    && cotizacion.isCotizacionEmpresa() && cotizacion.isEmpresaAdjudicada()) {
                if (adjudicada != null) {
                    throw errorUsuario("Debe existir exactamente una Empresa adjudicada.");
                }
                adjudicada = cotizacion;
            }
        }
        if (adjudicada == null) {
            throw errorUsuario("Debe seleccionar una Empresa adjudicada con cotizacion activa "
                    + "antes de enviar a autorizar o Crear Orden de Compra.");
        }
        if (adjudicada.getIdRequerimiento() == null
                || adjudicada.getIdRequerimiento().intValue()
                != requerimiento.getIdRequerimientoCompra()) {
            throw errorUsuario("La cotizacion adjudicada no pertenece al requerimiento.");
        }
        new PresupuestoCompraHelper().validarDatosEmpresaCotizacion(tx,
                adjudicada.getEmpresaCuit(), adjudicada.getEmpresaSucursal(),
                adjudicada.getDescripcionEmpresa(), 1);
    }

    public NotificacionCotizacionResultado enviarACotizar(
            int idRequerimientoCompra,
            String usuario,
            long companyId,
            ServiceContext serviceContext) throws Exception {

        try {
            RequerimientoCompra requerimiento =
                    validarRequerimientoParaEnviarACotizar(
                            idRequerimientoCompra
                    );

            NotificacionCotizacionResultado resultado =
                    notificacionHelper.notificarPrestadores(
                            requerimiento
                                    .getIdRequerimientoCompra(),
                            usuario,
                            companyId,
                            serviceContext
                    );

            if (resultado == null) {
                throw new IllegalStateException(
                        "El proceso de notificación "
                                + "no devolvió resultado."
                );
            }

            int estadoFinal =
                    EditarRequerimientoCompraServiceUtil.confirmarEnvioACotizar(
                            idRequerimientoCompra,
                            normalizarUsuario(
                                    usuario
                            )
                    );

            if (estadoFinal
                    != ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getEstadoPendiente()
                    && estadoFinal
                    != ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getEstadoACotizar()) {

                throw new IllegalStateException(
                        "Estado inesperado al confirmar "
                                + "el envío: "
                                + estadoFinal
                );
            }

            return resultado;

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "enviar el requerimiento a cotizar",
                    "No se pudo enviar el requerimiento a cotizar. "
                            + "Verifique los datos e intente nuevamente.",
                    e,
                    "idRequerimiento="
                            + idRequerimientoCompra
                            + ", companyId="
                            + companyId
                            + ", usuario="
                            + usuario
            );
        }
    }

    public NotificacionCotizacionResultado reintentarNotificacionesCotizacion(
            int idRequerimientoCompra,
            String usuario,
            long companyId,
            ServiceContext serviceContext)
            throws Exception {

        try {
            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

            RequerimientoCompra requerimiento =
                    BusquedaRequerimientoCompraServiceUtil
                            .getRequerimientoCompra(
                                    idRequerimientoCompra
                            );

            if (requerimiento == null) {
                throw errorUsuario(
                        "El requerimiento ya no está disponible."
                );
            }

            if (!requerimiento
                    .puedeReintentarNotificaciones()) {

                throw errorUsuario(
                        "Las notificaciones solo pueden reenviarse "
                                + "mientras el requerimiento esta en "
                                + "estado ENVIADO A COTIZAR."
                );
            }

            if (!BusquedaRequerimientoCompraServiceUtil
                    .hayPrestadoresPendientesNotificacion(
                            idRequerimientoCompra
                    )) {

                return new NotificacionCotizacionResultado();
            }

            return notificacionHelper
                    .notificarPrestadores(
                            idRequerimientoCompra,
                            usuario,
                            companyId,
                            serviceContext
                    );

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "reenviar las notificaciones de cotización",
                    "No se pudieron reenviar las notificaciones pendientes. "
                            + "Intente nuevamente.",
                    e,
                    "idRequerimiento="
                            + idRequerimientoCompra
                            + ", companyId="
                            + companyId
                            + ", usuario="
                            + usuario
            );
        }
    }

    public GuardadoCotizacionResultado guardarAvanceCotizacion(
            int idRequerimientoCompra,
            List<RequerimientoCompraDetalle> detalles,
            String usuario) throws Exception {

        return guardarCotizacion(
                idRequerimientoCompra,
                detalles,
                new ArrayList<Integer>(),
                null,
                usuario
        );
    }

    public GuardadoCotizacionResultado guardarAvanceCotizacion(
            int idRequerimientoCompra,
            List<RequerimientoCompraDetalle> detalles,
            List<Integer> idsDetallesEliminados,
            boolean surge,
            String usuario) throws Exception {

        return guardarCotizacion(
                idRequerimientoCompra,
                detalles,
                idsDetallesEliminados,
                surge,
                usuario
        );
    }

    public GuardadoCotizacionResultado cerrarCotizacion(
            int idRequerimientoCompra,
            List<RequerimientoCompraDetalle> detalles,
            String usuario) throws Exception {

        return guardarCotizacion(
                idRequerimientoCompra,
                detalles,
                new ArrayList<Integer>(),
                null,
                usuario
        );
    }

    public int registrarPresupuesto(
            RequerimientoCompraPresupuesto presupuesto,
            String usuario) throws Exception {

        try {
            validarPresupuestoParaRegistrar(presupuesto);

            int id =
                    EditarRequerimientoCompraServiceUtil.registrarPresupuesto(
                            presupuesto,
                            normalizarUsuario(usuario)
                    );

            if (id <= 0) {
                throw new IllegalStateException(
                        "El registro del presupuesto no devolvió un ID válido."
                );
            }

            return id;

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "registrar el presupuesto",
                    "No se pudo registrar el presupuesto. "
                            + "Vuelva a seleccionar el archivo e intente nuevamente.",
                    e,
                    construirContextoPresupuesto(
                            presupuesto,
                            usuario
                    )
            );
        }
    }

    public boolean darDeBajaPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra,
            String usuario) throws Exception {

        try {
            if (idRequerimientoPresupuesto <= 0) {
                throw errorUsuario(
                        "Debe informar el presupuesto que desea quitar."
                );
            }

            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

            return EditarRequerimientoCompraServiceUtil.darDeBajaPresupuesto(
                    idRequerimientoPresupuesto,
                    idRequerimientoCompra,
                    normalizarUsuario(usuario)
            );

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "quitar el presupuesto",
                    "No se pudo quitar el presupuesto. "
                            + "Actualice la pantalla e intente nuevamente.",
                    e,
                    "idRequerimientoPresupuesto="
                            + idRequerimientoPresupuesto
                            + ", idRequerimiento="
                            + idRequerimientoCompra
                            + ", usuario=" + usuario
            );
        }
    }

    public boolean reactivarPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        try {
            if (idRequerimientoPresupuesto <= 0) {
                throw errorUsuario(
                        "Debe informar el presupuesto que desea reactivar."
                );
            }

            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

            return EditarRequerimientoCompraServiceUtil.reactivarPresupuesto(
                    idRequerimientoPresupuesto,
                    idRequerimientoCompra
            );

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "reactivar el presupuesto",
                    "No se pudo reactivar el presupuesto. "
                            + "Actualice la pantalla e intente nuevamente.",
                    e,
                    "idRequerimientoPresupuesto="
                            + idRequerimientoPresupuesto
                            + ", idRequerimiento="
                            + idRequerimientoCompra
            );
        }
    }

    public boolean darDeBajaCotizacionEmpresa(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra,
            String usuario) throws Exception {

        try {
            if (idRequerimientoPresupuesto <= 0
                    || idRequerimientoCompra <= 0) {

                throw errorUsuario(
                        "Debe informar la cotización de Empresa "
                                + "y su requerimiento."
                );
            }

            return EditarRequerimientoCompraServiceUtil.darDeBajaCotizacionEmpresa(
                    idRequerimientoPresupuesto,
                    idRequerimientoCompra,
                    normalizarUsuario(usuario)
            );

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "quitar la cotización de Empresa",
                    "No se pudo quitar la cotización de Empresa. "
                            + "Actualice la pantalla e intente nuevamente.",
                    e,
                    "idRequerimientoPresupuesto="
                            + idRequerimientoPresupuesto
                            + ", idRequerimiento="
                            + idRequerimientoCompra
                            + ", usuario=" + usuario
            );
        }
    }

    public boolean reactivarCotizacionEmpresa(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        try {
            if (idRequerimientoPresupuesto <= 0
                    || idRequerimientoCompra <= 0) {

                throw errorUsuario(
                        "Debe informar la cotización de Empresa "
                                + "y su requerimiento."
                );
            }

            return EditarRequerimientoCompraServiceUtil.reactivarCotizacionEmpresa(
                    idRequerimientoPresupuesto,
                    idRequerimientoCompra
            );

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "reactivar la cotización de Empresa",
                    "No se pudo reactivar la cotización de Empresa. "
                            + "Actualice la pantalla e intente nuevamente.",
                    e,
                    "idRequerimientoPresupuesto="
                            + idRequerimientoPresupuesto
                            + ", idRequerimiento="
                            + idRequerimientoCompra
            );
        }
    }

    public void prepararRequerimientoParaGuardar(RequerimientoCompra requerimiento) throws Exception {
        prepararRequerimientoParaGuardar(requerimiento, null);
    }

    private void prepararRequerimientoParaGuardar(RequerimientoCompra requerimiento,
            EditarRequerimientoCompraServiceUtil.Transaccion tx) throws Exception {

        if (requerimiento == null) {
            return;
        }

        Integer idSector = requerimiento.getIdSector();

        if (idSector != null && idSector.intValue() > 0) {
            RequerimientoCompraSector sector;
            if (tx != null) {
                java.util.Map<String,Object> fila=null;
                if(requerimiento.getIdRequerimientoCompra()<=0){
                    List<java.util.Map<String,Object>> catalogo=tx.listarCatalogoSectores();
                    List<RequerimientoCompraSector> seleccionables=NomencladorCompraBusquedaHelper.filtrarSectoresSeleccionables(catalogo,
                            tx.getConfiguracionCompra("COMPRAS_SECTORES_NO_SELECCIONABLES"));
                    boolean habilitado=false;
                    for(int i=0;i<seleccionables.size();i++)if(seleccionables.get(i).getIdSector()==idSector.intValue())habilitado=true;
                    if(!habilitado)throw errorUsuario("El sector informado no esta habilitado para nuevas compras.");
                    for(int i=0;i<catalogo.size();i++)if(idSector.equals(catalogo.get(i).get("id")))fila=catalogo.get(i);
                }else fila=tx.obtenerSectorCatalogo(idSector.intValue());
                if (fila == null || !Boolean.TRUE.equals(fila.get("activo")) || fila.get("baja") != null) {
                    throw errorUsuario("El sector seleccionado ya no existe o no esta disponible.");
                }
                sector = new RequerimientoCompraSector(idSector, (String) fila.get("descripcion"), (Boolean) fila.get("requiere_afiliado"));
            } else sector = BusquedaRequerimientoCompraServiceUtil.getSector(idSector.intValue());
            if (sector == null || sector.getIdSector() <= 0) throw errorUsuario("El sector seleccionado ya no esta disponible.");
            requerimiento.setSectorDescripcion(sector.getDescripcion());
            requerimiento.setRequiereAfiliado(sector.isRequiereAfiliado());

            if (!sector.isRequiereAfiliado()) {
                aplicarReglaSectorSinAfiliado(requerimiento);
                return;
            }
        }

        /*
         * El snapshot del afiliado se captura exclusivamente durante el alta.
         *
         * En requerimientos existentes la identidad y los datos asociados
         * al afiliado son inmutables.
         */
        if (tx == null && requerimiento.getIdRequerimientoCompra() <= 0
                && requerimiento.tieneAfiliadoInformado()) {

            cargarSnapshotAfiliado(
                    requerimiento
            );
        }

        int cargoTercerizadora =
                requerimiento.getCargoTercerizadora() != null
                        ? requerimiento.getCargoTercerizadora().intValue()
                        : 0;

        requerimiento.setRecupero(
                cargoTercerizadora > 0
        );
    }

    private void validarRequerimientoParaGuardar(
            RequerimientoCompra requerimiento) throws Exception {

        if (requerimiento == null) {
            throw errorUsuario(
                    "Debe informar el requerimiento de compra."
            );
        }

        if (requerimiento.getIdSector() == null
                || requerimiento.getIdSector().intValue() <= 0) {

            throw errorUsuario(
                    "Debe seleccionar el sector del requerimiento."
            );
        }

        if (!requerimiento.isRequiereAfiliado()) {
            aplicarReglaSectorSinAfiliado(requerimiento);
        }

        if(requerimiento.getCargoOspim()==null)requerimiento.setCargoOspim(Integer.valueOf(0));
        if(requerimiento.getCargoTercerizadora()==null)requerimiento.setCargoTercerizadora(Integer.valueOf(0));

        validarPorcentaje(
                requerimiento.getCargoOspim(),
                "El cargo de OSPIM"
        );
        validarPorcentaje(
                requerimiento.getCargoTercerizadora(),
                "El cargo de la tercerizadora"
        );

        int cargoOspim =
                requerimiento.getCargoOspim() != null
                        ? requerimiento.getCargoOspim().intValue()
                        : 0;

        int cargoTercerizadora =
                requerimiento.getCargoTercerizadora() != null
                        ? requerimiento.getCargoTercerizadora().intValue()
                        : 0;

        if (cargoOspim + cargoTercerizadora != 100) {
            throw errorUsuario(
                    "La suma de los cargos de OSPIM y la tercerizadora "
                            + "debe ser exactamente 100 %."
            );
        }

        requerimiento.setRecupero(
                cargoTercerizadora > 0
        );

        if (requerimiento.isRequiereAfiliado()
                && cargoTercerizadora > 0
                && WebKeysCompras.isEmpty(
                requerimiento.getIdTercerizadora()
        )) {

            throw errorUsuario(
                    "Debe seleccionar una tercerizadora cuando su cargo "
                            + "es mayor que cero."
            );
        }

        if (requerimiento.isRequiereAfiliado()) {
            if (WebKeysCompras.isEmpty(
                    requerimiento.getAfiliadoCuilTitular()
            )) {
                throw errorUsuario(
                        "Debe informar el CUIL del titular afiliado."
                );
            }

        if (requerimiento.getAfiliadoInt() == null
                || requerimiento.getAfiliadoInt().intValue() < 0) {

            throw errorUsuario(
                    "Debe informar el integrante del grupo familiar."
            );
        }
    }
}

private void cargarSnapshotAfiliado(
        RequerimientoCompra requerimiento) throws Exception {

    List<Afiliado> afiliados =
            BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(
                    requerimiento.getAfiliadoCuilTitular(),
                    requerimiento.getAfiliadoIntString(),
                    null,
                    null,
                    0,
                    null,
                    null,
                    WebKeysGlobal.ID_DEFAULT_ENTIDAD,
                    0,
                    0,
                    new BigDecimal(0)
            );

    if (afiliados == null || afiliados.size() != 1) {
        throw errorUsuario(
                "No se pudo obtener un único afiliado para guardar el requerimiento."
        );
    }

    Afiliado afiliado = afiliados.get(0);

    requerimiento.setAfiliadoIdOspim(afiliado.getId_ospim());
    requerimiento.setAfiliadoNombre(afiliado.getNombre());
    requerimiento.setAfiliadoApellido(afiliado.getApellido());
    requerimiento.setAfiliadoDocumentoTipo(afiliado.getDocumento_tipo());
    requerimiento.setAfiliadoDocumentoNro(afiliado.getDocu_numero());
    requerimiento.setAfiliadoEmail(afiliado.getEmail());

    List<Domicilio> domicilios =
            BusquedaAfiliadoServiceUtil.buscarDomiciliosAfiliado(
                    requerimiento.getAfiliadoCuilTitular(),
                    requerimiento.getAfiliadoInt().intValue()
            );

    if ((domicilios == null || domicilios.isEmpty())
            && requerimiento.getAfiliadoInt().intValue() != 0) {

        domicilios =
                BusquedaAfiliadoServiceUtil.buscarDomiciliosAfiliado(
                        requerimiento.getAfiliadoCuilTitular(),
                        0
                );
    }

    if (domicilios == null || domicilios.isEmpty()) {
        return;
    }

    Domicilio domicilio = domicilios.get(0);

    requerimiento.setAfiliadoDireccion(
            formatearDireccion(domicilio)
    );
    requerimiento.setAfiliadoLocalidad(
            domicilio.getLocalidadAsString()
    );
    requerimiento.setAfiliadoProvincia(
            domicilio.getProvinciaAsString()
    );
    requerimiento.setAfiliadoCelular(
            formatearTelefono(
                    domicilio.getCod_area_celular(),
                    domicilio.getCelular()
            )
    );
    requerimiento.setAfiliadoTelefono(
            formatearTelefono(
                    domicilio.getCod_area_telefono(),
                    domicilio.getTelefono()
            )
    );
}

private String formatearDireccion(Domicilio domicilio) {
    if (domicilio == null) {
        return null;
    }

    StringBuilder direccion = new StringBuilder();
    agregarParte(direccion, domicilio.getCalle());
    agregarParte(direccion, domicilio.getNumero());
    agregarParte(direccion, prefijar("Piso", domicilio.getPiso()));
    agregarParte(direccion, prefijar("Dto.", domicilio.getDepto()));
    agregarParte(direccion, prefijar("Of.", domicilio.getOficina()));
    return WebKeysCompras.trimToNull(direccion.toString());
}

private String formatearTelefono(
        String codigoArea,
        String numero) {

    StringBuilder telefono = new StringBuilder();
    agregarParte(telefono, codigoArea);
    agregarParte(telefono, numero);
    return WebKeysCompras.trimToNull(telefono.toString());
}

private String prefijar(
        String prefijo,
        String valor) {

    String normalizado = WebKeysCompras.trimToNull(valor);
    return normalizado != null
            ? prefijo + " " + normalizado
            : null;
}

private void agregarParte(
        StringBuilder destino,
        String valor) {

    String normalizado = WebKeysCompras.trimToNull(valor);

    if (normalizado == null) {
        return;
    }

    if (destino.length() > 0) {
        destino.append(' ');
    }

    destino.append(normalizado);
}

private void aplicarReglaSectorSinAfiliado(
        RequerimientoCompra requerimiento) {

    if (requerimiento == null) {
        return;
    }

    requerimiento.setAfiliadoCuilTitular(null);
    requerimiento.setAfiliadoInt(null);
    requerimiento.setAfiliadoIdOspim((Integer) null);

    if (requerimiento.getIdRequerimientoCompra() <= 0) {
        requerimiento.setIdTercerizadora(null);
    }

    if (!requerimiento.esSectorSinCotizacionPrestador()) {
        requerimiento.setCargoOspim(Integer.valueOf(100));
        requerimiento.setCargoTercerizadora(Integer.valueOf(0));
    }

    requerimiento.setRecupero(
            requerimiento.getCargoTercerizadora() != null
                    && requerimiento.getCargoTercerizadora().intValue() > 0
    );
}

private void validarPrestacionNoRepetida(
        RequerimientoCompra requerimiento, RequerimientoCompraDetalle persistido,
        RequerimientoCompraDetalle detalle) throws Exception {
    validarPrestacionNoRepetida(requerimiento, persistido, detalle, null);
}

private void validarPrestacionNoRepetida(
        RequerimientoCompra requerimiento,
        RequerimientoCompraDetalle persistido,
        RequerimientoCompraDetalle detalle,
        EditarRequerimientoCompraServiceUtil.Transaccion transaccion) throws Exception {

    if (!detalle.esNomenclador() || requerimiento.getDetalles() == null) {
        return;
    }
    String codigo = WebKeysCompras.trimToNull(detalle.getCodigoNomenclador());
    if (transaccion != null && detalle.getIdPrestacionInt() > 0) {
        Nomenclador canonico = transaccion.obtenerNomencladorCanonico(detalle.getIdPrestacionInt());
        if (canonico != null && !WebKeysCompras.isEmpty(canonico.getCodigo())) {
            codigo = canonico.getCodigo().trim();
        }
    }
    for (RequerimientoCompraDetalle otro : requerimiento.getDetalles()) {
        if (otro == null || (persistido != null
                && otro.getIdInt() == persistido.getIdInt())) {
            continue;
        }
        String codigoOtro = otro.getCodigoNomenclador();
        if (transaccion != null && otro.getIdPrestacionInt() > 0) {
            Nomenclador canonicoOtro = transaccion.obtenerNomencladorCanonico(otro.getIdPrestacionInt());
            if (canonicoOtro != null && !WebKeysCompras.isEmpty(canonicoOtro.getCodigo())) {
                codigoOtro = canonicoOtro.getCodigo();
            }
        }
        if ((detalle.getIdPrestacion() != null
                && mismoInteger(otro.getIdPrestacion(), detalle.getIdPrestacion()))
                || (codigo != null && mismoTexto(codigo, codigoOtro))) {
            throw errorUsuario("El código de prestación ya está incluido en el requerimiento.");
        }
    }
}

private boolean mismoTexto(String a, String b) {
    String aa = a != null ? a.trim() : "";
    String bb = b != null ? b.trim() : "";
    return aa.equalsIgnoreCase(bb);
}

private boolean mismoInteger(Integer a, Integer b) {
    if (a == null && b == null) {
        return true;
    }

    return a != null
            && b != null
            && a.intValue() == b.intValue();
}

private void validarDetalleParaGuardar(
        RequerimientoCompra requerimiento, RequerimientoCompraDetalle persistido,
        RequerimientoCompraDetalle detalle, boolean requiereIdPersistido) throws Exception {
    validarDetalleParaGuardar(requerimiento, persistido, detalle, requiereIdPersistido, null);
}

private void validarDetalleParaGuardar(
        RequerimientoCompra requerimiento,
        RequerimientoCompraDetalle detallePersistido,
        RequerimientoCompraDetalle detalle,
        boolean requiereIdPersistido,
        EditarRequerimientoCompraServiceUtil.Transaccion transaccion) throws Exception {

    if (detalle == null) {
        throw errorUsuario(
                "Debe informar el detalle del requerimiento."
        );
    }

    Integer idRequerimiento = getIdRequerimientoDetalle(detalle);

    if (requiereIdPersistido && (idRequerimiento == null
            || idRequerimiento.intValue() <= 0)) {

        throw errorUsuario(
                "Primero debe guardar los datos generales del requerimiento."
        );
    }

    if (detalle.getCantidad() == null) {
        detalle.setCantidad(Integer.valueOf(ConfiguracionComprasHelper.getCantidadDetalleInicial()));
    }

    if (detalle.getCantidad().intValue() <= 0) {
        throw errorUsuario(
                "La cantidad debe ser mayor que cero."
        );
    }

    String tipoItem = detalle.getTipoItemNormalizado();

    if (!RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR.equals(tipoItem)
            && !RequerimientoCompraDetalle.TIPO_ITEM_MEDICAMENTO.equals(tipoItem)
            && !RequerimientoCompraDetalle.TIPO_ITEM_OBSERVACION.equals(tipoItem)) {

        throw errorUsuario(
                "El tipo de item seleccionado no es válido."
        );
    }

    if (RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR.equals(tipoItem)) {
        validarDetalleNomencladorParaGuardar(
                requerimiento,
                detallePersistido,
                detalle, transaccion
        );
    } else if (RequerimientoCompraDetalle.TIPO_ITEM_MEDICAMENTO.equals(tipoItem)) {
        validarDetalleMedicamentoParaGuardar(detalle);
    } else {
        validarDetalleObservacionParaGuardar(detalle);
    }

    if (detalle.getPrecioUnitarioEstimado() != null
            && detalle.getPrecioUnitarioEstimado()
            .compareTo(BigDecimal.ZERO) < 0) {

        throw errorUsuario(
                "El precio unitario no puede ser negativo."
        );
    }

    if (detalle.getPrecioTotalEstimadoInformado() != null
            && detalle.getPrecioTotalEstimadoInformado()
            .compareTo(BigDecimal.ZERO) < 0) {

        throw errorUsuario(
                "El precio total no puede ser negativo."
        );
    }
}

private void validarTipoPrestacionParaGuardar(
        RequerimientoCompra requerimiento, RequerimientoCompraDetalle persistido,
        RequerimientoCompraDetalle detalle) throws Exception {
    validarTipoPrestacionParaGuardar(requerimiento, persistido, detalle, null);
}

private void validarTipoPrestacionParaGuardar(
        RequerimientoCompra requerimiento,
        RequerimientoCompraDetalle detallePersistido,
        RequerimientoCompraDetalle detalle,
        EditarRequerimientoCompraServiceUtil.Transaccion transaccion) throws Exception {

    if (requerimiento == null
            || requerimiento.getIdSector() == null
            || requerimiento.getIdSector().intValue() <= 0
            || detalle == null) {

        throw errorUsuario(
                "No se pudo validar el tipo de prestación del detalle."
        );
    }

    List<TipoPrestacionCompra> tipos = transaccion != null
            ? transaccion.listarTiposPrestacion()
            : BusquedaRequerimientoCompraServiceUtil.listarTiposPrestacion();

    boolean sectorConCatalogo = false;
    boolean tipoValido = false;
    Integer idTipo = detalle.getIdTipoPrestacion();

    for (int i = 0; tipos != null && i < tipos.size(); i++) {
        TipoPrestacionCompra tipo = tipos.get(i);

        if (tipo == null
                || tipo.getIdSectorInt()
                != requerimiento.getIdSector().intValue()) {

            continue;
        }

        sectorConCatalogo = true;

        if (idTipo != null
                && tipo.getIdInt() == idTipo.intValue()) {

            tipoValido = true;
            detalle.setTipoPrestacionDescripcion(
                    tipo.getDescripcion()
            );
        }
    }

    if (idTipo == null) {
        if (detallePersistido != null
                && detallePersistido.getIdTipoPrestacion() != null) {

            throw errorUsuario(
                    "El tipo de prestación ya informado no puede quitarse."
            );
        }

        if (detallePersistido == null && sectorConCatalogo) {
            throw errorUsuario(
                    "Debe seleccionar el tipo de prestación."
            );
        }

        return;
    }

    if (!tipoValido) {
        throw errorUsuario(
                "El tipo de prestación no corresponde al sector "
                        + "del requerimiento."
        );
    }
}

private void validarDetalleNomencladorParaGuardar(
        RequerimientoCompra requerimiento, RequerimientoCompraDetalle persistido,
        RequerimientoCompraDetalle detalle) throws Exception {
    validarDetalleNomencladorParaGuardar(requerimiento, persistido, detalle, null);
}

private void validarDetalleNomencladorParaGuardar(
        RequerimientoCompra requerimiento,
        RequerimientoCompraDetalle detallePersistido,
        RequerimientoCompraDetalle detalle,
        EditarRequerimientoCompraServiceUtil.Transaccion transaccion) throws Exception {

    if (detalle.getIdPrestacion() == null
            || detalle.getIdPrestacion().intValue() <= 0) {

        throw errorUsuario(
                "Debe seleccionar una prestación del nomenclador."
        );
    }

    if (detalle.getIdTipoNomenclador() == null
            || detalle.getIdTipoNomenclador().intValue() <= 0) {

        throw errorUsuario(
                "Debe seleccionar el tipo de nomenclador."
        );
    }

    if (detalle.getIdMedicamento() != null
            || detalle.getTroquel() != null
            || !WebKeysCompras.isEmpty(
            detalle.getNombreMedicamento()
    )) {

        throw errorUsuario(
                "Los datos recibidos no corresponden a una prestación. "
                        + "Actualice la pantalla y vuelva a seleccionarla."
        );
    }

    Nomenclador nomenclador = transaccion != null
            ? transaccion.obtenerNomencladorCanonico(detalle.getIdPrestacion().intValue())
            : obtenerNomencladorCanonico(detalle.getIdPrestacion().intValue());

    if (nomenclador == null
            || nomenclador.getId_prestacion()
            != detalle.getIdPrestacion().intValue()
            || nomenclador.getBaja_fecha() != null) {

        throw errorUsuario(
                "La prestación seleccionada ya no existe "
                        + "o no está activa. Vuelva a seleccionarla."
        );
    }

    int idTipoNomencladorCanonico =
            nomenclador.getId_tipo_nomenclador();

    if (idTipoNomencladorCanonico <= 0
            || idTipoNomencladorCanonico
            != detalle.getIdTipoNomenclador().intValue()) {

        throw errorUsuario(
                "La prestación seleccionada no corresponde al "
                        + "tipo de nomenclador actual. Vuelva a seleccionarla."
        );
    }

    boolean nuevaSeleccion = detallePersistido == null
            || !detallePersistido.esNomenclador()
            || !mismoInteger(
                    detallePersistido.getIdPrestacion(),
                    detalle.getIdPrestacion())
            || !mismoInteger(
                    detallePersistido.getIdTipoPrestacion(),
                    detalle.getIdTipoPrestacion());

    /*
     * La configuracion vigente valida selecciones nuevas y cambios de rubro.
     * Retirar una asociacion no invalida la misma seleccion ya persistida.
     */
    if (!nuevaSeleccion) {
        ReclamoPrestacionalCompraPrecargaHelper.validarReferenciaPersistida(detallePersistido, nomenclador);
        detalle.setIdTipoNomenclador(detallePersistido.getIdTipoNomenclador());
        detalle.setCodigoNomenclador(detallePersistido.getCodigoNomenclador());
        detalle.setDescripcionNomenclador(detallePersistido.getDescripcionNomenclador());
        detalle.setCodigoItem(detallePersistido.getCodigoNomenclador());
        detalle.setDescripcionItem(detallePersistido.getDescripcionNomenclador());
        return;
    }
    if (nuevaSeleccion) {
        List<SectorNomencladorCompra> configuracion = transaccion != null
                ? NomencladorCompraBusquedaHelper.resolverConfiguracion(
                        requerimiento.getIdSector().intValue(), detalle.getIdTipoPrestacion(), transaccion)
                : BusquedaRequerimientoCompraServiceUtil.getConfiguracionNomenclador(
                        requerimiento.getIdSector().intValue(), detalle.getIdTipoPrestacion());

        if (configuracion == null || configuracion.isEmpty()) {
            throw errorUsuario(
                    "El sector y tipo de cotización no tienen "
                            + "nomencladores configurados para Compras.");
        }

        if (!BusquedaRequerimientoCompraServiceUtil
                .esNomencladorConfigurado(configuracion, nomenclador)) {
            throw errorUsuario(
                    "La prestación seleccionada no pertenece a los "
                            + "nomencladores configurados para el sector "
                            + "y tipo de cotización.");
        }
    }

    detalle.setIdTipoNomenclador(
            Integer.valueOf(idTipoNomencladorCanonico)
    );

    validarTextoTecnico(
            "código de nomenclador",
            detalle.getCodigoNomenclador(),
            nomenclador.getCodigo()
    );

    validarTextoTecnico(
            "descripción de nomenclador",
            detalle.getDescripcionNomenclador(),
            nomenclador.getDescripcion()
    );

    detalle.setCodigoNomenclador(
            emptyToNull(nomenclador.getCodigo())
    );
    detalle.setDescripcionNomenclador(
            emptyToNull(nomenclador.getDescripcion())
    );
    detalle.setCodigoItem(
            detalle.getCodigoNomenclador()
    );
    detalle.setDescripcionItem(
            detalle.getDescripcionNomenclador()
    );
}

private void validarDetalleMedicamentoParaGuardar(
        RequerimientoCompraDetalle detalle) throws Exception {

    if (detalle.getIdInt() <= 0) {
        throw errorUsuario(
                "No se pueden crear nuevos detalles de tipo MEDICAMENTO en Compras."
        );
    }

    if (detalle.getIdMedicamento() == null
            || detalle.getIdMedicamento().intValue() <= 0
            || WebKeysCompras.isEmpty(
            detalle.getNombreMedicamento()
    )) {

        throw errorUsuario(
                "El detalle histórico de medicamento no conserva "
                        + "una referencia válida."
        );
    }

    if (detalle.getIdPrestacion() != null
            || detalle.getIdTipoNomenclador() != null
            || !WebKeysCompras.isEmpty(detalle.getCodigoNomenclador())
            || !WebKeysCompras.isEmpty(detalle.getDescripcionNomenclador())) {

        throw errorUsuario(
                "El detalle histórico de medicamento contiene "
                        + "datos técnicos incompatibles."
        );
    }
}

private void validarDetalleObservacionParaGuardar(
        RequerimientoCompraDetalle detalle) throws Exception {

    if (WebKeysCompras.isEmpty(detalle.getObservaciones())) {
        throw errorUsuario(
                "Debe informar las Observaciones del detalle."
        );
    }

    if (detalle.getIdPrestacion() != null
            || detalle.getIdTipoNomenclador() != null
            || !WebKeysCompras.isEmpty(detalle.getCodigoNomenclador())
            || !WebKeysCompras.isEmpty(detalle.getDescripcionNomenclador())
            || detalle.getIdMedicamento() != null
            || detalle.getTroquel() != null
            || !WebKeysCompras.isEmpty(detalle.getNombreMedicamento())) {

        throw errorUsuario(
                "Un detalle de Observación no puede contener "
                        + "datos de código o medicamento."
        );
    }
}

private RequerimientoCompra validarRequerimientoDetalle(
        Integer idRequerimiento) throws Exception {

    if (idRequerimiento == null
            || idRequerimiento.intValue() <= 0) {

        throw errorUsuario(
                "Primero debe guardar los datos generales del requerimiento."
        );
    }

    RequerimientoCompra requerimiento =
            obtenerRequerimientoDetalle(
                    idRequerimiento.intValue()
            );

    if (requerimiento == null
            || !requerimiento.puedeEditarEstructura()) {

        throw errorUsuario(
                "Los detalles ya no pueden modificarse porque el "
                        + "requerimiento no se encuentra PENDIENTE."
        );
    }

    if (requerimiento.getSectorId() == null
            || requerimiento.getSectorId().intValue() <= 0) {

        throw errorUsuario(
                "El requerimiento no tiene un sector válido."
        );
    }

    return requerimiento;
}

private void prepararDetalleParaGuardar(
        RequerimientoCompra requerimiento,
        RequerimientoCompraDetalle detallePersistido,
        RequerimientoCompraDetalle detalle) throws Exception {
        prepararDetalleParaGuardar(requerimiento, detallePersistido, detalle, null);
    }

private void prepararDetalleParaGuardar(
        RequerimientoCompra requerimiento,
        RequerimientoCompraDetalle detallePersistido,
        RequerimientoCompraDetalle detalle,
            EditarRequerimientoCompraServiceUtil.Transaccion transaccion) throws Exception {

    if (detalle == null) {
        throw errorUsuario(
                "Debe informar el detalle del requerimiento."
        );
    }

    if (detallePersistido != null
            && detallePersistido.esMedicamento()) {

        String tipoRecibido = detalle.getTipoItemNormalizado();

        if (!WebKeysCompras.isEmpty(tipoRecibido)
                && !RequerimientoCompraDetalle
                .TIPO_ITEM_MEDICAMENTO
                .equals(tipoRecibido)) {

            throw errorUsuario(
                    "El detalle histórico de medicamento no puede "
                            + "convertirse directamente a otro tipo."
            );
        }

        detalle.setTipoItem(
                RequerimientoCompraDetalle.TIPO_ITEM_MEDICAMENTO
        );
        detalle.setIdPrestacion(null);
        detalle.setIdTipoNomenclador(null);
        detalle.setCodigoNomenclador(null);
        detalle.setDescripcionNomenclador(null);
        detalle.setIdMedicamento(detallePersistido.getIdMedicamento());
        detalle.setTroquel(detallePersistido.getTroquel());
        detalle.setNombreMedicamento(detallePersistido.getNombreMedicamento());
        detalle.setCodigoItem(detallePersistido.getCodigoItemVisible());
        detalle.setDescripcionItem(detallePersistido.getDescripcionItemVisible());
        return;
    }

    boolean sectorObservacion = transaccion != null
            ? ConfiguracionComprasHelper.resolverSectorDetalleObservacion(requerimiento.getSectorDescripcion(),
                    transaccion.getConfiguracionCompra("COMPRAS_SECTORES_DETALLE_OBSERVACION"))
            : WebKeysCompras.esSectorDetalleObservacionCompras(requerimiento != null ? requerimiento.getSectorDescripcion() : null);

    if (sectorObservacion) {
        if (detallePersistido != null
                && !detallePersistido.esObservacion()) {
            throw errorUsuario(
                    "El detalle existente no corresponde al sector "
                            + "del requerimiento. Actualice la pantalla "
                            + "e intente nuevamente."
            );
        }

        String tipoRecibido = detalle.getTipoItemNormalizado();

        if (!WebKeysCompras.isEmpty(tipoRecibido)
                && !RequerimientoCompraDetalle
                .TIPO_ITEM_OBSERVACION
                .equals(tipoRecibido)) {

            throw errorUsuario(
                    "El sector seleccionado requiere un detalle de OBSERVACIÓN."
            );
        }

        detalle.setTipoItem(
                RequerimientoCompraDetalle.TIPO_ITEM_OBSERVACION
        );
        limpiarReferenciaTecnica(detalle);
        detalle.setCodigoItem(null);
        detalle.setDescripcionItem(null);
        return;
    }

        if (detallePersistido != null
                && detallePersistido.esObservacion()) {

            throw errorUsuario(
                    "El detalle existente no corresponde al sector "
                            + "del requerimiento. Actualice la pantalla "
                            + "e intente nuevamente."
            );
        }

        String tipoRecibido = detalle.getTipoItemNormalizado();

        if (!WebKeysCompras.isEmpty(tipoRecibido)
                && !RequerimientoCompraDetalle
                .TIPO_ITEM_NOMENCLADOR
                .equals(tipoRecibido)) {

            throw errorUsuario(
                    "Los detalles nuevos de Compras deben utilizar NOMENCLADOR."
            );
        }

        detalle.setTipoItem(
                RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR
        );
        detalle.setIdMedicamento(null);
        detalle.setTroquel(null);
        detalle.setNombreMedicamento(null);
        detalle.setCodigoItem(detalle.getCodigoNomenclador());
        detalle.setDescripcionItem(detalle.getDescripcionNomenclador());
    }

    private void limpiarReferenciaTecnica(
            RequerimientoCompraDetalle detalle) {

        detalle.setIdPrestacion(null);
        detalle.setIdTipoNomenclador(null);
        detalle.setCodigoNomenclador(null);
        detalle.setDescripcionNomenclador(null);
        detalle.setIdMedicamento(null);
        detalle.setTroquel(null);
        detalle.setNombreMedicamento(null);
    }

    private RequerimientoCompraDetalle obtenerDetallePersistido(
            RequerimientoCompra requerimiento,
            int idDetalle) throws Exception {

        if (idDetalle <= 0) {
            return null;
        }

        if (requerimiento == null) {
            throw errorUsuario(
                    "No se pudo validar el requerimiento del detalle."
            );
        }

        List<RequerimientoCompraDetalle> detalles =
                requerimiento.getDetalles();

        if (detalles != null) {
            for (int i = 0; i < detalles.size(); i++) {
                RequerimientoCompraDetalle persistido = detalles.get(i);

                if (persistido != null
                        && persistido.getIdInt() == idDetalle
                        && persistido.getIdRequerimientoCompra()
                        == requerimiento.getIdRequerimientoCompra()) {

                    return persistido;
                }
            }
        }

        throw errorUsuario(
                "El detalle que intenta modificar ya no existe "
                        + "o no pertenece al requerimiento."
        );
    }

    protected RequerimientoCompra obtenerRequerimientoDetalle(
            int idRequerimiento) throws Exception {

        return BusquedaRequerimientoCompraServiceUtil
                .getRequerimientoCompra(idRequerimiento);
    }

    protected Nomenclador obtenerNomencladorCanonico(
            int idPrestacion) throws Exception {

        return NomencladorServiceUtil.buscarNomencladorPorId(
                idPrestacion
        );
    }

    protected Medicamento obtenerMedicamentoCanonico(
            int idMedicamento) throws Exception {

        return BusquedaMedicamentoServiceUtil.getMedicamento(
                idMedicamento
        );
    }

    public String normalizarTextoCarga(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() == 0) {
            return null;
        }

        String normalizado =
                Normalizer.normalize(
                        value,
                        Normalizer.Form.NFD
                );

        normalizado =
                DIACRITICOS.matcher(normalizado).replaceAll("");

        return normalizado.toUpperCase(Locale.ROOT).trim();
    }

    private void normalizarDetalleNuevo(
            RequerimientoCompraDetalle detalle) {

        if (detalle == null || detalle.getIdInt() > 0) {
            return;
        }

        detalle.setObservaciones(
                normalizarTextoCarga(
                        detalle.getObservaciones()
                )
        );
    }

    private void validarTextoTecnico(
            String campo,
            String recibido,
            String canonico) throws Exception {

        String recibidoNormalizado = normalizarTextoTecnico(recibido);
        String canonicoNormalizado = normalizarTextoTecnico(canonico);

        if (recibidoNormalizado == null
                || canonicoNormalizado == null
                || !recibidoNormalizado.equalsIgnoreCase(
                canonicoNormalizado
        )) {
            throw errorUsuario(
                    "El " + campo
                            + " cambió o ya no coincide con la información actual. "
                            + "Vuelva a seleccionarlo."
            );
        }
    }

    private String normalizarTextoTecnico(String value) {
        String result = emptyToNull(value);
        return result == null
                ? null
                : result.replaceAll("\\s+", " ");
    }

    private RequerimientoCompra validarRequerimientoParaEnviarACotizar(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw errorUsuario(
                    "Debe informar el requerimiento de compra."
            );
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(idRequerimientoCompra);

        if (requerimiento == null) {
            throw errorUsuario(
                    "No se encontró el requerimiento de compra informado."
            );
        }

        if (!requerimiento.puedeEnviarACotizar()) {
            throw errorUsuario(
                    "El requerimiento solo puede enviarse a cotizar "
                            + "mientras está PENDIENTE."
            );
        }

        if (requerimiento.getIdSector() == null
                || requerimiento.getIdSector().intValue() <= 0) {
            throw errorUsuario(
                    "Debe seleccionar el sector antes de enviar "
                            + "el requerimiento a cotizar."
            );
        }

        if (!requerimiento.tieneDetalles()) {
            throw errorUsuario(
                    "Debe agregar al menos un detalle antes de enviar "
                            + "el requerimiento a cotizar."
            );
        }

        if (requerimiento.isRequiereAfiliado()
                && !requerimiento.tieneAfiliadoInformado()) {
            throw errorUsuario(
                    "Debe completar los datos del afiliado antes de enviar "
                            + "el requerimiento a cotizar."
            );
        }

        return requerimiento;
    }

    private GuardadoCotizacionResultado guardarCotizacion(int id,
            List<RequerimientoCompraDetalle> detalles,List<Integer> eliminados,Boolean surge,String usuario)throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx=null;
        try{
            tx=EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            GuardadoCotizacionResultado r=guardarCotizacionEnTransaccion(tx,id,detalles,eliminados,surge,normalizarUsuario(usuario));
            tx.commit();return r;
        }catch(Exception e){
            if(tx!=null)try{tx.rollback();}catch(Exception fallo){e.addSuppressed(fallo);}
            throw manejarErrorOperacion("guardar la cotizacion","No se pudo guardar la cotizacion. Revise los datos e intente nuevamente.",e,"idRequerimiento="+id);
        }finally{if(tx!=null)tx.cerrar();}
    }

    private GuardadoCotizacionResultado guardarCotizacionEnTransaccion(EditarRequerimientoCompraServiceUtil.Transaccion tx,
            int id,List<RequerimientoCompraDetalle> detalles,List<Integer> eliminados,Boolean surge,String usuario)throws Exception {
        if(id<=0)throw errorUsuario("Debe informar el requerimiento de compra.");
        RequerimientoCompra vigente=tx.bloquearYObtenerRequerimiento(id);
        String estados=tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS);
        int aCotizar=ConfiguracionComprasHelper.resolverEstado("A_COTIZAR",estados),cotizado=ConfiguracionComprasHelper.resolverEstado("COTIZADO",estados);
        if(vigente==null||!vigente.isActivo())throw errorUsuario("El requerimiento ya no esta disponible.");
        if(vigente.getEstado()==cotizado)return new GuardadoCotizacionResultado(true,cotizado);
        if(vigente.getEstado()!=aCotizar)throw errorUsuario("La cotizacion solo puede guardarse en estado A COTIZAR.");
        validarDetallesCotizacionRecibidos(detalles);validarIdsDetallesEliminados(detalles,eliminados);
        Set<Integer> incluidos=new HashSet<Integer>();
        for(int i=0;i<detalles.size();i++)incluidos.add(Integer.valueOf(detalles.get(i).getIdInt()));
        incluidos.addAll(eliminados);
        if(incluidos.size()!=vigente.getDetalles().size())throw errorUsuario("Los detalles cambiaron mientras se guardaba la cotizacion.");
        for(int i=0;i<vigente.getDetalles().size();i++)if(!incluidos.contains(Integer.valueOf(vigente.getDetalles().get(i).getIdInt())))
            throw errorUsuario("La cotizacion no coincide con los detalles activos del requerimiento.");
        Integer prestador=obtenerPrestadorAdjudicadoUnico(detalles);String estadoPrestador=null,cotizadoPrestador=null;
        if(prestador!=null){
            if(prestador.intValue()<=0)throw errorUsuario("Debe seleccionar un prestador valido.");
            String envios=tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_ENVIO);
            String enviado=ConfiguracionComprasHelper.resolverEstadoEnvio("ENVIADO",envios);
            cotizadoPrestador=ConfiguracionComprasHelper.resolverEstadoEnvio("COTIZADO",envios);
            estadoPrestador=tx.bloquearEstadoPrestadorCotizacion(id,prestador.intValue());
            if(!enviado.equals(estadoPrestador)&&!cotizadoPrestador.equals(estadoPrestador))
                throw errorUsuario("El prestador adjudicado no fue notificado correctamente para este requerimiento.");
        }
        for(int i=0;i<eliminados.size();i++)if(tx.persistirBajaDetalle(id,eliminados.get(i).intValue(),usuario)!=1)
            throw errorUsuario("Un detalle cambio mientras se guardaba la cotizacion.");
        if(tx.persistirSurge(id,aCotizar,surge!=null?surge.booleanValue():vigente.isSurge(),usuario)!=1)
            throw errorUsuario("El requerimiento cambio mientras se guardaba la cotizacion.");
        boolean completa=prestador!=null;
        for(int i=0;i<detalles.size();i++){
            RequerimientoCompraDetalle recibido=detalles.get(i),persistido=obtenerDetallePersistido(vigente,recibido.getIdInt());
            BigDecimal precio=recibido.getPrecioUnitarioEstimado()!=null?WebKeysCompras.normalizarImporte(recibido.getPrecioUnitarioEstimado()):null;
            persistido.setPrecioUnitarioEstimado(precio);persistido.setIdPrestador(prestador);
            persistido.setPrecioTotalEstimado(WebKeysCompras.calcularPrecioTotal(persistido.getCantidad(),precio));
            if(tx.persistirImportesDetalle(persistido,usuario)!=1)throw errorUsuario("Un detalle cambio mientras se guardaba la cotizacion.");
            completa=completa&&precio!=null&&persistido.getPrecioTotalEstimadoInformado()!=null;
        }
        if(prestador!=null)validarPrestadorCorreccion(tx.bloquearYObtenerRequerimiento(id),prestador.intValue(),tx);
        if(completa){
            validarTienePresupuestoActivoDelPrestador(id,prestador.intValue(),tx);
            if(!cotizadoPrestador.equals(estadoPrestador))throw errorUsuario("El prestador debe encontrarse COTIZADO antes de cerrar la cotizacion.");
            if(!ConfiguracionComprasHelper.resolverTransicionEstado(aCotizar,cotizado,tx.getConfiguracionCompra("COMPRAS_TRANSICIONES_ESTADO"),estados))
                throw errorUsuario("La transicion de cotizacion no esta habilitada.");
            if(tx.persistirEstado(id,aCotizar,cotizado,usuario,null,false)!=1)throw errorUsuario("El requerimiento cambio mientras se cerraba la cotizacion.");
        }
        return new GuardadoCotizacionResultado(completa,completa?cotizado:aCotizar);
    }

    public int guardarCotizacionEnTransaccion(EditarRequerimientoCompraServiceUtil.Transaccion tx,
            int id,Integer[] ids,BigDecimal[] precios,Integer[] eliminados,Integer prestador,Boolean surge,String usuario)throws Exception {
        List<RequerimientoCompraDetalle> detalles=null;
        if(ids!=null&&precios!=null&&ids.length==precios.length){
            detalles=new ArrayList<RequerimientoCompraDetalle>();
            for(int i=0;i<ids.length;i++){
                RequerimientoCompraDetalle d=new RequerimientoCompraDetalle();d.setId(ids[i]);d.setIdRequerimientoCompra(id);
                d.setPrecioUnitarioEstimado(precios[i]);d.setIdPrestador(prestador);detalles.add(d);
            }
        }
        List<Integer> bajas=new ArrayList<Integer>();if(eliminados!=null)for(int i=0;i<eliminados.length;i++)bajas.add(eliminados[i]);
        return guardarCotizacionEnTransaccion(tx,id,detalles,bajas,surge,normalizarUsuario(usuario)).getEstadoFinal();
    }

    public int guardarCotizacionPersistida(int id,String ids,String precios,String eliminados,
            Integer prestador,Boolean surge,String usuario)throws Exception {
        String[] vi=leerArrayPersistido(ids),vp=leerArrayPersistido(precios),vb=leerArrayPersistido(eliminados);
        Integer[] idsDetalle=vi!=null?new Integer[vi.length]:null,bajas=vb!=null?new Integer[vb.length]:null;
        BigDecimal[] importes=vp!=null?new BigDecimal[vp.length]:null;
        try{
            for(int i=0;idsDetalle!=null&&i<idsDetalle.length;i++)idsDetalle[i]="NULL".equalsIgnoreCase(vi[i])?null:Integer.valueOf(vi[i]);
            for(int i=0;importes!=null&&i<importes.length;i++)importes[i]="NULL".equalsIgnoreCase(vp[i])?null:new BigDecimal(vp[i]);
            for(int i=0;bajas!=null&&i<bajas.length;i++)bajas[i]="NULL".equalsIgnoreCase(vb[i])?null:Integer.valueOf(vb[i]);
        }catch(NumberFormatException e){throw errorUsuario("La cotizacion contiene valores invalidos.");}
        EditarRequerimientoCompraServiceUtil.Transaccion tx=null;
        try{tx=EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            int estado=guardarCotizacionEnTransaccion(tx,id,idsDetalle,importes,bajas,prestador,surge,usuario);tx.commit();return estado;
        }catch(Exception e){if(tx!=null)try{tx.rollback();}catch(Exception fallo){e.addSuppressed(fallo);}throw e;
        }finally{if(tx!=null)tx.cerrar();}
    }

    private String[] leerArrayPersistido(String valor)throws Exception {
        if(valor==null)return null;String limpio=valor.trim();
        if(!limpio.startsWith("{")||!limpio.endsWith("}"))throw errorUsuario("La cotizacion contiene arreglos invalidos.");
        limpio=limpio.substring(1,limpio.length()-1).trim();if(limpio.length()==0)return new String[0];
        String[] elementos=limpio.split(",",-1);for(int i=0;i<elementos.length;i++)elementos[i]=elementos[i].trim();return elementos;
    }

    private boolean obtenerSurgeActual(
            int idRequerimientoCompra) throws Exception {

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(idRequerimientoCompra);

        if (requerimiento == null) {
            throw errorUsuario(
                    "No se encontró el requerimiento de compra informado."
            );
        }

        return requerimiento.isSurge();
    }

    protected void validarIdsDetallesEliminados(
            List<RequerimientoCompraDetalle> detalles,
            List<Integer> idsDetallesEliminados) throws Exception {

        if (idsDetallesEliminados == null) {
            throw errorUsuario(
                    "No se recibió la lista de prestaciones eliminadas."
            );
        }

        Set<Integer> idsConservados = new HashSet<Integer>();

        for (int i = 0; detalles != null && i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle = detalles.get(i);

            if (detalle != null && detalle.getIdInt() > 0) {
                idsConservados.add(Integer.valueOf(detalle.getIdInt()));
            }
        }

        Set<Integer> idsEliminados = new HashSet<Integer>();

        for (int i = 0; i < idsDetallesEliminados.size(); i++) {
            Integer id = idsDetallesEliminados.get(i);

            if (id == null
                    || id.intValue() <= 0
                    || idsConservados.contains(id)
                    || !idsEliminados.add(id)) {

                throw errorUsuario(
                        "La lista de prestaciones eliminadas fue manipulada."
                );
            }
        }
    }

    protected void validarDetallesCotizacionRecibidos(
            List<RequerimientoCompraDetalle> detalles) throws Exception {

        if (detalles == null || detalles.isEmpty()) {
            throw errorUsuario(
                    "La cotización no contiene detalles para guardar."
            );
        }

        Set<Integer> idsRecibidos = new HashSet<Integer>();

        for (int i = 0; i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle = detalles.get(i);

            if (detalle == null || detalle.getIdInt() <= 0) {
                throw errorUsuario(
                        "Los detalles recibidos no coinciden con el requerimiento. "
                                + "Actualice la pantalla y vuelva a intentarlo."
                );
            }

            Integer idDetalle = Integer.valueOf(detalle.getIdInt());

            if (!idsRecibidos.add(idDetalle)) {
                throw errorUsuario(
                        "La cotización contiene un detalle repetido. "
                                + "Actualice la pantalla y vuelva a intentarlo."
                );
            }

            if (detalle.getPrecioUnitarioEstimado() != null
                    && detalle.getPrecioUnitarioEstimado()
                    .compareTo(BigDecimal.ZERO) < 0) {

                throw errorUsuario(
                        "El precio unitario no puede ser negativo."
                );
            }
        }

        obtenerPrestadorAdjudicadoUnico(detalles);
    }

    protected Integer obtenerPrestadorAdjudicadoUnico(
            List<RequerimientoCompraDetalle> detalles) throws Exception {

        Integer idPrestadorAdjudicado = null;

        for (int i = 0;
             detalles != null && i < detalles.size();
             i++) {

            RequerimientoCompraDetalle detalle = detalles.get(i);

            if (detalle == null
                    || !detalle.tienePrestadorAdjudicado()) {
                continue;
            }

            Integer idPrestadorDetalle = detalle.getIdPrestador();

            if (idPrestadorAdjudicado == null) {
                idPrestadorAdjudicado = idPrestadorDetalle;
            } else if (idPrestadorAdjudicado.intValue()
                    != idPrestadorDetalle.intValue()) {

                throw errorUsuario(
                        "Debe seleccionar el mismo prestador adjudicado "
                                + "para todos los detalles."
                );
            }
        }

        return idPrestadorAdjudicado;
    }

    private void validarTienePresupuestoActivoDelPrestador(int idRequerimiento,
            int idPrestador) throws Exception {
        validarTienePresupuestoActivoDelPrestador(idRequerimiento, idPrestador, null);
    }

    private void validarTienePresupuestoActivoDelPrestador(
            int idRequerimientoCompra,
            int idPrestadorAdjudicado,
            EditarRequerimientoCompraServiceUtil.Transaccion transaccion) throws Exception {

        if (idPrestadorAdjudicado <= 0) {
            throw errorUsuario(
                    "Debe seleccionar un prestador adjudicado válido."
            );
        }

        List<RequerimientoCompraPresupuesto> presupuestos =
                transaccion != null ? transaccion.listarPresupuestos(idRequerimientoCompra)
                        : BusquedaRequerimientoCompraServiceUtil.listarPresupuestos(idRequerimientoCompra);

        for (int i = 0;
             presupuestos != null && i < presupuestos.size();
             i++) {

            RequerimientoCompraPresupuesto presupuesto = presupuestos.get(i);

            if (presupuesto == null
                    || presupuesto.getBajaFecha() != null
                    || presupuesto.getIdPrestador() == null
                    || presupuesto.getIdPrestador().intValue() <= 0
                    || presupuesto.getDlFileEntryId() == null
                    || presupuesto.getDlFileEntryId().longValue() <= 0L) {
                continue;
            }

            Integer idPrestadorPresupuesto = presupuesto.getIdPrestador();

            if (idPrestadorPresupuesto != null
                    && idPrestadorPresupuesto.intValue()
                    == idPrestadorAdjudicado) {
                return;
            }
        }

        throw errorUsuario(
                "Para cerrar la cotización, primero cargue un presupuesto "
                        + "activo del prestador adjudicado."
        );
    }

    private boolean debeValidarPresupuestoParaCotizado(
            Integer idPrestadorAdjudicado,
            BigDecimal[] preciosUnitarios) {

        if (idPrestadorAdjudicado == null
                || idPrestadorAdjudicado.intValue() <= 0
                || preciosUnitarios == null
                || preciosUnitarios.length == 0) {

            return false;
        }

        for (int i = 0; i < preciosUnitarios.length; i++) {
            if (preciosUnitarios[i] == null) {
                return false;
            }
        }

        return true;
    }

    private void validarPresupuestoParaRegistrar(
            RequerimientoCompraPresupuesto presupuesto) throws Exception {

        if (presupuesto == null) {
            throw errorUsuario(
                    "Debe informar el presupuesto del requerimiento."
            );
        }

        if (presupuesto.getIdRequerimiento() == null
                || presupuesto.getIdRequerimiento().intValue() <= 0) {
            throw errorUsuario(
                    "Debe informar el requerimiento de compra."
            );
        }

        if (presupuesto.getTipoDocumento() == null) {
            throw errorUsuario(
                    "Debe informar el tipo de documento del presupuesto."
            );
        }

        if (presupuesto.isPresupuestoPrestador()) {
            if (presupuesto.getIdPrestador() == null
                    || presupuesto.getIdPrestador().intValue() <= 0) {

                throw errorUsuario(
                        "Debe informar el prestador del presupuesto."
                );
            }

            if (!WebKeysCompras.isEmpty(presupuesto.getEmpresaCuit())
                    || !WebKeysCompras.isEmpty(
                            presupuesto.getEmpresaSucursal()
                    )
                    || !WebKeysCompras.isEmpty(
                            presupuesto.getDescripcionEmpresa()
                    )) {

                throw errorUsuario(
                        "Un presupuesto de prestador no puede asociarse "
                                + "a una Empresa."
                );
            }

        } else if (presupuesto.isCotizacionEmpresa()) {
            if (presupuesto.getIdPrestador() != null
                    || !WebKeysCompras.isEmpty(
                            presupuesto.getDescripcionPrestador()
                    )) {

                throw errorUsuario(
                        "Una cotización de Empresa no puede asociarse "
                                + "a un prestador."
                );
            }

            new PresupuestoCompraHelper().validarDatosEmpresaCotizacion(
                    presupuesto.getEmpresaCuit(), presupuesto.getEmpresaSucursal(),
                    presupuesto.getDescripcionEmpresa(), 1);

        } else {
            throw errorUsuario(
                    "El tipo de documento del presupuesto no es válido."
            );
        }

        if (presupuesto.getDlGroupId() == null
                || presupuesto.getDlGroupId().longValue() <= 0L
                || presupuesto.getDlFolderId() == null
                || presupuesto.getDlFolderId().longValue() < 0L
                || presupuesto.getDlFileEntryId() == null
                || presupuesto.getDlFileEntryId().longValue() <= 0L) {

            throw errorUsuario(
                    "No se pudo identificar correctamente el documento "
                            + "del presupuesto. Vuelva a seleccionarlo."
            );
        }

        if (WebKeysCompras.isEmpty(presupuesto.getNombreOriginal())
                || WebKeysCompras.isEmpty(presupuesto.getNombrePersistido())
                || WebKeysCompras.isEmpty(presupuesto.getTitulo())) {

            throw errorUsuario(
                    "El documento del presupuesto no conserva una identidad válida."
            );
        }
    }

    protected int confirmarEnvioACotizar(
            int idRequerimientoCompra,
            String usuario) throws Exception {

        return EditarRequerimientoCompraServiceUtil.confirmarEnvioACotizar(
                idRequerimientoCompra,
                normalizarUsuario(usuario)
        );
    }

    private Integer getIdRequerimientoDetalle(
            RequerimientoCompraDetalle detalle) {

        if (detalle == null) {
            return null;
        }

        if (detalle.getIdRequerimiento() != null
                && detalle.getIdRequerimiento().intValue() > 0) {
            return detalle.getIdRequerimiento();
        }

        return detalle.getIdRequerimientoCompra() > 0
                ? Integer.valueOf(detalle.getIdRequerimientoCompra())
                : null;
    }

    private void validarPorcentaje(
            Integer value,
            String label) throws Exception {

        int parsed = value != null
                ? value.intValue()
                : 0;

        if (parsed < 0 || parsed > 100) {
            throw errorUsuario(
                    label + " debe estar entre 0 y 100 %."
            );
        }
    }

    private String emptyToNull(String value) {
        return WebKeysCompras.trimToNull(value);
    }

    public static String normalizarUsuarioAuditoria(String usuario) {
        if(usuario==null)return null;int inicio=0,fin=usuario.length();
        while(inicio<fin&&usuario.charAt(inicio)==' ')inicio++;
        while(fin>inicio&&usuario.charAt(fin-1)==' ')fin--;
        return inicio==fin?null:usuario.substring(inicio,fin);
    }

    private String normalizarUsuario(String usuario) {
        return normalizarUsuarioAuditoria(usuario);
    }

    private MensajeUsuarioException errorUsuario(String mensaje) {
        return new MensajeUsuarioException(mensaje);
    }

    private MensajeUsuarioException errorUsuario(
            String mensaje,
            Throwable causa) {

        return new MensajeUsuarioException(
                mensaje,
                causa
        );
    }

    private Exception manejarErrorOperacion(
            String operacion,
            String mensajePredeterminado,
            Exception error,
            String contexto) {

        MensajeUsuarioException funcional =
                buscarMensajeUsuarioException(error);

        if (funcional != null) {
            return funcional;
        }

        StringBuilder mensajeLog = new StringBuilder();
        mensajeLog.append("Error técnico al ");
        mensajeLog.append(operacion);
        mensajeLog.append('.');

        if (!WebKeysCompras.isEmpty(contexto)) {
            mensajeLog.append(' ');
            mensajeLog.append(contexto);
        }

        _log.error(
                mensajeLog.toString(),
                error
        );

        Throwable causa = error;
        Set<Throwable> revisadas = new HashSet<Throwable>();
        while (causa != null && revisadas.add(causa)) {
            if (causa instanceof ConfiguracionComprasHelper.ConfiguracionInvalidaException) {
                return errorUsuario("La configuracion de Compras no esta disponible o es invalida. "
                        + "Solicite su revision antes de reintentar.", error);
            }
            causa = causa.getCause();
        }
        return errorUsuario(mensajePredeterminado, error);
    }

    private MensajeUsuarioException buscarMensajeUsuarioException(
            Throwable error) {

        Throwable actual = error;
        Set<Throwable> visitados = new HashSet<Throwable>();

        while (actual != null && visitados.add(actual)) {
            if (actual instanceof MensajeUsuarioException) {
                return (MensajeUsuarioException) actual;
            }

            actual = actual.getCause();
        }

        return null;
    }

    private int obtenerIdRequerimientoSeguro(
            RequerimientoCompra requerimiento) {

        return requerimiento != null
                ? requerimiento.getIdRequerimientoCompra()
                : 0;
    }

    private String construirContextoDetalle(
            RequerimientoCompraDetalle detalle,
            Integer idRequerimiento,
            String usuario) {

        if (detalle == null) {
            return "idRequerimiento=" + idRequerimiento
                    + ", detalle=null"
                    + ", usuario=" + usuario;
        }

        return "idDetalle=" + detalle.getId()
                + ", idRequerimiento=" + idRequerimiento
                + ", tipoItem=" + detalle.getTipoItemNormalizado()
                + ", idPrestacion=" + detalle.getIdPrestacion()
                + ", idTipoNomenclador=" + detalle.getIdTipoNomenclador()
                + ", idMedicamento=" + detalle.getIdMedicamento()
                + ", cantidad=" + detalle.getCantidad()
                + ", usuario=" + usuario;
    }

    private String construirContextoPresupuesto(
            RequerimientoCompraPresupuesto presupuesto,
            String usuario) {

        if (presupuesto == null) {
            return "presupuesto=null, usuario=" + usuario;
        }

        String contexto = "idRequerimiento="
                + presupuesto.getIdRequerimiento()
                + ", tipoDocumento="
                + presupuesto.getTipoDocumento()
                + ", idPrestador="
                + presupuesto.getIdPrestador();

        if (presupuesto.isCotizacionEmpresa()) {
            contexto += ", empresaCuit="
                    + presupuesto.getEmpresaCuit()
                    + ", empresaSucursal="
                    + presupuesto.getEmpresaSucursal();
        }

        return contexto
                + ", dlFileEntryId="
                + presupuesto.getDlFileEntryId()
                + ", usuario=" + usuario;
    }

    public int agregarOrdenesMedicasRequerimientoPendiente(
            int idRequerimientoCompra,
            List<OrdenMedicaValidada> ordenesMedicas,
            GestorOrdenMedicaDocumento gestorDocumento,
            String usuario) throws Exception {

        EditarRequerimientoCompraServiceUtil.Transaccion transaccion =
                null;

        List<DocumentoComprasCreado> documentosCreados =
                new ArrayList<DocumentoComprasCreado>();

        try {
            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

            RequerimientoCompra actual =
                    BusquedaRequerimientoCompraServiceUtil
                            .getRequerimientoCompra(
                                    idRequerimientoCompra
                            );

            if (actual == null) {
                throw errorUsuario(
                        "El requerimiento ya no está disponible."
                );
            }

            if (!actual.puedeEditarEstructura()) {
                throw errorUsuario(
                        "Solo pueden agregarse adjuntos mientras "
                                + "el requerimiento se encuentra PENDIENTE."
                );
            }

            validarOrdenesMedicasParaCarga(
                    ordenesMedicas
            );

            if (gestorDocumento == null) {
                throw new IllegalStateException(
                        "No se obtuvo el gestor documental "
                                + "del adjunto."
                );
            }

            transaccion =
                    EditarRequerimientoCompraServiceUtil.abrirTransaccion();

            for (int i = 0;
                 i < ordenesMedicas.size();
                 i++) {

                OrdenMedicaValidada ordenMedica =
                        ordenesMedicas.get(i);

                DocumentoComprasCreado documento =
                        gestorDocumento.crearOrdenMedica(
                                idRequerimientoCompra,
                                ordenMedica
                        );

                documentosCreados.add(
                        documento
                );

                int idDocumento =
                        DocumentoLibraryComprasHelper.registrarOrdenMedicaEnTransaccion(
                                transaccion,
                                idRequerimientoCompra,
                                ordenMedica,
                                documento,
                                normalizarUsuario(usuario)
                        );

                if (idDocumento <= 0) {
                    throw new IllegalStateException(
                            "El registro del adjunto "
                                    + "no devolvió un identificador válido."
                    );
                }
            }

            transaccion.commit();

            return documentosCreados.size();

        } catch (Exception e) {
            boolean rollbackConfirmado =
                    transaccion == null;

            if (transaccion != null) {
                try {
                    transaccion.rollback();
                    rollbackConfirmado = true;

                } catch (Exception rollbackError) {
                    _log.error(
                            "No se pudo confirmar el rollback al agregar "
                                    + "adjuntos a un requerimiento existente.",
                            rollbackError
                    );
                }
            }

            if (rollbackConfirmado
                    && gestorDocumento != null
                    && !documentosCreados.isEmpty()) {

                compensarOrdenesMedicasCreadas(
                        documentosCreados,
                        gestorDocumento
                );
            }

            throw manejarErrorOperacion(
                    "agregar adjuntos al requerimiento",
                    "No se pudieron agregar los adjuntos. "
                            + "Vuelva a seleccionar las imágenes "
                            + "e intente nuevamente.",
                    e,
                    "idRequerimiento="
                            + idRequerimientoCompra
                            + ", usuario="
                            + usuario
                            + ", cantidadOrdenesMedicas="
                            + (ordenesMedicas != null
                            ? ordenesMedicas.size()
                            : 0)
            );

        } finally {
            if (transaccion != null) {
                transaccion.cerrar();
            }
        }
    }

    private RequerimientoCompra validarRequerimientoParaBorrarDetalle(
            Integer idRequerimiento) throws Exception {

        if (idRequerimiento == null
                || idRequerimiento.intValue() <= 0) {

            throw errorUsuario(
                    "Debe informar el requerimiento del detalle."
            );
        }

        RequerimientoCompra requerimiento =
                obtenerRequerimientoDetalle(
                        idRequerimiento.intValue()
                );

        if (requerimiento == null) {
            throw errorUsuario(
                    "El requerimiento ya no está disponible."
            );
        }

        if (!requerimiento.puedeEliminarDetalle()) {
            throw errorUsuario(
                    "Los detalles solo pueden quitarse mientras "
                            + "el requerimiento se encuentra PENDIENTE "
                            + "o ENVIADO A COTIZAR."
            );
        }

        return requerimiento;
    }
}
