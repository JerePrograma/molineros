package ar.com.ospim.hoteles.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;

public class Reserva  implements Serializable {
	
	private static final long serialVersionUID = -7203000919326412541L;
	
	
	private String idHabitacion;
	private Integer idReserva;
	private Date fechaDesde;
	private Date fechaHasta;
	private String apellido;
	private String nombre;
	private String documento;
	private Integer idCliente;
	private Integer anio;
	private Double pagado;
	private Double totalAPagar;
	private Double totalCochera;
	private Integer fechaDesdeId;
	private Integer fechaHastaId;
	private Double senia;
	
	public String getIdHabitacion() {
		return idHabitacion;
	}

	public void setIdHabitacion(String idHabitacion) {
		this.idHabitacion = idHabitacion;
	}

	public Integer getIdReserva() {
		return idReserva;
	}

	public void setIdReserva(Integer idReserva) {
		this.idReserva = idReserva;
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
	
	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public Integer getAnio() {
		return anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	
	public Double getPagado() {
		return pagado;
	}

	public void setPagado(Double pagado) {
		this.pagado = pagado;
	}

	public Integer getFechaDesdeId() {
		return fechaDesdeId;
	}

	public void setFechaDesdeId(Integer fechaDesdeId) {
		this.fechaDesdeId = fechaDesdeId;
	}

	public Integer getFechaHastaId() {
		return fechaHastaId;
	}

	public void setFechaHastaId(Integer fechaHastaId) {
		this.fechaHastaId = fechaHastaId;
	}
	
	public Double getTotalAPagar() {
		return totalAPagar;
	}

	public void setTotalAPagar(Double totalAPagar) {
		this.totalAPagar = totalAPagar;
	}

	public Double getTotalCochera() {
		return totalCochera;
	}

	public void setTotalCochera(Double totalCochera) {
		this.totalCochera = totalCochera;
	}

	
	public Double getSenia() {
		return senia;
	}

	public void setSenia(Double senia) {
		this.senia = senia;
	}
	
	public static Reserva  getMapping(ResultSet rs, String prefix) throws Exception {
		Reserva hab  = new Reserva();

		  
	    hab.setIdHabitacion(rs.getString(prefix+"habitacion_id"));
		hab.setApellido(rs.getString(prefix+"apellido"));
		hab.setFechaDesde(rs.getDate(prefix + "fecha_desde"));
		hab.setFechaHasta(rs.getDate(prefix + "fecha_hasta"));
		hab.setIdCliente(rs.getInt(prefix + "cliente_id"));
		hab.setIdReserva(rs.getInt(prefix + "reserva_id"));
		hab.setNombre(rs.getString(prefix+"nombre"));
		hab.setDocumento(rs.getString(prefix+"documento"));
		
		return hab ;
   }
	
	
}
