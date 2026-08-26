package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Adaptador de la búsqueda de medicamentos usada por Compras.
 *
 * Sustituye el forward directo histórico para evitar que
 * requerimiento_compra_medicamento_búsqueda_resultado.jsp consulte Services.
 */
public class BuscarMedicamentosComprasAction extends PortletAction {

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        String error = "";

        List<Medicamento> medicamentos =
                new ArrayList<Medicamento>();

        try {
            String troquelRaw =
                    ParamUtil.getString(
                            renderRequest,
                            "troquel",
                            "0"
                    );

            String nombreMedicamento =
                    ParamUtil.getString(
                            renderRequest,
                            "nombre_medicamento",
                            ""
                    );

            troquelRaw = troquelRaw.trim();

            if (troquelRaw.length() == 0) {
                troquelRaw = "0";
            }

            if (!troquelRaw.matches("^[0-9]+$")) {
                throw new Exception(
                        "El troquel informado no es válido."
                );
            }

            int troquel;

            try {
                troquel =
                        Integer.parseInt(
                                troquelRaw
                        );
            } catch (NumberFormatException e) {
                throw new Exception(
                        "El troquel informado está fuera del rango permitido."
                );
            }

            nombreMedicamento = nombreMedicamento.trim();

            if (troquel <= 0
                    && nombreMedicamento.length() == 0) {

                throw new Exception(
                        "Debe informar troquel o nombre del medicamento."
                );
            }

            List<Medicamento> recuperados =
                    BusquedaMedicamentoServiceUtil
                            .getBusquedaMedicamentos(
                                    troquel,
                                    nombreMedicamento
                            );

            if (recuperados != null) {
                medicamentos.addAll(
                        recuperados
                );
            }

        } catch (Exception e) {
            error =
                    e.getMessage() != null
                            ? e.getMessage()
                            : "No se pudieron buscar medicamentos.";
        }

        renderRequest.setAttribute(
                "COMPRAS_RESULTADOS_MEDICAMENTOS",
                medicamentos
        );

        renderRequest.setAttribute(
                "COMPRAS_ERROR_BUSQUEDA_MEDICAMENTOS",
                error
        );

        return mapping.findForward(
                "portlet.compras.buscar_medicamentos"
        );
    }
}
