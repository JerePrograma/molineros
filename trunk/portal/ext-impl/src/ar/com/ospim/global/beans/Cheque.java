package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.afiliados.empleadores.DuplicateEmpresaIdException;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.ReciboGlobalServiceImpl;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;

public class Cheque extends Ingreso implements Serializable, Pago  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//private static Log _log = LogFactoryUtil.getLog(Cheque.class);
	public enum Tipo {
		DEBITO("D"), CREDITO("C");

		private String mapping;

		private Tipo(String mapping) {
			this.mapping = mapping;
		}

		public String toString() {
			return mapping;
		}
	};

	private String cuit;
	private String aNombreDe;
	private Date fecha;
	private BigDecimal importe;
	private BigDecimal numero;
	private boolean prestador = false;
	private String concepto;
	private CuentaBancaria cuentaBancaria;
	private Tipo debitoCredito;
	private Estado estado;
	private Banco banco;
	private int idOp;
	private String nroRecibo;
	private Date fechaRecibo;
	private int movBcrioId;

	public Cheque() {
	}

	@Deprecated
	public Cheque(BigDecimal nroCheque, int idBanco) {
		this.numero = nroCheque;
		this.banco = new Banco(idBanco, "");
	}
	
	

	public Cheque(String cuit, BigDecimal numero, CuentaBancaria cuentaBancaria, Banco banco) {
		super();
		this.cuit = cuit;
		this.numero = numero;
		this.cuentaBancaria = cuentaBancaria;
		this.banco = banco;
	}

	public Cheque(Cheque chequeP) {
		this.setNumero(chequeP.getNumero());
		this.setImporte(chequeP.getImporte());
		this.setFecha(chequeP.getFecha());
		this.setPrestador(false);
		this.setBanco(chequeP.getBanco());
		this.setDebitoCredito(chequeP.getDebitoCredito());
		this.setEstado(chequeP.getEstado());
		this.setCuentaBancaria(chequeP.getCuentaBancaria());
		this.setCuit(chequeP.getCuit());
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getANombreDe() {
		return aNombreDe;
	}

	public void setANombreDe(String aNombreDe) {
		this.aNombreDe = aNombreDe;
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

	public BigDecimal getNumero() {
		return numero;
	}

	public String getNumeroStr() {
		return numero != null ? numero.toString() : "";
	}

	public void setNumero(BigDecimal numero) {
		this.numero = numero;
	}

	public static Cheque getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static Cheque getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Banco b = new Banco(rs.getInt(prefix + "id_banco"), "");
		String cuentaBancaria = "falta recuperar el campo";
		try{
			cuentaBancaria = rs.getString(prefix+ "cta_bcria");
		}catch(Exception e){
			
		}
		CuentaBancaria cb = new CuentaBancaria(rs.getInt(prefix+ "id_cta_bcria"), cuentaBancaria);
		cb.setBanco(b);
		Cheque cheque = new Cheque();
		cheque.setANombreDe(rs.getString(prefix + "a_nombre_de"));
		cheque.setImporte(rs.getBigDecimal(prefix + "importe"));
		cheque.setFecha(rs.getDate(prefix + "fecha"));
		cheque.setCuit(rs.getString(prefix + "cuit"));
		cheque.setNumero(rs.getBigDecimal(prefix + "nro_cheque"));
		cheque.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		cheque.setAlta_usr(rs.getString(prefix + "alta_usr"));
		cheque.setAlta_ip(rs.getString(prefix + "alta_ip"));
		cheque.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		cheque.setModi_usr(rs.getString(prefix + "modi_usr"));
		cheque.setModi_ip(rs.getString(prefix + "modi_ip"));
		cheque.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		cheque.setBaja_usr(rs.getString(prefix + "baja_usr"));
		cheque.setBaja_ip(rs.getString(prefix + "baja_ip"));
		cheque.setBanco(b);
		cheque.setCuentaBancaria(cb);
		cheque.setConcepto(rs.getString(prefix + "concepto"));
		cheque.setDebitoCredito(getTipoMapping(rs.getString(prefix
				+ "debito_credito")));
		
		return cheque;
	}

	private static Tipo getTipoMapping(String debCred) {
		if (debCred != null) {
			for (Tipo tipo : Tipo.values()) {
				if (tipo.toString().equals(debCred)) {
					return tipo;
				}
			}
		}
		return null;
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Cheque other = (Cheque) obj;
//		if (numero == null) {
//			if (other.numero != null)
//				return false;
//		} else if (!numero.equals(other.numero))
//			return false;
//		if (banco != null && other.banco != null) {
//			if (!banco.equals(other.banco)) {
//				return false;
//			}
//		}
//		return true;
//	}

	
	
	public void setPrestador(boolean prestador) {
		this.prestador = prestador;
	}

	public boolean isPrestador() {
		return prestador;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public void setCuentaBancaria(CuentaBancaria cuentaBancaria) {
		this.cuentaBancaria = cuentaBancaria;
		if (cuentaBancaria != null) {
			this.banco = cuentaBancaria.getBanco();
		}
	}

	public CuentaBancaria getCuentaBancaria() {
		return cuentaBancaria;
	}

	public void setDebitoCredito(Tipo debitoCredito) {
		this.debitoCredito = debitoCredito;
	}

	public Tipo getDebitoCredito() {
		return debitoCredito;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public Banco getBanco() {
		return banco;
	}

	public static class Estado {
		public static final int EMITIDO = 1;
		public static final int CARGADO = 2;
		public static final int RECIBIDO = 3;
		public static final int DEPOSITADO = 4;
		public static final int RECHAZADO = 5;
		public static final int SUSTITUIDO = 6;
		public static final int CHEQUE_PROPIO_CANJEADO = 7;
		public static final int ENTREGADO_A_TERCEROS = 9;

		private int id;
		private String descripcion;

		public Estado() {
		}

		public Estado(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		public static Estado getMapping(ResultSet rs) throws SQLException {
			return getMapping(rs, "");
		}

		public static Estado getMapping(ResultSet rs, String prefix)
				throws SQLException {
			Estado cheque = new Estado();
			cheque.setId(rs.getInt(prefix + "id"));
			cheque.setDescripcion(rs.getString(prefix + "descripcion"));
			return cheque;
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
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Estado other = (Estado) obj;
			if (id != other.id)
				return false;
			return true;
		}

	}

	public boolean isNew() {
		return alta_fecha == null;
	}

	public int saveIngreso(ReciboGlobalServiceImpl instance, Recibo recibo,
			String user, Connection con, int entidad) throws SystemException,
			DuplicateNumeroChequeException {
		
		if(this.getCuentaBancaria().getId_cuenta_bcria()<=0){ // si hay que darla de alta...
			try {
				EmpresaServiceUtil.saveCuentaBancaria(this.getCuit(),"000", this.getCuentaBancaria(), user, con);
			} catch (DuplicateEmpresaIdException e) {
				throw new SystemException(e);
			} catch (SQLException e) {
				throw new SystemException(e);
			} 
		}
		return instance.save(this, recibo, user, con, entidad);
	}
	
	public String getDescripcion() {
		if(null!=banco && null!=banco.getDescripcion_banco()){
			return banco.getDescripcion_banco();
		}else{
			return "";
		}
	}

	public void savePago(OrdenPago op, String screenName, Connection con, int entidad)
			throws Exception {
		
			OrdenPagoServiceUtil.savePago(this, op, screenName, con, entidad);
		
	}

	public String getTipo() {
		if(null!=cuentaBancaria && cuentaBancaria.getId_cuenta_bcria()==99){
			return this.getClass().getSimpleName()+" de Terceros";
		}else{
			return this.getClass().getSimpleName();
		}
	}

	public String getIdTipo() {
		return "";
	}

	public String getaNombreDe() {
		return aNombreDe;
	}

	public void setaNombreDe(String aNombreDe) {
		this.aNombreDe = aNombreDe;
	}

	public int getIdOp() {
		return idOp;
	}

	public void setIdOp(int idOp) {
		this.idOp = idOp;
	}

	public String getNroRecibo() {
		return nroRecibo;
	}

	public void setNroRecibo(String nroRecibo) {
		this.nroRecibo = nroRecibo;
	}

	public Date getFechaRecibo() {
		return fechaRecibo;
	}
	
	public String getFechaReciboAsString(){
		return null != fechaRecibo ? DateUtils.format(fechaRecibo, DateUtils.SHORT) : "";
	}

	public void setFechaRecibo(Date fechaRecibo) {
		this.fechaRecibo = fechaRecibo;
	}
	
	
	public int getMovBcrioId() {
		return movBcrioId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((banco == null) ? 0 : banco.hashCode());
		result = prime * result + ((cuentaBancaria == null) ? 0 : cuentaBancaria.hashCode());
		result = prime * result + ((cuit == null) ? 0 : cuit.hashCode());
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
		Cheque other = (Cheque) obj;
		if (banco == null) {
			if (other.banco != null)
				return false;
		} else if (!banco.equals(other.banco))
			return false;
		if (cuentaBancaria == null) {
			if (other.cuentaBancaria != null)
				return false;
		} else if (!cuentaBancaria.equals(other.cuentaBancaria))
			return false;
		if (cuit == null) {
			if (other.cuit != null)
				return false;
		} else if (!cuit.equals(other.cuit))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		return true;
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

	@Override
	public PagoBancario getPagoBancario() {
		// TODO Auto-generated method stub
		return null;
	}	
	
}
