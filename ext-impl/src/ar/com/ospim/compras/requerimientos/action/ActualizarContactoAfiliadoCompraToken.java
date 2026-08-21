package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;

import com.liferay.portal.kernel.util.ParamUtil;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.UUID;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

public final class ActualizarContactoAfiliadoCompraToken {

    public static final String PARAM_TOKEN =
            "contacto_afiliado_token";

    public static final String ATTR_TOKEN =
            "COMPRAS_CONTACTO_AFILIADO_TOKEN";

    private static final String SESSION_CONTEXTOS =
            "COMPRAS_CONTACTO_AFILIADO_CONTEXTOS";

    private static final int MAX_CONTEXTOS = 20;

    public static void publicar(
            RenderRequest request,
            RequerimientoCompra requerimiento) {

        if (request == null || requerimiento == null) {
            return;
        }

        String token = UUID.randomUUID().toString();
        Contexto contexto = new Contexto(
                requerimiento.getIdRequerimientoCompra(),
                requerimiento.getAfiliadoCuilTitular(),
                requerimiento.getAfiliadoInt()
        );
        PortletSession session = request.getPortletSession();

        synchronized (session) {
            Contextos contextos = obtenerContextos(session);

            if (contextos.valores.size() >= MAX_CONTEXTOS) {
                Iterator<String> tokens =
                        contextos.valores.keySet().iterator();

                if (tokens.hasNext()) {
                    tokens.next();
                    tokens.remove();
                }
            }

            contextos.valores.put(token, contexto);
            session.setAttribute(SESSION_CONTEXTOS, contextos);
        }

        request.setAttribute(ATTR_TOKEN, token);
    }

    public static void validar(
            RenderRequest request,
            String cuilTitular,
            int integrante) throws Exception {

        if (request == null) {
            throw new Exception(
                    "No se pudo validar la pantalla de Compras."
            );
        }

        String token = ParamUtil.getString(request, PARAM_TOKEN);
        PortletSession session = request.getPortletSession();
        Contexto contexto;

        synchronized (session) {
            contexto = obtenerContextos(session).valores.get(token);
        }

        if (WebKeysCompras.isEmpty(token) || contexto == null) {
            throw new Exception(
                    "La pantalla de Compras esta desactualizada."
            );
        }

        boolean mismoIntegrante =
                (contexto.integrante != null
                        ? contexto.integrante.intValue()
                        : 0) == integrante;

        if (!mismoTexto(contexto.cuilTitular, cuilTitular)
                        || !mismoIntegrante
        ) {

            throw new Exception(
                    "El afiliado informado no corresponde al requerimiento."
            );
        }
    }

    public static void vincular(
            RenderRequest request,
            String cuilTitular,
            int integrante) throws Exception {

        if (request == null) {
            throw new Exception(
                    "No se pudo validar la pantalla de Compras."
            );
        }

        String token = ParamUtil.getString(request, PARAM_TOKEN);
        PortletSession session = request.getPortletSession();

        synchronized (session) {
            Contextos contextos = obtenerContextos(session);
            Contexto contexto = contextos.valores.get(token);

            if (WebKeysCompras.isEmpty(token) || contexto == null) {
                throw new Exception(
                        "La pantalla de Compras esta desactualizada."
                );
            }

            if (contexto.idRequerimientoCompra > 0
                    && (
                    !mismoTexto(contexto.cuilTitular, cuilTitular)
                            || (contexto.integrante != null
                            ? contexto.integrante.intValue()
                            : 0) != integrante
            )) {

                throw new Exception(
                        "El afiliado informado no corresponde al requerimiento."
                );
            }

            contextos.valores.put(
                    token,
                    new Contexto(
                            contexto.idRequerimientoCompra,
                            WebKeysCompras.trimToNull(cuilTitular),
                            Integer.valueOf(integrante)
                    )
            );
            session.setAttribute(SESSION_CONTEXTOS, contextos);
        }
    }

    private static Contextos obtenerContextos(
            PortletSession session) {

        Object value = session.getAttribute(SESSION_CONTEXTOS);

        if (value instanceof Contextos) {
            return (Contextos) value;
        }

        return new Contextos();
    }

    private static boolean mismoTexto(String a, String b) {
        String valorA = WebKeysCompras.trimToNull(a);
        String valorB = WebKeysCompras.trimToNull(b);

        return valorA == null ? valorB == null : valorA.equals(valorB);
    }

    private static final class Contexto implements Serializable {

        private static final long serialVersionUID = 1L;

        private final int idRequerimientoCompra;
        private final String cuilTitular;
        private final Integer integrante;

        private Contexto(
                int idRequerimientoCompra,
                String cuilTitular,
                Integer integrante) {

            this.idRequerimientoCompra = idRequerimientoCompra;
            this.cuilTitular = cuilTitular;
            this.integrante = integrante;
        }
    }

    private static final class Contextos implements Serializable {

        private static final long serialVersionUID = 1L;

        private final LinkedHashMap<String, Contexto> valores =
                new LinkedHashMap<String, Contexto>();
    }

    private ActualizarContactoAfiliadoCompraToken() {
    }
}
