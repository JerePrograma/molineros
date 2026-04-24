package ar.com.ospim.prestadores.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.Prestador.TipoPrestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="PrestadoresBaseAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * @edit SVA
 * 
 */
public class PrestadoresBaseAction extends PortletAction {

	protected Prestador getPrestadorEntry(HttpServletRequest request)
			throws Exception {

		Prestador prestador = null;
		String idString = request.getParameter("prestador_id");
		if (idString == null || idString.trim().equals("")){
			idString = (String)request.getAttribute("prestador_id");
		}
		if (idString != null && !idString.trim().equals("")) {
			int id = Integer.parseInt(idString);
			if (id > 0) {
				prestador = PrestadorServiceUtil.getPrestador(id);
			}
		}
		return prestador;
	}

	public Prestador getOtrosDatosFromRequest(HttpServletRequest req,
			Prestador prestador) {

		return prestador;
	}

	public Prestador getPrestadorFromRequest(HttpServletRequest req, Prestador prestador) {
		
		String cuit = ParamUtil.getString(req, "cuit");
		String desc = ParamUtil.getString(req, "desc");
		String ciaSeguro = ParamUtil.getString(req, "compania_seguro");
		boolean seguroCobertura = ParamUtil.getBoolean(req, "seguro_cobertura");
		boolean certificacionProfesional = ParamUtil.getBoolean(req, "certificacion"); 
		String otorgaCertificacion = ParamUtil.getString(req, "otorga_cert");

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");

		String seguroFechaVtoDia = ParamUtil.getString(req,"seguroFechaVtoDia");
		String seguroFechaVtoMes = ParamUtil.getString(req,"seguroFechaVtoMes");
		String seguroFechaVtoAnio = ParamUtil.getString(req,"seguroFechaVtoAnio");
		Date fechaVtoSeguro = null;
		try {
			fechaVtoSeguro = formatoDePeriodo.parse(seguroFechaVtoDia + "/"
					+ (Integer.parseInt(seguroFechaVtoMes) + 1) + "/"
					+ seguroFechaVtoAnio);
		} catch (Exception e) {
			fechaVtoSeguro = null;
		}
		
		String certificacionFechaVtoDia = ParamUtil.getString(req,"certificacionFechaVtoDia");
		String certificacionFechaVtoMes = ParamUtil.getString(req,"certificacionFechaVtoMes");
		String certificacionFechaVtoAnio = ParamUtil.getString(req,"certificacionFechaVtoAnio");
		Date fechaVtoCertificacion = null;
		try {
			fechaVtoCertificacion = formatoDePeriodo.parse(certificacionFechaVtoDia + "/"
					+ (Integer.parseInt(certificacionFechaVtoMes) + 1) + "/"
					+ certificacionFechaVtoAnio);
		} catch (Exception e) {
			fechaVtoCertificacion = null;
		}
		String contacto = ParamUtil.getString(req, "contacto");
		String obs = ParamUtil.getString(req, "observaciones");
		int idPrestador = ParamUtil.getInteger(req, "id_prestador");
		String codigoHospital = ParamUtil.getString(req, "codigo_hospital");
		int idTipoPrest = ParamUtil.getInteger(req, "tipo_prestador");
		TipoPrestador tipoPrestador = new TipoPrestador(idTipoPrest, "");
		
		String cbu = ParamUtil.getString(req,"cbu");
		
		prestador = new Prestador(idPrestador, cuit, tipoPrestador, contacto.toUpperCase(), obs, 
				desc.toUpperCase(), codigoHospital, ciaSeguro.toUpperCase(), seguroCobertura, certificacionProfesional, 
				otorgaCertificacion.toUpperCase(), fechaVtoSeguro, fechaVtoCertificacion);
		if(cbu!=null) {
			prestador.setCbu(cbu);
		}
//		int seccional = ParamUtil.getInteger(req, "id_seccional");
		
		
		
		return prestador;
	}

}