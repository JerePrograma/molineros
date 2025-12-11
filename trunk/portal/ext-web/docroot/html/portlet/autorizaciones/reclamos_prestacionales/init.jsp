<%
List<EstadosReclamosPrestacionales> listaestados = (ArrayList<EstadosReclamosPrestacionales>) portletSession
.getAttribute(WebKeysAutorizaciones.ESTADOS_RECLAMOS_PRESTACIONES_EN_SESION,
		PortletSession.APPLICATION_SCOPE);

if (listaestados == null) {
	listaestados = TraeListasServiceUtil.getEstadosReclamos();
	portletSession.setAttribute(WebKeysAutorizaciones.ESTADOS_RECLAMOS_PRESTACIONES_EN_SESION ,
			listaestados, PortletSession.APPLICATION_SCOPE);
}




List<TiposDeGestionReclamosPrestacionales> listatipogestionreclamos  = (ArrayList<TiposDeGestionReclamosPrestacionales>) portletSession
.getAttribute(WebKeysAutorizaciones.TIPOS_GESTION_RECLAMOS_PRESTACIONES_EN_SESION,
		PortletSession.APPLICATION_SCOPE);

if (listatipogestionreclamos == null) {
	listatipogestionreclamos = TraeListasServiceUtil.getTiposGestionReclamosPrestacionales();
	portletSession.setAttribute(WebKeysAutorizaciones.TIPOS_GESTION_RECLAMOS_PRESTACIONES_EN_SESION ,
			listatipogestionreclamos, PortletSession.APPLICATION_SCOPE);
}






List<ReclamosPrestacionalesRevisionEstado> listaRevisionEstado  = (ArrayList<ReclamosPrestacionalesRevisionEstado>) portletSession
.getAttribute(WebKeysAutorizaciones.RECLAMOS_PRESTACIONALES_REVISION_ESTADO_EN_SESION,
		PortletSession.APPLICATION_SCOPE);

if (listaRevisionEstado == null) {
	listaRevisionEstado = TraeListasServiceUtil.getReclamosPrestacionalesRevisionEstado();
	portletSession.setAttribute(WebKeysAutorizaciones.RECLAMOS_PRESTACIONALES_REVISION_ESTADO_EN_SESION ,
			listaRevisionEstado, PortletSession.APPLICATION_SCOPE);
}





List<ReclamosPrestacionalesIntegracion> listaIntegracion  = (ArrayList<ReclamosPrestacionalesIntegracion>) portletSession
.getAttribute(WebKeysAutorizaciones.RECLAMOS_PRESTACIONALES_INTEGRACION_EN_SESION,
		PortletSession.APPLICATION_SCOPE);

if (listaIntegracion == null) {
	listaIntegracion = TraeListasServiceUtil.getReclamosPrestacionalesIntegracion();
	portletSession.setAttribute(WebKeysAutorizaciones.RECLAMOS_PRESTACIONALES_INTEGRACION_EN_SESION ,
			listaIntegracion, PortletSession.APPLICATION_SCOPE);
}




%>