<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<% 

String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "liquidaciones";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
Calendar fechaFin = CalendarFactoryUtil.getCalendar();

fechaInicio.add(Calendar.MONTH, -1);
Calendar current = CalendarFactoryUtil.getCalendar();

Integer ini = (Integer)request.getAttribute("ordenIniId");
Integer fin = (Integer)request.getAttribute("ordenFinId");

String cheque_ini = (String)request.getAttribute("chequeIniId");
String cheque_fin = (String)request.getAttribute("chequeFinId");


boolean showABMButtons = true; //PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ORDEN_PAGO_AMTIMA);


List<OrdenPago> ordenes = (ArrayList<OrdenPago>)request.getSession().getAttribute(WebKeysLiquidaciones.ORDENES_PAGO);
int cantidad = ordenes.size();

List<String> fallas = (List<String> )request.getAttribute(WebKeysLiquidaciones.ORDENES_PAGO_ARCHIVOS_FALLAS);
List<String> duplicados = (List<String> )request.getAttribute(WebKeysLiquidaciones.ORDENES_PAGO_ARCHIVOS_DUPLICADOS);
List<String> sinCuit = (List<String> )request.getAttribute(WebKeysLiquidaciones.ORDENES_PAGO_ARCHIVOS_SIN_CUIT);
String opSuperada = (String)request.getAttribute("op_superada");
String caja=(String) request.getAttribute("cajaFarmacia");
String esAmtimaStr = request.getParameter("esAmtima");
boolean esAmtima = false;
if (esAmtimaStr != null && esAmtimaStr.equals("true")) {
	esAmtima = true;
}
List<CuentaBancaria> ctas = (List<CuentaBancaria>) request.getSession().getAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);
Cheque cheque = (Cheque) request.getAttribute("Cheques_Duplicados");

int nro_lote=OrdenPagoServiceUtil.getLoteOrdenPago();

%>
<b><%=caja%></b></br>
<% if (fallas != null && !fallas.isEmpty()){ %>
Los siguientes archivos no pudieron ser procesados:<br/>
<%
        for (String f : fallas){%>
        <%=f %><br/>
        <%}
} %>
<% if (sinCuit != null && !sinCuit.isEmpty()){ %>
Los siguientes archivos contienen farmacias para las que no se posee el cuit correspondiente:<br/>
<%
        for (String f : sinCuit){%>
        <%=f %><br/>
        <%}
} %>
<% if (opSuperada != null){ %>
La siguiente farmacia posee un anticipo que supera el importe de la OP:<br/>
<%=       
        opSuperada %><br/>
        
<%} %>
<% if (duplicados != null && !duplicados.isEmpty()){ %>
Los siguientes archivos ya fueron procesados con anterioridad<br/>
<%
        for (String f : duplicados){%>
        <%=f %><br/>
        <%}
} %>
<liferay-ui:error exception="<%=ar.com.ospim.liquidaciones.DuplicateNumeroChequeException.class %>" message="duplicate-cheque-uploaded-op" />
<liferay-ui:error exception="<%= ar.com.ospim.liquidaciones.ChequeSinChequeraException.class %>" message="cheque-sin-chequera" />
<liferay-ui:error exception="<%=ar.com.ospim.liquidaciones.AnticipoSuperaImporteOPException.class %>" message="anticipo-supera-importe-op" />
<% if (cheque!= null) { %>
	<span class="portlet-msg-error">Numero de cheque existente: <%=cheque.getNumero().toString()%></span>
<%} %>
<form action="" method="post"  name="<portlet:namespace />ordenesAImprimir" >
<portlet:defineObjects />
<table class="lfr-table">
	<tr>
		<% if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_LIQ_1_")) {%>
			<td>
				<span id="<portlet:namespace />spanctabcria">
					<liferay-ui:message key="cuenta-bancaria"/>:
				</span>
			</td>
			
			<td>
				<span id="<portlet:namespace />spanctabcria2">
					<select id="<portlet:namespace />id_cta_bcria" name="<portlet:namespace />id_cta_bcria" onchange="sugerirNroCheque()">
						<option value="0" selected>Seleccione Cta. Bcria.</option>
						<% 	for (CuentaBancaria cta : ctas) { 
								if (cta.getEntidad().equals("O")) {%>
									<option value="<%=cta.getId_cuenta_bcria()%>"><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
								<%	 }
						} %>
					</select>
				</span>
							
			</td>
		<%}%>
		<td align="right" width="20%">Completar con cheques a partir del nro:</td>
		<td align="left" width="10%"><input id="cheque_numero_ini" size="15" maxlength="11" type="text"	onkeydown="allowOnlyDigits(event)"/></td>
		<td width="20%" align="left">
			<a href="#" onclick="javasript:llenarChequesAPartirDelNro(); return false;">Aplicar</a>&nbsp;
			<a href="#" onclick="javasript:llenarChequesAPartirDelNro(1); return false;">Borrar Nros.Cta. Selecc.</a>&nbsp;
		</td>
		<td align="right"><b><%if(nro_lote>0){%><liferay-ui:message key="lote-actual" />:<input type="text" size="6" id="<portlet:namespace />nro_lote" name="<portlet:namespace />nro_lote"  value="<%=nro_lote%>"/><%}%></b></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<%if (ini == null && fin==null){%>
			<td colspan="6" align="center"><input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />salvar();return false;"/></td>
		<%}%>
		<%if (ini != null && fin!=null){%>
			<td colspan="3" align="center"><input type="button" value="<liferay-ui:message key="imprimir-op" />" onClick="<portlet:namespace />imprimirOp();return false;"/></td>
			<td colspan="3" align="center"><input type="button" value="<liferay-ui:message key="imprimir-cheques" />" onClick="<portlet:namespace />imprimirCheques();return false;"/></td>
		<%}%>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
</table>
<script type="text/javascript">
 		 function imprimir( nro){
			window.location.href ="/pdfservlet/?accion=ordenPago&id_orden_pago="+nro ;
 		 }

 		function verificarCheques(){
 			var cant = <%=cantidad%>;
 	 		cant = -1 * cant;
	 	 	var i = 0;
			while (i != cant){
				i--;
				var el = document.getElementById("cheque_nro_" + i);
				if (el != null){
					if (trim(el.value) == ""){
						alert("Debe completar el número de cheque para todas las ordenes de pago");
						el.focus();
						return false; 
					} 
				}
			} 	 	 	
			return true;
 	 	}

 		function calcularTotal(indice){ 			
	 	 	var imp = document.getElementById("importe_" + indice);
	 	 	var importeOrig = document.getElementById("importe_orig_" + indice).value;
	 	 	var dcto = document.getElementById("dcto_" + indice).value;
	 	 	var dcto_drog = document.getElementById("dcto_drog_" + indice).value;	 	 	
	 	 	imp.value = ((Math.round((importeOrig- Math.round((parseFloat(dcto) * importeOrig / 100)*100 - 0.5)/100  - parseFloat(dcto_drog))*100) /100)).toFixed(2);
 	 	}
 	 	
		 function <portlet:namespace />salvar(){
			 if (verificarCheques()){
			 
			 <% if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_LIQ_1_")) {%>			 
				 url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/salvar_uploaded_ordenes_pago' /></portlet:actionURL>";
			 <%}else{%>
			 	 url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/farmacia/salvar_uploaded_ordenes_pago' /></portlet:actionURL>";
			 <%}%>		
					submitForm(document.<portlet:namespace />ordenesAImprimir, url);				
					return true;
			 }
		 }
		 function <portlet:namespace />imprimirOp(){
		 	<%if(null!=ini && null!=fin){%>
				window.location.href ="/pdfservlet/?accion=ordenPagoOspimFarmacia&id_ini=<%= ini.toString()%>&id_fin=<%= fin.toString()%>";
			<%}%>
		 }		 
		 function <portlet:namespace />imprimirCheques(){
			<%if(null!=cheque_ini && null!=cheque_fin){%>
				window.location.href ="/odtservlet/?accion=chequeOspimFarmacia&ordenIniId=<%=ini.toString()%>&ordenFinId=<%=fin.toString()%>";
			<%}%>
		 }
		 
		 function sugerirNroCheque(){		 	
			var id = document.getElementById("<portlet:namespace />id_cta_bcria").value;
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_ultimo_nro_cheque&id_cta_bcria='+id;
			
			 <% if (esAmtima) {%>
			    	url += '&esAmtima=esAmtima';
		    <%}%>
			 url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery.ajax({   
				url: url,
				success: function(data){	
					var obj = jQuery.parseJSON(data);					
					jQuery("#cheque_numero_ini").val(parseInt(obj.numero) +1); 
				}
			});
		
	}
		 
		<%if (ini != null && fin!=null){%>
			alert("Se generaron las OP del:<%= ini.toString()%> al <%= fin.toString()%>");			
		<%}%>
</script>
			<%
				SearchContainer searchContainer = null;
					PortletURL portletURL = renderResponse.createRenderURL();
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
					 		List<String> headerNames = new ArrayList<String>();
					 		headerNames.add("codigo");
					 		headerNames.add("cuit");
					 		headerNames.add("a-nombre-de");
					 		headerNames.add("");
					 		headerNames.add("fecha");
					 		headerNames.add("periodo");					 		
					 		headerNames.add("importe-original");
					 		headerNames.add("importe-original-ospim-amtima");
					 		headerNames.add("importe");
					 		headerNames.add("beneficio-porcent");
					 		headerNames.add("descuento-por-drog");
					 		headerNames.add("anticipos");
					 		/*headerNames.add("tipo-pago");
					 		headerNames.add("cuenta-bancaria");					 		
					 		headerNames.add("cheque-cbu");
					 		headerNames.add("seccional");					 		
					 		headerNames.add("destino");
					 		headerNames.add("obs-interna");*/
					 		
					 		
					searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-ordenes-pago-were-found"));			
					 		String nrocheque = "";
					if(null!=ordenes){
				 				//Seteo el total de la lista.
					 	int total = ordenes.size();
					 	searchContainer.setTotal(total);
					 	
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < ordenes.size(); i++) {
					 		OrdenPago op = (OrdenPago) ordenes.get(i);
							
				 					ResultRow row = new ResultRow(op, "", i);
				 					ResultRow subRow = new ResultRow(op,"", i, true);	
				 					if (op.getId() <= 0) {				 						
				 						nrocheque = "";
				 						if (op.getFormaPago() != null && op.getFormaPago().size() >0){
				 							if(null==op.getCBUAsString()){
				 								nrocheque = ((Cheque)op.getFormaPago().get(0).getPago()).getNumeroStr();
				 							}else{
				 								nrocheque = ((PagoBancario)op.getFormaPago().get(0).getPago()).getNumeroStr();
				 							}				 							
				 						}
				 						StringBuilder sb=new StringBuilder();
				 						row.addText(op.getItems().get(0).getCodigoPrestador());
				 						sb=new StringBuilder();
						 				sb.append("<input type=\"text\" size=\"9\" value=\"").append(op.getAcreedor().getCuit()).append("\" size=\"11\" maxlength=\"11\" id=\"cuit_de_").append(op.getId()).append("\" name=\"cuit_de_").append(op.getId()).append("\" readonly />");
						 				sb.append("<input type=\"hidden\" value=\"").append(op.getAcreedor().getSucursal()).append("\" id=\"sucur_de_").append(op.getId()).append("\" name=\"sucur_de_"+op.getId()+"\" readonly />");
						 				row.addText(sb.toString());						 				
						 				row.addText("<input type=\"text\" value=\""+op.getAFavorDe()+"\" size=\"22\" maxlength=\"50\" id=\"a_favor_de_" + op.getId() + "\" name=\"a_favor_de_" + op.getId() + "\" />");
				 						
						 				sb=new StringBuilder();
						 				sb.append("<input type=\"button\" value=\"Editar\" onClick=\"javascript:verInfoEmpresa('").append(op.getAcreedor().getCuit()).append("','").append(op.getAcreedor().getSucursal()).append("',").append(op.getId()).append(");\"/>");
						 				//sb.append("<input type=\"button\" onClick=\"javascript:alert('hola');\" style=\"height: 10px; width: 10px\" src=\"").append(themeDisplay.getPathThemeImages()).append("/common/edit.png\"/>");
						 				row.addText(sb.toString());
						 				
						 				row.addText(op.getAlta_fechaAsString());
						 				row.addText(op.getFechaHastaMesAnio());
						 				
						 				row.addText(op.getImporteDeItemsPVP().toString()+"<input type='hidden' id='importe_orig_"+op.getId()+"' value='"+op.getImporteDeItemsPVP().toString()+"'/>");
						 				row.addText(op.getImporteDeItems().toString()+"<input type='hidden' id='importe_orig_"+op.getId()+"' value='"+op.getImporteDeItems().toString()+"'/>");
						 				BigDecimal doubleDcto=BigDecimal.ZERO;
						 				if(op.getBaseDescuentoFarmacia().equals("PVP")){
						 					doubleDcto=op.getDescuento()!=null?op.getImporteDeItemsPVP().multiply(op.getDescuento().divide(new BigDecimal(100))):new BigDecimal(1);
						 				}else{
						 					doubleDcto=op.getDescuento()!=null?op.getImporteDeItems().multiply(op.getDescuento().divide(new BigDecimal(100))):new BigDecimal(1);
						 				}
						 				BigDecimal importeCondescuento=op.getImporteDeItems().subtract(doubleDcto);
						 				importeCondescuento = importeCondescuento.setScale(2, BigDecimal.ROUND_DOWN);
						 				row.addText("<input id='importe_"+op.getId()+"' size='7' name='importe_"+op.getId()+"' type=\"text\" readonly='readonly' value='"+importeCondescuento.toString()+"'");
						 				
						 				String dcto = "";
						 				String dctoDrogueria = "";
						 				String anticipo="";
						 				if (op.getDescuento() != null){
						 					dcto = op.getDescuento().toString();
						 				}						 				
						 				if (op.getDescuentoDrogueria() != null){
						 					dctoDrogueria = op.getDescuentoDrogueria().toString();
						 				}else{
						 					dctoDrogueria = "0";
						 				}
						 				if(op.getTotalAnticipos()!=null){
						 				    anticipo=op.getTotalAnticipos().toString();
						 				}
						 				row.addText("<input type=\"text\" size=\"3\" maxlength=\"9\" id=\"dcto_" + op.getId() + "\" name=\"dcto_" + op.getId() + "\" value='"+(dcto.trim().length()>0?dcto:"0")+"' onchange='calcularTotal("+op.getId()+")'/>");
						 				row.addText("<input type=\"text\" size=\"6\" maxlength=\"9\" id=\"dcto_drog_" + op.getId() + "\" name=\"dcto_drog_" + op.getId() + "\" value='"+dctoDrogueria+"' onchange='calcularTotal("+op.getId()+")'/>");
						 				String anticipoString="<input type=\"text\"  size=\"6\" maxlength=\"9\" id=\"ant_" + op.getId() + "\" name=\"ant_" + op.getId() + "\" value='0'/>";
						 				if(anticipo!=null && !anticipo.trim().equals("0")){
						 					anticipoString=anticipoString+"(-"+anticipo+")";
						 				}
						 				
						 				row.addText(anticipoString);	
						 				//A PARTIR DE ACA NUEVA LINEA?						 				
						 				subRow.addText("<img src=\"/html/images/flecha_arbol.png\" style=\"width: 20px; height: 20px\"/>");
						 				
						 				sb=new StringBuilder();
				 						sb.append("<font size=\"1\" color=\"black\">Forma Pago</font><select id=\"tipo_pago_").append(op.getId()).append("\" name=\"tipo_pago_").append(op.getId()).append("\" onchange=\"changeTipo('").append(op.getId()).append("');\">");				 						
				 						sb.append("<option value=\"").append(Cheque.class.getName()).append("\">Cheque</option>");
				 						sb.append("<option value=\"").append(PagoBancario.class.getName()).append(PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA).append("\"");
				 						if(null!=op.getCBU()&&!"".equals(op.getCBU().trim())){
				 							sb.append(" selected ");
				 						}
				 						sb.append("\">Transf. Bcria.</option>");
				 						sb.append("</select>");
				 						
				 						subRow.addText(sb.toString());
				 						
				 						sb=new StringBuilder();
				 						sb.append("<font size=\"1\" color=\"black\">Cta. Bcria</font><select id=\"id_cta_bcria_").append(op.getId()).append("\" name=\"id_cta_bcria_").append(op.getId()).append("\" onchange=\"sugerirNroCheque()\">");
				 						sb.append("<option value=\"0\" selected>Seleccione Cta. Bcria.</option>");
										for (CuentaBancaria cta : ctas) { 
											if (cta.getEntidad().equals("O")) {
												sb.append("<option value=\"").append(cta.getId_cuenta_bcria()).append("\"");
												if(null!=op && null==op.getPagos() && cta.getId_cuenta_bcria()==2){
													sb.append(" selected ");
												}else if(null!=op && null!=op.getPagos() && op.getPagos().get(0).getCuentaBancaria().getId_cuenta_bcria()==cta.getId_cuenta_bcria()){
													sb.append(" selected ");
												}
												sb.append("\">").append(cta.getDescripcion()).append(" ").append(cta.getNro_cuenta()).append("/").append(String.valueOf(cta.getSucursal())).append("</option>");
												
											}
											
										} 
										sb.append("</select>");
										subRow.addText(sb.toString());
				 						
				 										 						
				 						
										subRow.addText("");
										sb=new StringBuilder();
										sb.append("<font size=\"1\" color=\"black\">CBU/Cheque</font><input type=\"text\" size=\"19\" id=\"cheque_nro_").append(op.getId()).append("\" name=\"cheque_nro_").append(op.getId()).append( "\" value='");
										if(null!=op.getCBU()&&!"".equals(op.getCBU().trim())){
											sb.append(!nrocheque.equals("")?nrocheque:op.getCBU());
										}else{
											sb.append(nrocheque);
										}
										sb.append("'/> ");																				
										
										subRow.addText(sb.toString());
						 				String destino=op.getDestino()!=null?op.getDestino():"";
						 				String obsInt=op.getObsInterna()!=null?op.getObsInterna():"";
						 				subRow.addText("<font size=\"1\" color=\"black\">Destino</font><input type=\"text\"  size=\"10\" maxlength=\"200\" id=\"destino_" + op.getId() + "\" name=\"destino_" + op.getId() + "\" value='"+destino+"'/>");
						 				subRow.addText("<font size=\"1\" color=\"black\">Sel.Seccional</font><input type=\"button\" value=\"Dest. Secc.\" onclick=\"buscarSeccionalFarm('"+op.getId()+"');\"/>");
						 				subRow.addText("<font size=\"1\" color=\"black\">Observación</font><input type=\"text\"  size=\"10\" maxlength=\"200\" id=\"obs_int_" + op.getId() + "\" name=\"obs_int_" + op.getId() + "\" value='"+obsInt+"'/>");						 				
						 				subRow.addText("");
						 				subRow.addText("");	
						 				subRow.addText("<input type=\"hidden\" id=\"cbu_sugerido_" + op.getId() + "\" name=\"cbu_sugerido_" + op.getId() + "\" value=\""+op.getCBU()+"\"/>");
				 						subRow.addText("<input type=\"hidden\" id=\"email_cbu_" + op.getId() + "\" name=\"email_cbu_" + op.getId() + "\" value=\""+op.getEmailCBU()+"\"/>");
						 				
				 					} else {
				 						row.addText(op.getItems().get(0).getCodigoPrestador());
				 						row.addText(op.getAcreedor().getCuit());
				 						row.addText(op.getFormaPago().get(0).getANombreDe() == null ? "" : op.getFormaPago().get(0).getANombreDe());
				 						row.addText("");
				 						row.addText(op.getAlta_fechaAsString());
						 				row.addText(op.getFechaHastaMesAnio());
						 				
						 				row.addText(op.getImporteDeItemsPVP().toString());
						 				row.addText(op.getImporteDeItems().toString());
						 				row.addText("0");
						 				
						 				row.addText(op.getDescuento().toString());
						 				row.addText(op.getDescuentoDrogueria().toString());
						 				row.addText(op.getTotalAnticiposAsString());
						 				
						 				
						 				//A PARTIR DE ACA NUEVA LINEA?						 				
						 				subRow.addText("<img src=\"/html/images/flecha_arbol.png\" style=\"width: 20px; height: 20px\"/>");
						 				
						 				if(null!=op.getNroChequeAsString()){
						 					subRow.addText("Cheque");
						 					subRow.addText("<font size=\"1\" color=\"black\">Cheque</font> "+op.getNroChequeAsString());
						 				}else{
						 					subRow.addText("Transferencia Bancaria");
						 					subRow.addText("<font size=\"1\" color=\"black\">CBU</font> "+op.getCBUAsString());
						 				}
			 						

				 						
										subRow.addText("");
																								
						 				String destino=op.getDestino()!=null?op.getDestino():"";
						 				String obsInt=op.getObsInterna()!=null?op.getObsInterna():"";
						 				subRow.addText(op.getDestino()!=null?op.getDestino():"");
						 				subRow.addText(op.getObsInterna()!=null?op.getObsInterna():"");						 				
						 				subRow.addText("");
						 				subRow.addText("");
						 				subRow.addText("");						 								
						 				
				 					}
				 			resultRows.add(row);
				 			resultRows.add(subRow);
					 	}
				 	}
			%>
 		
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
</form>

<script type="text/javascript">
var popup;
function buscarSeccionalFarm(valor) {
	
    popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-seccionales" />",modal:true,width:420});
       
    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/buscar_seccional&farmaciaop=true&idOP='+valor;
    
	jQuery(popup).load(url);    
}

function <portlet:namespace />cerrarSecc(){	
	<portlet:namespace />cerrarDivSecc();
	if(popup){		
		Liferay.Popup.close(popup);
	}
}
function <portlet:namespace />cerrarDivSecc(){
	jQuery("#divSeccional").hide("slow");		
}

function verInfoEmpresa(cuit_empleador, sucu_empleador, id_op) {			
    popup = Liferay.Popup({title:"<liferay-ui:message key="ver-info-empresa" />",modal:true,width:1200});
    var cuitEntidad = jQuery("#<portlet:namespace />cuit_entidad").val();
	var sucuEntidad = jQuery("#<portlet:namespace />sucursal_entidad").val();	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_empleadores_entry&cuit='+cuit_empleador+'&sucursal='+sucu_empleador+'&idOp='+id_op;

	jQuery(popup).load(url);
}		

function submitFormNotSavePOP(solapa){			
	if (<portlet:namespace />validarCampos()) {
		document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "CAMBIO_SOLAPA";
		document.getElementById("cambioSolapa").value="cambioSolapa";
		if(solapa=="datos"){				
			document.getElementById("tabs1").value="datos-fiscales";
		}else{
			document.getElementById("tabs1").value="datos";
		}
		
		var form = jQuery(document.<portlet:namespace />emple);
		var url = '<portlet:actionURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/editar_empleadores_entry';
					
		
		form.ajaxForm(
			{
				url: url,
		    	target: popup,//".ui-dialog-content",//poopup
		        type: "POST",
		        beforeSubmit: function() {			        
		        },
		        success: function() {
		        }
		    }
		);	
					
		form.submit();    
	}
}		

function changeTipo(idOp){	
	var tipo_pago=jQuery("#tipo_pago_"+idOp).val();	
	if(tipo_pago=='<%=Cheque.class.getName()%>'){		
	}else if(tipo_pago=='<%=PagoBancario.class.getName()+PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA%>'){		
		var cbu=jQuery("#cbu_sugerido_"+idOp).val();		
		jQuery("#cheque_nro_"+idOp).val(cbu);
	}	
}

function sugerirRazonSocialChequeYDestino(idOp){
	var cuitEntidad = jQuery("#cuit_de_"+idOp).val();
	var sucuEntidad = jQuery("#sucur_de_"+idOp).val();
	var idSeccional = 0;	
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_ultima_razon_social_cheque_op';
		url += '&cuit_entidad=' + cuitEntidad;
		url += '&sucu_entidad=' + sucuEntidad;
		url += '&id_seccional=' + idSeccional;
		url += '&rnd=' + Math.floor(Math.random()*100);
		
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);			
			jQuery("#a_favor_de_"+idOp).val(obj.razon);
			jQuery("#destino_"+idOp).val(obj.destino);
			jQuery("#cbu_sugerido_"+idOp).val(obj.cbu);
			jQuery("#email_cbu_"+idOp).val(obj.email);					
		}
	});
}

function llenarChequesAPartirDelNro(borrar){		
		var cant = <%=cantidad%>;
		cant = -1 * cant;
		if (trim(document.getElementById("cheque_numero_ini").value)!= ""){
	 		var ini = document.getElementById("cheque_numero_ini").value;
	 		var cta_ini = document.getElementById("<portlet:namespace />id_cta_bcria").value;
	 		var i = 0;
			while (i != cant){
				i--;				
				var el = jQuery("#cheque_nro_" + i).val();
				var fp = jQuery("#tipo_pago_" + i).val();			
				var cta = jQuery("#id_cta_bcria_" + i).val();				
				
				if (el != null && fp=='<%=Cheque.class.getName()%>' && cta==cta_ini){					
					if(borrar==1){
						jQuery("#cheque_nro_" + i).val('');
					}else{
						jQuery("#cheque_nro_" + i).val(ini);
						ini++;
					}
					
				}
			} 	 	 	
		}
}


</script>
