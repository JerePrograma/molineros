package ar.com.ospim.afip.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ReporteAporteContribucionesEmpresa {
	private String cuitContribuyente;
	private String cuilAportante;
	private Date periodo;
	private BigDecimal aporte;
	private BigDecimal contribucion;
	private int cantidadAfiliadosDeclarados;
	private int cantidadAfiliadosPagados;
	private BigDecimal remuneracionPagada;
	private BigDecimal remuneracionDeclarada;
	private BigDecimal pagado;
	private BigDecimal calculado;
	private BigDecimal porcentaje;
	private String razon;
	private String localidad;
	private String provincia;
	private String codigoPostal;
	private String numero;
	private int ramo;
	private String apellido;
	private String nombre;
	private Date fechaPago;
	private String tercerizadora;
	private String descripcion_ac_debito;
	private String descripcion_ac_credito;
	private BigDecimal importe_ac_debito;
	private BigDecimal importe_ac_credito;
	private String existePadron;
	private int idSeccional;
	private String Seccional;
	
	
	

	public static ReporteAporteContribucionesEmpresa getMapping(ResultSet rs, boolean act_conv)
			throws SQLException {
		return getMapping(rs, "", act_conv);
	}
	
	public static ReporteAporteContribucionesEmpresa getMappingMonotrib(ResultSet rs) throws SQLException {
		ReporteAporteContribucionesEmpresa aporte = new ReporteAporteContribucionesEmpresa();		
		aporte.setCuilAportante(rs.getString("cuil_aportante"));
		aporte.setPeriodo(rs.getDate("periodo"));
		aporte.setAporte(rs.getBigDecimal("importe"));		
		aporte.setApellido(rs.getString("apellido"));
		aporte.setTercerizadora(rs.getString("tercerizadora"));
		aporte.setNombre(rs.getString("nombre"));
		aporte.setExistePadron(rs.getString("no_existe_padron"));
		aporte.setIdSeccional(rs.getInt("id_seccional"));
		aporte.setSeccional(rs.getString("seccional"));
		return aporte;
	}

	public static ReporteAporteContribucionesEmpresa getMapping(ResultSet rs,
			String prefix, boolean acta_conv) throws SQLException {
		ReporteAporteContribucionesEmpresa aporte = new ReporteAporteContribucionesEmpresa();
		aporte.setCuitContribuyente(rs.getString("cuit_contribuyente"));
		aporte.setCuilAportante(rs.getString("cuil_aportante"));
		aporte.setPeriodo(rs.getDate("periodo"));
		aporte.setAporte(rs.getBigDecimal("aporte"));
		aporte.setContribucion(rs.getBigDecimal("contribucion"));
		aporte.setCantidadAfiliadosDeclarados(rs
				.getInt("cant_afiliados_declarados"));
		aporte.setCantidadAfiliadosPagados(rs.getInt("cant_afiliados_pagados"));
		aporte.setRemuneracionPagada(rs.getBigDecimal("rem_pagada"));
		aporte.setRemuneracionDeclarada(rs.getBigDecimal("rem_declarada"));
		aporte.setPagado(rs.getBigDecimal("pagado"));
		aporte.setCalculado(rs.getBigDecimal("calculado"));
		aporte.setPorcentaje(rs.getBigDecimal("porc"));
		aporte.setRazon(rs.getString("razon"));
		aporte.setLocalidad(rs.getString("localidad"));
		aporte.setProvincia(rs.getString("provincia_id"));
		aporte.setCodigoPostal(rs.getString("codigopostal"));
		aporte.setNumero(rs.getString("numero"));
		aporte.setRamo(rs.getInt("ramo"));
		aporte.setApellido(rs.getString("apellido"));
		aporte.setNombre(rs.getString("nombre"));
		aporte.setFechaPago(rs.getDate("fecha_recauda"));
		aporte.setTercerizadora(rs.getString("tercerizadora"));
		aporte.setIdSeccional(rs.getInt("id_seccional"));
		aporte.setSeccional(rs.getString("seccional"));
		//--
		if(acta_conv){
			aporte.setDescripcion_ac_credito(rs.getString("descripcion_acta_credito"));
			aporte.setDescripcion_ac_debito(rs.getString("descripcion_acta_debito"));
			aporte.setImporte_ac_credito(rs.getBigDecimal("importe_acta_credito"));
			aporte.setImporte_ac_debito(rs.getBigDecimal("importe_acta_debito"));			
		}
		return aporte;
	}
	
	public String getCuitContribuyente() {
		return cuitContribuyente;
	}

	public void setCuitContribuyente(String cuitContribuyente) {
		this.cuitContribuyente = cuitContribuyente;
	}

	public String getCuilAportante() {
		return cuilAportante;
	}

	public void setCuilAportante(String cuilAportante) {
		this.cuilAportante = cuilAportante;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public BigDecimal getAporte() {
		return aporte;
	}

	public void setAporte(BigDecimal aporte) {
		this.aporte = aporte;
	}

	public BigDecimal getContribucion() {
		return contribucion;
	}

	public void setContribucion(BigDecimal contribucion) {
		this.contribucion = contribucion;
	}

	public int getCantidadAfiliadosDeclarados() {
		return cantidadAfiliadosDeclarados;
	}

	public void setCantidadAfiliadosDeclarados(int cantidadAfiliadosDeclarados) {
		this.cantidadAfiliadosDeclarados = cantidadAfiliadosDeclarados;
	}

	public int getCantidadAfiliadosPagados() {
		return cantidadAfiliadosPagados;
	}

	public void setCantidadAfiliadosPagados(int cantidadAfiliadosPagados) {
		this.cantidadAfiliadosPagados = cantidadAfiliadosPagados;
	}

	public BigDecimal getRemuneracionPagada() {
		return remuneracionPagada;
	}

	public void setRemuneracionPagada(BigDecimal remuneracionPagada) {
		this.remuneracionPagada = remuneracionPagada;
	}

	public BigDecimal getRemuneracionDeclarada() {
		return remuneracionDeclarada;
	}

	public void setRemuneracionDeclarada(BigDecimal remuneracionDeclarada) {
		this.remuneracionDeclarada = remuneracionDeclarada;
	}

	public BigDecimal getPagado() {
		return pagado;
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

	public BigDecimal getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(BigDecimal porcentaje) {
		this.porcentaje = porcentaje;
	}

	public String getRazon() {
		return razon;
	}

	public void setRazon(String razon) {
		this.razon = razon;
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

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public int getRamo() {
		return ramo;
	}

	public void setRamo(int ramo) {
		this.ramo = ramo;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}

	public Date getFechaPago() {
		return fechaPago;
	}

	public String getTercerizadora() {
		return tercerizadora;
	}

	public void setTercerizadora(String tercerizadora) {
		this.tercerizadora = tercerizadora;
	}

	public String getDescripcion_ac_debito() {
		return descripcion_ac_debito;
	}

	public void setDescripcion_ac_debito(String descripcion_ac_debito) {
		this.descripcion_ac_debito = descripcion_ac_debito;
	}

	public String getDescripcion_ac_credito() {
		return descripcion_ac_credito;
	}

	public void setDescripcion_ac_credito(String descripcion_ac_credito) {
		this.descripcion_ac_credito = descripcion_ac_credito;
	}

	public BigDecimal getImporte_ac_debito() {
		return importe_ac_debito;
	}

	public void setImporte_ac_debito(BigDecimal importe_ac_debito) {
		this.importe_ac_debito = importe_ac_debito;
	}

	public BigDecimal getImporte_ac_credito() {
		return importe_ac_credito;
	}

	public void setImporte_ac_credito(BigDecimal importe_ac_credito) {
		this.importe_ac_credito = importe_ac_credito;
	}

	public String getExistePadron() {
		return existePadron;
	}

	public void setExistePadron(String existePadron) {
		this.existePadron = existePadron;
	}
	
	public int getIdSeccional() {
		return idSeccional;
	}

	public void setIdSeccional(int idSeccional) {
		this.idSeccional = idSeccional;
	}

	public String getSeccional() {
		return Seccional;
	}

	public void setSeccional(String seccional) {
		Seccional = seccional;
	}

	public static HashMap<String, List<ReporteAporteContribucionesEmpresa>> getHashMapApoCont(List<ReporteAporteContribucionesEmpresa> lista){
		HashMap<String, List<ReporteAporteContribucionesEmpresa>> hm=new HashMap<String, List<ReporteAporteContribucionesEmpresa>>();		
		for(ReporteAporteContribucionesEmpresa repo:lista){			
			List <ReporteAporteContribucionesEmpresa> temp=hm.get(repo.getCuitContribuyente());			
			if(temp==null){				
				temp=new ArrayList<ReporteAporteContribucionesEmpresa>();
				temp.add(repo);
			}else{
				temp.add(repo);				
			}
			hm.put(repo.getCuitContribuyente(),temp);
		}
		return hm;		
	}	
}
