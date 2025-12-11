package ar.com.ospim.afip.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReporteDeudaEmpresa {
	private String cuit;
	private String razonSocial;
	private Date periodo;	
	private BigDecimal deuda;
	private int cantAfiliadosDeclarados;
	private int cantAfiliadosPagados;
	private BigDecimal remDeclarada;
	private BigDecimal remPagada;
	private BigDecimal pagado;
	private BigDecimal calculado;
	private int ramo;
	private int cantAfiliadosDeclarados_81;
	private int cantAfiliadosDeclarados_765;
	private BigDecimal remDeclarada_81;
	private BigDecimal remDeclarada_765;	
	private BigDecimal calculado_765;
	private BigDecimal calculado_810;
	private BigDecimal total_calculado;
	private BigDecimal porc_pagado;
	private BigDecimal pagado_acta_convenio;
	private String calle;
	private String numero;
	private String piso;
	private String dpto;
	private String localidad;
	private String provincia;
	private String codPostal;
	 
	
	public int getCantAfiliadosDeclarados() {
		return cantAfiliadosDeclarados;
	}

	public void setCantAfiliadosDeclarados(int cantAfiliadosDeclarados) {
		this.cantAfiliadosDeclarados = cantAfiliadosDeclarados;
	}

	public int getCantAfiliadosPagados() {
		return cantAfiliadosPagados;
	}

	public void setCantAfiliadosPagados(int cantAfiliadosPagados) {
		this.cantAfiliadosPagados = cantAfiliadosPagados;
	}

	public BigDecimal getRemDeclarada() {
		return remDeclarada;
	}

	public void setRemDeclarada(BigDecimal remDeclarada) {
		this.remDeclarada = remDeclarada;
	}
	
	public String getRemDeclaradaAsString() {
		return remDeclarada==null?"":remDeclarada.toString();
	}

	public BigDecimal getRemPagada() {
		return remPagada;
	}

	public void setRemPagada(BigDecimal remPagada) {
		this.remPagada = remPagada;
	}

	public BigDecimal getPagado() {
		return pagado;
	}
	
	public String getPagadoAsString() {
		return pagado==null?"":pagado.toString();
	}

	public void setPagado(BigDecimal pagado) {
		this.pagado = pagado;
	}

	public BigDecimal getCalculado() {
		return calculado;
	}

	public void setCalculado(BigDecimal calculado) {
		this.calculado = calculado;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}
	
	public String getPeriodoAsString() {
		SimpleDateFormat sdf= new SimpleDateFormat("MM/yyyy");
		return sdf.format(periodo);
	}

	public BigDecimal getDeuda() {
		return deuda;
	}
	
	public String getDeudaAsString() {
		return deuda==null?"":deuda.toString();
	}

	public void setDeuda(BigDecimal deuda) {
		this.deuda = deuda;
	}
	
	public int getRamo() {
		return ramo;
	}

	public void setRamo(int ramo) {
		this.ramo = ramo;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
		

	public int getCantAfiliadosDeclarados_81() {
		return cantAfiliadosDeclarados_81;
	}

	public void setCantAfiliadosDeclarados_81(int cantAfiliadosDeclarados_81) {
		this.cantAfiliadosDeclarados_81 = cantAfiliadosDeclarados_81;
	}

	public int getCantAfiliadosDeclarados_765() {
		return cantAfiliadosDeclarados_765;
	}

	public void setCantAfiliadosDeclarados_765(int cantAfiliadosDeclarados_765) {
		this.cantAfiliadosDeclarados_765 = cantAfiliadosDeclarados_765;
	}

	public BigDecimal getRemDeclarada_81() {
		return remDeclarada_81;
	}
	
	public String getRemDeclarada_81AsString() {
		return remDeclarada_81==null?"":remDeclarada_81.toString();
	}
	
	public void setRemDeclarada_81(BigDecimal remDeclarada_81) {
		this.remDeclarada_81 = remDeclarada_81;
	}

	public BigDecimal getRemDeclarada_765() {
		return remDeclarada_765;
	}
	
	public String getRemDeclarada_765AsString() {
		return remDeclarada_765==null?"":remDeclarada_765.toString();
	}

	public void setRemDeclarada_765(BigDecimal remDeclarada_765) {
		this.remDeclarada_765 = remDeclarada_765;
	}
	
	
	public BigDecimal getCalculado_765() {
		return calculado_765;
	}
	public String getCalculado_765AsString() {
		return calculado_765==null?"":calculado_765.toString();
	}

	public void setCalculado_765(BigDecimal calculado_765) {
		this.calculado_765 = calculado_765;
	}

	public BigDecimal getCalculado_810() {
		return calculado_810;
	}
	public String getCalculado_810AsString() {
		return calculado_810==null?"":calculado_810.toString();
	}

	public void setCalculado_810(BigDecimal calculado_810) {
		this.calculado_810 = calculado_810;
	}

	public BigDecimal getTotal_calculado() {
		return total_calculado;
	}
	
	public String getTotal_calculadoAsString() {
		return total_calculado==null?"":total_calculado.toString();
	}

	public void setTotal_calculado(BigDecimal totalCalculado) {
		total_calculado = totalCalculado;
	}

	public BigDecimal getPorc_pagado() {
		return porc_pagado;
	}
	
	public String getPorc_pagadoAsString() {
		return porc_pagado==null?"":porc_pagado.toString();
	}

	public void setPorc_pagado(BigDecimal porcPagado) {
		porc_pagado = porcPagado;
	}
	

	public BigDecimal getPagado_acta_convenio() {
		return pagado_acta_convenio;
	}

	public void setPagado_acta_convenio(BigDecimal porcPagadoActaConvenio) {
		pagado_acta_convenio = porcPagadoActaConvenio;
	}
	
	public String getPagadoActaConvenioAsString() {
		return pagado_acta_convenio==null?"":pagado_acta_convenio.toString();
	}

	public static ReporteDeudaEmpresa getMapping(ResultSet rs)
			throws SQLException {
		return getMapping(rs, "");
	}	
	

	public static ReporteDeudaEmpresa getMapping(ResultSet rs, String prefix)
			throws SQLException {
		ReporteDeudaEmpresa deudaEmpresa = new ReporteDeudaEmpresa();
		deudaEmpresa.setPeriodo(rs.getDate(prefix + "periodo"));
		deudaEmpresa.setDeuda(rs.getBigDecimal(prefix + "deuda"));
		deudaEmpresa.setCuit(rs.getString(prefix + "cuit"));
		deudaEmpresa.setCantAfiliadosDeclarados(rs.getInt(prefix + "cant_afiliados_declarados"));
		deudaEmpresa.setCantAfiliadosPagados(rs.getInt(prefix + "cant_afiliados_pagados"));
		deudaEmpresa.setRemDeclarada(rs.getBigDecimal(prefix + "rem_declarada"));
		deudaEmpresa.setRemPagada(rs.getBigDecimal(prefix + "rem_pagada"));
		deudaEmpresa.setPagado(rs.getBigDecimal(prefix + "pagado"));
		deudaEmpresa.setCalculado(rs.getBigDecimal(prefix + "calculado"));
		deudaEmpresa.setCalle(rs.getString(prefix + "calle"));
		deudaEmpresa.setNumero(rs.getString(prefix + "numero"));
		deudaEmpresa.setPiso(rs.getString(prefix + "piso"));
		deudaEmpresa.setDpto(rs.getString(prefix + "dpto"));
		deudaEmpresa.setLocalidad(rs.getString(prefix + "localidad"));
		deudaEmpresa.setProvincia(rs.getString(prefix + "provincia"));
		deudaEmpresa.setCodPostal(rs.getString(prefix + "cod_postal"));
		return deudaEmpresa;
	}

	public static ReporteDeudaEmpresa getMapping2(ResultSet rs) throws SQLException{
		
		ReporteDeudaEmpresa deuda = new ReporteDeudaEmpresa();
		
		deuda.setPeriodo(rs.getDate("periodo"));
		deuda.setCuit(rs.getString("cuit"));
		deuda.setRazonSocial(rs.getString("razon_soc"));
		deuda.setRamo(rs.getInt("ramo"));
		deuda.setCantAfiliadosDeclarados_81(rs.getInt("total_afi_81"));
		deuda.setCantAfiliadosDeclarados_765(rs.getInt("total_afi_765"));
		deuda.setCantAfiliadosDeclarados(rs.getInt("total_empleados"));
		deuda.setRemDeclarada_81(rs.getBigDecimal("total_rem_81"));
		deuda.setRemDeclarada_765(rs.getBigDecimal("total_rem_765"));
		deuda.setRemDeclarada(rs.getBigDecimal("total_remuneracion"));
		deuda.setCalculado_810(rs.getBigDecimal("calculado_81"));
		deuda.setCalculado_765(rs.getBigDecimal("calculado_765"));
		deuda.setTotal_calculado(rs.getBigDecimal("total_calculado"));
		deuda.setPagado(rs.getBigDecimal("pagado"));
		deuda.setPagado_acta_convenio(rs.getBigDecimal("pagado_acta_convenio"));
		deuda.setPorc_pagado(rs.getBigDecimal("porc_pagado"));
		deuda.setDeuda(rs.getBigDecimal("deuda"));
		deuda.setCalle(rs.getString("calle"));
		deuda.setNumero(rs.getString("numero"));
		deuda.setPiso(rs.getString("piso"));
		deuda.setDpto(rs.getString("dpto"));
		deuda.setLocalidad(rs.getString("localidad"));
		deuda.setProvincia(rs.getString("provincia"));
		deuda.setCodPostal(rs.getString("cod_postal"));
		
		return deuda;
	}
	
	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getPiso() {
		return piso;
	}

	public void setPiso(String piso) {
		this.piso = piso;
	}

	public String getDpto() {
		return dpto;
	}

	public void setDpto(String dpto) {
		this.dpto = dpto;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getCodPostal() {
		return codPostal;
	}

	public void setCodPostal(String codPostal) {
		this.codPostal = codPostal;
	}
	
	

}
