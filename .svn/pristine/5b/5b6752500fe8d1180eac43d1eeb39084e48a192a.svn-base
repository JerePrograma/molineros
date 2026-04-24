<%

List<TiposDeVentas> listatipodeventas   = (ArrayList<TiposDeVentas>) portletSession
.getAttribute(WebKeysFarmaciaOspim.TIPOS_DE_VENTAS_EN_SESION ,
		PortletSession.APPLICATION_SCOPE);

if (listatipodeventas == null) {
	listatipodeventas = FarmaciaServiceUtil.getTipoDeVentas() ;
	portletSession.setAttribute(WebKeysFarmaciaOspim.TIPOS_DE_VENTAS_EN_SESION ,
			listatipodeventas, PortletSession.APPLICATION_SCOPE);
}



%>