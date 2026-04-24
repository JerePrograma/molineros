<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<script type="text/javascript">

function pasarParametrosAParentcodPostal(param) {
    jQuery("#<portlet:namespace />cod_postal").val(param);   
    <portlet:namespace />cerrarCodPostal();
}

</script>
<%
	//obtengo lista de session
	PortletSession ps = renderRequest.getPortletSession();
	List<Direccion> direcciones = new ArrayList<Direccion>();	
	String calle = (String)renderRequest.getParameter("calle");
	String numeroS = (String)renderRequest.getParameter("numero");
	int numero = 0;
	if (numeroS != null && !numeroS.equals("")) {
		numero = Integer.parseInt(numeroS);		
	}
	if (ps != null) {  		  
		direcciones = TraeListasServiceUtil.getCodPostales(calle);		 
	}	

	//recupero coincidencias		
	int total = direcciones.size();
	if (direcciones != null && total > 0) {
		//Seteo el total de la lista.
		//Si existe una sola coincidencia la plancho en los campos del parent
		if (total == 1) {
			Direccion dirUnica = direcciones.get(0);
			%>
				<script type="text/javascript">
					pasarParametrosAParentcodPostal("<%=dirUnica.getCp()%>");
				</script>
			<%
		//More de una coincidencia
		} else {
			int i = 0;
			boolean flag = false;
			while (i < total && !flag) {
				if (numero > 0) {
					int num_ini = direcciones.get(i).getAltura_inicio();
					int num_fin = direcciones.get(i).getAltura_fin();
					if (numero >= num_ini && numero <= num_fin) {
						flag = true;
						%><script type="text/javascript">
							pasarParametrosAParentcodPostal("<%=direcciones.get(i).getCp()%>");
						</script><%
					}					
				}
				i++;
			}			
		}
	}
%>