/**
 */
package ar.com.ospim.tesoreria;

import java.util.HashMap;
import java.util.Map;

/**
 * <a href="WebKeys.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class WebKeysTesoreria implements com.liferay.portal.kernel.util.WebKeys {
	public static final String ROL_ABM_TESORERIA = "ABM_Tesoreria";
	public static final String ROL_ABM_TESORERIA_UOMA = "ABM_Tesoreria_UOMA";
	
	public static final String ROL_ENTIDAD_OSPIM = "Entidad_Ospim";
	public static final String ROL_ENTIDAD_AMTIMA = "Entidad_Amtima";
	public static final String ROL_ENTIDAD_UOMA = "Entidad_Uoma";
	public static final String REPORTE_EMPLEADORES = "reporte_empleadores";
	
	public static final String[] DEBITO_CREDITO = { "DEBITO", "CREDITO" };
	public static final String CUENTAS_BCRIAS = "cuentas_bancarias";
	public static final String ACTA_EN_EDICION = "ACTA_EN_EDICION";
	public static final String ACTAS_ACTION_EDICION = "ACTAS_ACTION_EDICION";
	public static final String INSPECTORES_EN_SESSION = "INSPECTORES_EN_SESSION";
	public static final String INSPECTORES_AGREGADOS = "INSPECTORES_AGREGADOS";
	public static final String DETALLE_ACTA_AGREGADOS = "DETALLE_ACTA_AGREGADOS";
	public static final String BUSQUEDA_ACTAS = "BUSQUEDA_ACTAS";
	public static final String BUSQUEDA_DEUDAS = "BUSQUEDA_DEUDAS";
	public static final String ACTAS_ASOCIADAS_EN_SESSION = "ACTAS_ASOCIADAS_EN_SESSION";
	public static final String MOVS_BCRIOS = "movs_bcrios";
	public static final String REPORTE_APORTES_CONTRIBUYENTES = "REPORTE_APORTES_CONTRIBUYENTES";
	public static final String REPORTE_DEUDA_EMPRESA_PERIODO = "REPORTE_DEUDA_EMPRESA_PERIODO";
	public static final String MOV_BCRIO_EN_EDICION = "MOV_BCRIO_EN_EDICION";
	public static final String TIPOS_MOV_BCRIO_EN_REQUEST = "TIPOS_MOV_BCRIO_EN_REQUEST";
	public static final String TIPOS_MOV_BCRIO_AMTIMA_EN_REQUEST = "TIPOS_MOV_BCRIO_AMTIMA_EN_REQUEST";
	public static final String CHEQUERAS_EN_SESSION = "CHEQUERAS_EN_SESSION";
	public static final String TIPOS_TRX_BCRIA_EN_SESSION = "TIPOS_TRX_BCRIA_EN_SESSION";
	public static final String TIPOS_TRX_BCRIA_AMTIMA_EN_SESSION = "TIPOS_TRX_BCRIA_AMTIMA_EN_SESSION";
	public static final String INTERESES_AFIP_EN_SESSION = "INTERESES_AFIP_EN_SESSION";
	public static final String VENCIMIENTOS_AFIP_EN_SESSION = "VENCIMIENTOS_AFIP_EN_SESSION";
	public static final String BANCOS_EN_SESSION = "BANCOS_EN_SESSION";
	public static final String BUSQUEDA_CONVENIOS = "BUSQUEDA_CONVENIOS";
	public static final String CONVENIO_EN_EDICION = "CONVENIO_EN_EDICION";
	public static final String CONVENIOS_ACTION_EDICION = "CONVENIOS_ACTION_EDICION";
	public static final String RECIBO_EN_EDICION = "RECIBO_EN_EDICION";
	public static final String RECIBOS_ACTION_EDICION = "RECIBOS_ACTION_EDICION";
	public static final String ROL_ABM_ACTAS = "ABM_Actas";
	public static final String ROL_AUDITOR_ACTAS = "Auditor_Actas";
	public static final String ROL_ABM_CONVENIOS = "ABM_Convenios";
	public static final String ROL_ABM_RECIBOS = "ABM_Recibos";
	public static final String ROL_ABM_MOVIMIENTOS_BANCARIOS = "ABM_Movimientos_Bancarios";
	public static final String ROL_ABM_EMPRESA = "ABM_Empresa";
	public static final String BUSQUEDA_RECIBOS = "BUSQUEDA_RECIBOS";
	public static final String ROL_REPORTES_BANCOS = "Reportes_Bancos";
	public static final String ROL_REPORTE_TESORERIA_CONTADURIA_DECLARACION_JURADA = "Reportes_Tesoreria_Contaduria_Declaracion_Jurada";
	public static final String ROL_REPORTE_TESORERIA_CONTADURIA = "Reporte_Tesoreria_Contaduria";
	public static final String ROL_REPORTE_TESORERIA_LIQUIDACIONES = "Reporte_Tesoreria_Liquidaciones";
	public static final String ACTAS_PERIODOS = "ACTAS_PERIODOS";
	public static final String ACTA_PERIODOS_SUBTOTAL = "ACTA_PERIODOS_SUBTOTAL";
	public static final String ACTA_PERIODOS_INTERES = "ACTA_PERIODOS_INTERES";
	public static final String CANJE_CHEQUES_EN_SESSION = "CANJE_CHEQUES_EN_SESSION";
	public static final String CANJE_CHEQUES_RESULT = "CANJE_CHEQUES_RESULT";
	public static final String IS_AMTIMA = "isAmtima";
	public static final String IS_FARMACIA = "isFarmacia";
	public static final String ROL_ABM_OP = "ABM_OP";
	public static final String ROL_VER_OP = "VER_OP";
	public static final String ROL_ABM_EQUIVALENCIAS = "ABM_Equivalencias";
	public static final String ROL_ABM_EQUIVALENCIAS_PRESTACIONES = "ABM_Equivalencias_Nomenclador";
	public static final String ASIENTO_EN_SESSION = "asiento_en_session";
	public static final String ROL_ABM_CONTABILIDAD = "ABM_Contabilidad";
	public static final String PLAN_CUENTAS = "PLAN_CUENTAS";
	public static final String[] ESTADO_ACTAS_NO_OS = { "PENDIENTE", "PENDIENTE DE ACUERDO"};
	public static final String BUSQUEDA_ASIENTOS_EN_SESSION="BUSQUEDA_ASIENTOS_EN_SESSION";
	public static final String CONVENIO_EN_SESSION = "CONVENIO_EN_SESSION";
	public static final String CUENTAS_EN_SESSION = "CUENTAS_EN_SESSION";
	public static final String ACTA_NO_OS_TOTALES="ACTA_NO_OS_TOTALES";
	public static final String REPORTES_EGRESOS="reportes_egresos";
	public static final String PLANES_CUENTAS_EN_SESSION = "PLANES_CUENTAS_EN_SESSION";
	public static final String REPORTE_CHEQUES_PENDIENTE_COBRO="REPORTE_CHEQUES_PENDIENTE_COBRO";
	public static final String ROL_CALCULO_DEUDA_MASIVO = "Calculo_Deuda_Masivo";
	public static final String CALCULOS_DEUDA_MASIVA_RESULTADOS = "CALCULOS_DEUDA_MASIVA_RESULTADOS";
	public static final String COEFICIENTES_AJUSTE_INFLACION_EN_SESSION = "COEFICIENTES_AJUSTE_INFLACION_EN_SESSION";
	public static final String COEFICIENTE_EN_EDICION = "COEFICIENTE_EN_EDICION";
	public static final String ROL_ABM_OP_PAGOS = "ABM_OP_PAGOS";
	public static final String LISTA_DETALLE_ASIENTO_SUELDOS="LISTA_DETALLE_ASIENTO_SUELDOS";
	public static final String ASIENTO_SUELDOS_CUENTA_NETEO="ASIENTO_SUELDOS_CUENTA_NETEO";
	public static final String ASIENTO_SUELDOS_SECTOR_LIQUIDADO="ASIENTO_SUELDOS_SECTOR_LIQUIDADO";
	public static final String ASIENTO_ESPECIAL_EN_SESSION = "asiento_especial_en_session";
	
	
	public static final Map<Integer,Integer> PORTAL_EMPLEADORES_EQUIVALENCIA_CONCEPTOS=
			    new HashMap<Integer,Integer>(){{
		            put(224,1);put(891,2);put(964,4);put(976,3);put(894,4);put(988,5);
			    }};
	
    public static final String BOLETA_EMPLEADORES_NRO = "BOLETA_EMPLEADORES_NRO";	
    public static final String BOLETA_EMPLEADORES_IMPAGAS = "BOLETA_EMPLEADORES_IMPAGAS";
    
    public static final String EQUIVALENCIAS_SUELDOS_EN_EDICION="EQUIVALENCIAS_SUELDOS_EN_EDICION";
    public static final String ASIENTO_SUELDO_EN_SESSION="ASIENTO_SUELDO_EN_SESSION";
    public static final String PRECIO_EN_SESSION="PRECIO_EN_SESSION";
    public static final String PRECIO_EN_EDICION="PRECIO_EN_EDICION";
    public static final String PRECIO_EN_SESSION_PARENTESCOS="PRECIO_EN_SESSION_PARENTESCOS";
    public static final String PRECIO_EN_SESSION_PLANES="PRECIO_EN_SESSION_PLANES";
    public static final String PRECIO_EN_SESSION_PROVINCIAS="PRECIO_EN_SESSION_PROVINCIAS";
    public static final String PRECIOS_RESULT="PRECIOS_RESULT";
    public static final String PRECIOS_COTIZACION_RESULT="PRECIOS_COTIZACION_RESULT";
    public static final String PRECIO_COTIZACION="PRECIOS_COTIZACION";
    
    public static final String AJUSTE_EN_SESSION="AJUSTE_EN_SESSION";
    public static final String AJUSTE_EN_EDICION="AJUSTE_EN_EDICION";
    public static final String AJUSTE_EN_SESSION_PARENTESCOS="AJUSTE_EN_SESSION_PARENTESCOS";
    public static final String AJUSTE_EN_SESSION_PLANES="AJUSTE_EN_SESSION_PLANES";
    public static final String AJUSTE_EN_SESSION_PROVINCIAS="AJSUTE_EN_SESSION_PROVINCIAS";
    public static final String AJUSTES_RESULT="AJUSTES_RESULT";
    public static final String AJUSTES_COTIZACION_RESULT="AJUSTES_COTIZACION_RESULT";
    public static final String AJUSTES_COTIZACION_SELECCIONADO="AJUSTES_COTIZACION_SELECCIONADO";
    public static final String AJUSTES_COTIZACION_ASIGNADOS="AJUSTES_COTIZACION_ASIGNADOS";
    public static final String AJUSTE_COTIZACION="AJUSTE_COTIZACION";
    public static final String AJUSTE_COTIZACION_FECHA="AJUSTE_COTIZACION_FECHA";
    
    public static final String SALDOS_DIARIOS_CUENTAS_BANCARIAS ="SALDOS_DIARIOS_CUENTAS_BANCARIAS";
	
}