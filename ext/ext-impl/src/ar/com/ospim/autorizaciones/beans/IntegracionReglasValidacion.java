package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class IntegracionReglasValidacion implements Serializable{
	
	public IntegracionReglasValidacion() {
		super();
	}

	private List<ReglaValidacion>reglas;

	public List<ReglaValidacion> getReglas() {
		return reglas;
	}

	public void setReglas(List<ReglaValidacion> reglas) {
		this.reglas = reglas;
	}	
		
		
	
}

