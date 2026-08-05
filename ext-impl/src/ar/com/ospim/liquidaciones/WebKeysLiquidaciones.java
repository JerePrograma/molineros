/**
 */
package ar.com.ospim.liquidaciones;

/**
 * <a href="WebKeys.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * @modif SVA
 */
public class WebKeysLiquidaciones implements
		com.liferay.portal.kernel.util.WebKeys {
	public static final String ROL_ABM_LIQUIDACIONES = "ABM_Liquidaciones";
	public static final String ROL_ENTIDAD_OSPIM = "Entidad_Ospim";
	public static final String ROL_ENTIDAD_AMTIMA = "Entidad_Amtima";
	public static final String ROL_ENTIDAD_UOMA = "Entidad_Uoma";
	public static final String ROL_ABM_ODONTOLOGIA = "ABM_Odontologia";
	public static final String ROL_ABM_AUDITOR_ODO = "ABM_Auditor_Odo";
	public static final String ROL_ABM_DISCAPACIDAD = "ABM_Discapacidad";
	public static final String ROL_ABM_CONVENIO_PREST = "ABM_CONVENIO_PREST";
	public static final String ROL_AUDITOR_CONVENIO_PREST = "AUDITOR_CONVENIO_PREST";
	public static final String SECCIONALES_EN_SESSION = "seccionales_session";
	public static final String PRESTACIONES_EN_SESSION = "prestaciones_session";
	public static final String PRESTADORES_EN_SESSION = "prestadores_session";
	public static final String ID_DEFAULT_ENTIDAD = "O.S.P.I.M.";
	public static final String BUSQUEDA_REINTEGRO = "BUSQUEDA_REINTEGRO";
	public static final String BUSQUEDA_CONVENIOS_PRESTAC_RESULTS = "BUSQUEDA_CONVENIOS_PRESTAC_RESULTS";
	public static final String BUSQUEDA_CONVENIOS_PRESTAC_FILTRO = "BUSQUEDA_CONVENIOS_PRESTAC_FILTRO";
	public static final String REINTEGRO_DE_SECCIONAL = "REINTEGRO_DE_SECCIONAL";
	public static final String PRESTADOR_DE_LIQUIDACION = "PRESTADOR_DE_LIQUIDACION";
	public static final String BUSQUEDA_LIQUIDACION = "BUSQUEDA_LIQUIDACION";
	public static final String ID_REINTEGRO_EN_EDICION = "id_reintegro";
	public static final String ID_LIQUIDACION_EN_EDICION = "id_liquidacion";
	public static final String REINTEGRO_EN_EDICION = "REINTEGRO_EN_EDICION";
	public static final String LIQUIDACION_EN_EDICION = "LIQUIDACION_EN_EDICION";
	public static final String REINTEGRO_PRE = "pre";
	public static final String REINTEGRO_ODO_PROTESIS = "pro";
	public static final String REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA = "ort";
	public static final String LIQUIDACION_PRE = "pre";
	public static final String LIQUIDACION_ODO = "odo";
	public static final String REINTEGRO_PRESTACION_EN_EDICION = "REINTEGRO_PRESTACION_EN_EDICION";
	public static final String REINTEGRO_PRESTACIONES_EN_EDICION = "REINTEGRO_PRESTACIONES_EN_EDICION";
	public static final String LIQUIDACION_PRESTACION_EN_EDICION = "LIQUIDACION_PRESTACION_EN_EDICION";
	public static final String LIQUIDACION_PRESTACIONES_EN_EDICION = "LIQUIDACION_PRESTACIONES_EN_EDICION";
	public static final int REINTEGRO_ESTADO_CARGADO = 1;
	public static final int REINTEGRO_ESTADO_AUDITADO = 3; // protesis,
	// ortopedia_ortodoncia
	public static final int REINTEGRO_ESTADO_LIQUIDADO = 2;
	public static final int REINTEGRO_ESTADO_PENDIENTE = 4; // protesis,
	// ortopedia_ortodoncia
	// que se carga con
	// la información no
	// completa
	
	public static final Integer REINTEGRO_ODO_PROTESIS_MARCA = 4;
	
	public static final int REINTEGRO_ESTADO_AUTORIZADO = 5; // equivalente a
	// cargado pero
	// solo para
	// prótesis,
	// ortopedia_ortodoncia
	public static final int REINTEGRO_ESTADO_RECHAZADO = 6; // prótesis,
	
	
	
	public static final String  REINTEGRO_PRESTACIONES_RECLAMOS = "REINTEGRO_PRESTACIONES_RECLAMOS"; // LISTA DE PRESTACIONES DE RECLAMOS ASOCIADO AL REINTEGRO
	
	
	// ortopedia_ortodoncia
	// que es rechazada
	// al momento de
	// auditarla
	public static final int LIQUIDACION_ESTADO_CARGADO = 1;
	public static final int LIQUIDACION_ESTADO_CERRADO = 2;
	public static final int LIQUIDACION_ESTADO_LIQUIDADO = 3;
	public static final int LIQUIDACION_ESTADO_MODIFICAR_PAGA = 10;
	public static final int LIQUIDACION_ESTADO_CIERRE_PERIODO_CONTABLE = 11;
	public static final String TIPO_REINTEGRO_EN_EDICION = "TIPO_REINTEGRO_EN_EDICION";
	public static final String BUSQUEDA_LIQUIDACIONES_DEBITOS_TERCEROS = "BUSQUEDA_LIQUIDACIONES_DEBITOS_TERCEROS";
	public static final String TERCERIZADORAS_POR_CONVENIOS = "TERCERIZADORAS_POR_CONVENIOS";
	
	public static final String RECLAMO_PRESTACIONAL_REINTEGRO_EN_EDICION = "RECLAMO_REINTEGRO_EN_EDICION";
	

	public static final String BUSQUEDA_PRESTADORES = "BUSQUEDA_PRESTADORES";
	public static final String PRESTADOR_EN_EDICION = "PRESTADOR_EN_EDICION";
	public static final String PRESTADOR_EXTERNO_EN_EDICION = "PRESTADOR_EN_EDICION";
	public static final String PRESTADORES_EXTERNOS_ACTION_EDICION = "PRESTADORES_EXTERNOS_ACTION_EDICION";
	public static final String ROL_ABM_ADMINISTRACION = "ABM_Administracion";
	public static final String TIPOSPRESTADOR_EN_SESSION = "TIPOSPRESTADOR_EN_SESSION";
	public static final String ERROR_PARA_ALERT = "ERROR_PARA_ALERT";
	public static final String VIEW_REINTEGRO = "VIEW_REINTEGRO";
	public static final String VIEW_CONVENIO_PREST = "VIEW_CONVENIO_PREST";
	public static final String VIEW_REINTEGRO_FARMACIA = "VIEW_REINTEGRO_FARMACIA";
	public static final String VIEW_LIQUIDACION = "VIEW_LIQUIDACION";
	public static final String ROL_ABM_CHEQUES = "ABM_Cheques";
	public static final String ROL_ABM_ORDEN_PAGO_AMTIMA = "ABM_Orden_pago_amtima";
	public static final String BUSQUEDA_CHEQUES = "BUSQUEDA_CHEQUES";
	public static final String CHEQUERAS="chequeras";
	public static final String CHEQUE_EN_EDICION = "CHEQUE_EN_EDICION";
	public static final String CHEQUE_A_IMPRIMIR = "CHEQUE_A_IMPRIMIR";
	public static final String BUSQUEDA_ORDENES_PAGO = "BUSQUEDA_ORDENES_PAGO";
	public static final String ORDEN_PAGO_EN_EDICION = "ORDEN_PAGO_EN_EDICION";
	public static final String ORDEN_PAGO_EDICION = "ORDEN_PAGO_EDICION";
	public static final String ORDENES_PAGO = "ORDENES_PAGO";
	public static final String ORDENES_PAGO_ARCHIVOS_FALLAS = "ORDENES_PAGO_ARCHIVOS_FALLAS";
	public static final String FROM_REINTEGROS = "FROM_REINTEGROS";
	public static final String FROM_REINTEGROS_FARMACIA = "FROM_REINTEGROS_FARMACIA";
	public static final String FROM_LIQUIDACION = "FROM_LIQUIDACION";
	public static final String OP_REINTEGROS = "OP_REINTEGROS";
	public static final String LISTA_ORDEN_PAGO_EDICION = "LISTA_ORDEN_PAGO_EDICION";
	public static final int[] PRESTACIONES_CON_TOPES = { 630, 629, 628, 397,
			2358, 2335, 2359, 94, 95 }; // (630, 629, 628); --psicoterapia,
										// //(397); --
	// kinesiología, //(2358, 2335, 2359); --optica
	public static final int TOPES_PROTESIS_FAMILIA = 12750; //8500;//6750;//4500; No olvidar tabla autorizaciones.nomenclador_topes_reintegro
	public static final String ORDENES_PAGO_ARCHIVOS_DUPLICADOS = "ORDENES_PAGO_ARCHIVOS_DUPLICADOS";
	public static final String ALTA_USR_REINTEGROS_EN_SESSION = "ALTA_USR_REINTEGROS_EN_SESSION";
	public static final String ESTADOS_CHEQUE_EN_SESSION = "ESTADOS_CHEQUE";
	public static final String ESTADOS_PAGARE_EN_SESSION = "ESTADOS_PAGARE";
	public static final String MOTIVOS_DEBITO_EN_SESSION = "MOTIVOS_DEBITO_EN_SESSION";
	public static final String BUSQUEDA_DEBITOS = "BUSQUEDA_DEBITOS";
	public static final String BUSQUEDA_CATASTRAL = "BUSQUEDA_CATASTRAL";
	public static final String HISTORICO_PROTESIS = "HISTORICO_PROTESIS";
	public static final String BUSQUEDA_MEDICAMENTO = "BUSQUEDA_MEDICAMENTO";
	public static final String REINTEGRO_MEDICAMENTOS_EN_EDICION = "REINTEGRO_MEDICAMENTOS_EN_EDICION";
	public static final String ID_REINTEGRO_FARMACIA_EN_EDICION = "ID_REINTEGRO_FARMACIA_EN_EDICION";
	public static final String ESTADOS_EFECTIVO_EN_SESSION = "ESTADOS_EFECTIVO_EN_SESSION";
	public static final String CODIGO_DEFECTO_CATASTRO = "1001";
	public static final String ID_CODIGO_DEFECTO_CATASTRO = "98";

	public static final String IMAGEN_NORMAL = "normal.png";
	public static final String IMAGEN_EXTRACCION = "extraccion.png";

	public static final int PRESTADOR_CONSOLIDAR_SALUD = 974;
	public static final int PRESTADOR_CONSOLIDAR_CAPITAS = 1974;
	public static final int PRESTADOR_CONSOLIDAR_EMPRESAS = 2974;
	public static final int PRESTADOR_CONSOLIDAR_APE = 2975;
	public static final int PRESTADOR_CONSOLIDAR_DESEMPLEADOS = 3974;

	public static final String ROL_REPORTES_OPS = "Reportes_OP";
	public static final String BUSQUEDA_COMPROBANTES = "BUSQUEDA_COMPROBANTES";
	public static final String COMPROBANTE_EN_EDICION = "COMPROBANTE_EN_EDICION";
	public static final String COMPROBANTE_NUEVO = "COMPROBANTE_NUEVO";
	public static final String CONCEPTOS_EGRESOS = "CONCEPTOS_COMOPROBANTE";
	public static final String CONCEPTOS_LIQUIDACION = "CONCEPTOS_LIQUIDACION";
	public static final String COMPROBANTE_CONCEPTOS_AGREGADOS = "COMPROBANTE_CONCEPTOS_AGREGADOS";

	public static final String NO_MOSTRAR_BUSQUEDA_COMPROBANTES = "NO_MOSTRAR_BUSQUEDA_COMPROBANTES";
	public static final String CONCEPTOS_INGRESO = "CONCEPTOS_INGRESO";

	public static final String USUARIO_CARGA_ODONTOLOGIA = "rlagerojas";

	public static final String LISTA_MEDICAMENTOS = "LISTA_MEDICAMENTOS";
	public static final String EXCEPCION_SUBDIARIO_CUENTA_PASIVO_INI = "01/10/2010";
	public static final String EXCEPCION_SUBDIARIO_CUENTA_PASIVO_FIN = "31/07/2011";
	public static final String ORDEN_PAGO_ANULACION_FORMA_PAGO = "ORDEN_PAGO_ANULACION_FORMA_PAGO";
	public static final String FECHA_BAJA_OP = "FECHA_BAJA_OP";
	public static final String CHEQUES_REUTILIZABLES = "CHEQUES_REUTILIZABLES";
	public static final String CHEQUES_CARTERA = "CHEQUES_CARTERA";
	public static final String CHEQUES_REUTILIZABLES_DISPONIBLES = "CHEQUES_REUTILIZABLES_DISPONIBLES";
	public static final String CONCEPTOS_EGRESOS_AMTIMA = "CONCEPTOS_EGRESOS_AMTIMA";
	public static final String CONCEPTOS_EGRESOS_UOMA = "CONCEPTOS_EGRESOS_UOMA";
	public static final String FARMACIAS = "FARMACIAS";
	public static final String ORDENES_PAGO_ARCHIVOS_SIN_CUIT = "ORDENES_PAGO_ARCHIVOS_SIN_CUIT";
	public static final String BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD = "BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD";

	public static final int TRATAMIENTO_DISCA_ESTADO_PRE_AUTORIZACION = 0;
	public static final int TRATAMIENTO_DISCA_ESTADO_EN_CURSO = 1;
	public static final int TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE = 2;
	public static final int TRATAMIENTO_DISCA_ESTADO_CAMBIO_PRESTADOR = 3;
	public static final int TRATAMIENTO_DISCA_ESTADO_FINALIZADO = 4;
	public static final int TRATAMIENTO_DISCA_ESTADO_ABANDONADO = 5;

	public static final int MOTIVO_ALTA_POR_DISCAPACIDAD_ESTADO_AUTORIZADO = 0;
	public static final int MOTIVO_ALTA_POR_DISCAPACIDAD_ESTADO_DOC_FALTANTE = 1;
	public static final int MOTIVO_ALTA_POR_DISCAPACIDAD_ESTADO_SIN_TRATAMIENTO = 2;
	public static final int MOTIVO_ALTA_POR_DISCAPACIDAD_ESTADO_PER_INCORRECTO = 3;
	public static final int MOTIVO_ALTA_POR_DISCAPACIDAD_ESTADO_PERIODO_DUPLICADO_EXCEDIDO = 4;
	public static final int MOTIVO_ALTA_POR_DISCAPACIDAD_ESTADO_DOC_FALTANTE_Y_PERIODO_DUPLICADO_EXCEDIDO = 5;

	public static final String PRESTACION_TRANSPORTE = "23201";
	public static final String CONVENIO_PREST_EN_EDICION = "CONVENIO_PREST_EN_EDICION";
	public static final String CONVENIO_PREST_DETALLE_EN_EDICION = "CONVENIO_PREST_DETALLE_EN_EDICION";
	public static final String CONVENIO_PREST_DETALLES_EN_SESSION = "CONVENIO_PREST_DETALLES_EN_SESSION";
	public static final String CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE = "CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE";
	
	public static final String TIPOS_PAGO_CONVENIOS_PREST_EN_SESSION = "TIPOS_PAGO_CONVENIOS_PREST_EN_SESSION";
	public static final String TIPOS_NOMENCLADORES_EN_SESSION = "TIPOS_NOMENCLADORES_EN_SESSION";
	public static final String ROL_ABM_FARMACIA = "ABM_Farmacia";
	public static final String REPORTES_PRESTACIONES = "reportes_prestaciones";
	public static final String LOTE_ACTUAL = "LOTE_ACTUAL";
	
	public static final String ROL_AUMENTO_NOMENCLADOR = "AUMENTO_NOMENCLADOR";
	public static final String LISTAS_DE_PROFESION_PRESTADOR_EN_SESSION = "LISTAS_DE_PROFESION_PRESTADOR_EN_SESSION";
	public static final String LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION = "LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION";
	public static final String LISTAS_DE_SUB_ESPECIALIDAD_PRESTADOR_EN_SESSION = "LISTAS_DE_SUB_ESPECIALIDAD_PRESTADOR_EN_SESSION";
	
	public static final String PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION = "PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION";
	public static final String MATRICULAS_PRESTADOR_EN_SESSION = "MATRICULAS_PRESTADOR_EN_SESSION";
	public static final String LUGARES_ATENCION_PRESTADOR_EN_SESSION = "LUGARES_ATENCION_PRESTADOR_EN_SESSION";
	public static final String LUGARES_ATENCION_PRESTADOR_INDIRECTO_EN_SESSION = "LUGARES_ATENCION_PRESTADOR_INDIRECTO_EN_SESSION";
	public static final String LUGAR_ATENCION_TELEFONOS_EN_SESSION = "LUGAR_ATENCION_TELEFONOS_EN_SESSION";
	public static final String LUGAR_ATENCION_CONTACTOES_EN_SESSION = "LUGAR_ATENCION_CONTACTOES_EN_SESSION";
	public static final String LUGAR_ATENCION_PRESTADOR_EN_EDICION = "LUGAR_ATENCION_PRESTADOR_EN_EDICION";
	public static final String LUGAR_ATENCION_PRESTADOR_INDIRECTO_EN_EDICION = "LUGAR_ATENCION_PRESTADOR_INDIRECTO_EN_EDICION";
	public static final String DOMICILIO_AFIP_PRESTADOR_EN_EDICION = "DOMICILIO_AFIP_PRESTADOR_EN_EDICION";
	public static final String PLANES_PRESTADOR_EN_SESSION = "PLANES_PRESTADOR_EN_SESSION";
	public static final String PLANES_EN_SESSION  = "PLANES_EN_SESSION";
	public static final String LISTAS_PRESTACIONES_REINTEGROS_RESULTADOS = "LISTAS_PRESTACIONES_REINTEGROS_RESULTADOS";
	public static final String LISTAS_FARMACIAS_REINTEGROS_RESULTADOS = "LISTAS_FARMACIAS_REINTEGROS_RESULTADOS";
	
	public static final String COMPROBANTE_EXTENDIDO_ACUMULADO="COMPROBANTE_EXTENDIDO_ACUMULADO";
	
	public static final String ADD_FORMA_PAGO="ADD_FORMA_PAGO";
	
	
	public static final String DEBITOS_LIQ_PENDIENTES= "DEBITOS_LIQ_PENDIENTES";
	public static final String DEBITOS_HOSPITALES= "DEBITOS_HOSPITALES";
	public static final String DEBITOS_REINTEGROS= "DEBITOS_REINTEGROS";
	public static final String DEBITOS_PRESTADORES= "DEBITOS_PRESTADORES";

	public static final String ROL_BUSQUEDA_GENERAL_COMPROBANTES="COMPROBANTES_BUSQUEDA_GENERAL";


	public static final String CONSULTA_COMPROBANTES_GLOBAL_OFFSET_REG="CONSULTA_COMPROBANTES_GLOBAL_OFFSET_REG";
	public static final String CONSULTA_COMPROBANTES_GLOBAL_TOTAL_REGISTROS="CONSULTA_COMPROBANTES_GLOBAL_TOTAL_REGISTROS";     
	public static final String BUSQUEDA_COMPROBANTES_GLOBALES = "BUSQUEDA_COMPROBANTES_GLOBALES";
	
	public static final String ROL_LIQUIDACIONES_HOSPITALES = "liquidaciones_hospitales";	

	public static final String BUSQUEDA_COMPROBANTES_INTERBANKING = "BUSQUEDA_COMPROBANTES_INTERBANKING";
		
}