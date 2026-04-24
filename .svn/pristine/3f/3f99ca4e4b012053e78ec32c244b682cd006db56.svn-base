<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.afiliados.beans.AfiCuentasBancarias" %>
<%@ page import="ar.com.ospim.afiliados.services.AfiCuentasBancariasServiceUtil" %>
<%	
	
	ReclamoPrestacional reclamoprestacional = (ReclamoPrestacional) session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);



	Afiliado afiliadoTitular = reclamoprestacional != null ? reclamoprestacional.getAfiliadoTitular() : null;

	//List<AfiCuentaBancaria> afiCuenta = null;
	
	//nuevo
	List<AfiCuentasBancarias> afiCuenta = new ArrayList<AfiCuentasBancarias>();

	if (afiliadoTitular != null) {
	    List<AfiCuentasBancarias> todas = AfiCuentasBancariasServiceUtil.getCuentas(afiliadoTitular.getCuil_titular(), 0);
	
	    //filtramos solo las cuentas vigentes (baja_fecha = null)
	    for (AfiCuentasBancarias c : todas) {
	        if (c.getBajaFecha() == null) {
	            afiCuenta.add(c);
	        }
	    }
	}

	
	//afiCuenta  = ReclamosPrestacionesServiceUtil.traerCuentaBancariaAsociadas(afiliadoTitular.getCuil_titular());
	PortletURL portletURL = renderResponse.createRenderURL();				

	List<String> headerNames = new ArrayList<String>();
	headerNames.add("Titular/Apoderado");
	headerNames.add("CBU");
	headerNames.add("Email");
	headerNames.add("Apellido y Nombre");
	headerNames.add("Cuil");
	headerNames.add("Imagen CBU");
	headerNames.add("Imagen Nota Autorizada");
	headerNames.add("Seleccionar");
	
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
	SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
	LanguageUtil.get(pageContext, "no-cuentas-interna-were-found"));

	if(afiCuenta!=null){
							 	
		//seteo el total de la lista
	 	int total = afiCuenta.size();
	 	searchContainer.setTotal(total);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	 	List resultRows = searchContainer.getResultRows();
	 	for (int i = 0; i < afiCuenta.size(); i++) {

	 		//AfiCuentaBancaria cuenta = (AfiCuentaBancaria) afiCuenta.get(i);
	 		
	 		//nuevo
	 		AfiCuentasBancarias cuenta = afiCuenta.get(i);
	 		/*
	 		ResultRow row = new ResultRow(cuenta,cuenta.getCboTitular(), i);		
	 		
	 		String apeNom = cuenta.getApellido() + " "  + cuenta.getNombre();
	 		String cbu = cuenta.getAdjClaveCBU()  != null && cuenta.getAdjClaveCBU().length() > 1 ? "SI":"NO"; 
	 		String nota = cuenta.getAdjClaveNota() != null && cuenta.getAdjClaveNota().length() > 1 ? "SI":"NO";
	 		*/
	 		ResultRow row = new ResultRow(cuenta,cuenta.getCbu(), i);
	 		String apeNom = (cuenta.getApellido() != null ? cuenta.getApellido() : "") + " " +
	                (cuenta.getNombre() != null ? cuenta.getNombre() : "");
	 		String cbu = cuenta.getFileCbu()  != null && cuenta.getFileCbu().length() > 1 ? "SI":"NO"; 
	 		String nota = cuenta.getFileNotaAutorizada() != null && cuenta.getFileNotaAutorizada().length() > 1 ? "SI":"NO";
	 		
	 		
	 		/*
	 		row.addText(cuenta.getCboTitular().equals("0") == true ? "Titular ": "Apoderado " );
	 		row.addText(cuenta.getCbu());
			row.addText(cuenta.getEmail());
			row.addText(apeNom);
			row.addText(cuenta.getCuil());
			row.addText(cbu);
			row.addText(nota);*/
			
			//nuevo
			row.addText(cuenta.isTitular() ? "Titular" : "Apoderado");
			row.addText(cuenta.getCbu());
			row.addText((cuenta.getEmail() != null && !cuenta.getEmail().isEmpty()) ? cuenta.getEmail() : "");
			row.addText(apeNom);
			
			// CUIL a mostrar depende de titular/apoderado
						row.addText(cuenta.isTitular()
						        ? (cuenta.getCuilTitular() != null ? cuenta.getCuilTitular() : "")
						        : (cuenta.getCuilCbu() != null ? cuenta.getCuilCbu() : "")
						);
			
			row.addText(cuenta.getFileCbu() != null && !cuenta.getFileCbu().isEmpty() ? "SI" : "NO");
			row.addText(cuenta.getFileNotaAutorizada() != null && !cuenta.getFileNotaAutorizada().isEmpty() ? "SI" : "NO");

			
			Integer idCuentaSeleccionada = (Integer) session.getAttribute("ID_CUENTA_BANCARIA_SELECCIONADA");
			
			if (idCuentaSeleccionada == null || cuenta.getId() != idCuentaSeleccionada){
				StringBuilder sb= new StringBuilder();		
				sb.append("<img alt=\"<liferay-ui:message key='obs-interna'/>\" src=\"");
				sb.append(themeDisplay.getPathThemeImages());
				sb.append("/common/add_user.png\" onClick=\"javascript:obtenerCuenta('");
				sb.append(cuenta.getId());
				sb.append("');\" />");
				row.addText(sb.toString());	
			}else{
				row.addText(" ");
			}
			
 			resultRows.add(row);
	 	}
	}
%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	



<script type="text/javascript">

function obtenerCuenta(idCuenta) {

	  var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";
	  params = params + "&<%= Constants.CMD %>=" + "<%=WebKeysAutorizaciones.CUENTA_SELECT%>";
	  params = params + "&id_reclamosel=" + "<%=reclamoprestacional.getId_reclamo()%>";
	  params = params + "&id_cuenta_select=" + idCuenta;

	  
	  var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_seccional_entry" /></portlet:actionURL>';
	  url = url + "&esDatosTab=false";
	  url = url + params;
	  document.<portlet:namespace />reclamo_fm.method = 'post';
	
	  submitForm(document.<portlet:namespace />reclamo_fm, url);
	
}

</script>

