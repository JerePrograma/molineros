package ar.com.ospim.autorizaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;

public class TratamientoDiscapacidadSeguimiento extends TratamientoDiscapacidad {
    private List<ComprobanteTratamientoDiscapacidad>comprobantes;	
    
	public TratamientoDiscapacidadSeguimiento(){
       super();
       comprobantes= new ArrayList<ComprobanteTratamientoDiscapacidad>();
	}

	public List<ComprobanteTratamientoDiscapacidad> getComprobantes() {
		return comprobantes;
	}

	public void setComprobantes(
			List<ComprobanteTratamientoDiscapacidad> comprobantes) {
		this.comprobantes = comprobantes;
	}

	public static TratamientoDiscapacidadSeguimiento getMapping(ResultSet rs) throws SQLException {
		
		TratamientoDiscapacidadSeguimiento archivo = new TratamientoDiscapacidadSeguimiento();
/*		
		archivo.setDescripcion(rs.getString("descripcion"));
		archivo.setId(rs.getInt("id"));
*/		
		return archivo;
	}
	
	public  TratamientoDiscapacidadSeguimiento clonar(TratamientoDiscapacidad ori){
		TratamientoDiscapacidadSeguimiento ret= new TratamientoDiscapacidadSeguimiento();
	    ret.setId_tratamiento(ori.getId_tratamiento());
	    ret.setAfiliado(ori.getAfiliado());
	    ret.setPrestacion(ori.getPrestacion());
	    ret.setCantidad(ori.getCantidad());
	    ret.setImporte_total(ori.getImporte_total());
	    ret.setPeriodicidad(ori.getPeriodicidad());
	    ret.setPeriodo_desde(ori.getPeriodo_desde());
	    ret.setPeriodo_hasta(ori.getPeriodo_hasta());
	    ret.setAcreedor(ori.getAcreedor());
	    ret.setImporte_tercerizado(ori.getImporte_tercerizado());
	    
	    return ret;
	}
}