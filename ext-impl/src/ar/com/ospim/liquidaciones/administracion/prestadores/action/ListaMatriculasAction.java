package ar.com.ospim.liquidaciones.administracion.prestadores.action;

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

import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.administracion.prestadores.exception.MatriculaNacionalPrestadorException;
import ar.com.ospim.liquidaciones.administracion.prestadores.exception.MatriculaProvincialPrestadorException;
import ar.com.ospim.liquidaciones.beans.MatriculaPrestador;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class ListaMatriculasAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(ListaMatriculasAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		String matTipo = ParamUtil.getString(renderRequest, "matTipo");
		int matNumero = ParamUtil.getInteger(renderRequest, "matNumero");
		int matProvincia = ParamUtil.getInteger(renderRequest, "matProvincia");
		String provinciaDesc = ParamUtil.getString(renderRequest, "descProvincia");
		boolean presentoCopiaMatricula = ParamUtil.getBoolean(renderRequest, "presentoCopiaMatricula"); 
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");

		String matFechaVtoDia = ParamUtil.getString(renderRequest,"matFechaVtoDia");
		String matFechaVtoMes = ParamUtil.getString(renderRequest,"matFechaVtoMes");
		String matFechaVtoAnio = ParamUtil.getString(renderRequest,"matFechaVtoAnio");
		Date fechaVtoMatricula = null;

		if (matTipo.contains("R")){
			try {
				fechaVtoMatricula = formatoDePeriodo.parse(matFechaVtoDia + "/"
						+ (Integer.parseInt(matFechaVtoMes) + 1) + "/"
						+ matFechaVtoAnio);
			} catch (Exception e) {
				fechaVtoMatricula = null;
			}
		}
		
//		me aseguro sea un numero negativo para no confundir con IDs de BD
		Random r = new Random(System.currentTimeMillis());
		int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
		if(idAux > 0){
			idAux = (-1)*idAux;
		}
		
		MatriculaPrestador matPrest = new MatriculaPrestador(matTipo, matNumero, presentoCopiaMatricula, fechaVtoMatricula, 
					matTipo.equalsIgnoreCase("P")?new Provincia(matProvincia,provinciaDesc):null, null);
		matPrest.setEstado(MatriculaPrestador.ESTADOS.NUEVO);
		matPrest.setIdMatricula(idAux);
		
		_log.debug("Agrega Matricula: " + matPrest.toString());
		
		@SuppressWarnings("unchecked")
		List<MatriculaPrestador> matriculasPrestador = (ArrayList<MatriculaPrestador>) session.getAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION);

		session.removeAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION);
		
		if(matriculasPrestador == null){
			matriculasPrestador = new ArrayList<MatriculaPrestador>();
		}

		
		boolean validaMatriculas=true;
		
		try{
			validaMatriculas = validaMatriculas(matPrest, matriculasPrestador);
			
			if(validaMatriculas){
				matriculasPrestador.add(matPrest);
			}
			
		}catch (MatriculaNacionalPrestadorException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}catch (MatriculaProvincialPrestadorException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}			
		
		//pongo la lista en session
		
		session.setAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION, matriculasPrestador);	
		
//		return mapping.findForward("portlet.liquidaciones.matricula.prestador");
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.matricula.prestador"));
	}
	
	private boolean validaMatriculas(MatriculaPrestador nuevaMat, List<MatriculaPrestador> lista)
			throws SystemException, MatriculaProvincialPrestadorException, MatriculaNacionalPrestadorException{

		boolean resultado = true;
		
			//Verificar que solo tenga una matricula por provincia y una sola nacional si tiene mas de una tira la bronca (by GF)
			for (int i = 0; i < lista.size(); i++) {	    
		 		
				MatriculaPrestador mat = (MatriculaPrestador) lista.get(i);
		 		
				if (mat.getEstado() != MatriculaPrestador.ESTADOS.BAJA 
		 				&& mat.getTipo().contains(nuevaMat.getTipo()) 
		 				&& nuevaMat.getTipo().contains("N")){
					
					resultado = false;
					
		  			throw new MatriculaNacionalPrestadorException();
		 		}
				
		 		if (mat.getEstado() != MatriculaPrestador.ESTADOS.BAJA 
		 				&& mat.getTipo().contains(nuevaMat.getTipo()) 
		 				&& nuevaMat.getTipo().contains("P") 
		 				&& mat.getProvincia().getId()==nuevaMat.getProvincia().getId()){
		 			
		 			resultado = false;
		 			
		  			throw new MatriculaProvincialPrestadorException();
		  		}
			} 
		
		return resultado;
	}
		
}