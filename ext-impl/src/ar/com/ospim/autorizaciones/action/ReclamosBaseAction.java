package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacionalCuenta;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.Prestador.TipoPrestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.util.StringUtils;

public class ReclamosBaseAction extends PortletAction {

    private Logger _log = Logger.getLogger(this.getClass());

    protected Prestador getPrestadorEntry(HttpServletRequest request)
            throws Exception {

        Prestador prestador = null;
        String idString = request.getParameter("prestador_id");
        if (idString == null || idString.trim().equals("")) {
            idString = (String) request.getAttribute("prestador_id");
        }
        if (idString != null && !idString.trim().equals("")) {
            int id = Integer.parseInt(idString);
            if (id > 0) {
                prestador = PrestadorServiceUtil.getPrestador(id);
            }
        }
        return prestador;
    }

    public Prestador getOtrosDatosFromRequest(
            HttpServletRequest req,
            Prestador prestador) {

        return prestador;
    }

    public ReclamoPrestacional getReclamoPrestacionalFromRequest(
            HttpServletRequest req,
            ReclamoPrestacional reclamoprestacional,
            String cmdAction,
            String cmd) {

        int inte = 0;
        Date fechaOspim;
        Date fechaSeccional;
        Date fechaCierre;

        SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
        formatoDePeriodo.setLenient(false);

        String fechaOspimDia = ParamUtil.getString(req, "fechaospimDia");
        String fechaOspimMes = ParamUtil.getString(req, "fechaospimMes");
        String fechaOspimAnio = ParamUtil.getString(req, "fechaospimAnio");
        String fechaSeccionalDia = ParamUtil.getString(req, "fechaseccionalDia");
        String fechaSeccionalMes = ParamUtil.getString(req, "fechaseccionalMes");
        String fechaSeccionalAnio = ParamUtil.getString(req, "fechaseccionalAnio");
        String fechacierreDia = ParamUtil.getString(req, "fechacierreDia");
        String fechacierreMes = ParamUtil.getString(req, "fechacierreMes");
        String fechacierreAnio = ParamUtil.getString(req, "fechacierreAnio");

        String justificacionMedica = ParamUtil.getString(
                req,
                "justificacionmedcica_reclamo"
        );
        String dictamenComision = ParamUtil.getString(
                req,
                "dictamencomision_reclamo"
        );

        String sector = ParamUtil.getString(req, "sector");
        String estado = ParamUtil.getString(req, "estado");
        String cuil = ParamUtil.getString(req, "cuil");
        String intAux = ParamUtil.getString(req, "inte");
        if (!StringUtils.checkEmpty(intAux)) {
            inte = Integer.parseInt(intAux);
        }

        String reclamoObservacionCierre = ParamUtil.getString(
                req,
                "reclamo_observacion_cierre"
        );
        int tipoGestionVisible = ParamUtil.getInteger(
                req,
                "tipo_gestion_cierre_reclamo"
        );
        int tipoGestionCierreReclamo = ParamUtil.getInteger(
                req,
                "tipogestion"
        );
        if (tipoGestionCierreReclamo <= 0 && tipoGestionVisible > 0) {
            tipoGestionCierreReclamo = tipoGestionVisible;
        }

        int idObservacionMedica = ParamUtil.getInteger(
                req,
                "observacion_medica"
        );
        boolean reclamoPsFacturaOspim = ParamUtil.getBoolean(
                req,
                "reclamo_ps_factura_ospim"
        );
        boolean reclamoPorNegociar = ParamUtil.getBoolean(
                req,
                "reclamo_a_negociar"
        );
        boolean debitoPrestador = ParamUtil.getBoolean(
                req,
                "debitoprestadora"
        );
        boolean superIntendencia = ParamUtil.getBoolean(
                req,
                "chk_superintendencia"
        );
        boolean amparo = ParamUtil.getBoolean(req, "chk_amparo");
        boolean recuperable = ParamUtil.getBoolean(req, "chk_recuperable");
        boolean enTramite = ParamUtil.getBoolean(req, "chk_entramite");
        boolean incluidoConvenioGerenciadora = ParamUtil.getBoolean(
                req,
                "incluido_convenio_gerenciadora"
        );
        boolean dosporciento = ParamUtil.getBoolean(req, "dosporciento");

        String diagnostico = ParamUtil.getString(req, "diagnostico");
        String codigoCie10 = ParamUtil.getString(req, "codigoCie10");
        String evaluacion = ParamUtil.getString(req, "evaluacionreclamo");
        String tipoPedido = ParamUtil.getString(req, "tipopedido");
        Integer nroLote = ParamUtil.getInteger(req, "nroLote");
        int casoVinculado = ParamUtil.getInteger(req, "caso_vinculado");
        int codIntegracion = ParamUtil.getInteger(req, "integracion");

        ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO evaluacionReclamo =
                parseEvaluacionReclamo(evaluacion);

        fechaOspim = parseFechaOpcional(
                formatoDePeriodo,
                fechaOspimDia,
                fechaOspimMes,
                fechaOspimAnio,
                "Fecha OSPIM"
        );
        fechaSeccional = parseFechaOpcional(
                formatoDePeriodo,
                fechaSeccionalDia,
                fechaSeccionalMes,
                fechaSeccionalAnio,
                "Fecha Seccional"
        );
        fechaCierre = parseFechaOpcional(
                formatoDePeriodo,
                fechacierreDia,
                fechacierreMes,
                fechacierreAnio,
                "Fecha de cierre"
        );

        Afiliado afi = buscarAfiliado(cuil, inte, "afiliado del reclamo");
        Afiliado afiliadoTitular = buscarAfiliado(
                cuil,
                0,
                "titular del reclamo"
        );

        if (cmdAction != null
                && WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL
                        .equals(cmdAction)) {
            fechaOspim = new Date();
        }

        ReclamoPrestacionalCuenta cuenta = construirCuenta(req);

        try {
            if (WebKeysAutorizaciones.CUENTA.equals(cmd)) {
                reclamoprestacional = new ReclamoPrestacional();
                reclamoprestacional.setCuenta(cuenta);
            } else {
                if (StringUtils.checkEmpty(estado)) {
                    throw new IllegalArgumentException(
                            "No se informó el estado del reclamo."
                    );
                }

                reclamoprestacional = new ReclamoPrestacional(
                        cuil,
                        inte,
                        fechaOspim,
                        sector,
                        fechaSeccional,
                        Integer.parseInt(estado),
                        fechaCierre,
                        reclamoObservacionCierre,
                        tipoGestionCierreReclamo,
                        reclamoPsFacturaOspim,
                        reclamoPorNegociar,
                        superIntendencia,
                        amparo,
                        recuperable,
                        enTramite,
                        incluidoConvenioGerenciadora,
                        casoVinculado,
                        dosporciento,
                        dictamenComision,
                        justificacionMedica,
                        diagnostico,
                        codigoCie10,
                        tipoPedido,
                        debitoPrestador,
                        evaluacionReclamo,
                        afi,
                        idObservacionMedica,
                        codIntegracion
                );
            }
        } catch (Exception e) {
            _log.error(
                    "No se pudo reconstruir el Reclamo Prestacional desde "
                            + "la solicitud.",
                    e
            );
            throw new IllegalArgumentException(
                    "Los datos del Reclamo Prestacional son inválidos.",
                    e
            );
        }

        if (reclamoprestacional == null) {
            throw new IllegalStateException(
                    "La reconstrucción del Reclamo Prestacional no produjo "
                            + "un objeto válido."
            );
        }

        reclamoprestacional.setNroLote(nroLote);
        int idReclamo = ParamUtil.getInteger(req, "id_reclamosel");
        reclamoprestacional.setId(idReclamo);
        reclamoprestacional.setAfiliadoTitular(afiliadoTitular);

        if (!WebKeysAutorizaciones.CUENTA.equals(cmd)
                && "REINTEGRO".equalsIgnoreCase(
                        reclamoprestacional.getTipoPedido()
                )) {
            try {
                ReclamoPrestacional reclamoPrestacionalAux =
                        ReclamosPrestacionesServiceUtil
                                .getReclamoPrestacional(idReclamo);

                if (reclamoPrestacionalAux != null
                        && reclamoPrestacionalAux.getCuenta() != null) {
                    reclamoprestacional.setCuenta(
                            reclamoPrestacionalAux.getCuenta()
                    );
                }
            } catch (Exception e) {
                _log.warn(
                        "No se pudo recuperar la cuenta bancaria del reclamo "
                                + idReclamo,
                        e
                );
            }
        }

        return reclamoprestacional;
    }

    public ReclamoPrestacional getReclamoPrestacionalFromRequest(
            RenderRequest req,
            ReclamoPrestacional reclamoPrestacional) {

        int inte = Integer.parseInt(ParamUtil.getString(req, "inte"));
        SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
        formatoDePeriodo.setLenient(false);

        String fechaOspimDia = ParamUtil.getString(req, "fechaospimDia");
        String fechaOspimMes = ParamUtil.getString(req, "fechaospimMes");
        String fechaOspimAnio = ParamUtil.getString(req, "fechaospimAnio");
        String fechaSeccionalDia = ParamUtil.getString(req, "fechaseccionalDia");
        String fechaSeccionalMes = ParamUtil.getString(req, "fechaseccionalMes");
        String fechaSeccionalAnio = ParamUtil.getString(req, "fechaseccionalAnio");
        String fechaCierreDia = ParamUtil.getString(req, "fechacierreDia");
        String fechaCierreMes = ParamUtil.getString(req, "fechacierreMes");
        String fechaCierreAnio = ParamUtil.getString(req, "fechacierreAnio");

        String justificacionMedica = ParamUtil.getString(
                req,
                "justificacionmedcica_reclamo"
        );
        String dictamenComision = ParamUtil.getString(
                req,
                "dictamencomision_reclamo"
        );
        String sector = ParamUtil.getString(req, "sector");
        String estado = ParamUtil.getString(req, "estado");
        String cuil = ParamUtil.getString(req, "cuil");
        String reclamoObservacionCierre = ParamUtil.getString(
                req,
                "reclamo_observacion_cierre"
        );
        int tipoGestionCierreReclamo = ParamUtil.getInteger(
                req,
                "tipogestion"
        );
        int tipoGestionVisible = ParamUtil.getInteger(
                req,
                "tipo_gestion_cierre_reclamo"
        );
        if (tipoGestionCierreReclamo <= 0 && tipoGestionVisible > 0) {
            tipoGestionCierreReclamo = tipoGestionVisible;
        }

        int idObservacionMedica = ParamUtil.getInteger(
                req,
                "observacion_medica"
        );
        boolean reclamoPsFacturaOspim = ParamUtil.getBoolean(
                req,
                "reclamo_ps_factura_ospim"
        );
        boolean reclamoPorNegociar = ParamUtil.getBoolean(
                req,
                "reclamo_a_negociar"
        );
        boolean debitoPrestador = ParamUtil.getBoolean(
                req,
                "debitoprestadora"
        );
        boolean recuperable = ParamUtil.getBoolean(req, "chk_recuperable");
        boolean superIntendencia = ParamUtil.getBoolean(
                req,
                "chk_superintendencia"
        );
        boolean amparo = ParamUtil.getBoolean(req, "chk_amparo");
        boolean enTramite = ParamUtil.getBoolean(req, "chk_entramite");
        boolean convenioGerenciadora = ParamUtil.getBoolean(
                req,
                "incluido_convenio_gerenciadora"
        );
        boolean dosporciento = ParamUtil.getBoolean(req, "dosporciento");

        String diagnostico = ParamUtil.getString(req, "diagnostico");
        String codigoCie10 = ParamUtil.getString(req, "codigoCie10");
        String tipoPedido = ParamUtil.getString(req, "tipopedido");
        String evaluacion = ParamUtil.getString(req, "evaluacionreclamo");
        int casoVinculado = ParamUtil.getInteger(req, "caso_vinculado");
        int codIntegracion = ParamUtil.getInteger(req, "integracion");

        ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO evaluacionReclamo =
                parseEvaluacionReclamo(evaluacion);

        Date fechaOspim = parseFechaOpcional(
                formatoDePeriodo,
                fechaOspimDia,
                fechaOspimMes,
                fechaOspimAnio,
                "Fecha OSPIM"
        );
        Date fechaSeccional = parseFechaOpcional(
                formatoDePeriodo,
                fechaSeccionalDia,
                fechaSeccionalMes,
                fechaSeccionalAnio,
                "Fecha Seccional"
        );
        Date fechaCierre = parseFechaOpcional(
                formatoDePeriodo,
                fechaCierreDia,
                fechaCierreMes,
                fechaCierreAnio,
                "Fecha de cierre"
        );
        Afiliado afi = buscarAfiliado(cuil, inte, "afiliado del reclamo");

        try {
            if (StringUtils.checkEmpty(estado)) {
                throw new IllegalArgumentException(
                        "No se informó el estado del reclamo."
                );
            }

            reclamoPrestacional = new ReclamoPrestacional(
                    cuil,
                    inte,
                    fechaOspim,
                    sector,
                    fechaSeccional,
                    Integer.parseInt(estado),
                    fechaCierre,
                    reclamoObservacionCierre,
                    tipoGestionCierreReclamo,
                    reclamoPsFacturaOspim,
                    reclamoPorNegociar,
                    superIntendencia,
                    amparo,
                    recuperable,
                    enTramite,
                    convenioGerenciadora,
                    casoVinculado,
                    dosporciento,
                    dictamenComision,
                    justificacionMedica,
                    diagnostico,
                    codigoCie10,
                    tipoPedido,
                    debitoPrestador,
                    evaluacionReclamo,
                    afi,
                    idObservacionMedica,
                    codIntegracion
            );
        } catch (Exception e) {
            _log.error(
                    "No se pudo reconstruir el Reclamo Prestacional desde "
                            + "RenderRequest.",
                    e
            );
            throw new IllegalArgumentException(
                    "Los datos del Reclamo Prestacional son inválidos.",
                    e
            );
        }

        if (reclamoPrestacional == null) {
            throw new IllegalStateException(
                    "La reconstrucción del Reclamo Prestacional no produjo "
                            + "un objeto válido."
            );
        }

        return reclamoPrestacional;
    }

    public Prestador getPrestadorFromRequest(
            HttpServletRequest req,
            Prestador prestador) {

        String cuit = ParamUtil.getString(req, "cuit");
        String desc = ParamUtil.getString(req, "desc");
        String ciaSeguro = ParamUtil.getString(req, "compania_seguro");
        boolean seguroCobertura = ParamUtil.getBoolean(
                req,
                "seguro_cobertura"
        );
        boolean certificacionProfesional = ParamUtil.getBoolean(
                req,
                "certificacion"
        );
        String otorgaCertificacion = ParamUtil.getString(req, "otorga_cert");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");

        String seguroFechaVtoDia = ParamUtil.getString(req, "seguroFechaVtoDia");
        String seguroFechaVtoMes = ParamUtil.getString(req, "seguroFechaVtoMes");
        String seguroFechaVtoAnio = ParamUtil.getString(req, "seguroFechaVtoAnio");
        Date fechaVtoSeguro = null;
        try {
            fechaVtoSeguro = formatoDePeriodo.parse(
                    seguroFechaVtoDia + "/"
                            + (Integer.parseInt(seguroFechaVtoMes) + 1)
                            + "/" + seguroFechaVtoAnio
            );
        } catch (Exception e) {
            fechaVtoSeguro = null;
        }

        String certificacionFechaVtoDia = ParamUtil.getString(
                req,
                "certificacionFechaVtoDia"
        );
        String certificacionFechaVtoMes = ParamUtil.getString(
                req,
                "certificacionFechaVtoMes"
        );
        String certificacionFechaVtoAnio = ParamUtil.getString(
                req,
                "certificacionFechaVtoAnio"
        );
        Date fechaVtoCertificacion = null;
        try {
            fechaVtoCertificacion = formatoDePeriodo.parse(
                    certificacionFechaVtoDia + "/"
                            + (Integer.parseInt(certificacionFechaVtoMes) + 1)
                            + "/" + certificacionFechaVtoAnio
            );
        } catch (Exception e) {
            fechaVtoCertificacion = null;
        }

        String contacto = ParamUtil.getString(req, "contacto");
        String obs = ParamUtil.getString(req, "observaciones");
        int idPrestador = ParamUtil.getInteger(req, "id_prestador");
        String codigoHospital = ParamUtil.getString(req, "codigo_hospital");
        int idTipoPrest = ParamUtil.getInteger(req, "tipo_prestador");
        TipoPrestador tipoPrestador = new TipoPrestador(idTipoPrest, "");

        prestador = new Prestador(
                idPrestador,
                cuit,
                tipoPrestador,
                contacto.toUpperCase(),
                obs,
                desc.toUpperCase(),
                codigoHospital,
                ciaSeguro.toUpperCase(),
                seguroCobertura,
                certificacionProfesional,
                otorgaCertificacion.toUpperCase(),
                fechaVtoSeguro,
                fechaVtoCertificacion
        );

        return prestador;
    }

    private ReclamoPrestacionalCuenta construirCuenta(HttpServletRequest req) {
        ReclamoPrestacionalCuenta cuenta = new ReclamoPrestacionalCuenta();
        String titular = ParamUtil.getString(req, "cmb_titular");

        if ("0".equals(titular)) {
            cuenta.setIdReclamoPrestacional(
                    ParamUtil.getInteger(req, "id_reclamosel")
            );
            cuenta.setCbu(ParamUtil.getString(req, "cuenta_cbu"));
            cuenta.setEmail(ParamUtil.getString(req, "cuenta_email"));
            cuenta.setCuil(ParamUtil.getString(req, "cuil_titular_cuenta"));

            String denominacion = ParamUtil.getString(req, "denominacion");
            String[] partes = denominacion.split(",", 2);
            if (partes.length != 2) {
                throw new IllegalArgumentException(
                        "La denominación de la cuenta debe contener apellido "
                                + "y nombre separados por coma."
                );
            }
            cuenta.setApellido(partes[0].trim());
            cuenta.setNombre(partes[1].trim());
            cuenta.setCmbTitular(titular);
            cuenta.setCuilGrupoFamiliar(
                    ParamUtil.getString(req, "cuil_grupo_familar")
            );
            String cbu = ParamUtil.getString(req, "file_cbu");
            if (cbu != null && !"0".equals(cbu)) {
                cuenta.setImagenCBU(cbu);
            }
        } else if ("1".equals(titular)) {
            cuenta.setIdReclamoPrestacional(
                    ParamUtil.getInteger(req, "id_reclamosel")
            );
            cuenta.setCbu(
                    ParamUtil.getString(req, "cuenta_cbu_autorizado")
            );
            cuenta.setEmail(
                    ParamUtil.getString(req, "cuenta_email_autorizado")
            );
            cuenta.setCuil(ParamUtil.getString(req, "cuil_autorizado"));
            cuenta.setApellido(
                    ParamUtil.getString(req, "apellido_autorizado")
            );
            cuenta.setNombre(ParamUtil.getString(req, "nombre_autorizado"));
            cuenta.setCuilGrupoFamiliar(
                    ParamUtil.getString(req, "cuil_grupo_familar")
            );
            cuenta.setCmbTitular(titular);

            String cbu = ParamUtil.getString(req, "file_cbu");
            String notaAutorizada = ParamUtil.getString(
                    req,
                    "file_nota_autorizada"
            );
            if (cbu != null && !"0".equals(cbu)) {
                cuenta.setImagenCBU(cbu);
                cuenta.setImagenNotaAutorizada(notaAutorizada);
            }
        } else if ("2".equals(titular)) {
            String idSeccional = ParamUtil.getString(req, "id_seccional");
            if (StringUtils.checkEmpty(idSeccional)) {
                throw new IllegalArgumentException(
                        "No se informó la seccional titular de la cuenta."
                );
            }

            String email = null;
            List<ContactoElectronico> contactos =
                    SeccionalServiceUtil.buscarContactosSeccionalEmail(
                            Integer.parseInt(idSeccional)
                    );
            if (contactos != null) {
                for (ContactoElectronico contactoElectronico : contactos) {
                    email = contactoElectronico.getContacto();
                }
            }

            Seccional seccional;
            try {
                seccional = SeccionalServiceUtil.buscarSeccionalById(
                        Integer.parseInt(idSeccional)
                );
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "No se pudo recuperar la seccional de la cuenta.",
                        e
                );
            }

            if (seccional == null) {
                throw new IllegalArgumentException(
                        "La seccional informada no existe."
                );
            }

            String cuit = WebKeysGlobal.CUIT_UOMA;
            cuenta.setIdReclamoPrestacional(
                    ParamUtil.getInteger(req, "id_reclamosel")
            );
            cuenta.setCmbTitular(titular);
            cuenta.setEmail(email);
            cuenta.setCuil(cuit);
            cuenta.setCuilGrupoFamiliar(cuit);
            cuenta.setCbu(seccional.getCBU());
            cuenta.setApellido("Seccional");
            cuenta.setNombre(seccional.getDescripcion());
        }

        return cuenta;
    }

    private Afiliado buscarAfiliado(
            String cuil,
            int integrante,
            String contexto) {

        try {
            return EditarAfiliadoServiceUtil
                    .getAfiliadoEntryInclusoDadoBaja(cuil, integrante);
        } catch (NoSuchAfiliadoEntryException e) {
            throw new IllegalArgumentException(
                    "No se encontró el " + contexto + ".",
                    e
            );
        } catch (SystemException e) {
            throw new IllegalStateException(
                    "No se pudo consultar el " + contexto + ".",
                    e
            );
        }
    }

    private Date parseFechaOpcional(
            SimpleDateFormat formato,
            String dia,
            String mes,
            String anio,
            String etiqueta) {

        boolean vacia = StringUtils.checkEmpty(dia)
                && StringUtils.checkEmpty(mes)
                && StringUtils.checkEmpty(anio);
        if (vacia) {
            return null;
        }

        boolean incompleta = StringUtils.checkEmpty(dia)
                || StringUtils.checkEmpty(mes)
                || StringUtils.checkEmpty(anio);
        if (incompleta) {
            throw new IllegalArgumentException(
                    etiqueta + " incompleta."
            );
        }

        try {
            return formato.parse(
                    dia + "/" + (Integer.parseInt(mes) + 1) + "/" + anio
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    etiqueta + " inválida.",
                    e
            );
        }
    }

    private ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO
            parseEvaluacionReclamo(String evaluacion) {

        if (StringUtils.checkEmpty(evaluacion)) {
            return ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO
                    .SINEVALUACION;
        }

        String normalizada = evaluacion.trim().toUpperCase();

        if ("AUTORIZADO".equals(normalizada)
                || "AUTORIZADA".equals(normalizada)) {
            return ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO
                    .AUTORIZADA;
        }

        if ("RECHAZADO".equals(normalizada)
                || "RECHAZADA".equals(normalizada)) {
            return ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO
                    .RECHAZADA;
        }

        if ("SINEVALUACION".equals(normalizada)
                || "SIN_EVALUACION".equals(normalizada)) {
            return ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO
                    .SINEVALUACION;
        }

        if ("SINVALOR".equals(normalizada)) {
            return ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO
                    .SINVALOR;
        }

        throw new IllegalArgumentException(
                "Valor de evaluación de reclamo inválido: " + evaluacion
        );
    }
}
