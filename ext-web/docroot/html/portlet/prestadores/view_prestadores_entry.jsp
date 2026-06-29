<%@ include file="/html/portlet/prestadores/init.jsp"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<%
Prestador prestador =
(Prestador) request.getAttribute(
WebKeysLiquidaciones.PRESTADOR_EN_EDICION
);

if (prestador == null) {
prestador =
(Prestador) request.getSession().getAttribute(
WebKeysLiquidaciones.PRESTADOR_EN_EDICION
);
}

String cmd =
(String) request.getAttribute(Constants.CMD);

if (Validator.isNull(cmd)) {
cmd = ParamUtil.getString(
request,
Constants.CMD
);
}

int idPrestAux = 0;

if (prestador != null) {
idPrestAux = prestador.getId_prestador();
}

if (idPrestAux <= 0) {
idPrestAux = ParamUtil.getInteger(
request,
"prestador_id",
ParamUtil.getInteger(
request,
"id_prestador",
0
)
);
}

String tabValue =
ParamUtil.getString(
request,
"tab"
);

if (Validator.isNull(tabValue)) {
tabValue =
ParamUtil.getString(
request,
"tabs1"
);
}

if (Validator.isNull(tabValue)) {
tabValue =
(String) request.getAttribute(
"tab"
);
}

if (Validator.isNull(tabValue)) {
tabValue =
(String) request.getSession().getAttribute(
"tab"
);
}

if (Validator.isNull(tabValue)) {
tabValue = "datos";
}

/*

* Pestañas básicas del prestador.
  */
  String tabNames =
  "Datos,plan-prest,lugar-at";

String tabValues =
"datos,plan_prest,lugar_atencion";

/*

* El histórico sólo se muestra cuando el prestador
* ya fue guardado y tiene un identificador válido.
  */
  if (idPrestAux > 0) {
  tabNames +=
  ",historico-cotizacion";

  tabValues +=
  ",historico_cotizacion";
  }

/*

* Evita abrir manualmente el histórico durante
* el alta de un prestador todavía no persistido.
  */
  if (
  idPrestAux <= 0 &&
  "historico_cotizacion".equals(tabValue)
  ) {
  tabValue = "datos";
  }

PortletURL portletURL =
renderResponse.createRenderURL();

portletURL.setWindowState(
LiferayWindowState.MAXIMIZED
);

portletURL.setParameter(
"struts_action",
"/prestadores/editar_prestadores_entry"
);

if (Validator.isNotNull(cmd)) {
portletURL.setParameter(
Constants.CMD,
cmd
);
}

if (idPrestAux > 0) {
portletURL.setParameter(
"prestador_id",
String.valueOf(idPrestAux)
);
}
%>

<liferay-ui:error
key="error-prestador"
message="falta-prestador"
/>

<liferay-ui:error
key="error-prestador-existente"
message="prestador-existente"
/>

<liferay-ui:error
key="error-prestador-existente-con-fecha-baja"
message="prestador-existente-con-fecha-baja"
/>

<liferay-ui:error
key="error-prestador-profesion"
message="falta-prestador-profesion"
/>

<liferay-ui:error
key="error-prestador-matricula-nacional"
message="falta-prestador-matricula-nacional"
/>

<liferay-ui:error
key="error-prestador-matricula"
message="falta-prestador-matricula"
/>

<liferay-ui:error
key="error-prestador-lugar-at"
message="falta-prestador-domicilio"
/>

<liferay-ui:error
key="error-prestador-telefono"
message="falta-prestador-telefono"
/>

<liferay-ui:error
key="error-prestador-contacto"
message="falta-prestador-contactoe"
/>

<liferay-ui:error
key="error-prestador-contacto-facturacion"
message="falta-contacto-facturacion"
/>

<liferay-ui:error
key="error-prestador-cbu"
message="falta-prestador-cbu"
/>

<portlet:renderURL
windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"
var="volver">

<portlet:param
    name="struts_action"
    value="/prestadores/view"
/>

</portlet:renderURL>

<p>
    <a href="<%= volver %>">
        Volver
    </a>
</p>

<liferay-ui-custom:tabs
names="<%= tabNames %>"
tabsValues="<%= tabValues %>"
value="<%= tabValue %>"
url="<%= portletURL.toString() %>"
param="tab"
/>

<% if ("datos".equals(tabValue)) { %>

<liferay-util:include
    page="/html/portlet/prestadores/view_prestador.jsp"
/>

<% } else if ("plan_prest".equals(tabValue)) { %>

<liferay-util:include
    page="/html/portlet/prestadores/prestador_plan.jsp"
/>

<% } else if ("lugar_atencion".equals(tabValue)) { %>

<liferay-util:include
    page="/html/portlet/prestadores/prestador_lugar_atencion.jsp"
/>

<% } else if (
"historico_cotizacion".equals(tabValue) &&
idPrestAux > 0
) { %>

<liferay-util:include
    page="/html/portlet/prestadores/historico_cotizacion.jsp"
/>

<% } else { %>

<liferay-util:include
    page="/html/portlet/prestadores/view_prestador.jsp"
/>

<% } %>
