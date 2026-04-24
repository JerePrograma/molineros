package ar.com.ospim.prestadores.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.prestadores.exception.MatriculaNacionalPrestadorException;
import ar.com.ospim.prestadores.exception.MatriculaProvincialPrestadorException;
import ar.com.ospim.liquidaciones.beans.MatriculaPrestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author Gustavo Fernandez
 */

public class EditarMatriculasAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(EditarMatriculasAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
//		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
//		
//		int idMatricula = ParamUtil.getInteger(renderRequest, "idMatricula");
//		
//		String matTipo = ParamUtil.getString(renderRequest, "matTipoN");
//		int matNumero = ParamUtil.getInteger(renderRequest, "matNumeroN");
//		int matProvincia = ParamUtil.getInteger(renderRequest, "matProvinciaN");
//		boolean presentoCopiaMatricula =ParamUtil.getBoolean(renderRequest, "presentoCopiaMatriculaN"); 
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTimeInMillis(System.currentTimeMillis());
//		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
//
//		String matFechaVtoDia = ParamUtil.getString(renderRequest,
//				"matriculaFechaVtoDiaN");
//		String matFechaVtoMes = ParamUtil.getString(renderRequest,
//				"matriculaFechaVtoMesN");
//		String matFechaVtoAnio = ParamUtil.getString(renderRequest,
//				"matriculaFechaVtoAnioN");
//		Date fechaVtoMatricula = null;
//		if (matTipo.contains("R")){
//			try {
//				fechaVtoMatricula = formatoDePeriodo.parse(matFechaVtoDia + "/"
//						+ (Integer.parseInt(matFechaVtoMes) + 1) + "/"
//						+ matFechaVtoAnio);
//			} catch (Exception e) {
//				fechaVtoMatricula = null;
//			}
//		}
//		
//		boolean bajaEnBase = false;
//		
//		try {			
//			@SuppressWarnings("unchecked")
//			//traigo la matricula cargada si es que la hay
//			List<MatriculaPrestador> listaVieja = (ArrayList<MatriculaPrestador>) session.getAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_SESSION);
//			MatriculaPrestador matriculasPrestador = new MatriculaPrestador();
//			
//			//creo una lista con los datos que pasa el usuario
//			ArrayList<MatriculaPrestador> listaMatriculasPrestador = new ArrayList <MatriculaPrestador>();
//			
//				@SuppressWarnings("unused")
//				boolean validaMatriculas=false;
//				try{
//					
//					for (int i=0; i<listaVieja.size(); i++) {	    
//				 		MatriculaPrestador mat = (MatriculaPrestador) listaVieja.get(i);
//						if(mat.getIdMatricula()==idMatricula){
//							listaVieja.remove(i);							
//		                }
//					}
//					
//					validaMatriculas = PrestadorServiceUtil.getValidaMatriculas(matTipo, matNumero, matProvincia, listaVieja, bajaEnBase);
//
//					listaMatriculasPrestador.add(matriculasPrestador);
//					matriculasPrestador.setTipo(matTipo);
//					matriculasPrestador.setNumero(matNumero);
//					matriculasPrestador.setProvincia(matProvincia);
//					matriculasPrestador.setBajaEnBase(bajaEnBase);
//					matriculasPrestador.setPresentaCopia(presentoCopiaMatricula);
//					matriculasPrestador.setFechaVto(fechaVtoMatricula);
//					matriculasPrestador.setIdMatricula(idMatricula);
//					
//				}catch (MatriculaNacionalPrestadorException e) {
//					SessionErrors.add(renderRequest, e.getClass().getName());
//				}catch (MatriculaProvincialPrestadorException e) {
//					SessionErrors.add(renderRequest, e.getClass().getName());
//				}catch (Exception e) {
//				}			
//				
//				// si existe una listaVieja en session la agrego a la nuevaLista
//				if (listaVieja!=null){
//					listaMatriculasPrestador.addAll(listaVieja);
//				}
//			
//			//pongo la lista en session
//			session.removeAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_SESSION);
//			session.setAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_SESSION, listaMatriculasPrestador);
//			
//		} 
//		catch (Exception e) {
//			_log.error(e);
//		}		
		
		return mapping.findForward("portlet.liquidaciones.matricula.prestador");
	}
		
}