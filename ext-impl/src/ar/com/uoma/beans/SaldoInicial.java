package ar.com.uoma.beans;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.portlet.PortletRequest;

import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.afip.service.FeriadosServiceUtil;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysCajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceImpl;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import edu.emory.mathcs.backport.java.util.Arrays;

public class SaldoInicial implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6514721875746277069L;
	private static Log _log = LogFactoryUtil.getLog(SaldoInicial.class);
	
	private Integer Id;
	private String cuit;
	private String suc;
	private String razsoc;
	private Date periodo;
	private String periodo_str;
	private Integer tipo_boleta;
	private String cuenta_nombre;
	private Double monto;
	
	public Integer getId() {
		return this.Id;
	}

	public void setId(Integer Id) {
		this.Id = Id;
	}

	public String getCuit() {
		return this.cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getSucursal() {
		return this.suc;
	}

	public void setSucursal(String suc) {
		this.suc = suc;
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
		String strDate = "";
		if (periodo != null) 
			strDate = sm.format(periodo);
		return strDate;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public void setPeriodo_STR(String periodo) {
		this.periodo_str = periodo;
	}

	public String getPeriodo_STR() {
		return this.periodo_str;
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
	
	public SaldoInicial() {
	}
			
	public static SaldoInicial getMapping(ResultSet rs) throws SQLException {
		SaldoInicial _saldo = new SaldoInicial();
		_saldo.setId(rs.getInt("id"));
		_saldo.setCuit(rs.getString("empresa_cuit"));
		_saldo.setSucursal(rs.getString("empresa_suc"));		
		_saldo.setRazSoc(rs.getString("razon_social"));
		_saldo.setPeriodo(rs.getDate("periodo"));
		_saldo.setTipoBoleta(rs.getInt("tipo_boleta"));
		_saldo.setCuentaNombre(rs.getString("cuenta_nombre"));
		_saldo.setMonto(rs.getDouble("monto"));
				
		return _saldo;
	}	
}
