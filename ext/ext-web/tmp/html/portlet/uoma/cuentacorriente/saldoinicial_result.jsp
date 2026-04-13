<%@ include file="/html/portlet/uoma/init.jsp"%>

<%@ page import="ar.com.uoma.beans.SaldoInicial" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysInteres" %>

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


String portlet_name="uoma";

boolean rolAdministradorInteres = true; //PermissionUtil.userContainsRole(user,WebKeysInteres.ROL_ADMINISTRADOR_INTERES);

//List<CajaChica> archivos=(List<CajaChica>)session.getAttribute("ListaCajasChicas");
List<SaldoInicial> _reg=(List<SaldoInicial>)session.getAttribute("ListaSaldoInicial");

List<String> headerNames = new ArrayList<String>();
headerNames.add("Id");
headerNames.add("Cuit");
headerNames.add("Suc");
headerNames.add("Razon Social");
headerNames.add("Periodo");
headerNames.add("Cuenta");
headerNames.add("Monto");

headerNames.add("editar-borrar");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "saldoinicial-no-encontrado"));
					
if (_reg != null && !_reg.isEmpty()){
	int total = _reg.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	NumberFormat formatter = new DecimalFormat("#0.00");     
	
	for (int i = 0; i < _reg.size(); i++) {	    
		//CajaChica liq = (CajaChica) _interes.get(i);
		SaldoInicial _sld = (SaldoInicial) _reg.get(i);
		
		int _id = _sld.getId();
		String _periodo = _sld.getPeriodo_yyyymm();
		Double _monto = _sld.getMonto();
				
	 	ResultRow row = new ResultRow(_sld,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		
		/* String fechaSolicitud = "";
		if(Integer.toString(wd.getId()).equals(estadoId)){
			fechaSolicitud=sdf.format(wd.getFecha()) ;
		} */
		
		rowURL.setWindowState(WindowState.MAXIMIZED);
		
		row.addText(_sld.getId().toString());
		row.addText(_sld.getCuit());
		row.addText(_sld.getSucursal());
		row.addText(_sld.getRazSoc());
		row.addText(_sld.getPeriodo_yyyymm());
		row.addText(_sld.getCuentaNombre());		
		//row.addText(formatter.format(_sld.getMonto()));
		row.addText(String.format("%,.2f", _sld.getMonto()));
		
		
		StringBuilder sb = new StringBuilder();
		if(rolAdministradorInteres){
			
			sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
			sb.append("/portlet/edit.png\" onClick=\"javascript:editarSaldoInicial(");
			sb.append(_id);
			sb.append(", '");
			sb.append(_periodo);
			sb.append("', '");
			sb.append(_monto);			
			sb.append("',);\" />");
			sb.append(" / ");

			sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
			sb.append("/common/delete.png\" onClick=\"javascript:borrarSaldoInicial(");	 					
			sb.append(_id);
			sb.append(", '");
			sb.append(_periodo);
			sb.append("', '");
			sb.append(_monto);			
			sb.append("',);\" />");
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
 
function editarSaldoInicial(id, periodo, monto){
	
    jQuery('#<portlet:namespace />buscandoCC').show();
 	var editarNom = {'<%= Constants.CMD %>':'<%=Constants.EDIT%>',"campo_id":id,"periodo":periodo,"monto":monto, "usuario_modi":'<%=usuario_modi%>'}; 
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_saldoinicial';
 	interesEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Edición de Saldo Inicial:" />",modal:true,width:1000});
 	jQuery(interesEnEdicion).load(url,editarNom, function(){
														jQuery('#<portlet:namespace />buscandoCC').hide();            															
													  });		 
}


function borrarSaldoInicial(id, periodo, monto){
		
    jQuery('#<portlet:namespace />buscandoCC').show();
 	var editarNom = {'<%= Constants.CMD %>':'<%=Constants.PREVIEW%>',"campo_id":id,"periodo":periodo,"monto":monto, "usuario_modi":'<%=usuario_modi%>'};
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_saldoinicial';
 	interesEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Eliminar Saldo Inicial:" />",modal:true,width:1000});
 	jQuery(interesEnEdicion).load(url,editarNom, function(){
														jQuery('#<portlet:namespace />buscandoCC').hide();            															
													  });		 
}

</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>

