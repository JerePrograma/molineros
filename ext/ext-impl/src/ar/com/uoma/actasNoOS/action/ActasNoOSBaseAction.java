/**
 */

package ar.com.uoma.actasNoOS.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaEstadoSeguimiento;
import ar.com.ospim.tesoreria.beans.ActaPago;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;


public class ActasNoOSBaseAction extends PortletAction {

	protected Acta getActaEntry(HttpServletRequest request) throws Exception {

		Acta acta = null;
		String idString = request.getParameter("acta_id");
		if (idString == null || idString.trim().equals("")
				|| idString.trim().equals("0")) {
			idString = (String) request.getAttribute("acta_id");
		}
		if (idString != null && !idString.trim().equals("")) {
			int id = Integer.parseInt(idString);
			if (id > 0) {
				acta = ActaNoOSServiceUtil.getActa(id,0);
			}
		}
		return acta;
	}

	public Acta getOtrosDatosFromRequest(HttpServletRequest req, Acta acta) {

		return acta;
	}

	public Acta getActaFromRequest(HttpServletRequest req, Acta acta)
			throws ParseException {
		
		String cuit = ParamUtil.getString(req, "cuit_entidadacta_");
		String sucu = ParamUtil.getString(req, "sucursal_entidadacta_");
		// String empleador = ParamUtil.getString(req, "empleador", "");
		
		int idActa = ParamUtil.getInteger(req, "acta_id");
		if (ParamUtil.getString(req, "fechaInicioDia") != null
				&& !ParamUtil.getString(req, "fechaInicioDia").equals("")) {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String fechaInicioDia = ParamUtil.getString(req, "fechaInicioDia");
			String fechaInicioMes = ParamUtil.getString(req, "fechaInicioMes");
			fechaInicioMes = String
					.valueOf(Integer.valueOf(fechaInicioMes) + 1);
			String fechaInicioAnio = ParamUtil
					.getString(req, "fechaInicioAnio");
			
			acta.setFechaInicio(format.parse(fechaInicioDia + "-"
					+ fechaInicioMes + "-" + fechaInicioAnio));
			acta.setCierre_fecha(format.parse(fechaInicioDia + "-"
					+ fechaInicioMes + "-" + fechaInicioAnio));
			
			String fechaPagoDia = ParamUtil.getString(req, "fechaPagoDia");
			String fechaPagoMes = ParamUtil.getString(req, "fechaPagoMes");
			String fechaPagoAnio = ParamUtil.getString(req, "fechaPagoAnio");

			if (StringUtils.checkNotEmpty(fechaPagoDia)
					&& StringUtils.checkNotEmpty(fechaPagoMes)
					&& StringUtils.checkNotEmpty(fechaPagoAnio)) {
				fechaPagoMes = String.valueOf(Integer.valueOf(fechaPagoMes) + 1);
				acta.setFechaPago(format.parse(fechaPagoDia + "-"
						+ fechaPagoMes + "-" + fechaPagoAnio));
			}
			
			
			String fechaPeriodoDesdeDia = ParamUtil.getString(req, "fechaPeriodoDesdeDia");
			String fechaPeriodoDesdeMes = ParamUtil.getString(req, "fechaPeriodoDesdeMes");
			String fechaPeriodoDesdeAnio= ParamUtil.getString(req, "fechaPeriodoDesdeAnio");

			if (StringUtils.checkNotEmpty(fechaPeriodoDesdeDia)
					&& StringUtils.checkNotEmpty(fechaPeriodoDesdeMes)
					&& StringUtils.checkNotEmpty(fechaPeriodoDesdeAnio)) {
				fechaPeriodoDesdeMes = String
						.valueOf(Integer.valueOf(fechaPeriodoDesdeMes) + 1);
				acta.setPeriodoInicial(format.parse(fechaPeriodoDesdeDia + "-"
						+ fechaPeriodoDesdeMes + "-" + fechaPeriodoDesdeAnio));
			}
			
			String fechaPeriodoHastaDia = ParamUtil.getString(req, "fechaPeriodoHastaDia");
			String fechaPeriodoHastaMes = ParamUtil.getString(req, "fechaPeriodoHastaMes");
			String fechaPeriodoHastaAnio= ParamUtil.getString(req, "fechaPeriodoHastaAnio");

			if (StringUtils.checkNotEmpty(fechaPeriodoHastaDia)
					&& StringUtils.checkNotEmpty(fechaPeriodoHastaMes)
					&& StringUtils.checkNotEmpty(fechaPeriodoHastaAnio)) {
				fechaPeriodoHastaMes = String
						.valueOf(Integer.valueOf(fechaPeriodoHastaMes) + 1);
				acta.setPeriodoFinal(format.parse(fechaPeriodoHastaDia + "-"
						+ fechaPeriodoHastaMes + "-" + fechaPeriodoHastaAnio));
			}
		}
		String actaNro = ParamUtil.getString(req, "acta_numero");
		
		String entidad = ParamUtil.getString(req, "entidad");
		String estado  = ParamUtil.getString(req, "estado");
		
		acta.setEntidad(entidad);
		acta.setEstado(estado);

		String estadoSeguim = ParamUtil.getString(req, "estado_seguim", null);
		ActaEstadoSeguimiento aes = null;
		try{
			int id = Integer.parseInt(estadoSeguim);
			aes = new ActaEstadoSeguimiento(id, "");
		}catch (Exception e) {
//			nada, dejamos el null
		}
		acta.setEstadoSeguimiento(aes);
		
		if (acta != null && acta.getId() != 0
				&& acta.getDetallesActas() != null) {
			acta.setInspector(!acta.getDetallesActas().isEmpty());
		} else {
			boolean inspector = ParamUtil.getBoolean(req, "inspectorActa");
			acta.setInspector(inspector);
			if (!inspector && acta.getDetallesActas() != null) {
				acta.getDetallesActas().clear();
			}
		}
		acta.setNumero(actaNro);
		String otrosStr=null;
		String subtotalStr=null;
		String inteStr=null;
		BigDecimal otros=null;
		BigDecimal subtotal=null;
		BigDecimal inte=null;
				
		String subtotal_sindicato_str=null;
		String inte_sindicato_str=null;		
		BigDecimal subtotal_sindicato=null;
		BigDecimal inte_sindicato=null;
		
		String subtotal_solidario_str=null;
		String inte_solidario_str=null;		
		BigDecimal subtotal_solidario=null;
		BigDecimal inte_solidario=null;
		
		String subtotal_usufructo_str=null;
		String inte_usufructo_str=null;		
		BigDecimal subtotal_usufructo=null;
		BigDecimal inte_usufructo=null;
		
		
		String subtotal_art46_str=null;
		String inte_art46_str=null;		
		BigDecimal subtotal_art46=null;
		BigDecimal inte_art46=null;
		
		
		if(entidad.trim().equals("U.O.M.A.")){
			subtotal_sindicato_str = ParamUtil.getString(req, "subtotal_sindicato", "0");
			inte_sindicato_str = ParamUtil.getString(req, "inte_sindicato", "0");			
			subtotal_sindicato = new BigDecimal(subtotal_sindicato_str.equals("") ? "0"
					: subtotal_sindicato_str);
			inte_sindicato = new BigDecimal(inte_sindicato_str.equals("") ? "0" : inte_sindicato_str);
			
			subtotal_solidario_str = ParamUtil.getString(req, "subtotal_solidario", "0");
			inte_solidario_str = ParamUtil.getString(req, "inte_solidario", "0");			
			subtotal_solidario = new BigDecimal(subtotal_solidario_str.equals("") ? "0"
					: subtotal_solidario_str);
			inte_solidario = new BigDecimal(inte_solidario_str.equals("") ? "0" : inte_solidario_str);
			
			subtotal_usufructo_str = ParamUtil.getString(req, "subtotal_usufructo", "0");
			inte_usufructo_str = ParamUtil.getString(req, "inte_usufructo", "0");			
			subtotal_usufructo = new BigDecimal(subtotal_usufructo_str.equals("") ? "0"
					: subtotal_usufructo_str);
			inte_usufructo = new BigDecimal(inte_usufructo_str.equals("") ? "0" : inte_usufructo_str);
			
			subtotal_art46_str = ParamUtil.getString(req, "subtotal_art46", "0");
			inte_art46_str = ParamUtil.getString(req, "inte_art46", "0");			
			subtotal_art46 = new BigDecimal(subtotal_art46_str.equals("") ? "0"
					: subtotal_art46_str);
			inte_art46 = new BigDecimal(inte_art46_str.equals("") ? "0" : inte_art46_str);
			
		}else{
			otrosStr = ParamUtil.getString(req, "otros", "0");
			subtotalStr = ParamUtil.getString(req, "subtotal", "0");
			inteStr = ParamUtil.getString(req, "inte", "0");
			otros = new BigDecimal(otrosStr.equals("") ? "0" : otrosStr);
			subtotal = new BigDecimal(subtotalStr.equals("") ? "0"
					: subtotalStr);
			inte = new BigDecimal(inteStr.equals("") ? "0" : inteStr);
		}
		
		acta.setCapitalSindicato(subtotal_sindicato);
		acta.setInteresSindicato(inte_sindicato);
		acta.setCapitalSolidario(subtotal_solidario);
		acta.setInteresSolidario(inte_solidario);
		acta.setCapitalUsufructo(subtotal_usufructo);
		acta.setInteresUsufructo(inte_usufructo);
		acta.setCapitalArt46(subtotal_art46);
		acta.setInteresArt46(inte_art46);
		
		

		acta.setEmpresa(new Empresa(cuit, sucu, null));
		acta.setId(idActa);

		acta.setOtros(otros);
		acta.setCapital(subtotal);
		acta.setInteres(inte);

		BigDecimal deudaActas = acta.getDeudaFromActasRelacionadas();
		acta.setDeudaActasRelacionadas(deudaActas);
		if (acta.isInspector()) {
			BigDecimal interes = acta.getInteresFromDetalle();
			BigDecimal capital = acta.getCapitalFromDetalle();
			acta.setCapital(capital);
			acta.setInteres(interes);
		}

		List<ActaPago> pagos = acta.getPagos();
		if (pagos == null) {
			pagos = new ArrayList<ActaPago>();
			acta.setPagos(pagos);
		}

		ActaPago aPagoCuota = null;
		for (ActaPago a : pagos) {
			if (a.getTipo().equals(ActaPago.Tipo.CUOTA)) {
				aPagoCuota = a;
			}
		}

		int idCuota = ParamUtil.getInteger(req, "idCuota", 0);
		if (aPagoCuota == null) {
			aPagoCuota = new ActaPago();
			pagos.add(aPagoCuota);
		}
		aPagoCuota.setTipo(ActaPago.Tipo.CUOTA);
		aPagoCuota.setImporte(acta.getTotal());
		aPagoCuota.setFechaPago(acta.getAlta_fecha());
		aPagoCuota.setId(idCuota);
		acta.setMolinera(ParamUtil.getBoolean(req, "molinera", false));
		return acta;
	}

}