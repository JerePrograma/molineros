<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<input type="button" value="<liferay-ui:message key="reutilizar-cheques" />" onClick="<portlet:namespace />aplicar();" />

<%
	String isFarmaciaStr = (String) request.getAttribute(WebKeysTesoreria.IS_AMTIMA);
	boolean isFarmacia = false;
	if (isFarmaciaStr != null &&  isFarmaciaStr.equals(WebKeysTesoreria.IS_AMTIMA)){
		isFarmacia = true;
	}

	List<CuentaBancaria> ctas = (List<CuentaBancaria>) request
			.getSession().getAttribute(
					WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);

	List<Cheque> cheques = (List<Cheque>) request
			.getAttribute(WebKeysLiquidaciones.CHEQUES_REUTILIZABLES);

	PortletURL portletURLTercerizadora = renderResponse
			.createRenderURL();
	List<String> headerNamesTercerizadora = new ArrayList<String>();
	headerNamesTercerizadora.add("numero");
	headerNamesTercerizadora.add("importe");
	headerNamesTercerizadora.add("cuenta-bancaria");
	headerNamesTercerizadora.add("a-nombre-de");
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
			int index = ctas.indexOf(cheque.getCuentaBancaria());
			row.addText(ctas.get(index).getDescripcion() + " " +ctas.get(index).getNro_cuenta()+ "/" + ctas.get(index).getSucursal());
			row.addText(cheque.getANombreDe() != null ? cheque.getANombreDe() : "");
			row.addText("<input type='checkbox' id='utilizar_cheque_"+cheque.getNumeroStr()+"_"+cheque.getCuentaBancaria().getId_cuenta_bcria()+"' name='utilizar_cheque_'/>");
			resultRowsInspector.add(row);
		}
		searchContainer.setTotal(total);
	}
	
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
	
%>

<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />

<liferay-ui:search-iterator paginate="false" searchContainer="<%=searchContainer%>" />
<input type="hidden" id="cantidad_cheques" value="<%=String.valueOf(cant)%>"/>

<script type="text/javascript">

 function <portlet:namespace />aplicar(){
	 	jQuery('#<portlet:namespace />aplicando').show();
		var cantidad_cheques = document.getElementById("cantidad_cheques");
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/reutilizar_cheques_orden_pago_ospim';
		<% if (isFarmacia) { %>
			url += '&esAmtima=esAmtima';
		<%}%>
		
		var elementos = document.getElementsByName("utilizar_cheque_");
		for (var i = 0;  i < elementos.length; i++){
			if (elementos[i].checked){
				 url+="&utilizar_cheque_" + i + "=";
				 url+=elementos[i].id;
			}
		}
		 
		url += '&cantidad_cheques=' + cantidad_cheques.value;
		url += '&accion=reutilizar';
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery(popup).load(url, function() {
			Liferay.Popup.close(popup);
		});
	 }
	
 	jQuery('#<portlet:namespace />aplicando').hide();
</script>