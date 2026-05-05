<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.AutorizacionPrestacional" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ include file="/html/portlet/autorizaciones/init.jsp" %>

<%!
private String estadoTxt(int e) {
    switch (e) {
        case 0: return "PREAUTORIZACIÓN";
        case 1: return "EN CURSO";
        case 2: return "DOC. FALTANTE";
        case 3: return "CAMBIO PRESTADOR";
        case 4: return "FINALIZADO";
        case 5: return "ABANDONADO";
        case 6: return "RECHAZADO";
        case 7: return "MONOTRIBUTO";
        default: return String.valueOf(e);
    }
}

private boolean distinto(Date a, Date b) {
    return (a == null && b != null) ||
           (a != null && b == null) ||
           (a != null && b != null && !a.equals(b));
}

private boolean distinto(BigDecimal a, BigDecimal b) {
    return (a == null && b != null) ||
           (a != null && b == null) ||
           (a != null && b != null && a.compareTo(b) != 0);
}

private boolean hayCambios(AutorizacionPrestacional a, AutorizacionPrestacional prev) {
    if (a == null || prev == null) return false;

    // Estado
    if (a.getEstado() != prev.getEstado()) return true;

    // Períodos
    if (distinto(a.getPeriodo_desde(), prev.getPeriodo_desde())) return true;
    if (distinto(a.getPeriodo_hasta(), prev.getPeriodo_hasta())) return true;

    // Cantidad / Importe
    if (distinto(a.getCantidad(), prev.getCantidad())) return true;
    if (distinto(a.getImporte_total(), prev.getImporte_total())) return true;

    return false;
}
%>

<%
List<AutorizacionPrestacional> histo =
    (List<AutorizacionPrestacional>) renderRequest.getAttribute("HISTORICO_AUTORIZACIONES");

AutorizacionPrestacional actual =
    (AutorizacionPrestacional) renderRequest.getAttribute("AUTORIZACION_ACTUAL");

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

String titulo = "Historial de Autorización";
if (actual != null) {
    titulo += " - Nro: " + actual.getNroAutorizacion();
}

//para saber si existe al menos 1 cambio entre registros
boolean hayCambios = false;
if (histo != null) {
    for (int i = 0; i < histo.size() - 1; i++) {
        AutorizacionPrestacional a = histo.get(i);
        AutorizacionPrestacional prev = histo.get(i + 1);
        if (hayCambios(a, prev)) {
            hayCambios = true;
            break;
        }
    }
}
%>

<div class="section-container">
  <div class="portlet-section-header">
    <h3><%= titulo %></h3>
  </div>

  <% if (histo == null || histo.isEmpty() || !hayCambios) { %>
    <div class="portlet-msg-info" style="margin-top:10px;">
      No se encontraron cambios en el historial.
    </div>
  <% } else { %>

    <table class="taglib-search-iterator" width="100%" cellpadding="4" cellspacing="0" border="0">
      <thead>
        <tr class="portlet-section-header results-header">
          <th>Fecha</th>
          <th>Usuario</th>
          <th>Cambios</th>
          <th>Anterior</th>
          <th>Actual</th>
        </tr>
      </thead>

      <tbody>
<%
for (int i = 0; i < histo.size(); i++) {

    AutorizacionPrestacional a = histo.get(i);
    AutorizacionPrestacional prev = (i + 1 < histo.size()) ? histo.get(i + 1) : null;

    if (prev == null) {
        continue;
    }

    // Si entre a y prev no hay cambios, no imprimo fila
    if (!hayCambios(a, prev)) {
        continue;
    }

    String claseFila = (i % 2 == 0) ? "results-row alt" : "results-row";

    String fecha = (a.getModi_fecha() != null) ? sdf.format(a.getModi_fecha()) : "";
    String usr   = Validator.isNotNull(a.getModi_usr()) ? a.getModi_usr() : "";

    StringBuilder cambios = new StringBuilder();
    StringBuilder anterior = new StringBuilder();
    StringBuilder actualSB = new StringBuilder();

    // ESTADO
    if (a.getEstado() != prev.getEstado()) {
        cambios.append("ESTADO");
        anterior.append(estadoTxt(prev.getEstado()));
        actualSB.append(estadoTxt(a.getEstado()));
    }

    // PERÍODO DESDE
    Date pdA = prev.getPeriodo_desde();
    Date pdB = a.getPeriodo_desde();
    boolean cambioPd =
        (pdA == null && pdB != null) ||
        (pdA != null && pdB == null) ||
        (pdA != null && pdB != null && !pdA.equals(pdB));

    if (cambioPd) {
        if (cambios.length() > 0) { cambios.append("<br/>"); anterior.append("<br/>"); actualSB.append("<br/>"); }
        cambios.append("PERÍODO DESDE");
        anterior.append((pdA != null) ? sdf.format(pdA) : "");
        actualSB.append((pdB != null) ? sdf.format(pdB) : "");
    }

    // PERÍODO HASTA
    Date phA = prev.getPeriodo_hasta();
    Date phB = a.getPeriodo_hasta();
    boolean cambioPh =
        (phA == null && phB != null) ||
        (phA != null && phB == null) ||
        (phA != null && phB != null && !phA.equals(phB));

    if (cambioPh) {
        if (cambios.length() > 0) { cambios.append("<br/>"); anterior.append("<br/>"); actualSB.append("<br/>"); }
        cambios.append("PERÍODO HASTA");
        anterior.append((phA != null) ? sdf.format(phA) : "");
        actualSB.append((phB != null) ? sdf.format(phB) : "");
    }

    // CANTIDAD
    BigDecimal cantA = prev.getCantidad();
    BigDecimal cantB = a.getCantidad();
    boolean cambioCant =
        (cantA == null && cantB != null) ||
        (cantA != null && cantB == null) ||
        (cantA != null && cantB != null && cantA.compareTo(cantB) != 0);

    if (cambioCant) {
        if (cambios.length() > 0) { cambios.append("<br/>"); anterior.append("<br/>"); actualSB.append("<br/>"); }
        cambios.append("CANTIDAD");
        anterior.append((cantA != null) ? cantA.toString() : "");
        actualSB.append((cantB != null) ? cantB.toString() : "");
    }

    // IMPORTE TOTAL
    BigDecimal impA = prev.getImporte_total();
    BigDecimal impB = a.getImporte_total();
    boolean cambioImp =
        (impA == null && impB != null) ||
        (impA != null && impB == null) ||
        (impA != null && impB != null && impA.compareTo(impB) != 0);

    if (cambioImp) {
        if (cambios.length() > 0) { cambios.append("<br/>"); anterior.append("<br/>"); actualSB.append("<br/>"); }
        cambios.append("IMPORTE TOTAL");
        anterior.append((impA != null) ? impA.toString() : "");
        actualSB.append((impB != null) ? impB.toString() : "");
    }
%>
        <tr class="<%= claseFila %>">
            <td><%= fecha %></td>
            <td><%= usr %></td>
            <td><%= cambios.toString() %></td>
            <td><%= anterior.toString() %></td>
            <td><%= actualSB.toString() %></td>
        </tr>
<%
}
%>
      </tbody>
    </table>

  <% } %>
</div>
