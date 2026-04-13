package ar.com.ospim.afiliados.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.util.DateUtils;

/**
 * @author Administrador
 * @version 1.0
 * @created 29-Jul-2010 11:34:23 a.m.
 */
// TODO: esto es AfiPlan!!!!! (tabla afi_plan)
public class AfiAporteList implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<Integer, AfiAporte> listaAportes;
	private Plan plan;
	private Date fechaAlta;

	public AfiAporteList() {
	}

	public Map<Integer, AfiAporte> getMapAportes() {
		return listaAportes;
	}

	public void setMapAportes(Map<Integer, AfiAporte> listaAportes) {
		this.listaAportes = listaAportes;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	public Date getFechaAlta() {
		return fechaAlta;
	}

	public String getFechaAltaAsString() {
		return fechaAlta != null ? DateUtils.format(fechaAlta, "dd/MM/yyyy")
				: "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fechaAlta == null) ? 0 : fechaAlta.hashCode());
		result = prime * result + ((plan == null) ? 0 : plan.hashCode());
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
		AfiAporteList other = (AfiAporteList) obj;
		if (fechaAlta == null) {
			if (other.fechaAlta != null)
				return false;
		} else if (!fechaAlta.equals(other.fechaAlta))
			return false;
		if (plan == null) {
			if (other.plan != null)
				return false;
		} else if (!plan.equals(other.plan))
			return false;
		return true;
	}

	public Map<Integer, AfiAporte> getListaAportes() {
		return listaAportes;
	}

	public void setListaAportes(Map<Integer, AfiAporte> listaAportes) {
		this.listaAportes = listaAportes;
	}
}