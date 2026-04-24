package ar.com.uoma.facturacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.uoma.WebKeysUOMA;


public class Factura implements Serializable {

	/**
	 * 
	 */
	private static Log _log = LogFactoryUtil.getLog(Factura.class);


	private static final long serialVersionUID = -5748113487670826881L;
	
	public enum ESTADOS {
		ALTA, MODIFICACION, BAJA
	};
	
	private int id ;
	private Date fecha;
	private String tipo;
	private String letra;
	private String sucursal;
	private String numero;
	private Cliente cliente;
	private String cae;
	private Date fechaCae;
	private List<FacturaDetalle> detalles;
	private BigDecimal total = new BigDecimal(0);
	private BigDecimal totalExento = new BigDecimal(0);
	private BigDecimal totalNeto = new BigDecimal(0);
	private BigDecimal iva = new BigDecimal(0);
	private BigDecimal ivaReintegro = new BigDecimal(0);
	private String altaUsr;
	private Date altaFecha;
	private boolean presentaForm8001;
	private List<FacturaIngreso> ingresos = new ArrayList<FacturaIngreso>();
	private boolean manual;
	private String observaciones;
	private ESTADOS estado;
	
	private Integer totalRegistros;
	
	private List<Recibo> recibosAdelantos;
	
	private BigDecimal percepcion= new BigDecimal(0);
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getLetra() {
		return letra;
	}
	public void setLetra(String letra) {
		this.letra = letra;
	}
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public Date getFechaCae() {
		return fechaCae;
	}
	public void setFechaCae(Date fechaCae) {
		this.fechaCae = fechaCae;
	}
	public String getCae() {
		return cae;
	}
	public String getCaeDescripcion() {
		if(cae == null) {
			return "Pendiente";
		}
		return cae;
		
	}
	public void setCae(String cae) {
		this.cae = cae;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public ESTADOS getEstado() {
		return estado;
	}
	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}
	public List<FacturaDetalle> getDetalles() {
		return detalles;
	}
	public void setDetalle(List<FacturaDetalle> detalles) {
		this.detalles = detalles;
	}
	
	public BigDecimal getImporteTotal() {
		
		return total;
	}
	
	public BigDecimal getImporteTotalCalculado() {
				
		total = new BigDecimal(0);
		
/*	DS - 2023-04-03 Se cambio forma de facturar. Coexisten gravado y exento en la misma factura	
		if(iva.compareTo(new BigDecimal(0)) > 0 ) {
			total=total.add(totalNeto).add(iva);	
		}else {
			total=total.add(totalExento);
		}
*/	
		total=total.add(totalNeto!=null?totalNeto:BigDecimal.ZERO).add(iva!=null?iva:BigDecimal.ZERO);
		total=total.add(totalExento!=null?totalExento:BigDecimal.ZERO);
		total=total.add(percepcion!=null?percepcion: BigDecimal.ZERO);
		total=total.subtract(ivaReintegro.abs()!=null?ivaReintegro.abs(): BigDecimal.ZERO);
		
		return total;
	}
	
	public BigDecimal getImporteExento() {

		return totalExento;
	}

	public BigDecimal getImporteNeto() {
		
		return totalNeto;
	}
	
	public BigDecimal getIva() {

		return iva;
	}
	
	public void setIva(BigDecimal iva) {
		this.iva = iva;
	}
	
	public BigDecimal getTotalExento() {
		return totalExento;
	}
	
	public void setTotalExento(BigDecimal totalExento) {
		this.totalExento = totalExento;
	}
	public BigDecimal getTotalNeto() {
		return totalNeto;
	}
	public void setTotalNeto(BigDecimal totalNeto) {
		this.totalNeto = totalNeto;
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
	
	
	
	public BigDecimal getIvaReintegro() {
		return ivaReintegro;
	}
	public void setIvaReintegro(BigDecimal ivaReintegro) {
		this.ivaReintegro = ivaReintegro;
	}
	public static Factura getMapping(String prefix, ResultSet rs) throws SQLException{
		
		Factura fc = new Factura();
		fc.setId(rs.getInt(prefix+"id"));
		fc.setAltaFecha(rs.getDate(prefix+"alta_fecha"));
		fc.setAltaUsr(rs.getString(prefix+"alta_usr"));
		fc.setCae(rs.getString(prefix+"cae"));
		fc.setCliente(Cliente.getMapping("cliente_", rs));
		fc.setFecha(rs.getDate(prefix+"fecha"));
		fc.setFechaCae(rs.getDate(prefix + "fecha_vto_cae"));
		fc.setIva(rs.getBigDecimal(prefix +"iva_total"));
		fc.setTipo(rs.getString(prefix+"compro_tipo"));
		fc.setLetra(rs.getString(prefix+"compro_letra"));
		fc.setNumero(rs.getString(prefix+"compro_nro"));
		fc.setPresentaForm8001(rs.getBoolean(prefix+"presenta_form_8001"));
		fc.setSucursal(rs.getString(prefix+"compro_sucu"));
		fc.setTotalExento(rs.getBigDecimal(prefix+"exen"));
		fc.setTotalNeto(rs.getBigDecimal(prefix+"grava"));
		fc.setObservaciones(rs.getString(prefix+"observaciones"));
		fc.setPercepcion(rs.getBigDecimal(prefix+"percepcion"));
        fc.setIvaReintegro(rs.getBigDecimal(prefix+"iva_reintegro"));
		try {
			fc.setTotalRegistros(rs.getInt("total_registros_v"));
		}catch(Exception e) {}
		
		
		return fc;
	}

	public List<FacturaIngreso> getIngresos() {
		return ingresos;
	}
	
	public void setIngresos(List<FacturaIngreso> ingresos) {
		this.ingresos = ingresos;
	}
	
	public boolean isPresentaForm8001() {
		return presentaForm8001;
	}
	
	public void setPresentaForm8001(boolean presentaForm8001) {
		this.presentaForm8001 = presentaForm8001;
	}
	
	public boolean isManual() {
		return manual;
	}
	
	public void setManual(boolean manual) {
		this.manual = manual;
	}
	
	public void recalcularImportes() {
		
		if(detalles!=null) {
			
			total = new BigDecimal(0);
			iva = new BigDecimal(0);
			
			for (FacturaDetalle facturaDetalle : detalles) {
				Producto pr = facturaDetalle.getDetalle();
				
				if(pr.getDebitoCredito().equals("D")) {
//					total = total.add(pr.getPrecioUnitario());
					total = total.add(facturaDetalle.getPrecio());
				}else {
//					total = total.subtract(pr.getPrecioUnitario());
					total = total.subtract(facturaDetalle.getPrecio());
				}
			}
		}

		if(cliente != null) {
		
			if(cliente.getCategoriaIVA().equalsIgnoreCase(WebKeysUOMA.CATEGORIAS_IVA[0][0])     // Responsable Inscripto
					|| (cliente.getTipo() ==null && cliente.getCuit() == null) 					// Factura B no afiliado
					|| cliente.getTipo().equals(Cliente.TIPOS_CLIENTE.VISITA)                   // Factura B no afiliado
					) { // Responsable Inscrip
				
				totalExento = new BigDecimal(0);
				
				BigDecimal alicuotaIVA = new BigDecimal("1.21");
				
				totalNeto = total.divide(alicuotaIVA,2, RoundingMode.HALF_UP);  
//				totalNeto = totalNeto.setScale(2, RoundingMode.HALF_UP);
				
				if(total.compareTo(new BigDecimal(0)) > 0) { // evitar division x 0
					iva = total.subtract(totalNeto);
				}
				
			}else { // Consumidor final, Exento - Cliente Afiliado
				
				totalExento = total;
				
				iva = new BigDecimal(0);
				
				totalNeto = total;
			}
		}
	}
	
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	
	public int getComprobanteAFIP() {
		int comproTipo = 999;
		
		if(this.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA) && this.getLetra().equalsIgnoreCase("A")) {
			comproTipo = 1; // Factura A
		}else if(this.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA) && this.getLetra().equalsIgnoreCase("B")) {
			comproTipo = 6; // Factura B
		}else if(this.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO) && this.getLetra().equalsIgnoreCase("A")) {
			comproTipo = 3; // Nota de Crédito A
		}else if(this.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO) && this.getLetra().equalsIgnoreCase("B")) {
			comproTipo = 8; // Nota de Crédito B
		}else if(this.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO) && this.getLetra().equalsIgnoreCase("A")) {
			comproTipo = 201; // Factura crédito  electrónica A
		}else if(this.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO) && this.getLetra().equalsIgnoreCase("B")) {
			comproTipo = 206; // Factura crédito  electrónica A
		}else if(this.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA) && this.getLetra().equalsIgnoreCase("B")) {
			comproTipo = 6; // Factura B
		}else if(this.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA) && this.getLetra().equalsIgnoreCase("B")) {
			comproTipo = 6; // Factura B
		}else if(this.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA) && this.getLetra().equalsIgnoreCase("T")) {
			comproTipo = 195; // Factura T
		}else if(this.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO) && this.getLetra().equalsIgnoreCase("T")) {
			comproTipo = 197; // Nota Credito T
		}		
		return comproTipo;
	}
	
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public Integer getTotalRegistros() {
		return totalRegistros;
	}
	public void setTotalRegistros(Integer totalRegistros) {
		this.totalRegistros = totalRegistros;
	}
	
	public List<Recibo> getRecibosAdelantos() {
		return recibosAdelantos;
	}
	public void setRecibosAdelantos(List<Recibo> recibosAdelantos) {
		this.recibosAdelantos = recibosAdelantos;
	}
	
	public BigDecimal getPercepcion() {
		return percepcion!=null?percepcion:BigDecimal.ZERO;
	}
	public void setPercepcion(BigDecimal percepcion) {
		this.percepcion= percepcion;
	}
	
	public BigDecimal getImporteBaseSinImpuestos() {
		
		total = new BigDecimal(0);
		total=total.add(totalNeto!=null?totalNeto:BigDecimal.ZERO);
		total=total.add(totalExento!=null?totalExento:BigDecimal.ZERO);
		
		return total;
	}
	

	/**
	 * Pagina 90 del manual_desarrollador_COMPG_v2_12.pdf
	 * 
	 * 
	 * Obligatorio. Valores permitidos:
		1: Factura A
		2: Nota de Débito A
		3: Nota de Crédito A
		4: Recibo A
		6: Factura B
		7: Nota de Débito B
		8: Nota de Crédito B
		9: Recibo B
		11: Factura C
		12: Nota de Débito C
		13: Nota de Crédito C
		15: Recibo C
		51: Factura M (CAEA observa comprobante)
		52: Nota de Débito M (CAEA observa comprobante)
		53: Nota de Crédito M (CAEA observa comprobante)
		54: Recibo M
		Consultar método FEParamGetTiposCbte
	 */
	
}
