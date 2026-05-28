<%@ include file="/html/portlet/init.jsp" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="ar.com.uoma.beans.Incidente" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<%!
private String jsAfiliadoAutoSelect(Object value) {
    if (value == null) {
        return "";
    }

    return String.valueOf(value)
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E");
}

private boolean esNombreFuncionJavascriptValido(String value) {
    if (value == null || value.trim().length() == 0) {
        return false;
    }

    String[] partes = value.trim().split("\\.");

    for (int i = 0; i < partes.length; i++) {
        String parte = partes[i];

        if (parte.length() == 0) {
            return false;
        }

        char primero = parte.charAt(0);

        if (!Character.isLetter(primero) && primero != '_' && primero != '$') {
            return false;
        }

        for (int j = 1; j < parte.length(); j++) {
            char c = parte.charAt(j);

            if (!Character.isLetterOrDigit(c) && c != '_' && c != '$') {
                return false;
            }
        }
    }

    return true;
}

private String fechaRecepcionIncidente(Afiliado afiliado) {
    if (afiliado == null || afiliado.getIncidentes() == null || afiliado.getIncidentes().isEmpty()) {
        return "0";
    }

    Incidente incidente = (Incidente) afiliado.getIncidentes().iterator().next();

    if (incidente == null || incidente.getFechaRecepcion() == null) {
        return "0";
    }

    return new SimpleDateFormat("dd-MM-yyyy").format(incidente.getFechaRecepcion());
}
%>

<%
List<Afiliado> afiliadosList =
        (List<Afiliado>) renderRequest.getAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO);

int totalAfiliados = afiliadosList != null ? afiliadosList.size() : 0;
String funcionSeleccion = ParamUtil.getString(request, "funcion_seleccion", "seleccionaCamposAfiliado");

if (!esNombreFuncionJavascriptValido(funcionSeleccion)) {
    funcionSeleccion = "seleccionaCamposAfiliado";
}
%>

<script type="text/javascript">
    (function() {
        function resolverFuncionAfiliado(nombre) {
            var partes = String(nombre || '').split('.');
            var actual = window;

            for (var i = 0; i < partes.length; i++) {
                if (!partes[i] || actual == null) {
                    return null;
                }

                actual = actual[partes[i]];
            }

            return typeof actual == 'function' ? actual : null;
        }

        function mostrarMensajeAfiliadoAutoSelect(mensaje) {
            if (typeof <portlet:namespace />mostrarMensajeAfiliadoInicial == 'function') {
                <portlet:namespace />mostrarMensajeAfiliadoInicial(mensaje);
            }
        }

        <% if (totalAfiliados == 1) {
            Afiliado afiliado = afiliadosList.get(0);
            String idSeccional = "";
            String descSeccional = "";

            if (afiliado.getSeccional() != null) {
                idSeccional = String.valueOf(afiliado.getSeccional().getId());
                descSeccional = afiliado.getSeccional().getDescripcion();
            }

            String idPlan = afiliado.getUltimo_plan() != null
                    ? String.valueOf(afiliado.getUltimo_plan().getId())
                    : "0";
            String nroSocioPrev = afiliado.getPrevencion() != null
                    ? String.valueOf(afiliado.getPrevencion().getNroSocio())
                    : "0";
            String nroCredencialPrev = afiliado.getPrevencion() != null
                    ? String.valueOf(afiliado.getPrevencion().getNroCredencial())
                    : "0";
            String tieneAntecedentes = afiliado.getTieneAntecedentesJudiciales() == 1 ? "1" : "0";
        %>
            var callback = resolverFuncionAfiliado('<%= jsAfiliadoAutoSelect(funcionSeleccion) %>');

            if (callback == null) {
                callback = resolverFuncionAfiliado('seleccionaCamposAfiliado');
            }

            if (callback != null) {
                try {
                    callback.apply(window, [
                            '<%= jsAfiliadoAutoSelect(afiliado.getCuil_titular()) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getInteAsString()) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getDocumento_tipo()) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getDocu_numero()) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getNombre()) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getApellido()) %>',
                            '<%= jsAfiliadoAutoSelect(idSeccional) %>',
                            '<%= jsAfiliadoAutoSelect(descSeccional) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getId_ospim()) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getId_uoma()) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getId_amtima()) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getBaja_fechaAsString()) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getNombrePlan()) %>',
                            '<%= jsAfiliadoAutoSelect(idPlan) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getAlta_fechaAsString()) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getDiscapacitado()) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getId_tercerizadora()) %>',
                            '<%= jsAfiliadoAutoSelect(afiliado.getDesc_tercerizadora()) %>',
                            '<%= afiliado.getConReclamoPrestacional() ? "1" : "0" %>',
                            '<%= jsAfiliadoAutoSelect(nroSocioPrev) %>',
                            '<%= jsAfiliadoAutoSelect(nroCredencialPrev) %>',
                            '<%= jsAfiliadoAutoSelect(fechaRecepcionIncidente(afiliado)) %>',
                            '<%= tieneAntecedentes %>'
                    ]);

                    if (typeof <portlet:namespace />sincronizarAfiliadoRequerimiento == 'function') {
                        <portlet:namespace />sincronizarAfiliadoRequerimiento();
                    }

                    if (typeof <portlet:namespace />actualizarVisibilidadAfiliado == 'function') {
                        <portlet:namespace />actualizarVisibilidadAfiliado(false);
                    }

                    mostrarMensajeAfiliadoAutoSelect('');
                }
                catch (e) {
                    mostrarMensajeAfiliadoAutoSelect('No se pudo cargar automaticamente el afiliado. Puede buscarlo manualmente.');
                }
            }
            else {
                mostrarMensajeAfiliadoAutoSelect('No se encontro la funcion para cargar el afiliado. Puede buscarlo manualmente.');
            }
        <% } else if (totalAfiliados == 0) { %>
            mostrarMensajeAfiliadoAutoSelect('No se encontro un afiliado para el CUIL e integrante informados. Puede buscarlo manualmente.');
        <% } else { %>
            mostrarMensajeAfiliadoAutoSelect('La busqueda inicial encontro mas de un afiliado. Puede seleccionarlo manualmente con Buscar afiliado.');
        <% } %>
    })();
</script>
