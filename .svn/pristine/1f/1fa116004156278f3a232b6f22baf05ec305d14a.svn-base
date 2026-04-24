package ar.com.empresas.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.empresas.WebKeysEmpresas;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarCuentaBcriaAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarCuentaBcriaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Agregando ingreso a acta");

		int entidad = WebKeysGlobal.OSPIM;
		HttpSession session = null;
		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		} else if (renderResponse.getNamespace().equals("_EMP_1_")) {
			entidad = WebKeysGlobal.EMPRESA;
		} else if (renderResponse.getNamespace().equals("_EST_1_")) {
			entidad = WebKeysGlobal.ESTUDIO;
		}

		String cuit = ParamUtil.getString(renderRequest, "cuit");
		String sucursal = ParamUtil.getString(renderRequest, "sucursal");
		String accion = ParamUtil.getString(renderRequest, "accion");
		List<CuentaBancaria> list = null;
		Empresa empresa = null;		
		
		if (entidad == WebKeysGlobal.EMPRESA) {
			empresa = (Empresa) renderRequest.getPortletSession()
					.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,
							PortletSession.APPLICATION_SCOPE);
		} else {
			session = PortalUtil.getHttpServletRequest(renderRequest)
					.getSession();
			empresa = (Empresa) session
					.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION);
		}
		
		

		if (null == empresa || (null!=empresa && null!=empresa.getCuit() && !empresa.getCuit().equals(cuit))) {
			empresa = new Empresa();
		}
		
		empresa.setCuit(cuit);
		empresa.setSucursal(sucursal);
		
		if (accion.equals("BUSCAR")) {
			list = EmpresaServiceUtil.getCuentasBancarias(cuit, sucursal);
		} else {

			
			
			if (null != empresa.getCuentasBcrias()
					&& empresa.getCuentasBcrias().size() > 0) {
				list = empresa.getCuentasBcrias();
			} else {
				list = new ArrayList<CuentaBancaria>();
			}
			
			empresa.setCuit(cuit);
			
			if (accion.equals("ADD")) {
				list.add(getCuentaBancaria(renderRequest));
			} else if (accion.equals("DELETE")) {
				removeCtaBcriaFromList(list, getCuentaBancaria(renderRequest));

			}
		}
		empresa.setCuentasBcrias(list);

		
		
		renderRequest.getPortletSession().setAttribute(
					WebKeysEmpresas.EMPRESA_EN_EDICION, empresa,
					PortletSession.APPLICATION_SCOPE);
		
		
		
		renderRequest.setAttribute("esEdicion", "true");
		return mapping
				.findForward("portlet.empresas.cuentas_bancarias_search_result");

	}

	private CuentaBancaria getCuentaBancaria(RenderRequest renderRequest)
			throws ParseException, SystemException {
		CuentaBancaria cuenta = new CuentaBancaria();

		int idBanco = ParamUtil.getInteger(renderRequest, "id_banco_cta_bcria");
		int idCtaBcria = ParamUtil.getInteger(renderRequest, "id_cta_bcria");

		String sucuCtaBcria = ParamUtil.getString(renderRequest,
				"id_sucursal_cta_bcria");
		String descripcion = ParamUtil.getString(renderRequest,
				"descripcion_cta_bcria");
		String cbu = ParamUtil.getString(renderRequest, "cbu_cta_bcria");

		cuenta.setId_cuenta_bcria(idCtaBcria);
		cuenta.setBanco(new Banco(idBanco));
		cuenta.setSucursalString(sucuCtaBcria);
		cuenta.setDescripcion(descripcion);
		cuenta.setCBU(cbu);

		return cuenta;
	}

	private void removeCtaBcriaFromList(List<CuentaBancaria> list,CuentaBancaria ap) {
		
		Iterator<CuentaBancaria> it = list.iterator();
		
		while (it.hasNext()) {
			CuentaBancaria aCuentaEnLista = it.next();
			if (aCuentaEnLista.getBanco().getId_banco() == ap.getBanco().getId_banco()) {
				if (aCuentaEnLista.getId_cuenta_bcria() == ap.getId_cuenta_bcria()) {
					aCuentaEnLista.setBajaFecha(new Date());
//				} else {
//					it.remove();
				}
				
			}
		}
	}

}
