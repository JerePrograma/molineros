package ar.com.ospim.correspondencia.beans;

import java.io.Serializable;
import java.sql.ResultSet;

public class ItemCorrespondenciaTotal extends ItemCorrespondencia implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2574505795689350865L;
	
	private int total_registros;

	public int getTotal_registros() {
		return total_registros;
	}

	public void setTotal_registros(int total_registros) {
		this.total_registros = total_registros;
	}

	public static ItemCorrespondenciaTotal getMappingItemCorrespondencia(
			ResultSet rs, String prefix) throws Exception {
		
		ItemCorrespondenciaTotal corr = ItemCorrespondencia.getMappingItemCorrespondencia(rs, prefix);

		return corr;
	}

	
}
