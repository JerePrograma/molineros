package ar.com.ospim.farmaciaOspim.beans;

import java.io.Serializable;
import java.sql.ResultSet;

import ar.com.ospim.farmacia.beans.Medicamento;

public class ItemMedicacionTotal extends Medicamento  implements Serializable {
	
	private static final long serialVersionUID = -2574505795689777865L;	
	
	private int total_registros;

	public int getTotal_registros() {
		return total_registros;
	}
	
	public void setTotal_registros(int total_registros) {
		this.total_registros = total_registros;
	}
   
	public static ItemMedicacionTotal  getMappingItemCorrespondencia(
		ResultSet rs, String prefix) throws Exception {
		
		ItemMedicacionTotal corr =  Medicamento.getMappingOspimTotal(rs, prefix); 

		return corr;
	}

	
}
