package ar.com.ospim.autorizaciones.beans;


import java.sql.ResultSet;

public class ReporteIntegracionReclamo  {


	private Integer idReclamo;
	private String apellido;
	private String nombre;
	private String sexo;
	private String tipoDocu;
	private String docuNumero;
    
	//inicio datos de la prestacion
	private String codigo;
	private String nombrePrestacion;
	private double cantidad;
	private double importe;
	private double total;
    //fin datos de la prestacion
	
    
    //Inicio datos del comprobante
  	private String comprobanteTipo;
  	private String comprobanteNro;
  	private java.util.Date  comprobanteFecha;
  	private Double comprobanteCantidad;
  	private Double comprobanteImporte;
  	private Double comprobanteTotal;
  	private String comprobanteCUIT;
  	private String comprobanteCUITSucursal;
  	private String comprobanteSucursal;
  	private String comprobanteRazonSocial;
  	private String comprobanteLetra;
    //fin datos del comprobante

  	private String descIntegracion;

    private String estado;
	
    
    double cargoEnsalud;
    
  	
  	
	public Integer getIdReclamo() {
		return idReclamo;
	}
	public void setIdReclamo(Integer idReclamo) {
		this.idReclamo = idReclamo;
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
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getTipoDocu() {
		return tipoDocu;
	}
	public void setTipoDocu(String tipoDocu) {
		this.tipoDocu = tipoDocu;
	}
	public String getDocuNumero() {
		return docuNumero;
	}
	public void setDocuNumero(String docuNumero) {
		this.docuNumero = docuNumero;
	}
	
	
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}


	public String getCodigo() {
		return codigo;
	}
	public String getNombrePrestacion() {
		return nombrePrestacion;
	}
	public double getCantidad() {
		return cantidad;
	}
	public double getImporte() {
		return importe;
	}
	public double getTotal() {
		return total;
	}
	public double getCargoEnsalud() {
		return cargoEnsalud;
	}
	public String getComprobanteTipo() {
		return comprobanteTipo;
	}
	public String getComprobanteNro() {
		return comprobanteNro;
	}
	public java.util.Date getComprobanteFecha() {
		return comprobanteFecha;
	}
	public Double getComprobanteCantidad() {
		return comprobanteCantidad;
	}
	public Double getComprobanteImporte() {
		return comprobanteImporte;
	}
	public Double getComprobanteTotal() {
		return comprobanteTotal;
	}
	public String getComprobanteCUIT() {
		return comprobanteCUIT;
	}
	public String getComprobanteCUITSucursal() {
		return comprobanteCUITSucursal;
	}
	public String getComprobanteSucursal() {
		return comprobanteSucursal;
	}
	public String getComprobanteRazonSocial() {
		return comprobanteRazonSocial;
	}
	public String getComprobanteLetra() {
		return comprobanteLetra;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public void setNombrePrestacion(String nombrePrestacion) {
		this.nombrePrestacion = nombrePrestacion;
	}
	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public void setCargoEnsalud(double cargoEnsalud) {
		this.cargoEnsalud = cargoEnsalud;
	}
	public void setComprobanteTipo(String comprobanteTipo) {
		this.comprobanteTipo = comprobanteTipo;
	}
	public void setComprobanteNro(String comprobanteNro) {
		this.comprobanteNro = comprobanteNro;
	}
	public void setComprobanteFecha(java.util.Date comprobanteFecha) {
		this.comprobanteFecha = comprobanteFecha;
	}
	public void setComprobanteCantidad(Double comprobanteCantidad) {
		this.comprobanteCantidad = comprobanteCantidad;
	}
	public void setComprobanteImporte(Double comprobanteImporte) {
		this.comprobanteImporte = comprobanteImporte;
	}
	public void setComprobanteTotal(Double comprobanteTotal) {
		this.comprobanteTotal = comprobanteTotal;
	}
	public void setComprobanteCUIT(String comprobanteCUIT) {
		this.comprobanteCUIT = comprobanteCUIT;
	}
	public void setComprobanteCUITSucursal(String comprobanteCUITSucursal) {
		this.comprobanteCUITSucursal = comprobanteCUITSucursal;
	}
	public void setComprobanteSucursal(String comprobanteSucursal) {
		this.comprobanteSucursal = comprobanteSucursal;
	}
	public void setComprobanteRazonSocial(String comprobanteRazonSocial) {
		this.comprobanteRazonSocial = comprobanteRazonSocial;
	}
	public void setComprobanteLetra(String comprobanteLetra) {
		this.comprobanteLetra = comprobanteLetra;
	}
	public static ReporteIntegracionReclamo getMappingReporte(ResultSet rs, String prefix) throws Exception {
		

		ReporteIntegracionReclamo reclamo = new ReporteIntegracionReclamo();
	
		try{		
			
			reclamo.setIdReclamo(rs.getInt(prefix + "id_reclamo"));
			reclamo.setApellido(rs.getString(prefix + "apellido"));
			reclamo.setNombre(rs.getString(prefix + "nombre"));
			reclamo.setSexo(rs.getString(prefix + "sexo"));    
			reclamo.setTipoDocu(rs.getString(prefix + "tipo_docu"));
			reclamo.setDocuNumero(rs.getString(prefix + "docu_numero"));
			reclamo.setCodigo(rs.getString(prefix + "codigo"));
			reclamo.setNombrePrestacion(rs.getString(prefix + "nombre_prestacion")); 
			reclamo.setCantidad(rs.getDouble(prefix + "cantidad"));
			reclamo.setImporte(rs.getDouble(prefix + "importe"));
			reclamo.setTotal(rs.getDouble(prefix + "total"));
			reclamo.setComprobanteTipo(rs.getString(prefix + "comprobanteTipo"));
			reclamo.setComprobanteNro(rs.getString(prefix + "comprobanteNro"));
			reclamo.setComprobanteFecha(rs.getDate(prefix + "comprobanteFecha"));
			reclamo.setComprobanteCantidad(rs.getDouble(prefix + "comprobanteCantidad")); 
			reclamo.setComprobanteImporte(rs.getDouble(prefix + "comprobanteImporte"));
			reclamo.setComprobanteTotal(rs.getDouble(prefix + "comprobanteTotal"));
			reclamo.setComprobanteCUIT(rs.getString(prefix + "comprobanteCUIT"));
			reclamo.setComprobanteCUITSucursal(rs.getString(prefix + "comprobanteCUITSucursal"));
			reclamo.setComprobanteSucursal(rs.getString(prefix + "comprobanteSucursal"));
			reclamo.setComprobanteRazonSocial(rs.getString(prefix + "comprobanteRazonSocial"));
			reclamo.setComprobanteLetra(rs.getString(prefix + "comprobanteLetra"));
			reclamo.setEstado(rs.getString(prefix + "estado"));
			reclamo.setCargoEnsalud(rs.getDouble(prefix + "cargoEnsalud"));
			reclamo.setDescIntegracion(rs.getString(prefix + "integracion"));


			
		}
		catch (Exception e ){
		    throw e;
		}
				
		return reclamo;

	}
	public String getDescIntegracion() {
		return descIntegracion;
	}
	public void setDescIntegracion(String descIntegracion) {
		this.descIntegracion = descIntegracion;
	}
	
	
}
