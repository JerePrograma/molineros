package ar.com.ospim.hoteles.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmaciaOspim.beans.ItemVademecumTotal;
import ar.com.ospim.global.beans.Seccional;
import ar.com.uoma.facturacion.Factura;

public class Prestamo  implements Serializable {
	
	
	private static final long serialVersionUID = 8003208514746030924L;
	
	private Long id;
	private String codHotel;
	private String descripcionHotel;
	private Afiliado afiliado;
	private Date estadiaDesde;
	private Date estadiaHasta;
	private String observaciones;
	
	private Date fechaConvenio;
	private Double total;
	private Double pagado;
	
	//Filtro
	private Date fechaConvenioDesde;
	private Date fechaConvenioHasta;
	private Date fechaCuotaDesde;
	private Date fechaCuotaHasta;
	private Integer cuotaNro;
	
	private Factura factura;
	
	private String imgFactura;
	private String imgConvenio;
	
	private Date acuerdoFecha;
	private Date primeraCuota;
	private Double monto;
	private Double movilidad;
	private Double interesPorcentaje;
	private Double interesImporte;
	private Integer cantidadCuotas;
	private String errorMsg;
	private List<PrestamoCuota>cuotas;
	private String ultimoRecibo;
	private Date deudaExigibleAl;
	private Double deudaExigible;
	
	private Double cuotasAdeudadas;
	private String cuit;
	private String razonSocial;
	
	private Date bajaFechaAmtima;
	private Date corteCuentaCorriente;
	
   
	public String getCodHotel() {
		return codHotel;
	}


	public void setCodHotel(String codHotel) {
		this.codHotel = codHotel;
	}


	public List<PrestamoCuota> getCuotas() {
		return cuotas;
	}


	public void setCuotas(List<PrestamoCuota> cuotas) {
		this.cuotas = cuotas;
	}


	public String getHotel() {
		return codHotel;
	}


	public void setHotel(String codHotel) {
		this.codHotel = codHotel;
	}


	public Afiliado getAfiliado() {
		return afiliado;
	}


	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Date getEstadiaDesde() {
		return estadiaDesde;
	}


	public void setEstadiaDesde(Date estadiaDesde) {
		this.estadiaDesde = estadiaDesde;
	}


	public Date getEstadiaHasta() {
		return estadiaHasta;
	}


	public void setEstadiaHasta(Date estadiaHasta) {
		this.estadiaHasta = estadiaHasta;
	}


	public String getObservaciones() {
		return observaciones;
	}


	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}


	public Date getFechaConvenio() {
		return fechaConvenio;
	}


	public void setFechaConvenio(Date fechaConvenio) {
		this.fechaConvenio = fechaConvenio;
	}


	public Date getFechaConvenioDesde() {
		return fechaConvenioDesde;
	}


	public void setFechaConvenioDesde(Date fechaConvenioDesde) {
		this.fechaConvenioDesde = fechaConvenioDesde;
	}


	public Date getFechaConvenioHasta() {
		return fechaConvenioHasta;
	}


	public void setFechaConvenioHasta(Date fechaConvenioHasta) {
		this.fechaConvenioHasta = fechaConvenioHasta;
	}


	public Date getFechaCuotaDesde() {
		return fechaCuotaDesde;
	}


	public void setFechaCuotaDesde(Date fechaCuotaDesde) {
		this.fechaCuotaDesde = fechaCuotaDesde;
	}


	public Date getFechaCuotaHasta() {
		return fechaCuotaHasta;
	}


	public void setFechaCuotaHasta(Date fechaCuotaHasta) {
		this.fechaCuotaHasta = fechaCuotaHasta;
	}


	public Double getTotal() {
		return total;
	}


	public void setTotal(Double total) {
		this.total = total;
	}


	public Double getPagado() {
		return pagado;
	}


	public void setPagado(Double pagado) {
		this.pagado = pagado;
	}

	
	public String getDescripcionHotel() {
		return descripcionHotel;
	}


	public void setDescripcionHotel(String descripcionHotel) {
		this.descripcionHotel = descripcionHotel;
	}

	
	public Factura getFactura() {
		return factura;
	}


	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	

	public String getImgFactura() {
		return imgFactura;
	}


	public void setImgFactura(String imgFactura) {
		this.imgFactura = imgFactura;
	}


	public String getImgConvenio() {
		return imgConvenio;
	}


	public void setImgConvenio(String imgConvenio) {
		this.imgConvenio = imgConvenio;
	}

	

	public Date getAcuerdoFecha() {
		return acuerdoFecha;
	}


	public void setAcuerdoFecha(Date acuerdoFecha) {
		this.acuerdoFecha = acuerdoFecha;
	}


	public Date getPrimeraCuota() {
		return primeraCuota;
	}


	public void setPrimeraCuota(Date primeraCuota) {
		this.primeraCuota = primeraCuota;
	}


	public Double getMonto() {
		return monto;
	}


	public void setMonto(Double monto) {
		this.monto = monto;
	}


	public Double getInteresPorcentaje() {
		return interesPorcentaje;
	}


	public void setInteresPorcentaje(Double interesPorcentaje) {
		this.interesPorcentaje = interesPorcentaje;
	}


	public Double getInteresImporte() {
		return interesImporte;
	}


	public void setInteresImporte(Double interesImporte) {
		this.interesImporte = interesImporte;
	}


	public Integer getCantidadCuotas() {
		return cantidadCuotas;
	}


	public void setCantidadCuotas(Integer cantidadCuotas) {
		this.cantidadCuotas = cantidadCuotas;
	}


	public Integer getCuotaNro() {
		return cuotaNro;
	}


	public void setCuotaNro(Integer cuotaNro) {
		this.cuotaNro = cuotaNro;
	}


	
	public String getErrorMsg() {
		return errorMsg;
	}


	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	

	public String getUltimoRecibo() {
		return ultimoRecibo;
	}


	public void setUltimoRecibo(String ultimoRecibo) {
		this.ultimoRecibo = ultimoRecibo;
	}
	
	
	public Double getMovilidad() {
		return movilidad;
	}


	public void setMovilidad(Double movilidad) {
		this.movilidad = movilidad;
	}

	public Date getDeudaExigibleAl() {
		return deudaExigibleAl;
	}


	public void setDeudaExigibleAl(Date deudaExigibleAl) {
		this.deudaExigibleAl = deudaExigibleAl;
	}


	public Double getDeudaExigible() {
		return deudaExigible;
	}


	public void setDeudaExigible(Double deudaExigible) {
		this.deudaExigible = deudaExigible;
	}
	
	public Double getCuotasAdeudadas() {
		return cuotasAdeudadas;
	}


	public void setCuotasAdeudadas(Double cuotasAdeudadas) {
		this.cuotasAdeudadas = cuotasAdeudadas;
	}


	public String getCuit() {
		return cuit;
	}


	public void setCuit(String cuit) {
		this.cuit = cuit;
	}


	public String getRazonSocial() {
		return razonSocial;
	}


	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	

	public Date getBajaFechaAmtima() {
		return bajaFechaAmtima;
	}


	public void setBajaFechaAmtima(Date bajaFechaAmtima) {
		this.bajaFechaAmtima = bajaFechaAmtima;
	}
	
	public Date getCorteCuentaCorriente() {
		return corteCuentaCorriente;
	}

	public void setCorteCuentaCorriente(Date corteCuentaCorriente) {
		this.corteCuentaCorriente = corteCuentaCorriente;
	}


	public static Prestamo  getMapping(ResultSet rs, String prefix) throws Exception {
		Prestamo  pre  = new Prestamo();
		
		pre.setId(rs.getLong("id"));
		Seccional seccional=new Seccional();
		seccional.setId_seccional(rs.getInt("seccional_id"));
		seccional.setDescripcion(rs.getString("seccional_descripcion"));
		
	    Afiliado afiliado = new Afiliado();
	    afiliado.setCuil_titular(rs.getString("cuil_titular"));
	    afiliado.setInte(rs.getInt("inte"));
	    afiliado.setApellido(rs.getString("afiliado_descripcion"));
	    afiliado.setSeccional(seccional);
	    pre.setAfiliado(afiliado);
	    
	    pre.setHotel(rs.getString("hotel"));
	    pre.setDescripcionHotel(rs.getString("hotel_descripcion"));
	    pre.setFechaConvenio(rs.getDate("fecha_convenio"));
	    pre.setEstadiaDesde(rs.getDate("estadia_desde"));
	    pre.setEstadiaHasta(rs.getDate("estadia_hasta"));
	    pre.setObservaciones(rs.getString("observaciones"));
	    pre.setTotal(rs.getDouble("acuerdo_total"));
	    pre.setImgFactura(rs.getString("factura_imagen"));
	    pre.setImgConvenio(rs.getString("convenio_imagen"));
	    pre.setAcuerdoFecha(rs.getDate("acuerdo_fecha"));
		
	    Factura factura = new Factura();	
	    factura.setTipo("FCP");
	    factura.setLetra(rs.getString("factura_letra"));
	    factura.setSucursal(rs.getString("factura_sucursal"));
	    factura.setNumero(rs.getString("factura_numero"));
//	    factura.setFecha(rs.getDate("factura_fecha"));
	    factura.setTotalExento(rs.getBigDecimal("factura_importe"));
	    pre.setFactura(factura);
	    pre.setPagado(rs.getDouble("pagado"));
	    
	    
	    pre.setAcuerdoFecha(rs.getDate("acuerdo_fecha"));
	    pre.setMonto(rs.getDouble("acuerdo_monto"));
	    pre.setInteresPorcentaje(rs.getDouble("acuerdo_interes_porc"));
	    pre.setInteresImporte(rs.getDouble("acuerdo_interes_importe"));
	    pre.setCantidadCuotas(rs.getInt("acuerdo_cuotas")); 
	    pre.setPrimeraCuota(rs.getDate("acuerdo_primera_cuota"));
	    
	    pre.setUltimoRecibo(rs.getString("recibo"));
	    
	    pre.setMovilidad(rs.getDouble("movilidad"));
	    try {
	    	pre.setDeudaExigible(rs.getDouble("deuda_exigible")); 
	    	pre.setCuotasAdeudadas(rs.getDouble("cuotas_adeudadas"));
	    	pre.setCuit(rs.getString("cuit"));
	    	pre.setRazonSocial(rs.getString("razon_social"));
	    }catch(Exception e){
	    	
	    }
	    
	    try {
	    	pre.setBajaFechaAmtima(rs.getDate("baja_fecha_amtima"));
	    }catch(Exception e){
	    	
	    }
	    
		return pre ;
   }

	
}
