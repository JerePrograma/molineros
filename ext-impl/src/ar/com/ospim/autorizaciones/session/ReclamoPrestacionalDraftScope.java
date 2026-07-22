package ar.com.ospim.autorizaciones.session;

import java.util.regex.Pattern;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.liferay.portal.kernel.util.ParamUtil;

/**
 * Convención central para migrar el estado global de sesión de Reclamos
 * Prestacionales hacia claves aisladas por borrador.
 *
 * La ausencia de draftId mantiene compatibilidad con el flujo legacy. Cuando
 * está presente debe respetar el formato generado por el guard de pestañas.
 */
public final class ReclamoPrestacionalDraftScope {

    public static final String PARAM_DRAFT_ID = "reclamoDraftId";
    public static final String LEGACY_DRAFT_ID = "legacy";

    private static final Pattern DRAFT_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_-]{8,80}$"
    );

    private ReclamoPrestacionalDraftScope() {
    }

    public static String resolver(HttpServletRequest request) {
        return normalizar(ParamUtil.getString(request, PARAM_DRAFT_ID));
    }

    public static String resolver(PortletRequest request) {
        return normalizar(ParamUtil.getString(request, PARAM_DRAFT_ID));
    }

    public static String normalizar(String draftId) {
        if (draftId == null || draftId.trim().length() == 0) {
            return LEGACY_DRAFT_ID;
        }

        String normalizado = draftId.trim();
        if (!DRAFT_PATTERN.matcher(normalizado).matches()) {
            throw new IllegalArgumentException(
                    "Identificador de borrador de reclamo inválido."
            );
        }
        return normalizado;
    }

    public static boolean esLegacy(String draftId) {
        return LEGACY_DRAFT_ID.equals(normalizar(draftId));
    }

    public static String clave(String claveBase, String draftId) {
        if (claveBase == null || claveBase.trim().length() == 0) {
            throw new IllegalArgumentException(
                    "La clave base de sesión es obligatoria."
            );
        }

        String scope = normalizar(draftId);
        if (LEGACY_DRAFT_ID.equals(scope)) {
            return claveBase;
        }
        return claveBase + "::DRAFT::" + scope;
    }

    public static Object obtener(
            HttpSession session,
            String claveBase,
            String draftId) {

        validarSession(session);
        return session.getAttribute(clave(claveBase, draftId));
    }

    public static void guardar(
            HttpSession session,
            String claveBase,
            String draftId,
            Object valor) {

        validarSession(session);
        String clave = clave(claveBase, draftId);
        if (valor == null) {
            session.removeAttribute(clave);
        } else {
            session.setAttribute(clave, valor);
        }
    }

    public static void eliminar(
            HttpSession session,
            String claveBase,
            String draftId) {

        validarSession(session);
        session.removeAttribute(clave(claveBase, draftId));
    }

    public static Object obtener(
            PortletSession session,
            String claveBase,
            String draftId) {

        validarSession(session);
        return session.getAttribute(
                clave(claveBase, draftId),
                PortletSession.APPLICATION_SCOPE
        );
    }

    public static void guardar(
            PortletSession session,
            String claveBase,
            String draftId,
            Object valor) {

        validarSession(session);
        String clave = clave(claveBase, draftId);
        if (valor == null) {
            session.removeAttribute(
                    clave,
                    PortletSession.APPLICATION_SCOPE
            );
        } else {
            session.setAttribute(
                    clave,
                    valor,
                    PortletSession.APPLICATION_SCOPE
            );
        }
    }

    public static void eliminar(
            PortletSession session,
            String claveBase,
            String draftId) {

        validarSession(session);
        session.removeAttribute(
                clave(claveBase, draftId),
                PortletSession.APPLICATION_SCOPE
        );
    }

    private static void validarSession(Object session) {
        if (session == null) {
            throw new IllegalArgumentException(
                    "La sesión es obligatoria para el scope de borrador."
            );
        }
    }
}
