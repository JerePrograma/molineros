<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
		String portlet_name = ParamUtil.getString(request, "portlet_name");
	
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "tesoreria";
		}
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
		}
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		}  
		
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaPago = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
		

%>
<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
		<fieldset class="block-labels">
				<legend>Panel de Control INGRESOS-EGRESOS</legend>
				<table class="lfr-table">
				  <tr>
				   <td>
				     <table class="lfr-table">
					  <tr>
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaDesdeDia1"
							dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
							monthParam="fechaDesdeMes1"
							monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
							yearParam="fechaInicioAnio1"
							yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 50 %>"
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
				   
					     <td>
						   	&nbsp;
					     </td>
							
					  <td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaHastaDia2"
							dayValue="<%= fechaPago.get(Calendar.DATE) %>" 
							monthParam="fechaHastaMes2"
							monthValue="<%= fechaPago.get(Calendar.MONTH) %>"				
							yearParam="fechaHastaAnio2"
							yearValue="<%= fechaPago.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -50 %>"	
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaPago.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
						<td>
							&nbsp;
						</td>
					     <td>
						   	&nbsp;
					     </td>
						  <td>
							     <input id="<portlet:namespace />reporte" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
							    
						  </td>
					  </tr>
				  </table>
				 </td>
				 <td>
				     <td>
				     		   <fieldset class="block-labels">
				          <legend>Resultados</legend>
				     
				            <table class="lfr-table">
						      <tr>
						         <td><label>Ingresos:</label></td>
						         <td>
					               <input id="<portlet:namespace />ingresos" value="                                 " 
					                  title="<liferay-ui:message key="ingresos" />" type="button"
					                  style="background: #FFFFE0"
					                  onclick="<portlet:namespace />explosion('I')"/>
					             </td> 
						       </tr>
						       <tr>
					           <td>
						     	   &nbsp;
					           </td>
					          </tr> 
						      <tr>
						        <td><label>Egresos:</label></td>
						        <td>
					              <input id="<portlet:namespace />egresos" value="                                 " 
					              title="<liferay-ui:message key="egresos" />" type="button"
					              style="background: #FFFFE0"
					              onclick="<portlet:namespace />explosion('E')"
					              />
					            </td> 
						      </tr>
						   
						      <tr>
					            <td>
						     	   &nbsp;
					            </td>
					          </tr> 
						      <tr>
						        <td><label>Declarado:</label></td>
						        <td>
					              <input id="<portlet:namespace />declarado" value="EMPRESAS" 
					              title="<liferay-ui:message key="declarado" />" type="button" 
					              style="background: #FFFFE0"
					              onclick="<portlet:namespace />explosionEmpresas()"
					              />
					            </td> 
						      </tr>
						      
						      <tr>
					            <td>
						     	   &nbsp;
					            </td>
					          </tr>
						      
						      <tr>
						        <td colspan="2">
						          <label style="color: blue">Click sobre botón para ver Detalle</label>
						        </td>
						      </tr>
						     
						    </table>
						</fieldset>
		      
					 </td>
				 </td> 
				</tr> 
			  </table>	  	      	  
		</fieldset>	
		
		
		<fieldset class="block-labels">
			<div align="center" id="<portlet:namespace />buscando_ie">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
		</fieldset>
		
		<table>
		  <tr>
		     <td valign="top">
		        <div align="center" id="<portlet:namespace />controlIngresosEgresosPRA" style="border: 1px solid" > </div>
		     </td>
		     
		     <td valign="top">
		        <div align="center" id="<portlet:namespace />controlIngresosEgresosSDA" style="border: 1px solid"> </div>
		     </td>
		  
		  </tr>
		</table>
</form>			
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando_ie').hide();	

var popupMD;
var variable; 

jQuery('#<portlet:namespace />reporte').click(function(){
	jQuery('#<portlet:namespace />buscando_ie').show(); 
	
	var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
	var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
	var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

	var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
	var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
	var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");

			
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/verificar_equivalencias_completas';
		url +='&fechaDesdeDia='+fechaDesdeDia.value;
		url +='&fechaDesdeMes='+fechaDesdeMes.value;
		url +='&fechaDesdeAnio='+fechaDesdeAnio.value;
		url +='&fechaHastaDia='+fechaHastaDia.value;
		url +='&fechaHastaMes='+fechaHastaMes.value;
		url +='&fechaHastaAnio='+fechaHastaAnio.value;			
		url +='&entidad=1';			
		url += '&rnd=' + Math.floor(Math.random()*100);			
		
	jQuery.ajax({   
		url: url,
		success: function(data){
			jQuery('#<portlet:namespace />buscando_ie').hide();
			
			var obj = jQuery.parseJSON(data);
			if (obj.status == "equivalencias_conceptos_incompleto"){
				alert("Las equivalencias conceptos-cuentas se encuentran incompletas, por favor complete las mismas y vuelva a intentarlo.");
				return;
			}
			if (obj.status == "equivalencias_prestaciones_incompleto"){
				alert("Las equivalencias prestaciones-conceptos se encuentran incompletas, por favor complete las mismas y vuelva a intentarlo.");
				return;
			}
			if (obj.status == "falla_inesperada"){
				alert("Falla inesperada. Contacte a sistemas");
				return;
			}
			if (obj.status == "ok"){	
				jQuery('#<portlet:namespace />buscando_ie').show();
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_control_ingresos_egresos'
					+'&fechaDesdeDia='+fechaDesdeDia.value
					+'&fechaDesdeMes='+fechaDesdeMes.value
					+'&fechaDesdeAnio='+fechaDesdeAnio.value
					+'&fechaHastaDia='+fechaHastaDia.value
					+'&fechaHastaMes='+fechaHastaMes.value
					+'&fechaHastaAnio='+fechaHastaAnio.value
					+'&entidad=1';
				
				jQuery.ajax({   
					url: url,
					success: function(data){
						jQuery('#<portlet:namespace />buscando_ie').hide();
						var obj = jQuery.parseJSON(data);
						var ingresos=obj.ingresos;
						var egresos=obj.egresos;
						var declarado=obj.declarado;
						jQuery('#<portlet:namespace />ingresos').val(formatNumber.format(ingresos));
						jQuery('#<portlet:namespace />egresos').val(formatNumber.format(egresos));
					}
				});		
			}
			
		}
	});
	
});





function <portlet:namespace />explosion(tipo){
	
	jQuery('#<portlet:namespace />buscando_ie').show();
	jQuery('#<portlet:namespace />controlIngresosEgresosSDA').hide();
	
	var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
	var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
	var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

 	var busquedaNom = {"tipo":tipo,"cmd":"nivel_1"};
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/uoma/controlIngresosEgresosExplosion" /></portlet:renderURL>';
 	url += '&fechaDesdeDia='+fechaDesdeDia.value
	       +'&fechaDesdeMes='+fechaDesdeMes.value
	       +'&fechaDesdeAnio='+fechaDesdeAnio.value;
 	
	jQuery('#<portlet:namespace />controlIngresosEgresosPRA').load(url,busquedaNom, function(){
														jQuery('#<portlet:namespace />buscando_ie').hide();
														
	});	
	
}


function <portlet:namespace />explosionEmpresas(){
	
	   	if(popupMD==null)
    	    popupMD = Liferay.Popup({title:"Empresas",modal:true,width:900,position:[0,5],onClose: function() { popupMD = null;}});
   	
    	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/uoma/controlIngresosEgresosExplosionEmpresas';
		jQuery(popupMD).load(url);
       

}

var formatNumber = {
		 separador: ".", 
		 sepDecimal: ',',
		 formatear:function (num){
		  num +='';
		  var splitStr = num.split('.');
		  var splitLeft = splitStr[0];
		  var splitRight = splitStr.length > 1 ? this.sepDecimal + splitStr[1] : '';
		  var regx = /(\d+)(\d{3})/;
		  while (regx.test(splitLeft)) {
		  splitLeft = splitLeft.replace(regx, '$1' + this.separador + '$2');
		  }
		  return this.simbol + splitLeft  +splitRight;
		 },
		 format:function(num, simbol){
		  this.simbol = simbol ||'';
		  return this.formatear(num);
		 }
}

	
</script>
