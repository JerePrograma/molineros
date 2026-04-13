package ar.com.ospim.afiliados.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.beans.TipoDiscapacidad;
import ar.com.ospim.util.StringUtils;

public class DetalleDiscapacidad implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2223035522089140243L;
	
	private String cuil_titular;
	private int inte;    
	private String diagnostico;
    private boolean dependencia;    
    private String telefono_contacto;
    private String cie_diez;
    private String cieDiezDescripcion ;
    private List<TipoDiscapacidad> tiposDiscapacidad;
    
    public DetalleDiscapacidad() {
    	diagnostico = "";
    	dependencia = false;
    	telefono_contacto = "";
    	setTiposDiscapacidad(new ArrayList<TipoDiscapacidad>());
    }
    
	public DetalleDiscapacidad(String cuilTitular, int inte,
			String diagnostico, boolean dependencia, String telefonoContacto, 
			String cie_diez, String splitearIdsTiposDiscapacidades) {	
		
		
		this.cuil_titular = cuilTitular;
		this.inte = inte;
		this.diagnostico = diagnostico;
		this.dependencia = dependencia;
		this.telefono_contacto = telefonoContacto;
		this.cie_diez = cie_diez;
		
		this.armarListaTiposDiscapacidades(splitearIdsTiposDiscapacidades);
		
	}
	
	public String getCuil_titular() {
		return cuil_titular;
	}

	public void setCuil_titular(String cuilTitular) {
		cuil_titular = cuilTitular;
	}

	public int getInte() {
		return inte;
	}

	public void setInte(int inte) {
		this.inte = inte;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public boolean isDependencia() {
		return dependencia;
	}

	public void setDependencia(boolean dependencia) {
		this.dependencia = dependencia;
	}

	public String getTelefono_contacto() {
		return telefono_contacto;
	}

	public void setTelefono_contacto(String telefonoContacto) {
		telefono_contacto = telefonoContacto;
	}

	public String getCie_diez() {
		return cie_diez;
	}

	public void setCie_diez(String cieDiez) {
		cie_diez = cieDiez;
	}
	
	public String getCie_diezDescripcion () {
		return cieDiezDescripcion ;
	}

	public void setCie_diezDescripcion(String cieDiezDescrip) {
		cieDiezDescripcion = cieDiezDescrip;
	}
		

	public static DetalleDiscapacidad getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}
	
	public static DetalleDiscapacidad getMapping(ResultSet rs,
			String prefix) throws SQLException {
		
		DetalleDiscapacidad dd = new DetalleDiscapacidad(
				rs.getString(prefix + "cuil_titular"), 
				rs.getInt(prefix + "inte"), 
				rs.getString(prefix + "diagnostico"), 
				rs.getBoolean(prefix+"dependencia"), 
				rs.getString(prefix+"telefono_contacto"), 
				rs.getString(prefix+"cie_diez"), 
				rs.getString(prefix+"ids_tipo_discapacidad"));
		
		return dd;
	}

	public List<TipoDiscapacidad> getTiposDiscapacidad() {
		return tiposDiscapacidad;
	}

	public void setTiposDiscapacidad(List<TipoDiscapacidad> tiposDiscapacidad) {
		this.tiposDiscapacidad = tiposDiscapacidad;
	}
	
	public void armarListaTiposDiscapacidades(String splitearIdsTiposDiscapacidades){
		
		TipoDiscapacidad td = null;
		ArrayList<TipoDiscapacidad> tiposDiscap = new ArrayList<TipoDiscapacidad>();
		List<TipoDiscapacidad> tiposDiscapacidades = null; 
		int id, pos;
		
		if(StringUtils.checkNotEmpty(splitearIdsTiposDiscapacidades)){
			String[] tipDisAux = splitearIdsTiposDiscapacidades.split(",");
			if(tipDisAux !=null){
				tiposDiscapacidades = TraeListasServiceUtil.getTiposDiscapacidad();
				
				for (int i = 0; i < tipDisAux.length; i++) {
					id = Integer.parseInt(tipDisAux[i]);
					td = new TipoDiscapacidad(id, "");
					pos = tiposDiscapacidades.indexOf(td);
					td = tiposDiscapacidades.get(pos);
					
					tiposDiscap.add(td);
				}
				
				this.setTiposDiscapacidad(tiposDiscap);
				
			}else{
				this.tiposDiscapacidad = null;
			}
		}	
	}
	
	public String getTiposDiscapacidadDelAfiliado(){
		String tipos = null;
		
		if(this.tiposDiscapacidad != null && this.tiposDiscapacidad.size() > 0){
			tipos = "";
			for (Iterator<TipoDiscapacidad> iterator = this.tiposDiscapacidad.iterator(); iterator.hasNext();) {
				TipoDiscapacidad td = iterator.next();
				tipos = tipos.concat(td.getId() +",");
			}
			tipos = tipos.substring(0, tipos.length()-1);
		}
			
		return tipos;	
	}
	
	public String getDescripcionTiposDiscapacidadDelAfiliado(){
		String tipos = null;
		
		if(this.tiposDiscapacidad != null && this.tiposDiscapacidad.size() > 0){
			tipos = "";
			for (Iterator<TipoDiscapacidad> iterator = this.tiposDiscapacidad.iterator(); iterator.hasNext();) {
				TipoDiscapacidad td = iterator.next();
				tipos = tipos.concat(td.getDescripcion() +",");
			}
			tipos = tipos.substring(0, tipos.length()-1);
		}
			
		return tipos;	
	}
}