package ar.com.ospim.portalempleadores.reportes;

import java.math.BigDecimal;
import java.util.Date;

public class CuentaCorrientePortalEmpleadores implements
		Comparable<CuentaCorrientePortalEmpleadores> {
	private String razon_soc;
	private Date fecha;
	private Date periodo;
	private String tipo;
	private String descripcion;
	private BigDecimal debe;
	private BigDecimal haber;
	private String cuit;
	private int nroDeclaracion;
	private String entidad;

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public BigDecimal getDebe() {
		return debe;
	}

	public void setDebe(BigDecimal debe) {
		this.debe = debe;
	}

	public BigDecimal getHaber() {
		return haber;
	}

	public void setHaber(BigDecimal haber) {
		this.haber = haber;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public String getTipo() {
		if (tipo == null) {
			return "";
		}
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRazon_soc() {
		return razon_soc;
	}

	public void setRazon_soc(String razon_soc) {
		this.razon_soc = razon_soc;
	}

	public int getNroDeclaracion() {
		return nroDeclaracion;
	}

	public void setNroDeclaracion(int nroDeclaracion) {
		this.nroDeclaracion = nroDeclaracion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + nroDeclaracion;
		result = prime * result + ((periodo == null) ? 0 : periodo.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
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
		CuentaCorrientePortalEmpleadores other = (CuentaCorrientePortalEmpleadores) obj;
		if (nroDeclaracion != other.nroDeclaracion)
			return false;
		if (periodo == null) {
			if (other.periodo != null)
				return false;
		} else if (!periodo.equals(other.periodo))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		return true;
	}

	public int compareTo(CuentaCorrientePortalEmpleadores arg1) {
		int compararCuit = getCuit().compareTo(arg1.getCuit());
		if (compararCuit != 0) {
			return compararCuit;
		} else {
			int compararPeri = getPeriodo().compareTo(arg1.getPeriodo());
			if (compararPeri != 0) {
				return compararPeri;
			} else {
				int compararTipo = getTipo().compareTo(arg1.getTipo());
				if (compararTipo != 0) {
					return compararTipo;
				} else {
					if (getTipo().equals("")) {
						int compEntidad = getEntidad().compareTo(
								arg1.getEntidad());
						if (compEntidad == 0) {
							return getFecha().compareTo(arg1.getFecha());
						} else {
							return compEntidad;
						}
					} else {
						return getFecha().compareTo(arg1.getFecha());
					}
				}
			}
		}
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

}