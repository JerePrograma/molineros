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
	
	String organizacionId = user.getOrganizations().size()>0?String.valueOf(user.getOrganizations().get(0).getOrganizationId()):"";
	String tabValue = ParamUtil.getString(request, "tab", null); // "datos"
	
	String titulo=demanda!=null && demanda.getId()!=null? 
		       demanda.getId().toString() + "-" + demanda.getCuit() + 
		       " -(" +(demanda.getSucursal()!=null?demanda.getSucursal() :"") +") " +
		       (demanda.getRazonSocial()!=null?demanda.getRazonSocial().toUpperCase():"")
		       :"";
		
%>
	<fieldset class="block-labels"> 
		  <legend>Registros Contables</legend>
		  
		  <h1>Demanda Nro. <%=titulo%></h1>
		  
		  <table class="lfr-table">
		   <tr>
		    <td>
			  <%if(demanda!=null && demanda.getId()!=null){%>
			    <input id="<portlet:namespace />agregarEstado" value="Agregar Registración Contable" onClick="javascript:agregarAsiento();"
		          type="button"	 />
		      <%}%>    
			</td>	  
		  </tr>
		  
		  <tr><td>&nbsp;&nbsp;</td></tr>
		  
		  <tr> 
		   <td colspan="20">
		    <div align="center" id="<portlet:namespace />asientosDiv">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_contabilidad_result.jsp">
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEdicion)%>" />
				</liferay-util:include>
			</div>	
		  </td>
		 </tr> 
		</table>
		
	</fieldset>  
		
		
<script type="text/javascript">
var popupE;

function agregarAsiento(){	
	if(popupE==null)
		    popupE = Liferay.Popup({title:"Registro Contable",modal:true,width:1200,
		    	onClose: function() { popupE = null;
		    	           var url1 ='<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_contabilidad_result';
		    	           jQuery("#<portlet:namespace />asientosDiv").load(url1); 
		    	}});

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
	url += '&cmd=agregarAsiento&nro=0';
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery(popupE).load(url);
}


</script>

