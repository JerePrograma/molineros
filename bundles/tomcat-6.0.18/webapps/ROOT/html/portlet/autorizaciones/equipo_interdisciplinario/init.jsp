<%
List<OpcionesPrestacion> listaOpciones  = (ArrayList<OpcionesPrestacion>) portletSession
.getAttribute(WebKeysAutorizaciones.OPCIONES_PRESTACION_EN_SESION,
		PortletSession.APPLICATION_SCOPE);

if (listaOpciones == null) {
	listaOpciones = TraeListasServiceUtil.getOpcionesPrestacion("23101");
	portletSession.setAttribute(WebKeysAutorizaciones.OPCIONES_PRESTACION_EN_SESION ,
			listaOpciones, PortletSession.APPLICATION_SCOPE);
}

%>