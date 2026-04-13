/**
 */

package ar.com.ospim.tesoreria.actas.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ar.com.empresas.WebKeysEmpresas;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaEstadoSeguimiento;
import ar.com.ospim.tesoreria.beans.ActaPago;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="ActasBaseAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class ActasBaseAction extends PortletAction {

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
				acta = ActaServiceUtil.getActa(id,0);
			}
		}
		return acta;
	}

	public Acta getOtrosDatosFromRequest(HttpServletRequest req, Acta acta) {

		return acta;
	}

	public Acta getActaFromRequest(PortletRequest renderRequest, Acta acta)
			throws ParseException {
		HttpServletRequest req=PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session= PortalUtil.getHttpServletRequest(renderRequest).getSession();
		Empresa empresa= (Empresa) session.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION);
		
		String cuit = ParamUtil.getString(req, "cuit_entidadacta_");
		String sucu = ParamUtil.getString(req, "sucursal_entidadacta_");
		
		if(null!=empresa && null!=empresa.getCuit() && !empresa.getCuit().equals(cuit)){
			empresa=new Empresa(cuit, sucu);
			session.setAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION, empresa);
		}else if(empresa==null){
			empresa=new Empresa(cuit, sucu);
		}
		
		acta.setEmpresa(empresa);
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
			String fechaPagoDia = ParamUtil.getString(req, "fechaPagoDia");
			String fechaPagoMes = ParamUtil.getString(req, "fechaPagoMes");
			fechaPagoMes = String.valueOf(Integer.valueOf(fechaPagoMes) + 1);
			String fechaPagoAnio = ParamUtil.getString(req, "fechaPagoAnio");
			acta.setFechaInicio(format.parse(fechaInicioDia + "-"
					+ fechaInicioMes + "-" + fechaInicioAnio));
			acta.setFechaPago(format.parse(fechaPagoDia + "-" + fechaPagoMes
					+ "-" + fechaPagoAnio));

			String fechaActaDia = ParamUtil.getString(req, "fechaActaDia");
			String fechaActaMes = ParamUtil.getString(req, "fechaActaMes");
			String fechaActaAnio = ParamUtil.getString(req, "fechaActaAnio");

			if (StringUtils.checkNotEmpty(fechaActaDia)
					&& StringUtils.checkNotEmpty(fechaActaMes)
					&& StringUtils.checkNotEmpty(fechaActaAnio)) {
				fechaActaMes = String
						.valueOf(Integer.valueOf(fechaActaMes) + 1);
				acta.setCierre_fecha(format.parse(fechaActaDia + "-"
						+ fechaActaMes + "-" + fechaActaAnio));
			}
		}
		String actaNro = ParamUtil.getString(req, "acta_numero");

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
		String otrosStr = ParamUtil.getString(req, "otros", "0");
		String subtotalStr = ParamUtil.getString(req, "subtotal", "0");
		String inteStr = ParamUtil.getString(req, "inte", "0");
		BigDecimal otros = new BigDecimal(otrosStr.equals("") ? "0" : otrosStr);
		BigDecimal subtotal = new BigDecimal(subtotalStr.equals("") ? "0"
				: subtotalStr);
		BigDecimal inte = new BigDecimal(inteStr.equals("") ? "0" : inteStr);
	
		String estadoSeguim = ParamUtil.getString(req, "estado_seguim", null);
		ActaEstadoSeguimiento aes = null;
		try{
			int id = Integer.parseInt(estadoSeguim);
			aes = new ActaEstadoSeguimiento(id, "");
		}catch (Exception e) {
//			nada, dejamos el null
		}
		acta.setEstadoSeguimiento(aes);
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
		aPagoCuota.setFechaPago(acta.getFechaPago());
		aPagoCuota.setId(idCuota);		
		
		
		return acta;
	}

}