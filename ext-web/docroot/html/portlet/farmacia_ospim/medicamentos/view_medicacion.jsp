<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%@ include file="/html/portlet/farmacia_ospim/medicamentos/init.jsp"%>
<%

String nroSitMedica = "";
boolean esEdicion = false;
boolean inHabilitar = false;
String cmd = (String) request.getAttribute(Constants.CMD);

Calendar fechaDia  =Calendar.getInstance(); 		
fechaDia.setTime(new Date());

Date fechaReg = null;

Calendar fecha = CalendarFactoryUtil.getCalendar();
fecha.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
Calendar fechaPeriodo = CalendarFactoryUtil.getCalendar();
fechaPeriodo.setTime(DateUtils.getLastDateOfYear(new Date(), true));

Medicamento    medicacion  = (Medicamento)request.getSession().getAttribute(WebKeysFarmaciaOspim.MEDICACION_EN_EDICION );

if (cmd != null && cmd.equalsIgnoreCase(Constants.VIEW)){
	inHabilitar= true;
}
if (cmd != null && cmd.equalsIgnoreCase(Constants.EDIT)){
    esEdicion = true;	
}

if(medicacion != null  ){
	nroSitMedica ="Nro Registro : " + "000"+  String.valueOf(medicacion.getId_medicamento() );
}
boolean fechaOk=false;
boolean fechaPeriodoOk=false;
fechaReg = Validator.isNotNull(medicacion)? medicacion.getFecha()    : null;
if (fechaReg != null) {
	fecha.setTime(medicacion.getFecha()  );
	fechaOk=true ;
}
fechaReg = Validator.isNotNull(medicacion)? medicacion.getPeriodo()     : null;
if (fechaReg != null) {
	fechaPeriodo.setTime(medicacion.getPeriodo());
	fechaPeriodoOk=true;
}

//NUEVO
//Para que vuelva a la vista anterior con el ancho de pantalla MAXIMIZED
PortletURL backURL = renderResponse.createRenderURL();
backURL.setWindowState(LiferayWindowState.MAXIMIZED);
backURL.setParameter("struts_action", "/farmaciaospim/view");
backURL.setParameter("tabs1", "medicacion-ospim");

themeDisplay.getPortletDisplay().setShowBackIcon(true);
themeDisplay.getPortletDisplay().setURLBack(backURL.toString());		
%>
<style>

div.divHeaderNro {
  position: absolute;
  top: 210px;
  right:30;
  left:1000px;
  background-color: #cccccc;
  width:200px;
  height:20px;
  border:1px solid black;
  font-size:145%
}


</style>


<form action="EditarPrestadoresEntryAction" name="<portlet:namespace />sitmedica_fm" id="<portlet:namespace />sitmedica_fm" >
    
 	<input  type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" />
 	<input  type="hidden" id="<portlet:namespace />idMedicamento"	name="<portlet:namespace />idMedicamento" size="8"  value="<%=Validator.isNotNull(medicacion)  ? medicacion.getId_medicamento()   : "0"  %>" />
    <input  type="hidden" id="<portlet:namespace />okTroquelRegistro"	name="<portlet:namespace />okTroquelRegistro" size="8"  value =''  />
    <input  type="hidden" id="<portlet:namespace />okNroRegistro"	name="<portlet:namespace />okTroquelRegistro" size="8"  value =''  />
		
<fieldset class="block-labels">
	<legend>		
		<liferay-ui:message key="reg-medicamento" /> 
	</legend>
	
	<div class="divHeaderNro">		     
		  <label align='center' ><b> <%=nroSitMedica%> </b>  </label>   
    </div>
		
		
<table align ='left'  class="lfr-table">
    <tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
	<td >&nbsp;</td>
	</tr>
		<tr>
		
		<td ><label><liferay-ui:message key="Fecha" />:</label> </td>
		
			<% if (fechaOk) {%>
			<td><liferay-ui:input-date dayParam="fechaDia"
			dayValue="<%= fecha.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaMes"
			monthValue="<%= fecha.get(Calendar.MONTH)%>"
			monthNullable="<%= true %>" yearParam="fechaAnio"
			yearValue="<%= fecha.get(Calendar.YEAR)%>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDia.get(Calendar.YEAR)  -1%>"
			yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
			disabled="<%= inHabilitar %>" /> </td>
			<%}else{ %>
			<td> <liferay-ui:input-date dayParam="fechaDia"
			dayValue="<%= fechaDia.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaMes"
			monthValue="<%= fechaDia.get(Calendar.MONTH)%>"
			monthNullable="<%= true %>" yearParam="fechaAnio"
			yearValue="<%= fechaDia.get(Calendar.YEAR)%>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDia.get(Calendar.YEAR)%>"
			yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  +1 %>"
			firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
			disabled="<%= inHabilitar %>" /></td>
			<%} %>
			
	
		<td >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </td>				
		<td ><label>&nbsp;&nbsp;&nbsp;&nbsp;<liferay-ui:message key="Periodo" />:</label> </td>
		
	<% if (fechaPeriodoOk) {%>			
		
			<td><liferay-ui:input-date dayParam="periodoDia"
			dayValue="<%= fechaPeriodo.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="periodoMes"
			monthValue="<%= fechaPeriodo.get(Calendar.MONTH)%>"
			monthNullable="<%= true %>" yearParam="periodoAnio"
			yearValue="<%= fechaPeriodo.get(Calendar.YEAR)%>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDia.get(Calendar.YEAR)  -1%>"
			yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
			disabled="<%= inHabilitar %>" /> </td>
			<%}else{ %>
			<td><liferay-ui:input-date dayParam="periodoDia"
			dayValue=""
			dayNullable="<%= true %>" monthParam="periodoMes"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="periodoAnio"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDia.get(Calendar.YEAR)  -1%>"
			yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
			disabled="<%= inHabilitar %>" /> </td>
			<%} %>	
		
		<td colspan="1"><label align='center' id="<portlet:namespace/>captionregistro">Nro Registro</label></td>
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />registro"
			name="<portlet:namespace />registro" size="7" maxlength="7" onblur="validarNroRegistrooTroquel(false);" 
			type="text" onkeydown="allowOnlyDigits(event);"
				 <% if (inHabilitar || esEdicion) { %><%="disabled='disabled'" %><%}%> 
			value="<%= medicacion != null &&  medicacion.getRegistro()>0  ? medicacion.getRegistro()  : "" %>" />			
		</td>
		
		
		<td colspan="1"><label align='center' id="<portlet:namespace/>captionmanualdat">Manual DAT</label></td>		
		<td colspan="5">
		<input type="checkbox"id="<portlet:namespace />manualdat" name="<portlet:namespace />manualdat"			
			
			<%=Validator.isNotNull(medicacion) && medicacion.getManualDat()   ? "checked" : "Unchecked" %>
					
			<% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>>
		</td>
		
		
		</tr>
	</table>
	
		<table align ='center'  class="lfr-table">
    <tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	
		<tr>
		<td colspan="1"><label align='center' id="<portlet:namespace/>captiontroquel">Troquel</label></td>
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />troquel"
			name="<portlet:namespace />troquel" size="7" maxlength="7"
			type="text" onkeydown="allowOnlyDigits(event);"  onblur="validarNroRegistrooTroquel(true);"
			value="<%= medicacion != null &&  medicacion.getTroquel()>0  ? medicacion.getTroquel()  : "" %>" />			
		</td>
		
		<td colspan="1"><label align='center' id="<portlet:namespace/>captionnombre">Nombre</label></td>
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />nombre"
			name="<portlet:namespace />nombre" size="50" maxlength="50"
			type="text"
			value="<%= medicacion != null &&  medicacion.getNombre()!=null   ? medicacion.getNombre()   : "" %>" />			
		</td>
		
		<td colspan="1"><label align='center' id="<portlet:namespace/>captionpresentacion">Presentacion</label></td>
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />presentacion"
			name="<portlet:namespace />presentacion" size="50" maxlength="100"
			type="text"
			value="<%= medicacion != null &&  medicacion.getPresentacion()!=null   ? medicacion.getPresentacion()   : "" %>" />			
		</td>		
		</tr>
		<tr>
		<td colspan="12">&nbsp;</td>
	    </tr>	
		<tr>
		<td colspan="1"><label align='center' id="<portlet:namespace/>captionlaboratorio">Laboratorio</label></td>
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />laboratorio"
			name="<portlet:namespace />laboratorio" size="50" maxlength="100"
			type="text"
			value="<%= medicacion != null &&  medicacion.getLaboratorio() !=null   ? medicacion.getLaboratorio()   : "" %>" />			
		</td>
		
		<td colspan="1"><label align='center' id="<portlet:namespace/>captionprecio">Precio</label></td>
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />precio"
			name="<portlet:namespace />precio" size="10" maxlength="15"
			type="text"
			value="<%= medicacion != null &&  medicacion.getPrecio()  !=null   ? String.valueOf(medicacion.getPrecio())   : "" %>" />			
		</td>
		
		<td colspan="1"><label align='center' id="<portlet:namespace/>captionpresentacionactiva">Presentacion Activa</label></td>		
		
		<td>
					<select <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
						name="<portlet:namespace />presentacionactiva"
						id="<portlet:namespace />presentacionactiva" > 
						<%-- <option value=""  ></option>												
                        <option value="1" <%= medicacion != null  && medicacion.getBaja().equals("1")  ? "selected" : ""  %>>NO</option> --%>
                        
                        <option value="0"  <%= medicacion != null  && medicacion.getBaja().equals("0")  ? "selected" : ""  %>  >SI</option>
					</select>
				</td>
		
		
		
		</tr>
		</table>
		<table   class="lfr-table">
		<tr>
		<td colspan="12">&nbsp;</td>
	    </tr>	
		<tr>
		
		<td colspan="1"><label align='center' id="<portlet:namespace/>captioncodebar">Codigo Barras</label></td>
		<td colspan="1"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />codebar"
			name="<portlet:namespace />codebar" size="13" maxlength="13"
			type="text" onkeydown="allowOnlyDigits(event);"
			value="<%= medicacion != null &&  medicacion.getCod_barra()  !=null   ? medicacion.getCod_barra()   : "" %>" />			
		</td>
		
		<td colspan="2"><label align='center' id="<portlet:namespace/>captionaccion"><liferay-ui:message key="Accion" /></label></td>
		<td colspan="1"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />accion"
			name="<portlet:namespace />accion" size="80" maxlength="100"
			type="text" 
			value="<%= medicacion != null &&  medicacion.getAccion()   !=null   ? medicacion.getAccion()   : "" %>" />			
		</td>
		
		<td colspan="2"><label align='center' id="<portlet:namespace/>captiondroga">Droga</label></td>
		<td colspan="1"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />droga"
			name="<portlet:namespace />droga" size="30" maxlength="100"
			type="text"
			value="<%= medicacion != null &&  medicacion.getDroga()    !=null   ? medicacion.getDroga()   : "" %>" />			
		</td>		
		</tr>
		<tr>
		<td colspan="12">&nbsp;</td>
	    </tr>	
		<tr>
     	<tr>	
     	<td><label id="<portlet:namespace />tipoventalabel" > <liferay-ui:message key="Tipo Venta:"/>:</label></td>	
		<td>
		<select <% if (inHabilitar) { %> <%="disabled='disabled'" %>  
				<%}%> name="<portlet:namespace/>tipoventa"	id="<portlet:namespace/>tipoventa">
					<option selected value="0">SELECCIONE</option>
					<% for (TiposDeVentas tipoVenta    : listatipodeventas) { %>
					<option
					    <%= medicacion != null && medicacion.getTipoVentaInt() == tipoVenta.getCodigo() ? "selected" : ""  %>
						value="<%=tipoVenta.getCodigo()  %>"><%=tipoVenta.getDescripcion()%>
						</option>
					<% } %>
		</select>
		</td>
		
		<td colspan="1"><label align='center' id="<portlet:namespace/>captioniva">IVA</label></td>
		<td>
					<select <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
						name="<portlet:namespace/>iva"
						id="<portlet:namespace />iva" > 
						<option value=""></option>						
						<option value="0" <%= medicacion != null  && medicacion.getIva().equals("0")  ? "selected" : ""  %> >SI</option>
                        <option value="1" <%= medicacion != null  && medicacion.getIva().equals("1")  ? "selected" : ""  %> >NO</option>
					</select>
				</td>
		</tr>
</table>
		
</fieldset>
      

<br/>

<div  id="<portlet:namespace />saveMedicacionDiv" align="center"  style="height:80px;  overflow-x: hidden;">
<table>
	<tr>	
<td>
<input type="button" value="<liferay-ui:message key="Grabar" />"
	onClick="<portlet:namespace />saveMedicacion();"  title="<liferay-ui:message key="Graba los Datos Ingresados." />"/>	
</td>
</tr>		
</table>
</div>


<div  id="<portlet:namespace />diveditSituacionMedica" align="center"  style="height:80px;  overflow-x: hidden;">
<table>
	<tr>	
<td>
<% if (!inHabilitar ) { %>
<div id='<portlet:namespace />divBotonEdicion'>
<input type="button" value="<liferay-ui:message key="Grabar"  />"
	onClick="<portlet:namespace />editaMedicacion();"  title="<liferay-ui:message key="Edita los Datos Ingresados." />"/>
</div>		
<% }%>
</td>
</tr>
</table>
</div>


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

<input	 type="hidden" name="<portlet:namespace />consultaequipointer"  id="<portlet:namespace />consultaequipointer" value="<%=inHabilitar  ? true  : false  %>"/>
<input  id="<portlet:namespace />nom_seleccionado" name="<portlet:namespace />nom_seleccionado" type="hidden" value="1"/>


<div id='validarExistenciaCuit' style="float: right;"></div>

</form>

<script type="text/javascript">

var popupMD;

jQuery('#<portlet:namespace />buscando').hide();
jQuery("#<portlet:namespace />printButton").hide();
jQuery("#<portlet:namespace />periodoDia").hide();
jQuery('#<portlet:namespace />diveditSituacionMedica').hide();
//jQuery('#<portlet:namespace />manualdat').hide();
//jQuery('#<portlet:namespace/>captionmanualdat').hide();

document.getElementById("<portlet:namespace />manualdat").disabled = "disabled";


// *******************************************************************************************************************************
// *******************************************************************************************************************************



function <portlet:namespace />saveMedicacion() {
			
	if ( validaDatos())  {
		var data=jQuery('#<portlet:namespace/>tipoventa').val();
		jQuery('#<portlet:namespace />valortipoventa').val(data);		
		document.getElementById("<portlet:namespace />manualdat").disabled = "";
		jQuery("#<portlet:namespace />manualdat").removeAttr("checked");// siempre es FALSE manual Dat desde el ABM
		var accionEnCurso = document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value;
		document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.SAVE  %>';				
		
		var xportletUrl = '/farmaciaospim/editar_borrar_medicamentos_entry';		
		var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="accionEnCurso" value="__accionEnCurso"/>'+
		'<liferay-portlet:param name="esDatosTab" value="true"/>'+
		'</liferay-portlet:actionURL>';
	
	    url = url.replace("__xportletUrl",xportletUrl); 
  	    url = url.replace("__accionEnCurso", accionEnCurso);
  	      
		
		document.<portlet:namespace />sitmedica_fm.method = 'post';
		submitForm(document.<portlet:namespace />sitmedica_fm, url);
	}	
}	

function <portlet:namespace />editaMedicacion() {
	<% if ( medicacion!=null ) { %>
     if (validaDatos())	{
    	    document.getElementById("<portlet:namespace />manualdat").disabled = "";
 		    jQuery("#<portlet:namespace />manualdat").removeAttr("checked");// siempre es FALSE manual Dat desde el ABM
			var accionEnCurso = document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value;
			document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.UPDATE %>';			 
			
			<%-- var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/farmaciaospim/editar_borrar_medicamentos_entry" /></portlet:actionURL>';
			url = url + '&accionEnCurso=' + accionEnCurso + "&esDatosTab=false"; --%>
			
			var xportletUrl = '/farmaciaospim/editar_borrar_medicamentos_entry';
			
			var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="accionEnCurso" value="__accionEnCurso"/>'+
			'<liferay-portlet:param name="esDatosTab" value="false"/>'+
			'</liferay-portlet:actionURL>';
		
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__accionEnCurso", accionEnCurso);
		    
			document.<portlet:namespace />sitmedica_fm.method = 'post';			
			submitForm(document.<portlet:namespace />sitmedica_fm, url);			
			
   	 }				
     <%}%>
}

<% if (esEdicion){	%>
	jQuery('#<portlet:namespace />diveditSituacionMedica').show();
	jQuery('#<portlet:namespace />saveMedicacionDiv').hide();
<%}%>
<% if (inHabilitar){	%>
	jQuery('#<portlet:namespace />diveditSituacionMedica').hide();
	jQuery('#<portlet:namespace />saveMedicacionDiv').hide();
<%}%>

function validarNroRegistrooTroquel(esTroquel)
{
	validarRegistroTroquel(esTroquel);
	}
	
function validaDatos(){
		
	var respuesta;
	respuesta=true;
	// fecha 	  
	var diaExist  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaDia").val()));
	var mesExist  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaMes").val()));
	var anioExist   = isNaN(parseInt(jQuery("#<portlet:namespace />fechaAnio").val()));
	// periodo
	var diaExist1  = isNaN(parseInt(jQuery("#<portlet:namespace />periodoDia").val()));
	var mesExist1  = isNaN(parseInt(jQuery("#<portlet:namespace />periodoMes").val()));
	var anioExist1   = isNaN(parseInt(jQuery("#<portlet:namespace />periodoAnio").val()));	
	var validaTroquel  = document.getElementById("<portlet:namespace />okTroquelRegistro").value;
	var validaNroRegistro  = document.getElementById("<portlet:namespace />okNroRegistro").value;
	
	
	if   ( respuesta  &&  (diaExist || mesExist || anioExist) )   {
	   alert("Debe ingresar la fecha.");
	   respuesta= false ;
	}
	
	if   ( respuesta  &&  (mesExist1 || anioExist1)  )   {
		   alert("Debe ingresar el Periodo.");
		   respuesta= false ;
		}
	
	
	if (validaTroquel=='Bad Troquel' && respuesta){
    	alert('El nro de Troquel esta asociado a otro medicamento.');
    	respuesta= false ;
    }
	
	if (validaNroRegistro=='Bad Registro' && respuesta){
    	alert('El nro de Registro esta asociado a otro medicamento.');
    	respuesta= false ;
    }
	
	var nroRegistro=jQuery('#<portlet:namespace />registro').val();
	
    if ((nroRegistro=="") && respuesta)
    {		
		alert ('Debe ingresar el nro de registro.');		
		jQuery('#<portlet:namespace />registro').focus();
		respuesta= false;
	}
	
    var nroTroquel =jQuery('#<portlet:namespace />troquel').val();
	
    if ((nroTroquel=="") && respuesta)
    {		
		alert ('Debe ingresar el troquel.');		
		jQuery('#<portlet:namespace />troquel').focus();
		respuesta= false;
	}
    
    if ( nroTroquel.length<7 && respuesta ){
    	alert ('El troquel debe tener 7 digitos.');		
		jQuery('#<portlet:namespace />troquel').focus();
		respuesta= false;
    }
    
    if ( jQuery('#<portlet:namespace />nombre').val()=="" && respuesta )  {    	
    	alert ('Debe ingresar el nombre del medicamento.');
		jQuery('#<portlet:namespace />nombre').focus();
		respuesta= false;
    }    
    
    	
	return respuesta;
}




function validarRegistroTroquel(esTroquel)
{
	var params="";
	var nroRegistro=jQuery('#<portlet:namespace />registro').val();
	var nroTroquel =jQuery('#<portlet:namespace />troquel').val();
	var idMedicamento=0;
	
	  <%if (medicacion!=null && medicacion.getId_medicamento()>0){%>
	  	var idMedicamento=<%=medicacion.getId_medicamento()%>;
	  <%}%>
	 	
		var xportletUrl = '/farmaciaospim/consultar_nro_registro_medicamento';
		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="nroRegistro" value="__nroRegistro"/>'+
		'<liferay-portlet:param name="idMedicamento" value="__idMedicamento"/>'+
		'<liferay-portlet:param name="nroTroquel" value="__nroTroquel"/>'+
		'<liferay-portlet:param name="esTroquel" value="__esTroquel"/>'+
		'</liferay-portlet:renderURL>';
	
	    url = url.replace("__xportletUrl",xportletUrl); 
  	    url = url.replace("__nroRegistro", nroRegistro);
  	    url = url.replace("__idMedicamento", idMedicamento);
  	    url = url.replace("__nroTroquel", nroTroquel);
  	    url = url.replace("__esTroquel", esTroquel);
	    
	 
	 jQuery.ajax({   
		url: url,
		success: function(data) {
			var obj = jQuery.parseJSON(data);
			var respuesta = obj.nroRegistroSoloEnMedicamentos  ;
			var nombre  = obj.nombreMedicacion  ;
			if(respuesta=='true' ){
					if (esTroquel){
						alert('El Numero de Troquel existe y esta asociado al medicamento ' + nombre);
						jQuery('#<portlet:namespace />okTroquelRegistro').val("Bad Troquel");	
					}else{
						alert('El Numero Registro existe y esta asociado al medicamento ' + nombre);
						jQuery('#<portlet:namespace />okNroRegistro').val("Bad Registro");
					}
					
				    return true;
			}else{
				if (esTroquel){
					jQuery('#<portlet:namespace />okTroquelRegistro').val("Ok");	
				}else{
					jQuery('#<portlet:namespace />okNroRegistro').val("Ok");
				}
			}
			
								}
				});

	 return false;
}

</script>

