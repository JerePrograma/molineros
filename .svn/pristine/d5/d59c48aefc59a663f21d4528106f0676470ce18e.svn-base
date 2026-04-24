package ar.com.ospim.afiliados.beans;

import java.math.BigDecimal;
import java.util.Comparator;

public class NombreArchivo implements Comparable<NombreArchivo> {
	
	String nombre;
	
	public NombreArchivo(String nombre) {
		this.nombre = nombre;
	}
	

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	
	public static Comparator<NombreArchivo> COMPARE_BY_NUMBER = new Comparator<NombreArchivo>() {
	       public int compare(NombreArchivo one, NombreArchivo other) {
	       BigDecimal a = new BigDecimal(one.getNombre().replaceAll("[^\\d.]", ""));
	       BigDecimal b = new BigDecimal(other.getNombre().replaceAll("[^\\d.]", ""));
	        return a.compareTo(b);
	       
	        
	        }
	  };

	@Override
	public int compareTo(NombreArchivo o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
