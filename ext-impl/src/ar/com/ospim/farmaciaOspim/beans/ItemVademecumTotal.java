package ar.com.ospim.farmaciaOspim.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import ar.com.ospim.farmacia.beans.Vademecum;

public class ItemVademecumTotal extends Vademecum  implements Serializable {
	
	private static final long serialVersionUID = -2579999795689777865L;	
	
	private int total_registros;

	public int getTotal_registros() {
		return total_registros;
	}
	
	public void setTotal_registros(int total_registros) {
		this.total_registros = total_registros;
	}
   
	public static ItemVademecumTotal  getMappingItemCorrespondencia(
		ResultSet rs, String prefix) throws Exception {
		
		ItemVademecumTotal corr =  Vademecum.getMappingTotal(rs, prefix); 

		return corr;
	}

	
}
