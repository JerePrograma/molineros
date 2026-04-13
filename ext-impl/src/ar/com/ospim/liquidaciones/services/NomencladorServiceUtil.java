package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <a href="NomencladorServiceUtil.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * <p>
 * </p>
 * 
 * @author Gustavo Fernandez
 * 
 */
public class NomencladorServiceUtil {

	private static NomencladorServiceImpl instance = null;

	public static NomencladorServiceImpl getInstance() {
		if (null == instance) {
			instance = new NomencladorServiceImpl();
		}
		return instance;
	}
	
	public static int getIncrementarNomenclador(Date vigAumento,
			BigDecimal porc_aumento, String resolucion , boolean ttos , int nomenclador, String usuario_modi, int cod_desde , int cod_hasta) throws Exception {
		
		int aumentoNomenclador = getInstance().getIncrementarNomenclador(vigAumento,
				porc_aumento,resolucion ,ttos ,nomenclador,	usuario_modi ,cod_desde ,cod_hasta);
		
		return aumentoNomenclador;
	}
}
