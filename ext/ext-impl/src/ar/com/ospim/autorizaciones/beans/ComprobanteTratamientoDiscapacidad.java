package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;

public class ComprobanteTratamientoDiscapacidad implements Serializable{
	private static final long serialVersionUID = -6511853820566306801L;
	
	private LiquidacionPrestacion liquidacionPrestacion;
	private Prestador prestador;
	private Integer tratamientoId;
	private Integer seguimientoId;
	private Medicamento medicamento;
	
	public static ComprobanteTratamientoDiscapacidad getMapping(ResultSet rs) throws SQLException {
		
		ComprobanteTratamientoDiscapacidad archivo = new ComprobanteTratamientoDiscapacidad();
		archivo.setTratamientoId(rs.getInt("id_tratamiento"));
		archivo.setSeguimientoId(rs.getInt("id_seguimiento"));
		LiquidacionPrestacion lp = new LiquidacionPrestacion();
		lp.setCuil_titular(rs.getString("cuil_titular"));
		lp.setCantidad(rs.getBigDecimal("cantidad"));
		lp.setId_liquidacion(rs.getInt("id_liquidacion"));
		lp.setImporte(rs.getBigDecimal("importe"));
		lp.setInte(rs.getInt("inte"));
        lp.setOrden(rs.getInt("orden"));
        lp.setId_prestacion(rs.getInt("id_prestacion"));
		archivo.setLiquidacionPrestacion(lp);
		
		Prestador pr= new Prestador();
		pr.setId_prestador(rs.getInt("id_prestador"));
		try{
			pr.setDescripcion(rs.getString("descripcion_prestador"));
			pr.setCuit(rs.getString("cuit_prestador"));
		}catch(Exception e){
			
		}
		archivo.setPrestador(pr);
		
		return archivo;
		
	}

	public LiquidacionPrestacion getLiquidacionPrestacion() {
		return liquidacionPrestacion;
	}

	public void setLiquidacionPrestacion(LiquidacionPrestacion liquidacionPrestacion) {
		this.liquidacionPrestacion = liquidacionPrestacion;
	}

	public Prestador getPrestador() {
		return prestador;
	}


	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public Integer getTratamientoId() {
		return tratamientoId;
	}

	public void setTratamientoId(Integer tratamientoId) {
		this.tratamientoId = tratamientoId;
	}

	public Integer getSeguimientoId() {
		return seguimientoId;
	}

	public void setSeguimientoId(Integer seguimientoId) {
		this.seguimientoId = seguimientoId;
	}

	public Medicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(Medicamento medicamento) {
		this.medicamento = medicamento;
	}
	
	
}
