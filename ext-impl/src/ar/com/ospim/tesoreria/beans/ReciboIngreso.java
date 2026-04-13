package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Ingreso;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.beans.ReciboAnticipo;

public class ReciboIngreso {
	private int id;
	private Ingreso ingreso;
	protected Date alta_fecha;
	protected String alta_usr;
	protected String alta_ip;
	protected Date modi_fecha;
	protected String modi_usr;
	protected String modi_ip;
	protected Date baja_fecha;
	protected String baja_usr;
	protected String baja_ip;
	protected int convenioId;
	protected int actaId;
	private int movBcrioId;

	public ReciboIngreso() {
	}

	public ReciboIngreso(Ingreso ingreso, int id) {
		this.ingreso = ingreso;
		this.id = id;
	}

	public ReciboIngreso(Ingreso ingreso) {
		this.ingreso = ingreso;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Ingreso getIngreso() {
		return ingreso;
	}

	public void setIngreso(Ingreso ingreso) {
		this.ingreso = ingreso;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ingreso == null) ? 0 : ingreso.hashCode());
		return result;
	}

	public int getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(int convenioId) {
		this.convenioId = convenioId;
	}
		
	public int getActaId() {
		return actaId;
	}

	public void setActaId(int actaId) {
		this.actaId = actaId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReciboIngreso other = (ReciboIngreso) obj;
		if (ingreso == null) {
			if (other.ingreso != null)
				return false;
		}
		if (ingreso instanceof Cheque && null != other.getIngreso() && other.getIngreso() instanceof Cheque) {
			if(ingreso.getBanco().getId_banco()!=other.ingreso.getBanco().getId_banco()){
				return false;
			}
			if(null==ingreso.getNumeroStr() || null==other.ingreso.getNumeroStr() || !ingreso.getNumeroStr().equals(other.ingreso.getNumeroStr())){
				return false;				
			}
			if(null==ingreso.getCuentaBancaria() || null==other.ingreso.getCuentaBancaria() || !ingreso.getCuentaBancaria().equals(other.ingreso.getCuentaBancaria())){
				return false;				
			}
		} else { //para transferencias bancarias
			if (ingreso.getCuentaBancaria() != null
					&& ingreso.getCuentaBancaria().getId_cuenta_bcria() != 0) {
				if (other.getIngreso() != null
						&& other.getIngreso().getCuentaBancaria() != null
						&& other.getIngreso().getCuentaBancaria()
								.getId_cuenta_bcria() == ingreso
								.getCuentaBancaria().getId_cuenta_bcria()) {
					return true;
				} else {
					return false;
				}
			} else if (null != ingreso.getImporte()) {
				if (null != other.getIngreso()
						&& null != other.getIngreso().getImporte()
						&& other.getIngreso().getImporte()
								.equals(ingreso.getImporte())) {
					return true;
				} else {
					return false;
				}
			}
		}

		/*
		 * (!ingreso.equals(other.ingreso)) return false;
		 */
		return true;
	}

	public static ReciboIngreso getMapping(ResultSet rs, int entidad)
			throws SQLException {
		return getMapping(rs, "", entidad);
	}

	public static ReciboIngreso getMapping(ResultSet rs, String prefix,
			int entidad) throws SQLException {
		Ingreso ingreso = null;
		
		int id = rs.getInt(prefix + "id");
		int idBanco = rs.getInt(prefix + "id_banco");
		int id_cuenta_bcria_destino_deposito = rs.getInt(prefix + "id_cuenta_bcria_destino_deposito");
		int tipoDeposito = rs.getInt(prefix + "id_recibo_ingreso_tipo_deposito");
		String nroCheque = rs.getString(prefix + "nro_cheque");
		BigDecimal nroPagare = null;
		try {
			nroPagare = rs.getBigDecimal(prefix + "nro_pagare");
		} catch (Exception e) {
			// do nothing
		}

		String nroDepositoBancario = rs.getString(prefix + "numero_deposito");
		BigDecimal importe = rs.getBigDecimal(prefix + "importe");
		Date fecha = rs.getDate(prefix + "fecha");
		int idAntic = 0;
		try {
			idAntic = rs.getInt(prefix + "id_anticipo_recibo_concepto");
		} catch (Exception e) {	}
		
		if (nroCheque != null) {
			Cheque cheque = Cheque.getMapping(rs, "ch__");
			cheque.setEstado(Cheque.Estado.getMapping(rs, "es__"));
			cheque.setImporte(importe);
			cheque.setBanco(new Banco(idBanco, ""));
			cheque.setNumero(new BigDecimal(nroCheque));

			ingreso = cheque;
		} else if (tipoDeposito != 0) {
			DepositoBancario depo = new DepositoBancario();
			CuentaBancaria ctaBcria = new CuentaBancaria(
					id_cuenta_bcria_destino_deposito);
			ctaBcria.setBanco(new Banco(rs.getInt("ri__id_banco_destino_deposito"), ""));
			depo.setCuentaBancaria(ctaBcria);
			depo.setTipoDeposito(tipoDeposito);
			depo.setNumero(nroDepositoBancario);
			depo.setImporte(importe);
			depo.setFecha(fecha);
			if (entidad == WebKeysGlobal.UOMA) {
				try {
					depo.setSucuNacion(rs.getInt("ri__sucu_bco"));
				} catch (Exception e) { }
			}
			ingreso = depo;
		} else if (idAntic != 0) {
			ReciboAnticipo ra = ReciboAnticipo.getMapping(rs, "RA__");
			ingreso = ra;
		} else if (nroPagare != null) {
			Pagare pagare = new Pagare(nroPagare);
			pagare.setImporte(importe);
			pagare.setFecha(fecha);
			ingreso = pagare;
		} else {
			Efectivo ef = new Efectivo();
			ef.setImporte(importe);
			ef.setFecha(fecha);
			ef.setEstado(new Efectivo.Estado(rs.getInt(prefix + "id_estado_efectivo")));
			ingreso = ef;
		}

		ingreso.setNroRecibo(rs.getString("nro_recibo"));
		ingreso.setFechaRecibo(rs.getDate("fecha_recibo"));

		ReciboIngreso reciboIngreso = new ReciboIngreso(ingreso, id);
		reciboIngreso.setAlta_usr(rs.getString(prefix + "alta_usr"));
		reciboIngreso.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		reciboIngreso.setModi_usr(rs.getString(prefix + "modi_usr"));
		reciboIngreso.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		reciboIngreso.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		reciboIngreso.setBaja_usr(rs.getString(prefix + "baja_usr"));
		try{
			reciboIngreso.setMovBcrioId(rs.getInt("movimiento_bcrio_id"));
		}catch(Exception e){
			//do nothing
		}
		return reciboIngreso;
	}

	public int getMovBcrioId() {
		return movBcrioId;
	}

	public void setMovBcrioId(int movBcrioId) {
		this.movBcrioId = movBcrioId;
	}
	
	
}
