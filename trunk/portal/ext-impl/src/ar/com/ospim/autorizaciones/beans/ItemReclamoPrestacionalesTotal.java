package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;

public class ItemReclamoPrestacionalesTotal extends ReclamoPrestacional implements Serializable {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2574505795689777865L;	
	
	private int total_registros;
	private String prestacionesConcat;

	public int getTotal_registros() {
		return total_registros;
	}
	
	public void setTotal_registros(int total_registros) {
		this.total_registros = total_registros;
	}

	public String getPrestacionesConcat() {
		return prestacionesConcat;
	}

	public void setPrestacionesConcat(String prestacionesConcat) {
		this.prestacionesConcat = prestacionesConcat;
	}

	
}
