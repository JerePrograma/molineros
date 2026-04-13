package ar.com.ospim.farmacia.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.util.DateUtils;

/**
 * @author sistema-01
 * @version 1.0
 * @created 25-Ago-2010 02:25:46 p.m.
 */
public class ReintegroMedicamento {

	protected int id_reintegro;
	protected Afiliado afiliado;
	protected String cuil_titular;
	protected int inte;
	protected Seccional seccional;
	protected Date fecha;
	protected Date periodo;
	protected int id_seccional;
	protected Date alta_fecha;
	protected String alta_usr;
	protected Date modi_fecha;
	protected String modi_usr;
	protected Date baja_fecha;
	protected String baja_usr;
	protected List<ReintegroMedicamentoItem> medicamentos;
	protected BigDecimal importeTotal;
	protected BigDecimal precioPublicoTotal;
	protected int idOP;
	protected BigInteger chequeOP;
	protected Date fechaOP;
	protected Date bajaFechaOP;
	protected boolean lidadoOP;
	

	protected String observaciones;
	
	protected boolean transferenciaBancaria;
	protected String cbu;
	protected String cuilCuenta;
	protected String emailCuenta;
	protected String apellidoCuenta;
	protected String nombreCuenta;
	
	protected int id_lista_reintegro;
	
	public ReintegroMedicamento() {
	}

	public ReintegroMedicamento(int id_reintegro, BigDecimal importe) {
		this.id_reintegro = id_reintegro;
		this.importeTotal = importe;
	}

	public int getId_reintegro() {
		return id_reintegro;
	}

	public String getId_reintegroString() {
		return String.valueOf(id_reintegro);
	}

	/**
	 * @param idReintegro
	 *            the id_reintegro to set
	 */
	public void setId_reintegro(int idReintegro) {
		id_reintegro = idReintegro;
	}

	/**
	 * @return the fecha
	 */
	public Date getFecha() {
		return fecha;
	}

	public String getFechaAsString() {
		return null != fecha ? DateUtils.format(fecha, DateUtils.SHORT) : "";
	}

	/**
	 * @param fecha
	 *            the fecha to set
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return the periodo
	 */
	public Date getPeriodo() {
		return periodo;
	}

	/**
	 * @return the periodo
	 */
	public String getPeriodoString() {
		return null != periodo ? DateUtils.format(periodo, DateUtils.PERIODO)
				: "";
	}

	/**
	 * @param periodo
	 *            the periodo to set
	 */
	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	/**
	 * @return the id_seccional
	 */
	public int getId_seccional() {
		return id_seccional;
	}

	/**
	 * @param idSeccional
	 *            the id_seccional to set
	 */
	public void setId_seccional(int idSeccional) {
		id_seccional = idSeccional;
	}

	/**
	 * @return the alta_fecha
	 */
	public Date getAlta_fecha() {
		return alta_fecha;
	}

	/**
	 * @param altaFecha
	 *            the alta_fecha to set
	 */
	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	/**
	 * @return the alta_usr
	 */
	public String getAlta_usr() {
		return alta_usr;
	}

	/**
	 * @param altaUsr
	 *            the alta_usr to set
	 */
	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	/**
	 * @return the modi_fecha
	 */
	public Date getModi_fecha() {
		return modi_fecha;
	}

	/**
	 * @param modiFecha
	 *            the modi_fecha to set
	 */
	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	/**
	 * @return the modi_usr
	 */
	public String getModi_usr() {
		return modi_usr;
	}

	/**
	 * @param modiUsr
	 *            the modi_usr to set
	 */
	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	/**
	 * @return the baja_fecha
	 */
	public Date getBaja_fecha() {
		return baja_fecha;
	}

	/**
	 * @param bajaFecha
	 *            the baja_fecha to set
	 */
	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	/**
	 * @return the baja_usr
	 */
	public String getBaja_usr() {
		return baja_usr;
	}

	/**
	 * @param bajaUsr
	 *            the baja_usr to set
	 */
	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	/**
	 * @return the afiliado
	 */
	public Afiliado getAfiliado() {
		return afiliado;
	}

	/**
	 * @param afiliado
	 *            the afiliado to set
	 */
	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_reintegro;
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
		ReintegroMedicamento other = (ReintegroMedicamento) obj;
		if (id_reintegro != other.id_reintegro)
			return false;
		return true;
	}

	/**
	 * @return the seccional
	 */
	public Seccional getSeccional() {
		return seccional;
	}

	/**
	 * @param seccional
	 *            the seccional to set
	 */
	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}

	public BigDecimal getImporteTotal() {
		if (importeTotal == null) {
			BigDecimal total = new BigDecimal(0);
			if (medicamentos != null) {
				for (ReintegroMedicamentoItem rPrest : medicamentos) {
					if (rPrest.isDelete() || rPrest.getBaja_fecha() != null) {
						continue;
					}
					total = total
							.add(rPrest != null
									&& rPrest.getImporteCoberturaAmtima() != null ? (rPrest
									.getImporteCoberturaAmtima().add(rPrest
									.getImporteCoberturaOspim()).add(rPrest.getImporteCoberturaPrestadora().add(
											rPrest.getImporteCoberturaImesa()!=null? rPrest.getImporteCoberturaImesa():BigDecimal.ZERO))
									): new BigDecimal(0));
				}
			}
			setImporteTotal(total);
		}
		return importeTotal;
	}

	public BigDecimal getPrecioPublicoTotal() {
		if (precioPublicoTotal == null) {
			BigDecimal total = new BigDecimal(0);
			if (medicamentos != null) {
				for (ReintegroMedicamentoItem rPrest : medicamentos) {
					if (rPrest.isDelete() || rPrest.getBaja_fecha() != null) {
						continue;
					}
					total = total.add(rPrest != null
							&& rPrest.getPrecio_al_publico() != null ? (rPrest
							.getPrecio_al_publico()).multiply(new BigDecimal(
							rPrest.getCantidad())) : new BigDecimal(0));
				}
			}
			setPrecioPublicoTotal(total);
		}
		return precioPublicoTotal;
	}

	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}

	public List<ReintegroMedicamentoItem> getMedicamentos() {
		return medicamentos;
	}

	public void setMedicamentos(List<ReintegroMedicamentoItem> medicamentos) {
		this.medicamentos = medicamentos;
	}

	/**
	 * @return the idOP
	 */
	public int getIdOP() {
		return idOP;
	}

	/**
	 * @param idOP
	 *            the idOP to set
	 */
	public void setIdOP(int idOP) {
		this.idOP = idOP;
	}

	/**
	 * @return the chequeOP
	 */
	public BigInteger getChequeOP() {
		return chequeOP;
	}

	/**
	 * @param chequeOP
	 *            the chequeOP to set
	 */
	public void setChequeOP(BigInteger chequeOP) {
		this.chequeOP = chequeOP;
	}

	/**
	 * @return the fechaOP
	 */
	public Date getFechaOP() {
		return fechaOP;
	}

	/**
	 * @param fechaOP
	 *            the fechaOP to set
	 */
	public void setFechaOP(Date fechaOP) {
		this.fechaOP = fechaOP;
	}

	/**
	 * @return the observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}

	/**
	 * @param observaciones
	 *            the observaciones to set
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	/**
	 * @return the cuil_titular
	 */
	public String getCuil_titular() {
		return cuil_titular;
	}

	/**
	 * @param cuilTitular
	 *            the cuil_titular to set
	 */
	public void setCuil_titular(String cuilTitular) {
		cuil_titular = cuilTitular;
	}

	/**
	 * @return the inte
	 */
	public int getInte() {
		return inte;
	}

	/**
	 * @param inte
	 *            the inte to set
	 */
	public void setInte(int inte) {
		this.inte = inte;
	}

	public static ReintegroMedicamento getMapping(ResultSet rs)
			throws SQLException {
		return getMapping(rs, "");
	}

	/**
	 * @param precioPublicoTotal
	 *            the precioPublicoTotal to set
	 */
	public void setPrecioPublicoTotal(BigDecimal precioPublicoTotal) {
		this.precioPublicoTotal = precioPublicoTotal;
	}

	public int getId_lista_reintegro() {
		return id_lista_reintegro;
	}

	public void setId_lista_reintegro(int idListaReintegro) {
		id_lista_reintegro = idListaReintegro;
	}

	
	public String getOPReintegro() {
		StringBuffer sb = new StringBuffer("");
		sb.append(this.id_lista_reintegro > 0 ? this.id_lista_reintegro + " / " : "" );
		sb.append(this.idOP > 0 ? this.idOP + " / " : "");
		sb
				.append(this.chequeOP != null && this.chequeOP.intValue() != 0 ? this.chequeOP
						.toString()
						+ " / "
						: "");
		sb.append(this.fechaOP != null ? this.fechaOP.toString() : "");
		return sb.toString();
	}

	public boolean isTransferenciaBancaria() {
		return transferenciaBancaria;
	}

	public void setTransferenciaBancaria(boolean transferenciaBancaria) {
		this.transferenciaBancaria = transferenciaBancaria;
	}

	public String getCbu() {
		return cbu;
	}

	public void setCbu(String cbu) {
		this.cbu = cbu;
	}

	public String getCuilCuenta() {
		return cuilCuenta;
	}

	public void setCuilCuenta(String cuilCuenta) {
		this.cuilCuenta = cuilCuenta;
	}

	public String getEmailCuenta() {
		return emailCuenta;
	}

	public void setEmailCuenta(String emailCuenta) {
		this.emailCuenta = emailCuenta;
	}

	public String getApellidoCuenta() {
		return apellidoCuenta;
	}

	public void setApellidoCuenta(String apellidoCuenta) {
		this.apellidoCuenta = apellidoCuenta;
	}

	public String getNombreCuenta() {
		return nombreCuenta;
	}

	public void setNombreCuenta(String nombreCuenta) {
		this.nombreCuenta = nombreCuenta;
	}
	
	
	public Date getBajaFechaOP() {
		return bajaFechaOP;
	}

	public void setBajaFechaOP(Date bajaFechaOP) {
		this.bajaFechaOP = bajaFechaOP;
	}
	
	public boolean isLidadoOP() {
		return lidadoOP;
	}

	public void setLidadoOP(boolean lidadoOP) {
		this.lidadoOP = lidadoOP;
	}

	// SETEAR AFUERA SECCIONAL Y AFILIADO, IMPORTE TOTAL, MEDICAMENTOS,
	// setear también INFO de la OP
	public static ReintegroMedicamento getMapping(ResultSet rs, String prefix)
			throws SQLException {
		ReintegroMedicamento reintegro = new ReintegroMedicamento();
		reintegro.setId_reintegro(rs.getInt(prefix + "id_reintegro"));
		reintegro.setId_seccional(rs.getInt(prefix + "id_seccional"));
		reintegro.setInte(rs.getInt(prefix + "inte"));
		reintegro.setCuil_titular(rs.getString(prefix + "cuil_titular"));
		reintegro.setFecha(rs.getDate(prefix + "fecha"));
		reintegro.setPeriodo(rs.getDate(prefix + "periodo"));
		reintegro.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		reintegro.setAlta_usr(rs.getString(prefix + "alta_usr"));
		reintegro.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		reintegro.setModi_usr(rs.getString(prefix + "modi_usr"));
		reintegro.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		reintegro.setBaja_usr(rs.getString(prefix + "baja_usr"));
		reintegro.setObservaciones(rs.getString(prefix + "observacion"));
		
		try {
			reintegro.setIdOP(rs.getInt(prefix + "id_orden_pago"));
		}catch(Exception e) {
			
		}
		return reintegro;
	}

	
}