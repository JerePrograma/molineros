<%
List<Provincia> provincias = (ArrayList<Provincia>) portletSession
.getAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (provincias == null) {
	provincias = TraeListasServiceUtil.getProvincias();
	portletSession.setAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,
	provincias,PortletSession.APPLICATION_SCOPE);	
}

List<Localidad> localidades = (ArrayList<Localidad>) portletSession
.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION,
PortletSession.APPLICATION_SCOPE);

if (localidades == null || localidades.size()==0) {
	localidades = TraeListasServiceUtil.getLocalidades();
	portletSession.setAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION,
	localidades,PortletSession.APPLICATION_SCOPE);	
}

/* List<Seccional> seccionales = (ArrayList<Seccional>) portletSession
.getAttribute(WebKeysAfiliados.SECCIONALES_EN_SESSION,
PortletSession.APPLICATION_SCOPE); */

List<Prestador.TipoPrestador> tiposPrestador = (ArrayList<Prestador.TipoPrestador>) portletSession
.getAttribute(WebKeysLiquidaciones.TIPOSPRESTADOR_EN_SESSION,
PortletSession.APPLICATION_SCOPE);

List<ProfesionPrestador> profesionPrestador = (ArrayList<ProfesionPrestador>) portletSession
.getAttribute(WebKeysLiquidaciones.LISTAS_DE_PROFESION_PRESTADOR_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (profesionPrestador == null) {
	profesionPrestador = TraeListasServiceUtil.getProfesionesPrestador();
	portletSession.setAttribute(WebKeysLiquidaciones.LISTAS_DE_PROFESION_PRESTADOR_EN_SESSION ,
	profesionPrestador, PortletSession.APPLICATION_SCOPE);
}

List<EspecialidadPrestador> especialidadPrestador = (ArrayList<EspecialidadPrestador>) portletSession
.getAttribute(WebKeysLiquidaciones.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (especialidadPrestador == null) {
	especialidadPrestador = TraeListasServiceUtil.getEspecialidadesPrestador();
	portletSession.setAttribute(WebKeysLiquidaciones.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION,
	especialidadPrestador, PortletSession.APPLICATION_SCOPE);
}

List<SubEspecialidadPrestador> subEspecialidadPrestador = (ArrayList<SubEspecialidadPrestador>) portletSession
.getAttribute(WebKeysLiquidaciones.LISTAS_DE_SUB_ESPECIALIDAD_PRESTADOR_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (subEspecialidadPrestador == null) {
	subEspecialidadPrestador = TraeListasServiceUtil.getSubEspecialidadesPrestador();
	portletSession.setAttribute(WebKeysLiquidaciones.LISTAS_DE_SUB_ESPECIALIDAD_PRESTADOR_EN_SESSION,
	subEspecialidadPrestador, PortletSession.APPLICATION_SCOPE);
}


//DS - Manejo Localidades por Provincia
//Map<Integer,List<Localidad>> localidadesPorProvincia = TraeListasServiceUtil.getLocalidadesAgrupadasPorProvincia();

Map<Integer,List<Localidad>> localidadesPorProvincia = (Map<Integer,List<Localidad>>) portletSession
.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION_POR_PROVINCIA, PortletSession.APPLICATION_SCOPE);

if (localidadesPorProvincia == null || localidadesPorProvincia.size()==0) {
	localidadesPorProvincia = new HashMap<Integer,List<Localidad>>();	 
		
	for(Localidad l:localidades){
		if(l!=null && l.getId_provincia()>0 && l.getDescripcion()!=null && !"".equalsIgnoreCase(l.getDescripcion().trim() )){
			List<Localidad> lst =  new ArrayList<Localidad>();
			try{
			  lst = localidadesPorProvincia.get(l.getId_provincia());
			  if(lst==null) lst =  new ArrayList<Localidad>();
			}catch(Exception e){
			  lst =  new ArrayList<Localidad>();
			}
			lst.add(l);
			localidadesPorProvincia.put(l.getId_provincia(), lst);
	   }
	}
	
	portletSession.setAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION_POR_PROVINCIA,
			localidadesPorProvincia,PortletSession.APPLICATION_SCOPE);

}

//DS


%>
