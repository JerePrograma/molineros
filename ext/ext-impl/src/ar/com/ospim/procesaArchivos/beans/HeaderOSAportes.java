package ar.com.ospim.procesaArchivos.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeaderOSAportes {
	private String codigo_registro; 	//2
	private String codigo_archivo; 		//10
	private String subcodigo;			//4
	private String uso_futuro1;			//6
	private Date fecha_proceso;			//8
	private String hora_proceso;		//6
	private String uso_futuro2;			//3
	private String high_value;			//6
	
	public HeaderOSAportes(String line) throws ParseException{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");		
		this.codigo_registro=line.substring(0,2);
		this.codigo_archivo=line.substring(2,12);
		this.subcodigo=line.substring(12,16);
		this.uso_futuro1=line.substring(16,22);
		this.fecha_proceso=sdf.parse(line.substring(22,30));
		this.hora_proceso=line.substring(30,36);
		this.uso_futuro2=line.substring(36).trim();
	}
	
	public String getCodigo_registro() {
		return codigo_registro;
	}
	public void setCodigo_registro(String codigoRegistro) {
		codigo_registro = codigoRegistro;
	}
	public String getCodigo_archivo() {
		return codigo_archivo;
	}
	public void setCodigo_archivo(String codigoArchivo) {
		codigo_archivo = codigoArchivo;
	}
	public String getSubcodigo() {
		return subcodigo;
	}
	public void setSubcodigo(String subcodigo) {
		this.subcodigo = subcodigo;
	}
	public String getUso_futuro1() {
		return uso_futuro1;
	}
	public void setUso_futuro1(String usoFuturo) {
		uso_futuro1 = usoFuturo;
	}
	public Date getFecha_proceso() {
		return fecha_proceso;
	}
	public void setFecha_proceso(Date fechaProceso) {
		fecha_proceso = fechaProceso;
	}
	public String getHora_proceso() {
		return hora_proceso;
	}
	public void setHora_proceso(String horaProceso) {
		hora_proceso = horaProceso;
	}
	public String getUso_futuro2() {
		return uso_futuro2;
	}
	public void setUso_futuro2(String usoFuturo) {
		uso_futuro2 = usoFuturo;
	}
	public String getHigh_value() {
		return high_value;
	}
	public void setHigh_value(String highValue) {
		high_value = highValue;
	}
	
	public String toString(){
		StringBuilder aux= new StringBuilder();
		aux.append("COD. REG: ");
		aux.append(this.getCodigo_registro());
		aux.append("\nCOD. ARCH: ");
		aux.append(this.getCodigo_archivo());
		aux.append("\nSUBCOD. ARCH: ");
		aux.append(this.getSubcodigo());
		aux.append("\nUSO FUTURO: ");
		aux.append(this.getUso_futuro1());
		aux.append("\nFECHA PROCESO: ");
		aux.append(this.getFecha_proceso());
		aux.append("\nHORA PROCESO: ");
		aux.append(this.getHora_proceso());
		aux.append("\nUSO FUTURO 2: ");
		aux.append(this.getUso_futuro2());
		return aux.toString();		
	}
	

}
