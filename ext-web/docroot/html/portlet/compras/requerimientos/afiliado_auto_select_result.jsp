<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="ar.com.uoma.beans.Incidente" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>



<%!
private String jsAfiliadoCompra(Object value) {
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
%>

<script type="text/javascript">
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
        if (typeof seleccionaCamposAfiliado == 'function') {
            seleccionaCamposAfiliado(
                    '<%= jsAfiliadoCompra(afiliado.getCuil_titular()) %>',
                    '<%= jsAfiliadoCompra(afiliado.getInteAsString()) %>',
                    '<%= jsAfiliadoCompra(afiliado.getDocumento_tipo()) %>',
                    '<%= jsAfiliadoCompra(afiliado.getDocu_numero()) %>',
                    '<%= jsAfiliadoCompra(afiliado.getNombre()) %>',
                    '<%= jsAfiliadoCompra(afiliado.getApellido()) %>',
                    '<%= jsAfiliadoCompra(idSeccional) %>',
                    '<%= jsAfiliadoCompra(descSeccional) %>',
                    '<%= jsAfiliadoCompra(afiliado.getId_ospim()) %>',
                    '<%= jsAfiliadoCompra(afiliado.getId_uoma()) %>',
                    '<%= jsAfiliadoCompra(afiliado.getId_amtima()) %>',
                    '<%= jsAfiliadoCompra(afiliado.getBaja_fechaAsString()) %>',
                    '<%= jsAfiliadoCompra(afiliado.getNombrePlan()) %>',
                    '<%= jsAfiliadoCompra(idPlan) %>',
                    '<%= jsAfiliadoCompra(afiliado.getAlta_fechaAsString()) %>',
                    '<%= jsAfiliadoCompra(afiliado.getDiscapacitado()) %>',
                    '<%= jsAfiliadoCompra(afiliado.getId_tercerizadora()) %>',
                    '<%= jsAfiliadoCompra(afiliado.getDesc_tercerizadora()) %>',
                    '<%= afiliado.getConReclamoPrestacional() ? "1" : "0" %>',
                    '<%= jsAfiliadoCompra(nroSocioPrev) %>',
                    '<%= jsAfiliadoCompra(nroCredencialPrev) %>',
                    '<%= jsAfiliadoCompra(fechaRecepcionIncidente(afiliado)) %>',
                    '<%= tieneAntecedentes %>'
            );

            if (typeof <portlet:namespace />sincronizarAfiliadoRequerimiento == 'function') {
                <portlet:namespace />sincronizarAfiliadoRequerimiento();
            }

            if (typeof <portlet:namespace />actualizarVisibilidadAfiliado == 'function') {
                <portlet:namespace />actualizarVisibilidadAfiliado(false);
            }

            if (typeof <portlet:namespace />mostrarMensajeAfiliadoInicial == 'function') {
                <portlet:namespace />mostrarMensajeAfiliadoInicial('');
            }
        }
    <% } else if (totalAfiliados == 0) { %>
        if (typeof <portlet:namespace />mostrarMensajeAfiliadoInicial == 'function') {
            <portlet:namespace />mostrarMensajeAfiliadoInicial('No se encontro un afiliado para el CUIL e integrante informados. Puede buscarlo manualmente.');
        }
    <% } else { %>
        if (typeof <portlet:namespace />mostrarMensajeAfiliadoInicial == 'function') {
            <portlet:namespace />mostrarMensajeAfiliadoInicial('La busqueda inicial encontro mas de un afiliado. Puede seleccionarlo manualmente con Buscar afiliado.');
        }
    <% } %>
</script>
