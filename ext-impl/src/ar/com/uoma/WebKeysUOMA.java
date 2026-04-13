/**
 */
package ar.com.uoma;

/**
 * <a href="WebKeys.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Federico Brachi
 * 
 */
public class WebKeysUOMA implements com.liferay.portal.kernel.util.WebKeys {
	
	public static final String ROL_ABM_UOMA_ARCHIVOS = "ABM_UOMA_ARCHIVOS";
	public static final String ROL_ABM_UNIDAD_OPERATIVA = "ABM_UNIDAD_OPERATIVA";	
	public static final String ROL_ABM_CORRESPONDENCIA="ABM_Correspondencia";
	public static final String ROL_ABM_ORDEN_PAGO_UOMA="ABM_ORDEN_PAGO_UOMA";
	public static final String VER_SOLAPA_ACTA_UOMA="Ver_ACTA_UOMA";
	public static final String VER_SOLAPA_CONVENIO_UOMA="Ver_CONVENIO_UOMA";
	public static final String VER_REPORTES_UOMA="VER_REPORTES_UOMA";
	public static final String VER_REPORTES_CTACTE_ACTAS_CONVENIOS_UOMA="VER_REPORTES_CTACTE_ACTAS_CONVENIOS_UOMA";
	public static final String VER_REPORTES_GENERALES_UOMA="VER_REPORTES_GENERALES_UOMA";
	public static final String ROL_ABM_CENTRO_COSTO_UOMA="ABM_CENTRO_COSTO_UOMA";
	public static final String CENTRO_COSTO_EN_EDICION="CENTRO_COSTO_EN_EDICION";
	public static final String CENTRO_COSTO_FILTRO="CENTRO_COSTO_FILTRO";
	public static final String ROL_TABLERO_CENTRO_COSTO_UOMA="TABLERO_CENTRO_COSTO_UOMA";
	public static final String CENTRO_COSTO_COMPROBANTES="CENTRO_COSTO_COMPROBANTES";
	public static final String SUBIR_PARITARIAS="SUBIR_PARITARIAS";
	public static final String BUSCAR_PARITARIAS = "BUSCAR_PARITARIAS"; 
	public static final String[][] CAMARA={{"CAENA","CAENA"}, {"CEPA","CEPA"} ,{"FAIM","FAIM"}};
	public static final String ALTA_PARITARIAS = "ALTA_PARITARIAS"; 
	public static final String SUELDOS_BASICOS = "SUELDOS_BASICOS"; 
	public static final String JORNALES_BASICOS = "JORNALES_BASICOS"; 
	public static final String PROVEEDOR_EN_EDICION = "PROVEEDOR_EN_EDICION";
	public static final String PROVEEDORES_RESULT="PROVEEDORES_RESULT";
	public static final String CTACTE_RESULT="CTACTE_RESULT";
	public static final String CTACTE_RESULT_EXPORT="CTACTE_RESULT_EXPORT";
	public static final String CTACTE_RESULT_EXPORT_SALDOINI="CTACTE_RESULT_EXPORT_SALDOINI";
	public static final String CTACTE_RESULT_TIT_PERIODO="CTACTE_RESULT_TIT_PERIODO";
	public static final String CTACTE_RESULT_TIT_ACCION="CTACTE_RESULT_TIT_ACCION";
	
	public static final String CTACTE_RESULT_TOT="CTACTE_RESULT_TOT";
	public static final String CTACTE_RESULT_TOT_DDJJ="CTACTE_RESULT_TOT_DDJJ";
	public static final String CTACTE_RESULT_TOT_BOLETAS="CTACTE_RESULT_TOT_BOLETAS";
	public static final String CTACTE_RESULT_TOT_CUIT="CTACTE_RESULT_TOT_CUIT";
	public static final String CTACTE_RESULT_TOT_RAZSOC="CTACTE_RESULT_TOT_RAZSOC";
	public static final String CTACTE_RESULT_TOT_TIPOCTA="CTACTE_RESULT_TOT_TIPOCTA";
	public static final String CTACTE_RESULT_TOT_ACTAS="CTACTE_RESULT_TOT_ACTAS";
	public static final String CTACTE_RESULT_TOT_SALDO_INI="CTACTE_RESULT_TOT_SALDO_INI";
	
	public static final String CTACTE_EMPRESAS_OFFSET_REG="CTACTE_EMPRESAS_OFFSET_REG";
	public static final String CTACTE_EMPRESAS_TOTAL_REGISTROS="CTACTE_EMPRESAS_TOTAL_REGISTROS"; 
	
	//public static final String[][] CATEGORIAS_IVA={{"RI","Responsable Inscripto"}, {"MT","Monotributista"}, {"EX","Exento"}, {"CS","Consumidor Final"}}; //no cambiar el orden
	public static final String[][] CATEGORIAS_IVA={{"AC","Activo"}, {"NI","No Inscripto"}, {"EX","Exento"}, {"NA","No Alcanzado"},
			{"XN","Exento No Alcanzado"} ,{"AN","Activo No Alcanzado"},{"NC","No Corresponde"}   }; //no cambiar el orden
	
	public static final String[][] CATEGORIAS_MONOTRIBUTO={{"A","A"}, {"B","B"} ,{"C","C"},{"D","D"},{"E","E"},{"F","F"},{"G","G"},{"H","H"},{"I","I"},{"J","J"} };
	public static final String[][] FORMAS_PAGO={{"CH","Cheque"}, {"TR","Transferencia"}};
	
	public static final String ROL_PROVEEDORES = "ABM_Proveedores";
	
	public static final String ROL_FACTURACION = "ABM_FACTURACION";
	public static final String ROL_FACTURACION_MANUAL = "ABM_FACTURACION_MANUAL";
	
	public static final String FACTURA_EN_EDICION = "FACTURA_EN_EDICION";
	public static final String FACTURA_DETALLE_EN_EDICION = "FACTURA_DETALLE_EN_EDICION";
	public static final String PRODUCTOS_EN_SESSION = "PRODUCTOS_EN_SESSION";
	public static final String CLIENTES_EN_SESSION = "CLIENTES_EN_SESSION";
	public static final String FILTRO_BUSQUEDA_FACTURAS = "FILTRO_BUSQUEDA_FACTURAS";
	public static final String BUSQUEDA_FACTURAS_RESULT = "BUSQUEDA_FACTURAS_RESULT";
	public static final String FILTRO_BUSQUEDA_FACTURAS_TOTAL_REGISTROS = "FILTRO_BUSQUEDA_FACTURAS_TOTAL_REGISTROS";
	public static final String FILTRO_BUSQUEDA_FACTURAS_OFFSET_REG = "FILTRO_BUSQUEDA_FACTURAS_OFFSET_REG";
	public static final String[][] PUNTOS_DE_VENTA_HOTELES={{"00010","EVA PERON"},{"00020","LOS DIQUES"},{"00030","30 de JUNIO"},{"00031","00031"},
			{"00200","00200"},{"00300","00300"},{"00400","00400"},{"10400","10400"}};

	
	public static final String SALDOINICIAL_EN_EDICION = "SALDOINICIAL_EN_EDICION";
	
	public static final String RET_IIBB = "IIBB";
	public static final String RET_IVA = "RIVA";
	public static final String RET_SUSS = "RSUS";
	
	public static final Integer TARJETA_VISA = 1;
	public static final Integer TARJETA_MAESTRO = 2;
	public static final Integer TARJETA_AMEX = 3;
	public static final Integer TARJETA_CABAL = 4;
	public static final Integer TARJETA_MASTERCARD = 5;
	public static final Integer TARJETA_OTRAS = 6;
	
	public static final Integer TARJETA_BANCO_DEFECTO = 28;

	public static final String ACCION = "ACCION";
	public static final String ACCION_EDIT = "EDIT";
	public static final String ACCION_DELETE = "DELETE";
	public static final String ACCION_NEW = "NEW";

}