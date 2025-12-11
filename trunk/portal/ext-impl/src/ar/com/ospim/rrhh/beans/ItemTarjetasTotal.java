package ar.com.ospim.rrhh.beans;


import java.io.Serializable;
import java.sql.ResultSet;



public class ItemTarjetasTotal extends TarjetaAcceso implements Serializable {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2574505795689777865L;	
	
	private int total_registros;

	public int getTotal_registros() {
		return total_registros;
	}
	public void setTotal_registros(int total_registros) {
		this.total_registros = total_registros;
	}
   
	public static ItemTarjetasTotal  getMappingItemCorrespondencia(
			ResultSet rs, String prefix) throws Exception {

		ItemTarjetasTotal corr = TarjetaAcceso.getMappingBuscadorTotal(rs, prefix) ;
		
		return corr;
	}

	
}
