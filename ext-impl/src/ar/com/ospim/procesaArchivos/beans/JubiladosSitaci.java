package ar.com.ospim.procesaArchivos.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JubiladosSitaci implements Serializable { 
	private static final long serialVersionUID = -5091085736859532856L;

	String beneficio="";
	String afiliado="";
	String tipo1="";
	String tipo2="";
	String dni="";
	String concepto="";
	Double sumatoria=0D;
	Double conceptoImporte=0D;
	String periodo="";
	String cuil="";
	Date nacimiento=null;
	String sexo="";
	String filler01="";
	String registro="";
	
	Integer totalRegistros;
	Date fechaLiquidado;
	Double importeLiquidado;
	String tercerizadora;
	String tercerizadoraDescripcion;
	Integer periodoLiquidacion;
	
	public JubiladosSitaci() {
		super();
	}

	
	public JubiladosSitaci(String beneficio, String afiliado, String tipo1, String tipo2, String dni, String concepto,
			Double sumatoria, Double conceptoImporte, String periodo, String cuil, Date nacimiento, String sexo,
			String filler01, String registro,Integer periodoLiquidacion) {
		super();
		this.beneficio = beneficio;
		this.afiliado = afiliado;
		this.tipo1 = tipo1;
		this.tipo2 = tipo2;
		this.dni = dni;
		this.concepto = concepto;
		this.sumatoria = sumatoria;
		this.conceptoImporte = conceptoImporte;
		this.periodo = periodo;
		this.cuil = cuil;
		this.nacimiento = nacimiento;
		this.sexo = sexo;
		this.filler01 = filler01;
		this.registro = registro;
		this.periodoLiquidacion=periodoLiquidacion;
	}



	public String getBeneficio() {
		return beneficio;
	}


	public void setBeneficio(String beneficio) {
		this.beneficio = beneficio;
	}


	public String getAfiliado() {
		return afiliado;
	}


	public void setAfiliado(String afiliado) {
		this.afiliado = afiliado;
	}


	public String getTipo1() {
		return tipo1;
	}


	public void setTipo1(String tipo1) {
		this.tipo1 = tipo1;
	}


	public String getTipo2() {
		return tipo2;
	}


	public void setTipo2(String tipo2) {
		this.tipo2 = tipo2;
	}


	public String getDni() {
		return dni;
	}


	public void setDni(String dni) {
		this.dni = dni;
	}


	public String getConcepto() {
		return concepto;
	}


	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}


	public Double getSumatoria() {
		return sumatoria;
	}


	public void setSumatoria(Double sumatoria) {
		this.sumatoria = sumatoria;
	}


	public Double getConceptoImporte() {
		return conceptoImporte;
	}


	public void setConceptoImporte(Double conceptoImporte) {
		this.conceptoImporte = conceptoImporte;
	}


	public String getPeriodo() {
		return periodo;
	}


	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}


	public String getCuil() {
		return cuil;
	}


	public void setCuil(String cuil) {
		this.cuil = cuil;
	}


	public Date getNacimiento() {
		return nacimiento;
	}


	public void setNacimiento(Date nacimiento) {
		this.nacimiento = nacimiento;
	}


	public String getSexo() {
		return sexo;
	}


	public void setSexo(String sexo) {
		this.sexo = sexo;
	}


	public String getFiller01() {
		return filler01;
	}


	public void setFiller01(String filler01) {
		this.filler01 = filler01;
	}


	public String getRegistro() {
		return registro;
	}


	public void setRegistro(String registro) {
		this.registro = registro;
	}


	public Integer getTotalRegistros() {
		return totalRegistros;
	}


	public void setTotalRegistros(Integer totalRegistros) {
		this.totalRegistros = totalRegistros;
	}


	public Date getFechaLiquidado() {
		return fechaLiquidado;
	}


	public void setFechaLiquidado(Date fechaLiquidado) {
		this.fechaLiquidado = fechaLiquidado;
	}


	public Double getImporteLiquidado() {
		return importeLiquidado;
	}


	public void setImporteLiquidado(Double importeLiquidado) {
		this.importeLiquidado = importeLiquidado;
	}


	public String getTercerizadora() {
		return tercerizadora;
	}


	public void setTercerizadora(String tercerizadora) {
		this.tercerizadora = tercerizadora;
	}


	public String getTercerizadoraDescripcion() {
		return tercerizadoraDescripcion;
	}


	public void setTercerizadoraDescripcion(String tercerizadoraDescripcion) {
		this.tercerizadoraDescripcion = tercerizadoraDescripcion;
	}


	public Integer getPeriodoLiquidacion() {
		return periodoLiquidacion;
	}


	public void setPeriodoLiquidacion(Integer periodoLiquidacion) {
		this.periodoLiquidacion = periodoLiquidacion;
	}
	
	
}
