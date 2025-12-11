package ar.com.ospim.tesoreria.beans.canje;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.util.DateUtils;

public class CanjeChequePropio implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -424845315669396873L;
	private int id;
	private List<Cheque> chequesNuevos;
	private List<ChequeACanjear> chequesViejos;
	private OrdenPago ordenPago;
	private OrdenPago ordenPagoNueva;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private int idMovimientoBancario;

	public void setChequesNuevos(List<Cheque> chequesNuevos) {
		this.chequesNuevos = chequesNuevos;
	}

	public List<Cheque> getChequesNuevos() {
		return chequesNuevos;
	}

	public List<ChequeACanjear> getChequesViejos() {
		return chequesViejos;
	}

	public void setChequesViejos(List<ChequeACanjear> chequesViejos) {
		this.chequesViejos = chequesViejos;
	}

	public OrdenPago getOrdenPago() {
		return ordenPago;
	}

	public void setOrdenPago(OrdenPago ordenPago) {
		this.ordenPago= ordenPago;
	}

	public boolean validarTotales() {
		BigDecimal nuevos = BigDecimal.ZERO;
		if (chequesNuevos != null) {
			for (Cheque cheque : chequesNuevos) {
				nuevos = nuevos.add(cheque.getImporte());
			}

		}
		BigDecimal viejos = BigDecimal.ZERO;
		if (chequesViejos != null) {
			for (ChequeACanjear cheque : chequesViejos) {
				if (cheque.isCanjeado()) {
					viejos = viejos.add(cheque.getCheque().getImporte());
				}
			}

		}
		return nuevos.compareTo(viejos) == 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static class ChequeACanjear {
		private Cheque cheque;
		private boolean canjeado;

		public ChequeACanjear(Cheque cheque) {
			this.cheque = cheque;
			this.canjeado = false;
		}

		public Cheque getCheque() {
			return cheque;
		}

		public void setCheque(Cheque cheque) {
			this.cheque = cheque;
		}

		public boolean isCanjeado() {
			return canjeado;
		}

		public void setCanjeado(boolean canjeado) {
			this.canjeado = canjeado;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((cheque == null) ? 0 : cheque.hashCode());
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
			ChequeACanjear other = (ChequeACanjear) obj;
			if (cheque == null) {
				if (other.cheque != null)
					return false;
			} else if (!cheque.equals(other.cheque))
				return false;
			return true;
		}

	}

	public static CanjeChequePropio getMapping(ResultSet rs)
			throws SQLException {
		return getMapping(rs, "");
	}

	public static CanjeChequePropio getMapping(ResultSet rs, String prefix)
			throws SQLException {
		CanjeChequePropio canje = new CanjeChequePropio();

		canje.setId(rs.getInt(prefix + "id"));
		canje.setOrdenPago(new OrdenPagoOspim(rs.getInt(prefix
				+ "id_orden_pago_ospim")));
		canje.setOrdenPagoNueva(new OrdenPagoOspim(rs.getInt(prefix
				+ "id_orden_pago_ospim_nueva")));
		canje.setIdMovimientoBancario(rs.getInt(prefix
				+ "id_movimiento"));
		canje.setAlta_usr(rs.getString(prefix + "alta_usr"));
		canje.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		canje.setModi_usr(rs.getString(prefix + "modi_usr"));
		canje.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		canje.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		canje.setBaja_usr(rs.getString(prefix + "baja_usr"));
		return canje;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public String getAlta_fechaAsString() {
		return null != alta_fecha ? DateUtils.format(alta_fecha,
				DateUtils.SHORT) : "";
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

	public BigDecimal getImporteCanjeado() {
		BigDecimal total = BigDecimal.ZERO;
		for (Cheque cheque : chequesNuevos) {
			total = total.add(cheque.getImporte());
		}
		return total;
	}

	public OrdenPago getOrdenPagoNueva() {
		return ordenPagoNueva;
	}

	public void setOrdenPagoNueva(OrdenPago ordenPagoOspimNueva) {
		this.ordenPagoNueva = ordenPagoOspimNueva;
	}

	public int getIdMovimientoBancario() {
		return idMovimientoBancario;
	}

	public void setIdMovimientoBancario(int idMovimientoBancario) {
		this.idMovimientoBancario = idMovimientoBancario;
	}
}
