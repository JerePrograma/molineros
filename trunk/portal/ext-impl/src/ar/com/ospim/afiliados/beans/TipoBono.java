package ar.com.ospim.afiliados.beans;

import java.sql.ResultSet;
import java.sql.SQLException;


/***
 * 
 * @author sergio
 *
 */
public class TipoBono {

	protected int tipo_bono;
	protected String tipo_bono_string;
	
	public TipoBono(int tipoBono, String descripcion) {
		super();
		tipo_bono = tipoBono; 
		tipo_bono_string = descripcion; // descripcion
	}

	public TipoBono() {
		// TODO Auto-generated constructor stub
	}

	public int getTipo_bono() {
		return tipo_bono;
	}

	public void setTipo_bono(int tipoBono) {
		tipo_bono = tipoBono;
	}

	public String getTipo_bono_string() {
		return tipo_bono_string;
	}

	public void setTipo_bono_string(String tipo_bono_string) {
		this.tipo_bono_string = tipo_bono_string;
	}

	public static TipoBono getMapping(ResultSet rs) throws SQLException{
		
		TipoBono tb = new TipoBono(rs.getInt("tipo_bono"), rs.getString("descripcion"));
		
		return tb;
	}
	
}