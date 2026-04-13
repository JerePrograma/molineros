package ar.com.ospim.global.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.portlet.PortletRequest;

import ar.com.ospim.global.services.TraeListasServiceUtil;

public class Banco {
	private int id_banco;
	private String descripcion;

	public Banco() {
	}

	public Banco(int id) {
		this.id_banco = id;
	}

	public Banco(int id, String desc) {
		this.id_banco = id;
		this.descripcion = desc;
	}

	public int getId_banco() {
		return id_banco;
	}

	public void setId_banco(int idBanco) {
		id_banco = idBanco;
	}

	public String getDescripcion_banco() {
		return descripcion;
	}

	public String getDescripcion_banco(PortletRequest portletRequest)
			throws Exception {
		if (descripcion == null && id_banco != 0) {
			List<Banco> bancos = TraeListasServiceUtil
					.getBancos(portletRequest);
			for (Banco banco : bancos) {
				if (banco.getId_banco() == id_banco) {
					descripcion = banco.getDescripcion_banco();
				}
			}

		}
		return descripcion;
	}

	public void setDescripcion(String descripcionBanco) {
		descripcion = descripcionBanco;
	}

	public static Banco getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_banco;
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
		Banco other = (Banco) obj;
		if (id_banco != other.id_banco)
			return false;
		return true;
	}

	public static Banco getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Banco banco = new Banco();
		banco.setDescripcion(rs.getString(prefix + "descripcion"));
		banco.setId_banco(rs.getInt(prefix + "id_banco"));
		return banco;
	}

}
