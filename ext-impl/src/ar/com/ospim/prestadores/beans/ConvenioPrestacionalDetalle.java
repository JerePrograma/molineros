package ar.com.ospim.prestadores.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.liquidaciones.beans.TipoNomenclador;

/**
 * @version 1.0
 * @created 19-Oct-2012 02:25:41 p.m.
 */
public class ConvenioPrestacionalDetalle implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -2871379242655844634L;

	private int id; //id_convenio_prest_detalle
	private int idConvenioPrestacional;
	private Date fechaDesde;
	private Date fechaHasta;
//	private int id_prestacion_desde;
	private Prestacion prestacion;
	private String codigo;
	private TipoNomenclador tipoNomenclador;
	private Prestacion prestacionDesde;
	private String codigoDesde;
	private Prestacion prestacionHasta;
	private String codigoHasta;
//	private String descripcion_prestacion_desde;
//	private int id_prestacion_hasta;
//	private String descripcion_prestacion_hasta;
	private int idPlan; // --todos los id_plan mas la opci�n 'todos' que es 0
	private String planDescripcion;
//	private int idCartilla;
	private BigDecimal coseguro;
	private String tipoValorizacion;
//	private BigDecimal honorarios;
//	private BigDecimal gastos;
//	private BigDecimal importeTotal;
	private BigDecimal importe;
	private BigDecimal porcentaje;
	private String servicio;
	private Date altaFecha;
	private String altaUsr;
	private Date modiFecha;
	private String modiUsr;
	private Date bajaFecha;
	private String bajaUsr;

	private ESTADOS estado;

	public ConvenioPrestacionalDetalle(String line) {
	}

	public enum ESTADOS {
		NUEVO, MODIF, BAJA
	}

	public ConvenioPrestacionalDetalle() {
		super();
	}

	public ConvenioPrestacionalDetalle(
			int id,
			int idConvenioPrestacional,
			Date fechaDesde,
			Date fechaHasta,
			Prestacion prestacion,
			String codigo,
			int idPlan,
			String planDescripcion,
			BigDecimal coseguro,
			String tipoValorizacion,
			BigDecimal importe,
			BigDecimal porcentaje,
			String servicio) {

		this.id = id;
		this.idConvenioPrestacional = idConvenioPrestacional;
		this.fechaDesde = fechaDesde;
		this.fechaHasta = fechaHasta;
		this.prestacion = prestacion;
		this.codigo = codigo;
		this.idPlan = idPlan;
		this.planDescripcion = planDescripcion;
		this.coseguro = coseguro;
		this.tipoValorizacion = tipoValorizacion;
		this.importe = importe;
		this.porcentaje = porcentaje;
		this.servicio = servicio;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(BigDecimal porcentaje) {
		this.porcentaje = porcentaje;
	}

	public static ConvenioPrestacionalDetalle getMapping(ResultSet rs, String prefix) throws SQLException {

		Prestacion prestacion = new Prestacion(
				rs.getInt(prefix + "id_prestacion"),
				rs.getString(prefix + "prestacion_descripcion")
		);

		ConvenioPrestacionalDetalle det = new ConvenioPrestacionalDetalle();
		det.setIdConvenioPrestacional(rs.getInt(prefix + "id_convenio_prest"));
		det.setId(rs.getInt(prefix + "id_convenio_prest_detalle"));

		det.setFechaDesde(rs.getTimestamp(prefix + "fecha_desde"));
		det.setFechaHasta(rs.getTimestamp(prefix + "fecha_hasta"));

		det.setPrestacion(prestacion);
		det.setCodigo(rs.getString(prefix + "codigo"));
		det.setPrestacionDesde(prestacion);
		det.setCodigoDesde(det.getCodigo());	
		det.setPrestacionHasta(prestacion);
		det.setCodigoHasta(det.getCodigo());
		try {
			det.setTipoNomenclador(TipoNomenclador.getMapping(prefix + "tpno_", rs));
		} catch (SQLException ignored) {
			// compatibilidad con consultas que no incluyen tipo de nomenclador
		}

		det.setIdPlan(rs.getInt(prefix + "id_plan"));
		det.setPlanDescripcion(rs.getString(prefix + "plan_descripcion"));
		det.setCoseguro(rs.getBigDecimal(prefix + "coseguro"));
		det.setTipoValorizacion(rs.getString(prefix + "tipo_valorizacion"));
		det.setImporte(rs.getBigDecimal(prefix + "importe"));
		det.setPorcentaje(rs.getBigDecimal(prefix + "porcentaje"));
		det.setServicio(rs.getString(prefix + "servicio"));

		det.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
		det.setAltaUsr(rs.getString(prefix + "alta_usr"));
		det.setModiFecha(rs.getTimestamp(prefix + "modi_fecha"));
		det.setModiUsr(rs.getString(prefix + "modi_usr"));
		det.setBajaFecha(rs.getTimestamp(prefix + "baja_fecha"));
		det.setBajaUsr(rs.getString(prefix + "baja_usr"));

		return det;
	}

	public String getPlanDescripcion() {
		return planDescripcion;
	}

	public void setPlanDescripcion(String planDescripcion) {
		this.planDescripcion = planDescripcion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdConvenioPrestacional() {
		return idConvenioPrestacional;
	}

	public void setIdConvenioPrestacional(int idConvenioPrestacional) {
		this.idConvenioPrestacional = idConvenioPrestacional;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public Prestacion getPrestacion() {
		return prestacion;
	}

	public void setPrestacion(Prestacion prestacion) {
		this.prestacion = prestacion;
		if (this.prestacionDesde == null) {
			this.prestacionDesde = prestacion;
		}
		if (this.prestacionHasta == null) {
			this.prestacionHasta = prestacion;
		}
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
	    this.codigo = codigo;

	    if (this.codigoDesde == null) {
	        this.codigoDesde = codigo;
	    }

	    if (this.codigoHasta == null) {
	        this.codigoHasta = codigo;
	    }
	}

	public TipoNomenclador getTipoNomenclador() {
		return tipoNomenclador;
	}

	public void setTipoNomenclador(TipoNomenclador tipoNomenclador) {
		this.tipoNomenclador = tipoNomenclador;
	}

	public Prestacion getPrestacionDesde() {
		return prestacionDesde != null ? prestacionDesde : prestacion;
	}

	public void setPrestacionDesde(Prestacion prestacionDesde) {
		this.prestacionDesde = prestacionDesde;
		if (this.prestacion == null) {
			this.prestacion = prestacionDesde;
		}
	}

	public String getCodigoDesde() {
		return codigoDesde != null ? codigoDesde : codigo;
	}

	public void setCodigoDesde(String codigoDesde) {
		this.codigoDesde = codigoDesde;
		if (this.codigo == null) {
			this.codigo = codigoDesde;
		}
	}

	public Prestacion getPrestacionHasta() {
		return prestacionHasta != null ? prestacionHasta : prestacion;
	}

	public void setPrestacionHasta(Prestacion prestacionHasta) {
		this.prestacionHasta = prestacionHasta;
	}

	public String getCodigoHasta() {
		return codigoHasta != null ? codigoHasta : codigo;
	}

	public void setCodigoHasta(String codigoHasta) {
		this.codigoHasta = codigoHasta;
	}

	public int getIdPlan() {
		return idPlan;
	}

	public void setIdPlan(int idPlan) {
		this.idPlan = idPlan;
	}

	public BigDecimal getCoseguro() {
		return coseguro;
	}

	public void setCoseguro(BigDecimal coseguro) {
		this.coseguro = coseguro;
	}

	public String getTipoValorizacion() {
		return tipoValorizacion;
	}

	public void setTipoValorizacion(String tipoValorizacion) {
		this.tipoValorizacion = tipoValorizacion;
	}

	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	public Date getAltaFecha() {
		return altaFecha;
	}

	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}

	public String getAltaUsr() {
		return altaUsr;
	}

	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}

	public Date getModiFecha() {
		return modiFecha;
	}

	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}

	public String getModiUsr() {
		return modiUsr;
	}

	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}

	public Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

	public String getBajaUsr() {
		return bajaUsr;
	}

	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}


	@Override
	public String toString() {
		return "ConvenioPrestacionalDetalle [id=" + id
				+ ", fechaDesde=" + fechaDesde
				+ ", fechaHasta=" + fechaHasta
				+ ", codigo=" + codigo
				+ ", idPlan=" + idPlan
				+ ", planDescripcion=" + planDescripcion
				+ ", importe=" + importe
				+ ", porcentaje=" + porcentaje
				+ ", servicio=" + servicio + "]";
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
		ConvenioPrestacionalDetalle other = (ConvenioPrestacionalDetalle) obj;
		if (id != other.id)
			return false;
		return true;
	}

}