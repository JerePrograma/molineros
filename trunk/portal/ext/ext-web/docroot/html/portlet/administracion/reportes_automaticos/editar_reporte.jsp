<%@ include file="/html/portlet/administracion/init.jsp"%>
<%@ page import="ar.com.ospim.administracion.exception.EmailInvalidoException"%>

<%
	ReporteAutomatico ra = (ReporteAutomatico) request
			.getAttribute(WebKeysAdministracion.REPORTE_EN_EDICION);

	Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
	if (ra != null && ra.getFechaUnicaVez() != null) {
		fechaInicio.setTime(ra.getFechaUnicaVez());
	}
%>

<liferay-ui:error
	exception="<%= ar.com.ospim.administracion.exception.EmailInvalidoException.class %>"
	message="email-invalido" />

<fieldset class="block-labels"><legend>Agregar/Editar
reporte</legend>
<table class="lfr-table">
	<tr>
		<td><label><liferay-ui:message key="id" />:</label></td>
		<td><input type="text" readonly="readonly"
			id="<portlet:namespace />id" name="<portlet:namespace />id"
			value="<%=ra != null && ra.getId() != 0 ? String
					.valueOf(ra.getId()) : new String("")%>"
			size="5" /></td>
		<td colspan="3">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="titulo" />:</label></td>
		<td colspan="4"><input type="text"
			id="<portlet:namespace />titulo" name="<portlet:namespace />titulo"
			value="<%=ra != null ? ra.getTitulo() : new String("")%>" size="80"
			maxlength="100" /></td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="periodicidad" />:</label></td>
		<td colspan="3">
		<table>
			<tr>
				<td>Diario:</td>
				<td><input type="radio"
					name="<portlet:namespace />periodicidad" id="radio_diario"
					value="diario" />&nbsp; <label><liferay-ui:message
					key="incluir-fin-de-semana" />:</label>&nbsp; <input type="checkbox"
					id="<portlet:namespace />incluir_fin_de_semana"
					name="<portlet:namespace />incluir_fin_de_semana" value="true"
					<%if (ra != null && ra.isIncluirFinDeSemana()) {%>
					checked="checked" <%}%> /></td>
			</tr>
			<tr>
				<td>Semanal:</td>
				<td><input type="radio"
					name="<portlet:namespace />periodicidad" id="radio_semanal"
					value="semanal" />&nbsp; <select
					id="<portlet:namespace />dia_de_la_semana"
					name="<portlet:namespace />dia_de_la_semana">
					<option value="0"></option>
					<option value="2">Lunes</option>
					<option value="3">Martes</option>
					<option value="4">Miercoles</option>
					<option value="5">Jueves</option>
					<option value="6">Viernes</option>
					<option value="7">Sabado</option>
					<option value="1">Domingo</option>
				</select></td>
			</tr>
			<tr>
				<td>Mensual:</td>
				<td><input type="radio"
					name="<portlet:namespace />periodicidad" value="mensual"
					id="radio_mensual" />&nbsp; Dia:&nbsp;<input type="text"
					id="<portlet:namespace />dia_del_mes"
					name="<portlet:namespace />dia_del_mes"
					value="<%=ra != null
					? String.valueOf(ra.getDiaDelMes())
					: new String("")%>"
					size="5" /></td>
			</tr>
			<td>Unica vez:</td>
			<td><input type="radio" name="<portlet:namespace />periodicidad"
				value="unicavez" id="radio_unicavez" />&nbsp; <liferay-ui:input-date
				dayParam="fechaDiaUnicaVez"
				dayValue="<%= fechaInicio.get(Calendar.DATE) %>"
				monthParam="fechaMesUnicaVez"
				monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"
				yearParam="fechaAnioUnicaVez"
				yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaInicio.get(Calendar.YEAR) %>"
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 2%>"
				firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
				disabled="false" /></td>
			</tr>
		</table>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="hora" />&nbsp;(0-23):</label></td>
		<td><input type="text" id="<portlet:namespace />hora"
			name="<portlet:namespace />hora"
			value="<%=ra != null && ra.getId() != 0 ? String.valueOf(ra
					.getHora()) : new String("")%>"
			size="5" /></td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="stored-procedure" />:</label></td>
		<td colspan="4"><input type="text"
			id="<portlet:namespace />stored_procedure"
			name="<portlet:namespace />stored_procedure"
			value="<%=ra != null ? ra.getStoredProcedure() : new String("")%>"
			size="80" maxlength="100" /></td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td width="15%"><label><liferay-ui:message
			key="csv-parameteres" />:</label></td>
		<td colspan="4"><textarea
			id="<portlet:namespace />csv_parameteres"
			name="<portlet:namespace />csv_parameteres" rows="5" cols="82"><%=ra != null ? ra.getCsvParameteres() : new String("")%></textarea></br>
		(param1=Tipo,param2=Tipo,...)</br>
		ej: 1=Integer,null=String,Ejemplo=String,true=Boolean,01/01/2011 12:00:00=Date</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="emails" />:</label></td>
		<td colspan="4"><textarea id="<portlet:namespace />emails"
			name="<portlet:namespace />emails" rows="5" cols="82"><%=ra != null ? ra.getEmails() : new String("")%></textarea></br>
		(mail1,mail2,mail3,...)</br>
		ej: sample@mail.com,sample2@other-mail.com</td>
	</tr>
	<tr>
		<td><label>Difusión:</label></td>
		<td colspan="4">
			<input type="text" id="<portlet:namespace />difusion" name="<portlet:namespace />difusion" 
			value="<%=ra != null ? ra.getDifusion() : 0%>" maxlength="1" onkeydown="allowOnlyDigits(event);" size="5" />
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label>Base:</label></td>
		<td colspan="4" >
			<select name="<portlet:namespace />base" id="<portlet:namespace />base">
				<option value="1" <%if(ra !=null && ra.getBase()==1){%> selected="selected"  <% } %> >Portal</option>
				<option value="2" <%if(ra !=null && ra.getBase()==2){%> selected="selected"  <% } %> >Portal Empleadores</option>
			</select>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label>Java class:</label></td>
		<td colspan="4" ><input type="text"
			id="<portlet:namespace />java"
			name="<portlet:namespace />java"
			value="<%=ra != null ? ra.getJava() : new String("")%>"
			size="80" maxlength="100" />
			</br>
		(incluir package completo y nombre de clase)
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="5"><input id="<portlet:namespace />agregar"
			value="<liferay-ui:message key="agregar"/>"
			title="<liferay-ui:message key="agregar" />" type="button"
			onclick="agregarReporteDesdePopup()" /></td>
	</tr>
</table>
</fieldset>

<script type="text/javascript">
	function agregarReporteDesdePopup(){
		var id = jQuery('#<portlet:namespace />id').val();
		var titulo = jQuery('#<portlet:namespace />titulo').val();
		var dia = jQuery('#<portlet:namespace />dia_de_la_semana').val();
		var incluirFinde = document.getElementById("<portlet:namespace />incluir_fin_de_semana");
		var hora = jQuery('#<portlet:namespace />hora').val();
		var sp = jQuery('#<portlet:namespace />stored_procedure').val();
		var csv = jQuery('#<portlet:namespace />csv_parameteres').val();
		var emails = jQuery('#<portlet:namespace />emails').val();

		var dia_del_mes = jQuery('#<portlet:namespace />dia_del_mes').val();
		var fechadia = jQuery('#<portlet:namespace />fechaDiaUnicaVez').val();
		var fechaMes = jQuery('#<portlet:namespace />fechaMesUnicaVez').val();
		var fechaAnio = jQuery('#<portlet:namespace />fechaAnioUnicaVez').val();

		var difusion = jQuery('#<portlet:namespace />difusion').val();
		var base = jQuery('#<portlet:namespace />base').val();
		var java = jQuery('#<portlet:namespace />java').val();
		
		var periodicidad = document.getElementsByName('<portlet:namespace />periodicidad');
		var periodicidadValue = "";
		var i = 0;
		for (i = 0; i<periodicidad.length; i++){
			if (periodicidad[i].checked) {
				periodicidadValue= periodicidad[i].value; 
			}
		}

		if (trim(periodicidadValue) == ""){
			alert("Debe seleccionar un tipo de periodicidad");
			return;
		}

		if (trim(periodicidadValue) == "semanal" && dia == 0){
			alert("Debe seleccionar un dia de la semana");
			return;
		}

		if (trim(periodicidadValue) != "semanal"){
			dia=0;
		} 

		if (trim(periodicidadValue) == "mensual" && !(trim(dia_del_mes) > 0 && trim(dia_del_mes) < 32)){
			alert("Debe ingresar un dia del mes valido");
			return;
		}
		 		
		if (trim(titulo) == "") {
			alert("Debe completar el Titulo");
			return;
		}

		if (trim(hora) == "") {
			alert("Debe completar la Hora");
			return;
		}

		if (trim(sp) == "" && trim(java) == "" ) {
			alert("Debe completar el Stored Procedure o Java Class");
			return;
		}
		if (trim(sp) != "" && trim(java) != "" ) {
			alert("Debe completar solo Stored Procedure o solo Java Class");
			return;
		}
		if (trim(emails) == "") {
			alert("Debe completar al menos un mail para Notificar");
			return;
		}

		if (hora<0 || hora>23){
			alert("La hora debe estar entre 0 y 23");
			return;
		}
			
		var incluir = incluirFinde.checked ? 'true' : 'false';
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/administracion/agregar_reportes'
			+ '&id=' + id
			+ '&titulo=' + encodeURI(trim(titulo))
			+ '&dia_de_la_semana=' + dia
			+ '&incluir_fin_de_semana=' + (incluirFinde.checked ? incluirFinde.value : 'false')
			+ '&hora=' + escape(trim(hora))
			+ '&stored_procedure=' + escape(trim(sp)) 
			+ '&csv_parameteres=' + escape(trim(csv))
			+ '&emails=' + escape(trim(emails))
			+ '&periodicidad=' +  periodicidadValue
		    + '&dia_del_mes=' + escape(dia_del_mes)
		    + '&fechaDiaUnicaVez=' + fechadia
		    + '&fechaMesUnicaVez=' + fechaMes
		    + '&fechaAnioUnicaVez=' + fechaAnio
		    + '&difusion=' + escape(difusion)
		    + '&base=' + escape(base)
		    + '&java=' + encodeURI(java)
			+ '&rnd=' + Math.floor(Math.random()*100);
		jQuery(popup).load(url);
	}


	<%String success = (String) request.getAttribute(WebKeysAdministracion.SUCCESS);
		if (success != null&& success.equals(WebKeysAdministracion.SUCCESS)) {%>
			   if(popup){
				   Liferay.Popup.close(popup);
			   }
   <%}%>

  <%if (ra != null && ra.getDiaDeLaSemana() != 0) {%>
  	document.getElementById("radio_semanal").checked = true;
  <%} else if (ra != null && ra.isDiario()) {%>
  	document.getElementById("radio_diario").checked = true;
  <%} else if (ra != null && ra.getDiaDelMes() != 0) {%>
	 document.getElementById("radio_mensual").checked = true;
  <%} else if (ra != null && ra.getFechaUnicaVez() != null) {%>
  	document.getElementById("radio_unicavez").checked = true;
  <%}%>

  <%if (ra != null && ra.getDiaDeLaSemana() != 0) {%>
  	seleccionarSelect("<portlet:namespace />dia_de_la_semana",<%=ra.getDiaDeLaSemana()%>);
  <%}%>
</script>