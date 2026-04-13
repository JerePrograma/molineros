<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="ar.com.ospim.global.beans.Seccional" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.global.beans.Comprobante"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	NumberFormat formatter = new DecimalFormat("#0.00");  
	
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	
	Integer entidad = WebKeysGlobal.OSPIM;
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		entidad = WebKeysGlobal.UOMA;
		portlet_name = "uoma";
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	CajaChica cajaChica=(CajaChica)request.getSession().getAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION);
	
	int id_caja_chica=cajaChica!=null && cajaChica.getId() !=null ?(int)cajaChica.getId():0;
	
	if(cajaChica==null){
		cajaChica= new CajaChica();
	} 
	
	
	Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
	fechaDesde.setTime(new Date());
	
	 fechaDesde.add(Calendar.DAY_OF_YEAR, -30);
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	List<Seccional>seccionales = TraeListasServiceUtil.getSeccionales();
	%>

<form action="" method="post" name="<portlet:namespace />fmCJCHEJ">

	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<fieldset class="block-labels">
		<legend>Caja Chica</legend>

		<table class="lfr-table">
			<tr>
			
			   <td><label><liferay-ui:message key="caja-chica-nombre" />:</label></td>
				<td><input id="<portlet:namespace />descripcionCajaChica"
					name="<portlet:namespace />descripcionCajaChica" size="70"
					maxlength="70" type="text"
					value='<%=cajaChica.getDescripcion()==null?"":cajaChica.getDescripcion() %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/></td>
			   <td>
					<liferay-ui:message key="Estado" />
				</td>
				<td>
				   <input id="<portlet:namespace />estadoCajaChica"
					name="<portlet:namespace />estadoCajaChica" size="40"
					maxlength="40" type="text" 
					value='<%=cajaChica.getEstado().getDescripcion()==null ?"":cajaChica.getEstado().getDescripcion() %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/>
				</td>	
					
				<td>
				   <input id="<portlet:namespace />estadoFechaCajaChica"
					name="<portlet:namespace />estadoFechaCajaChica" size="20"
					maxlength="20" type="text"
					value='<%=cajaChica.getEstado().getFecha() ==null?"":sdf.format(cajaChica.getEstado().getFecha()) %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/>
				</td>
				<td>
					<liferay-ui:message key="Saldo" />
				</td>
				<td>
				
				   <input id="<portlet:namespace />saldoCajaChica"  style="background-color: #72A4D2;"
					name="<portlet:namespace />saldoCajaChica" size="20" 
					maxlength="20" type="text"
					value='<%=formatter.format(cajaChica.getSaldo())%>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/>
					
				</td>		
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>	
	</fieldset>
	<br>
	<fieldset class="block-labels">
		<legend>Parámetros de Selección</legend>
		
		<table class="lfr-table">
		    <tr>   
		      <td>
		        <liferay-ui:message key="seccional"/>
			  </td>
			  <td>
			    <select name="<portlet:namespace/>seccionalCajaChica" id="<portlet:namespace/>seccionalCajaChica" 
			       onchange="<portlet:namespace/>seccionalVerificaImpresion()" >
					<option value="0">Seleccione una seccional</option>
					<%	for (Seccional tnom : seccionales) { %>
							<option value="<%= tnom.getId()  %>"><%=tnom.getDescripcion() %></option>
					<%	} %>
				</select>
			  </td>	
			  
			  <td>
			     <div id="<portlet:namespace />divReciboCajaChica" hidden="hidden">
			      <span style="font-size: 13pt; color: red; "><label >Impreso</label></span>
			     </div> 
			  </td>
			</tr>
			   
			<tr>
				<td>&nbsp;</td>
			</tr>
			
			<tr>
			
			    <td>
					<liferay-ui:message key="nombre" />
				</td>
				<td>
				   <input id="<portlet:namespace />nombreReciboCajaChica"
					name="<portlet:namespace />nombreReciboCajaChica" size="50"
					maxlength="200" type="text"/>
				</td>
			</tr>
			
			
			<tr>
				<td>&nbsp;</td>
			</tr>
			
			<tr>
				  <td><input id="<portlet:namespace />reporteCajaChica"
		              value="<liferay-ui:message key="recibo"/>"
		              title="<liferay-ui:message key="recibo" />"
		              onClick="javascript: <portlet:namespace />emitirReciboCajaChica();"
		              type="button"/>
		          </td>
			</tr>
			</table>
					
	</fieldset>
	
	<input type="hidden" name="<portlet:namespace />id_caja_chica"
		id="<portlet:namespace />id_caja_chica" value="<%=id_caja_chica%>" />
    <input type="hidden" value="" name="view" id="view" /> 

    
   
</form>



<script type="text/javascript">

var popupCJ;
var auxiliar;

function <portlet:namespace />emitirReciboCajaChica(){
	var idCajaChica=jQuery('#<portlet:namespace />id_caja_chica').val();
	var idSeccional=jQuery('#<portlet:namespace/>seccionalCajaChica').val();
	var desSeccional=jQuery('#<portlet:namespace/>seccionalCajaChica option:selected').text();
	var nombre=jQuery('#<portlet:namespace/>nombreReciboCajaChica').val();
	
	if(idSeccional==0){
		alert('Debe seleccionar una Seccional');
	}else{
		var params = "&<%= Constants.CMD %>=" + "marcacomprobante";
		    params += "&entidad=<%=entidad%>";
		    params += '&id_caja_chica='+idCajaChica;
		    params += '&id_seccional='+idSeccional;	 
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
		url = url + params;
	    jQuery.ajax({   
		url: url,
		success: function(data){
			window.location.href ='/pdfservlet/?accion=<%="recibocajachica"%>+&entidad=<%=entidad%>'
		          +'&id_caja_chica='+idCajaChica
		          +'&id_seccional='+idSeccional
		          +'&des_seccional='+ encodeURI(desSeccional)
		          +'&nombre='+encodeURI(nombre);	                                                                                                                                                                                                                                                            
		 }
	    });		
	}
}


function <portlet:namespace />seccionalVerificaImpresion(){
	var idCajaChica=jQuery('#<portlet:namespace />id_caja_chica').val();
	var idSeccional=jQuery('#<portlet:namespace/>seccionalCajaChica').val();
	
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/verifica_impresion_recibo_caja_chica';
    url += '&id_caja_chica='+idCajaChica;
    url += '&id_seccional='+idSeccional;	 
	url += "&entidad=<%=entidad%>";
    
		jQuery.ajax({   
				url: url,
				async: false,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					var resultado =obj.resultado;
		            if((resultado>0)){
		            	jQuery('#<portlet:namespace />divReciboCajaChica').show();
		            }else{
		                jQuery('#<portlet:namespace />divReciboCajaChica').hide();
		            }
				}				                                                                                                                                                                                                                                                            
				
		});
	
}


</script>

