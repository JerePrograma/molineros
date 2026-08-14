package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.autorizaciones.action
        .BuscarAfiliadoFechaVtoDiscapacidad;

import com.liferay.portal.struts.ActionConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class BuscarAfiliadoFechaVtoDocumentacionCompraAction
        extends BuscarAfiliadoFechaVtoDiscapacidad {

    @Override
    public ActionForward execute(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        /*
         * Conserva íntegramente el JSON producido por la action
         * legacy de Autorizaciones.
         */
        super.execute(
                mapping,
                form,
                request,
                response
        );

        /*
         * Evita que PortletRequestProcessor interprete el null
         * contractual de JSONAction como un forward inexistente.
         */
        return new ActionForward(
                ActionConstants.COMMON_NULL
        );
    }
}