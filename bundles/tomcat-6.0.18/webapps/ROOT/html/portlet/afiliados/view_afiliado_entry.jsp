<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%
Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
//String tabsA = ParamUtil.getString(request, "tabs1", "informacion_general");
StringBuilder tabsAValues = new StringBuilder("informacion_general");

//Nuevo bloque
String tabsA = ParamUtil.getString(request, "tabs1", "");
if(tabsA.equals("")){
	tabsA = (String)request.getAttribute("tabs1");
}
if((tabsA == null) || (tabsA != null && tabsA.equals(""))) {
	tabsA = "informacion_general";
}
// fin nuevo bloque

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setParameter("struts_action", "/afiliados/view_afiliado_entry");
portletURL.setParameter("tabs1", tabsA);
portletURL.setParameter("view", "true");

if(null!=afiliado){
	tabsAValues.append(",informacion_adicional");
	tabsAValues.append(",historico_movimientos");
	tabsAValues.append(",historico_contactos");
	tabsAValues.append(",imagenes_afiliados");
	portletURL.setParameter("cuil_titular",afiliado.getCuil_titular());
	portletURL.setParameter("inte",afiliado.getInteAsString());
}
String tabsANames = StringUtil.replace(tabsAValues.toString(), StringPool.UNDERLINE, StringPool.DASH); 

String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
    portlet_name = "afiliados";
}
%>
<!--  
<form action="">
-->
<form action="" method="post" name="<portlet:namespace />fmAI" enctype="multipart/form-data">
<div id="<portlet:namespace />grupoFliar" style="position:fixed; left:53%; width:40%; height:5%; align:right; display:none;">
	<c:choose>	
		<c:when test='<%=afiliado!=null %>'>		
			<jsp:include page="/html/portlet/afiliados/grupo_fliar_search_result.jsp">
				<jsp:param name="view" value="true" />
			</jsp:include> 		
		</c:when>
	</c:choose>
</div>

<div id="<portlet:namespace />grupoFliarShow" style="position:fixed; left:50%; width:40%; height:5%; align:right;">
	<c:choose>	
		<c:when test='<%=afiliado!=null %>'>
			<table align="right">	
				<tr>
					<c:choose>	
						<c:when test='<%=afiliado!=null %>'>
							<td><label><%=afiliado.getApellido()%>,&nbsp;<%=afiliado.getNombre()%>&nbsp;(<%=afiliado.getParentesco()%>)&nbsp;&nbsp;&nbsp;</label>
							</td>
						</c:when>
					</c:choose>	
					<td>	
						<a align="right" href="javascript:showGrupoFliar();">
					 		<liferay-ui:message key='grupo-fliar'/>&nbsp;
						</a>						
					</td>
					<td>
						<img alt="<liferay-ui:message key='mostrar-grupo-fliar'/>" align="right" src="<%=themeDisplay.getPathThemeImages()%>/common/group.png" onClick="javascript:showGrupoFliar();"/>
					</td>
				</tr>
			</table>
		</c:when>
	</c:choose>
</div>

<liferay-ui-custom:tabs		
	names="<%= tabsANames %>"
	tabsValues="<%= tabsAValues.toString() %>"		
	portletURL="<%= portletURL %>"
	value="<%= tabsA%>"
/>

<c:choose>
	<c:when test='<%= tabsA.equals("informacion_general") %>'>
		<liferay-util:include page="/html/portlet/afiliados/view_afiliado.jsp"/>
	</c:when>	
	<c:when test='<%= tabsA.equals("informacion_adicional")%>'>		
		<liferay-util:include page="/html/portlet/afiliados/view_otros_datos.jsp"/>
	</c:when>
	<c:when test='<%= tabsA.equals("historico_movimientos")%>'>
		<liferay-util:include page="/html/portlet/afiliados/historico_movimientos.jsp" />
	</c:when>
	<c:when test='<%= tabsA.equals("historico_contactos")%>'>
		<liferay-util:include page="/html/portlet/crm/historico_contactos.jsp" >			
		</liferay-util:include>	
	</c:when>
	<c:when test='<%= tabsA.equals("imagenes_afiliados")%>'>
	   <liferay-util:include page="/html/portlet/afiliados/afiliado_imagenes.jsp"/>
	</c:when>			
</c:choose>

</form>

<script type="text/javascript">	
	jQuery("#<portlet:namespace />grupoFliar").hide();
	function hideGrupoFliar(){		
		jQuery('#<portlet:namespace/>grupoFliar').hide();
		jQuery("#<portlet:namespace />grupoFliarShow").show();		
	}
	
	function showGrupoFliar(){		
		jQuery("#<portlet:namespace />grupoFliarShow").hide();
		jQuery('#<portlet:namespace/>grupoFliar').show();				
	}

	function editarIntegrante(cuil,inte){		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_afiliado_entry&cuil_titular='+cuil+
		'&inte='+inte;		
		window.location=url;
		
		//jQuery('#userlist').remove();
	}
	var popupda;
	function <portlet:namespace />documentacionAdjunta(cuil, inte) {
		popupda = Liferay.Popup({title:"<liferay-ui:message key="documentacion-adjunta" />",modal:true,width:900});
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/documentacion_adjunta&cuil_titular='+cuil+'&inte='+inte+'&view=true';
		jQuery(popupda).load(url);
	}

	var popupdar;
	function <portlet:namespace />documentacionAdjuntaRecuperar(cuil, inte) {
		popupdar = Liferay.Popup({title:"<liferay-ui:message key="documentacion-adjunta-recuperar" />",modal:true,width:900});
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/documentacion_adjunta_recuperar_afiliado&cuil_titular='+cuil+'&inte='+inte;
		jQuery(popupdar).load(url);
	}	

	function <portlet:namespace />certificadoAfiliacion(cuil, inte) {
		popupdac = Liferay.Popup({title:"<liferay-ui:message key="certificado-afiliacion" />",modal:true,width:300});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/odt_rtf&cuil='+cuil+'&inte='+inte;
		jQuery(popupdac).load(url);		
	}	

	function <portlet:namespace />odtRtf(cuil, inte, tipo) {
		window.location.href ="/odtservlet/?accion=certificadoAfiliacion&cuil="+cuil+"&inte="+inte+"&tipo="+tipo;
	}
	

	
	<c:if test="<%= afiliado != null %>">
		function <portlet:namespace />verImagenes() {
			//popupda = Liferay.Popup({title:"<liferay-ui:message key="documentacion-adjunta" />",modal:true,width:900});
			
			<%String todoAfiliado=afiliado.getCuil_titular()+"*";%>
			var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString()%>">'+
			    			'<liferay-portlet:param name="struts_action" value="/afiliados/buscar_documentacion"/>'+
			    			'<liferay-portlet:param name="keywords" value="<%=todoAfiliado%>"/>'+
		             '</liferay-portlet:renderURL>';      
		    window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 	             
			//jQuery(popupda).load(url);
		}
	
		function <portlet:namespace />verImagenDirecta() {
			//popupda = Liferay.Popup({title:"<liferay-ui:message key="documentacion-adjunta" />",modal:true,width:900});		
			var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
			    			'<liferay-portlet:param name="struts_action" value="/afiliados/documentacion_adjunta_recuperar"/>'+
			    			'<liferay-portlet:param name="name" value="<%=afiliado.getTitle()%>"/>'+
			    			'<liferay-portlet:param name="folderId" value="<%=String.valueOf(afiliado.getFolderid())%>"/>'+
		             '</liferay-portlet:actionURL>';      
		    window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 	             
			//jQuery(popupda).load(url);
		}
	</c:if>
</script>
<script type="text/javascript">

function <portlet:namespace />uploadImagenAfiliado() {	
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_afiliado';
	document.<portlet:namespace />fmAI.method = 'post';
	url = url+'&imagen'+'='+'<%=Constants.ADD%>'+'&nrsolicitud=<%=afiliado.getCuil() %>';
	submitForm(document.<portlet:namespace />fmAI, url);
}

function verImagenAfiliado(folderId,fileName){
   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
   '<liferay-portlet:param name="struts_action" value="/afiliados/documentacion_adjunta_recuperar"/>'+
   '<liferay-portlet:param name="name" value="__Name"/>'+
   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
   '</liferay-portlet:actionURL>';      
   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
   window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}

function deleteImagenAfiliado(folderId,fileName) {
	var confirmar=false;
	confirmar = confirm ('Está seguro de eliminar este documento');
	if(confirmar){
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_afiliado';						
		document.<portlet:namespace />fmAI.method = 'post';
        url = url+'&imagen'+'='+'<%=Constants.DELETE %>';
		url += "&folderid="+folderId;
		url += "&filename="+fileName;
		submitForm(document.<portlet:namespace />fmAI, url);
	}else{
		return false;
	}	
}

</script>

