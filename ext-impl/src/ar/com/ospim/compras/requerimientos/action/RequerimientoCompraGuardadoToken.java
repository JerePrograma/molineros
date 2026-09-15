package ar.com.ospim.compras.requerimientos.action;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

public final class RequerimientoCompraGuardadoToken {

    public static final String PARAMETRO = "compras_save_token";

    private static final String ATRIBUTO = "COMPRAS_SAVE_TOKEN";
    private static final String SESION = "COMPRAS_SAVE_TOKENS";
    private static final int MAX_TOKENS = 20;

    private static final Object BLOQUEO = new Object();

    private RequerimientoCompraGuardadoToken() {
    }

    public static void publicar(RenderRequest request) {
        if (request == null) {
            return;
        }

        String token =
                UUID.randomUUID().toString();

        PortletSession session =
                request.getPortletSession();

        synchronized (BLOQUEO) {
            Set<String> tokens =
                    copiarTokens(
                            session.getAttribute(
                                    SESION
                            )
                    );

            while (tokens.size() >= MAX_TOKENS) {
                Iterator<String> iterator =
                        tokens.iterator();

                if (!iterator.hasNext()) {
                    break;
                }

                iterator.next();
                iterator.remove();
            }

            tokens.add(token);

            session.setAttribute(
                    SESION,
                    tokens
            );
        }

        request.setAttribute(
                ATRIBUTO,
                token
        );
    }

    public static void consumir(
            ActionRequest request,
            String valor)
            throws ValidacionCompraException {

        if (request == null) {
            throw new IllegalArgumentException(
                    "No se recibio la solicitud de guardado."
            );
        }

        String token =
                valor != null
                        ? valor.trim()
                        : "";

        PortletSession session =
                request.getPortletSession(false);

        if (session == null) {
            throw rechazoEnvio();
        }

        synchronized (BLOQUEO) {
            Set<String> tokens =
                    copiarTokens(
                            session.getAttribute(
                                    SESION
                            )
                    );

            if (token.length() == 0
                    || "null".equals(token)
                    || !tokens.remove(token)) {

                throw rechazoEnvio();
            }

            if (tokens.isEmpty()) {
                session.removeAttribute(
                        SESION
                );
            } else {
                session.setAttribute(
                        SESION,
                        tokens
                );
            }
        }
    }

    private static Set<String> copiarTokens(Object valor) {
        Set<String> tokens = new LinkedHashSet<String>();

        if (valor instanceof Set) {
            for (Object item : (Set<?>) valor) {
                if (item instanceof String) {
                    tokens.add((String) item);
                }
            }
        }

        return tokens;
    }

    private static ValidacionCompraException rechazoEnvio() {
        return new ValidacionCompraException(
                "guardar",
                "No se procesó este envío. Consulte el requerimiento "
                        + "antes de guardar nuevamente."
        );
    }
}
