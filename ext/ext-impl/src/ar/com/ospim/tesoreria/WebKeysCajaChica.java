/**
 */
package ar.com.ospim.tesoreria;

public class WebKeysCajaChica implements com.liferay.portal.kernel.util.WebKeys {
	
	public static final String CAJA_CHICA_EN_EDICION = "CAJA_CHICA_EN_EDICION";
	public static final String CAJA_CHICA_COMPROBANTE_EN_EDICION = "CAJA_CHICA_COMPROBANTE_EN_EDICION";
	
	public static final int SOLICITAREPOSICION = 2;
	public static final int REPOSICIONAPROBADASINOP = 6;
	public static final String[][] ESTADO_CAJA_CHICA={ {"1","En Uso"},{"2","Solicita Reposición"},{"3","Reposición Aprobada"},{"4","Reposición Rechazada"},
        {"5","De baja"},{"6","Reposición Aprobada Sin OP"}  };
	
	public static final String ROL_ADMINISTRADOR_CAJA_CHICA = "CajaChica_Administrador";
	public static final String ROL_ADMINISTRADOR_CAJA_CHICA_SIN_OP = "CajaChica_Administrador_Sin_OP";
	public static final String ROL_USUARIO_CAJA_CHICA = "CajaChica_Usuario";
	public static final String COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION="COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION";
	public static final String COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION_AGRUPADO="COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION_AGRUPADO";
	public static final String COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION_AGRUPADO_COMPROBANTE="COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION_AGRUPADO_COMPROBANTE";
}