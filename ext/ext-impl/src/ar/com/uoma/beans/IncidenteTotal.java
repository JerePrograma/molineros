package ar.com.uoma.beans;

import java.io.Serializable;
import java.sql.ResultSet;


import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Seccional;



public class IncidenteTotal extends Incidente implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -336301035559706713L;
	
	private int total_registros;

	public int getTotal_registros() {
		return total_registros;
	}

	public void setTotal_registros(int total_registros) {
		this.total_registros = total_registros;
	}

	public static IncidenteTotal getMappingIncidentes(ResultSet rs) throws Exception{
		
		IncidenteTotal incidente=new IncidenteTotal();
		Afiliado afiliado=new Afiliado();
		incidente.setFecha(rs.getDate("fecha"));
		afiliado.setCuil_titular(rs.getString("cuil_titular"));
		afiliado.setNombre(rs.getString("nombre"));
		afiliado.setApellido(rs.getString("apellido"));
		afiliado.setDocu_numero(rs.getString("nro_doc"));
		afiliado.setDocumento_tipo(rs.getString("docu_tipo"));
		afiliado.setBaja_fecha(rs.getDate("baja_fecha_afi"));
		Seccional seccional=new Seccional(rs.getInt("id_secc_afi"), rs.getString("descrip_secc_afi"));
		afiliado.setSeccional(seccional);
		incidente.setAfiliado(afiliado);
		incidente.setDetalleIncidente(rs.getString("detalle_incidente"));
		incidente.setIdSeccional(rs.getInt("id_seccional"));
		incidente.setDescripcionSeccional(rs.getString("seccional"));
		incidente.setIdIncidente(rs.getInt("id_incidente"));
		incidente.setFechaRecepcion(rs.getDate("fecha_recepcion"));		
		
		incidente.setTotal_registros(rs.getInt("total_registros_v"));
		
		return incidente;		
	}

	
}
