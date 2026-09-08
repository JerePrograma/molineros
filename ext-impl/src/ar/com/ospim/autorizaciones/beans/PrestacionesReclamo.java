package ar.com.ospim.autorizaciones.beans;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import com.liferay.portal.kernel.util.Validator;
import com.sun.star.bridge.oleautomation.Date;

import ar.com.ospim.liquidaciones.beans.EspecialidadPrestador;

//import ar.com.ospim.liquidaciones.beans.ProfesionPrestador.ESTADOS;

public class PrestacionesReclamo implements Serializable {
	
	private static final long serialVersionUID = -753007388444001315L;
	private int idReclamoPrestacional; // referencia a la pk de tabla reclamos prestacionales 
	private int idPrestacionReclamo; // referencia a la pk de tabla de prestaciones de los reclamos 
	private int idMedicamento;
	private int idPrestacion ;
	private int idPrestacionRecord ;
	private int tipoPrestacion; 
	private double importe;
	private double cantidad ;
	private String frecuencia;
	private double cargoOspim;
	private Boolean recuperableSur ;
	private String Codigo;
	
	private String opDePago;
	
	private double cargoPs;
	private String observaciones;
	private String observacionesAutorizaRechaza;
	
	private Date altaFecha;
	private String altaUsr;
	private java.util.Date  bajaFecha;
	private String descripcion; 	 
	private int idRegistro;
	private String nombreMedicacion;
	private String nombrePrestacion;	
		
    private int estadoRechazoAprobado ;	
	private List<EspecialidadPrestador> especialidades;
	
	private String cuilTitular;
	private int inte;
	
	private ESTADOS estado;
	
	//datos del comprobante
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
	
	private java.util.Date  fechaPrestacion;
	
	//Datos de la cuenta
	private String  cbu;
	private String cuilCuenta;
	private String emailCuenta;
	private String apellidoCuenta;
	private String nombreCuenta;
	private double importeTopePlan;
	private Double reconocidoSSS;
	
	private Integer recuperable;  //1 - SUR || 3 - Integración || 2 -- No Recuperable
	
	private double cargoImesa;
	
	private String idTercerizadora;
	
	public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};
	
	
	public PrestacionesReclamo (){
		super();
	}
	
	public PrestacionesReclamo(String observaciones , String frecuencia,double cargOps,double importe,double  cargoOspim , int idPrestacion, int idMedicamento,  int tipoPrestacion, String nombreMedicacion ,String nombrePrestacion,Boolean recuperableSur , int cantidad) {
		
		super();
		this.frecuencia = frecuencia;
		this.observaciones = observaciones ;
		this.importe = importe;
		this.cargoOspim = cargoOspim;
		this.cargoPs= cargOps;
		this.idPrestacion =idPrestacion;
		this.idMedicamento=idMedicamento;		
		this.setTipoPrestacion(tipoPrestacion);
		this.setNombremedicacion(nombreMedicacion );
		this.setNombreprestacion(nombrePrestacion);
		this.setRecuperableSur(recuperableSur);
		this.cantidad = cantidad; 
		if (nombreMedicacion =="")
		{
			this.descripcion=nombrePrestacion;
		}else		{
			this.descripcion=nombreMedicacion ;
		}
	}


    public PrestacionesReclamo(String observaciones , String frecuencia,double cargOps,double importe,double  cargoOspim , int idPrestacion, int idMedicamento, 
    		int tipoPrestacion, String nombreMedicacion ,String nombrePrestacion,Boolean recuperableSur , double cantidad,
    		String comprobanteTipo,String comprobanteNro, java.util.Date comprobanteFecha, Double comprobanteCantidad, Double comprobanteImporte,
	        Double comprobanteTotal,String cuit,String sucursal,String cuitSucursal, String comprobanteLetra, java.util.Date fechaPrestacion,
	        double importeTopePlan) {
		
		super();
		this.frecuencia = frecuencia;
		this.observaciones = observaciones ;
		this.importe = importe;
		this.cargoOspim = cargoOspim;
		this.cargoPs= cargOps;
		this.idPrestacion =idPrestacion;
		this.idMedicamento=idMedicamento;		
		this.setTipoPrestacion(tipoPrestacion);
		this.setNombremedicacion(nombreMedicacion );
		this.setNombreprestacion(nombrePrestacion);
		this.setRecuperableSur(recuperableSur);
		this.cantidad = cantidad; 
		if (nombreMedicacion =="")
		{
			this.descripcion=nombrePrestacion;
		}else		{
			this.descripcion=nombreMedicacion ;
		}
		
		
		this.comprobanteTipo=comprobanteTipo;
		this.comprobanteNro=comprobanteNro;
		if(comprobanteFecha!=null) {
			this.comprobanteFecha=comprobanteFecha;
		}
		this.comprobanteCantidad =comprobanteCantidad;
		this.comprobanteImporte=comprobanteImporte;
        this.comprobanteTotal=comprobanteTotal;
        this.comprobanteCUIT=cuit;
        this.comprobanteSucursal=sucursal;
        this.comprobanteCUITSucursal=cuitSucursal;
        this.comprobanteLetra = comprobanteLetra;
        this.fechaPrestacion = fechaPrestacion;
		this.importeTopePlan = importeTopePlan;
	}   


    
    public PrestacionesReclamo(String observaciones , String frecuencia,double cargOps,double importe,double  cargoOspim , int idPrestacion, int idMedicamento, 
    		int tipoPrestacion, String nombreMedicacion ,String nombrePrestacion,Boolean recuperableSur , double cantidad,
    		String comprobanteTipo,String comprobanteNro, java.util.Date comprobanteFecha, Double comprobanteCantidad, Double comprobanteImporte,
	        Double comprobanteTotal,String cuit,String sucursal,String cuitSucursal, String comprobanteLetra, java.util.Date fechaPrestacion,
	        double importeTopePlan,Double cargoImesa) {
		
		super();
		this.frecuencia = frecuencia;
		this.observaciones = observaciones ;
		this.importe = importe;
		this.cargoOspim = cargoOspim;
		this.cargoPs= cargOps;
		this.cargoImesa= cargoImesa;
		this.idPrestacion =idPrestacion;
		this.idMedicamento=idMedicamento;		
		this.setTipoPrestacion(tipoPrestacion);
		this.setNombremedicacion(nombreMedicacion );
		this.setNombreprestacion(nombrePrestacion);
		this.setRecuperableSur(recuperableSur);
		this.cantidad = cantidad; 
		if (nombreMedicacion =="")
		{
			this.descripcion=nombrePrestacion;
		}else		{
			this.descripcion=nombreMedicacion ;
		}
		
		
		this.comprobanteTipo=comprobanteTipo;
		this.comprobanteNro=comprobanteNro;
		if(comprobanteFecha!=null) {
			this.comprobanteFecha=comprobanteFecha;
		}
		this.comprobanteCantidad =comprobanteCantidad;
		this.comprobanteImporte=comprobanteImporte;
        this.comprobanteTotal=comprobanteTotal;
        this.comprobanteCUIT=cuit;
        this.comprobanteSucursal=sucursal;
        this.comprobanteCUITSucursal=cuitSucursal;
        this.comprobanteLetra = comprobanteLetra;
        this.fechaPrestacion = fechaPrestacion;
		this.importeTopePlan = importeTopePlan;
	}   


	public static PrestacionesReclamo getMapping(String prefix, ResultSet rs)
			throws SQLException {
		
		PrestacionesReclamo  prestacionesreclamo = new PrestacionesReclamo ();
		
		
		prestacionesreclamo.setIdreclamoprestacional(rs.getInt(prefix + "id_reclamo_prestacional"));
		prestacionesreclamo.setFrecuencia(rs.getString(prefix + "frecuencia"));
		prestacionesreclamo.setImporte(rs.getDouble(prefix + "importe"));
		prestacionesreclamo.setCargo_ospim(rs.getDouble(prefix + "cargo_ospim"));
		prestacionesreclamo.setCargo_ps(rs.getDouble(prefix + "cargo_ps"));
		prestacionesreclamo.setCargo_imesa(rs.getDouble(prefix + "cargo_imesa"));
		prestacionesreclamo.setId_medicamento(rs.getInt(prefix + "id_medicamento"));
		prestacionesreclamo.setId_prestacion(rs.getInt(prefix + "id_prestacion"));
		prestacionesreclamo.setObservaciones(rs.getString(prefix + "observaciones"));
		prestacionesreclamo.setNombreprestacion(rs.getString(prefix + "nombreprestacion"));
		prestacionesreclamo.setDescripcion(rs.getString(prefix + "nombreprestacion"));
		prestacionesreclamo.setId_prestacionrecord(rs.getInt(prefix + "id_recl_prest_prest"));
		prestacionesreclamo.setEstadoRechazoAprobado(rs.getInt(prefix + "estado_aprobacion"));
		prestacionesreclamo.setCodigoPrestacion(rs.getString(prefix + "codigo_prestacion"));
		prestacionesreclamo.setRecuperable(rs.getInt(prefix + "recuperablesur"));
		prestacionesreclamo.setCantidad(rs.getDouble(prefix + "cantidad"));
		prestacionesreclamo.setOp(rs.getString(prefix + "datoop"));
		prestacionesreclamo.setIdRegistro(rs.getInt(prefix + "id_recl_prest_prest"));
		prestacionesreclamo.setBajaFecha(rs.getDate(prefix + "baja_fecha"));
		prestacionesreclamo.setIdreclamoprestacional(rs.getInt(prefix + "id_recl_prest_prest"));
		prestacionesreclamo.setReconocidoSSS(rs.getDouble(prefix + "reconocido_sss"));
		
		try {
		    prestacionesreclamo.setIdTercerizadora(rs.getString(prefix + "id_tecerizadora"));
		} catch (Exception e) {
		    prestacionesreclamo.setIdTercerizadora(null);
		}
		
		if(rs.getString(prefix + "comprobante_tipo")!=null) {
		  prestacionesreclamo.setComprobanteTipo(rs.getString(prefix + "comprobante_tipo"));
		}
		String comprobanteNro = rs.getString(prefix + "comprobante_nro");
		if(comprobanteNro !=null && !"00000000".equalsIgnoreCase(comprobanteNro)) {
		   prestacionesreclamo.setComprobanteNro(rs.getString(prefix+"comprobante_nro"));
		   prestacionesreclamo.setComprobanteSucursal(rs.getString(prefix + "comprobante_sucursal"));
		}   
		
		if ( rs.getDate(prefix + "comprobante_fecha") != null) {
		 prestacionesreclamo.setComprobanteFecha(rs.getDate(prefix+"comprobante_fecha"));
		}
		prestacionesreclamo.setComprobanteCantidad(rs.getDouble(prefix +"comprobante_cantidad"));
		prestacionesreclamo.setComprobanteImporte(rs.getDouble(prefix +"comprobante_importe"));
		prestacionesreclamo.setComprobanteTotal(rs.getDouble(prefix +"comprobante_total"));
		prestacionesreclamo.setComprobanteCUIT(rs.getString(prefix +"comprobante_cuit"));
		
		
		prestacionesreclamo.setComprobanteCUITSucursal(rs.getString(prefix +"comprobante_cuit_sucursal"));
		prestacionesreclamo.setComprobanteLetra(rs.getString(prefix +"comprobante_letra"));
		
		if ( rs.getDate(prefix + "fecha_prestacion") != null) {
			 prestacionesreclamo.setFechaPrestacion(rs.getDate(prefix + "fecha_prestacion"));
		}
		
		if ( rs.getString(prefix + "baja_fecha") != null) {
			prestacionesreclamo.setEstado( PrestacionesReclamo.ESTADOS.BAJA  );		
		}			
		
		
//		if (prestacionesreclamo.getId_prestacion() > 0) {
//			prestacionesreclamo.setIdRegistro(prestacionesreclamo.getId_prestacion());
//		} else {
//			prestacionesreclamo.setIdRegistro(prestacionesreclamo.getId_medicamento());
//		}		
		
		return prestacionesreclamo ;
	}
	
	public static PrestacionesReclamo getMapping_1(String prefix, ResultSet rs)
			throws SQLException {
		
		PrestacionesReclamo  prestacionesreclamo = new PrestacionesReclamo ();
		
		
		prestacionesreclamo.setIdreclamoprestacional(rs.getInt(prefix + "id_reclamo_prestacional"));
		prestacionesreclamo.setIdprestacionReclamo (rs.getInt(prefix + "id_prestacion_reclamo"));
		//prestacionesreclamo.setIdRegistro(rs.getInt(prefix + "id_prestacion_reclamo"));
		prestacionesreclamo.setFrecuencia(rs.getString(prefix + "frecuencia"));
		prestacionesreclamo.setImporte(rs.getDouble(prefix + "importe"));
		prestacionesreclamo.setCargo_ospim(rs.getDouble(prefix + "cargo_ospim"));
		prestacionesreclamo.setCargo_ps(rs.getDouble(prefix + "cargo_ps"));
		prestacionesreclamo.setCargo_imesa(rs.getDouble(prefix + "cargo_imesa"));
		prestacionesreclamo.setId_medicamento(rs.getInt(prefix + "id_medicamento"));
		prestacionesreclamo.setId_prestacion(rs.getInt(prefix + "id_prestacion"));
		prestacionesreclamo.setObservaciones(rs.getString(prefix + "observaciones"));
		prestacionesreclamo.setNombreprestacion(rs.getString(prefix + "nombreprestacion"));
		prestacionesreclamo.setDescripcion(rs.getString(prefix + "nombreprestacion"));
		prestacionesreclamo.setId_prestacionrecord(rs.getInt(prefix + "id_prestacionrecord"));
		prestacionesreclamo.setEstadoRechazoAprobado(rs.getInt(prefix + "estado_aprobacion"));
		prestacionesreclamo.setCodigoPrestacion(rs.getString(prefix + "codigo_prestacion"));
		
		
		try {
		    prestacionesreclamo.setIdTercerizadora(rs.getString(prefix + "id_tercerizadora"));
		} catch (Exception e) {
		    prestacionesreclamo.setIdTercerizadora(null);
		}
		
		prestacionesreclamo.setCantidad(rs.getDouble(prefix + "cantidad_prestacion"));
		
		
		if(rs.getString(prefix + "comprobante_tipo")!=null) {
			  prestacionesreclamo.setComprobanteTipo(rs.getString(prefix + "comprobante_tipo"));
		}
			
		if(rs.getString(prefix + "comprobante_nro")!=null) {
			   prestacionesreclamo.setComprobanteNro(rs.getString(prefix+"comprobante_nro"));
		}   
			
		if ( rs.getDate(prefix + "comprobante_fecha") != null) {
			 prestacionesreclamo.setComprobanteFecha(rs.getDate(prefix+"comprobante_fecha"));
		}
		
		if ( rs.getDate(prefix + "fecha_prestacion") != null) {
			 prestacionesreclamo.setFechaPrestacion(rs.getDate(prefix+"fecha_prestacion"));
		}
		
		try {
			 prestacionesreclamo.setComprobanteLetra(rs.getString(prefix+"compro_letra"));
		}catch (Exception e) {
		}
	
		
		prestacionesreclamo.setComprobanteCantidad(rs.getDouble(prefix +"comprobante_cantidad"));
		prestacionesreclamo.setComprobanteImporte(rs.getDouble(prefix +"comprobante_importe"));
		prestacionesreclamo.setComprobanteTotal(rs.getDouble(prefix +"comprobante_total"));
		prestacionesreclamo.setComprobanteCUIT(rs.getString(prefix +"comprobante_cuit"));
		prestacionesreclamo.setComprobanteSucursal(rs.getString(prefix + "comprobante_sucursal"));
		prestacionesreclamo.setComprobanteCUITSucursal(rs.getString(prefix +"comprobante_cuit_sucursal"));
		
		prestacionesreclamo.setComprobanteLetra(rs.getString(prefix +"compro_letra"));

		
		try {
			prestacionesreclamo.setCuilTitular(rs.getString(prefix + "cuil_titular"));
			prestacionesreclamo.setInte(rs.getInt(prefix + "inte"));
		}catch (Exception e) {
		}
	
		
		
		if ( rs.getString(prefix + "baja_fecha") != null) {
			prestacionesreclamo.setEstado( PrestacionesReclamo.ESTADOS.BAJA  );		
			}			
		
		
		if (prestacionesreclamo.getId_prestacion()> 0){
			prestacionesreclamo.setIdRegistro(prestacionesreclamo.getId_prestacion());
		}else{
		//	prestacionesreclamo.setIdRegistro(prestacionesreclamo.getId_medicamento());	
			// Este metodo lo usa en el equal para comparar
			prestacionesreclamo.setIdRegistro(prestacionesreclamo.getIdprestacionReclamo());
		}
		
		
		if(rs.getString(prefix + "cbu")!=null) {
			  prestacionesreclamo.setCbu(rs.getString(prefix + "cbu"));
		}
			
		if(rs.getString(prefix + "cuil_cuenta")!=null) {
			  prestacionesreclamo.setCuilCuenta(rs.getString(prefix + "cuil_cuenta"));
		}
		
		if(rs.getString(prefix + "email_cuenta")!=null) {
			  prestacionesreclamo.setEmailCuenta(rs.getString(prefix + "email_cuenta"));
		}
			
		if(rs.getString(prefix + "apellido_cuenta")!=null) {
			  prestacionesreclamo.setApellidoCuenta(rs.getString(prefix + "apellido_cuenta"));
		}
		
		if(rs.getString(prefix + "nombre_cuenta")!=null) {
			  prestacionesreclamo.setNombreCuenta(rs.getString(prefix + "nombre_cuenta"));
		}
	
		try {
			if(rs.getString(prefix + "tope_importe_plan")!=null) {
				prestacionesreclamo.setImporteTopePlan(rs.getDouble(prefix + "tope_importe_plan"));
			}
		}catch (Exception e) {
		}		

		return prestacionesreclamo ;
	}
	
	
	public static PrestacionesReclamo getMappingPrestacionDelReclamo(String prefix, ResultSet rs)
			throws SQLException {
		
		PrestacionesReclamo  prestacionesreclamo = new PrestacionesReclamo();

		prestacionesreclamo.setIdreclamoprestacional(rs.getInt(prefix + "id_reclamo_prestacional"));
		prestacionesreclamo.setFrecuencia(rs.getString(prefix + "frecuencia"));
		prestacionesreclamo.setImporte(rs.getDouble(prefix + "importe"));
		prestacionesreclamo.setCargo_ospim(rs.getDouble(prefix + "cargo_ospim"));
		prestacionesreclamo.setCargo_ps(rs.getDouble(prefix + "cargo_ps"));
		prestacionesreclamo.setId_medicamento(rs.getInt(prefix + "id_medicamento"));
		prestacionesreclamo.setId_prestacion(rs.getInt(prefix + "id_prestacion"));
		prestacionesreclamo.setObservaciones(rs.getString(prefix + "observaciones"));
		prestacionesreclamo.setEstadoRechazoAprobado(rs.getInt(prefix + "estado_aprobacion"));
		
		try {
			prestacionesreclamo.setCargo_imesa(rs.getDouble(prefix + "cargo_imesa"));
		}catch(Exception e) {
			
		}

		return prestacionesreclamo ;
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
				+ ", descripcion=" + descripcion + ", frecuencia="
				+ frecuencia + ", observaciones="
				+ observaciones + ", idRegistro=" + idRegistro 
				+ ", estado=" + estado  
				+ "idPrestacionReclamo= " + idPrestacionReclamo + "]";
	}

	public List<EspecialidadPrestador> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(List<EspecialidadPrestador> especialidades) {
		this.especialidades = especialidades;
	}
	
	public int getIdprestacionReclamo () {
		return idPrestacionReclamo;
	}

	public void setIdprestacionReclamo (int idPrestacionReclamo ) {
		this.idPrestacionReclamo = idPrestacionReclamo ;
	}

	
	public int getIdreclamoprestacional() {
		return idReclamoPrestacional;
	}

	public void setIdreclamoprestacional(int idReclamoPrestacional) {
		this.idReclamoPrestacional = idReclamoPrestacional;
	}

	public int getId_medicamento() {
		return idMedicamento;
	}

	public void setId_medicamento(int idMedicamento) {
		this.idMedicamento = idMedicamento;
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

	public double getImporteTopePlan() {
		return importeTopePlan;
	}
	
	public String getImporteString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatter.format(importe);
	}

	public String getImporteTopePlanString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatter.format(importeTopePlan);
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public void setImporteTopePlan(double importeTopePlan) {
		this.importeTopePlan = importeTopePlan;
	}

	public String getFrecuencia() {
		return frecuencia;
	}

	public void setFrecuencia(String frecuencia) {
		this.frecuencia = frecuencia;
	}

	public double getCargo_ospim() {
		return cargoOspim;
	}
	
	public String getCargo_ospimString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatter.format(cargoOspim);
	}
	
	
	public void setCargo_ospim(double cargoOspim) {
		this.cargoOspim = cargoOspim;
	}

	public double getCargo_ps() {
		return cargoPs;
	}
	
	public String getOp () {
		if ( Validator.isNull(opDePago)){
			return  "";
		}else{
			return  opDePago;	
		}
	}
	
	public void setOp (String  valor ) {
		opDePago = valor;
	}
	
	
	public String getCargo_psString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatter.format(cargoPs);		
	}
	
	public String getCargo_imesaString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatter.format(cargoImesa);		
	}

	
	public void setCargo_ps(double cargoPs) {
		this.cargoPs = cargoPs;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	public String getObservacionesAutorizaRechaza() {
		return observacionesAutorizaRechaza;
	}

	public void setObservacionesAutorizaRechaza(String observaciones) {
		this.observacionesAutorizaRechaza= observaciones;
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
	
	public int getIdRegistro() {
		return idRegistro;
	}
	public String getIdregistroString() {		
		return Integer.toString(idRegistro) ;
	}

	public String getIdprestacionString() {		
		return Integer.toString(idPrestacion) ;
	}
	
	public int getEstadoRechazoAprobado() {
		return this.estadoRechazoAprobado;
	}

	public void setEstadoRechazoAprobado(int estadoRechazoAprobado) {
		this.estadoRechazoAprobado = estadoRechazoAprobado;
	}
	
	public String getIdmedicacionString() {		
		return Integer.toString(idMedicamento) ;
	}
	
	
	public void setIdRegistro(int idRegistro) {
		this.idRegistro = idRegistro;
	}
	
	public String getCodigoPrestacion() {
		return Codigo  ;
	}
	public void  setCodigoPrestacion(String CodigoPrestacion) {
		this.Codigo  =CodigoPrestacion;
	}
	
	public int getTipoPrestacion() {
		return tipoPrestacion;
	}
	public void  setTipoPrestacion(int tipoPrestacion) {
		this.tipoPrestacion=tipoPrestacion ;
	}	

	public double getCantidad() {
		return cantidad ;
	}
	public String getCantidadString() {
		return String.valueOf(cantidad); 
	}
	public void  setCantidad (double cantidadPrestacion ) {
		this.cantidad =cantidadPrestacion ;
	}	
	
	
	public String getTotalString() {
		double  valor ;
		valor = cantidad * importe;
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatter.format(valor); 
	}
	
	public String getNombremedicacion() {
		return nombreMedicacion;
	}

	public void setNombremedicacion(String nombreMedicacion) {
		this.nombreMedicacion = nombreMedicacion;
	}

	public String getNombreprestacion() {
		return nombrePrestacion;
	}

	
	public void setRecuperableSur (Boolean recuperable ) {
		this.recuperableSur = recuperable ;
	}

	public Boolean  isRecuperableSur () {
		return this.recuperableSur ;
	}

	
	public void setNombreprestacion(String nombrePrestacion) {
		this.nombrePrestacion = nombrePrestacion;
		this.descripcion= nombrePrestacion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idRegistro;
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
		PrestacionesReclamo other = (PrestacionesReclamo) obj;
		if (idRegistro != other.idRegistro)
			return false;
		return true;
	}
	
	


	public String getCuilTitular() {
		return cuilTitular;
	}

	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}

	public int getInte() {
		return inte;
	}

	public void setInte(int inte) {
		this.inte = inte;
	}

	public String getComprobanteTipo() {
		return comprobanteTipo;
	}

	public void setComprobanteTipo(String comprobanteTipo) {
		this.comprobanteTipo = comprobanteTipo;
	}

	public String getComprobanteNro() {
		return comprobanteNro;
	}

	public void setComprobanteNro(String comprobanteNro) {
		this.comprobanteNro = comprobanteNro;
	}

	public java.util.Date getComprobanteFecha() {
		return comprobanteFecha;
	}

	
	public int getComprobanteFechaDia() {
		Calendar c = Calendar.getInstance();
		c.setTime(comprobanteFecha);
		return  c.get(Calendar.DAY_OF_MONTH);
	}
	
	public int getComprobanteFechaMes() {
		Calendar c = Calendar.getInstance();
		c.setTime(comprobanteFecha);
		return  c.get(Calendar.MONTH);
	}
	
	public int getComprobanteFechaAnno() {
		Calendar c = Calendar.getInstance();
		c.setTime(comprobanteFecha);
		return  c.get(Calendar.YEAR);
	}
	
	
	public int getFechaPrestacionDia() {
		Calendar c = Calendar.getInstance();
		c.setTime(fechaPrestacion);
		return  c.get(Calendar.DAY_OF_MONTH);
	}
	
	public int getFechaPrestacionMes() {
		Calendar c = Calendar.getInstance();
		c.setTime(fechaPrestacion);
		return  c.get(Calendar.MONTH);
	}
	
	public int getFechaPrestacionAnno() {
		Calendar c = Calendar.getInstance();
		c.setTime(fechaPrestacion);
		return  c.get(Calendar.YEAR);
	}
	
	public void setComprobanteFecha(java.util.Date  comprobanteFecha) {
		this.comprobanteFecha = comprobanteFecha;
	}

	public Double getComprobanteCantidad() {
		return comprobanteCantidad;
	}

	public void setComprobanteCantidad(Double comprobanteCantidad) {
		this.comprobanteCantidad = comprobanteCantidad;
	}

	public Double getComprobanteImporte() {
		return comprobanteImporte;
	}

	public void setComprobanteImporte(Double comprobanteImporte) {
		this.comprobanteImporte = comprobanteImporte;
	}

	public Double getComprobanteTotal() {
		return comprobanteTotal;
	}

	public void setComprobanteTotal(Double comprobanteTotal) {
		this.comprobanteTotal = comprobanteTotal;
	}

	public String getComprobanteCUIT() {
		return comprobanteCUIT;
	}

	public void setComprobanteCUIT(String comprobanteCUIT) {
		this.comprobanteCUIT = comprobanteCUIT;
	}

	public String getComprobanteSucursal() {
		return comprobanteSucursal;
	}

	public void setComprobanteSucursal(String comprobanteSucursal) {
		this.comprobanteSucursal = comprobanteSucursal;
	}

	public String getComprobanteRazonSocial() {
		return comprobanteRazonSocial;
	}

	public void setComprobanteRazonSocial(String comprobanteRazonSocial) {
		this.comprobanteRazonSocial = comprobanteRazonSocial;
	}

	public String getComprobanteCUITSucursal() {
		return comprobanteCUITSucursal;
	}

	public void setComprobanteCUITSucursal(String comprobanteCUITSucursal) {
		this.comprobanteCUITSucursal = comprobanteCUITSucursal;
	}

	public String getComprobanteLetra() {
		return comprobanteLetra;
	}

	public void setComprobanteLetra(String comprobanteLetra) {
		this.comprobanteLetra = comprobanteLetra;
	}

	public java.util.Date getFechaPrestacion() {
		return fechaPrestacion;
	}

	public void setFechaPrestacion(java.util.Date fechaPrestacion) {
		this.fechaPrestacion = fechaPrestacion;
	}

	public java.util.Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(java.util.Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

	public String getCbu() {
		return cbu;
	}

	public void setCbu(String cbu) {
		this.cbu = cbu;
	}

	public String getCuilCuenta() {
		return cuilCuenta;
	}

	public void setCuilCuenta(String cuilCuenta) {
		this.cuilCuenta = cuilCuenta;
	}

	public String getEmailCuenta() {
		return emailCuenta;
	}

	public void setEmailCuenta(String emailCuenta) {
		this.emailCuenta = emailCuenta;
	}

	public String getApellidoCuenta() {
		return apellidoCuenta;
	}

	public void setApellidoCuenta(String apellidoCuenta) {
		this.apellidoCuenta = apellidoCuenta;
	}

	public String getNombreCuenta() {
		return nombreCuenta;
	}

	public void setNombreCuenta(String nombreCuenta) {
		this.nombreCuenta = nombreCuenta;
	}

	public Double getReconocidoSSS() {
		return reconocidoSSS;
	}

	public void setReconocidoSSS(double reconocidoSSS) {
		this.reconocidoSSS = reconocidoSSS;
	}

	public String getReconocidoSSSString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatter.format(reconocidoSSS);		
	}
	
	public Integer getRecuperable() {
		return recuperable;
	}

	public void setRecuperable(Integer recuperable) {
		this.recuperable = recuperable;
	}
    	
	public void setCargo_imesa(Double cargoImesa) {
		this.cargoImesa = cargoImesa;
	}

	public Double getCargo_imesa() {
		return cargoImesa;
	}
	
	public String getIdTercerizadora() {
	    return idTercerizadora;
	}

	public void setIdTercerizadora(String idTercerizadora) {
	    this.idTercerizadora = idTercerizadora;
	}
}


