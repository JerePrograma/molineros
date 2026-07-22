package ar.com.ospim.autorizaciones.services;

/**
 * <a href="WebKeys.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Gustavo Fernandez
 * 
 */
public class WebKeysAutorizaciones implements
		com.liferay.portal.kernel.util.WebKeys {
	
	
	public static final String ROL_ALTA_AUTORIZACIONES_PMI = "ABM_Autorizaciones";
	public static final String GENERAR_AUTORIZACIONES_PMI = "ALTA_AUTORIZACIONES_PMI";
	public static final String MODALIDAD_ATENCION = "MODALIDAD_ATENCION";
	public static final String NOMENCLADOR_EN_EDICION = "NOMENCLADOR_EN_EDICION";
	public static final String PRESTACIONCONCEPTO_EN_EDICION = "PRESTACIONCONCEPTO_EN_EDICION";
	public static final String PRESTACIONCONCEPTO_ORIGINAL = "PRESTACIONCONCEPTO_ORIGINAL";
	
	public static final String[][] TIPOS_EXPEDIENTES={ {"1","OSPIM"},{"2","TERCERIZADORA"},{"3","AMBOS"},{"4","OSPIM-PREVENCION"},{"5","PREVENCION-OMINT"},{"6","OSPIM-OMINT"},{"7","OSPIM-ENSALUD"}, {"8","OSPIM-PREVENCION-OMINT"},
			{"9","PREVENCION-ENSALUD"},{"10","ENSALUD-PREVENCION-OMINT"},{"11","ENSALUD-PREVENCION-OMINT-CEMIC"}};
	public static final String SEGUIMIENTO_EN_EDICION = "SEGUIMIENTO_EN_EDICION";
	public static final String BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD = "BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD";
	public static final String[][] AUTORIZA_OMINT={ {"1","TOTAL"},{"2","PARCIAL"},{"3","NO AUTORIZA"} };
	public static final String BUSQUEDA_COMPROBANTES_TRATAMIENTOS_DISCAPACIDAD = "BUSQUEDA_COMPROBANTES_TRATAMIENTOS_DISCAPACIDAD";
	public static final String[][] ESTADOS_EXPEDIENTES={ {"1","CERRADOS"} };
	public static final String ROL_ALTA_EXPEDIENTES_SUR = "expedientes_sur";
	public static final String ROL_CIERRE_EXPEDIENTES_SUR = "expedientes_sur_cerrar";
	public static final String[][] MOTIVOS_CIERRE_EXPEDIENTES={{"PG","Pagado"}, {"NR","Rechazado SSS - No Recuperable"} ,{"RD","Rechazado SSS - Período Duplicado"},
		{"RV","Rechazado SSS - Plazo Vencido"}};
	public static final String[][] CLASES_EXPEDIENTES={ {"DI","Discapacidad"},{"ME","Medicamentos"},{"PR","Prótesis"},{"OT","Otras Patologías"},
		                                                {"HI","HIV"},{"HE","Hemofilia"},{"DB","Diabetes"},{"DR","Drogadependencia"},{"FE","Fertilidad"},{"CO","COVID-19"}  };	
	public static final String[][] TIPOS_EXPEDIENTES_TERCERIZADORA={ {"1","OMINT"},{"2","PREVENCION"},{"3","ENSALUD"},{"4","CEMIC"} };
	
	public static final String[][] TIPO_COMPROBANTE={ {"CBU","CBU"},{"NOTA AUTORIZACION PAGO","NOTA AUTORIZACION PAGO"}};

	public static final String[][] FILTRO_PAGO={ {"0","Titular"},{"1","Apoderado"},{"2","Seccional"}};

	public static final String[][] LETRA_COMPROBANTE={ {"",""},{"A","A"},{"B","B"},{"C","C"}};
	
	public static final String[][] UNIDAD_MEDIDA_DROGADEPENDENCIA={ {"MM","Módulo Mensual"} };
	public static final String BUSQUEDA_COMPROBANTES_SEGUIMIENTOSUR = "BUSQUEDA_COMPROBANTES_SEGUIMIENTOSUR";
	public static final String CARTILLA_EN_EDICION = "CARTILLA_EN_EDICION";
	public static final String ROL_ABM_DISCAPACIDAD = "ABM_Discapacidad";
	public static final String ROL_ABM_EXPED_SUR = "ABM_Expendientes_SUR";
	public static final String ROL_ABM_RECLAM_PREST = "ABM_Reclamos_Prestacionales";
	public static final String ROL_ABM_PREAUTORIZACION = "ABM_Preautorizaciones";
	public static final String ABM_RECLAMOS_PRESTACIONALES_PRECARGA = "ABM_Reclamos_Prestacionales_precarga";
	public static final String ROL_CONSULTA_RECLAMOS_PRESTACIONALES = "CONSULTA_RECLAMO_PRESTACIONAL";
	
	public static final String ROL_ENTIDAD_OSPIM = "Entidad_Ospim";
	public static final String ROL_ENTIDAD_AMTIMA = "Entidad_Amtima";
	public static final String ROL_ENTIDAD_UOMA = "Entidad_Uoma";

	public static final int TRATAMIENTO_DISCA_ESTADO_PRE_AUTORIZACION = 0;
	public static final int TRATAMIENTO_DISCA_ESTADO_EN_CURSO = 1;
	public static final int TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE = 2;
	public static final int TRATAMIENTO_DISCA_ESTADO_CAMBIO_PRESTADOR = 3;
	public static final int TRATAMIENTO_DISCA_ESTADO_FINALIZADO = 4;
	public static final int TRATAMIENTO_DISCA_ESTADO_ABANDONADO = 5;
	public static final int TRATAMIENTO_DISCA_ESTADO_MONOTRIBUTO = 7;
	public static final String PRESTACION_TRANSPORTE = "23201";
	
	// Reclamos Prestacionales 	
	public static final String ESTADOS_RECLAMOS_PRESTACIONES_EN_SESION  = "ESTADOS RECLAMOS EN SESION";
	public static final String TIPOS_GESTION_RECLAMOS_PRESTACIONES_EN_SESION  = "TIPOS GESTION RECLAMOS EN SESION";
	public static final String RECLAMO_PRESTACION_EN_EDICION   = "RECLAMO_PRESTACION_EN_EDICION";
	public static final String LISTADO_PRESTACIONES_RECLAMOS_EN_SESION  = "LISTADO DE PRESTACIONES RECLAMOS EN SESION";
	public static final String LISTADO_REVISIONES_RECLAMOS_EN_SESION  = "LISTADO DE REVISIONES DE RECLAMOS EN SESION";
	public static final String BUSQUEDA_RECLAMOS_PRESTACIONALES = "LISTADO DE RECLAMOS PRESTACIONALES";
	public static final String BUSQUEDA_RECLAMOS_PRESTACIONALES_FILTRO = "BUSQUEDA_RECLAMOS_PRESTACIONALES_FILTRO";
	public static final String LISTADO_PRESTACIONES_ASOCIADAS_RECLAMOS_EN_SESION  = "LISTADO DE PRESTACIONES RECLAMOS ASOCIADA EN SESION";
	public static final String PRESTACION_EN_PROCESO_DE_EDICION   = "PRESTACION_EN_	PROCESO_DE_EDICION";		
	public static final String LISTADO_CONTACTOS_RECLAMOS_EN_SESION  = "LISTADO DE CONTACTOS DE RECLAMOS EN SESION";
	public static final String PREAUTORIZACION_EN_EDICION   = "PREAUTORIZACION_EN_EDICION";
	public static final String RECLAMOS_PRESTACIONALES_REVISION_ESTADO_EN_SESION  = "RECLAMOS_PRESTACIONALES_REVISION_ESTADO_EN_SESION";
	public static final String RECLAMOS_PRESTACIONALES_INTEGRACION_EN_SESION  = "RECLAMOS_PRESTACIONALES_INTEGRACION_EN_SESION";
	public static final String CUENTA  = "CUENTA";
	public static final String CUENTA_SELECT  = "CUENTA_SELECT";
	public static final String RECLAMO_PRESTACION_CUENTA_SELECT   = "RECLAMO_PRESTACION_CUENTA_SELECT";
	
	public static final Integer RECLAMO_PRESTACIONAL_ESTADO_CARGADO=1;
	public static final Integer RECLAMO_PRESTACIONAL_ESTADO_RECHAZADO=3;
	public static final String RECLAMO_PRESTACIONAL_RESOLUCION_ESTADO_RECHAZADO="RECHAZADO";
	public static final Integer RECLAMO_PRESTACIONAL_TIPO_GESTION_ESTADO_RECHAZADO=5;
	public static final Integer RECLAMO_PRESTACIONAL_ESTADO_PRESTACION_RECHAZADO=3;
	public static final Integer RECLAMO_PRESTACIONAL_ESTADO_PRESTACION_AUTORIZADO=1;
	public static final String RECLAMO_PRESTACIONAL_PEDIDO_EXCEPCION ="EXCEPCION";
	public static final String RECLAMO_PRESTACIONAL_SECTOR_PRESTACIONES_MEDICAS="PRESTACIONES MEDICAS";
	public static final String RECLAMO_PRESTACIONAL_SECTOR_FARMACIA="FARMACIA";
	public static final String[][] RECLAMO_PRESTACIONAL_FRECUENCIA={ {"UNICA","UNICA"},{"SEMANAL","SEMANAL"},
			{"TRIMESTRAL","TRIMESTRAL"},{"MENSUAL","MENSUAL"},{"SEMESTRAL","SEMESTRAL"},{"ANUAL","ANUAL"}};
	public static final String RECLAMO_PRESTACIONAL_RESOLUCION_RESPONSABLE="AUDITORIA DE PRESTACIONES MEDICAS";
	public static final String RECLAMO_PRESTACIONAL_RESOLUCION_PRESENTES="AUDITORIA MEDICA";
	public static final String RECLAMO_PRESTACIONAL_SECCIONAL = "RECLAMO_PRESTACIONAL_SECCIONAL";
	public static final String RECLAMO_NUEVO_ESTADO_OBS = "RECLAMO_NUEVO_ESTADO_OBS";

	public static final String GESTION_OSPIM_ESTADO_RECHAZADO="RE";
	public static final String GESTION_OSPIM_ESTADO_AUTORIZADO="AU";
	
	public static final String GESTION_OSPIM_TIPO_GESTION_RECHAZADO="RC";
		
	public static final String[][] ESTADOS_PREAUTORIZACIONES={{"CA","CARGADO"},{"AP","APP"},{"AU","AUTORIZADO"},{"RE","RECHAZADO"},{"OB","OBSERVADO"},{"NR","NO REQUIERE AUTORIZACION"},{"GO","GESTION OSPIM"},{"DE","DESESTIMADO"}};
	
	public static final String BUSQUEDA_RECLAMOS_PRESTACIONALES_DEL_AFILIADO_SIN_REINTEGRO = "LISTADO DE TODOS LOS RECLAMOS PRESTACIONALES DEL AFILIADO SIN REINTEGRO";
	public static final String RECLAMO_PRESTACION_EXPORTACION = "RECLAMO_PRESTACION_EXPORTACION";
	public static final String[][] TIPOS_ENTREGA={ {"P","PRESENCIAL"},{"E","EMAIL"} };
	public static final String PREAUTORIZACIONES_FILTRO   = "PREAUTORIZACIONES_FILTRO";	
	public static final String BUSQUEDA_PREAUTORIZACIONES_RESULT   = "BUSQUEDA_PREAUTORIZACIONES_RESULT";	
	public static final String EMAIL_AUTORIZACIONES   = "autorizaciones@ospim.org.ar;";
	public static final String EMAIL_AUDITORIA_MEDICA   = "auditoriamedica@ospim.org.ar;";
	public static final String EMAIL_SISTEMAS   = "sistemas@ospim.org.ar";
	public static final String FILTRO_BUSQUEDA_RECLAMOS_TOTAL_REGISTROS = "CANTIDAD_RECLAMOS";
	public static final String FILTRO_BUSQUEDA_RECLAMOS_OFFSET_REG = "FILTRO_BUSQUEDA_RECLAMOS_OFFSET_REG";
	public static final String FILTRO_BUSQUEDA_RECLAMOS_SECCIONAL = "FILTRO_BUSQUEDA_RECLAMOS_SECCIONAL";
	public static final String FILTRO_BUSQUEDA_RECLAMOS = "FILTRO_BUSQUEDA_RECLAMOS";

	
	public static final String ROL_PREAUTORIZACION_GERENCIAL = "Preautorizaciones_Gerencial";
	public static final String ROL_PREAUTORIZACION_ALERTA_ROJA = "Preautorizaciones_alerta_roja";
	public static final String ROL_PREAUTORIZACION_PROCESA_ARCHIVO_PREVENCION = "Preautorizaciones_procesa_archivo_prevencion";
	public static final String ROL_PREAUTORIZACION_GESTION_OSPIM = "Preautorizaciones_gestion_ospim";
	
	// EQUIPO INTERDISCPLINARIO 
	public static final String ROL_ABM_EQUIPO_INTERDISCIPLINARIO  ="ABM_EquipoInterdisciplinario";
	public static final String EQUIPO_DISCIPLINARIO_EN_EDICION   = "EQUIPO_INTERDISCIPLINARIO_EN_EDICION";
	public static final String BUSQUEDA_REGISTROS_EQUIPOS_INTERDISCIPLINARIOS = "LISTADO DE REGISTROS DE EQUIPOS INTERDISCIPLINARIOS";
	public static final String LISTADO_PRESTACIONES_EQUIPO_EN_SESION  = "LISTADO DE PRESTACIONES EQUIPOS DISCIPLINARIOS";
	public static final String OPCIONES_PRESTACION_EN_SESION  = "OPCIONES DE LA PRESTACION";
	public static final String[][] PREAUTORIZACIONES_GESTION_OSPIM_PEDIDO={ {"EX","EXCEPCION"}};
	public static final String[][] PREAUTORIZACIONES_GESTION_OSPIM_ESTADOS={ {"AU","AUTORIZADO"},{"RE","RECHAZADO"},{"OB","OBSERVADO"},{"DE","DESESTIMADO"}};
	public static final String[][] PREAUTORIZACIONES_GESTION_OSPIM_TIPOS_GESTION={ {"EX","EXTRACAPITA"},{"FD","FACTURACION DIRECTA"},{"RI","REINTEGRO"},{"RC","RECHAZADO"}};
	
    // prestaciones medicas de afiliados 
	public static final String TIPOS_SITUACIONES_MEDICAS_EN_SESION = "TIPOS SITUACIONES MEDICAS";
	public static final String ROL_ABM_SITUACIONES_MEDICAS  = "ABM_Situaciones_Medicas";
	public static final String BUSQUEDA_SITUACIONES_MEDICAS= "LISTADO DE SITUACIONES MEDICAS";
	public static final String FILTRO_BUSQUEDA_SITUACIONMEDICA_OFFSET_REG = "FILTRO_BUSQUEDA_SITUACIONMEDICA_OFFSET_REG";
	public static final String FILTRO_BUSQUEDA_SITUACIONMEDICA_TOTAL_REGISTROS = "CANTIDAD_SITUACIONESMEDICAS";	
	public static final String BUSQUEDA_REGISTROS_SITUACIONES_MEDICAS= "LISTADO DE REGISTROS DE SITUACIONES MEDICAS";	
	public static final String SITUACION_MEDICA_EN_EDICION   = "SITUACION_MEDICA_EN_EDICION";
	public static final String SITUACION_MEDICA_POPUP_EN_EDICION = "SITUACION_MEDICA_POPUP_EN_EDICION";
	public static final String LISTADO_PATOLOGIAS_SITUACION_MEDICA_EN_SESION  = "LISTADO_PATOLOGIAS_SITUACION_MEDICA_EN_SESION";		
	public static final String SUCCESS = "Success";	
	public static final String SITUACION_MEDICA_POPUPCONSULTA="SITUACION_MEDICA_POPUPCONSULTA"; 
	public static final String FILTRO_BUSQUEDA_SITUACIONMEDICA = "CANTIDAD_SITUACIONESMEDICAS";
	public static final String VTNAPOPUP_EDICION_SITUACION_MEDICA = "VTNAPOPUP_EDICION_SITUACION_MEDICA";
	public static final String REGISTROVTNAPOPUP_EDICION_SITUACION_MEDICA = "REGISTROVTNAPOPUP_EDICION_SITUACION_MEDICA";
	
	//INTEGRACION
	public static final String ROL_INTEGRACION = "ABM_Integracion";
	public static final String ROL_INTEGRACION_LIQUIDACION = "ABM_Integracion_Liquidacion";
	public static final String ROL_INTEGRACION_GENERACION = "ABM_Integracion_Generacion";
	public static final String ROL_INTEGRACION_INFORMES = "ABM_Integracion_Informes";
	public static final String ROL_INTEGRACION_RENDICION = "ABM_Integracion_Rendicion";
	public static final String[][] INTEGRACION_ENTIDADES={ {"OM","OMINT"},{"PS","PREVENCION"},{"OS","OSPIM"},{"CE","CEMIC"}};
	public static final String[][] INTEGRACION_ERRORES_UPLOAD={ {"AI","Afiliado Inexistente"},{"AB","Afiliado Dado de Baja"},{"AD","Afiliado no Discapacitado"},{"CV","Certificado Vencido"},
			{"PI","Prestador Inexistente"},{"NI","Prestación Inexistente"},{"CB","CBU Inexistente"},{"PS","Prestación Inválida Sola"},
			{"CE","Cantidad Prestaciones Excedidas"},{"DE","Dependencia Incorrecta"},{"FE","Fecha Prestación Inválida"},
			{"IC","Prestación Inválida en este período"},
			{"II","Importe Comprobante menor al solicitado"}};
	
	public static final String[][] COMPROBANTES_INTEGRACION= {{"","NDefinido"},{"FCP","A"},{"RCB","A"},{"FCP","B"},{"RCB","B"},
			                                                  {"FCP","C"},{"RCB","C"},{"FCP","M"},{"RCB","M"}};

	public static final String ROL_ESTADISTICA_PREST_AUTORIZADAS = "ESTADISTICA_PREST_AUTORIZADAS";
	public static final String DIAGNOSTICOS = "DIAGNOSTICOS";
	public static final String ORDENES_PAGO = "ORDENES_PAGO"; 
	
	public static final String SEGUIMIENTO_SUR_FILTRO   = "SEGUIMIENTO_SUR_FILTRO";	
	public static final String BUSQUEDA_SEGUIMIENTO_SUR_RESULT   = "BUSQUEDA_SEGUIMIENTO_SUR_RESULT";
	
	public static final String INTEGRACION_DEVOLUCION_TOTAL_REGISTROS = "INTEGRACION_DEVOLUCION_TOTAL_REGISTROS";
	public static final String INTEGRACION_DEVOLUCION_OFFSET_REG = "INTEGRACION_DEVOLUCION_OFFSET_REG";
	public static final String INTEGRACION_DEVOLUCION_FILTRO = "INTEGRACION_DEVOLUCION_FILTRO";
	public static final String INTEGRACION_DEVOLUCION_REGISTRO_EN_EDICION = "INTEGRACION_DEVOLUCION_REGISTRO_EN_EDICION";
	public static final String ROL_REABRIR_RECLAMO_PRESTACIONAL = "ABM_Reabrir_Reclamo_Prestacional";

	public static final String[] ESTADOS_AUTORIZACIONES_PRESTACIONALES={ "Pre Autorización","En Curso",
			      "Documentación Faltante","Cambio Prestador","Finalizado","Abandonado","Rechazado"};
	
	public static final String BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD_TOTAL_REGISTROS ="BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD_TOTAL_REGISTROS";
	public static final String BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD_OFFSET_REG="BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD_OFFSET_REG";
	public static final String TOPES_REINTEGROS = "TOPES_REINTEGROS";
	
	public static final String HISTORICO_RECLAMO = "HISTORICO_RECLAMO";
	
	public static final String RECLAMOS_PRESTACIONALES_REVISION_ESTADO_AUTORIZADO_EN_SESION = "RECLAMOS_PRESTACIONALES_REVISION_ESTADO_AUTORIZADO_EN_SESION";
}
	
	