<%@include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/hoteles/view");
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "hoteles";
	}
	
	String ptoVtaAfip="00030";

	try{
		ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 		
	}catch(Exception e){
		//ptoVtaAfip="0000";
		ptoVtaAfip="00030";
	}
	
	List<ProductoCategoria> categorias =  HotelesServiceUtil.getProductosCategorias(ptoVtaAfip);

	session.setAttribute(WebKeysHoteles.CATEGORIAS_RESULT,categorias);
	
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data">
    <input name="<portlet:namespace />id_hotel" type="hidden"	value="<%=ptoVtaAfip%>" /> 
	<fieldset class="block-labels">
		<legend>ABM Categorias</legend>
		
		<input type="button" value="Nuevo" onClick="<portlet:namespace />nuevaCategoria();"/>&nbsp;
		
		

	  
		<div id="<portlet:namespace />div_categorias">
			<jsp:include page='/html/portlet/hoteles/categorias_productos_result.jsp' />  	
		</div>
		
	</fieldset>
</form>		

<script type="text/javascript">
		
	var popupMD;
	
	<portlet:namespace />initDateFields();
	
	function <portlet:namespace />initDateFields(){
	}
	
	function <portlet:namespace />nuevaCategoria() {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_categorias_abm" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
		
	
</script>

