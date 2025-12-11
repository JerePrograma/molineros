package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.PrestacionesEquipoInterdisciplinario;
import ar.com.ospim.autorizaciones.exceptions.PrestacionesEquipoInterdisciplinarioException;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;

import com.liferay.portal.kernel.util.Constants;

public class ListaEquipoInterdisciplinarioAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(ListaEquipoInterdisciplinarioAction .class);

	public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		double importe  = ParamUtil.getDouble(renderRequest, "importe");
		double total= ParamUtil.getDouble(renderRequest, "total");
		int cantidad  = ParamUtil.getInteger(renderRequest, "cantidad");
		String prestacion= ParamUtil.getString(renderRequest, "prestacion");
		int  tiponomenclador= ParamUtil.getInteger(renderRequest, "tiponomenclador");
		String nombrePrestacion= ParamUtil.getString(renderRequest, "nombre_prestacion");
		int idValorTipoPrestacion= ParamUtil.getInteger(renderRequest, "idTipoPrestacion");
		String detalleTipoPrestacion = ParamUtil.getString(renderRequest, "detalleTipoPrestacion");
		int idPrestacion=0;
		
				
//		me aseguro sea un numero negativo para no confundir con IDs de BD
		Random r = new Random(System.currentTimeMillis());
		int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
		if(idAux > 0){
			idAux = (-1)*idAux;
		}	
		
		if (tiponomenclador==1){
			// buscar id de la prestacion
			idPrestacion=0;
			List<Nomenclador> nomencladores = NomencladorServiceUtil.getListaNomenclador(0,"",0, prestacion,false,"");
			 
			   for(Nomenclador nom:nomencladores){			   				   
				   if(prestacion.equals(nom.getCodigo())  ){
					   idPrestacion= nom.getId_prestacion();
				   }   
			   }
            if ( idPrestacion==0){
            	_log.debug("Error en la busqueda de id prestacion : ");
            }		
		}	
		
		
		PrestacionesEquipoInterdisciplinario  prestacionequipo = new PrestacionesEquipoInterdisciplinario (total ,cantidad , importe, idPrestacion,   nombrePrestacion,prestacion, idValorTipoPrestacion , detalleTipoPrestacion );
		prestacionequipo.setEstado(PrestacionesEquipoInterdisciplinario.ESTADOS.NUEVO );
	
		prestacionequipo.setIdregistro(idAux); 
		
		_log.debug("Agrega prestacion: " + prestacionequipo.toString());
		
		@SuppressWarnings("unchecked")
		List<PrestacionesEquipoInterdisciplinario> prestacionesEquipo= (ArrayList<PrestacionesEquipoInterdisciplinario>) session.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION  );

		session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION);
		
		if(prestacionesEquipo== null){
			prestacionesEquipo= new ArrayList<PrestacionesEquipoInterdisciplinario >();
		}
		
		boolean validaprestacion =true;
		
		try{
			validaprestacion = validaPrestacionEquipo (prestacionequipo, prestacionesEquipo );
			
			if(validaprestacion ){
				prestacionesEquipo.add(prestacionequipo);
			}
			
		}catch (PrestacionesEquipoInterdisciplinarioException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}
			
		//pongo la lista en session
		
		session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION , prestacionesEquipo);	
		
 		
		return mapping.findForward(getForward(renderRequest,
				"portlet.autorizaciones.equipointerdisciplinario.prestacion_equipointer"));
		
	}


	private boolean validaPrestacionEquipo  (PrestacionesEquipoInterdisciplinario  nuevaPrestacion , List<PrestacionesEquipoInterdisciplinario > lista)
			throws SystemException, PrestacionesEquipoInterdisciplinarioException {

		boolean resultado = true;		
		boolean cond1 ;
			 
			for (int i = 0; i < lista.size(); i++) {	    
		 		
				PrestacionesEquipoInterdisciplinario mat = (PrestacionesEquipoInterdisciplinario) lista.get(i);
		 		cond1 = mat.getEstado() != PrestacionesEquipoInterdisciplinario.ESTADOS.BAJA && mat.getId_prestacion() == nuevaPrestacion.getId_prestacion();
		 		
				if (cond1) {					
					resultado = false;					
		  			throw new PrestacionesEquipoInterdisciplinarioException();
		 		}				
		 		
			} 
		
		return resultado;
	}
		
}