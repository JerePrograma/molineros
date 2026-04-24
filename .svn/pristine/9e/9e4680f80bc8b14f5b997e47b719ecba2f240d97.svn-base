package ar.com.ospim.procesaArchivos.beans.farmaciaospim;

import java.util.Date;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.farmaciaOspim.action.UploadArchivosFarmOspimAction;

import java.text.SimpleDateFormat;


public class Detallemedesp {
	
	
	
	private  Date  fecha_compra; 	
	private  String cuit_proveedor ; 		
	private  String  proveedor 	; 	
	private  String  cuil 	; 	
	private  String  nombre 	; 	
	private  String  cod_medicamento; 	 	
	private  String  medicamento 	; 	
	private  Integer cantidad; 	 	
	private  double preciosiniva; 	 
	private  double precioconiva; 	 
	private  double iva; 	 
	
	private  double totalconiva; 
	private  double totalsiniva; 	 
	private  String plan; 	 
	private  String afiliado;
	private  Integer inte ;
	

	private  String docu_numero; 	 
	private  Integer troquel; 	 
	private  Integer periodomes; 	 
	private  Integer periodoanio; 	 
    private  Integer idmedespecial ; 	
	
	private static Log logger = LogFactoryUtil
			.getLog(UploadArchivosFarmOspimAction.class);
	
	
	public Detallemedesp() {
		super();
	}
	


	public Detallemedesp (String lineadatosfile) throws Exception {

		String[] datos = null;
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");


		datos =    lineadatosfile.split(";");
		String[] extra; // medicamento
		String[] extra1; // cantidad viene con coma y punto en algunos casos
		String[] extra2; // 
		Date fechacompra = null;
				
		try {
			fechacompra = formatoDeFecha.parse(datos[1]);
		} catch (Exception e) {
			fechacompra = null;
		}
		
		try {// cuando el  troquel viene en blanco no se genera el campo indice 20
			
		
				this.fecha_compra =fechacompra; // java.sql.Date.valueOf("2017-07-07");
				extra=datos[4].split("-"); // extrae dos datos de columna proveedor 
				this.cuit_proveedor=extra[0];			
				this.proveedor =extra[1].trim() ;				
				this.cuil = datos[5]	;	
				this.nombre =datos[6].trim() ; 		
				extra=datos[7].split("-"); // extrae dos datos de columna medicamento 
				this.cod_medicamento=extra[0]; 	 	
				this.medicamento = extra[1].trim();			 		
				extra1=	datos[8].split(",");		
				this.cantidad = Integer.valueOf( extra1[0]) ; 	 	
				this.preciosiniva =  Double.parseDouble(datos[10].replace(",",".") ); 	 
				this.precioconiva = Double.parseDouble(datos[12].replace(",",".") ); 	  
				this.iva = Double.parseDouble(datos[11].replace(",",".") ); 	   	 
				this.totalconiva = Double.parseDouble(datos[13].replace(",",".") ); 	   	 
				this.totalsiniva=Double.parseDouble(datos[15].replace(",",".") ); 	    	 
				this.plan= datos[17]	;
				extra2=	datos[18].split("/");
				this.afiliado=extra2[0].trim() ;
				this.inte= Integer.parseInt(extra2[1]);
				this.docu_numero=datos[19];
			
				try {// cuando el  troquel viene en blanco no se genera el campo indice 20  
					this.troquel=Integer.valueOf(datos[20].trim()) ;
				} catch (Exception e) {
					this.troquel=0;
				}		
				 	 
				extra=datos[0].split("/"); // extrae mes y año del periodo informado en el archivo
				this.periodomes=Integer.valueOf( extra[0]); 	 
				this.periodoanio=Integer.valueOf( extra[1]);
			
			}catch (Exception e) {
				logger.debug("Error en el formato de los datos del archivo");
				throw e;
			}	
		
	}
	
	public Date getFecha_compra() {
		return fecha_compra;
	}

	public void setFecha_compra(Date fecha_compra) {
		this.fecha_compra = fecha_compra;
	}

	public String getCuit_proveedor() {
		return cuit_proveedor;
	}

	public void setCuit_proveedor(String cuit_proveedor) {
		this.cuit_proveedor = cuit_proveedor;
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCod_medicamento() {
		return cod_medicamento;
	}

	public void setCod_medicamento(String cod_medicamento) {
		this.cod_medicamento = cod_medicamento;
	}

	public String getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}


	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public double getPreciosiniva() {
		return preciosiniva;
	}

	public void setPreciosiniva(double preciosiniva) {
		this.preciosiniva = preciosiniva;
	}

	public double getPrecioconiva() {
		return precioconiva;
	}

	public void setPrecioconiva(double precioconiva) {
		this.precioconiva = precioconiva;
	}



	public double getIva() {
		return iva;
	}



	public void setIva(double iva) {
		this.iva = iva;
	}



	public double getTotalconiva() {
		return totalconiva;
	}



	public void setTotalconiva(double totalconiva) {
		this.totalconiva = totalconiva;
	}



	public double getTotalsiniva() {
		return totalsiniva;
	}



	public void setTotalsiniva(double totalsiniva) {
		this.totalsiniva = totalsiniva;
	}



	public String getPlan() {
		return plan;
	}



	public void setPlan(String plan) {
		this.plan = plan;
	}



	public String getAfiliado() {
		return afiliado;
	}



	public void setAfiliado(String afiliado) {
		this.afiliado = afiliado;
	}



	public String getDocu_numero() {
		return docu_numero;
	}



	public void setDocu_numero(String docu_numero) {
		this.docu_numero = docu_numero;
	}



	public Integer getTroquel() {
		return troquel;
	}



	public void setTroquel(Integer  troquel) {
		this.troquel = troquel;
	}



	public Integer getPeriodomes() {
		return periodomes;
	}



	public void setPeriodomes(Integer periodomes) {
		this.periodomes = periodomes;
	}



	public Integer getPeriodoanio() {
		return periodoanio;
	}



	public void setPeriodoanio(Integer periodoanio) {
		this.periodoanio = periodoanio;
	}

	public Integer getIdmedespecial() {
		return idmedespecial;
	}



	public void setIdmedespecial(Integer idmedespecial) {
		this.idmedespecial = idmedespecial;
	}

	public Integer getInte() {
		return inte;
	}

	public void setInte(Integer inte) {
		this.inte = inte;
	}
    
	public String setInteString() {
		return inte.toString();
	}
	
  
   
      
}
