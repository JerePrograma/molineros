<%@ include file="/html/portlet/estudio_isidro/init.jsp"%>
<%@page import="ar.com.ospim.estudioisidro.service.DemandaJudicialServiceUtil"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	DemandaJudicial demanda=(DemandaJudicial)request.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	NumberFormat format2D = new DecimalFormat("#0.00");
	boolean esEdicion = true;
	if(viewStr==null){
		viewStr=ParamUtil.getString(request, "view");
	}
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "estudio_isidro";
	}
	
	int id_demanda=demanda!=null && demanda.getId()!= null ?(int)demanda.getId():0;
	if(demanda==null){
		demanda= new DemandaJudicial();
	} 
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
	if(demanda.getFecha()==null){
		fecha.setTime(new Date());
	}else{
	  fecha.setTime(demanda.getFecha());
	} 
	
	
	String[] tiposStr = TraeListasServiceUtil.getSystemConfig("GESTION_JUDICIAL_TIPOS_ESTADOS").split(";");
		List<ClaseBase> tipos =new ArrayList<ClaseBase>();
		for(int i=0;i<=tiposStr.length-1;i++){
			ClaseBase c =new ClaseBase();
			String codigo = tiposStr[i].split("=")[0];
			String descripcion = tiposStr[i].split("=")[1];
			c.setId(codigo);
			c.setDescripcion(descripcion);
			tipos.add(c);
		}
	
	String organizacionId = user.getOrganizations().size()>0?String.valueOf(user.getOrganizations().get(0).getOrganizationId()):"";
	String tabValue = ParamUtil.getString(request, "tab", null); // "datos"
	
	String titulo=demanda!=null && demanda.getId()!=null? 
		       demanda.getId().toString() + "-" + demanda.getCuit() + 
		       " -(" +(demanda.getSucursal()!=null?demanda.getSucursal() :"") +") " +
		       (demanda.getRazonSocial()!=null?demanda.getRazonSocial().toUpperCase():"")
		       :"";
		
%>


		

	<fieldset class="block-labels"> 
		  <legend>Estados</legend>
		  
		  <h1>Demanda Nro. <%=titulo%></h1>
		  
		  <table class="lfr-table">
		   <tr>
		    <td>
			<label>Estado:</label>
			</td>
		    <td>
			<td>
			    <select id="<portlet:namespace />estado" name="<portlet:namespace />estado">
						<%for(ClaseBase c:tipos) {%>
						<option	value="<%= c.getId() %>"><%=c.getDescripcion() %>
						</option>
						<% } %>				
					</select>
			</td>	
			
			<td>
			       <label><liferay-ui:message key="fecha" />:</label>
			</td>
			<td>  
			         <liferay-ui:input-date
						 dayParam="fechaEstadoDia"
						 dayValue="<%=fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						 dayNullable="<%= false %>" monthParam="fechaEstadoMes"
						 monthValue="<%=fechaHasta.get(Calendar.MONTH )%>"
						 monthNullable="<%= false %>" yearParam="fechaEstadoAnio"
						 yearValue="<%=fechaHasta.get(Calendar.YEAR ) %>"
						 yearNullable="<%= false %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/>
			</td>
			
			<table class="lfr-table">
		     <legend><liferay-ui:message key="obs-internas" />:</legend>
		   	   <textarea rows="4" cols="160" maxlength="20000" 
		              id="<portlet:namespace />observacionEstado" 
		              name="<portlet:namespace />observacionEstado"
		              style="resize:vertical;"></textarea>
		
		      </table>
					
			<td>
			  <%if(demanda!=null && demanda.getId()!=null){%>
			    <input id="<portlet:namespace />agregarEstado" value="Agregar Estado" onClick="javascript:agregarEstado();"
		          type="button"	 />
		      <%}%>    
			</td>	  
		  </tr>
		    <tr> 
		   <td colspan="20">
		    <div align="center" id="<portlet:namespace />estadosDiv">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_estados_result.jsp">
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEdicion)%>" />
				</liferay-util:include>
			</div>	
		  </td>
		  </tr> 
		</table>
		
	</fieldset>  
		
		
<script type="text/javascript">


function agregarEstado(){	
	var estado=jQuery('#<portlet:namespace />estado').val();
	var dia=jQuery('#<portlet:namespace />fechaEstadoDia').val();
	var mes=jQuery('#<portlet:namespace />fechaEstadoMes').val();
	var anio=jQuery('#<portlet:namespace />fechaEstadoAnio').val();
	var observacion=jQuery('#<portlet:namespace />observacionEstado').val();

	//jQuery('#<portlet:namespace />buscando').show();

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
	url += '&cmd=agregarEstado&estado='+estado+"&dia="+dia+"&mes="+mes+"&anio="+anio ;
	url += '&observacion='+encodeURI(observacion);
	url += '&rnd=' + Math.floor(Math.random()*100);

	jQuery('#<portlet:namespace />estadosDiv').load(url, function() {
		    jQuery('#<portlet:namespace />estado').val("");
		    jQuery('#<portlet:namespace />observacionEstado').val("");
			//jQuery('#<portlet:namespace />buscando').hide();
		}
	);
}


</script>

