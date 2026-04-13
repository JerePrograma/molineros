package ar.com.ospim.liquidaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

import ar.com.ospim.global.beans.Telefono;

public class TelefonoPrestador extends Telefono {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6347655377193799082L;
	
	private String propio ; // D = LugarAt Directo, I = LugarAt Indirecto, P = Propio (sin asociar a LugarAt)

	public String getPropio() {
		return propio;
	}

	public void setPropio(String propio) {
		this.propio = propio;
	}
	
	public TelefonoPrestador(){
		super();
	}
	
	public static TelefonoPrestador getMapping(ResultSet rs) throws SQLException {
		TelefonoPrestador tel = new TelefonoPrestador();		
		tel.setId(rs.getInt("id_telefono"));
		tel.setTipo(rs.getString("tipo_tele"));		
		tel.setCodigoPais(rs.getString("codigo_pais"));
		tel.setCodigoArea(rs.getString("codigo_area"));
		tel.setNumero(rs.getString("numero"));
		tel.setExtension(rs.getString("extension"));
		tel.setObservaciones(rs.getString("observaciones"));
		tel.setPropio(rs.getString("propio"));
		return tel;
	}	
	
}
