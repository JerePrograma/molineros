/**
 */

package ar.com.ospim.prestadores.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;


/**
 * <a href="BuscarPrestadoresAction.java.html"><b><i>View Source</i></b></a>
 * <p>
		setForward(actionRequest, "portlet.prestadores.result.search");
 * 
 * @author Martin Moreyra
 * @modif SVA
 */
public class BuscarPrestadoresAction extends PrestadoresBaseAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarPrestadoresAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.prestadores.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			int id;
			String cuit = null;
			String descripcion = null;
			boolean soloVigentes=false;
			int provincia = 0;
			int localidad = 0;
			String hospital=null;

			if (null != renderRequest.getParameter("cuit")) {
				cuit = renderRequest.getParameter("cuit").trim().length() > 0 ? renderRequest
						.getParameter("cuit")
						: null;
			}
			if (null != renderRequest.getParameter("descripcion")) {
				descripcion = renderRequest.getParameter("descripcion").trim().length() > 0 ? renderRequest
						.getParameter("descripcion")
						: null;
//				if(descripcion!=null && descripcion.contains("%26")){
//					descripcion.replace("%26", "&");
//				}

//				FIXME sacar espacio en blanco y ñ, Ñ de la busqueda, se podría mejorar ? 		
				if(descripcion!=null && (descripcion.contains("%C3%B1") || descripcion.contains("%C3%91") ) ){
//					descripcion.replace("%C3%B1", "ñ");
//					descripcion.replaceAll("%C3%B1", "ñ");

					String[] spliteoEnies = descripcion.split("%C3%B1");
					if(spliteoEnies == null){
						spliteoEnies = descripcion.split("%C3%91");
					}
					descripcion = "";
					for (int i = 0; i < spliteoEnies.length; i++) {
						descripcion = descripcion + spliteoEnies[i] + (i < spliteoEnies.length-1?"ñ":""); 
					}
				}
				if(descripcion!=null && descripcion.contains("%20")){
					String[] spliteoBlancos = descripcion.split("%20");
					descripcion = "";
					for (int i = 0; i < spliteoBlancos.length; i++) {
						descripcion = descripcion + spliteoBlancos[i] + (i < spliteoBlancos.length-1?" ":""); 
					}
//					descripcion.replace("%20", " ");
				}
			}		
			if (null != renderRequest.getParameter("solo_vigentes")) {
				soloVigentes = ParamUtil.getBoolean(renderRequest,"solo_vigentes");
			}	
			id = ParamUtil.getInteger(renderRequest,  "id_prestador" , 0);
			provincia = ParamUtil.getInteger(renderRequest,  "provincia" , 0);
			localidad = ParamUtil.getInteger(renderRequest,  "localidad" , 0);

			int profesion = ParamUtil.getInteger(renderRequest,  "profesion" , 0);
			int especialidad = ParamUtil.getInteger(renderRequest,  "especialidad" , 0);
			int subEspecialidad = ParamUtil.getInteger(renderRequest,  "subEspecialidad" , 0);
			int tipoPrestado = ParamUtil.getInteger(renderRequest,  "tipoPrestador" , 0);

			if (null != renderRequest.getParameter("hospital")) {
				hospital = renderRequest.getParameter("hospital").trim().length() > 0 ? renderRequest
						.getParameter("hospital")
						: null;
			}

			boolean soloHabilitadosCotizar =
					ParamUtil.getBoolean(
							renderRequest,
							"solicitarCotizacionFiltro",
							false
					);

			List<Prestador> busqueda = PrestadorServiceUtil
					.getPrestadores(
							id,
							cuit,
							descripcion,
							provincia,
							localidad,
							soloVigentes,
							profesion,
							especialidad,
							subEspecialidad,
							tipoPrestado,
							hospital,
							soloHabilitadosCotizar
					);

			renderRequest.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_PRESTADORES);
			renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_PRESTADORES, busqueda);			

		} catch (Exception e) {
			_log.error("Error buscando prestadores", e);
		}

		return mapping.findForward("portlet.prestadores.result.search");
	}
}
