package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class BuscarItemTecnicoComprasAction extends PortletAction {

    private static final int MAX_RESULTADOS = 100;
    private static final Pattern DIACRITICOS =
            Pattern.compile("[\\p{InCombiningDiacriticalMarks}]");

    private final RequerimientoCompraDetalleHelper detalleHelper =
            new RequerimientoCompraDetalleHelper();

    public ActionForward render(ActionMapping mapping,
                                ActionForm form,
                                PortletConfig portletConfig,
                                RenderRequest request,
                                RenderResponse response) throws Exception {

        User user = PortalUtil.getUser(request);
        detalleHelper.validarPermisoABM(user);

        String tipoItem = ParamUtil.getString(request, "tipo_item", "").trim();
        String callback = sanitizarCallback(
                ParamUtil.getString(request, "callback", "")
        );

        request.setAttribute("COMPRAS_TIPO_ITEM_BUSQUEDA", tipoItem);
        request.setAttribute("COMPRAS_CALLBACK_BUSQUEDA", callback);

        if (callback.length() == 0) {
            request.setAttribute(
                    "COMPRAS_ERROR_BUSQUEDA",
                    "No se pudo identificar el formulario que recibira la seleccion."
            );
            return mapping.findForward("portlet.compras.buscar_item_tecnico");
        }

        if ("MEDICAMENTO".equals(tipoItem)) {
            buscarMedicamentos(request);
        } else if ("NOMENCLADOR".equals(tipoItem)) {
            buscarNomenclador(request);
        } else {
            request.setAttribute(
                    "COMPRAS_ERROR_BUSQUEDA",
                    "El tipo de item tecnico informado no es valido."
            );
        }

        return mapping.findForward("portlet.compras.buscar_item_tecnico");
    }

    private void buscarMedicamentos(RenderRequest request) {
        String troquelTexto = ParamUtil.getString(request, "troquel", "").trim();
        String nombre = ParamUtil.getString(request, "nombre", "").trim();
        String presentacion = ParamUtil.getString(request, "presentacion", "").trim();

        if (troquelTexto.length() == 0
                && nombre.length() == 0
                && presentacion.length() == 0) {
            request.setAttribute(
                    "COMPRAS_ERROR_BUSQUEDA",
                    "Ingrese nombre, presentacion o troquel."
            );
            return;
        }

        if (troquelTexto.length() > 0 && !troquelTexto.matches("^[0-9]+$")) {
            request.setAttribute(
                    "COMPRAS_ERROR_BUSQUEDA",
                    "El troquel debe ser numerico."
            );
            return;
        }

        int troquel = troquelTexto.length() > 0
                ? Integer.parseInt(troquelTexto)
                : 0;

        List<Medicamento> encontrados =
                BusquedaMedicamentoServiceUtil.getBusquedaMedicamentos(
                        troquel,
                        0,
                        nombre,
                        presentacion,
                        null,
                        null
                );

        List<Medicamento> activos = new ArrayList<Medicamento>();

        if (encontrados != null) {
            for (int i = 0;
                    i < encontrados.size() && activos.size() < MAX_RESULTADOS;
                    i++) {

                Medicamento medicamento = encontrados.get(i);

                if (medicamento != null
                        && medicamento.getId_medicamento() > 0
                        && medicamento.getFecha_baja() == null) {

                    activos.add(medicamento);
                }
            }
        }

        request.setAttribute("COMPRAS_MEDICAMENTOS_BUSQUEDA", activos);
        request.setAttribute(
                "COMPRAS_RESULTADOS_LIMITADOS",
                Boolean.valueOf(encontrados != null && encontrados.size() > MAX_RESULTADOS)
        );
    }

    private void buscarNomenclador(RenderRequest request) throws Exception {
        String codigo = ParamUtil.getString(request, "codigo", "").trim();
        String descripcion = ParamUtil.getString(request, "descripcion", "").trim();
        int idTipo = ParamUtil.getInteger(request, "id_tipo_nomenclador", 0);
        String sector = normalizar(ParamUtil.getString(request, "sector", ""));

        if (codigo.length() == 0 && descripcion.length() == 0 && idTipo <= 0) {
            request.setAttribute(
                    "COMPRAS_ERROR_BUSQUEDA",
                    "Ingrese codigo, descripcion o tipo de nomenclador."
            );
            return;
        }

        List<Nomenclador> encontrados;

        if ("PRESTACIONES MEDICAS".equals(sector)) {
            encontrados = NomencladorServiceUtil
                    .getListaNomencladorPrestacionesMedicas(
                            idTipo,
                            descripcion,
                            0,
                            codigo,
                            Boolean.FALSE,
                            ""
                    );
        } else if ("LEGALES".equals(sector)) {
            encontrados = NomencladorServiceUtil.getListaNomenclador(
                    idTipo,
                    descripcion,
                    0,
                    codigo,
                    Boolean.FALSE,
                    ""
            );
        } else {
            request.setAttribute(
                    "COMPRAS_ERROR_BUSQUEDA",
                    "El sector no admite prestaciones de nomenclador."
            );
            return;
        }

        List<Nomenclador> activos = new ArrayList<Nomenclador>();

        if (encontrados != null) {
            for (int i = 0;
                    i < encontrados.size() && activos.size() < MAX_RESULTADOS;
                    i++) {

                Nomenclador nomenclador = encontrados.get(i);

                if (nomenclador != null
                        && nomenclador.getId_prestacion() > 0
                        && nomenclador.getId_tipo_nomenclador() > 0
                        && nomenclador.getBaja_fecha() == null) {

                    activos.add(nomenclador);
                }
            }
        }

        request.setAttribute("COMPRAS_NOMENCLADORES_BUSQUEDA", activos);
        request.setAttribute(
                "COMPRAS_RESULTADOS_LIMITADOS",
                Boolean.valueOf(encontrados != null && encontrados.size() > MAX_RESULTADOS)
        );
    }

    private String sanitizarCallback(String callback) {
        callback = callback == null ? "" : callback.trim();
        return callback.matches("^[A-Za-z_$][A-Za-z0-9_$]*$") ? callback : "";
    }

    private String normalizar(String value) {
        value = value == null ? "" : value.trim();
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalized = DIACRITICOS.matcher(normalized).replaceAll("");
        return normalized.toUpperCase(Locale.ROOT);
    }
}
