package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Concepto;

public class ReciboOtroConcepto extends ReciboConcepto {
	private Concepto concepto;
	private BigDecimal importe;
	private Cheque cheque;
	private BigDecimal remuneracionTotal;
	private Integer cantidadEmpleados;
	private Date periodo;
    private Integer boletaNro;
    private Integer nroSecuenciaDDJJ;
    private BigDecimal totalBoleta;
	public ReciboOtroConcepto() {
	}

	public ReciboOtroConcepto(int id) {
		this.id = id;
	}

	public ReciboOtroConcepto(Concepto conceptoCaja, BigDecimal importe) {
		this.concepto = conceptoCaja;
		this.importe = importe;
	}
	
	public ReciboOtroConcepto(Concepto conceptoCaja, BigDecimal importe, BigDecimal remunTotal, Integer cantEmple, Date periodo) {
		this.concepto = conceptoCaja;
		this.importe = importe;
		this.remuneracionTotal=remunTotal;
		this.cantidadEmpleados = cantEmple;
		this.periodo = periodo;
	}

	public ReciboOtroConcepto(Concepto conceptoCaja, BigDecimal importe, BigDecimal remunTotal, Integer cantEmple, Date periodo,Integer nroBoleta,Integer nroSecuenciaDDJJ,BigDecimal totalBoleta) {
		this.concepto = conceptoCaja;
		this.importe = importe;
		this.remuneracionTotal=remunTotal;
		this.cantidadEmpleados = cantEmple;
		this.periodo = periodo;
		this.boletaNro=nroBoleta;
		this.nroSecuenciaDDJJ=nroSecuenciaDDJJ;
		this.totalBoleta=totalBoleta;
	}
	
	public Concepto getConcepto() {
		return concepto;
	}

	public void setConcepto(Concepto concepto) {
		this.concepto = concepto;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReciboOtroConcepto other = (ReciboOtroConcepto) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public static ReciboOtroConcepto getMapping(ResultSet rs)
			throws SQLException {
		return getMapping(rs, "");
	}

	public static ReciboOtroConcepto getMapping(ResultSet rs, String prefix)
			throws SQLException {
		ReciboOtroConcepto oc = new ReciboOtroConcepto();
		oc.setConcepto(new Concepto(rs.getInt(prefix + "concepto_id")));
		oc.setImporte(rs.getBigDecimal(prefix + "importe"));
		oc.setAlta_usr(rs.getString(prefix + "alta_usr"));
		oc.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		oc.setModi_usr(rs.getString(prefix + "modi_usr"));
		oc.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		oc.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		oc.setBaja_usr(rs.getString(prefix + "baja_usr"));
		//TODO _Sva mapear nuevos campos con los nuevos atributos
		return oc;
	}

	public void setCheque(Cheque cheque) {
		this.cheque = cheque;
	}

	public Cheque getCheque() {
		return cheque;
	}

	@Override
	public BigDecimal getTotalAPagar() {
		return getImporte();
	}

	@Override
	public Date getFechaAPagar() {
		return new Date();
	}

	@Override
	public String getDescripcion() {
		return concepto.getDescripcion();
	}

	public BigDecimal getRemuneracionTotal() {
		return remuneracionTotal;
	}

	public void setRemuneracionTotal(BigDecimal remuneracionTotal) {
		this.remuneracionTotal = remuneracionTotal;
	}

	public Integer getCantidadEmpleados() {
		return cantidadEmpleados;
	}

	public void setCantidadEmpleados(Integer cantidadEmpleados) {
		this.cantidadEmpleados = cantidadEmpleados;
	}

	public Date getPeriodo() {
		return periodo;
	}
	
	public String getPeriodoAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("MMMMM/yyyy");
		return periodo!=null?sdf.format(periodo):"";
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	
	
	public BigDecimal getTotalAPagarNoOS() {		
		return getImporte();
	}

	public Integer getBoletaNro() {
		return boletaNro;
	}

	public void setBoletaNro(Integer boletaNro) {
		this.boletaNro = boletaNro;
	}

	public Integer getNroSecuenciaDDJJ() {
		return nroSecuenciaDDJJ;
	}

	public void setNroSecuenciaDDJJ(Integer nroSecuenciaDDJJ) {
		this.nroSecuenciaDDJJ = nroSecuenciaDDJJ;
	}

	public BigDecimal getTotalBoleta() {
		return totalBoleta;
	}

	public void setTotalBoleta(BigDecimal totalBoleta) {
		this.totalBoleta = totalBoleta;
	}
	
	
}
