package ar.com.ospim.autorizaciones.beans;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.sun.star.bridge.oleautomation.Date;



//import ar.com.ospim.liquidaciones.beans.ProfesionPrestador.ESTADOS;

public class PrestacionesEquipoInterdisciplinario implements Serializable {
	
	private static final long serialVersionUID = -753007388444001315L;
	private int idEquipoInterdisciplinario; // referencia a la pk de tabla reclamos prestacionales 
	private int idPrestacionEquipo; // referencia a la pk de tabla de prestaciones de los reclamos 
	
	private int idPrestacion ;
	private int idPrestacionRecord ;
	private int tipoPrestacion; 
	private double importe;
	private double total ;
	private String Codigo;
	private int cantidad ;
	
	
	private Date altaFecha;
	private String altaUsr;
	private String descripcion; 	 
	int idRegistro;
	private String nombrePrestacion;	
    private int opcionPrestacion ;
    private String detalleTipoPrestacion ;
		
	private ESTADOS estado;
	
	public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};
	
	

	public PrestacionesEquipoInterdisciplinario (){
		super();
	}
	
 public PrestacionesEquipoInterdisciplinario(double total ,int cantidad , double importe, int idPrestacion,   String nombrePrestacion , String prestacion ,int idOpcionPrestacion,String detalleTipoPrestacion) {
		
		super();
		this.importe = importe;
		this.total = total;
		this.idPrestacion =idPrestacion;
		this.cantidad = cantidad ;	
		this.setCodigo_Prestacion( prestacion );
		//this.setTipo_Prestacion(8); // DISCAPACIDAD CAMBIO EL CRITERIO  
		this.setNombreprestacion(nombrePrestacion);
		this.settipoPrestacion(idOpcionPrestacion);
		this.setTipoPrestacionDetalle(detalleTipoPrestacion);
	}



	public static PrestacionesEquipoInterdisciplinario getMapping(String prefix, ResultSet rs)
			throws SQLException {
		
		PrestacionesEquipoInterdisciplinario  prestacionesequipo = new PrestacionesEquipoInterdisciplinario ();
		
		
		prestacionesequipo.setidEquipoInterdisciplinario(rs.getInt(prefix + "id_equipo_interdisciplinario"));
		prestacionesequipo.setImporte(rs.getDouble(prefix + "importe"));
		prestacionesequipo.setTotal(rs.getInt(prefix + "total"));
		prestacionesequipo.setCantidad(rs.getInt(prefix + "cantidad"));
		prestacionesequipo.setId_prestacion(rs.getInt(prefix + "id_prestacion"));
		prestacionesequipo.setNombreprestacion(rs.getString(prefix + "nombreprestacion"));
		prestacionesequipo.setDescripcion(rs.getString(prefix + "nombreprestacion"));
		prestacionesequipo.setId_prestacionrecord(rs.getInt(prefix + "id_prestacionrecord"));
		prestacionesequipo.setCodigo_Prestacion(rs.getString(prefix + "codigo_prestacion"));
		prestacionesequipo.settipoPrestacion(rs.getInt(prefix + "tipo_prestacion"));
		prestacionesequipo.setTipoPrestacionDetalle(rs.getString(prefix + "detalle_tipo_prestacion"));
		
		if ( rs.getString(prefix + "baja_fecha") != null) {
			prestacionesequipo.setEstado( PrestacionesEquipoInterdisciplinario.ESTADOS.BAJA);		
			}			
		
			prestacionesequipo.setIdregistro(prestacionesequipo.getId_prestacion());
		
		
		return prestacionesequipo ;
	}
	
	public static PrestacionesEquipoInterdisciplinario getMapping_1(String prefix, ResultSet rs)
			throws SQLException {
		
		
		PrestacionesEquipoInterdisciplinario  prestacionesequipo = new PrestacionesEquipoInterdisciplinario ();
		
		prestacionesequipo.setidEquipoInterdisciplinario(rs.getInt(prefix + "id_reclamo_prestacional"));
		prestacionesequipo.setidPrestacionEquipo (rs.getInt(prefix + "id_prestacion_reclamo"));
		prestacionesequipo.setidEquipoInterdisciplinario(rs.getInt(prefix + "id_equipo_interdisciplinario"));
		prestacionesequipo.setImporte(rs.getDouble(prefix + "importe"));
		prestacionesequipo.setTotal(rs.getInt(prefix + "total"));
		prestacionesequipo.setCantidad(rs.getInt(prefix + "cantidad"));
		prestacionesequipo.setId_prestacion(rs.getInt(prefix + "id_prestacion"));
		prestacionesequipo.setNombreprestacion(rs.getString(prefix + "nombreprestacion"));
		prestacionesequipo.setDescripcion(rs.getString(prefix + "nombreprestacion"));
		prestacionesequipo.setId_prestacionrecord(rs.getInt(prefix + "id_prestacionrecord"));
		prestacionesequipo.setCodigo_Prestacion(rs.getString(prefix + "codigo_prestacion"));
		
		
		if ( rs.getString(prefix + "baja_fecha") != null) {
			prestacionesequipo.setEstado( PrestacionesEquipoInterdisciplinario.ESTADOS.BAJA  );		
			}			
		
		prestacionesequipo.setIdregistro(prestacionesequipo.getId_prestacion());
		
		
		
		return prestacionesequipo ;
	}
	
	
	public static PrestacionesEquipoInterdisciplinario getMappingPretacionDelEquipo(String prefix, ResultSet rs)
			throws SQLException {
		
		PrestacionesEquipoInterdisciplinario  prestacionesequipo = new PrestacionesEquipoInterdisciplinario();
		
		
		prestacionesequipo.setidEquipoInterdisciplinario(rs.getInt(prefix + "id_reclamo_prestacional"));
		prestacionesequipo.setidPrestacionEquipo (rs.getInt(prefix + "id_prestacion_reclamo"));
		prestacionesequipo.setidEquipoInterdisciplinario(rs.getInt(prefix + "id_equipo_interdisciplinario"));
		prestacionesequipo.setImporte(rs.getDouble(prefix + "importe"));
		prestacionesequipo.setTotal(rs.getInt(prefix + "total"));
		prestacionesequipo.setCantidad(rs.getInt(prefix + "cantidad"));
		prestacionesequipo.setId_prestacion(rs.getInt(prefix + "id_prestacion"));
		prestacionesequipo.setNombreprestacion(rs.getString(prefix + "nombreprestacion"));
		prestacionesequipo.setDescripcion(rs.getString(prefix + "nombreprestacion"));
		prestacionesequipo.setId_prestacionrecord(rs.getInt(prefix + "id_prestacionrecord"));
		prestacionesequipo.setCodigo_Prestacion(rs.getString(prefix + "codigo_prestacion"));
		

		return prestacionesequipo ;
	}

	

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "ProfesionPrestador [id_prestacion =" + idPrestacion 
				+ ", descripcion=" + descripcion + ", importe="
				+ importe+ ", cantidad="
				+ cantidad + "]";
	}

	
	
	
	public int getidPrestacionEquipo () {
		return idPrestacionEquipo;
	}

	public void setidPrestacionEquipo (int idPrestacionEquipo ) {
		this.idPrestacionEquipo = idPrestacionEquipo ;
	}

	
	public int getidEquipoInterdisciplinario() {
		return idEquipoInterdisciplinario;
	}

	public void setidEquipoInterdisciplinario(int idEquipoInterdisciplinario) {
		this.idEquipoInterdisciplinario = idEquipoInterdisciplinario;
	}

	

	public int getId_prestacion() {
		return idPrestacion;
	}

	public void setId_prestacion(int idPrestacion) {
		this.idPrestacion = idPrestacion;
	}

	public int getId_prestacionrecord() {
		return idPrestacionRecord ;
	}

	public void setId_prestacionrecord(int idPrestacion) {
		this.idPrestacionRecord = idPrestacion;
	}

	public double getImporte() {
		return importe;
	}

	public String getImporteString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatter.format(importe);
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	

	public double getTotal () {
		return total ;
	}
	
	public String getTotalString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatter.format(total );
	}
	
	
	public void setTotal (double total ) {
		this.total = total ;
	}
	
	
	public Date getAlta_fecha() {
		return altaFecha;
	}

	public void setAlta_fecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}

	public String getAlta_usr() {
		return altaUsr;
	}

	public void setAlta_usr(String altaUsr) {
		this.altaUsr = altaUsr;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public int getIdregistro() {
		return idRegistro;
	}
	public String getIdregistroString() {		
		return Integer.toString(idRegistro) ;
	}

	public String getIdprestacionString() {		
		return Integer.toString(idPrestacion) ;
	}
	
		
	public void setIdregistro(int idRegistro) {
		this.idRegistro = idRegistro;
	}
	
	public String getCodigo_Prestacion() {
		return Codigo  ;
	}
	public void  setCodigo_Prestacion(String CodigoPrestacion) {
		this.Codigo  =CodigoPrestacion;
	}
	
	public int getTipo_Prestacion() {
		return tipoPrestacion;
	}
	public void  setTipo_Prestacion(int tipoPrestacion) {
		this.tipoPrestacion=tipoPrestacion ;
	}	

	
	public int gettipoPrestacion() {
		return opcionPrestacion ;
	}
	public void  settipoPrestacion(int opcionPrestacion ) {
		this.opcionPrestacion =opcionPrestacion ;
	}
	

	public String getTipoPrestacionDetalle() {
		return detalleTipoPrestacion ;
	}
	public void  setTipoPrestacionDetalle(String detalleTipoPrestacion ) {
		this.detalleTipoPrestacion  =detalleTipoPrestacion ;
	}
	
	
	
	public String getNombreprestacion() {
		return nombrePrestacion;
	}

	

	
	public void setNombreprestacion(String nombrePrestacion) {
		this.nombrePrestacion = nombrePrestacion;
		this.descripcion= nombrePrestacion;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrestacionesEquipoInterdisciplinario other = (PrestacionesEquipoInterdisciplinario) obj;
		if (idRegistro != other.idRegistro)
			return false;
		return true;
	}
	

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getCantidadString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatter.format(cantidad);
	}
	
}


