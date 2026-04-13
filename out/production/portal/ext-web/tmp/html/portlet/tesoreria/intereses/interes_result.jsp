<%@ include file="/html/portlet/tesoreria/init.jsp"%>

<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.beans.interes.Interes" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysInteres" %>

<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.WorkflowDefinition" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd");
Date auxDate = null;
String auxFechaIni = null;
String auxFechaFin = null;
String auxInteres = null;


String portlet_name=null;
portlet_name = "tesoreria";
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}


boolean rolAdministradorInteres = PermissionUtil.userContainsRole(user,WebKeysInteres.ROL_ADMINISTRADOR_INTERES);

//List<CajaChica> archivos=(List<CajaChica>)session.getAttribute("ListaCajasChicas");
List<Interes> _interes=(List<Interes>)session.getAttribute("ListaInteres");

List<String> headerNames = new ArrayList<String>();
headerNames.add("Fecha Inicio");
headerNames.add("Fecha Fin");
headerNames.add("Interes Dia");
headerNames.add("editar-borrar");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "interes-no-encontrado"));
					
if (_interes != null && !_interes.isEmpty()){
	int total = _interes.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	NumberFormat formatter = new DecimalFormat("#0.00000");     
	
	for (int i = 0; i < _interes.size(); i++) {	    
		//CajaChica liq = (CajaChica) _interes.get(i);
		Interes _int = (Interes) _interes.get(i);
		
	 	ResultRow row = new ResultRow(_int,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		
		/* String fechaSolicitud = "";
		if(Integer.toString(wd.getId()).equals(estadoId)){
			fechaSolicitud=sdf.format(wd.getFecha()) ;
		} */
		
		rowURL.setWindowState(WindowState.MAXIMIZED);
		
		// Format FechaIni
		auxDate = sdfFormat.parse(_int.getFechaInicio());
		auxFechaIni = sdf.format(auxDate);
		row.addText(auxFechaIni);
		// Format FechaFin
		auxDate = sdfFormat.parse(_int.getFechaFin());
		auxFechaFin = sdf.format(auxDate);
		row.addText(auxFechaFin);		
		// Format Interes
		auxInteres = formatter.format(_int.getInteresDia());	
		row.addText(formatter.format(_int.getInteresDia()));
		
		StringBuilder sb = new StringBuilder();
		if(rolAdministradorInteres){
			
			sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
			sb.append("/portlet/edit.png\" onClick=\"javascript:editarInteres('");
			sb.append(auxFechaIni);
			sb.append("', '");
			sb.append(auxFechaFin);
			sb.append("', '");
			sb.append(auxInteres);
			sb.append("');\" />");
			sb.append(" / ");

			sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
			sb.append("/common/delete.png\" onClick=\"javascript:borrarInteres('");	 					
			sb.append(auxFechaIni);
			sb.append("', '");
			sb.append(auxFechaFin);
			sb.append("', '");
			sb.append(auxInteres);	 					
			sb.append("');\" />");
			row.addText(sb.toString());
	       			
		}else{
		   sb = new StringBuilder();
		   sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		   row.addText(sb.toString());
		}			
	  resultRows.add(row);
	}
}
%>
		
<script type="text/javascript">
var interesEnEdicion;
var interesFechaInicio;
 
function editarInteres(fecha_inicio, fecha_fin, interes_dia){
	
    jQuery('#<portlet:namespace />buscandoCC').show();
 	var editarNom = {'<%= Constants.CMD %>':'<%=Constants.EDIT%>',"fecha_inicio":fecha_inicio,"fecha_fin":fecha_fin,"interes_dia":interes_dia, "usuario_modi":'<%=usuario_modi%>'};
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_interes';
 	interesEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Edición de Interes Afip:" />",modal:true,width:1000});
 	jQuery(interesEnEdicion).load(url,editarNom, function(){
														jQuery('#<portlet:namespace />buscandoCC').hide();            															
													  });		 
}


function borrarInteres(fecha_inicio, fecha_fin, interes_dia){
	
    jQuery('#<portlet:namespace />buscandoCC').show();
 	var editarNom = {'<%= Constants.CMD %>':'<%=Constants.PREVIEW%>',"fecha_inicio":fecha_inicio,"fecha_fin":fecha_fin,"interes_dia":interes_dia, "usuario_modi":'<%=usuario_modi%>'};
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_interes';
 	interesEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Eliminar Interes Afip:" />",modal:true,width:1000});
 	jQuery(interesEnEdicion).load(url,editarNom, function(){
														jQuery('#<portlet:namespace />buscandoCC').hide();            															
													  });		 
}

</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>

