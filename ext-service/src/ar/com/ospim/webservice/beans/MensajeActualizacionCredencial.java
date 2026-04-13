package ar.com.ospim.webservice.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

public class MensajeActualizacionCredencial implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5718301501400857764L;
	
	private CabeceraActualizacionCredencial cabecera;
	private DetalleActualizacionCredencial detalle;
	
	public CabeceraActualizacionCredencial getCabecera() {
		return cabecera;
	}
	public void setCabecera(CabeceraActualizacionCredencial cabecera) {
		this.cabecera = cabecera;
	}
	public DetalleActualizacionCredencial getDetalle() {
		return detalle;
	}
	
	public void setDetalle(DetalleActualizacionCredencial detalle) {
		this.detalle = detalle;
	}
	@Override
	public String toString() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		String mensaje = "";
		AfiliacionPrevencionDTO dto = null;
		
		if(cabecera != null){
			if(cabecera.getCodigoCuenta() != null){
				mensaje += " CodCuenta= " +cabecera.getCodigoCuenta(); 
			}
			if(cabecera.getFechaVigencia() != null){
				try{
					mensaje += " Vigencia= " +sdf.format(cabecera.getFechaVigencia());
				}catch (Exception e) {
//					nada, va sin fecha
				}
			}
		}
		if(detalle != null){
			dto = detalle.getAfiliacion();
			if(dto != null){
				if(dto.getCuilTitular() != null){
					mensaje += " Cuil titular= " + dto.getCuilTitular();
				}
				if(dto.getIntePrevencion() != null){
					mensaje += " /" + dto.getIntePrevencion();
				}
				if(dto.getNroDocumento() != null){
					mensaje += " Nro. Documento= " + dto.getNroDocumento();
				}
				if(dto.getNroCredencial() != null){
					mensaje += " Nro. Credencial= " + dto.getNroCredencial();
				}
				if(dto.getNroSocio() != null){
					mensaje += " Nro. Socio= " + dto.getNroSocio();
				}
			}
		}
		return mensaje;
	}
	
	
}
