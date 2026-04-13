package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.global.beans.Efectivo.Estado;
import ar.com.ospim.global.services.ReciboGlobalServiceImpl;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboConcepto;
import ar.com.ospim.tesoreria.beans.ReciboOtroConcepto;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;

public class ReciboAnticipo extends Ingreso {
	private ReciboConcepto anticipo;
	private Recibo recibo;
	private BigDecimal importe;
	private Estado estado;

	@Override
	public Banco getBanco() {
		return null;
	}

	@Override
	public CuentaBancaria getCuentaBancaria() {
		return null;
	}

	@Override
	public Date getFecha() {
		return recibo.getFecha();
	}

	@Override
	public String getFechaAsString() {
		return null != recibo.getFecha() ? DateUtils.format(recibo.getFecha(),
				DateUtils.SHORT) : "";
	}

	@Override
	public String getNumeroStr() {
		return recibo.getNumero();
	}

	@Override
	public String getTipo() {
		return "Anticipo";
	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public void setCuit(String cuit) {

	}

	@Override
	public int saveIngreso(ReciboGlobalServiceImpl instance, Recibo recibo,
			String user, Connection con, int entidad) throws SystemException,
			DuplicateNumeroChequeException {
		return instance.save(this, recibo, user, con, entidad);
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	@Override
	public BigDecimal getImporte() {
		return importe;
	}

	public void setRecibo(Recibo recibo) {
		this.recibo = recibo;
	}

	public Recibo getReciboAnticipo() {
		return recibo;
	}

	public void setAnticipo(ReciboConcepto anticipo) {
		this.anticipo = anticipo;
	}

	public ReciboConcepto getAnticipo() {
		return anticipo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + ((anticipo == null) ? 0 : anticipo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		ReciboAnticipo other = (ReciboAnticipo) obj;
		if (anticipo == null) {
			if (other.anticipo != null)
				return false;
		} else if (!anticipo.equals(other.anticipo))
			return false;
		return true;
	}

	public static ReciboAnticipo getMapping(ResultSet rs, String prefix)
			throws SQLException {
		ReciboAnticipo ra = new ReciboAnticipo();
		ReciboConcepto reciboConcepto = new ReciboOtroConcepto(rs.getInt(prefix
				+ "id"));
		ra.setAnticipo(reciboConcepto);
		Recibo recibo = new Recibo(rs.getInt(prefix + "recibo_id"));
		recibo.setNumero(rs.getString(prefix + "numero"));
		recibo.setFecha(rs.getDate(prefix + "recibo_fecha"));
		ra.setRecibo(recibo);
		BigDecimal importe = rs.getBigDecimal(prefix + "importe");
		ra.setImporte(importe);
		return ra;
	}

	public static ReciboAnticipo getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Estado getEstado() {
		return estado;
	}

	@Override
	public Integer getEmisor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEmisor(Integer emisor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCuotas() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCuotas(int cuotas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getEmisorDescripcion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEmisorDescripcion(String emisorDescripcion) {
		// TODO Auto-generated method stub
		
	}
}
