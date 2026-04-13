package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;

public class SeguimientoSurComprobante extends Comprobante implements Serializable{
    private static final long serialVersionUID = -6644154242936404728L;
	private Integer seguimientoId;
	private Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SeguimientoSurComprobante(Integer id) {
		super();
		this.id = id;
	}

	public Integer getSeguimientoId() {
		return seguimientoId;
	}

	public void setSeguimientoId(Integer seguimientoId) {
		this.seguimientoId = seguimientoId;
	}

	public SeguimientoSurComprobante() {
		super();
	}

	public SeguimientoSurComprobante(Comprobante comp) {
		super(comp);
	}
	
	public SeguimientoSurComprobante(int ptoVenta, String tipoComprobante,
			String nroComprobante, String cuitEmisor, Date fechaEmision,
			Date fechaRecepcion, BigDecimal importeComprobante,
			String letraComprobante, int sucuComprobante, Date fechaVencimiento) {
		super(ptoVenta, tipoComprobante, nroComprobante, cuitEmisor, fechaEmision,
				fechaRecepcion, importeComprobante, letraComprobante, sucuComprobante,
				fechaVencimiento);
	}

	public SeguimientoSurComprobante(int ptoVenta, String tipoComprobante,
			String nroComprobante, String cuitEmisor, Date fechaEmision,
			Date fechaRecepcion, BigDecimal importeComprobante,
			String letraComprobante, int sucuComprobante, Date fechaVencimiento,int idSeguimiento) {
		
		super(ptoVenta, tipoComprobante, nroComprobante, cuitEmisor, fechaEmision,
				fechaRecepcion, importeComprobante, letraComprobante, sucuComprobante,
				fechaVencimiento);
		
		seguimientoId=idSeguimiento;
		
	}

	public SeguimientoSurComprobante(int ptoVenta, String tipoComprobante,
			String nroComprobante, String cuitEmisor, Date fechaEmision,
			Date fechaRecepcion, BigDecimal importeComprobante,
			String letraComprobante, int sucuComprobante, Date fechaVencimiento,Empresa empresa) {
		super(ptoVenta, tipoComprobante, nroComprobante, cuitEmisor, fechaEmision,
				fechaRecepcion, importeComprobante, letraComprobante, sucuComprobante,
				fechaVencimiento);
		setAcreedorEmpresa(empresa);
	}
	
	public static SeguimientoSurComprobante getMapping(ResultSet rs) throws SQLException {
		
		SeguimientoSurComprobante a = new SeguimientoSurComprobante();
		
		a.setCuit(rs.getString("cuit"));
		Empresa empresa = new Empresa(rs.getString("cuit"),rs.getString("id_prestador"),rs.getString("razon_social"));
		a.setAcreedorEmpresa(empresa);
		a.setTipoComprobante(rs.getString("compro_tipo"));
        a.setLetraComprobante(rs.getString("compro_letra"));
        a.setSucuComprobante(rs.getInt("compro_sucu"));
        a.setPtoVenta(rs.getInt("id_punto_venta"));
        a.setNroComprobante(rs.getString("compro_nro"));
        a.setFechaEmision(rs.getDate("fecha_emision"));
        a.setFechaRecepcion(rs.getDate("fecha_recibido"));
        a.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
        a.setImporteComprobante(rs.getBigDecimal("importe_original")); 
        
		return a;
	}

	
}
