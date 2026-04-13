<%

List<TiposDeSituacionesMedicas> listatipodesituacionesmedicas   = (ArrayList<TiposDeSituacionesMedicas>) portletSession
.getAttribute(WebKeysAutorizaciones.TIPOS_SITUACIONES_MEDICAS_EN_SESION,
		PortletSession.APPLICATION_SCOPE);

if (listatipodesituacionesmedicas == null) {
	listatipodesituacionesmedicas = TraeListasServiceUtil.getTipoSituacionesMedicas();
	portletSession.setAttribute(WebKeysAutorizaciones.TIPOS_SITUACIONES_MEDICAS_EN_SESION ,
			listatipodesituacionesmedicas, PortletSession.APPLICATION_SCOPE);
}



%>