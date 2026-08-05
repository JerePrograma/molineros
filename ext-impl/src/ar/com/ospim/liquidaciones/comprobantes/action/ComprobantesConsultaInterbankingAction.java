package ar.com.ospim.liquidaciones.comprobantes.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteFiltro;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.util.StringUtils;

public class ComprobantesConsultaInterbankingAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(ComprobantesConsultaInterbankingAction.class);
	
	static SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		

		int entidad = WebKeysGlobal.OSPIM;
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		
		
		if (!StringUtils.checkEmpty(cmd)) {
			if(cmd.equals("filter") ){
				ComprobanteFiltro comp = getComprobanteFromRequest(renderRequest);
				session
				.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_COMPROBANTES_INTERBANKING);
				List<Object>busqueda =buscarDocumentosPorLike(comp.getCuit(),comp.getFechaEmisionDesde(),comp.getFechaEmisionHasta());
				renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_COMPROBANTES_INTERBANKING,busqueda);
				return mapping
						.findForward("portlet.liquidaciones.comprobantes.interbanking.search.result");
			}
		}
		
		

		return mapping
					.findForward("portlet.liquidaciones.comprobantes.interbanking.search.result");
	}
	
	
	public static ComprobanteFiltro getComprobanteFromRequest(
			PortletRequest renderRequest) {
		
		String fechaDia = ParamUtil.getString(renderRequest,"fechadesdedia");
		String fechaMes = ParamUtil.getString(renderRequest,"fechadesdemes");
		String fechaAnio = ParamUtil.getString(renderRequest,"fechadesdeanio");
		
		String fechaDiaH = ParamUtil.getString(renderRequest,"fechahastadia");
		String fechaMesH = ParamUtil.getString(renderRequest,"fechahastames");
		String fechaAnioH = ParamUtil.getString(renderRequest,"fechahastaanio");
		
		Date fechaD = null;
		try {
			fechaD = formatoDeFechas.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/"
					+ fechaAnio);
		} catch (Exception e) {
			fechaD = null;
		}
		
		Date fechaH = null;
		try {
			fechaH = formatoDeFechas.parse(fechaDiaH + "/"
					+ (Integer.parseInt(fechaMesH) + 1) + "/"
					+ fechaAnioH);
		} catch (Exception e) {
			fechaH = null;
		}
		ComprobanteFiltro comprobante = new ComprobanteFiltro();
		String cuitAcreedor = renderRequest.getParameter("cuit_entidad");
		comprobante.setFechaEmisionDesde(fechaD);
		comprobante.setFechaEmisionHasta(fechaH);
		comprobante.setCuit(cuitAcreedor);
		
		return comprobante;
	}
	
	public List<Object> buscarDocumentosPorLike(String tituloParcial, Date fechaInicio, Date fechaFin) {
	    try {
	        DynamicQuery query = DynamicQueryFactoryUtil.forClass(DLFileEntry.class);
	        DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "Interbanking");
	        query.add(RestrictionsFactoryUtil.eq("folderId", f.getFolderId()));
	        String patronTitulo = "%" + tituloParcial + "%";
	        query.add(RestrictionsFactoryUtil.ilike("title", patronTitulo));
	        Criterion entreFechas = RestrictionsFactoryUtil.between("createDate", fechaInicio, fechaFin);
	        query.add(entreFechas);
            List<Object> resultados = DLFileEntryLocalServiceUtil.dynamicQuery(query);
	        return resultados;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	

}
