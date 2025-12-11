package ar.com.ospim.autorizaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConsultaIGSTotal extends ConsultaIGS {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3236642576135669393L;
	
	private int total_registros;

	public int getTotal_registros() {
		return total_registros;
	}

	public void setTotal_registros(int total_registros) {
		this.total_registros = total_registros;
	}
	
	public static ConsultaIGSTotal getMapping(String prefix, ResultSet rs) throws SQLException{
		
		ConsultaIGSTotal cIGSTot = new ConsultaIGSTotal(); 
		
		cIGSTot.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
		cIGSTot.setApellido(rs.getString(prefix + "apellido"));
		cIGSTot.setCuilParametro(rs.getString(prefix + "cuil_parametro"));
		cIGSTot.setCuilTitular(rs.getString(prefix + "cuil_titular"));
		cIGSTot.setDocuNumero(rs.getString(prefix + "docu_numero"));
		cIGSTot.setDocuNumeroParam(rs.getString(prefix + "docu_numero_param"));
		cIGSTot.setDocuTipo(rs.getString(prefix + "docu_tipo"));
		cIGSTot.setDocuTipoParam(rs.getString(prefix + "docu_tipo_param"));
		cIGSTot.setEstado(rs.getString(prefix + "estado"));
		cIGSTot.setIdOspim(rs.getInt(prefix + "id_ospim"));
		cIGSTot.setIdOspimParam(rs.getString(prefix + "id_ospim_param"));
		cIGSTot.setInte(rs.getInt(prefix + "inte"));
		cIGSTot.setInteParam(rs.getInt(prefix + "inte_param"));
		cIGSTot.setIp(rs.getString(prefix + "ip"));
		cIGSTot.setLocalidad(rs.getString(prefix + "localidad"));
		cIGSTot.setNombre(rs.getString(prefix + "nombre"));
		cIGSTot.setNroCredencial(rs.getBigDecimal(prefix + "nro_credencial"));
		cIGSTot.setNroCredencialParam(rs.getBigDecimal(prefix + "nro_credencial_param"));
		cIGSTot.setPlan(rs.getString(prefix + "plan"));
		cIGSTot.setProvincia(rs.getString(prefix + "provincia"));
		cIGSTot.setTelefono(rs.getString(prefix + "telefono"));

		cIGSTot.setTotal_registros(rs.getInt("total_registros_v"));
		
		return cIGSTot;
		
	}
	
}
