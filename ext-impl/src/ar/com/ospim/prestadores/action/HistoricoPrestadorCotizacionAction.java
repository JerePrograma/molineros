package ar.com.ospim.prestadores.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.prestadores.beans.HistoricoPrestadorCotizacion;

import com.liferay.portal.kernel.util.ParamUtil;

public class HistoricoPrestadorCotizacionAction
        extends PrestadoresBaseAction {

    @Override
    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse)
            throws Exception {

        int idPrestador = ParamUtil.getInteger(
                renderRequest,
                "idPrestador"
        );

        List<HistoricoPrestadorCotizacion> historico =
                PrestadorServiceUtil
                        .listarHistoricoCotizacionPrestador(
                                idPrestador
                        );

        renderRequest.setAttribute(
                "historicoPrestadorCotizacion",
                historico
        );

        return mapping.findForward(
                "portlet.prestadores.historico_cotizacion_result"
        );
    }
}