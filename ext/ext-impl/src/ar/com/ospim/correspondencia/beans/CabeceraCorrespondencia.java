package ar.com.ospim.correspondencia.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.model.Organization;
import com.liferay.portal.service.OrganizationLocalServiceUtil;

public class CabeceraCorrespondencia implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 755483494761055852L;
	
	private long id_correspondencia;
	private String lugarRecepEmision;
	private String lugarDescription;
	private Date fecha;
	private String tipoRegistro;
	private String tipoEnvio;
	private String oblea;
	
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	
	private ArrayList<ItemCorrespondencia> itemsCorrespondencia;

	public String getFechaAsString() {
		return null!=fecha?DateUtils.format(fecha,DateUtils.SHORT):"";
	}
	
	public long getId_correspondencia() {
		return id_correspondencia;
	}

	public void setId_correspondencia(long id_correspondencia) {
		this.id_correspondencia = id_correspondencia;
	}

	public String getLugarRecepEmision() {
		return lugarRecepEmision;
	}
	
	public String getLugar() {
		return lugarRecepEmision;
	}
	

	public void setLugarRecepEmision(String lugarRecepEmision) {
		this.lugarRecepEmision = lugarRecepEmision;
	}

	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public String getTipoEnvio() {
		return tipoEnvio;
	}

	public void setTipoEnvio(String tipoEnvio) {
		this.tipoEnvio = tipoEnvio;
	}

	public String getOblea() {
		return oblea;
	}

	public void setOblea(String oblea) {
		this.oblea = oblea;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date alta_fecha) {
		this.alta_fecha = alta_fecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String alta_usr) {
		this.alta_usr = alta_usr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modi_fecha) {
		this.modi_fecha = modi_fecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modi_usr) {
		this.modi_usr = modi_usr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String baja_usr) {
		this.baja_usr = baja_usr;
	}

	public static CabeceraCorrespondencia getMapping(ResultSet rs, String prefix)
			throws SQLException, SystemException {

		HashMap<Long, String> empresaHM = new HashMap<Long,String>();
		List<Organization> empresas = OrganizationLocalServiceUtil.getOrganizations(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		for (Iterator<Organization> iterator = empresas.iterator(); iterator.hasNext();) {
			Organization org = iterator.next();
			empresaHM.put(org.getOrganizationId(), org.getName());
		}
	
		CabeceraCorrespondencia cab = new CabeceraCorrespondencia();
		cab.setId_correspondencia(rs.getLong(prefix + "id_correspondencia"));		
		cab.setLugarRecepEmision(rs.getString(prefix + "lugar"));  //_recep_emision
		cab.setLugarDescription( empresaHM.get( Long.parseLong(rs.getString(prefix + "lugar")) ));
		cab.setFecha(rs.getTimestamp(prefix + "fecha"));  //_recep_emision
		cab.setTipoRegistro(rs.getString(prefix + "tipo_registro"));
		cab.setTipoEnvio(rs.getString(prefix + "tipo_envio"));	
		cab.setOblea(rs.getString(prefix + "oblea"));
		cab.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		cab.setAlta_usr(rs.getString(prefix + "alta_usr"));
		cab.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		cab.setModi_usr(rs.getString(prefix + "modi_usr"));
		cab.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		cab.setBaja_usr(rs.getString(prefix + "baja_usr"));
	    
		return cab;
	}
	
	public String getLugarDescription() {
		return lugarDescription;
	}

	public void setLugarDescription(String lugarDescription) {
		this.lugarDescription = lugarDescription;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public ArrayList<ItemCorrespondencia> getItemsCorrespondencia() {
		return itemsCorrespondencia;
	}

	public void setItemsCorrespondencia(
			ArrayList<ItemCorrespondencia> itemsCorrespondencia) {
		this.itemsCorrespondencia = itemsCorrespondencia;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fecha == null) ? 0 : fecha.hashCode());
		result = prime * result
				+ (int) (id_correspondencia ^ (id_correspondencia >>> 32));
		result = prime
				* result
				+ ((lugarDescription == null) ? 0 : lugarDescription.hashCode());
		result = prime
				* result
				+ ((lugarRecepEmision == null) ? 0 : lugarRecepEmision
						.hashCode());
		result = prime * result + ((oblea == null) ? 0 : oblea.hashCode());
		result = prime * result
				+ ((tipoEnvio == null) ? 0 : tipoEnvio.hashCode());
		result = prime * result
				+ ((tipoRegistro == null) ? 0 : tipoRegistro.hashCode());
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
		CabeceraCorrespondencia other = (CabeceraCorrespondencia) obj;
		if (fecha == null) {
			if (other.fecha != null)
				return false;
		} else if (!fecha.equals(other.fecha))
			return false;
		if (id_correspondencia != other.id_correspondencia)
			return false;
		if (lugarDescription == null) {
			if (other.lugarDescription != null)
				return false;
		} else if (!lugarDescription.equals(other.lugarDescription))
			return false;
		if (lugarRecepEmision == null) {
			if (other.lugarRecepEmision != null)
				return false;
		} else if (!lugarRecepEmision.equals(other.lugarRecepEmision))
			return false;
		if (oblea == null) {
			if (other.oblea != null)
				return false;
		} else if (!oblea.equals(other.oblea))
			return false;
		if (tipoEnvio == null) {
			if (other.tipoEnvio != null)
				return false;
		} else if (!tipoEnvio.equals(other.tipoEnvio))
			return false;
		if (tipoRegistro == null) {
			if (other.tipoRegistro != null)
				return false;
		} else if (!tipoRegistro.equals(other.tipoRegistro))
			return false;
		return true;
	}
	
	
}
