package ar.com.ospim.afiliados.reportes.beans;

import java.util.Date;
import java.util.Locale;

import com.liferay.ibm.icu.text.SimpleDateFormat;


public class PadronInformado {

	private Date fecha;
	private Date periodo;
	private String tercerizadora;
	private String tipo;
	private String idTerc;
	
	public PadronInformado(){		
	}
	
	public PadronInformado(Date fecha, String id_terc, String descripcion, String tipo, Date periodo){
		this.fecha=fecha;
		this.tercerizadora=descripcion;
		this.tipo=tipo;
		this.idTerc=id_terc;
		this.periodo=periodo;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getTercerizadora() {
		return tercerizadora;
	}

	public void setTercerizadora(String tercerizadora) {
		this.tercerizadora = tercerizadora;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public String getFechaAsString(){
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return this.fecha!=null?sdf.format(this.fecha):"";
	}

	public String getPeriodoAsString(){
//		SimpleDateFormat sdf=new SimpleDateFormat("MM/yyyy");
  		SimpleDateFormat sdf = new SimpleDateFormat("MMM/yyyy",  new Locale("es", "ES"));

		return this.periodo!=null?sdf.format(this.periodo):"";
	}
	
	public String getIdTerc() {
		return idTerc;
	}

	public void setIdTerc(String idTerc) {
		this.idTerc = idTerc;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}
			
		
}
