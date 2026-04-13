package ar.com.ospim.afiliados.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.util.DateUtils;

/**
 * @author Administrador
 * @version 1.0
 * @created 29-Jul-2010 11:34:23 a.m.
 */

public class AporteAfiliado implements Serializable {
	private static final long serialVersionUID = -6651256311894113920L;
	private Afiliado afiliado;
	private Empresa empleador;
	private Date periodo;
	private Date fechaTransf;
	private BigDecimal importe;
	private BigDecimal contribucionEstimada;
	private BigDecimal totalLiqTercerizadora;
	private BigDecimal comisionOS;
	private Date fechaLiqTercerizadora;
	private BigDecimal liqActas;
	private BigDecimal remuneracion;
	private Date fechaRecauda;
	private String concepto;
	private String idTerc;
	private int tipoAporte;
	private String tipoAporteDeno;
	private String camara;
	private String categoriaSalarial; 
	private BigDecimal importeNoRemunerativo; 
	private boolean mostrar;
	
	public AporteAfiliado() {
	}

	public AporteAfiliado(String cuil_titular, String cuit, Date periodo,
			double importe) {
		this.afiliado = new Afiliado(cuil_titular);
		this.empleador = new Empresa(cuit);
		this.periodo = new Date(periodo.getTime());
		this.importe = new BigDecimal(importe);
	}

	public AporteAfiliado(String cuil_titular, String apellido, String nombre,
			Date fecha_ingre, Date fecha_baja, String cuit, String razon_soc,
			Date periodo, double importe, Date fecha_transf, BigDecimal contr,
			BigDecimal totalTerc, Date fechaLiqF, BigDecimal liqActas, BigDecimal comisionOS, Date fechaTransf) {
		this.afiliado = new Afiliado(cuil_titular, 0, nombre, apellido);
		afiliado.setIngre_fecha(fecha_ingre);
		afiliado.setBaja_fecha(fecha_baja);
		this.empleador = new Empresa(cuit, null, razon_soc);
		this.periodo = new Date(periodo.getTime());
		this.importe = new BigDecimal(importe);
		this.contribucionEstimada = contr;
		this.totalLiqTercerizadora = totalTerc;
		this.fechaLiqTercerizadora = fechaLiqF;
		this.liqActas = liqActas;
		this.comisionOS=comisionOS;
		this.fechaTransf=fecha_transf;
	}
	
	public AporteAfiliado(int tipo_aporte, String deno_tipo_aporte, String cuil_titular, String apellido, String nombre,
			Date fecha_ingre,  String cuit, String razon_soc,
			Date periodo, BigDecimal remuneracion, BigDecimal importe, Date fechaTransf, Date fechaRecauda) {
		this.tipoAporte=tipo_aporte;
		this.tipoAporteDeno=deno_tipo_aporte;
		this.afiliado = new Afiliado(cuil_titular, 0, nombre, apellido);
		afiliado.setIngre_fecha(fecha_ingre);
		this.empleador = new Empresa(cuit, null, razon_soc);
		this.periodo = new Date(periodo.getTime());
		this.remuneracion=remuneracion;
		this.importe = importe;		
		this.fechaTransf=fechaTransf;
		this.fechaRecauda=fechaRecauda;
	}
	
	public AporteAfiliado(int tipo_boleta, String deno_tipo_aporte, String cuil_titular, String apellido, String nombre,
			Date fecha_ingre, Date fecha_baja, String cuit, String razon_soc,
			Date periodo, double importe, Date fecha_transf, BigDecimal contr,
			BigDecimal totalTerc, Date fechaLiqF, BigDecimal liqActas, BigDecimal comisionOS, Date fechaTransf, Date fechaRecauda, String concepto,  
			BigDecimal remuneracion, String id_terc) {
		this.tipoAporte=tipo_boleta;
		this.tipoAporteDeno=deno_tipo_aporte;
		this.afiliado = new Afiliado(cuil_titular, 0, nombre, apellido);
		afiliado.setIngre_fecha(fecha_ingre);
		afiliado.setBaja_fecha(fecha_baja);
		this.empleador = new Empresa(cuit, null, razon_soc);
		this.periodo = new Date(periodo.getTime());
		this.importe = new BigDecimal(importe);
		this.contribucionEstimada = contr;
		this.totalLiqTercerizadora = totalTerc;
		this.fechaLiqTercerizadora = fechaLiqF;
		this.liqActas = liqActas;
		this.comisionOS=comisionOS;
		this.fechaTransf=fecha_transf;
		this.fechaRecauda=fechaRecauda;
		this.concepto=concepto;
		this.remuneracion=remuneracion;
		this.idTerc=id_terc;
	}


	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public Empresa getEmpleador() {
		return empleador;
	}

	public void setEmpleador(Empresa empleador) {
		this.empleador = empleador;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public String getPeriodoAsString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return periodo != null ? sdf.format(this.periodo) : null;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public String getImporteAsString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return importe != null ? formatter.format(importe.doubleValue()) : "";
	}
	public String getRemuneracionAsString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return remuneracion != null ? formatter.format(remuneracion.doubleValue()) : "";
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public void setContribucionEstimada(BigDecimal contribucionEstimada) {
		this.contribucionEstimada = contribucionEstimada;
	}

	public BigDecimal getContribucionEstimada() {
		return contribucionEstimada;
	}

	public void setFechaLiqTercerizadora(Date fechaLiqTercerizadora) {
		this.fechaLiqTercerizadora = fechaLiqTercerizadora;
	}

	public Date getFechaLiqTercerizadora() {
		return fechaLiqTercerizadora;
	}

	public String getFechaLiqTercerizadoraString() {
		return null != fechaLiqTercerizadora ? DateUtils.format(
				fechaLiqTercerizadora, DateUtils.SHORT) : "";
	}

	public void setTotalLiqTercerizadora(BigDecimal totalLiqTercerizadora) {
		this.totalLiqTercerizadora = totalLiqTercerizadora;
	}

	public BigDecimal getTotalLiqTercerizadora() {
		return totalLiqTercerizadora;
	}

	/**
	 * @return the liqActas
	 */
	public BigDecimal getLiqActas() {
		return liqActas;
	}

	/**
	 * @param liqActas the liqActas to set
	 */
	public void setLiqActas(BigDecimal liqActas) {
		this.liqActas = liqActas;
	}
	
	public String getLiqActasAsString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return liqActas != null ? formatter.format(liqActas.doubleValue()) : null;
	}
	
	public String getComisionOSAsString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return comisionOS != null ? formatter.format(comisionOS.doubleValue()) : "";
	}

	public BigDecimal getComisionOS() {
		return comisionOS;
	}

	public void setComisionOS(BigDecimal comisionOS) {
		this.comisionOS = comisionOS;
	}

	public Date getFechaTransf() {
		return fechaTransf;
	}
	
	public String getFechaTransfAsString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return fechaTransf != null ? sdf.format(this.fechaTransf) : "";
	}
	
	public String getFechaRecaudaAsString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return fechaRecauda != null ? sdf.format(this.fechaRecauda) : "";
	}

	public void setFechaTransf(Date fechaTransf) {
		this.fechaTransf = fechaTransf;
	}

	public BigDecimal getRemuneracion() {
		return remuneracion;
	}

	public void setRemuneracion(BigDecimal remuneracion) {
		this.remuneracion = remuneracion;
	}

	public Date getFechaRecauda() {
		return fechaRecauda;
	}

	public void setFechaRecauda(Date fecha_recauda) {
		this.fechaRecauda = fecha_recauda;
	}

	public String getConcepto() {
		return concepto!=null?concepto:"";
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getIdTerc() {
		return idTerc;
	}

	public void setIdTerc(String idTerc) {
		this.idTerc = idTerc;
	}

	public int getTipoAporte() {
		return tipoAporte;
	}

	public String getCamara() {
		return camara;
	}

	public String getCategoriaSalarial() {
		return categoriaSalarial;
	}

	public BigDecimal getImporteNoRemunerativo() {
		return importeNoRemunerativo;
	}

	public void setTipoAporte(int tipoAporte) {
		this.tipoAporte = tipoAporte;
	}

	public void setCamara(String camara) {
		this.camara = camara;
	}

	public void setCategoriaSalarial(String categoriaSalarial) {
		this.categoriaSalarial = categoriaSalarial;
	}

	public void setImporteNoRemunerativo(BigDecimal importeNoRemunerativo) {
		this.importeNoRemunerativo = importeNoRemunerativo;
	}

	public boolean isMostrar() {
		return mostrar;
	}

	public void setMostrar(boolean mostrar) {
		this.mostrar = mostrar;
	}

	public String getTipoAporteDeno() {
		return tipoAporteDeno;
	}

	public void setTipoAporteDeno(String tipoAporteDeno) {
		this.tipoAporteDeno = tipoAporteDeno;
	}
	
	
	

}