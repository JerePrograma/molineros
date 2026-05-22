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

            List<RequerimientoCompra> requerimientos =
                    BusquedaRequerimientoCompraServiceUtil.buscarRequerimientos(filtro);

            renderRequest.setAttribute(WebKeysCompras.FILTRO_REQUERIMIENTOS_COMPRA, filtro);
            renderRequest.setAttribute(WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA, requerimientos);

            PortletSession portletSession = renderRequest.getPortletSession();

            portletSession.setAttribute(
                    WebKeysCompras.FILTRO_REQUERIMIENTOS_COMPRA,
                    filtro,
                    PortletSession.PORTLET_SCOPE
            );

            portletSession.setAttribute(
                    WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA,
                    requerimientos,
                    PortletSession.PORTLET_SCOPE
            );
        } catch (Exception e) {
            _log.error(e);
            renderRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());
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

        int idSector = ParamUtil.getInteger(request, "id_sector", 0);
        if (idSector <= 0) {
            idSector = ParamUtil.getInteger(request, "sector_id", 0);
        }
        if (idSector > 0) {
            filtro.setIdSector(Integer.valueOf(idSector));
        }

        int idEstado = ParamUtil.getInteger(request, "id_estado", 0);
        if (idEstado <= 0) {
            idEstado = ParamUtil.getInteger(request, "estado", 0);
        }
        if (idEstado > 0) {
            filtro.setIdEstado(Integer.valueOf(idEstado));
        }

        filtro.setSolicitanteUsr(ParamUtil.getString(request, "solicitante_usr", null));
        filtro.setTexto(ParamUtil.getString(request, "texto", null));

        filtro.setAfiliadoCuilTitular(ParamUtil.getString(request, "afiliado_cuil_titular", null));

        int afiliadoInte = ParamUtil.getInteger(request, "afiliado_inte", -1);
        if (afiliadoInte >= 0) {
            filtro.setAfiliadoInte(Integer.valueOf(afiliadoInte));
        }

        filtro.setTipoArticulo(ParamUtil.getString(request, "tipo_articulo", null));

        return filtro;
    }

    private Date parseInputDate(RenderRequest request, String prefix) {
        String dia = ParamUtil.getString(request, prefix + "Dia");
        String mes = ParamUtil.getString(request, prefix + "Mes");
        String anio = ParamUtil.getString(request, prefix + "Anio");

        try {
            if (dia == null || dia.length() == 0
                    || mes == null || mes.length() == 0
                    || anio == null || anio.length() == 0) {
                return null;
            }

            int mesLiferay = Integer.parseInt(mes) + 1;
            return new SimpleDateFormat("dd/MM/yyyy").parse(dia + "/" + mesLiferay + "/" + anio);
        } catch (Exception e) {
            return null;
        }
    }
}
