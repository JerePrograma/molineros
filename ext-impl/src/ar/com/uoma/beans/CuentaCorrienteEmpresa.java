package ar.com.uoma.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;

import ar.com.empresas.beans.Actividad;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Regimen;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

public class CuentaCorrienteEmpresa implements Serializable {
	private static Log _log = LogFactoryUtil.getLog(CuentaCorrienteEmpresa.class);
	
	private Integer Id;
	private String cuit;
	private String razsoc;
	private Date periodo;
	private Integer tipo_boleta;
	private String cuenta_nombre;
	private Double monto;
	private String modo;
	private Date fecha_recauda;
	private Integer id_boleta;
	private Integer numerosecuencia;
	private Double monto_ddjj;
	private Double monto_boletas;
	private Double monto_pagos;
	private Double monto_actas;
	private Integer ddjj_seq;
	private String ddjj_es_max;
	private Double debe;
	private Double haber;
	private String ent_cob;
	private Double saldo;
	private Double saldo_ant;
	private Integer tot_reg;
	private Double tot_hd_ddjj;
	private Double tot_hd_boletas;
	private Double tot_hd_actas;
	private Double tot_hd_pagos;
	private Double tot_hd_saldo;
	
	public CuentaCorrienteEmpresa(Integer Id, String cuit, String razsoc, 
			Date periodo, Integer tipo_boleta, 
			String cuenta_nombre, Double monto,
			String modo, Date fecha_recauda, 
			Integer id_boleta,
			Integer numerosecuencia,
			Double monto_ddjj, Double monto_boletas, Double monto_pagos, Double monto_actas,
			Integer ddjj_seq, String ddjj_es_max,
			Double debe, Double haber,
			String ent_cob, Double saldo, Double saldo_ant,
			Integer tot_reg, Double tot_hd_ddjj, 
			Double tot_hd_boletas, Double tot_hd_actas,
			Double tot_hd_pagos, Double tot_hd_saldo) {
		
		this.Id = Id;
		this.cuit = cuit;
		this.razsoc = razsoc;
		this.periodo = periodo;
		this.tipo_boleta = tipo_boleta;
		this.cuenta_nombre = cuenta_nombre;
		this.monto = monto;
		this.monto_ddjj = monto_ddjj;
		this.monto_boletas = monto_boletas;
		this.monto_pagos = monto_pagos;
		this.monto_actas = monto_actas;
		this.modo = modo;
		this.fecha_recauda = fecha_recauda;
		this.id_boleta = id_boleta;
		this.numerosecuencia = numerosecuencia;
		this.ddjj_seq = ddjj_seq;
		this.ddjj_es_max = ddjj_es_max;
		this.debe = debe;
		this.haber = haber;
		this.ent_cob = ent_cob;
		this.saldo = saldo;
		this.saldo_ant = saldo_ant;
		this.tot_reg = tot_reg;
		this.tot_hd_ddjj = tot_hd_ddjj;
		this.tot_hd_boletas = tot_hd_boletas;
		this.tot_hd_actas = tot_hd_actas;
		this.tot_hd_pagos = tot_hd_pagos;
		this.tot_hd_saldo = tot_hd_saldo;
	}

	public Integer getId() {
		return this.Id;
	}

	public void setId(Integer Id) {
		this.Id = Id;
	}

	public Integer getTotReg() {
		return this.tot_reg;
	}

	public void setTotReg(Integer tot_reg) {
		this.tot_reg = tot_reg;
	}

	public Double getTotHdSaldo() {
		return this.tot_hd_saldo;
	}

	public void setTotHdSaldo(Double tot_hd_saldo) {
		this.tot_hd_saldo = tot_hd_saldo;
	}

	public Double getTotHdDdjj() {
		return this.tot_hd_ddjj;
	}

	public void setTotHdDdjj(Double tot_hd_ddjj) {
		this.tot_hd_ddjj = tot_hd_ddjj;
	}

	public Double getTotHdBoletas() {
		return this.tot_hd_boletas;
	}

	public void setTotHdBoletas(Double tot_hd_boletas) {
		this.tot_hd_boletas = tot_hd_boletas;
	}
	
	public Double getTotHdActas() {
		return this.tot_hd_actas;
	}

	public void setTotHdActas(Double tot_hd_actas) {
		this.tot_hd_actas = tot_hd_actas;
	}
	
	public Double getTotHdPagos() {
		return this.tot_hd_pagos;
	}

	public void setTotHdPagos(Double tot_hd_pagos) {
		this.tot_hd_pagos = tot_hd_pagos;
	}
	
	public String getCuit() {
		return this.cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRazSoc() {
		return this.razsoc;
	}

	public void setRazSoc(String razsoc) {
		this.razsoc = razsoc;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public String getPeriodo_yyyymm() {
		SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM");
		String strDate = sm.format(periodo);
		return strDate;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}
 
	public Integer getTipoBoleta() {
		return this.tipo_boleta;
	}

	public void setTipoBoleta(Integer tipo_boleta) {
		this.tipo_boleta = tipo_boleta;
	}

	public String getCuentaNombre() {
		return this.cuenta_nombre;
	}

	public void setCuentaNombre(String cuenta_nombre) {
		this.cuenta_nombre = cuenta_nombre;
	}
	
	public Double getMonto() {
		return this.monto;
	}

	public BigDecimal getMonto_BD() {
		BigDecimal aux = new BigDecimal(this.monto);
		aux = aux.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return aux;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public Double getMontoDDJJ() {
		return this.monto_ddjj;
	}

	public BigDecimal getMontoDDJJ_BD() {
		BigDecimal aux = new BigDecimal(this.monto_ddjj);
		aux = aux.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return aux;
	}
	
	public void setMontoDDJJ(Double monto) {
		this.monto_ddjj = monto;
	}

	public Double getMontoBoletas() {
		return this.monto_boletas;
	}

	public BigDecimal getMontoBoletas_BD() {
		BigDecimal aux = new BigDecimal(this.monto_boletas);
		aux = aux.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return aux;
	}
	
	public void setMontoBoletas(Double monto_boletas) {
		this.monto_boletas = monto_boletas;
	}	
		
	public Double getMontoPagos() {
		return this.monto_pagos;
	}

	public BigDecimal getMontoPagos_BD() {
		BigDecimal aux = new BigDecimal(this.monto_pagos);
		aux = aux.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return aux;
	}

	public void setMontoPagos(Double monto_pagos) {
		this.monto_pagos = monto_pagos;
	}	
	
	public Double getMontoActas() {
		return this.monto_actas;
	}

	public BigDecimal getMontoActas_BD() {
		BigDecimal aux = new BigDecimal(this.monto_actas);
		aux = aux.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return aux;
	}

	public void setMontoActas(Double monto_actas) {
		this.monto_actas = monto_actas;
	}	
	
	public void setModo(String modo) {
		this.modo = modo;
	}

	public String getModo() {
		return this.modo;
	}

	public Date getFechaRecauda() {
		return fecha_recauda;
	}

	public void setFechaRecauda(Date fecha_recauda) {
		this.fecha_recauda = fecha_recauda;
	}

	public Integer getIdBoleta() {
		return this.id_boleta;
	}

	public void setIdBoleta(Integer id_boleta) {
		this.id_boleta = id_boleta;
	}

	public Integer getNumeroSecuencia() {
		return this.numerosecuencia;
	}

	public void setNumeroSecuencia(Integer numerosecuencia) {
		this.numerosecuencia = numerosecuencia;
	}

	public Integer getDDJJ_Seq() {
		return this.ddjj_seq;
	}

	public void setDDJJ_Seq(Integer ddjj_seq) {
		this.ddjj_seq= ddjj_seq;
	}

	public String getDDJJ_Es_Max() {
		return this.ddjj_es_max;
	}

	public void setDDJJ_Es_Max(String ddjj_es_max) {
		this.ddjj_es_max = ddjj_es_max;
	}

	public Double getDebe() {
		return this.debe;
	}

	public void setDebe(Double debe) {
		this.debe = debe;
	}

	public Double getHaber() {
		return this.haber;
	}

	public void setHaber(Double haber) {
		this.haber = haber;
	}

	public String getEntcob() {
		if (this.ent_cob == null)
			return "";
		else 
		    return this.ent_cob;
	}

	public void setEntcob(String ent_cob) {
		this.ent_cob = ent_cob;
	}

	public Double getSaldo() {
		return this.saldo;
	}

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}

	public Double getSaldoAnt() {
		return this.saldo_ant;
	}

	public void setSaldoAnt(Double saldo_ant) {
		this.saldo_ant = saldo_ant;
	}


	public static CuentaCorrienteEmpresa getMapping(Integer Id, ResultSet rs, String prefix)
			throws SQLException {
		
		Integer _Id = Id;
		String _cuit = rs.getString(prefix + "empresa_cuit");
		String _razsoc = rs.getString(prefix + "razon_soc");
		
		Date _periodo = rs.getDate(prefix + "periodo");
		Integer _tipo_boleta = rs.getInt(prefix + "tipo_boleta");
		String _cta_nombre = rs.getString(prefix + "cta_nombre");
		Double _monto = rs.getDouble(prefix + "monto");
		
		String _modo = "";
		Date _fecha_recauda = null;
		String _ent_cob = "";
		
		Integer _id_boleta = 0;
		Integer _nro_seq = 0;
		Double _monto_ddjj = 0.00;
		Double _monto_boletas = 0.00;
		Double _monto_pagos = 0.00;
		Double _monto_actas = 0.00;
		Integer _ddjj_seq = 0;
		String _ddjj_es_max = "";
		Double _debe = 0.00;
		Double _haber = 0.00;
		Double _saldo = 0.00;
		Double _saldo_ant = 0.00;
		Integer _tot_reg = 0;
		Double _tot_hd_ddjj = 0.00;
		Double _tot_hd_boletas = 0.00;
		Double _tot_hd_actas = 0.00;
		Double _tot_hd_pagos = 0.00;
		Double _tot_hd_saldo = 0.00;
		
		try {
			_tot_reg = rs.getInt(prefix + "total_registros");
		} catch (Exception e) {
			
		}

		try {
			_tot_hd_saldo = rs.getDouble(prefix + "total_hd_saldo");
		} catch (Exception e) {
			
		}

		try {
			_tot_hd_ddjj = rs.getDouble(prefix + "total_hd_ddjj");
		} catch (Exception e) {
			
		}

		try {
			_tot_hd_boletas = rs.getDouble(prefix + "total_hd_boletas");
		} catch (Exception e) {
			
		}

		try {
			_tot_hd_actas = rs.getDouble(prefix + "total_hd_actas");
		} catch (Exception e) {
			
		}

		try {
			_tot_hd_pagos = rs.getDouble(prefix + "total_hd_pagos");
		} catch (Exception e) {
			
		}

		try {
			_id_boleta = rs.getInt(prefix + "id_boleta");
		} catch (Exception e) {
			
		}

		try {
			_nro_seq = rs.getInt(prefix + "numerosecuencia");
		} catch (Exception e) {
			
		}
		
		try {
			_monto_ddjj = rs.getDouble(prefix + "monto_ddjj");
		} catch (Exception e) {
			
		}

		try {
			_monto_boletas = rs.getDouble(prefix + "monto_boletas");
		} catch (Exception e) {
			
		}

		try {
			_monto_pagos = rs.getDouble(prefix + "monto_pagos");
		} catch (Exception e) {
			
		}

		try {
			_monto_actas = rs.getDouble(prefix + "monto_actas");
		} catch (Exception e) {
			
		}

		try {
			_ddjj_seq = rs.getInt(prefix + "ddjj_seq");
		} catch (Exception e) {
			
		}

		try {
			_ddjj_es_max = rs.getString(prefix + "ddjj_es_max");
		} catch (Exception e) {
			
		}

		try {
			_debe = rs.getDouble(prefix + "debe");
		} catch (Exception e) {
			
		}
		
		try {
			_haber = rs.getDouble(prefix + "haber");
		} catch (Exception e) {
			
		}

		try {
			_fecha_recauda = rs.getDate(prefix + "fecha_recauda");
		} catch (Exception e) {
			
		}

		try {
			_ent_cob = rs.getString(prefix + "ent_cob");
		} catch (Exception e) {
			
		}

		try {
			_saldo = rs.getDouble(prefix + "saldo");
		} catch (Exception e) {
			
		}

		try {
			_saldo_ant = rs.getDouble(prefix + "saldo_ant");
		} catch (Exception e) {
			
		}

		CuentaCorrienteEmpresa ctacte = new CuentaCorrienteEmpresa(_Id, 
				_cuit, _razsoc, _periodo, _tipo_boleta, 
				_cta_nombre, _monto, _modo,
				_fecha_recauda, 
				_id_boleta, _nro_seq, _monto_ddjj,
				_monto_boletas, _monto_pagos, _monto_actas,
				_ddjj_seq, _ddjj_es_max, 
				_debe, _haber, _ent_cob, _saldo, _saldo_ant,
				_tot_reg, _tot_hd_ddjj, _tot_hd_boletas,
				_tot_hd_actas, _tot_hd_pagos, _tot_hd_saldo);
				
		return ctacte;
	}	
}