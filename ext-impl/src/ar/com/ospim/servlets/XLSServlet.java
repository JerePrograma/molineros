package ar.com.ospim.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.afiliados.reportes.ReporteAfiliadosAnsesExcel;
import ar.com.ospim.afiliados.reportes.ReporteAfiliadosBajaExcel;
import ar.com.ospim.afiliados.reportes.ReporteAmtimaPmiExcel;
import ar.com.ospim.afiliados.reportes.ReporteBonosSeccionalExcel;
import ar.com.ospim.afiliados.reportes.ReporteCantBonosSeccionalExcel;
import ar.com.ospim.afiliados.reportes.ReporteCantBonosSeccionalExcelVent;
import ar.com.ospim.afiliados.reportes.ReporteChequesPendientesCobroExcel;
import ar.com.ospim.afiliados.reportes.ReporteConsultasIGSExcel;
import ar.com.ospim.afiliados.reportes.ReporteCredenEmitidasExcel;
import ar.com.ospim.afiliados.reportes.ReporteDesreguladoSinAporteExcel;
import ar.com.ospim.afiliados.reportes.ReporteEOAFExcel;
import ar.com.ospim.afiliados.reportes.ReporteESFCExcel;
import ar.com.ospim.afiliados.reportes.ReporteInformeAportesMonotributistasExcel;
import ar.com.ospim.afiliados.reportes.ReporteLegajosProcesadosExcel;
import ar.com.ospim.afiliados.reportes.ReporteListadoPadron;
import ar.com.ospim.afiliados.reportes.ReporteOpcionesExcel;
import ar.com.ospim.afiliados.reportes.ReportePanelControlAfiliadosExcel;
import ar.com.ospim.afiliados.reportes.ReportePosiblesInconsistenciasExcel;
import ar.com.ospim.afiliados.reportes.ReporteResultBusquedaBonosSeccionalExcel;
import ar.com.ospim.afiliados.reportes.action.ReporteSeccional;
import ar.com.ospim.autorizaciones.reportes.action.ReporteAutorizacionesPmiExcel;
import ar.com.ospim.autorizaciones.reportes.action.ReporteAutorizacionesPrestacionalesExcel;
import ar.com.ospim.autorizaciones.reportes.action.ReporteEquiposInterDisciplinarios;
import ar.com.ospim.autorizaciones.reportes.action.ReporteIntegracionErroresExcel;
import ar.com.ospim.autorizaciones.reportes.action.ReporteIntegracionInconsistenciaExcel;
import ar.com.ospim.autorizaciones.reportes.action.ReporteIntegracionLiquidacionExcel;
import ar.com.ospim.autorizaciones.reportes.action.ReporteIntegracionLiquidacionSuperintendenciaExcel;
import ar.com.ospim.autorizaciones.reportes.action.ReporteInterbanking;
import ar.com.ospim.autorizaciones.reportes.action.ReportePreautorizacionEstadosExcel;
import ar.com.ospim.autorizaciones.reportes.action.ReportePreautorizacionExcel;
import ar.com.ospim.autorizaciones.reportes.action.ReportePrestacionesAutorizadasExcel;
import ar.com.ospim.autorizaciones.reportes.action.ReporteReclamosPrestacionales;
import ar.com.ospim.autorizaciones.reportes.action.ReporteSeguimientoSurExcel;
import ar.com.ospim.autorizaciones.reportes.action.ReporteSituacionMedica;
import ar.com.ospim.crm.reportes.ReporteCrmContactoExcel;
import ar.com.ospim.crm.reportes.ReporteCrmReclamosExcel;
import ar.com.ospim.estudioisidro.reportes.ReporteDemandasExcel;
import ar.com.ospim.estudioisidro.reportes.ReporteEstadisticoSeguimientoEmpresasExcel;
import ar.com.ospim.estudioisidro.reportes.ReporteSeguimientoEmpresasExcel;
import ar.com.ospim.farmacia.action.ReporteReintegrosFarmacia;
import ar.com.ospim.farmacia.ordenespago.reportes.ReporteOPReintegrosFarmacia;
import ar.com.ospim.farmacia.ordenespago.reportes.ReporteOPReintegrosFarmaciaPresta;
import ar.com.ospim.farmacia.reportes.GeneraVademecumAltasBajasXLS;
import ar.com.ospim.farmacia.reportes.GeneraVademecumXLS;
import ar.com.ospim.farmaciaOspim.reportes.action.ReporteArchivoAdmifarmMonotributo;
import ar.com.ospim.farmaciaOspim.reportes.action.ReporteArchivoAdmifarmOspimGeneral;
import ar.com.ospim.farmaciaOspim.reportes.action.ReporteDesgloseFarmacia;
import ar.com.ospim.farmaciaOspim.reportes.action.ReporteMedicamentosOspim;
import ar.com.ospim.farmaciaOspim.reportes.action.ReportePrestadoresInexistentesExcel;
import ar.com.ospim.liquidaciones.action.ReporteReintegros;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteOPReintegros;
import ar.com.ospim.liquidaciones.reportes.action.ReporteDebitosaTercerizadorasExcel;
import ar.com.ospim.liquidaciones.reportes.action.ReporteLiquidacionesExcel;
import ar.com.ospim.liquidaciones.reportes.action.ReporteLiquidacionesFarmaciaExcel;
import ar.com.ospim.liquidaciones.reportes.action.ReporteOrdenesPagoCompletoExcel;
import ar.com.ospim.liquidaciones.reportes.action.ReporteOrdenesPagoExcel;
import ar.com.ospim.liquidaciones.reportes.action.ReporteReintegrosExcel;
import ar.com.ospim.liquidaciones.reportes.action.ReporteTratamientoDiscapacidadExcel;
import ar.com.ospim.novedades.reporte.ReporteNovedadPadronConsolidadoSSSExcel;
import ar.com.ospim.novedades.reporte.ReporteNovedadesEmpleadoresExcel;
import ar.com.ospim.novedades.reporte.ReporteNovedadesSSSExcel;
import ar.com.ospim.portalempleadores.reportes.ReporteCuentaCorrientePortalEmpleadoresExcel;
import ar.com.ospim.rrhh.reportes.action.ReporteRrhh;
import ar.com.ospim.tesoreria.reportes.ReporteAcreditacionesAFIPExcel;
import ar.com.ospim.tesoreria.reportes.ReporteActas;
import ar.com.ospim.tesoreria.reportes.ReporteAnticiposExcel;
import ar.com.ospim.tesoreria.reportes.ReporteAnticiposOPExcel;
import ar.com.ospim.tesoreria.reportes.ReporteAplicacionCobranzas;
import ar.com.ospim.tesoreria.reportes.ReporteAportesContribucionesExcel;
import ar.com.ospim.tesoreria.reportes.ReporteAportesNoOSExcel;
import ar.com.ospim.tesoreria.reportes.ReporteAportesPagoRamoExcel;
import ar.com.ospim.tesoreria.reportes.ReporteBoletaPortalEmpleadoresExcel;
import ar.com.ospim.tesoreria.reportes.ReporteCajaChicaExcel;
import ar.com.ospim.tesoreria.reportes.ReporteContabilidadBalanceGeneralExcel;
import ar.com.ospim.tesoreria.reportes.ReporteContabilidadBalanceSumasSaldosExcel;
import ar.com.ospim.tesoreria.reportes.ReporteContabilidadDiario;
import ar.com.ospim.tesoreria.reportes.ReporteContabilidadMayorGeneralExcel;
import ar.com.ospim.tesoreria.reportes.ReporteConvenios;
import ar.com.ospim.tesoreria.reportes.ReporteCuentasCorrienteActasYConveniosExcel;
import ar.com.ospim.tesoreria.reportes.ReporteCuentasCorrientePrestamosTurismoExcel;
import ar.com.ospim.tesoreria.reportes.ReporteCuentasCorrientesExcel;
import ar.com.ospim.tesoreria.reportes.ReporteDebitosTercerosExcel;
import ar.com.ospim.tesoreria.reportes.ReporteDeudaEmpresaPeriodoExcel;
import ar.com.ospim.tesoreria.reportes.ReporteEgresosPorConceptosExcel;
import ar.com.ospim.tesoreria.reportes.ReporteEstadoComprobantesExcel;
import ar.com.ospim.tesoreria.reportes.ReporteHospitalesAutogestionExcel;
import ar.com.ospim.tesoreria.reportes.ReporteIngresosDevengadosExcel;
import ar.com.ospim.tesoreria.reportes.ReporteJubiladosSitaciExcel;
import ar.com.ospim.tesoreria.reportes.ReporteLibroBancoExcel;
import ar.com.ospim.tesoreria.reportes.ReporteLibroCajaExcel;
import ar.com.ospim.tesoreria.reportes.ReporteLiqActaConvenio;
import ar.com.ospim.tesoreria.reportes.ReporteListadoValoresExcel;
import ar.com.ospim.tesoreria.reportes.ReporteListadodDeDeudasExcel;
import ar.com.ospim.tesoreria.reportes.ReporteNomencladorConcepto;
import ar.com.ospim.tesoreria.reportes.ReporteNuevosAfiliadosEmpresasExcel;
import ar.com.ospim.tesoreria.reportes.ReportePreciosPlanesSuperadores;
import ar.com.ospim.tesoreria.reportes.ReportePrestamosTurismoExcel;
import ar.com.ospim.tesoreria.reportes.ReporteRankingDeudaEmpresasExcel;
import ar.com.ospim.tesoreria.reportes.ReporteRecibosExcel;
import ar.com.ospim.tesoreria.reportes.ReporteResumenProcesoCalcDeudaMasivoExcel;
import ar.com.ospim.tesoreria.reportes.ReporteSubdiarioEgresoExcel;
import ar.com.ospim.tesoreria.reportes.ReporteSubdiarioEgresoInterbankingExcel;
import ar.com.ospim.tesoreria.reportes.ReporteSubdiarioIngresoExcel;
import ar.com.ospim.tesoreria.reportes.ReporteUltimosComprobantesCajaChicaExcel;
import ar.com.ospim.tesoreria.reportes.action.ReporteDerivacionTercerizadorasExcel;
import ar.com.ospim.tesoreria.reportes.action.ReporteDesempleoSSExcel;
import ar.com.ospim.tesoreria.reportes.action.ReporteEgresosLiquidacionesExcel;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.centro_costo.ReporteCentrosDeCostoExcel;
import ar.com.uoma.centro_costo.ReportePorCuentaContablePorCentroCosto;
import ar.com.uoma.reportes.ControlIngresosEgresosEmpresas1Excel;
import ar.com.uoma.reportes.ControlIngresosEgresosNivel1Excel;
import ar.com.uoma.reportes.ControlIngresosEgresosNivel2Excel;
import ar.com.uoma.reportes.ReporteActasAcuerdosExcel;
import ar.com.uoma.reportes.ReporteCorrespondenciaExcel;
import ar.com.uoma.reportes.ReporteFacturasExcel;
import ar.com.uoma.reportes.ReporteLibroIVAExcel;
import ar.com.uoma.reportes.ReportePercepcionesIIBBExcel;
import ar.com.uoma.reportes.ReporteDeudaCuentaCorrienteExcel;

public class XLSServlet extends HttpServlet {
	public static final String MIME_TYPE_XLS = "application/vnd.ms-excel; charset= UTF-8";
	private static final String LISTADO_BAJAS = "LISTADO_BAJAS";
	private static final String OP_REINTEGRO = "OP_REINTEGRO";
	private static final String REP_REINTEGRO = "REP_REINTEGRO";
	private static final String APORTES_CONTRIBUCIONES_EMP = "APORTES_CONTRIBUCIONES_EMP";
	private static final String ACTA_PERIODOS_DETALLE = "ACTA_PERIODOS_DETALLE";
	private static final String ACTA_NO_OS_PERIODOS_DETALLE = "ACTA_NO_OS_PERIODOS_DETALLE";
	private static final String ACTA_NO_OS_PERIODOS_NOMINA_DETALLE = "ACTA_NO_OS_PERIODOS_NOMINA_DETALLE";
	private static final String ACTA_NO_OS_PERIODOS_GENERAL = "ACTA_NO_OS_PERIODOS_GENERAL";
	private static final String LISTADO_PADRON = "LISTADO_PADRON";
	private static final String LISTADO_PADRON_SECCIONAL = "LISTADO_PADRON_SECCIONAL";
	private static final String REPORTE_APORTES = "REPORTE_APORTES";
	private static final String REPORTE_DEUDA_EMPRESA_PERIODO = "REPORTE_DEUDA_EMPRESA_PERIODO";
	private static final String REPORTE_DEUDA_EMPRESA_PERIODO_CONSOLIDADO = "REPORTE_DEUDA_EMPRESA_PERIODO_CONSOLIDADO";
	private static final String REPORTE_BOLETA_PORTAL_EMPLEADORES = "REPORTE_BOLETA_PORTAL_EMPLEADORES";
	private static final String LIBRO_BANCO = "LIBRO_BANCO";
	private static final String LIBRO_CAJA = "LIBRO_CAJA";
	private static final String LISTADO_CHEQUES = "LISTADO_CHEQUES";
	private static final String ACREDITACIONES_AFIP = "ACREDITACIONES_AFIP";
	private static final String REPORTE_ORDENES_PAGO = "REPORTE_ORDENES_PAGO";
	private static final String REPORTE_ORDENES_PAGO_COMPLETO = "REPORTE_ORDENES_PAGO_COMPLETO";
	private static final String REPORTE_BONOS_SECCIONAL = "REPORTE_BONOS_SECCIONAL";
	private static final String POSIBLES_INCONSISTENCIAS = "POSIBLES_INCONSISTENCIAS";
	private static final String CUENTAS_CORRIENTES = "CUENTAS_CORRIENTES";
	private static final String ESTADO_COMPROBANTES = "ESTADO_COMPROBANTES";
	private static final String CUENTAS_CORRIENTES_ACTAS_Y_CONV = "CUENTAS_CORRIENTES_ACTAS_Y_CONV";
	// Reporte Consolidado de CC Prestamos de Turimos
	private static final String CUENTAS_CORRIENTES_PRESTAMOS_TURISMO_HD = "CUENTAS_CORRIENTES_TUR_PRE_HD";
	// Reporte Detallado de CC Prestamos de Turimos
	private static final String CUENTAS_CORRIENTES_PRESTAMOS_TURISMO_IT = "CUENTAS_CORRIENTES_TUR_PRE_IT";	
	private static final String REPORTE_CANT_BONOS_SECCIONAL = "REPORTE_CANT_BONOS_SECCIONAL";
	private static final String REPORTE_CANT_BONOS_SECCIONAL_VENT = "REPORTE_CANT_BONOS_SECCIONAL_VENT";
	private static final String REPORTE_AFILIADOS_ANSES = "REPORTE_AFILIADOS_ANSES";
	private static final String REPORTE_RESULT_BUSQUEDA_BONOS_SECCIONAL = "REPORTE_RESULT_BUSQUEDA_BONOS_SECCIONAL";
	private static final String REPORTE_RESULT_BUSQUEDA_RECLAMOS_PRESTACIONALES = "REPORTE_RESULT_BUSQUEDA_RECLAMOS_PRESTACIONALES";
	private static final String REPORTE_SUBDIARIO_EGRESOS = "REPORTE_SUBDIARIO_EGRESOS";
	private static final String REPORTE_SUBDIARIO_EGRESOS_INTERBANKING = "REPORTE_SUBDIARIO_EGRESOS_INTERBANKING";
	private static final String REPORTE_SUBDIARIO_INGRESOS = "REPORTE_SUBDIARIO_INGRESOS";
	private static final String REPORTE_DESREGULADOS_SIN_APORTES = "REPORTE_DESREGULADOS_SIN_APORTES";
	private static final String REPORTE_RECIBOS = "REPORTE_RECIBOS";
	private static final String REPORTE_ANTICIPOS = "REPORTE_ANTICIPOS";
	private static final String REPORTE_LLAMADOS_ESTUDIO = "REPORTE_LLAMADOS_ESTUDIO";
	private static final String DETALLE_lIQUIDACION_DEBITOS_TERCEROS = "DETALLE_lIQUIDACION_DEBITOS_TERCEROS";
	private static final String REPORTE_REINTEGROS = "REPORTE_REINTEGROS";
	private static final String FICHA_DE_CONSUMO = "FICHA_DE_CONSUMO";
	private static final String FICHA_DE_FARMACIA = "FICHA_DE_FARMACIA";
	private static final String REPORTE_PMI = "REPORTE_PMI";
	private static final String REPORTE_DISCAPACIDAD = "REPORTE_DISCAPACIDAD";
	private static final String TRATAMIENTO_DISCAPACIDAD = "TRATAMIENTO_DISCAPACIDAD";
	private static final String REPORTE_LIQUIDACIONES = "REPORTE_LIQUIDACIONES";
	private static final String LISTADO_DE_DEUDA = "LISTADO_DE_DEUDA";
	private static final String OBTENER_VADEMECUM = "OBTENER_VADEMECUM";
	private static final String ALTAS_BAJAS_VADEMECUM = "ALTAS_BAJAS_VADEMECUM";
	private static final String EXPORTACION_PADRON_VADEMECUM = "EXPORTACION_PADRON_VADEMECUM";
	private static final String REPORTE_AMTIMA_PMI = "REPORTE_AMTIMA_PMI";
	private static final String OP_REINTEGRO_FARMACIA = "OP_REINTEGRO_FARMACIA";
	private static final String OP_REINTEGRO_FARMACIA_PRESTA = "OP_REINTEGRO_FARMACIA_PRESTA";
	private static final String REP_REINTEGRO_FARMACIA = "REP_REINTEGRO_FARMACIA";
	private static final String EGRESO_POR_CONCEPTOS = "EGRESO_POR_CONCEPTOS";
	private static final String REPORTE_ANTICIPOS_OP = "REPORTE_ANTICIPOS_OP";
	private static final String REPORTE_EGRESOS_LIQUIDACIONES = "REPORTE_EGRESOS_LIQUIDACIONES";
	private static final String REPORTE_LIQ_ACTA_CONVENIO = "REPORTE_LIQ_ACTA_CONVENIO";
	private static final String REPORTE_PANEL_CONTROL_AFILIADOS = "REPORTE_PANEL_CONTROL_AFILIADOS";
	private static final String REPORTE_ACTAS = "REPORTE_ACTAS";
	private static final String REPORTE_ACTAS_ESTUDIO = "REPORTE_ACTAS_ESTUDIO";
	private static final String REPORTE_CONVENIOS = "REPORTE_CONVENIOS";
	private static final String REPORTE_CONVENIOS_ESTUDIO = "REPORTE_CONVENIOS_ESTUDIO";
	private static final String REPORTE_APLICACION_COBRANZAS = "REPORTE_APLICACION_COBRANZAS";
	private static final String REPORTE_CORRESPONDENCIA = "REPORTE_CORRESPONDENCIA";
	private static final String REPORTE_CONTABILIDAD_DIARIO = "REPORTE_CONTABILIDAD_DIARIO";
	private static final String REPORTE_CONTABILIDAD_MAYOR_GENERAL = "REPORTE_MAYOR_GENERAL";
	private static final String REPORTE_CONTABILIDAD_BALANCE_SUMAS_SALDOS = "REPORTE_CONTABILIDAD_BALANCE_SUMAS_SALDOS";
	private static final String REPORTE_CONTABILIDAD_BALANCE_GENERAL = "REPORTE_CONTABILIDAD_BALANCE_GENERAL";
	private static final String REPORTE_DESEMPLEO_SS = "REPORTE_DESEMPLEO_SS";
	private static final String CUENTA_CORRIENTE_PORTAL_EMPLEADORES = "CUENTA_CORRIENTE_PORTAL_EMPLEADORES";
	private static final String REPORTE_RECIBOS_SEGUIMIENTO = "REPORTE_RECIBOS_SEGUIMIENTO";
	private static final String REPORTE_LECTURAS_ACCESO = "REPORTE_LECTURAS_ACCESO";
	private static final String REPORTE_CONTROL_ACCESO = "REPORTE_CONTROL_ACCESO";
	private static final String REPORTE_INFORMACION_PERSONAS = "REPORTE_INFORMACION_PERSONAS";
	private static final String REPORTE_BUSQUEDA_CORRESPONDENCIA = "REPORTE_BUSQUEDA_CORRESPONDENCIA";
	private static final String REPORTE_CORRESPONDENCIA_EMPAQUETADA = "REPORTE_CORRESPONDENCIA_EMPAQUETADA";
	private static final String EXPORTACION_NUEVAS_OPCIONES_SSS = "EXPORTACION_NUEVAS_OPCIONES_SSS";
	private static final String REPORTE_LISTADO_CREDEN_EMITIDAS = "REPORTE_LISTADO_CREDEN_EMITIDAS";
	private static final String REPORTE_LEGAJOS_PROCESADOS = "REPORTE_LEGAJOS_PROCESADOS";
	private static final String REPORTE_BUSQUEDA_NOVEDADES_EMPLEADORES = "REPORTE_BUSQUEDA_NOVEDADES_EMPLEADORES";
	private static final String REPORTE_BUSQUEDA_NOVEDADES_SSS = "REPORTE_BUSQUEDA_NOVEDADES_SSS";
	private static final String REPORTE_BUSQUEDA_CONTACTOSCRM = "REPORTE_BUSQUEDA_CONTACTOSCRM";
	private static final String ESTADISTICA_AGRUPADO_CONTACTOSCRM = "ESTADISTICA_AGRUPADO_CONTACTOSCRM";
	private static final String ESTADISTICA_RENDIMIENTO_CONTACTOSCRM = "ESTADISTICA_RENDIMIENTO_CONTACTOSCRM";
	private static final String ESTADISTICA_CIERRES_CONTACTOSCRM = "ESTADISTICA_CIERRES_CONTACTOSCRM";
	private static final String REPORTE_DERIVACIONES = "REPORTE_DERIVACIONES";
	private static final String ESTADISTICA_NOVEDADES_SSS_PROCESADAS = "ESTADISTICA_NOVEDADES_SSS_PROCESADAS";
	private static final String REPORTE_SEGUIMIENTOSUR = "REPORTE_SEGUIMIENTOSUR";
	private static final String LISTADO_ACTAS_ACUERDOS = "LISTADO_ACTAS_ACUERDOS";
	private static final String REPORTE_BUSQUEDA_RECLAMOSCRM = "REPORTE_BUSQUEDA_RECLAMOSCRM";
	private static final String REPORTE_CAJA_CHICA = "REPORTE_CAJA_CHICA";
	private static final String REPORTE_CONSULTA_IGS = "REPORTE_CONSULTA_IGS";
	private static final String REPORTE_ULTIMOS_COMPROBANTES_CAJA_CHICA = "REPORTE_ULTIMOS_COMPROBANTES_CAJA_CHICA";
	private static final String REPORTE_HOSPITALES_AUTOGESTION = "REPORTE_HOSPITALES_AUTOGESTION";
	private static final String REPORTE_RANKING_DEUDA_EMPRESAS = "REPORTE_RANKING_DEUDA_EMPRESAS";
	private static final String REPORTE_APORTES_PAGO_RAMO = "REPORTE_APORTES_PAGO_RAMO";
	private static final String REPORTE_NUEVOS_AFILIADOS_EMPRESAS = "REPORTE_NUEVOS_AFILIADOS_EMPRESAS";
	private static final String REPORTE_INGRESOS_DEVENGADOS = "REPORTE_INGRESOS_DEVENGADOS";
	private static final String REPORTE_INFORME_APORTES_MONOTRIBUTISTAS = "REPORTE_INFORME_APORTES_MONOTRIBUTISTAS";
	private static final String REPORTE_CHEQUES_PENDIENTES_COBRO = "REPORTE_CHEQUES_PENDIENTES_COBRO";
	private static final String REPORTE_ESTADISTICO_ESTUDIO = "REPORTE_ESTADISTICO_ESTUDIO";
	private static final String REPORTE_EOAF = "REPORTE_EOAF";
	private static final String REPORTE_ESFC = "REPORTE_ESFC";
	private static final String REPORTE_RESUMEN_CALC_DEUDA_MASIVO = "REPORTE_RESUMEN_CALC_DEUDA_MASIVO";
	private static final String CONTROL_INGRESOS_EGRESOS_NIVEL_1 = "CONTROL_INGRESOS_EGRESOS_NIVEL_1";
	private static final String CONTROL_INGRESOS_EGRESOS_NIVEL_2 = "CONTROL_INGRESOS_EGRESOS_NIVEL_2";
	private static final String CONTROL_INGRESOS_EGRESOS_EMPRESAS_1 = "CONTROL_INGRESOS_EGRESOS_EMPRESAS_1";
	private static final String REPORTE_ACTAS_CONVENIOS_ESTADISTICO_ESTUDIO = "REPORTE_ACTAS_CONVENIOS_ESTADISTICO_ESTUDIO";
	private static final String REPORTE_DETALLE_LOTE_PAGOS_SEGUIMIENTO_SUR = "REPORTE_DETALLE_LOTE_PAGOS_SEGUIMIENTO_SUR";
	private static final String REPORTE_PREAUTORIZACION = "REPORTE_PREAUTORIZACION";
	private static final String REPORTE_PREAUTORIZACION_ESTADISTICO_ESTADOS = "REPORTE_PREAUTORIZACION_ESTADISTICO_ESTADOS";
	private static final String REPORTE_CENTROS_COSTOS = "REPORTE_CENTROS_COSTOS";
	private static final String REPORTE_CENTROS_COSTOS_DETALLE = "REPORTE_CENTROS_COSTOS_DETALLE";
	private static final String REPORTE_RESULT_BUSQUEDA_EQUIPOS_INTERDISCIPLINARIOS = "REPORTE_RESULT_BUSQUEDA_EQUIPOS_INTERDISCIPLINARIOS";
	private static final String REPORTE_RESULT_BUSQUEDA_SECCIONALES = "REPORTE_RESULT_BUSQUEDA_SECCIONALES";
	private static final String REPORTE_RESULT_BUSQUEDA_SITUACION_MEDICA = "REPORTE_RESULT_BUSQUEDA_SITUACION_MEDICA";
	private static final String REPORTE_RESULT_BUSQUEDA_SITUACION_MEDICA_COMPLETO = "REPORTE_RESULT_BUSQUEDA_SITUACION_MEDICA_COMPLETO";
	private static final String REPORTE_RESULT_BUSQUEDA_MEDICAMENTOS_OSPIM = "REPORTE_RESULT_BUSQUEDA_MEDICAMENTOS_OSPIM";
	private static final String REPORTE_ARCHIVO_PREVENCION_FARMACIA_DESGLOSE_PERIODO = "REPORTE_ARCHIVO_PREVENCION_FARMACIA_DESGLOSE_PERIODO";
	private static final String REPORTE_ERRORES_INTEGRACION = "REPORTE_ERRORES_INTEGRACION";
	private static final String REPORTE_DETALLE_INTEGRACION = "REPORTE_DETALLE_INTEGRACION";
	private static final String REPORTE_DETALLE_LIQUIDACION_INTEGRACION = "REPORTE_DETALLE_LIQUIDACION_INTEGRACION";
	private static final String REPORTE_DETALLE_LIQUIDACION_INTEGRACION_CABECERA = "REPORTE_DETALLE_LIQUIDACION_INTEGRACION_CABECERA";
	private static final String REPORTE_DETALLE_HISTORICO_INTEGRACION = "REPORTE_DETALLE_HISTORICO_INTEGRACION";
	private static final String REPORTE_INCONSISTENCIAS_FECHA_TRANSFERENCIA_INTEGRACION = "REPORTE_INCONSISTENCIAS_FECHA_TRANSFERENCIA_INTEGRACION";
	private static final String REPORTE_LIQUIDACION_SUPERINTENDENCIA_INTEGRACION = "REPORTE_LIQUIDACION_SUPERINTENDENCIA_INTEGRACION";
	private static final String REPORTE_PRESTACIONES_AUTORIZADAS_PS = "REPORTE_PRESTACIONES_AUTORIZADAS_PS";
	private static final String ORDENES_PAGO = "ORDENES_PAGO";
	private static final String PERCEPCIONES_IIBB = "PERCEPCIONES_IIBB";
	private static final String LIBRO_IVA = "LIBRO_IVA";
	private static final String REPORTE_DEBITO_TERCERIZADORAS = "REPORTE_DEBITO_TERCERIZADORAS";
	private static final String REPORTE_PRESTADORES_INEXISTENTES = "REPORTE_PRESTADORES_INEXISTENTES";
	private static final String REPORTE_NOVEDAD_PADRON_CONSOLIDADO_SSS = "REPORTE_NOVEDAD_PADRON_CONSOLIDADO_SSS";
	private static final String REPORTE_FACTURAS = "REPORTE_FACTURAS";
	private static final String REPORTE_AUTORIZACIONES_PRESTACIONALES = "REPORTE_AUTORIZACIONES_PRESTACIONALES";
	private static final String REPORTE_DEUDA_CUENTACORRIENTE = "REPORTE_DEUDA_CUENTACORRIENTE";
	private static final String REPORTE_DEMANDAS = "REPORTE_DEMANDAS";
	private static final String REPORTE_PRESTAMOS_TURISMO = "REPORTE_PRESTAMOS_TURISMO";
	private static final String JUBILADOS_SITACI_DETALLE = "JUBILADOS_SITACI_DETALLE";
	private static final String REPORTE_ARCHIVO_ADMIFARM_PERIODO = "REPORTE_ARCHIVO_ADMIFARM_PERIODO";
	private static final String REPORTE_CENTROS_COSTOS_CONTABLE = "REPORTE_CENTROS_COSTOS_CONTABLE";
	private static final String	REPORTE_EXPORTAR_CENTROS_COSTOS_CONTABLES="REPORTE_EXPORTAR_CENTROS_COSTOS_CONTABLES";
	private static final String REPORTE_UPLOAD_ARCHIVOS="REPORTE_UPLOAD_ARCHIVOS";
	private static final String REPORTE_PRECIOS_FACTURACION="REPORTE_PRECIOS_FACTURACION";
	private static final String REPORTE_AJUSTES_FACTURACION="REPORTE_AJUSTES_FACTURACION";
	private static final String REPORTE_ARCHIVO_ADMIFARM_OSPIM_GENERAL_PERIODO = "REPORTE_ARCHIVO_ADMIFARM_OSPIM_GENERAL_PERIODO";

	

	private static Log _log = LogFactoryUtil.getLog(XLSServlet.class);
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		_log.debug("Generando reporte XLS");
		Workbook wb = generarReporte(req, res);
		res.setHeader("Cache-Control", "no-cache");
		res.setContentType(MIME_TYPE_XLS);
		if (wb instanceof SXSSFWorkbook) {
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			wb.write(outByteStream);

			byte[] outArray = outByteStream.toByteArray();
			OutputStream outStream = res.getOutputStream();
			outStream.write(outArray);
			outStream.flush();

		} else {
			// Write the output to a file
			OutputStream outStream = res.getOutputStream();
			wb.write(outStream);
			outStream.flush();
			outStream.close();

		}

		_log.debug("Terminando reporte XLS");
	}

	private Workbook generarReporte(HttpServletRequest req, HttpServletResponse res) {
		Workbook wb = null;

		String reporte = ParamUtil.getString(req, "reporte");
		if (reporte.equals(OP_REINTEGRO)) {
			wb = ReporteOPReintegros.generaReporteOPReintegros(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteOPReintegros.xls\"");
		} else if (reporte.equals(REP_REINTEGRO)) {
			wb = ReporteReintegros.generaReporteReintegros(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteReintegros.xls\"");
		} else if (reporte.equals(APORTES_CONTRIBUCIONES_EMP)) {
			wb = ReporteAportesContribucionesExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteAportesYContribuciones.xlsx\"");
		} else if (reporte.equals(ACTA_PERIODOS_DETALLE)) {
			String totales = req.getParameter("totales");
			boolean conTotales = false;
			if (StringUtils.checkNotEmpty(totales) && totales.equals("totales")) {
				conTotales = true;
			}
			wb = ReporteAportesContribucionesExcel.generaReporteNominaEmpresaFromActa(req, res);
			String nombreFile = "detalleNomina.xls";
			if (conTotales) {
				nombreFile = "acta.xls";
			}
			res.setHeader("Content-Disposition", "attachment; filename=\"" + nombreFile + "\"");
		} else if (reporte.equals(ACTA_NO_OS_PERIODOS_DETALLE)) {
			try {
				wb = ReporteAportesNoOSExcel.generaReportePeriodoEmpresaFromActaNoOSWorkBook(req, res);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String nombreFile = "detalleActa.xls";

			res.setHeader("Content-Disposition", "attachment; filename=\"" + nombreFile + "\"");
		} else if (reporte.equals(ACTA_NO_OS_PERIODOS_NOMINA_DETALLE)) {
			try {
				wb = ReporteAportesNoOSExcel.generaReporteNominaEmpresaFromActaNoOS(req, res);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String nombreFile = "detalleNominaActa.xls";

			res.setHeader("Content-Disposition", "attachment; filename=\"" + nombreFile + "\"");
		} else if (reporte.equals(ACTA_NO_OS_PERIODOS_GENERAL)) {
			try {
				wb = ReporteAportesNoOSExcel.generaReporteGralFromActaNoOS(req, res);
				ReporteAportesNoOSExcel.generaReportePeriodoEmpresaFromActaNoOS(req, res, (HSSFWorkbook) wb);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String nombreFile = "detalleGralActa.xls";

			res.setHeader("Content-Disposition", "attachment; filename=\"" + nombreFile + "\"");

		} else if (reporte.equals(LISTADO_PADRON)) {
			wb = ReporteListadoPadron.generaReportePadron(req, res);

			boolean esExportaTercerizadora = ParamUtil.getBoolean(req, "vistaTercerizadora");
			boolean esVistaAdmifarm = ParamUtil.getBoolean(req, "vistaAdmifarm");
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			String fecha = format.format(Calendar.getInstance().getTime());
			String salida = "OSPIM" + fecha + ".xlsx";
			if(esVistaAdmifarm) {
				res.setHeader("Content-Disposition", "attachment; filename=\"listadoPadronAdmifarm.xlsx\"");
			}else if (esExportaTercerizadora) {
				res.setHeader("Content-Disposition", "attachment;filename=" + salida);
			} else {
				res.setHeader("Content-Disposition", "attachment; filename=\"listadoPadron.xlsx\"");
			}
		} else if (reporte.equals(LISTADO_PADRON_SECCIONAL)) {
			wb = ReporteListadoPadron.generaReportePadron(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"listadoPadron.xlsx\"");
		} else if (reporte.equals(REPORTE_APORTES)) {
			wb = ar.com.ospim.afiliados.reportes.ReporteAportesContribucionesExcel.generaReporteAportes(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteAportes.xls\"");
		} else if (reporte.equals(REPORTE_DEUDA_EMPRESA_PERIODO)) {
			wb = ReporteDeudaEmpresaPeriodoExcel.generaReporteDeudaEmpresaPeriodo(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteDeudaEmpPeriodo.xls\"");
		} else if (reporte.equals(REPORTE_DEUDA_EMPRESA_PERIODO_CONSOLIDADO)) {
			wb = ReporteDeudaEmpresaPeriodoExcel.generaReporteDeudaEmpresaPeriodoConsolidado(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteDeudaEmpPeriodoConsolidado.xls\"");
		} else if (reporte.equals(REPORTE_BOLETA_PORTAL_EMPLEADORES)) {
			wb = ReporteBoletaPortalEmpleadoresExcel.generaReporteBoletaPortalEmpleadores(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"ReporteBoletasPortalEmpleadores.xls\"");
		} else if (reporte.equals(LIBRO_BANCO)) {
			wb = ReporteLibroBancoExcel.generaReporteLibroBanco(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"libroBanco.xls\"");
		} else if (reporte.equals(ACREDITACIONES_AFIP)) {
			wb = ReporteAcreditacionesAFIPExcel.generaReporteLibroBanco(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"acreditacionesAFIP.xls\"");
		} else if (reporte.equals(REPORTE_ORDENES_PAGO)) {
			wb = ReporteOrdenesPagoExcel.generaReporteOPs(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"ordenesPago.xls\"");
		} else if (reporte.equals(REPORTE_ORDENES_PAGO_COMPLETO)) {
			wb = ReporteOrdenesPagoCompletoExcel.generaReporteOPs(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"ordenesPagoDetallado.xls\"");
		} else if (reporte.equals(LIBRO_CAJA)) {
			wb = ReporteLibroCajaExcel.generaReporteLibroCaja(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"libroCaja.xls\"");
		} else if (reporte.equals(LISTADO_CHEQUES)) {
			wb = ReporteListadoValoresExcel.generaListadoValores(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"listadoValores.xls\"");
		} else if (reporte.equals(REPORTE_BONOS_SECCIONAL)) {
			wb = ReporteBonosSeccionalExcel.generaReporteBonosSeccional(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"BonosSeccional.xls\"");
		} else if (reporte.equals(REPORTE_CANT_BONOS_SECCIONAL)) {
			wb = ReporteCantBonosSeccionalExcel.generaReporteCantBonosSeccional(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"CantBonosSeccional.xls\"");
		} else if (reporte.equals(REPORTE_CANT_BONOS_SECCIONAL_VENT)) {
			wb = ReporteCantBonosSeccionalExcelVent.generaReporteCantBonosSeccionalVent(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"CantBonosSeccional.xls\"");
//		} else if (reporte.equals(REPORTE_AFILIADOS_ANSES)) {
//			wb = ReporteAfiliadosAnsesExcel.generaReporteAfiliadosAnses(req, res);
//			res.setHeader("Content-Disposition", "attachment; filename=\"AfiliadosActivosJubiladosxAFIP.xls\"");
		} else if (reporte.equals(REPORTE_RESULT_BUSQUEDA_BONOS_SECCIONAL)) {
			wb = ReporteResultBusquedaBonosSeccionalExcel.generaReporteResultBonosSeccional(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"CantBonosSeccional.xls\"");

		} else if (reporte.equals(REPORTE_RESULT_BUSQUEDA_RECLAMOS_PRESTACIONALES)) {
			wb = ReporteReclamosPrestacionales.generaReporteReclamosPrestacionales(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"ReclamosPrestacionales.xls\"");
		} else if (reporte.equals(REPORTE_RESULT_BUSQUEDA_EQUIPOS_INTERDISCIPLINARIOS)) {
			wb = ReporteEquiposInterDisciplinarios.generaReporteEquiposInterdisciplinarios(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"EquipoInterdisciplinario.xls\"");
		} else if (reporte.equals(REPORTE_RESULT_BUSQUEDA_SITUACION_MEDICA)) {
			wb = ReporteSituacionMedica.generaReporteSituacionMedica(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"SituacionMedicaAfiliados.xls\"");
		} else if (reporte.equals(REPORTE_RESULT_BUSQUEDA_SITUACION_MEDICA_COMPLETO)) {
			wb = ReporteSituacionMedica.generaReporteSituacionMedica(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"SituacionMedicaAfiliados.xls\"");
		} else if (reporte.equals(REPORTE_RESULT_BUSQUEDA_MEDICAMENTOS_OSPIM)) {
			wb = ReporteMedicamentosOspim.generaReporteMedicacionOspim(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"MedicamentosOspim.xls\"");
		} else if (reporte.equals(REPORTE_ARCHIVO_PREVENCION_FARMACIA_DESGLOSE_PERIODO)) {
			wb = ReporteDesgloseFarmacia.generaReporteDesgloseFarmacia(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"FarmaciaDesgloseArchivoPrevencion.xls\"");
		} else if (reporte.equals(REPORTE_RESULT_BUSQUEDA_SECCIONALES)) {
			wb = ReporteSeccional.generaReporteSeccionales(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"Seccionales.xls\"");
		} else if (reporte.equals(POSIBLES_INCONSISTENCIAS)) {
			wb = ReportePosiblesInconsistenciasExcel.generaReportePosiblesInconsistencias(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"BonosSeccional.xls\"");
		} else if (reporte.equals(CUENTAS_CORRIENTES)) {
			wb = ReporteCuentasCorrientesExcel.generaReporteCtasCtes(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"CtasCorrientes.xls\"");
		} else if (reporte.equals(ESTADO_COMPROBANTES)) {
			wb = ReporteEstadoComprobantesExcel.generaReporteEstadoComprobantes(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"estadoComprobantes.xls\"");
		} else if (reporte.equals(CUENTAS_CORRIENTES_ACTAS_Y_CONV)) {
			wb = ReporteCuentasCorrienteActasYConveniosExcel.generaReporteCtasCtes(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"CtasCorrientesActasYConvenios.xls\"");
		} else if (reporte.equals(CUENTAS_CORRIENTES_PRESTAMOS_TURISMO_HD)) {
			wb = ReporteCuentasCorrientePrestamosTurismoExcel.generaReportePrestamosTurismo(req, res, true);
			res.setHeader("Content-Disposition", "attachment; filename=\"BeneficiosTurismoConsolidado.xls\"");			
		} else if (reporte.equals(CUENTAS_CORRIENTES_PRESTAMOS_TURISMO_IT)) {
			wb = ReporteCuentasCorrientePrestamosTurismoExcel.generaReportePrestamosTurismo(req, res, false);
			res.setHeader("Content-Disposition", "attachment; filename=\"BeneficiosTurismoDetalle.xls\"");			
		} else if (reporte.equals(REPORTE_SUBDIARIO_EGRESOS)) {
			wb = ReporteSubdiarioEgresoExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"subdiarioEgresos.xls\"");
		} else if (reporte.equals(REPORTE_SUBDIARIO_EGRESOS_INTERBANKING)) {
			wb = ReporteSubdiarioEgresoInterbankingExcel.generaReporte(req, res);
			int ctabcria = ParamUtil.getInteger(req, "ctabcria");
			res.setHeader("Content-Disposition",
					"attachment; filename=\"subdiarioEgresosInterbanking_" + String.valueOf(ctabcria) + "_.xls\"");
		} else if (reporte.equals(REPORTE_SUBDIARIO_INGRESOS)) {
			wb = ReporteSubdiarioIngresoExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"subdiarioIngresos.xls\"");
		} else if (reporte.equals(REPORTE_DESREGULADOS_SIN_APORTES)) {
			wb = ReporteDesreguladoSinAporteExcel.generaReporteDesreguladoSinAportePeriodo(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"DesreguladosSinAportes.xls\"");
		} else if (reporte.equals(REPORTE_RECIBOS)) {
			wb = ReporteRecibosExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"listadoRecibos.xls\"");
		} else if (reporte.equals(REPORTE_ANTICIPOS)) {
			wb = ReporteAnticiposExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteAnticipos.xls\"");
		} else if (reporte.equals(REPORTE_LLAMADOS_ESTUDIO)) {
			wb = ReporteSeguimientoEmpresasExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteSeguimientoEmpresas.xls\"");

		} else if (reporte.equals(DETALLE_lIQUIDACION_DEBITOS_TERCEROS)) {
			wb = ReporteDebitosTercerosExcel.generaReporteDetalleDebitosATerceros(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"detalleTercerizadoss.xls\"");
		} else if (reporte.equals(REPORTE_REINTEGROS)) {
			wb = ReporteReintegrosExcel.generaReporteReintegrosExcel(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteTodosReintegros.xls\"");
		} else if (reporte.equals(FICHA_DE_CONSUMO)) {
			wb = ReporteLiquidacionesExcel.generaFichaDeConsumo(req, res);
			// res.setContentType("application/vnd.ms-excel");
			res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteFichaConsumo.xlsx\"");
		} else if (reporte.equals(FICHA_DE_FARMACIA)) {
			wb = ReporteLiquidacionesFarmaciaExcel.generaFichaFarmacia(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"generaFichaFarmacia.xls\"");
		} else if (reporte.equals(REPORTE_PMI)) {
			wb = ReporteAutorizacionesPmiExcel.generaReporteAutorizacionesPmi(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteAutorizaconesPMI.xls\"");
		} else if (reporte.equals(REPORTE_DISCAPACIDAD)) {
			wb = ReporteLiquidacionesExcel.generaReporteDiscapacidad(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteDiscapacidad.xls\"");
		} else if (reporte.equals(TRATAMIENTO_DISCAPACIDAD)) {
			wb = ReporteTratamientoDiscapacidadExcel.generaReporteTratamientoDiscapacidadExcel(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteTratamientoDiscapacidad.xls\"");
		} else if (reporte.equals(REPORTE_LIQUIDACIONES)) {
			wb = ReporteLiquidacionesExcel.generaReporteLiquidacionesExcel(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteLiquidaciones.xls\"");
		} else if (reporte.equals(LISTADO_DE_DEUDA)) {
			wb = ReporteListadodDeDeudasExcel.generaReporteListadoDeDeudas(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"listadoDeDeudas.xls\"");
		} else if (reporte.equals(LISTADO_BAJAS)) {
			wb = ReporteAfiliadosBajaExcel.generaReporteAfiliadosBajaExcel(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"listadoBajas.xls\"");
		} else if (reporte.equals(OBTENER_VADEMECUM)) {
			wb = GeneraVademecumXLS.generaVademecum(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"vademecum.xls\"");
		} else if (reporte.equals(ALTAS_BAJAS_VADEMECUM)) {
			wb = GeneraVademecumAltasBajasXLS.generaVademecumAltasBajas(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"vademecumaltasbajas.xls\"");
		} else if (reporte.equals(EXPORTACION_PADRON_VADEMECUM)) {
			wb = GeneraVademecumXLS.generaVademecum(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"vademecum_padron.xls\"");
		} else if (reporte.equals(REPORTE_AMTIMA_PMI)) {
			wb = ReporteAmtimaPmiExcel.generaReporteAmtimaPmi(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporte_pmi_amtima.xls\"");
		} else if (reporte.equals(OP_REINTEGRO_FARMACIA)) {
			wb = ReporteOPReintegrosFarmacia.generaReporteOPReintegros(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteOPReintegros.xls\"");
		} else if (reporte.equals(OP_REINTEGRO_FARMACIA_PRESTA)) {
			wb = ReporteOPReintegrosFarmaciaPresta.generaReporteOPReintegrosFarmaciaPresta(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteOPReintegros.xls\"");
		} else if (reporte.equals(REP_REINTEGRO_FARMACIA)) {
			wb = ReporteReintegrosFarmacia.generaReporteReintegros(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteReintegros.xls\"");
		} else if (reporte.equals(EGRESO_POR_CONCEPTOS)) {
			wb = ReporteEgresosPorConceptosExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteEgresosPorConcepto.xls\"");
		} else if (reporte.equals(REPORTE_ANTICIPOS_OP)) {
			wb = ReporteAnticiposOPExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteAnticiposOP.xls\"");
		} else if (reporte.equals(REPORTE_LIQ_ACTA_CONVENIO)) {
			wb = ReporteLiqActaConvenio.generaReporteLiqActaConvenio(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteLiqActaConvenio.xls\"");
		} else if (reporte.equals("equivalencia_prestacion_concepto")) {
			wb = ReporteNomencladorConcepto.generaNomencladorConceptos(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"nomencladorConceptos.xls\"");
		} else if (reporte.equals(REPORTE_EGRESOS_LIQUIDACIONES)) {
			wb = ReporteEgresosLiquidacionesExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteEgresosConceptoLiquidaciones.xls\"");
		} else if (reporte.equals(REPORTE_DESEMPLEO_SS)) {
			wb = ReporteDesempleoSSExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"desempleo.xls\"");
		} else if (reporte.equals(REPORTE_PANEL_CONTROL_AFILIADOS)) {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String fecha = format.format(new Date(System.currentTimeMillis()));
			wb = ReportePanelControlAfiliadosExcel.generaReportePanelControlAfiliados(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"PanelControlAfiliados" + fecha + ".xls\"");
		} else if (reporte.equals(REPORTE_ACTAS)) {
			wb = ReporteActas.generar(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteActas.xls\"");
		} else if (reporte.equals(REPORTE_ACTAS_ESTUDIO)) {
			wb = ReporteActas.generarReporteSeguimiento(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteActas.xls\"");
		} else if (reporte.equals(REPORTE_RECIBOS_SEGUIMIENTO)) {
			wb = ReporteSeguimientoEmpresasExcel.generaReporteRecibos(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteRecSeguEmp.xls\"");
		} else if (reporte.equals(REPORTE_CONVENIOS)) {
			wb = ReporteConvenios.generar(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteConvenios.xls\"");
		} else if (reporte.equals(REPORTE_CONVENIOS_ESTUDIO)) {
			wb = ReporteConvenios.generarReporteSeguimiento(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteConvSegEmp.xls\"");
		} else if (reporte.equals(REPORTE_APLICACION_COBRANZAS)) {
			wb = ReporteAplicacionCobranzas.generar(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteAplicacionCobranzas.xls\"");
		} else if (reporte.equals(REPORTE_CORRESPONDENCIA)) {
			wb = ReporteCorrespondenciaExcel.generaReporteCorrespondencia(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteGestionCorrespondencia.xls\"");
		} else if (reporte.equals(REPORTE_CONTABILIDAD_DIARIO)) {
			wb = ReporteContabilidadDiario.generar(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteDiario.xls\"");
		} else if (reporte.equals(REPORTE_CONTABILIDAD_MAYOR_GENERAL)) {
			wb = ReporteContabilidadMayorGeneralExcel.generar(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteMayorGeneral.xls\"");
		} else if (reporte.equals(REPORTE_CONTABILIDAD_BALANCE_SUMAS_SALDOS)) {
			wb = ReporteContabilidadBalanceSumasSaldosExcel.generar(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteBalanceSumasYSaldos.xls\"");
		} else if (reporte.equals(REPORTE_CONTABILIDAD_BALANCE_GENERAL)) {
			wb = ReporteContabilidadBalanceGeneralExcel.generar(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteBalanceGeneral.xls\"");
		} else if (reporte.equals(CUENTA_CORRIENTE_PORTAL_EMPLEADORES)) {
			wb = ReporteCuentaCorrientePortalEmpleadoresExcel.generar(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reportePagosEmpleadores.xls\"");
		} else if (reporte.equals(REPORTE_INFORMACION_PERSONAS)) {
			wb = ReporteRrhh.generaReporteInformacionPersonas(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteInfoPersonas.xls\"");
		} else if (reporte.equals(REPORTE_LECTURAS_ACCESO)) {
			wb = ReporteRrhh.generaReporteLecturasAcceso(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteLecturasAcceso.xls\"");
		} else if (reporte.equals(REPORTE_CONTROL_ACCESO)) {
			wb = ReporteRrhh.generaReporteControlAccesoAgrupado(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteControlAccesoAgrupado.xls\"");
		} else if (reporte.equals(REPORTE_BUSQUEDA_CORRESPONDENCIA)) {
			wb = ReporteCorrespondenciaExcel.generaReporteEntradasSalidasCorrespondencia(req, res, "NORMAL");
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteBusqCorrespondencia.xls\"");
		} else if (reporte.equals(REPORTE_CORRESPONDENCIA_EMPAQUETADA)) {
			wb = ReporteCorrespondenciaExcel.generaReporteEntradasSalidasCorrespondencia(req, res, "EMPAQUETADO");
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteCorreosEnPaquete.xls\"");
		} else if (reporte.equals(REPORTE_LISTADO_CREDEN_EMITIDAS)) {
			wb = ReporteCredenEmitidasExcel.generaReporteCredenEmitidas(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteListCredenEmitidas.xls\"");
		} else if (reporte.equals(REPORTE_LEGAJOS_PROCESADOS)) {
			wb = ReporteLegajosProcesadosExcel.generaReporteLegajosProcesados(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteListLegajosProc.xls\"");
		} else if (reporte.equals(EXPORTACION_NUEVAS_OPCIONES_SSS)) {
			// List<DetalleOpcionesSS> opcionesxExportar =
			// BusquedaAfiliadoServiceUtil.buscarOpcionesSSSpendientesExportar();
			wb = ReporteOpcionesExcel.generaReporteOpcionesSinEnviar(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteNuevasOpcionesSinExportar.xls\"");
		} else if (reporte.equals(REPORTE_BUSQUEDA_NOVEDADES_EMPLEADORES)) {
			wb = ReporteNovedadesEmpleadoresExcel.generaReporteNovedadEmpleadores(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteBusqNovedadesEmpleadores.xls\"");
		} else if (reporte.equals(REPORTE_BUSQUEDA_NOVEDADES_SSS)) {
			wb = ReporteNovedadesSSSExcel.generaReporteNovedadSSS(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteBusqNovedadesSSS.xls\"");
		} else if (reporte.equals(REPORTE_BUSQUEDA_CONTACTOSCRM)) {
			wb = ReporteCrmContactoExcel.generaReporteResolucionContactoCRM(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteResolucionContactosCRM.xls\"");
		} else if (reporte.equals(ESTADISTICA_AGRUPADO_CONTACTOSCRM)) {
			wb = ReporteCrmContactoExcel.generaEstadisticaAgrupadoContactoCRM(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"estadisticaAgrupadoContactosCRM.xls\"");
		} else if (reporte.equals(ESTADISTICA_RENDIMIENTO_CONTACTOSCRM)) {
			wb = ReporteCrmContactoExcel.generaEstadisticaRendimientoContactoCRM(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"estadisticaRendimientoContactosCRM.xls\"");
		} else if (reporte.equals(ESTADISTICA_CIERRES_CONTACTOSCRM)) {
			wb = ReporteCrmContactoExcel.generaEstadisticaCierresContactoCRM(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"estadisticaCierresContactosCRM.xls\"");
		} else if (reporte.equals(REPORTE_DERIVACIONES)) {
			wb = ReporteDerivacionTercerizadorasExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteDerivacionesTercerizadoras.xls\"");
		} else if (reporte.equals(ESTADISTICA_NOVEDADES_SSS_PROCESADAS)) {
			wb = ReporteNovedadesSSSExcel.generaEstadisticaNovedadSSSProcesadas(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"estadisticaNovedadesSSS.xls\"");
		} else if (reporte.equals(REPORTE_SEGUIMIENTOSUR)) {
			wb = ReporteSeguimientoSurExcel.generaReporteSeguimientoSur(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteSeguimientoSur.xls\"");
		} else if (reporte.equals(LISTADO_ACTAS_ACUERDOS)) {
			wb = ReporteActasAcuerdosExcel.generaReporteActasAcuerdos(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"actasAcuerdos.xls\"");

		} else if (reporte.equals(REPORTE_BUSQUEDA_RECLAMOSCRM)) {
			wb = ReporteCrmReclamosExcel.generaReporteSeguimientoReclamosCRM(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteReclamosCRM.xls\"");

		} else if (reporte.equals(REPORTE_CAJA_CHICA)) {
			wb = ReporteCajaChicaExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"cajaChica.xls\"");
		} else if (reporte.equals(REPORTE_ULTIMOS_COMPROBANTES_CAJA_CHICA)) {
			try {
				wb = ReporteUltimosComprobantesCajaChicaExcel.generaReporte(req, res);
			} catch (Exception e) {

			}
			res.setHeader("Content-Disposition", "attachment; filename=\"ultimosComprobantesCajaChica.xls\"");
		} else if (reporte.equals(REPORTE_CONSULTA_IGS)) {
			wb = ReporteConsultasIGSExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"consultasIGS.xls\"");
		} else if (reporte.equals(REPORTE_HOSPITALES_AUTOGESTION)) {
			wb = ReporteHospitalesAutogestionExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"hospitalesAutogestion.xls\"");
		} else if (reporte.equals(REPORTE_RANKING_DEUDA_EMPRESAS)) {
			wb = ReporteRankingDeudaEmpresasExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"rankingDeudaEmpresas.xls\"");
		} else if (reporte.equals(REPORTE_APORTES_PAGO_RAMO)) {
			wb = ReporteAportesPagoRamoExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"aportesPagoRamo.xls\"");
		} else if (reporte.equals(REPORTE_NUEVOS_AFILIADOS_EMPRESAS)) {
			wb = ReporteNuevosAfiliadosEmpresasExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteNuevosAfiliadosEmpresa.xls\"");
		} else if (reporte.equals(REPORTE_INGRESOS_DEVENGADOS)) {
			wb = ReporteIngresosDevengadosExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"ingresosDevengados.xls\"");
		} else if (reporte.equals(REPORTE_INFORME_APORTES_MONOTRIBUTISTAS)) {
			wb = ReporteInformeAportesMonotributistasExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"controlAportesMonotributistas.xls\"");
		} else if (reporte.equals(REPORTE_CHEQUES_PENDIENTES_COBRO)) {
			wb = ReporteChequesPendientesCobroExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"chequesPendientesDeCobro.xls\"");
		} else if (reporte.equals(REPORTE_ESTADISTICO_ESTUDIO)) {
			wb = ReporteEstadisticoSeguimientoEmpresasExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteEstadisticoSeguimientoEmpresas.xls\"");
		} else if (reporte.equals(REPORTE_EOAF)) {
			wb = ReporteEOAFExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"eoaf.xls\"");
		} else if (reporte.equals(REPORTE_ESFC)) {
			wb = ReporteESFCExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"esfc.xls\"");
		} else if (reporte.equals(REPORTE_RESUMEN_CALC_DEUDA_MASIVO)) {
			wb = ReporteResumenProcesoCalcDeudaMasivoExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"resumenProcesoCalcDeudaMasivo.xls\"");
		} else if (reporte.equals(CONTROL_INGRESOS_EGRESOS_NIVEL_1)) {
			try {
				wb = ControlIngresosEgresosNivel1Excel.generaReporte(req, res);
				res.setHeader("Content-Disposition", "attachment; filename=\"controlIngresosEgresosNivel_1.xls\"");
			} catch (SystemException e) {
			} catch (SQLException e) {
			}
		} else if (reporte.equals(CONTROL_INGRESOS_EGRESOS_NIVEL_2)) {
			try {
				wb = ControlIngresosEgresosNivel2Excel.generaReporte(req, res);
				res.setHeader("Content-Disposition", "attachment; filename=\"controlIngresosEgresosNivel_2.xls\"");
			} catch (Exception e) {

			}
		} else if (reporte.equals(CONTROL_INGRESOS_EGRESOS_EMPRESAS_1)) {
			try {
				wb = ControlIngresosEgresosEmpresas1Excel.generaReporte(req, res);
				res.setHeader("Content-Disposition", "attachment; filename=\"controlIngresosEgresosEmpresas_1.xls\"");
			} catch (SystemException e) {
			} catch (SQLException e) {
			}
		} else if (reporte.equals(REPORTE_ACTAS_CONVENIOS_ESTADISTICO_ESTUDIO)) {
			wb = ReporteActas.generarReporteConvenioEstadistico(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteActasConveniosEstadistico.xls\"");
		} else if (reporte.equals(REPORTE_DETALLE_LOTE_PAGOS_SEGUIMIENTO_SUR)) {
			try {
				wb = ReporteSeguimientoSurExcel.generaReporteSeguimientoSurDetalleLote(req, res);
				res.setHeader("Content-Disposition", "attachment; filename=\"reporteDetalleLoteSeguimientoSur.xls\"");
			} catch (SystemException e) {
			}

		} else if (reporte.equals(REPORTE_PREAUTORIZACION)) {
			try {
				wb = ReportePreautorizacionExcel.generaReportePreautorizacion(req, res);
			} catch (SystemException e) {
			}
			res.setHeader("Content-Disposition", "attachment; filename=\"reportePreautorizaciones.xls\"");
		} else if (reporte.equals(REPORTE_PREAUTORIZACION_ESTADISTICO_ESTADOS)) {
			wb = ReportePreautorizacionEstadosExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition",
					"attachment; filename=\"reportePreautorizacionEstadisticoEstados.xls\"");
		} else if (reporte.equals(REPORTE_CENTROS_COSTOS)) {
			wb = ReporteCentrosDeCostoExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteCentrosDeCosto.xls\"");
		} else if (reporte.equals(REPORTE_CENTROS_COSTOS_DETALLE)) {
			try {
				wb = ReporteCentrosDeCostoExcel.generaReporteDetalle(req, res);
				res.setHeader("Content-Disposition", "attachment; filename=\"reporteCentrosDeCostoDetalle.xls\"");
			} catch (SystemException e) {

			}
		} else if (reporte.equals(REPORTE_ERRORES_INTEGRACION)) {
			try {
				wb = ReporteIntegracionErroresExcel.generaReporte(req, res);
				res.setHeader("Content-Disposition", "attachment; filename=\"reporteIntegracion.xls\"");
			} catch (SystemException e) {
			}
		} else if (reporte.equals(REPORTE_DETALLE_LIQUIDACION_INTEGRACION)) {
			try {
				wb = ReporteIntegracionLiquidacionExcel.generaReporte(req, res);
				res.setHeader("Content-Disposition", "attachment; filename=\"reporteIntegracionLiquidacion.xls\"");
			} catch (SystemException e) {
			}

		}else if (reporte.equals(REPORTE_DETALLE_LIQUIDACION_INTEGRACION_CABECERA)) {
			try {
				wb = ReporteIntegracionLiquidacionExcel.generaReporte(req, res);
				res.setHeader("Content-Disposition", "attachment; filename=\"reporteIntegracionLiquidacion.xls\"");
			} catch (SystemException e) {
			}

		}else if (reporte.equals(REPORTE_INCONSISTENCIAS_FECHA_TRANSFERENCIA_INTEGRACION)) {
			try {
				wb = ReporteIntegracionInconsistenciaExcel.generaReporte(req, res);
				res.setHeader("Content-Disposition", "attachment; filename=\"reporteIntegracionInconsistencias.xls\"");
			} catch (SystemException e) {
			}

		} else if (reporte.equals(REPORTE_LIQUIDACION_SUPERINTENDENCIA_INTEGRACION)) {
			try {
				wb = ReporteIntegracionLiquidacionSuperintendenciaExcel.generaReporte(req, res);
				res.setHeader("Content-Disposition", "attachment; filename=\"reporteIntegracionSuperintendencia.xls\"");
			} catch (SystemException e) {
			}
		} else if (reporte.equals(REPORTE_PRESTACIONES_AUTORIZADAS_PS)) {
			wb = ReportePrestacionesAutorizadasExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"estadisticaPrestacAutoriz.xls\"");
		} else if (reporte.equals(ORDENES_PAGO)) {
			wb = ReporteInterbanking.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"ordenesPagosProcesarInterbanking.xls\"");
		} else if (reporte.equals(PERCEPCIONES_IIBB)) {
			wb = ReportePercepcionesIIBBExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"percepciones_iibb.xls\"");
		} else if (reporte.equals(LIBRO_IVA)) {

			wb = ReporteLibroIVAExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"libro_iva.xls\"");
		} else if (reporte.equals(REPORTE_DEBITO_TERCERIZADORAS)) {
			String tercerizadoraNombre = "";
			String tercerizadora = ParamUtil.getString(req, "tipo_debitos_tercerizadoras");
			if ("OMI".equalsIgnoreCase(tercerizadora)) {
				tercerizadoraNombre = "OMINT";
			} else if ("MPS".equalsIgnoreCase(tercerizadora)) {
				tercerizadoraNombre = "MOLINEROS POR PS";
			} else if ("MEN".equalsIgnoreCase(tercerizadora)) {
				tercerizadoraNombre = "MOLINEROS POR ENSALUD";
			} else if ("CEM".equalsIgnoreCase(tercerizadora)) {
				tercerizadoraNombre = "CEMIC";
			}else if ("MIM".equalsIgnoreCase(tercerizadora)) {
				tercerizadoraNombre = "IMESA";
			}else if ("MON".equalsIgnoreCase(tercerizadora)) {
				tercerizadoraNombre = "MONOTRIBUTO";
			}else if ("MCE".equalsIgnoreCase(tercerizadora)) {
				tercerizadoraNombre = "MOLINEROS POR CES";
			}
			String periodo = ParamUtil.getString(req, "periodo") + ".xls";
			wb = ReporteDebitosaTercerizadorasExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition",
					"attachment; filename=\"" + "DEBITO_TERCERIZADORAS_" + tercerizadoraNombre + "_" + periodo + "\"");
		} else if (reporte.equals(REPORTE_PRESTADORES_INEXISTENTES)) {
			wb = ReportePrestadoresInexistentesExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"prestadores_inexistentes.xls\"");
		} else if (reporte.equals(REPORTE_NOVEDAD_PADRON_CONSOLIDADO_SSS)) {
			wb = ReporteNovedadPadronConsolidadoSSSExcel.generaReporte(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"REPORTE_NOVEDAD_PADRON_CONSOLIDADO_SSS.xls\"");
		} else if (reporte.equals(REPORTE_FACTURAS)) {
			try {
			  wb = ReporteFacturasExcel.generaReporte(req, res);
			  res.setHeader("Content-Disposition", "attachment; filename=\"reporte_facturas.xls\"");
			} catch (SystemException e) {
			}  
		}else if (reporte.equals(REPORTE_AUTORIZACIONES_PRESTACIONALES)) {
			try {
				wb = ReporteAutorizacionesPrestacionalesExcel.generaReporte(req, res);
			} catch (SystemException e) {}
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteAutorizacionesPrestacionales.xls\"");
		}else if (reporte.equals(REPORTE_DEUDA_CUENTACORRIENTE)) {
			wb = ReporteDeudaCuentaCorrienteExcel.generaReporteDeuda(req, res);
			int vista = ParamUtil.getInteger(req, "vista");
			
			if (vista == 0) {
				res.setHeader("Content-Disposition", "attachment; filename=\"reporteDeudaCuentaCorriente_v0.xls\"");	
			} else if (vista == 1) {
				res.setHeader("Content-Disposition", "attachment; filename=\"reporteDeudaCuentaCorriente_v1.xls\"");
			} else  {
				res.setHeader("Content-Disposition", "attachment; filename=\"reporteDeudaCuentaCorriente_v2.xls\"");
			}  
			
		} else if (reporte.equals(REPORTE_DEMANDAS)) {
			try {
			  wb = ReporteDemandasExcel.generaReporte(req, res);
			} catch (Exception e) {}  
			res.setHeader("Content-Disposition", "attachment; filename=\"reporte_demandas_judiciales.xls\"");
		} else if (reporte.equals(REPORTE_PRESTAMOS_TURISMO)) {
			try {
			  wb = ReportePrestamosTurismoExcel.generaReportePrestamosTurismo(req, res, true);
			} catch (Exception e) {}  
			res.setHeader("Content-Disposition", "attachment; filename=\"reporte_prestamos_turismo.xls\"");			
		} else if (reporte.equals(JUBILADOS_SITACI_DETALLE)) {
			try {
				  wb = ReporteJubiladosSitaciExcel.generaReporte(req, res);
				} catch (Exception e) {}  
				res.setHeader("Content-Disposition", "attachment; filename=\"JUBILADOS_SITACI_DETALLE.xls\"");			
		} else if (reporte.equals(REPORTE_ARCHIVO_ADMIFARM_PERIODO)) {
			wb = ReporteArchivoAdmifarmMonotributo.generaReporteAdmifarmMonotributo(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"archivo_admifarm_monotributo.xls\"");
		} else if (reporte.equals(REPORTE_ARCHIVO_ADMIFARM_OSPIM_GENERAL_PERIODO)) {
			wb = ReporteArchivoAdmifarmOspimGeneral.generaReporteAdmifarmOspimGeneral(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"archivo_admifarm_ospim_general.xls\"");
		} else if(reporte.equals(REPORTE_CENTROS_COSTOS_CONTABLE)) {
			wb = ReportePorCuentaContablePorCentroCosto.generar(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteCentroCosto.xls\"");
		}else if(reporte.equals(REPORTE_EXPORTAR_CENTROS_COSTOS_CONTABLES)) {
			wb = ReportePorCuentaContablePorCentroCosto.generarListado(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteCentroCostoContables.xls\"");
		}else if(reporte.equals(REPORTE_UPLOAD_ARCHIVOS)) {
			String vista = ParamUtil.getString(req, "proceso");
			if("SUB".equals(vista)) {
				wb = ReporteAcreditacionesAFIPExcel.generaReporteSubsidios(req, res);
			    res.setHeader("Content-Disposition", "attachment; filename=\"subsidiosAFIP.xls\"");
			}    
		}else if(reporte.equals(REPORTE_PRECIOS_FACTURACION)) {
		    wb = ReportePreciosPlanesSuperadores.generarListado(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reportePreciosPlanesSuperadores.xls\"");
		}else if(reporte.equals(REPORTE_AJUSTES_FACTURACION)) {
		    wb = ReportePreciosPlanesSuperadores.generarListadoAjustes(req, res);
			res.setHeader("Content-Disposition", "attachment; filename=\"reporteAjustesPlanesSuperadores.xls\"");
		}else if (reporte.equals("test")) {
			wb = test(req, res);
		}
		
		return wb;
	}

	private HSSFWorkbook test(HttpServletRequest req, HttpServletResponse res) {

		List<Columna> headers = new ArrayList<Columna>();
		headers.add(new Columna(Columna.Tipo.STRING, "Primera"));
		headers.add(new Columna(Columna.Tipo.STRING, "Segunda"));
		headers.add(new Columna(Columna.Tipo.STRING, "Tercera"));
		headers.add(new Columna(Columna.Tipo.STRING, "Cuarta"));

		List<Object[]> list = new ArrayList<Object[]>();
		list.add(new Object[] { "sd", "asd", new Date(), 1.2D });
		list.add(new Object[] { "2sd", "2asd", new Date(), 2.2D });
		list.add(new Object[] { "3sd", "3asd", new Date(), 3.2D });
		list.add(new Object[] { "4sd", "4asd", new Date(), 4.2D });

		return getGenericExcelReport(res, "test.xls", headers, list);
	}

	public static HSSFWorkbook getGenericExcelReport(HttpServletResponse res, String outXlsFileName,
			List<Columna> headers, List<? extends Object[]> repo) {
		HSSFWorkbook wb = new HSSFWorkbook();

		HSSFSheet sheet = wb.createSheet("Hoja 1");

		if (repo != null) {
			HSSFRow rowHeader = sheet.createRow(0);
			for (int h = 0; h < repo.size(); h++) {
				HSSFCell cell = rowHeader.createCell(h);
				cell.setCellValue(new HSSFRichTextString(headers.get(h).titulo));
			}
			for (int i = 0; i < repo.size(); i++) {
				// Create a row and put some cells in it. Rows are 0 based.
				HSSFRow row = sheet.createRow(i + 1);
				Object[] fila = repo.get(i);
				for (int j = 0; j < fila.length; j++) {
					// Create a cell and put a value in it.
					HSSFCell cell = row.createCell(j);
					switch (headers.get(j).tipo) {
					case DATE:
						cell.setCellValue(new HSSFRichTextString(DateUtils.format((Date) fila[j], DateUtils.SHORT)));
						break;
					case STRING:
						cell.setCellValue(new HSSFRichTextString(fila[j].toString()));
						break;
					case DOUBLE:
						cell.setCellValue(new BigDecimal((fila[j].toString())).doubleValue());
						break;
					}
				}
			}
		}
		res.setHeader("Content-Disposition", "attachment; filename=\"" + outXlsFileName + "\"");
		return wb;
	}

	private static class Columna {
		public enum Tipo {
			DATE, DOUBLE, STRING
		};

		public Tipo tipo = Tipo.STRING;
		public String titulo = "";

		public Columna(Tipo tipo, String titulo) {
			super();
			this.tipo = tipo;
			this.titulo = titulo;
		}
	}

}
