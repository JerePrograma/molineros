package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class HospitalAutogestion {
	
	
	Date   fecha_proceso_transf;
	String codigo_organismo_transf;
	String debito_credito_transf;
	String codigo_organismo;
	String numero_expediente;
	Date fecha_proceso;
	Date fecha_transferencia;
	String clasif_expediente;
	BigDecimal importe_total ;
	Integer nro_cuota ;
	BigDecimal importe_transferencia ;
	String debito_credito;
	String nro_expediente_original;
	String codigo_htal;
	String nro_expediente_anssal;
	String observacion;
	String detalle_juzgado; 
	String detalle_secretaria;
	String autos;
	String numero_factura;
	Integer id_punto_venta;
	String compro_tipo;
	String compro_nro ;
	Date factu_perio; 
	Date fecha;
	Date impre_fecha;
	String cuil_titular; 
	Integer inte;
	Date vto;
	Date vto2;
	BigDecimal exen ;
	BigDecimal grava ;
	BigDecimal iva_total;
	BigDecimal ivan_total;
	BigDecimal total;
	String cance;
	Integer anu_moti;
	Date anu_fecha; 
	String anu_usu;
	String observaciones; 
	Date alta_fecha ;
	String alta_usr;
	Date modi_fecha;
	String modi_usr;
	Date baja_fecha; 
	String baja_usr;
	Date fecha_recepcion;
	Date fecha_emision;
	String cuit; 
	String compro_letra;
	Integer compro_sucu;
	Boolean debito_para_egreso ;
	Integer seccional;
	String cuit_acreedor;
	String sucu_acreedor;
	Date periodo_prestacion ;
	Date anulado_fecha ; 
	String anulado_usr;
	BigDecimal importe_original;
	
	public HospitalAutogestion() {}
	
	public Date getFecha_proceso_transf() {
		return fecha_proceso_transf;
	}


	public void setFecha_proceso_transf(Date fecha_proceso_transf) {
		this.fecha_proceso_transf = fecha_proceso_transf;
	}

	public String getCodigo_organismo_transf() {
		return codigo_organismo_transf;
	}

	public void setCodigo_organismo_transf(String codigo_organismo_transf) {
		this.codigo_organismo_transf = codigo_organismo_transf;
	}

	public String getDebito_credito_transf() {
		return debito_credito_transf;
	}

	public void setDebito_credito_transf(String debito_credito_transf) {
		this.debito_credito_transf = debito_credito_transf;
	}

	public String getCodigo_organismo() {
		return codigo_organismo;
	}

	public void setCodigo_organismo(String codigo_organismo) {
		this.codigo_organismo = codigo_organismo;
	}

	public String getNumero_expediente() {
		return numero_expediente;
	}

	public void setNumero_expediente(String numero_expediente) {
		this.numero_expediente = numero_expediente;
	}

	public Date getFecha_transferencia() {
		return fecha_transferencia;
	}

	public void setFecha_transferencia(Date fecha_transferencia) {
		this.fecha_transferencia = fecha_transferencia;
	}

	public String getClasif_expediente() {
		return clasif_expediente;
	}

	public void setClasif_expediente(String clasif_expediente) {
		this.clasif_expediente = clasif_expediente;
	}

	public BigDecimal getImporte_total() {
		return importe_total;
	}

	public void setImporte_total(BigDecimal importe_total) {
		this.importe_total = importe_total;
	}

	public Integer getNro_cuota() {
		return nro_cuota;
	}

	public void setNro_cuota(Integer nro_cuota) {
		this.nro_cuota = nro_cuota;
	}

	public BigDecimal getImporte_transferencia() {
		return importe_transferencia;
	}

	public void setImporte_transferencia(BigDecimal importe_transferencia) {
		this.importe_transferencia = importe_transferencia;
	}

	public String getDebito_credito() {
		return debito_credito;
	}

	public void setDebito_credito(String debito_credito) {
		this.debito_credito = debito_credito;
	}

	public String getNro_expediente_original() {
		return nro_expediente_original;
	}

	public void setNro_expediente_original(String nro_expediente_original) {
		this.nro_expediente_original = nro_expediente_original;
	}

	public String getCodigo_htal() {
		return codigo_htal;
	}

	public void setCodigo_htal(String codigo_htal) {
		this.codigo_htal = codigo_htal;
	}

	public String getNro_expediente_anssal() {
		return nro_expediente_anssal;
	}

	public void setNro_expediente_anssal(String nro_expediente_anssal) {
		this.nro_expediente_anssal = nro_expediente_anssal;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getDetalle_juzgado() {
		return detalle_juzgado;
	}

	public void setDetalle_juzgado(String detalle_juzgado) {
		this.detalle_juzgado = detalle_juzgado;
	}

	public String getDetalle_secretaria() {
		return detalle_secretaria;
	}

	public void setDetalle_secretaria(String detalle_secretaria) {
		this.detalle_secretaria = detalle_secretaria;
	}

	public String getAutos() {
		return autos;
	}

	public void setAutos(String autos) {
		this.autos = autos;
	}

	public String getNumero_factura() {
		return numero_factura;
	}

	public void setNumero_factura(String numero_factura) {
		this.numero_factura = numero_factura;
	}

	public Integer getId_punto_venta() {
		return id_punto_venta;
	}

	public void setId_punto_venta(Integer id_punto_venta) {
		this.id_punto_venta = id_punto_venta;
	}

	public String getCompro_tipo() {
		return compro_tipo;
	}

	public void setCompro_tipo(String compro_tipo) {
		this.compro_tipo = compro_tipo;
	}

	public String getCompro_nro() {
		return compro_nro;
	}

	public void setCompro_nro(String compro_nro) {
		this.compro_nro = compro_nro;
	}

	public Date getFactu_perio() {
		return factu_perio;
	}

	public void setFactu_perio(Date factu_perio) {
		this.factu_perio = factu_perio;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getImpre_fecha() {
		return impre_fecha;
	}

	public void setImpre_fecha(Date impre_fecha) {
		this.impre_fecha = impre_fecha;
	}

	public String getCuil_titular() {
		return cuil_titular;
	}

	public void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}

	public Integer getInte() {
		return inte;
	}

	public void setInte(Integer inte) {
		this.inte = inte;
	}

	public Date getVto() {
		return vto;
	}

	public void setVto(Date vto) {
		this.vto = vto;
	}

	public Date getVto2() {
		return vto2;
	}

	public void setVto2(Date vto2) {
		this.vto2 = vto2;
	}

	public BigDecimal getExen() {
		return exen;
	}

	public void setExen(BigDecimal exen) {
		this.exen = exen;
	}

	public BigDecimal getGrava() {
		return grava;
	}

	public void setGrava(BigDecimal grava) {
		this.grava = grava;
	}

	public BigDecimal getIva_total() {
		return iva_total;
	}

	public void setIva_total(BigDecimal iva_total) {
		this.iva_total = iva_total;
	}

	public BigDecimal getIvan_total() {
		return ivan_total;
	}

	public void setIvan_total(BigDecimal ivan_total) {
		this.ivan_total = ivan_total;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getCance() {
		return cance;
	}

	public void setCance(String cance) {
		this.cance = cance;
	}

	public Integer getAnu_moti() {
		return anu_moti;
	}

	public void setAnu_moti(Integer anu_moti) {
		this.anu_moti = anu_moti;
	}

	public Date getAnu_fecha() {
		return anu_fecha;
	}

	public void setAnu_fecha(Date anu_fecha) {
		this.anu_fecha = anu_fecha;
	}

	public String getAnu_usu() {
		return anu_usu;
	}

	public void setAnu_usu(String anu_usu) {
		this.anu_usu = anu_usu;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date alta_fecha) {
		this.alta_fecha = alta_fecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String alta_usr) {
		this.alta_usr = alta_usr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modi_fecha) {
		this.modi_fecha = modi_fecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modi_usr) {
		this.modi_usr = modi_usr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String baja_usr) {
		this.baja_usr = baja_usr;
	}

	public Date getFecha_recepcion() {
		return fecha_recepcion;
	}

	public void setFecha_recepcion(Date fecha_recepcion) {
		this.fecha_recepcion = fecha_recepcion;
	}

	public Date getFecha_emision() {
		return fecha_emision;
	}

	public void setFecha_emision(Date fecha_emision) {
		this.fecha_emision = fecha_emision;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getCompro_letra() {
		return compro_letra;
	}

	public void setCompro_letra(String compro_letra) {
		this.compro_letra = compro_letra;
	}

	public Integer getCompro_sucu() {
		return compro_sucu;
	}

	public void setCompro_sucu(Integer compro_sucu) {
		this.compro_sucu = compro_sucu;
	}

	public Boolean getDebito_para_egreso() {
		return debito_para_egreso;
	}

	public void setDebito_para_egreso(Boolean debito_para_egreso) {
		this.debito_para_egreso = debito_para_egreso;
	}

	public Integer getSeccional() {
		return seccional;
	}

	public void setSeccional(Integer seccional) {
		this.seccional = seccional;
	}

	public String getCuit_acreedor() {
		return cuit_acreedor;
	}

	public void setCuit_acreedor(String cuit_acreedor) {
		this.cuit_acreedor = cuit_acreedor;
	}

	public String getSucu_acreedor() {
		return sucu_acreedor;
	}

	public void setSucu_acreedor(String sucu_acreedor) {
		this.sucu_acreedor = sucu_acreedor;
	}

	public Date getPeriodo_prestacion() {
		return periodo_prestacion;
	}

	public void setPeriodo_prestacion(Date periodo_prestacion) {
		this.periodo_prestacion = periodo_prestacion;
	}

	public Date getAnulado_fecha() {
		return anulado_fecha;
	}

	public void setAnulado_fecha(Date anulado_fecha) {
		this.anulado_fecha = anulado_fecha;
	}

	public String getAnulado_usr() {
		return anulado_usr;
	}

	public void setAnulado_usr(String anulado_usr) {
		this.anulado_usr = anulado_usr;
	}

	public BigDecimal getImporte_original() {
		return importe_original;
	}

	public void setImporte_original(BigDecimal importe_original) {
		this.importe_original = importe_original;
	}

	public Date getFecha_proceso() {
		return fecha_proceso;
	}

	public void setFecha_proceso(Date fecha_proceso) {
		this.fecha_proceso = fecha_proceso;
	}

	public static HospitalAutogestion getMapping(ResultSet rs)
				throws SQLException {
			HospitalAutogestion h = new HospitalAutogestion();
			
			h.setFecha_proceso_transf(rs.getDate("fecha_proceso_transf")); 
			h.setCodigo_organismo_transf(rs.getString("codigo_organismo_transf")); 
			h.setDebito_credito_transf(rs.getString("debito_credito_transf"));  
			h.setCodigo_organismo(rs.getString("codigo_organismo"));
			h.setNumero_expediente(rs.getString("numero_expediente")); 
			h.setFecha_proceso(rs.getDate("fecha_proceso")); 
			h.setFecha_transferencia(rs.getDate("fecha_transferencia"));
			h.setClasif_expediente(rs.getString("clasif_expediente"));
			h.setImporte_total(rs.getBigDecimal("importe_total"));
			h.setNro_cuota(rs.getInt("nro_cuota"));
			h.setImporte_transferencia(rs.getBigDecimal("importe_transferencia"));
			h.setDebito_credito(rs.getString("debito_credito")); 
			h.setNro_expediente_original(rs.getString("nro_expediente_original")); 
			h.setCodigo_htal(rs.getString("codigo_htal")); 
			h.setNro_expediente_anssal(rs.getString("nro_expediente_anssal")); 
			h.setObservacion(rs.getString("observacion"));
			h.setDetalle_juzgado(rs.getString("detalle_juzgado"));  
			h.setDetalle_secretaria(rs.getString("detalle_secretaria")); 
			h.setAutos(rs.getString("autos"));  
			h.setNumero_factura(rs.getString("numero_factura")); 
			h.setId_punto_venta(rs.getInt("id_punto_venta"));
			h.setCompro_tipo(rs.getString("compro_tipo"));  
			h.setCompro_nro(rs.getString("compro_nro"));  
			h.setFactu_perio(rs.getDate("factu_perio")); 
			h.setFecha(rs.getDate("fecha"));
			h.setImpre_fecha(rs.getDate("impre_fecha"));
			h.setCuil_titular(rs.getString("cuil_titular")); 
			h.setInte(rs.getInt("inte"));
			h.setVto(rs.getDate("vto"));
			h.setVto2(rs.getDate("vto2")); 
			h.setExen(rs.getBigDecimal("exen"));
			h.setGrava(rs.getBigDecimal("grava"));
			h.setIva_total(rs.getBigDecimal("iva_total"));
			h.setIvan_total(rs.getBigDecimal("ivan_total")); 
			h.setTotal(rs.getBigDecimal("total"));
            h.setCance(rs.getString("cance"));
			h.setAnu_moti(rs.getInt("anu_moti")); 
			h.setAnu_fecha(rs.getDate("anu_fecha")); 
			h.setAnu_usu(rs.getString("anu_usu")); 
			h.setObservaciones(rs.getString("observaciones"));
			h.setAlta_fecha(rs.getDate("alta_fecha"));
			h.setAlta_usr(rs.getString("alta_usr"));  
			h.setModi_fecha(rs.getDate("modi_fecha")); 
			h.setModi_usr(rs.getString("modi_usr"));
			h.setBaja_fecha( rs.getDate("baja_fecha")); 
			h.setBaja_usr(rs.getString("baja_usr"));  
			h.setFecha_recepcion(rs.getDate("fecha_recepcion"));
			h.setFecha_emision(rs.getDate("fecha_emision"));
			h.setCuit(rs.getString("cuit"));
			h.setCompro_letra(rs.getString("compro_letra"));
			h.setCompro_sucu(rs.getInt("compro_sucu"));   
			h.setDebito_para_egreso(rs.getBoolean("debito_para_egreso"));
			h.setSeccional(rs.getInt("seccional")); 
			h.setCuit_acreedor(rs.getString("cuit_acreedor")); 
			h.setSucu_acreedor(rs.getString("sucu_acreedor")); 
			h.setPeriodo_prestacion(rs.getDate("periodo_prestacion"));
			h.setAnulado_fecha(rs.getDate("anulado_fecha"));
            h.setAnulado_usr(rs.getString("anulado_usr"));
            h.setImporte_original(rs.getBigDecimal("importe_original")); 
			return h;
	}
	
	
		
}
