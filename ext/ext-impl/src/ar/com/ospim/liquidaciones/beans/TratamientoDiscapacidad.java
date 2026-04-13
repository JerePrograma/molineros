package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.Documento;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.util.DateUtils;

/**
 * @author Carlos Rivas
 * @version 1.0
 * @created 14-Mar-2012 11:34:23 a.m.
 */
public class TratamientoDiscapacidad implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id_tratamiento;
	private Afiliado afiliado;
	private Prestacion prestacion;
	private int id_afiliado;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private BigDecimal cantidad;
	private String periodicidad;
	private Date periodo_desde;
	private Date periodo_hasta;
	private BigDecimal importe_total;
	private Prestador prestador;
	private int estado;
	private boolean recupera_ape;
	private String observaciones;
	private List<Documento> documentosFaltantes;
	private Empresa acreedor;
	private Seccional seccional;
	private BigDecimal cantidad_viajes_mes;
	private BigDecimal cantidad_kilometros_dia;
	private BigDecimal cantidad_kilometros_mes;
	private BigDecimal importe_kilometro_unit;
	private BigDecimal hs_espera_dia;
	private BigDecimal hs_espera_mes;
	private BigDecimal importe_hs_espera_unit;
	
	private BigDecimal importe_tercerizado;
	private String id_tercerizadora;
    private Boolean excepcionContratoPrestador;
	
	public TratamientoDiscapacidad() {
	}

	public TratamientoDiscapacidad(String cuil_titular, int inte,
			Date alta_fecha, int id_afiliado) {
		this.afiliado = new Afiliado(cuil_titular, inte);
		this.alta_fecha = alta_fecha;
		this.id_afiliado = id_afiliado;
	}

	public int getId_tratamiento() {
		return id_tratamiento;
	}

	public String getId_tratamientoString() {
		return String.valueOf(id_tratamiento);
	}

	public void setId_tratamiento(int idTratamiento) {
		id_tratamiento = idTratamiento;
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

	public int getId_afiliado() {
		return id_afiliado;
	}

	public void setId_afiliado(int idAfiliado) {
		id_afiliado = idAfiliado;
	}

	public String getAlta_fechaAsString() {
		return alta_fecha != null ? DateUtils.format(alta_fecha, "dd/MM/yyyy")
				: "";
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public Prestacion getPrestacion() {
		return prestacion;
	}

	public void setPrestacion(Prestacion prestacion) {
		this.prestacion = prestacion;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	public String getPeriodicidad() {
		return periodicidad;
	}

	public void setPeriodicidad(String periodicidad) {
		this.periodicidad = periodicidad;
	}

	public Date getPeriodo_desde() {
		return periodo_desde;
	}

	public void setPeriodo_desde(Date periodoDesde) {
		periodo_desde = periodoDesde;
	}

	public Date getPeriodo_hasta() {
		return periodo_hasta;
	}

	public void setPeriodo_hasta(Date periodoHasta) {
		periodo_hasta = periodoHasta;
	}

	public BigDecimal getImporte_total() {
		return importe_total;
	}

	public void setImporte_total(BigDecimal importeTotal) {
		importe_total = importeTotal;
	}

	public Boolean getExcepcionContratoPrestador() {
		return excepcionContratoPrestador==null?false:excepcionContratoPrestador;
	}

	public void setExcepcionContratoPrestador(Boolean excepcionContratoPrestador) {
		this.excepcionContratoPrestador = excepcionContratoPrestador;
	}

	public static TratamientoDiscapacidad getMapping(ResultSet rs, String prefix)
			throws SQLException {
		TratamientoDiscapacidad ap = new TratamientoDiscapacidad();
		ap.setId_tratamiento(rs.getInt(prefix + "id_tratamiento"));
		ap.setAfiliado(new Afiliado(rs.getString(prefix + "cuil_titular"), rs
				.getInt(prefix + "inte")));
		// ap.getAfiliado().setNombre(rs.getString(prefix + "nombre"));
		// ap.getAfiliado().setApellido(rs.getString(prefix + "apellido"));

		ap.setCantidad(rs.getBigDecimal(prefix + "cantidad"));
		ap.setPeriodicidad(rs.getString(prefix + "periodicidad"));

		ap.setPeriodo_desde(rs.getDate(prefix + "periodo_desde"));
		ap.setPeriodo_hasta(rs.getDate(prefix + "periodo_hasta"));
		ap.setImporte_total(rs.getBigDecimal(prefix + "importe_total"));

		ap.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		ap.setAlta_usr(rs.getString(prefix + "alta_usr"));
		ap.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		ap.setModi_usr(rs.getString(prefix + "modi_usr"));
		ap.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		ap.setBaja_usr(rs.getString(prefix + "baja_usr"));

		// ap.setPrestador(new Prestador(rs.getInt(prefix + "id_prestador")));
		ap.setObservaciones(rs.getString(prefix + "observaciones"));
		ap.setRecupera_ape(rs.getBoolean(prefix + "recupera_ape"));
		ap.setEstado(rs.getInt(prefix + "estado"));

		String cuitAcreedor = rs.getString(prefix + "cuit");
		String sucuAcreedor = rs.getString(prefix + "sucursal");
		String razonSocial = rs.getString(prefix + "razon_soc");
		int seccional = rs.getInt(prefix + "id_seccional");
		Empresa emp = new Empresa(cuitAcreedor, sucuAcreedor, null);
		emp.setRazon_soc(razonSocial);
		ap.setAcreedor(emp);
		ap.setSeccional(new Seccional(seccional, null));

		try {
			ap.setCantidad_viajes_mes(rs.getBigDecimal(prefix
					+ "cantidad_viajes_mes"));
			ap.setCantidad_kilometros_dia(rs.getBigDecimal(prefix
					+ "cantidad_kilometros_dia"));
			ap.setCantidad_kilometros_mes(rs.getBigDecimal(prefix
					+ "cantidad_kilometros_mes"));
			ap.setImporte_kilometro_unit(rs.getBigDecimal(prefix
					+ "importe_kilometro_unit"));
			// --total mes y total dia
			ap.setHs_espera_dia(rs.getBigDecimal(prefix + "hs_espera_dia"));
			ap.setHs_espera_mes(rs.getBigDecimal(prefix + "hs_espera_mes"));
			ap.setImporte_hs_espera_unit(rs.getBigDecimal(prefix
					+ "importe_hs_espera_unit"));
			ap.setImporte_tercerizado(rs.getBigDecimal(prefix
					+ "importe_tercerizado"));
			ap.setId_tercerizadora(rs.getString(prefix
					+ "id_tercerizadora"));
		} catch (Exception e) {
		}
		
		try{
	       ap.setExcepcionContratoPrestador(rs.getBoolean(prefix+"excepcion_contrato_prestador"));	
		} catch(Exception e){
		   ap.setExcepcionContratoPrestador(false); 	
		}
		return ap;
	}

	public String getPeriodoDesdeString() {
		return null != periodo_desde ? DateUtils.format(periodo_desde,
				DateUtils.PERIODO) : "";
	}

	public String getPeriodoHastaString() {
		return null != periodo_hasta ? DateUtils.format(periodo_hasta,
				DateUtils.PERIODO) : "";
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public boolean isRecupera_ape() {
		return recupera_ape;
	}

	public void setRecupera_ape(boolean recuperaApe) {
		recupera_ape = recuperaApe;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public List<Documento> getDocumentosFaltantes() {
		return documentosFaltantes;
	}

	public void setDocumentosFaltantes(List<Documento> documentosFaltantes) {
		this.documentosFaltantes = documentosFaltantes;
	}

	public void setAcreedor(Empresa acreedor) {
		this.acreedor = acreedor;
	}

	public Empresa getAcreedor() {
		return acreedor;
	}

	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}

	public Seccional getSeccional() {
		return seccional;
	}

	public BigDecimal getCantidad_viajes_mes() {
		return cantidad_viajes_mes != null ? cantidad_viajes_mes : BigDecimal.ZERO;
	}

	public void setCantidad_viajes_mes(BigDecimal cantidadViajesMes) {
		cantidad_viajes_mes = cantidadViajesMes;
	}

	public BigDecimal getCantidad_kilometros_dia() {
		return cantidad_kilometros_dia != null ? cantidad_kilometros_dia : BigDecimal.ZERO;
	}

	public void setCantidad_kilometros_dia(BigDecimal cantidadKilometrosDia) {
		cantidad_kilometros_dia = cantidadKilometrosDia;
	}

	public BigDecimal getCantidad_kilometros_mes() {
		try {
			this.cantidad_kilometros_mes = this.cantidad_kilometros_dia.multiply(this.cantidad_viajes_mes);
		} catch (NullPointerException e) {
			return new BigDecimal(0);
		}
		return cantidad_kilometros_mes;
	}

	public void setCantidad_kilometros_mes(BigDecimal cantidadKilometrosMes) {
		cantidad_kilometros_mes = cantidadKilometrosMes;
	}

	public BigDecimal getImporte_kilometro_unit() {
		return importe_kilometro_unit != null ? importe_kilometro_unit : BigDecimal.ZERO;
	}

	public void setImporte_kilometro_unit(BigDecimal importeKilometroUnit) {
		importe_kilometro_unit = importeKilometroUnit;
	}

	public BigDecimal getHs_espera_dia() {
		return hs_espera_dia != null ? hs_espera_dia : BigDecimal.ZERO;
	}

	public void setHs_espera_dia(BigDecimal hsEsperaDia) {
		hs_espera_dia = hsEsperaDia;
	}

	public BigDecimal getHs_espera_mes() {
		try {
			this.hs_espera_mes = this.hs_espera_dia.multiply(this.cantidad_viajes_mes);
		} catch (NullPointerException e) {
			return new BigDecimal(0);
		}
		return  hs_espera_mes;
	}

	public void setHs_espera_mes(BigDecimal hsEsperaMes) {
		hs_espera_mes = hsEsperaMes;
	}

	public BigDecimal getImporte_hs_espera_unit() {
		return importe_hs_espera_unit != null ? importe_hs_espera_unit : BigDecimal.ZERO;
	}

	public void setImporte_hs_espera_unit(BigDecimal importeHsEsperaUnit) {
		importe_hs_espera_unit = importeHsEsperaUnit;
	}
	
	public BigDecimal getImporte_total_km() {
		BigDecimal importe_total_km = BigDecimal.ZERO;
		try {
			importe_total_km = getCantidad_kilometros_mes().multiply(getImporte_kilometro_unit());
		} catch (NullPointerException e) {
			return new BigDecimal(0);
		}
		return  importe_total_km;
				
	}

	public BigDecimal getImporte_total_hs() {
		
		BigDecimal importe_total_hs = BigDecimal.ZERO;
		try {
			importe_total_hs = getHs_espera_mes().multiply(getImporte_hs_espera_unit());
		} catch (NullPointerException e) {
			return new BigDecimal(0);
		}
		return  importe_total_hs;
	}

	public BigDecimal getImporte_tercerizado() {
		return importe_tercerizado;
	}

	public void setImporte_tercerizado(BigDecimal importeTercerizado) {
		importe_tercerizado = importeTercerizado;
	}

	public String getId_tercerizadora() {
		return id_tercerizadora;
	}

	public void setId_tercerizadora(String idTercerizadora) {
		id_tercerizadora = idTercerizadora;
	}	
}