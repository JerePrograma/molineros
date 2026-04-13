package ar.com.ospim.afip.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReporteDeudaEmpresaDet 
	   extends ReporteDeudaEmpresa
	   implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7114882173664399218L;
	private int id;
	private int idCab;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdCab() {
		return idCab;
	}
	public void setIdCab(int idCab) {
		this.idCab = idCab;
	}
	
	public static ReporteDeudaEmpresaDet getMapping3(ResultSet rs) throws SQLException{
		
		ReporteDeudaEmpresaDet deuda = new ReporteDeudaEmpresaDet();
		
		deuda.setPeriodo(rs.getDate("periodo"));
		deuda.setCuit(rs.getString("cuit"));
		deuda.setRazonSocial(rs.getString("razon_soc"));
		deuda.setRamo(rs.getInt("ramo"));
		deuda.setCantAfiliadosDeclarados_81(rs.getInt("total_afi_81"));
		deuda.setCantAfiliadosDeclarados_765(rs.getInt("total_afi_765"));
		deuda.setCantAfiliadosDeclarados(rs.getInt("total_empleados"));
		deuda.setRemDeclarada_81(rs.getBigDecimal("total_rem_81"));
		deuda.setRemDeclarada_765(rs.getBigDecimal("total_rem_765"));
		deuda.setRemDeclarada(rs.getBigDecimal("total_remuneracion"));
		deuda.setCalculado_810(rs.getBigDecimal("calculado_81"));
		deuda.setCalculado_765(rs.getBigDecimal("calculado_765"));
		deuda.setTotal_calculado(rs.getBigDecimal("total_calculado"));
		deuda.setPagado(rs.getBigDecimal("pagado"));
		deuda.setPagado_acta_convenio(rs.getBigDecimal("pagado_acta_convenio"));
		deuda.setPorc_pagado(rs.getBigDecimal("porc_pagado"));
		deuda.setDeuda(rs.getBigDecimal("deuda"));
		deuda.setCalle(rs.getString("calle"));
		deuda.setNumero(rs.getString("numero"));
		deuda.setPiso(rs.getString("piso"));
		deuda.setDpto(rs.getString("dpto"));
		deuda.setLocalidad(rs.getString("localidad"));
		deuda.setProvincia(rs.getString("provincia"));
		deuda.setCodPostal(rs.getString("cod_postal"));
//		deuda.setId(id);
//		deuda.setIdCab(idCab);
		
		return deuda;
	}
}
