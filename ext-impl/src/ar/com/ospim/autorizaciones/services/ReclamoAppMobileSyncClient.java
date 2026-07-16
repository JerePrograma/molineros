package ar.com.ospim.autorizaciones.services;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.global.services.TraeListasServiceUtil;

/**
 * Cliente acotado para sincronizar estados de reintegros con AppMobile.
 *
 * A diferencia del método legacy, devuelve el resultado real de la operación
 * HTTP y permite que el llamador registre una reconciliación pendiente.
 */
public final class ReclamoAppMobileSyncClient {

    private static final Log _log = LogFactoryUtil.getLog(
            ReclamoAppMobileSyncClient.class
    );

    private static final String CONFIG_HOST = "APP_HOST_WEBSERVICE";
    private static final String PATH_ESTADO_REINTEGRO =
            "/api/auth/pedidoreintegro/estado/";

    private ReclamoAppMobileSyncClient() {
    }

    public static boolean actualizarEstadoReintegro(
            int idReintegro,
            String nuevoEstado,
            String token) {

        if (idReintegro <= 0) {
            _log.error("Id de reintegro inválido para sincronización: "
                    + idReintegro);
            return false;
        }

        if (nuevoEstado == null || nuevoEstado.trim().length() == 0) {
            _log.error("Estado externo vacío para reintegro " + idReintegro);
            return false;
        }

        if (token == null || token.trim().length() == 0) {
            _log.error("Token inválido para sincronizar reintegro "
                    + idReintegro);
            return false;
        }

        String host = TraeListasServiceUtil.getSystemConfig(CONFIG_HOST);
        if (host == null || host.trim().length() == 0) {
            _log.error("No está configurado " + CONFIG_HOST
                    + " para sincronizar reintegro " + idReintegro);
            return false;
        }

        String url = normalizarHost(host)
                + PATH_ESTADO_REINTEGRO
                + idReintegro
                + "?estado="
                + nuevoEstado.trim();

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(url);
        post.addRequestHeader("accept", "application/json");
        post.addRequestHeader("Authorization", "Bearer " + token.trim());

        try {
            int status = httpClient.executeMethod(post);
            String response = post.getResponseBodyAsString();

            if (status == 200 || status == 204) {
                _log.info("AppMobile confirmó estado " + nuevoEstado
                        + " para reintegro " + idReintegro);
                return true;
            }

            _log.error("AppMobile rechazó actualización de reintegro. id="
                    + idReintegro
                    + " estado=" + nuevoEstado
                    + " http=" + status
                    + " respuesta=" + limitar(response, 1000));
            return false;
        } catch (Exception e) {
            _log.error("Error sincronizando reintegro " + idReintegro
                    + " con estado " + nuevoEstado, e);
            return false;
        } finally {
            post.releaseConnection();
        }
    }

    private static String normalizarHost(String host) {
        String normalizado = host.trim();
        while (normalizado.endsWith("/")) {
            normalizado = normalizado.substring(
                    0,
                    normalizado.length() - 1
            );
        }
        return normalizado;
    }

    private static String limitar(String texto, int maximo) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= maximo) {
            return texto;
        }
        return texto.substring(0, maximo) + "...";
    }
}
