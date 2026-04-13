package ar.com.uoma.facturacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.com.ospim.global.beans.Concepto;
import ar.com.uoma.beans.CentroCosto;

public class FacturaDetalle implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2910934455939651787L;
	
	public enum ESTADOS {
		ALTA, MODIFICACION, BAJA
	};
	
	private int id;
//	private int cantidad;
	private Producto detalle;
	private List<FacturaDetalleConcepto> conceptos;
	private ESTADOS estado;  
	private BigDecimal precio;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
//	public int getCantidad() {
//		return cantidad;
//	}
//	public void setCantidad(int cantidad) {
//		this.cantidad = cantidad;
//	}
	public Producto getDetalle() {
		return detalle;
	}
	public void setDetalle(Producto detalle) {
		this.detalle = detalle;
	}
	
	public List<FacturaDetalleConcepto> getConceptos() {
		return conceptos;
	}
	public void setConceptos(List<FacturaDetalleConcepto> conceptos) {
		this.conceptos = conceptos;
	}

	public ESTADOS getEstado() {
		return estado;
	}
	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	public BigDecimal getPrecio() {
		return precio;
	}
	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

	public static FacturaDetalle getMapping(String prefix, ResultSet rs)
			throws SQLException {
		
		FacturaDetalle fd = new FacturaDetalle();
		fd.setId(rs.getInt(prefix + "id" ));
		fd.setPrecio(rs.getBigDecimal(prefix + "precio"));
		fd.setDetalle(Producto.getMapping("producto_", rs));
		
		return fd;
	}
	
	
	public static class FacturaDetalleConcepto {
		
		private Concepto conceptoDetalle;
//		private BigDecimal importe;
//		private BigDecimal importeOriginal;
//		private boolean isBorradoLogicamente = false;
//		private boolean nuevo = true;
//		private Date alta_fecha;
//		private String alta_usr;
//		private Date modi_fecha;
//		private String modi_usr;
//		private Date baja_fecha;
//		private String baja_usr;
//		private boolean anticipo;
		private CentroCosto centroCosto;
		
		

		public CentroCosto getCentroCosto() {
			return centroCosto;
		}

		public void setCentroCosto(CentroCosto centroCosto) {
			this.centroCosto = centroCosto;
		}

		public FacturaDetalleConcepto(Concepto cc) {
			this.conceptoDetalle = cc;
		}

		public FacturaDetalleConcepto(Concepto cc, BigDecimal importe) {
			this.conceptoDetalle = cc;
//			this.importe = importe;
		}

		public FacturaDetalleConcepto() {
		}

		public FacturaDetalleConcepto(Concepto cc, BigDecimal importe, CentroCosto centro) {
			this.conceptoDetalle = cc;
//			this.importe = importe;
			this.centroCosto=centro;
		}
		
		public FacturaDetalleConcepto(Concepto cc, CentroCosto centro) {
			this.conceptoDetalle = cc;
			this.centroCosto=centro;
		}
		
		public Concepto getConceptoComprobante() {
			return conceptoDetalle;
		}

		public void setConceptoComprobante(Concepto conceptoComprobante) {
			this.conceptoDetalle = conceptoComprobante;
		}

//		public boolean isBorradoLogicamente() {
//			return isBorradoLogicamente;
//		}
//
//		public void setBorradoLogicamente(boolean isBorradoLogicamente) {
//			this.isBorradoLogicamente = isBorradoLogicamente;
//		}
//
//		public Date getAlta_fecha() {
//			return alta_fecha;
//		}
//
//		public void setAlta_fecha(Date altaFecha) {
//			alta_fecha = altaFecha;
//		}
//
//		public String getAlta_usr() {
//			return alta_usr;
//		}
//
//		public void setAlta_usr(String altaUsr) {
//			alta_usr = altaUsr;
//		}
//
//		public Date getModi_fecha() {
//			return modi_fecha;
//		}
//
//		public void setModi_fecha(Date modiFecha) {
//			modi_fecha = modiFecha;
//		}
//
//		public String getModi_usr() {
//			return modi_usr;
//		}
//
//		public void setModi_usr(String modiUsr) {
//			modi_usr = modiUsr;
//		}
//
//		public Date getBaja_fecha() {
//			return baja_fecha;
//		}
//
//		public void setBaja_fecha(Date bajaFecha) {
//			baja_fecha = bajaFecha;
//		}
//
//		public String getBaja_usr() {
//			return baja_usr;
//		}
//
//		public void setBaja_usr(String bajaUsr) {
//			baja_usr = bajaUsr;
//		}

		public static FacturaDetalleConcepto getMapping(ResultSet rs)
				throws SQLException {
			return getMapping(rs, "");
		}

		public static FacturaDetalleConcepto getMapping(ResultSet rs, String prefix)
				throws SQLException {
			
			FacturaDetalleConcepto cc = new FacturaDetalleConcepto();
//			cc.setNuevo(false);
//			cc.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
//			cc.setAlta_usr(rs.getString(prefix + "alta_usr"));
//			cc.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
//			cc.setModi_usr(rs.getString(prefix + "modi_usr"));
//			cc.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
//			cc.setBaja_usr(rs.getString(prefix + "baja_usr"));
			cc.setConceptoComprobante(new Concepto(rs.getInt(prefix
					+ "concepto_id")));
//			cc.setImporte(rs.getBigDecimal(prefix + "importe"));
//			cc.setImporteOriginal(rs.getBigDecimal(prefix + "importe"));
			try{
			  CentroCosto centro = new CentroCosto();
			  centro.setId(rs.getInt(prefix + "id_centro_costo") );
			  centro.setDescripcion(rs.getString(prefix+"descripcion_centro_costo"));
			  cc.setCentroCosto(centro);
				
			}catch(Exception e){
				
			}
			return cc;
		}

//		
//		public void setNuevo(boolean nuevo) {
//			this.nuevo = nuevo;
//		}
//
//		public boolean isNuevo() {
//			return nuevo;
//		}
//
//		public void setImporte(BigDecimal importe) {
//			this.importe = importe;
//		}
//
//		public BigDecimal getImporte() {
//			return importe;
//		}
//
//		public String getTipo() {
//			return "Comprobante";
//		}
//
//		// Para reporte SubdiarioEgreso
//		public String getCuenta() {
//			return conceptoDetalle != null ? conceptoDetalle
//					.getNumero() : "";
//		}
//
//		// Para reporte SubdiarioEgreso
//		public int getCuentaId() {
//			return conceptoDetalle != null ? conceptoDetalle
//					.getPlanCuentas().getId() : 0;
//		}
//
//		public String getDescripcionPAraSubdiario() {
//			return conceptoDetalle != null ? conceptoDetalle
//					.getCuenta() : "";
//		}
//
//		public void setAnticipo(boolean anticipo) {
//			this.anticipo = anticipo;
//		}
//
//		public boolean isAnticipo() {
//			return anticipo;
//		}
//
//		public BigDecimal getImporteOriginal() {
//			return null != importeOriginal
//					&& importeOriginal.compareTo(BigDecimal.ZERO) > 0 ? importeOriginal
//					: importe;
//		}
//
//		public void setImporteOriginal(BigDecimal importeOriginal) {
//			this.importeOriginal = importeOriginal;
//		}

	}

}
