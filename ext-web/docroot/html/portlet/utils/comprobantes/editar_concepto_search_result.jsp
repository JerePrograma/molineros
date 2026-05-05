<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "liquidaciones";
	}else if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}else if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}
	BigDecimal totalConceptos = BigDecimal.ZERO;
	boolean esEdicion = true;

	Comprobante comprobante = (Comprobante) request
			.getSession().getAttribute(
					WebKeysGlobal.COMPROBANTE_EN_EDICION);

	List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
	
	PortletURL portletURL = renderResponse.createRenderURL();
	String orderByCol = ParamUtil.getString(request, "orderByCol");
	String orderByType = ParamUtil.getString(request, "orderByType");
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("concepto");
	headerNames.add("importe");
	headerNames.add("importe-a-pagar");

	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
			LanguageUtil.get(pageContext, "no-conceptos-were-found"));
	%>
	<form name="<portlet:namespace />conceptos_embebidos" method="post">
	<%
	List resultRows = searchContainer.getResultRows();
	int total = conceptos.size();
	int i=0;
	if (comprobante.getConceptos() != null && comprobante.getConceptos().size()>0){
		for (ComprobanteConcepto cc : comprobante.getConceptos()){
			ResultRow row = new ResultRow(cc, cc.hashCode(), i);
			row.addText(cc.getConceptoComprobante().getDescripcion());
			row.addText(cc.getImporteOriginal().toString());
			StringBuilder sb= new StringBuilder();
				sb.append("<input type=\"text\" id=\"");
				sb.append(cc.getConceptoComprobante().getId() * 1000000 + cc.getCentroCosto().getId() );
				sb.append("\" name=\"");
				sb.append(cc.getConceptoComprobante().getId() * 1000000 + cc.getCentroCosto().getId() );
				sb.append("\" value=\"");
				sb.append(cc.getImporte());				
				sb.append("\" />");
			row.addText(sb.toString());
			resultRows.add(row);
			totalConceptos=totalConceptos.add(cc.getImporte());
			i++;
		}
	}
	searchContainer.setTotal(total);
%>

<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
<table width="100%" align="left">
<tr>
	<td colspan="2">&nbsp;</td>
</tr>
<tr>
<td><label><liferay-ui:message key="importe-conceptos" />:</label>&nbsp;&nbsp;&nbsp;<%=totalConceptos.toString()%></td>
<td align="right"><input id="<portlet:namespace />editar" value="<liferay-ui:message key="edit"/>" title="<liferay-ui:message key="edit" />" 
				  type="button" onClick="javascript:editaConceptoEmbebido();"/></td>
</tr>
</table>
</form>
<script type="text/javascript">
 
 function editaConceptoEmbebido(){
	 		form1 = document.<portlet:namespace />conceptos_embebidos;
	 		
	 		var obj;								
			for(a=0;a<form1.elements.length;a++){
				obj = form1.elements[a];				
				if(obj.type=="text"&&!IsNumeric(obj.value)){
					alert('<liferay-ui:message key="importe-numerico" />');	
					return false;
				}								
			}
	 		
	 		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_concepto_embebido';
			url += '&portlet_name=<%=portlet_name%>';
			url += '&rnd=' + Math.floor(Math.random()*100);						
						
//			jQuery('#<portlet:namespace />busquedaChequeDiv').load(url,{concepto:jQuery(form1).serialize()}, function() {
			jQuery('#<portlet:namespace />busquedaComprobDiv').load(url,{concepto:jQuery(form1).serialize()}, function() {				
					cerrarPopupRecalcularImportes();										
				  }
			);
			
  }
 </script>	