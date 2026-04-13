package ar.com.ospim.afip.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.WebKeysGlobal;

public class ReporteDeudaNominaEmpresa {

	private String cuitContribuyente;
	private String cuilAportante;
	private Date periodo;
	private BigDecimal aporte;
	private BigDecimal contribucion;
	private BigDecimal remuneracionDeclarada;
	private BigDecimal remuneracionPagada;
	private BigDecimal calculado;
	private BigDecimal deuda;
	private String apellido;
	private String nombre;
	private List<PagosEmpresa> pagos;
	private int tipoAporte=0;
	private String camara;
	private Date fechaIngreso;
	private String categoria;
	private BigDecimal remuneracionDeclaradaTotal;
	private int cantAfiliadosTotal;
	
	
	
	public BigDecimal getRemuneracionDeclaradaTotal() {
		return remuneracionDeclaradaTotal;
	}

	public void setRemuneracionDeclaradaTotal(BigDecimal remuneracionDeclaradaTotal) {
		this.remuneracionDeclaradaTotal = remuneracionDeclaradaTotal;
	}

	public int getCantAfiliadosTotal() {
		return cantAfiliadosTotal;
	}

	public void setCantAfiliadosTotal(int cantAfiliadosTotal) {
		this.cantAfiliadosTotal = cantAfiliadosTotal;
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

	public BigDecimal getRemuneracionDeclarada() {
		return remuneracionDeclarada;
	}

	public void setRemuneracionDeclarada(BigDecimal remuneracionDeclarada) {
		this.remuneracionDeclarada = remuneracionDeclarada;
	}

	public BigDecimal getRemuneracionPagada() {
		return remuneracionPagada;
	}

	public void setRemuneracionPagada(BigDecimal remuneracionPagada) {
		this.remuneracionPagada = remuneracionPagada;
	}

	public BigDecimal getCalculado() {
		return calculado;
	}

	public void setCalculado(BigDecimal calculado) {
		this.calculado = calculado;
	}

	public BigDecimal getDeuda() {
		return deuda;
	}

	public void setDeuda(BigDecimal deuda) {
		this.deuda = deuda;
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

	public int getTipoAporte() {
		return tipoAporte;
	}

	public void setTipoAporte(int tipoAporte) {
		this.tipoAporte = tipoAporte;
	}

	public static ReporteDeudaNominaEmpresa getMapping(ResultSet rs)
			throws SQLException {
		return getMapping(rs, "");
	}

	public static ReporteDeudaNominaEmpresa getMapping(ResultSet rs,
			String prefix) throws SQLException {
		ReporteDeudaNominaEmpresa repo = new ReporteDeudaNominaEmpresa();
		repo.setCuitContribuyente(rs.getString("cuit_contribuyente"));
		repo.setCuilAportante(rs.getString("cuil_aportante"));
		repo.setPeriodo(rs.getDate("periodo"));
		repo.setAporte(rs.getBigDecimal("aporte"));
		repo.setContribucion(rs.getBigDecimal("contribucion"));
		repo.setRemuneracionDeclarada(rs.getBigDecimal("rem_declarada"));
		repo.setRemuneracionPagada(rs.getBigDecimal("rem_pagada"));
		repo.setCalculado(rs.getBigDecimal("calculado"));
		repo.setDeuda(rs.getBigDecimal("deuda"));
		repo.setApellido(rs.getString("apellido"));
		repo.setNombre(rs.getString("nombre"));
		ArrayList<PagosEmpresa> pagos = new ArrayList<PagosEmpresa>();
		pagos.add(new PagosEmpresa(rs.getDate("fecha_recauda"), rs
				.getBigDecimal("pagado")));
		repo.setPagos(pagos);
		return repo;
	}
	
	public static ReporteDeudaNominaEmpresa getMappingEmpleadores(ResultSet rs) throws SQLException {
		ReporteDeudaNominaEmpresa repo = new ReporteDeudaNominaEmpresa();
		repo.setCuitContribuyente(rs.getString("cuit"));
		repo.setCuilAportante(rs.getString("cuil"));
		repo.setPeriodo(rs.getDate("periodo"));
		int tipoAporte=rs.getInt("tipo_boleta");
		repo.setTipoAporte(tipoAporte);
		repo.setAporte(rs.getBigDecimal("pagado"));
		if(tipoAporte==WebKeysGlobal.TIPO_BOLETA_AMTIMA){
			repo.setCalculado(rs.getBigDecimal("amtima"));	
		}else if(tipoAporte==WebKeysGlobal.TIPO_BOLETA_ART_46){
			repo.setCalculado(rs.getBigDecimal("art_46"));	
		}else if(tipoAporte==WebKeysGlobal.TIPO_BOLETA_SOCIAL_UOMA){
			repo.setCalculado(rs.getBigDecimal("cta_uoma"));	
		}else if(tipoAporte==WebKeysGlobal.TIPO_BOLETA_SOLIDARIO_UOMA){
			repo.setCalculado(rs.getBigDecimal("solidario"));	
		}else if(tipoAporte==WebKeysGlobal.TIPO_BOLETA_USUFRUCTO){
			repo.setCalculado(rs.getBigDecimal("usufructo"));	
		}
		repo.setRemuneracionDeclarada(rs.getBigDecimal("remuneracion"));		
		
		repo.setApellido(rs.getString("apellido"));
		repo.setNombre(rs.getString("nombre"));
		repo.setCamara(rs.getString("camara"));
		repo.setCategoria(rs.getString("categoriasalarial"));
		repo.setFechaIngreso(rs.getDate("fechaingreso"));
		repo.setCantAfiliadosTotal(rs.getInt("cantidad_afiliados"));
		repo.setRemuneracionDeclaradaTotal(rs.getBigDecimal("remuneracion_total"));
		
		ArrayList<PagosEmpresa> pagos = new ArrayList<PagosEmpresa>();
		pagos.add(new PagosEmpresa(rs.getDate("fecha_recauda"), rs
				.getBigDecimal("pagado"),tipoAporte));
		repo.setPagos(pagos);
		return repo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPagos(List<PagosEmpresa> pagos) {
		this.pagos = pagos;
	}

	public List<PagosEmpresa> getPagos() {
		return pagos;
	}
		

	public String getCamara() {
		return camara;
	}

	public void setCamara(String camara) {
		this.camara = camara;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cuilAportante == null) ? 0 : cuilAportante.hashCode());
		result = prime
				* result
				+ ((cuitContribuyente == null) ? 0 : cuitContribuyente
						.hashCode());
		result = prime * result + ((periodo == null) ? 0 : periodo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ReporteDeudaNominaEmpresa other = (ReporteDeudaNominaEmpresa) obj;
		if (cuilAportante == null) {
			if (other.cuilAportante != null){
				return false;
			}
		} else if (!cuilAportante.equals(other.cuilAportante)){
			return false;
		}
		if (cuitContribuyente == null) {
			if (other.cuitContribuyente != null){
				return false;
			}
		} else if (!cuitContribuyente.equals(other.cuitContribuyente)){
			return false;
		}
		if (periodo == null) {
			if (other.periodo != null){
				return false;
			}
		} else if (!periodo.equals(other.periodo)){
			return false;
		}
		
		if (tipoAporte != other.tipoAporte) {			
			return false;
		}		
	
		return true;
	}

	static public class PagosEmpresa {
		private Date fecha;
		private BigDecimal monto;
		private int tipoBoleta;

		public Date getFecha() {
			return fecha;
		}

		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}

		public BigDecimal getMonto() {
			return monto;
		}

		public void setMonto(BigDecimal monto) {
			this.monto = monto;
		}
		
		public int getTipoBoleta() {
			return tipoBoleta;
		}

		public void setTipoBoleta(int tipoBoleta) {
			this.tipoBoleta = tipoBoleta;
		}

		public PagosEmpresa(Date fecha, BigDecimal monto) {
			super();
			this.fecha = fecha;
			this.monto = monto;
		}
		
		public PagosEmpresa(Date fecha, BigDecimal monto, int tipo_boleta) {
			super();
			this.fecha = fecha;
			this.monto = monto;
			this.tipoBoleta=tipo_boleta;
		}
	}
}
