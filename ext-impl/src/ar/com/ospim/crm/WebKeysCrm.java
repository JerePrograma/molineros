package ar.com.ospim.crm;

/**
 * 
 * @author SVA
 * 
 */
public class WebKeysCrm implements com.liferay.portal.kernel.util.WebKeys {

	public static final String PORTLET_AFILIADO = "AFI_1";
	public static final String PORTLET_CRM = "CRM_1";
	public static final String PORTLET_TAB_CRM_CONTACTO = "TAB_CRM_CONTACTO";

	
	public static final String CRM_LISTA_MOTIVOS = "CRM_LISTA_MOTIVOS";
	public static final String CRM_LISTA_TIPOS = "CRM_LISTA_TIPOS";
	public static final String CRM_LISTA_CATEGORIAS = "CRM_LISTA_CATEGORIAS";
	public static final String CRM_LISTA_TIPOS_RECLAMO = "CRM_LISTA_TIPOS_RECLAMO";
	public static final String CRM_AFILIADO = "CRM_AFILIADO";
	public static final String CRM_AFILIADO_DOMICILIO = "CRM_AFILIADO_DOMICILIO";
	public static final String CRM_AFILIADO_EMAIL = "CRM_AFILIADO_EMAIL";

	public static final String CRM_CONTACTO_EFICACIA = "CRM_CONTACTO_EFICACIA";

	public static final String CRM_ES_AFILIADO = "CRM_ES_AFILIADO";
	public static final String CRM_ID_CONTACTO = "CRM_ID_CONTACTO";

	public static final String[] CRM_ESTADOS = { "PENDIENTE", "DERIVADO", "CERRADO"};
	public static final String[][] CRM_IMPORTANCIA = { {"0", "NORMAL"},{"1", "URGENTE"} };

	public static final String CRM_ESTADO_DEFAULT = "CERRADO";
	public static final int CRM_TIPO_CONT_DEFAULT = 1;    //Llamado Entrante
	public static final int CRM_TIPO_RECLAMO_DEFAULT = 5; //Carta doc/nota
	public static final int CRM_CATEGORIA_DEFAULT = 1;    //Consulta
	public static final int CRM_MOTIVO_DEFAULT = 1;       //Otros
	
	
	public static final String ROL_ABM_CRM = "ABM_CRM";
	public static final String ROL_CRM_Auditoria = "CRM_Auditoria";
	public static final String ROL_CRM_Busqueda = "CRM_Busqueda";

	public static final String ROL_ABM_CRM_LEGALES = "ABM_CRM_LEGALES";
	public static final String ROL_CONSULTA_CRM_LEGALES = "consulta_CRM_LEGALES";
	
	public static final String CRM_ULTIMOS_CONTACTOS = "CRM_ULTIMOS_CONTACTOS";
	public static final String CRM_CONTACTO_EN_EDICION = "CRM_CONTACTO_EN_EDICION";
	public static final String CRM_CONTACTO_EN_VIEW = "CRM_CONTACTO_EN_VISTA";
	public static final String CRM_EFICACIA_EN_EDICION = "CRM_EFICACIA_EN_EDICION";
	public static final String CRM_DERIVACIONES_CONTACTO = "CRM_DERIVACIONES_CONTACTO";
	public static final String CRM_DOCUM_LEGAL_EN_EDICION = "CRM_DOCUMENTO_LEGAL_EN_EDICION";
	public static final String CRM_DOCUM_LEGAL_EN_VIEW = "CRM_DOCUMENTO_LEGAL_EN_VISTA";
	public static final String CRM_ULTIMOS_DOCUM_LEGAL = "CRM_ULTIMOS_RECLAMOS";
	public static final String CRM_DOCUM_LEGAL_FOLDER = "DocumentosLegal";
	public static final String CRM_DOCUM_LEGAL_SUBFOLDER = "Reclamo_";
	public static final String BUSQUEDA_CONTACTOS_RESULT = "busqueda_contactos_result";
	public static final String FILTRO_BUSQUEDA_CONTACTOS = "filtro_busqueda_contactos";
	public static final String FILTRO_BUSQUEDA_CONTACTOS_TOTAL_REGISTROS = "filtro_busqueda_contactos_total_registros";
	public static final String FILTRO_BUSQUEDA_CONTACTOS_OFFSET_REG = "filtro_busqueda_contactos_offset_reg";
	public static final String BUSQUEDA_DOC_LEGAL_RESULT = "busqueda_reclamos_result";
	public static final String FILTRO_BUSQUEDA_DOC_LEGAL = "filtro_busqueda_reclamos";
	public static final String FILTRO_BUSQUEDA_DOC_LEGAL_TOTAL_REGISTROS = "filtro_busqueda_reclamos_total_registros";
	public static final String FILTRO_BUSQUEDA_DOC_LEGAL_OFFSET_REG = "filtro_busqueda_reclamos_offset_reg";
	
	public static final String ROL_VER_PORTLET_CAI = "VER_PORTLET_CAI";
	public static final String CRM_ES_PERSONAL_SECCIONAL = "CRM_ES_PERSONAL_SECCIONAL";
	public static final String CRM_PERSONAL_SECCIONAL = "CRM_PERSONAL_SECCIONAL";
	public static final String CRM_PRESTADOR = "CRM_PRESTADOR";
	public static final String CRM_ES_PRESTADOR = "CRM_ES_PRESTADOR";
	public static final String CRM_EMPRESA = "CRM_EMPRESA";
	public static final String CRM_ES_EMPRESA = "CRM_ES_EMPRESA";
	public static final String CRM_COMPANIERO = "CRM_COMPANIERO";
	public static final String CRM_ES_COMPANIERO = "CRM_ES_COMPANIERO";
	public static final String CRM_CIERRE_CONTACTO_PREDET = "CRM_CIERRE_CONTACTO_PREDETERMINADO";
	

} 