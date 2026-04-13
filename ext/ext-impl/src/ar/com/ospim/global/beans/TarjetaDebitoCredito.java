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

public class TarjetaDebitoCredito extends Ingreso {
	public static final int ID_TIPO_DEBITO = 3;
	public static final int ID_TIPO_CREDITO = 4;
	
	private Date fecha;
	private BigDecimal importe;
	private String numero;
	private int cuotas;
	private Integer emisor;
	private String emisorDescripcion;
	
	//private CuentaBancaria ctaBcria;
	
	private Banco banco;
	private int tipo;
	
	
	//private int sucuNacion;
	transient private String cuit;
	//transient private Convenio convenio;

	public TarjetaDebitoCredito() {
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

	public int getCuotas() {
		return cuotas;
	}

	public void setCuotas(int cuotas) {
		this.cuotas = cuotas;
	}

	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}
	
	public Integer getEmisor() {
		return emisor;
	}

	public void setEmisor(Integer emisor) {
		this.emisor = emisor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((getBanco() == null) ? 0 : getBanco()
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
		TarjetaDebitoCredito other = (TarjetaDebitoCredito) obj;
		if (getBanco() == null) {
			if (other.getBanco() != null)
				return false;
		} else if (!getBanco().equals(other.getBanco()))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		
		if (tipo == 0) {
			if (other.tipo != 0)
				return false;
		} else if (tipo!=other.tipo)
			return false;
		
		return true;
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

	public static TarjetaDebitoCredito getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static TarjetaDebitoCredito getMapping(ResultSet rs, String prefix)
			throws SQLException {
		TarjetaDebitoCredito depo = new TarjetaDebitoCredito();
		depo.setImporte(rs.getBigDecimal(prefix + "importe"));
		depo.setFecha(rs.getDate(prefix + "fecha"));
		/*
		depo.setCuentaBancaria(new CuentaBancaria(rs.getInt(prefix
				+ "id_cuenta_bcria_destino_deposito")));
		*/		
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

	public String getTipo() {
		if (tipo == ID_TIPO_DEBITO){
			return "Tarjeta Débito";
		}
		if (tipo == ID_TIPO_CREDITO){
			return "Tarjeta Crédito";
		}
		return "";
	}
	
	public void setTipo(int tipo) {
		this.tipo=tipo;
	}

	@Override
	public CuentaBancaria getCuentaBancaria() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getEmisorDescripcion() {
		return emisorDescripcion;
	}

	public void setEmisorDescripcion(String emisorDescripcion) {
		this.emisorDescripcion = emisorDescripcion;
	}

	public TarjetaDebitoCredito( Integer emisor,Banco banco,Date fecha, String numero, int cuotas ,BigDecimal importe ) {
		super();
		this.fecha = fecha;
		this.importe = importe;
		this.numero = numero;
		this.cuotas = cuotas;
		this.emisor = emisor;
		this.banco = banco;
	}

	
}
