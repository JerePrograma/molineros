package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.autorizaciones.action.AfiliadoDatosJSONAction;

import com.liferay.portal.struts.ActionConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class BuscarAfiliadoDatosCompraAction
        extends AfiliadoDatosJSONAction {

    @Override
    public ActionForward execute(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        super.execute(
                mapping,
                form,
                request,
                response
        );

        return new ActionForward(
                ActionConstants.COMMON_NULL
        );
    }
}
