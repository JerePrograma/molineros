package ar.com.ospim.tesoreria.beans.convenio;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.util.DateUtils;

public class ConvenioPago {
	public enum Tipo {
		CUOTA("CUO"), PAGO("PGO");

		private String mapping;

		private Tipo(String mapping) {
			this.mapping = mapping;
		}

		public void setMapping(String mapping) {
			this.mapping = mapping;
		}

		public String getMapping() {
			return mapping;
		}
	};

	private int id;
	private int nroCuota;
	private Tipo tipo;
	private Date fechaPago;
	private BigDecimal importe;
	private BigDecimal interes;
	private Recibo recibo;
	private Convenio convenioCancelatorio;
	private Date alta_fecha;
	private String alta_usr;
	private String alta_ip;
	private Date modi_fecha;
	private String modi_usr;
	private String modi_ip;
	private Date baja_fecha;
	private String baja_usr;
	private String baja_ip;
	private Cheque cheque;
	private Pagare pagare;
	private boolean borradoLogico = false;
	private int convenioId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Date getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public Recibo getRecibo() {
		return recibo;
	}

	public void setRecibo(Recibo recibo) {
		this.recibo = recibo;
	}

	public Convenio getConvenioCancelatorio() {
		return convenioCancelatorio;
	}

	public void setConvenioCancelatorio(Convenio convenioCancelatorio) {
		this.convenioCancelatorio = convenioCancelatorio;
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

	public String getAlta_ip() {
		return alta_ip;
	}

	public void setAlta_ip(String altaIp) {
		alta_ip = altaIp;
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

	public String getModi_ip() {
		return modi_ip;
	}

	public void setModi_ip(String modiIp) {
		modi_ip = modiIp;
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

	public String getBaja_ip() {
		return baja_ip;
	}

	public void setBaja_ip(String bajaIp) {
		baja_ip = bajaIp;
	}

	public void setCheque(Cheque cheque) {
		this.cheque = cheque;
	}

	public Cheque getCheque() {
		return cheque;
	}

	public void setBorradoLogico(boolean borradoLogico) {
		this.borradoLogico = borradoLogico;
	}

	public boolean isBorradoLogico() {
		return borradoLogico;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ConvenioPago other = (ConvenioPago) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	public static ConvenioPago getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static ConvenioPago getMapping(ResultSet rs, String prefix)
			throws SQLException {
		ConvenioPago ap = new ConvenioPago();
		ap.setTipo(getTipo(rs, prefix));
		ap.setFechaPago(rs.getDate(prefix + "fecha_pago"));
		ap.setImporte(rs.getBigDecimal(prefix + "importe"));
		ap.setInteres(rs.getBigDecimal(prefix + "interes"));
		if (rs.getInt(prefix + "recibo_id") != 0){
			ap.setRecibo(new Recibo(rs.getInt(prefix + "recibo_id")));
		}		
		ap.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		ap.setAlta_usr(rs.getString(prefix + "alta_usr"));
		ap.setAlta_ip(rs.getString(prefix + "alta_ip"));
		ap.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		ap.setModi_usr(rs.getString(prefix + "modi_usr"));
		ap.setModi_ip(rs.getString(prefix + "modi_ip"));
		ap.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		ap.setBaja_usr(rs.getString(prefix + "baja_usr"));
		ap.setBaja_ip(rs.getString(prefix + "baja_ip"));
		ap.setId(rs.getInt(prefix + "id"));
		int convenioCancelatorioId = rs.getInt(prefix + "convenio_cancalatorio_id");
		if (convenioCancelatorioId != 0){
			ap.setConvenioCancelatorio(new Convenio(convenioCancelatorioId));
		}
		ap.setConvenioId(rs.getInt(prefix+"convenio_id"));
		ap.setNroCuota(rs.getInt(prefix + "cuota_id"));
		Cheque cheque = new Cheque();
		cheque.setNumero(rs.getBigDecimal(prefix + "nro_cheque"));
		cheque.setBanco(new Banco(rs.getInt(prefix + "banco_cheque"), ""));
		if (cheque.getNumero() != null) {
			ap.setCheque(cheque);
		}
		return ap;
	}

	private static Tipo getTipo(ResultSet rs, String prefix)
			throws SQLException {
		String tipo = rs.getString(prefix + "tipo");
		if (tipo != null && tipo.equals("CUO")) {
			return Tipo.CUOTA;
		}
		return Tipo.PAGO;
	}

	public void setNroCuota(int nroCuota) {
		this.nroCuota = nroCuota;
	}

	public int getNroCuota() {
		return nroCuota;
	}

	public String getFechaPagoAsString() {
		return null != fechaPago ? DateUtils.format(fechaPago, DateUtils.SHORT)
				: "";
	}

	public void setInteres(BigDecimal interes) {
		this.interes = interes;
	}

	public BigDecimal getInteres() {
		return interes;
	}

	public Pagare getPagare() {
		return pagare;
	}

	public void setPagare(Pagare pagare) {
		this.pagare = pagare;
	}

	public int getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(int convenioId) {
		this.convenioId = convenioId;
	}
	
	
	
	
}
