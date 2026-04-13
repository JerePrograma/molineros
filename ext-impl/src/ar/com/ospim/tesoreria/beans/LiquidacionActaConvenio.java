package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LiquidacionActaConvenio {
	//FEC_OBLIG	NUMERO	CUIT	EMPRESA	CUIL	AFILIADO	PERIODO	REMUNE	OMINT

	private Date fechaLiq;
	private Date fechaObligacion;
	private String numeroRecibo;
	private String numeroActa;
	private String cuit;
	private String razonSoc;
	private String cuil;
	private String afiliado;
	private Date periodo;
	private BigDecimal remunera;
	private BigDecimal totalTerce;
	private String tercerizadora;
	
	
	
	public static LiquidacionActaConvenio getMapping(ResultSet rs) throws SQLException {
		LiquidacionActaConvenio liquidacion = new LiquidacionActaConvenio();
		liquidacion.setFechaLiq(rs.getDate("fecha_liq"));
		liquidacion.setFechaObligacion(rs.getDate("fecha_obligacion"));
		liquidacion.setNumeroRecibo(rs.getString("numero_recibo"));
		liquidacion.setNumeroActa(rs.getString("acta"));
		liquidacion.setCuit(rs.getString("cuit"));
		liquidacion.setRazonSoc(rs.getString("razon_soc"));
		liquidacion.setCuil(rs.getString("cuil"));
		liquidacion.setAfiliado(rs.getString("afiliado"));
		liquidacion.setPeriodo(rs.getDate("periodo"));		
		liquidacion.setRemunera(rs.getBigDecimal("remuneracion"));
		liquidacion.setTotalTerce(rs.getBigDecimal("importe"));		
		liquidacion.setTercerizadora(rs.getString("tercerizadora"));
		return liquidacion;
	}



	public Date getFechaLiq() {
		return fechaLiq;
	}

	public String getFechaLiqAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaLiq!=null?sdf.format(fechaLiq):"";
	}

	public void setFechaLiq(Date fechaLiq) {
		this.fechaLiq = fechaLiq;
	}



	public BigDecimal getTotalTerce() {
		return totalTerce;
	}
	
	public String getTotalTerceAsString() {
		DecimalFormat nf=new DecimalFormat("#,##0.00");
		return totalTerce!=null?nf.format(totalTerce):"0.00";
	}



	public void setTotalTerce(BigDecimal importeTotal) {
		this.totalTerce = importeTotal;
	}



	public Date getFechaObligacion() {
		return fechaObligacion;
	}



	public void setFechaObligacion(Date fechaObligacion) {
		this.fechaObligacion = fechaObligacion;
	}



	public String getNumeroRecibo() {
		return numeroRecibo;
	}



	public void setNumeroRecibo(String numeroRecibo) {
		this.numeroRecibo = numeroRecibo;
	}



	public String getNumeroActa() {
		return numeroActa;
	}



	public void setNumeroActa(String numeroActa) {
		this.numeroActa = numeroActa;
	}



	public String getCuit() {
		return cuit;
	}



	public void setCuit(String cuit) {
		this.cuit = cuit;
	}



	public String getRazonSoc() {
		return razonSoc;
	}



	public void setRazonSoc(String razonSoc) {
		this.razonSoc = razonSoc;
	}



	public String getCuil() {
		return cuil;
	}



	public void setCuil(String cuil) {
		this.cuil = cuil;
	}



	public String getAfiliado() {
		return afiliado;
	}



	public void setAfiliado(String afiliado) {
		this.afiliado = afiliado;
	}



	public Date getPeriodo() {
		return periodo;
	}

	public String getPeriodoAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return periodo!=null?sdf.format(periodo):"";
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}



	public BigDecimal getRemunera() {
		return remunera;
	}



	public void setRemunera(BigDecimal remunera) {
		this.remunera = remunera;
	}



	public String getTercerizadora() {
		return tercerizadora;
	}



	public void setTercerizadora(String tercerizadora) {
		this.tercerizadora = tercerizadora;
	}
	
	
}
