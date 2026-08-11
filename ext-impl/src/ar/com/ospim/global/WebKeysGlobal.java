/**
 */
package ar.com.ospim.global;

/**
 * <a href="WebKeys.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class WebKeysGlobal implements com.liferay.portal.kernel.util.WebKeys {
	public static final int UOMA=1;
	public static final int OSPIM=2;
	public static final int AMTIMA=3;
	public static final int ESTUDIO=4;
	public static final int EMPRESA=5;
	public static final String SQL_STATE_DUPLICATE_KEY = "23505";
	public static final String SQL_STATE_CHECK_VIOLATION= "23514";
	public static final String SQL_STATE_ROW_NOT_FOUND_UPDATE = "02000";
	public static final int ANIOS_MAYOR_EDAD = 21;
	public static final String PORTLET_LIQUIDACIONES = "LIQ_1";
	public static final String[] ENTIDADES_UOMA = { "O.S.P.I.M.","A.M.T.I.M.A." ,"U.O.M.A."
			 };
//	public static final String[] TIPOS_BONO = { "1-CONSULTA", "2-RUTINA",
//			"3-ALTA COMPLEJIDAD", "18-CONSULTA", "19-PRACTICA",
//			"100-FORMULARIO DE OPCION", "101-CONSULTA VISITAR", "202-RUTINA VISITAR", "303-ALTA COMPLEJIDAD VISITAR" };
	public static final String ENTIDAD_OSPIM = "O.S.P.I.M.";
	public static final String ENTIDAD_UOMA = "U.O.M.A.";
	public static final String ENTIDAD_AMTIMA = "A.M.T.I.M.A.";
	public static final String ROL_ENTIDAD_OSPIM = "Entidad_Ospim";
	public static final String ROL_ENTIDAD_AMTIMA = "Entidad_Amtima";
	public static final String ROL_ENTIDAD_UOMA = "Entidad_Uoma";
	public static final String ID_DEFAULT_ENTIDAD = "O.S.P.I.M.";
	public static final int[] ENTIDADES_UOMA_INDICES = { 0, 1, 2 };
	public static final String CAMBIO_SOLAPA = "CAMBIO_SOLAPA";
	public static final String COMPROBANTES_EN_SESSION = "COMPROBANTES_EN_SESSION";
	public static final String COMPROBANTE_EN_EDICION = "COMPROBANTE_EN_EDICION";
	public static final String SUMA_COMPROBANTES_EN_SESSION = "SUMA_COMPROBANTES_EN_SESSION";
	public static final String[] LISTA_SERVICIO = { "AMBULATORIO",
			"INTERNACIÓN CLÍNICA", "PARTO NORMAL", "PARTO POR CESAREA",
			"QUIRÚRGICO" };
	public static final String ENTIDAD_COMPROBANTE_ORDEN_PAGO_OSPIM = "opo";
	public static final String ENTIDAD_COMPROBANTE_LIQUIDACION = "liq";
	public static final String ENTIDAD_COMPROBANTE_ORDEN_PAGO_AMTIMA = "opoamtima";
	public static final String COMPROBANTE_NOTA_DEBITO = "NDB";
	public static final String COMPROBANTE_NOTA_DEBITO_FARMACIA = "NDF";
	public static final String COMPROBANTE_NOTA_CREDITO = "NCR";
	public static final String COMPROBANTE_NOTA_CREDITO_BIS = "NCP";
	public static final String COMPROBANTE_ANTICIPO = "ANT";
	public static final String COMPROBANTE_VARIOS = "VAR";
	public static final String COMPROBANTE_REINTEGRO = "REI";
	public static final String COMPROBANTE_FACTURA = "FCP";
	public static final String COMPROBANTE_FACTURA_CREDITO = "FCE";
	public static final String COMPROBANTE_NOTA_CREDITO_ELECTRONICA = "NCE";
	public static final String COMPROBANTE_RECIBO = "RCB";
	public static final String COMPROBANTE_TICKET = "TCK";
	public static final String CUIT_OSPIM = "30629138567";
	public static final String CUIT_UOMA = "30531143856";
	public static final String CUIT_AMTIMA = "30604119568";
	public static final int TIPO_MOVIMIENTO_BANCARIO_CHEQUE_DEPOSITADO = 16;
	public static final int ID_ESTADO_CHEQUE_RECHAZADO = 5;
	
	// IMPORTANTE: esto no se debe usar mas, lo dejo aca simplemente como
	// recordatorio de que lo ids no pueden estar hardcodeados, sino que deben
	// hacer algo como: ConceptoServiceUtil.getIdPrestacionesMedicas()

	// public static final int ID_CONCEPTO_AJUSTES = 126;
	// public static final int ID_CONCEPTO_CANJE_CHEQUE = 225;
	// public static final int ID_CONCEPTO_REINTEGROS = 132;
	// public static final int ID_CONCEPTO_CONVENIOS_GLOBALES_OMINT =127;
	// public static final int ID_CONCEPTO_CONVENIOS_GLOBALES_NO_LIQUIDACIONES =
	// 222;
	// public static final int ID_CONCEPTO_PRESTACIONES_MEDICAS =149;
	// public static final int CONCEPTO_SUELDO_ID1 = 64;
	// public static final int CONCEPTO_SUELDO_ID2 = 104;
	//
	public static final String DESCRIPCION_CONCEPTO_REINTEGROS = "REINTEGRO AFILIADOS";
	public static final String DESCRIPCION_CONCEPTO_REINTEGROS_FARMACIA = "REINTEGRO AFILIADOS FARMACIA";
	public static final String DESCRIPCION_CONCEPTO_PRESTACIONES_MEDICAS = "PRESTACIONES MEDICAS";
	public static final String CONSOLIDAR_CUIT = "30520634971";
	public static final String OMINT_CUIT = "30550245309";
	public static final String PREVENCION_CUIT = "30713045000";
	public static final String ENSALUD_CUIT = "30707118454";
	public static final String STRING_OSPIM = "Ospim";
	public static final String STRING_OMINT = "Omint";
	public static final String DOCUMENTOS_DISCAPACIDAD = "DOCUMENTOS_DISCAPACIDAD";
	public static final String DOCUMENTOS_CIE = "DOCUMENTOS_CIE";
	public static final String ROL_ABM_DISCAPACIDAD = "ABM_Discapacidad";
	public static final String TIPOS_DISCAPACIDAD = "TIPOS_DISCAPACIDAD";

	public static final String ROL_CONSULTA_RRHH = "Consulta_Rrhh";
	public static final String ROL_ABM_RRHH = "ABM_Rrhh";
	
	public static final int ID_RAMO_EMPRESA = 99;
	public static final String BUSQUEDA_LECTURAS = "BUSQUEDA_LECTURAS";	
	
	
	public static final String LISTA_DESTINATARIOS="LISTA_DESTINATARIOS";
	public static final String LISTAS_MAILING="LISTAS_MAILING";
	public static final String ALL_LISTAS_MAILING="ALL_LISTAS_MAILING";
	public static final String LISTAS_MAILING_EN_EDICION="LISTAS_MAILING_EN_EDICION";
	
	public static final String LISTA_TARJETAS_ACCESO = "LISTA_TARJETAS_ACCESO";	
	public static final String DESTINATARIO_EN_SESSION = "DESTINATARIO_EN_SESSION";
	public static final String LISTA_USUARIOS_CORRESPONDENCIA = "LISTA_USUARIOS_CORRESPONDENCIA";
	
	public static final String LISTA_BOLETINES="LISTA_BOLETINES";
	public static final String BOLETIN_EN_EDICION="BOLETIN_EN_EDICION";
	public static final String[] SECCIONES_MAIL = {"TITULO", "NOTICIAS", "REPORTAJES",	"NOVEDADES", "DOCUMENTOS" };
	public static final int TIPO_BOLETA_OS=0;
	public static final int TIPO_BOLETA_AMTIMA=1;
	public static final int TIPO_BOLETA_SOCIAL_UOMA=2;
	public static final int TIPO_BOLETA_USUFRUCTO=3;
	public static final int TIPO_BOLETA_ART_46=4;
	public static final int TIPO_BOLETA_SOLIDARIO_UOMA=5;
	public static final int TIPO_BOLETA_BLANCA_OSPIM=6;
	public static final int TIPO_BOLETA_BLANCA_UOMA=7;
	public static final int TIPO_BOLETA_BLANCA_AMTIMA=8;
	public static final String DENO_APORTE_OSPIM="APORTE OS.";
	public static final String SECCIONALES_EN_SESSION = "seccionales_session";
	public static final String DIRECCIONES_EN_SESSION = "DIRECCIONES_EN_SESSION";
	public static final String FERIADOS="FERIADOS";
	public static final String SEGUIMIENTO_EMPRESA="seguimiento_empresa";
	
	public static final String FAIM="FAIM";
	public static final String CAENA="CAENA";
	public static final String CEPA="CEPA";
	public static final String SOLO_VER="SOLO_VER";
	
	public static final String ROL_INTERBANKING = "INTERBANKING";
	public static final String INTERBANKING_OPS= "INTERBANKING_OPS";
	public static final int ENTIDADESUNIFICADAS=123;
}

