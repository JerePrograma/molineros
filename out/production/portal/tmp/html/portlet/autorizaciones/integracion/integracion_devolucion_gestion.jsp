<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
	response.setHeader("Cache-Control", "no-store"); //HTTP 1.1
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	response.setDateHeader("Expires", 0); //prevents caching at the proxy server
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/autorizaciones/view");
	Integer periodo= ParamUtil.getInteger(request, "periodo");
	String portlet_name=null;

	portlet_name="autorizaciones";
%>

<form action="<%=portletURL%>" method="post"
	name="<portlet:namespace />fmIntDev" enctype="multipart/form-data"
	onSubmit="submitForm(this); return false;">
	
<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />	
	
	<liferay-portlet:renderURLParams varImpl="portletURL" />
	<fieldset class="block-labels">
	<legend>Gestión Devolución Periodo <%=periodo %> 	</legend>
	 <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
	  <tr>
	  <td width="65%">
      <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		<tr>
			<td><label>Clave:</label></td>
			<td><input id="<portlet:namespace />claveFiltro" name="<portlet:namespace />claveFiltro" size="11" maxlength="11" type="text" /></td>
			<td>CUIT:</td>
			<td><input id="<portlet:namespace />cuitFiltro" name="<portlet:namespace />cuitFiltro" size="11" maxlength="11" type="text"/></td>
			<td>Prestación:</td>
			<td><input id="<portlet:namespace />prestacionFiltro" name="<portlet:namespace />prestacionFiltro" size="3" maxlength="3" type="text"/></td>
			<td>CUIL:</td>
			<td><input id="<portlet:namespace />cuilFiltro" name="<portlet:namespace />cuilFiltro" size="11" maxlength="11" type="text"/></td>
		</tr>
	    <tr>
		  <td>
			<label>Con Problemas:</label>
		  </td>
		  <td>	
			<input type="checkbox"
			 id="<portlet:namespace />conErrorFiltro"
			 name="<portlet:namespace />conErrorFiltro" value="false">
		 </td>
	    </tr>
      </table>
     </td>
     <td >
        <fieldset class="block-labels">
	       <legend>Devolución OK</legend>
           <input type="file" name="archivoDevolucionOK"/>
           <input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivoRendicion()"/>
        </fieldset>   
        <fieldset class="block-labels">
	       <legend>Devolución ERROR</legend>
           <input type="file" name="archivoDevolucionError"/>
           <input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivoRendicion()"/>
        </fieldset>
     </td> 
     </tr> 
     </table> 
</fieldset>
<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="volver">
		<portlet:param name="struts_action" value="/autorizaciones/integracion_rendicion" />
</portlet:renderURL>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
	<tr>
		<td colspan="4" align="left"><input id="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar"/>"
			title="<liferay-ui:message key="buscar-reclamo-prestacion" />" type="button" />
		</td>	
	
		<td  colspan="4" align="center">
		<input id="<portlet:namespace />exportar-busqueda" value="Archivo SSS" 
				title="Generar Archivo" type="button" onclick="exportarDevolucionFTP()"  />
		</td>
		
		<td>
		<p><a href="<%= volver %>">Volver</a></p>
		</td>
		
	</tr>

</table>




<fieldset class="block-labels">
<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
<div align="center" id="<portlet:namespace />gestionDevolucionDiv">
   	<jsp:include page='/html/portlet/autorizaciones/integracion/integracion_devolucion_gestion_search_result.jsp' />  	
</div>
</fieldset>
</form>


<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />


<script type="text/javascript">
    var popupINT;
	jQuery('#<portlet:namespace />buscando').hide();	
	
	jQuery('#<portlet:namespace />buscar').click(function(){
		<portlet:namespace />busquedaIntegracionDevolucion(0);
	});
	
	
	function <portlet:namespace />busquedaIntegracionDevolucion(esBusqueda){
	    var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();	
	    var clave=jQuery("#<portlet:namespace/>claveFiltro").val();
	    var cuit=jQuery("#<portlet:namespace/>cuitFiltro").val();
	    var prestacion=jQuery("#<portlet:namespace/>prestacionFiltro").val();
	    var conError=sinReintento=jQuery("#<portlet:namespace/>conErrorFiltro").is(':checked');
	    var cuil=jQuery("#<portlet:namespace/>cuilFiltro").val();
//		jQuery("#pagina").val(pagina_sel);

        if(esBusqueda==0){
        	pagina_sel=0;
        } 
		jQuery('#<portlet:namespace />buscando').show();
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar&cmd=editar_devolucion_periodo_pagina';
		url += '&cuit='+cuit+'&prestacion='+prestacion;
		url += '&cuil='+cuil;
        url += '&clave='+clave+'&conError='+conError+'&pagina_sel='+pagina_sel+'&periodo=<%=periodo%>';	
        jQuery('#<portlet:namespace />gestionDevolucionDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	}
	
	function <portlet:namespace />initDateFields(){}
	
	<portlet:namespace />initDateFields();
	
	
	
    function editarRegistroDR(id) {
    	
    	var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();	
 	    var clave=jQuery("#<portlet:namespace/>claveFiltro").val();
 	    var cuit=jQuery("#<portlet:namespace/>cuitFiltro").val();
 	    var prestacion=jQuery("#<portlet:namespace/>prestacionFiltro").val();
 	    var conError=sinReintento=jQuery("#<portlet:namespace/>conErrorFiltro").is(':checked');
 	    var cuil=jQuery("#<portlet:namespace/>cuilFiltro").val();
		popupINT = Liferay.Popup({title:"Edición Detalle",modal:true,width:1040, position:['center',30],onClose: function() {
			var urlReload = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar&cmd=editar_devolucion_periodo_pagina';
			urlReload += '&cuit='+cuit+'&prestacion='+prestacion;
			urlReload += '&cuil='+cuil;
	        urlReload += '&clave='+clave+'&conError='+conError+'&pagina_sel='+pagina_sel+'&periodo=<%=periodo%>';
	        jQuery('#<portlet:namespace />gestionDevolucionDiv').load(urlReload, function() {        															
			  }
            );
         }});
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
	   	url = url+'&<%= Constants.CMD %>'+'='+'editar_registro_dr'+'&nroRegistro='+id;
	   	url += '&cuit='+cuit+'&prestacion='+prestacion;
	   	url += '&cuil='+cuil;
        url += '&clave='+clave+'&conError='+conError+'&pagina_sel='+pagina_sel+'&periodo=<%=periodo%>';	
              
	   	jQuery(popupINT).load(url,function() {} 	);
      
	}
	
    function <portlet:namespace />uploadArchivoRendicion() {
    	
    	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/integracion_upload_archivos_rendicion';
    	url+= '&periodo=<%=periodo%>'+'&origen=GES';
    	document.<portlet:namespace />fmIntDev.method = 'post';
    	submitForm(document.<portlet:namespace />fmIntDev, url);
    }
    
    function exportarDevolucionFTP(){
    	window.location.href ="/txtservlet/?reporte=INTEGRACION_EXPORTAR_RENDICION&periodo=<%=periodo%>";
	}
	
		
	function soloNumeros(e) 
	{ 
	var key = window.Event ? e.which : e.keyCode 
	return ((key >= 48 && key <= 57) || (key==8)) 
	}
	
</script>