package ar.com.ospim.compras.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraFiltro;
import ar.com.ospim.compras.service.BusquedaRequerimientoCompraServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarRequerimientosComprasAction extends PortletAction {

    private static Log _log = LogFactoryUtil.getLog(BuscarRequerimientosComprasAction.class);

    public void processAction(ActionMapping mapping, ActionForm form,
                              PortletConfig portletConfig, ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        setForward(actionRequest, "portlet.compras.result.search");
    }

    public ActionForward render(ActionMapping mapping, ActionForm form,
                                PortletConfig portletConfig, RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        try {
            RequerimientoCompraFiltro filtro = getFiltroFromRequest(renderRequest);
            List<RequerimientoCompra> requerimientos = BusquedaRequerimientoCompraServiceUtil.buscarRequerimientos(filtro);

            renderRequest.setAttribute(WebKeysCompras.FILTRO_COMPRAS, filtro);
            renderRequest.setAttribute(WebKeysCompras.BUSQUEDA_COMPRAS, requerimientos);

            PortletSession portletSession = renderRequest.getPortletSession();
            portletSession.setAttribute(WebKeysCompras.FILTRO_COMPRAS, filtro, PortletSession.PORTLET_SCOPE);
            portletSession.setAttribute(WebKeysCompras.BUSQUEDA_COMPRAS, requerimientos, PortletSession.PORTLET_SCOPE);
        } catch (Exception e) {
            _log.error(e);
        }

        return mapping.findForward("portlet.compras.result.search");
    }

    private RequerimientoCompraFiltro getFiltroFromRequest(RenderRequest request) {
        RequerimientoCompraFiltro filtro = new RequerimientoCompraFiltro();

        int numero = ParamUtil.getInteger(request, "numero", 0);
        if (numero > 0) {
            filtro.setNumero(Integer.valueOf(numero));
        }

        filtro.setFechaDesde(parseInputDate(request, "fechaDesde"));
        filtro.setFechaHasta(parseInputDate(request, "fechaHasta"));

        int sectorId = ParamUtil.getInteger(request, "sector_id", 0);
        if (sectorId > 0) {
            filtro.setSectorId(Integer.valueOf(sectorId));
        }

        filtro.setSolicitanteUsr(ParamUtil.getString(request, "solicitante_usr", null));
        filtro.setEntidad(ParamUtil.getString(request, "entidad", null));

        int prioridad = ParamUtil.getInteger(request, "prioridad", 0);
        if (prioridad > 0) {
            filtro.setPrioridad(Integer.valueOf(prioridad));
        }

        int estado = ParamUtil.getInteger(request, "estado", 0);
        if (estado > 0) {
            filtro.setEstado(Integer.valueOf(estado));
        }

        filtro.setTexto(ParamUtil.getString(request, "texto", null));

        return filtro;
    }

    private Date parseInputDate(RenderRequest request, String prefix) {
        String dia = ParamUtil.getString(request, prefix + "Dia");
        String mes = ParamUtil.getString(request, prefix + "Mes");
        String anio = ParamUtil.getString(request, prefix + "Anio");

        try {
            if (dia == null || dia.length() == 0 || mes == null || mes.length() == 0 || anio == null || anio.length() == 0) {
                return null;
            }

            int mesLiferay = Integer.parseInt(mes) + 1;
            return new SimpleDateFormat("dd/MM/yyyy").parse(dia + "/" + mesLiferay + "/" + anio);
        } catch (Exception e) {
            return null;
        }
    }
}
