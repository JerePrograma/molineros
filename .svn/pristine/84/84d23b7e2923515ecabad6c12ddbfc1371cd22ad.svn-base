package ar.com.empresas.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import ar.com.ospim.global.WebKeysGlobal;

public class ReporteEntidadCamaraMasaBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, ItemReporte> items;
	private Date periodo;
	private String cuit;
	private String periodosEmpleadores;
	private String periodosAfip;

	public static ReporteEntidadCamaraMasaBean getMapping(ResultSet rs)
			throws SQLException {
		ReporteEntidadCamaraMasaBean reporteEntidadCamaraMasaBean = new ReporteEntidadCamaraMasaBean();
		HashMap<String, ItemReporte> its = new HashMap<String, ItemReporte>();

		if (rs.getInt("amtima") > 0) {
			ItemReporte itAmtima = new ItemReporte();
			itAmtima.setEntidad(WebKeysGlobal.ENTIDAD_AMTIMA);
			itAmtima.setCantidad(rs.getInt("amtima"));
			itAmtima.setRemuneracion(rs.getBigDecimal("amtima_remu"));
			its.put(WebKeysGlobal.ENTIDAD_AMTIMA, itAmtima);
		}

		if (rs.getInt("uoma") > 0) {
			ItemReporte itUoma = new ItemReporte();
			itUoma.setEntidad(WebKeysGlobal.ENTIDAD_UOMA);
			itUoma.setCantidad(rs.getInt("uoma"));
			itUoma.setRemuneracion(rs.getBigDecimal("uoma_remu"));
			its.put(WebKeysGlobal.ENTIDAD_UOMA, itUoma);
		}
		if (rs.getInt("ospim") > 0) {
			ItemReporte itOspim = new ItemReporte();
			itOspim.setEntidad(WebKeysGlobal.ENTIDAD_OSPIM);
			itOspim.setCantidad(rs.getInt("ospim"));
			itOspim.setRemuneracion(rs.getBigDecimal("ospim_remu"));
			itOspim.setPeriodo(rs.getDate("periodo_ospim"));
			its.put(WebKeysGlobal.ENTIDAD_OSPIM, itOspim);
		}

		if (rs.getInt("faim") > 0) {
			ItemReporte itFAIM = new ItemReporte();
			itFAIM.setEntidad(WebKeysGlobal.FAIM);
			itFAIM.setCantidad(rs.getInt("faim"));
			itFAIM.setRemuneracion(rs.getBigDecimal("faim_total_remu"));
			its.put(WebKeysGlobal.FAIM, itFAIM);
		}

		if (rs.getInt("caena") > 0) {
			ItemReporte itCAENA = new ItemReporte();
			itCAENA.setEntidad(WebKeysGlobal.CAENA);
			itCAENA.setCantidad(rs.getInt("caena"));
			itCAENA.setRemuneracion(rs.getBigDecimal("caena_total_remu"));
			its.put(WebKeysGlobal.CAENA, itCAENA);
		}

		if (rs.getInt("cepa") > 0) {
			ItemReporte itCEPA = new ItemReporte();
			itCEPA.setEntidad(WebKeysGlobal.CEPA);
			itCEPA.setCantidad(rs.getInt("cepa"));
			itCEPA.setRemuneracion(rs.getBigDecimal("cepa_total_remu"));
			its.put(WebKeysGlobal.CEPA, itCEPA);
		}

		if (rs.getInt("total_empresa") > 0) {
			ItemReporte itTotal = new ItemReporte();
			itTotal.setEntidad("Total");
			itTotal.setCantidad(rs.getInt("total_empresa"));
			itTotal.setRemuneracion(rs.getBigDecimal("remuneracion_total"));
			its.put("TOTAL_EMPRESA", itTotal);
		}

		if (rs.getInt("ospim_portal") > 0) {
			ItemReporte itPortOsp = new ItemReporte();
			itPortOsp.setEntidad("Portal OSPIM");
			itPortOsp.setCantidad(rs.getInt("ospim_portal"));
			its.put("OSPIM_PORTAL", itPortOsp);
		}

		if (rs.getInt("uoma_portal") > 0) {
			ItemReporte itPortUom = new ItemReporte();
			itPortUom.setEntidad("Portal UOMA");
			itPortUom.setCantidad(rs.getInt("uoma_portal"));
			its.put("UOMA_PORTAL", itPortUom);
		}

		if (rs.getInt("amtima_portal") > 0) {
			ItemReporte itPortAmt = new ItemReporte();
			itPortAmt.setEntidad("Portal AMTIMA");
			itPortAmt.setCantidad(rs.getInt("amtima_portal"));
			its.put("AMTIMA_PORTAL", itPortAmt);
		}

		if (rs.getInt("total_portal") > 0) {
			ItemReporte totalPortal = new ItemReporte();
			totalPortal.setEntidad("Portal Total");
			totalPortal.setCantidad(rs.getInt("total_portal"));
			its.put("TOTAL_PORTAL", totalPortal);
		}
		reporteEntidadCamaraMasaBean.setPeriodo(rs.getDate("periodo"));
		reporteEntidadCamaraMasaBean.setPeriodosEmpleadores(rs.getString("periodos_emple"));
		reporteEntidadCamaraMasaBean.setPeriodosAfip(rs.getString("periodos_afip"));
		reporteEntidadCamaraMasaBean.setItems(its);
		 
		return reporteEntidadCamaraMasaBean;
	}

	public static class ItemReporte {
		String entidad;
		int cantidad;
		Date periodo;
		BigDecimal remuneracion;

		public String getPeriodoAsString() {
			if (null != periodo) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
				return sdf.format(periodo);
			} else {
				return "";
			}
		}

		public Date getPeriodo() {
			return periodo;
		}

		public void setPeriodo(Date periodo) {
			this.periodo = periodo;
		}

		public String getEntidad() {
			return entidad;
		}

		public void setEntidad(String entidad) {
			this.entidad = entidad;
		}

		public int getCantidad() {
			return cantidad;
		}

		public void setCantidad(int cantidad) {
			this.cantidad = cantidad;
		}

		public BigDecimal getRemuneracion() {
			return remuneracion;
		}

		public void setRemuneracion(BigDecimal remuneraciones) {
			this.remuneracion = remuneraciones;
		}

	}

	public HashMap<String, ItemReporte> getItems() {
		return items;
	}

	public void setItems(HashMap<String, ItemReporte> items) {
		this.items = items;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public String getPeriodoAsString() {
		if (null != periodo) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
			return sdf.format(periodo);
		} else {
			return "";
		}
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getPeriodosEmpleadores() {
		return periodosEmpleadores;
	}

	public void setPeriodosEmpleadores(String periodosEmple) {
		this.periodosEmpleadores = periodosEmple;
	}

	public String getPeriodosAfip() {
		return periodosAfip;
	}

	public void setPeriodosAfip(String periodosAfip) {
		this.periodosAfip = periodosAfip;
	}
	
	

}