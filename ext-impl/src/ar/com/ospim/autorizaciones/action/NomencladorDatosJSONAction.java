package ar.com.ospim.autorizaciones.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;

public class NomencladorDatosJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int clase = ParamUtil.getInteger(req, "clase");
		String codigo = ParamUtil.getString(req, "codigo");
		boolean esDiscapacidad = ParamUtil.getBoolean(req, "esdiscapacidad");
		Double numero=0D;
		Integer idPrestacion=0;
		boolean requiereAutorizacion=false;
		boolean requiereHistoriaClinica=false;
		boolean requiereEstudiosComplementarios=false;
		boolean requiereBiopsia=false;
		boolean requiereAnatomiaPatologica=false;
		boolean supra=false;
		boolean cirugia=false;
		try {
			
			//List<Nomenclador>nomencladores=NomencladorServiceUtil.getListaNomencladorPreautorizaciones(clase, null, 0, codigo, false, null);
			List<Nomenclador>nomencladores= new ArrayList<Nomenclador>();
			if (esDiscapacidad) {
				nomencladores = NomencladorServiceUtil.getListaNomencladorMarcaReinLiq(clase,null,0,codigo,false,"",6);
			}else{
				nomencladores = NomencladorServiceUtil.getListaNomencladorPreautorizaciones(clase, null, 0, codigo, false, null);
			}
			if(nomencladores!=null && nomencladores.size()>0){
				
				numero = nomencladores.get(0).getImporte();
				requiereAutorizacion=nomencladores.get(0).getRequiereAutorizacion();
				supra=nomencladores.get(0).isSupra();
				idPrestacion=nomencladores.get(0).getId_prestacion();
				cirugia=nomencladores.get(0).isCirugia();
				
				Nomenclador m = NomencladorServiceUtil.getEstudiosRequeridosPorId(idPrestacion);
				requiereHistoriaClinica=m.isRequiereHistoriaClinica();
				requiereEstudiosComplementarios=m.isRequiereEstudiosComplementarios();
				requiereBiopsia=m.isRequiereBiopsia();
				requiereAnatomiaPatologica=m.isRequiereAnatomiaPatologica();
			}
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		if(null==numero){
			numero=0D;
		}
		
		return "{ \"importe\" : \""+ numero+
				 "\",\"requiereautorizacion\" : \"" + requiereAutorizacion +
				 "\",\"requierehistoriaclinica\" : \"" + requiereHistoriaClinica +
				 "\",\"requiereestudioscomplementarios\" : \"" + requiereEstudiosComplementarios +
				 "\",\"requierebiopsia\" : \"" + requiereBiopsia +
				 "\",\"requiereanatomiapatologica\" : \"" + requiereAnatomiaPatologica +
				 "\",\"idprestacion\" : \"" + idPrestacion +
				 "\",\"supra\" : \"" + supra +
				 "\",\"cirugia\" : \"" + cirugia +
				"\"}";
		
	}
	
}
