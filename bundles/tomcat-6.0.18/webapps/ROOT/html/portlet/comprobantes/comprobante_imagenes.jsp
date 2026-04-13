<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil"%>
<%@ include file="/html/portlet/comprobantes/init.jsp"%>



<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	Comprobante comprobante=(Comprobante)request.getSession().getAttribute(WebKeysComprobantes.COMPROBANTE_IMAGEN_VIEW);
//	String nroSolicitud=ParamUtil.getString(request, "nroSolicitud");;
	boolean esEdicion = true;
	if(viewStr==null){
		viewStr=ParamUtil.getString(request, "view");
	}
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "comprobantes";
	}
	
	//int id_preautorizacion=preautorizacion!=null && preautorizacion.getId()!= null ?(int)preautorizacion.getId():0;
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	String tabValue = ParamUtil.getString(request, "tab", null); 
	String cmd = (String) request.getAttribute(Constants.CMD);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	String desdeResult = (String) request.getSession().getAttribute("desde_result");
	
%>

<form action="" method="post" name="<portlet:namespace />fmSI" enctype="multipart/form-data">
	
<fieldset class="block-labels"><legend>Imágenes Comprobantes</legend>

<liferay-ui:error key="errorUploadFile" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
<liferay-ui:error key="errorAfiliadoNull" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />

<h1>Comprobante. <%=comprobante!=null ? comprobante.getCuit() + "  " +
         comprobante.getTipoComprobante()+" "+comprobante.getLetraComprobante()+ " " +
         String.format("%05d",comprobante.getPtoVenta())+"-"+comprobante.getNroComprobante() :""%></h1>

<table class="lfr-table">
  <tr>
  </tr>
</table>
</fieldset>

 
<div id="<portlet:namespace />listado_imagenes_comprobantes">
 
				<jsp:include page='/html/portlet/comprobantes/comprobante_search_documentos.jsp' />
				  
</div>


<input  type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" />
<input type="hidden" name="<portlet:namespace />tab_seleccionada"  value="<%=tabValue%>" />
<input type="hidden" value='<%=esEdicion?"EDIT":"VIEW"%>' name="view" id="view" /> 
</form>

<script type="text/javascript">




</script>
