<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.global.beans.Banco" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%
	String portlet_name="liquidaciones";
	if (renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}

	if(renderResponse.getNamespace().equals("_TES_1_")){
		portlet_name = "tesoreria";
	}
	
	String isFarmaciaStr = (String) request.getAttribute(WebKeysTesoreria.IS_AMTIMA);
	boolean isFarmacia = false;
	List<Banco> bancos = TraeListasServiceUtil.getBancos();
	if (isFarmaciaStr != null &&  isFarmaciaStr.equals(WebKeysTesoreria.IS_AMTIMA)){
		isFarmacia = true;
	}
	
	List<Cheque> cheques = (List<Cheque>) request
			.getAttribute(WebKeysLiquidaciones.CHEQUES_CARTERA);

	PortletURL portletURLTercerizadora = renderResponse
			.createRenderURL();
	List<String> headerNamesTercerizadora = new ArrayList<String>();
	headerNamesTercerizadora.add("numero");
	headerNamesTercerizadora.add("importe");
	headerNamesTercerizadora.add("banco");	
	headerNamesTercerizadora.add("recibo");
	headerNamesTercerizadora.add("fecha-recibo");
	headerNamesTercerizadora.add("Utilizar");

	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, portletURLTercerizadora,
			headerNamesTercerizadora, LanguageUtil.get(pageContext,
					"no-cheques-were-found"));

	int cant = 0;
	if (null != cheques) {
		int total = cheques.size();
		List resultRowsInspector = searchContainer.getResultRows();
		for (int i = 0; i < cheques.size(); i++) {
			cant++;
			Cheque cheque = cheques.get(i);
			ResultRow row = new ResultRow(cheque, cheque.getNumeroStr(), i);
			row.addText(cheque.getNumero().toString());
			row.addText(cheque.getImporte().toString());
			int index = bancos.indexOf(cheque.getBanco());
			row.addText(bancos.get(index).getDescripcion_banco());			
			row.addText(cheque.getNroRecibo());
			row.addText(cheque.getFechaReciboAsString());
			StringBuffer sb1=new StringBuffer();
			sb1.append("&nbsp;<img title=\"Utilizar cheque\" src=\"");
				sb1.append(themeDisplay.getPathThemeImages());
				sb1.append("/common/checked.png\" onClick=\"javascript:usarCheque('");
				sb1.append(cheque.getNumero());				
				sb1.append("|");
				sb1.append(cheque.getBanco().getId_banco());
				sb1.append("|");
				sb1.append(cheque.getCuentaBancaria().getId_cuenta_bcria());
				sb1.append("|");
				sb1.append(cheque.getCuit()!=null?cheque.getCuit():"");
				sb1.append("');\" />");
			row.addText(sb1.toString());
			resultRowsInspector.add(row);
		}
		searchContainer.setTotal(total);
	}

%>

<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />

<liferay-ui:search-iterator paginate="false" searchContainer="<%=searchContainer%>" />
<input type="hidden" id="cantidad_cheques" value="<%=String.valueOf(cant)%>"/>

<script type="text/javascript">

 function usarCheque(id){
	 	jQuery('#<portlet:namespace />aplicando').show();	
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/reutilizar_cheques_recibidos_terceros';
		url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';				
		url +='&idCheque=' + encodeURI(id);
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery(popup).load(url, function() {
			Liferay.Popup.close(popup);
		});
		
 }
 
 
 
</script>