package ar.com.ospim.autorizaciones.services;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.global.services.TraeListasServiceUtil;

/**
 * Cliente de autenticación AppMobile sin secretos embebidos en código.
 */
public final class ReclamoAppMobileAuthClient {

    private static final Log _log = LogFactoryUtil.getLog(
            ReclamoAppMobileAuthClient.class
    );

    private static final String CONFIG_HOST = "APP_HOST_WEBSERVICE";
    private static final String CONFIG_API_KEY = "APP_BACKOFFICE_API_KEY";
    private static final String CONFIG_EMAIL = "APP_BACKOFFICE_EMAIL";
    private static final String CONFIG_PASSWORD = "APP_BACKOFFICE_PASSWORD";
    private static final String LOGIN_PATH = "/api/auth/backoffice/login";

    private ReclamoAppMobileAuthClient() {
    }

    public static String obtenerToken() {
        String host = configuracion(CONFIG_HOST);
        String apiKey = configuracion(CONFIG_API_KEY);
        String email = configuracion(CONFIG_EMAIL);
        String password = configuracion(CONFIG_PASSWORD);

        if (vacio(host) || vacio(apiKey) || vacio(email) || vacio(password)) {
            _log.error(
                    "Configuración AppMobile incompleta. Se requieren "
                    + CONFIG_HOST + ", "
                    + CONFIG_API_KEY + ", "
                    + CONFIG_EMAIL + " y "
                    + CONFIG_PASSWORD + "."
            );
            return null;
        }

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(normalizarHost(host) + LOGIN_PATH);
        post.addRequestHeader("accept", "application/json");
        post.addRequestHeader("api-key", apiKey);
        post.addRequestHeader("Content-Type", "application/json");

        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);

            post.setRequestEntity(new StringRequestEntity(
                    body.toString(),
                    "application/json",
                    "UTF-8"
            ));

            int status = httpClient.executeMethod(post);
            String response = post.getResponseBodyAsString();
            if (status != 200) {
                _log.error("AppMobile rechazó autenticación. http="
                        + status
                        + " responseLength=" + longitud(response));
                return null;
            }

            JSONObject json = new JSONObject(response);
            String token = json.optString("token", "");
            if (vacio(token)) {
                _log.error("AppMobile autenticó sin devolver token.");
                return null;
            }

            return token.trim();
        } catch (Exception e) {
            _log.error("No se pudo obtener token AppMobile.", e);
            return null;
        } finally {
            post.releaseConnection();
        }
    }

    private static String configuracion(String clave) {
        try {
            return TraeListasServiceUtil.getSystemConfig(clave);
        } catch (Exception e) {
            _log.error("No se pudo leer configuración " + clave + ".", e);
            return null;
        }
    }

    private static boolean vacio(String valor) {
        return valor == null || valor.trim().length() == 0;
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

    private static int longitud(String texto) {
        return texto == null ? 0 : texto.length();
    }
}
