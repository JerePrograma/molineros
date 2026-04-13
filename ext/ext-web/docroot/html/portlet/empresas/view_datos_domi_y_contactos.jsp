<%@ include file="/html/portlet/empresas/init.jsp"%>
<%
Empresa empresa = (Empresa)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);

boolean esEdicion = true;
//String idOp=(String)renderRequest.getAttribute("idOp");
String prefijo="empre_";
String vista = "H";
String tamanio="40";
%>
<div id="<portlet:namespace/>ocultarDatosDomiyContac">
<fieldset class="block-labels">
<legend>
<liferay-ui:message	key="datos-domi-contact" /> 
</legend>

<table class="lfr-table" style="border-collapse: separate; border-spacing: 2px;">	
	<tr>			
		<td><label><liferay-ui:message key="domicilios" />:</label></td>
		<td>
			<%=empresa!=null?empresa.getDomicilioAsString():""%>
		</td>
	</tr>
	<tr>
		<td colspan="2">			
			<a href="javascript:<portlet:namespace />showHideDivDomicilios();">
			<legend>
				<liferay-ui:message	key="Editar domicilios" /> (<%=null!=empresa&&null!=empresa.getDomicilios()?empresa.getDomicilios().size():""%>)
				<img name="arrow_domicilios" id="<portlet:namespace />arrow_domicilios" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
			</legend>
			</a>
		</td>	
	</tr>	
	<tr>			
		<td><label><liferay-ui:message key="contactos" />:</label></td>
		<td><%=empresa!=null?empresa.getContactosEConcatSinPersonas("PERSONAL").trim():""%><br/>
			<%=empresa!=null?empresa.getContactosEConcatSinPersonas("TELEFONO").trim():"" %><br/>
			<%=empresa!=null?empresa.getContactosEConcatSinPersonas("EMAIL").trim():""%><br/>
		</td>	
	</tr>
	<tr>
		<td colspan="2">
		<a href="javascript:<portlet:namespace />showHideDivContactos();">
						<legend>
							<liferay-ui:message	key="address-book" /> (<%=null!=empresa&&null!=empresa.getContactos()?empresa.getContactos() .size()-empresa.getContactosPorNombreApePersonas().size():""%>)
							<img name="arrow_contactos" id="<portlet:namespace />arrow_contactos" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
						</legend>
		</a>
	</td>
	</tr>
</table>
</fieldset>
<fieldset class="block-labels">
<legend>
	<liferay-ui:message	key="datos-empre-contact" /> 
</legend>

<table class="lfr-table" style="border-collapse: separate; border-spacing: 2px;">
	<tr>			
		<td><label><liferay-ui:message key="contactos" /> Personalizados:</label></td>
		<td>
			<%=empresa!=null&&empresa.getContactosPorNombreApeConcatenados()!=null&&
					empresa.getContactosPorNombreApeConcatenados().length()>0?empresa.getContactosPorNombreApeConcatenados():"No se han cargado contactos personalizados"%>
		</td>	
	</tr>
	<tr>
		<td colspan="2">
		    <a href="javascript:<portlet:namespace />showHideDivContactosPers1();">
							<legend>
								<liferay-ui:message	key="address-book" /> (<%=null!=empresa&&null!=empresa.getContactos()?empresa.getContactosPorNombreApePersonas().size():""%>)
								<img name="arrow_contactos_pers" id="<portlet:namespace />arrow_contactos_pers" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
							</legend>
			</a>
		</td>
	</tr>
</table>
</fieldset>
</div>
<script type="text/javascript">	
<%if(portlet_name.equals("estudio_isidro")){%>	
jQuery('#<portlet:namespace />ocultarDatosDomiyContac').css('display','none')		
<%}%>
/*Ojo que esta en la jsp view_datos_encuadramiento*/
function <portlet:namespace />showHideDivDomicilios(){		
	if (jQuery("#<portlet:namespace />ocultarDomicilios").css('display') === 'none') {
		jQuery('#<portlet:namespace />ocultarDomicilios').css('display','block')
		jQuery('#<portlet:namespace />arrow_domicilios').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />ocultarDomicilios').css('display','none')
		jQuery('#<portlet:namespace />arrow_domicilios').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
	}
}
/*Ojo que esta en la jsp view_datos_encuadramiento*/
function <portlet:namespace />showHideDivContactos(){		
	if (jQuery("#<portlet:namespace />ocultarContactos").css('display') === 'none') {
		jQuery('#<portlet:namespace />ocultarContactos').css('display','block')
		jQuery('#<portlet:namespace />arrow_contactos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />ocultarContactos').css('display','none')
		jQuery('#<portlet:namespace />arrow_contactos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
	}
}

         
function <portlet:namespace />showHideDivContactosPers1(){		
if (jQuery("#<portlet:namespace />ocultarContactosPersonalizados").css('display') === 'none') {
	jQuery('#<portlet:namespace />ocultarContactosPersonalizados').css('display','block')
	jQuery('#<portlet:namespace />arrow_contactos_pers').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
}else{
	jQuery('#<portlet:namespace />ocultarContactosPersonalizados').css('display','none')
	jQuery('#<portlet:namespace />arrow_contactos_pers').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
}

}

</script>

