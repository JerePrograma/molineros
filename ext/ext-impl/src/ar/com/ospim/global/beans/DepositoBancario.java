package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.global.services.ReciboGlobalServiceImpl;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;

public class DepositoBancario extends Ingreso {
	public static final int ID_TIPO_DEPOSITO = 1;
	public static final int ID_TIPO_TRANSFERENCIA = 2;
	
	private Date fecha;
	private BigDecimal importe;
	private String numero;
	private CuentaBancaria ctaBcria;
	private int tipoDeposito;
	private int sucuNacion;
	transient private String cuit;
	transient private Convenio convenio;

	public DepositoBancario() {
	}

	public DepositoBancario(Convenio convenio) {
		this.convenio = convenio;
	}

	public DepositoBancario(BigDecimal importe, Date fecha) {
		this.importe = importe;
		this.fecha = fecha;
	}

	public DepositoBancario(String nro, CuentaBancaria ctaBcria) {
		this.numero = nro;
		this.setCuentaBancaria(ctaBcria);
	}
	
	public DepositoBancario(String nro, int sucu_nacion, CuentaBancaria ctaBcria) {
		this.numero = nro;
		this.sucuNacion=sucu_nacion;
		this.setCuentaBancaria(ctaBcria);
	}

	public Date getFecha() {
		return fecha;
	}

	public String getFechaAsString() {
		return null != fecha ? DateUtils.format(fecha, DateUtils.SHORT) : "";
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public String getFechaPagoAsString() {
		return null != fecha ? DateUtils.format(fecha, DateUtils.SHORT) : "";
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNumero() {
		return numero;
	}

	public String getNumeroStr() {
		return numero != null ? numero.toString() : "";
	}

	public int getSucuNacion() {
		return sucuNacion;
	}

	public void setSucuNacion(int sucuNacion) {
		this.sucuNacion = sucuNacion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((getCuentaBancaria() == null) ? 0 : getCuentaBancaria()
						.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
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
		DepositoBancario other = (DepositoBancario) obj;
		if (getCuentaBancaria() == null) {
			if (other.getCuentaBancaria() != null)
				return false;
		} else if (!getCuentaBancaria().equals(other.getCuentaBancaria()))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		return true;
	}

	public void setConvenio(Convenio convenio) {
		this.convenio = convenio;
	}

	public Convenio getConvenio() {
		return convenio;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	public boolean isNew() {
		return alta_fecha == null;
	}

	public int saveIngreso(ReciboGlobalServiceImpl instance, Recibo recibo,
			String user, Connection con, int entidad) throws SystemException,
			DuplicateNumeroChequeException {
		return instance.save(this, recibo, user, con, entidad);
	}

	public static DepositoBancario getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static DepositoBancario getMapping(ResultSet rs, String prefix)
			throws SQLException {
		DepositoBancario depo = new DepositoBancario();
		depo.setImporte(rs.getBigDecimal(prefix + "importe"));
		depo.setFecha(rs.getDate(prefix + "fecha"));
		depo.setCuentaBancaria(new CuentaBancaria(rs.getInt(prefix
				+ "id_cuenta_bcria_destino_deposito")));
		depo.setNumero(rs.getString(prefix + "numero"));
		depo.setAlta_usr(rs.getString(prefix + "alta_usr"));
		depo.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		depo.setModi_usr(rs.getString(prefix + "modi_usr"));
		depo.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		depo.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		depo.setBaja_usr(rs.getString(prefix + "baja_usr"));
		return depo;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuentaBancaria(CuentaBancaria ctaBcria) {
		this.ctaBcria = ctaBcria;
	}

	public CuentaBancaria getCuentaBancaria() {
		return ctaBcria;
	}

	@Override
	public Banco getBanco() {
		if (ctaBcria != null && ctaBcria.getBanco() != null) {
			return ctaBcria.getBanco();
		}
		return null;
	}
	
	public String getTipo() {
		if (tipoDeposito == ID_TIPO_TRANSFERENCIA){
			return "Transferencia Bancaria";
		}
		return "Deposito Bancario";
	}

	public void setTipoDeposito(int tipoDeposito) {
		this.tipoDeposito = tipoDeposito;
	}

	public int getTipoDeposito() {
		return tipoDeposito;
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
