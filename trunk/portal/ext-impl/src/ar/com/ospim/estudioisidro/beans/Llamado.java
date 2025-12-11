package ar.com.ospim.estudioisidro.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.liferay.ibm.icu.text.SimpleDateFormat;

public class Llamado implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -72433900083816554L;
	
	private int id;
	private Date fecha;
	private String user;
	private String cuit;
	private String observaciones;
	private String tipoContacto;
	private int cantidadTotal;
	private String estado;
	private String ubicacionCarpeta;
	private Date fechaAgenda;
	private String cartaDocumento;
	private String googleEvent;
	private EstadoGestion estadoGestion;
	private Integer lote;
	private String tipoLote;
	private Double deuda;
	private Double deudaLote;
	
	
	public Llamado() {
	}
	
	public Llamado(String cuit, String user, Date fecha, String observaciones) {
		this.cuit = cuit;
		this.user = user;
		this.fecha = fecha;
		this.observaciones = observaciones;
	}

		
	public static Llamado getMapping(ResultSet rs)	throws SQLException {
			Llamado llamado = new Llamado();
			llamado.setCuit(rs.getString("cuit"));
			llamado.setUser(rs.getString("usuario"));
			llamado.setObservaciones(rs.getString("observaciones"));
			llamado.setFecha(rs.getTimestamp("fecha"));
			llamado.setTipoContacto(rs.getString("tipo_contacto"));
			llamado.setCantidadTotal(rs.getInt("cantidad_llamados"));
			llamado.setEstado(rs.getString("estado"));
			llamado.setEstadoGestion(EstadoGestion.getMapping("estado_", rs));
			llamado.setUbicacionCarpeta(rs.getString("ubicacion_carpeta"));
			llamado.setFechaAgenda(rs.getTimestamp("fecha_agenda"));
			llamado.setCartaDocumento(rs.getString("carta_doc"));
			llamado.setId(rs.getInt("id"));
			llamado.setGoogleEvent(rs.getString("id_event"));
			String numLote = rs.getString("lote");
			try{
				llamado.setLote(Integer.parseInt(numLote));
				llamado.setDeudaLote(rs.getDouble("deuda_lote"));
			}catch(NumberFormatException e){
				llamado.setLote(null);
				llamado.setDeudaLote(0D);
			}
//			llamado.setLote(rs.getInt("lote"));
			llamado.setTipoLote(rs.getString("tipo_lote"));
			return llamado;
		
	}

	public String getFechaAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sdf.format(fecha);
	}
	
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getTipoContacto() {
		return tipoContacto;
	}

	public void setTipoContacto(String tipoContacto) {
		this.tipoContacto = tipoContacto;
	}

	public int getCantidadTotal() {
		return cantidadTotal;
	}

	public void setCantidadTotal(int cantidadTotal) {
		this.cantidadTotal = cantidadTotal;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getUbicacionCarpeta() {
		return ubicacionCarpeta;
	}

	public void setUbicacionCarpeta(String ubicacionCarpeta) {
		this.ubicacionCarpeta = ubicacionCarpeta;
	}

	public Date getFechaAgenda() {
		return fechaAgenda;
	}

	public void setFechaAgenda(Date fechaAgenda) {
		this.fechaAgenda = fechaAgenda;
	}
	
	public String getFechaAgendaAsString() {
		if(null!=this.fechaAgenda){
			SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return sdf.format(this.fechaAgenda);
		}else{
			return "";
		}
	}

	public String getCartaDocumento() {
		return cartaDocumento;
	}

	public void setCartaDocumento(String cartaDocumento) {
		this.cartaDocumento = cartaDocumento;
	}

	public String getGoogleEvent() {
		return googleEvent;
	}

	public void setGoogleEvent(String googleEvent) {
		this.googleEvent = googleEvent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EstadoGestion getEstadoGestion() {
		return estadoGestion;
	}

	public void setEstadoGestion(EstadoGestion estadoGestion) {
		this.estadoGestion = estadoGestion;
	}

	public Integer getLote() {
		return lote;
	}

	public void setLote(Integer lote) {
		this.lote = lote;
	}

	public String getTipoLote() {
		return tipoLote;
	}

	public void setTipoLote(String tipoLote) {
		this.tipoLote = tipoLote;
	}
	
		
	public Double getDeuda() {
		return deuda;
	}

	public void setDeuda(Double deuda) {
		this.deuda = deuda;
	}

	
	public Double getDeudaLote() {
		return deudaLote;
	}

	public void setDeudaLote(Double deudaLote) {
		this.deudaLote = deudaLote;
	}

	public Llamado(String line) {
		String[] vLine = line.split(";");
		cuit=vLine[0];
		lote=Integer.parseInt(vLine[1]);
		tipoLote = vLine[2];
		deuda = Double.parseDouble(vLine[3]);
	}
	
}
