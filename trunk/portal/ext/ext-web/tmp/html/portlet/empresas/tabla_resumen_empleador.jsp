<%@ include file="/html/portlet/empresas/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
Empresa empresa = (Empresa)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);
LlamadosEstudio llest=null;
ActaAcuerdoSeguimientoResumen resumen =null;
try{
 llest =(LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
 resumen = LlamadoServiceUtil.getDesglosePagosActasConvenios(empresa.getCuit());
}catch(Exception e){
	
}
List<RamoEmpresa> ramos = (ArrayList<RamoEmpresa>) portletSession
.getAttribute(WebKeysEmpresas.RAMOS_EMPRESA_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

boolean esEdicion = true;
String idOp=(String)renderRequest.getAttribute("idOp");
String prefijo="empre_";
%>

<table width="100%">
<tr>
	<td>
		<a href="javascript:<portlet:namespace />showHideDivDatosGral();">
		<legend>
			<liferay-ui:message	key="data" /> (<%=null!=empresa?1:""%>)
			<img name="arrow_datos_gral" id="<portlet:namespace />arrow_datos_gral" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
		</legend>
		</a>
	</td>	
	<%-- <td>
		<a href="javascript:<portlet:namespace />showHideDivDomicilios();">
		<legend>
			<liferay-ui:message	key="domicilios" /> (<%=null!=empresa&&null!=empresa.getDomicilios()?empresa.getDomicilios().size():""%>)
			<img name="arrow_domicilios" id="<portlet:namespace />arrow_domicilios" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
		</legend>
		</a>
	</td> --%>
	<%-- <td>
		<a href="javascript:<portlet:namespace />showHideDivContactos();">
						<legend>
							<liferay-ui:message	key="address-book" /> (<%=null!=empresa&&null!=empresa.getContactos()?empresa.getContactos().size():""%>)
							<img name="arrow_contactos" id="<portlet:namespace />arrow_contactos" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
						</legend>
		</a>
	</td> --%>
	<td>
		<a href="javascript:<portlet:namespace />showHideDivGestiones();" align="left">
				<legend>
					<liferay-ui:message key="Gestiones" /> (<span id="<portlet:namespace />cantidad_gestiones"><%=null!=llest&&null!=llest.getLlamados()?llest.getLlamados().size():""%></span>)
					<img name="arrow_gestiones" id="<portlet:namespace />arrow_gestiones" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
				</legend>
		</a>
	</td>
	<td>
		<a href="javascript:<portlet:namespace />showHideDivCtasBcrias();">
			<legend>
				<liferay-ui:message
					key="cuentas-bancarias" /> (<%=null!=empresa&&null!=empresa.getCuentasBcrias()?empresa.getCuentasBcrias().size():"0"%>)
					<img name="arrow_cta_bcria" id="<portlet:namespace />arrow_cta_bcria" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
					<!--onClick=\"javascript:editaCuenta('");-->
			</legend>			 
		</a>
	</td>
	<td>
		<a href="javascript:<portlet:namespace />showHideDivCalculoDeuda();" align="left">
				<legend>
					<liferay-ui:message key="calculos-deuda" /> (<span id="<portlet:namespace />cantidad_deudas"><%=null!=llest?llest.getCantDeudas():""%></span>)
					<img name="arrow_calculos" id="<portlet:namespace />arrow_calculos" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>					
				</legend>
		</a>
	</td>
</tr>
<tr><td colspan="5">&nbsp;</td></tr>
<tr>	
	<td>
		<a href="javascript:<portlet:namespace />showHideDivActa(0,0);" align="left">
				<legend>
					<liferay-ui:message key="actas" /> (<span id="<portlet:namespace />cantidad_actas"><%=null!=llest?llest.getCantActas():""%></span>) y
					<liferay-ui:message key="acuerdos" /> (<span id="<portlet:namespace />cantidad_acuerdos"><%=null!=llest?llest.getCantConvenios():""%></span>)
					<img name="arrow_actas_acuerdos" id="<portlet:namespace />arrow_actas_acuerdos" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
				</legend>
		</a>
	</td>	
	<td>		
		<a href="javascript:<portlet:namespace />showHideDivRecibos();" align="left">
				<legend>
					<liferay-ui:message key="recibos" /> (<span id="<portlet:namespace />cantidad_recibos"><%=null!=llest?llest.getCantRecibos():""%></span>)
					<img name="arrow_recibos" id="<portlet:namespace />arrow_recibos" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
				</legend>
		</a>
	</td>
	<td>
		<a href="javascript:<portlet:namespace />showHideDivCheques();" align="left">
				<legend>
					<liferay-ui:message key="cheques" />
					<img name="arrow_cheques" id="<portlet:namespace />arrow_cheques" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
				</legend>
		</a>
	</td>	
	<td colspan="2">&nbsp;</td>
</tr>
<tr>	

	<td><legend>|- <a href="javascript:<portlet:namespace />showHideDivActa('ACTA',2);">OSPIM:&nbsp;&nbsp;&nbsp;Actas (<%=llest.getCantActas(WebKeysGlobal.OSPIM)%>)&nbsp;<%if (llest.getCantActas(WebKeysGlobal.OSPIM)>0){ %>Saldo <%=llest.getSaldoActasAsString(WebKeysGlobal.OSPIM)%><%}%></a>
	       <a  style="color:#FF0000" > Atr: $<%= resumen.getActasPagosAtrasadosOspim()%> </a>&nbsp;&nbsp;
	       <a  style="color:green" > A Cobr: $<%= resumen.getActasPagosACobrarOspim()%> </a>
	    </legend></td>		
	<td><legend>|- OSPIM (<%=llest.getCantRecibos(WebKeysGlobal.OSPIM)%>)&nbsp;<%if (llest.getCantRecibos(WebKeysGlobal.OSPIM)>0){%> Importe <%=llest.getImporteRecibosAsString(WebKeysGlobal.OSPIM)%><%}%></legend></td>
	<td><legend>|- Rechazados (<%=llest.getCantChequesRechazados()%>)&nbsp;<%if (llest.getCantChequesRechazados()>0){%>Saldo <%=llest.getImporteChequesRechazadosAsString()%><%}%></legend></td>
	<td colspan="2">&nbsp;</td>
</tr>
<tr>					
		<td><legend>|&nbsp;&nbsp;&nbsp;|--- <a href="javascript:<portlet:namespace />showHideDivActa('CONVENIO',2);">Acuerdos (<%=llest.getCantConvenios(WebKeysGlobal.OSPIM)%>)&nbsp;&nbsp;<%if (llest.getCantConvenios(WebKeysGlobal.OSPIM)>0){ %> Saldo <%=llest.getSaldoConveniosAsString(WebKeysGlobal.OSPIM)%><%}%></a>
		        <a  style="color:#FF0000" > Atr: $<%= resumen.getConveniosPagosAtrasadosOspim()%> </a>&nbsp;&nbsp;
		        <a  style="color:green" > A Cobr: $<%= resumen.getConveniosPagosACobrarOspim()%> </a>
		    </legend></td>							
		<td><legend>|- UOMA (<%=llest.getCantRecibos(WebKeysGlobal.UOMA)%>)&nbsp;<%if (llest.getCantRecibos(WebKeysGlobal.UOMA)>0){ %>Importe <%=llest.getImporteRecibosAsString(WebKeysGlobal.UOMA)%><%}%></legend></td>
		<td><legend>|- En Cartera (<%=llest.getCantChequesCartera()%>)&nbsp;<%if (llest.getCantChequesCartera()>0){ %>Saldo <%=llest.getImporteChequesCarteraAsString()%><%}%></legend></td>
		<td colspan="2">&nbsp;</td>	
</tr>
<tr>
		<td><legend>|- <a href="javascript:<portlet:namespace />showHideDivActa('ACTA',1);">UOMA:&nbsp;&nbsp;&nbsp;&nbsp;Actas(<%=llest.getCantActas(WebKeysGlobal.UOMA)%>) <%if (llest.getCantActas(WebKeysGlobal.UOMA)>0){ %> Saldo <%=llest.getSaldoActasAsString(WebKeysGlobal.UOMA)%><%}%></a>
		               <a  style="color:#FF0000" > Atr: $<%= resumen.getActasPagosAtrasadosUoma()%> </a>&nbsp;&nbsp;
		               <a  style="color:green" > A Cobr: $<%= resumen.getActasPagosACobrarUoma()%> </a>
		    </legend></td>
		<td><legend>|- AMTIMA (<%=llest.getCantRecibos(WebKeysGlobal.AMTIMA)%>)&nbsp;<%if (llest.getCantRecibos(WebKeysGlobal.AMTIMA)>0){ %>Importe <%=llest.getImporteRecibosAsString(WebKeysGlobal.AMTIMA)%><%}%></legend></td>
		<td colspan="3"><legend>|- Reemp.X Rech. (<%=llest.getCantReemplazadosRechazo()%>)&nbsp;<%if (llest.getCantReemplazadosRechazo()>0){ %>Saldo <%=llest.getImporteReemplazadosRechazoAsString()%><%}%></legend></td>
</tr>
<tr>
		<td colspan="2"><legend>|&nbsp;&nbsp;&nbsp;|--- <a href="javascript:<portlet:namespace />showHideDivActa('CONVENIO',1);">Acuerdos (<%=llest.getCantConvenios(WebKeysGlobal.UOMA)%>)&nbsp;&nbsp;<%if (llest.getCantConvenios(WebKeysGlobal.UOMA)>0){ %> Saldo <%=llest.getSaldoConveniosAsString(WebKeysGlobal.UOMA)%><%}%></a>
		                <a  style="color:#FF0000" > Atr: $<%= resumen.getConveniosPagosAtrasadosUoma()%> </a>&nbsp;&nbsp;
		                <a  style="color:green" > A Cobr: $<%= resumen.getConveniosPagosACobrarUoma()%> </a>
		               </legend>
		
			<td colspan="3">
				<%if(llest.getCantCanjeadosSinDepo() >0){%>
				<legend>|- Canjeados sin Depo. (<%=llest.getCantCanjeadosSinDepo()%>)&nbsp;Saldo <%=llest.getImporteCanjeadosSinDepoAsString()%></legend>
				<%}%>
			</td>				
</tr>
<tr>	
	<td colspan="5"><legend>|- <a href="javascript:<portlet:namespace />showHideDivActa('ACTA',3);">AMTIMA:&nbsp;Actas(<%=llest.getCantActas(WebKeysGlobal.AMTIMA)%>) <%if (llest.getCantActas(WebKeysGlobal.AMTIMA)>0){ %> Saldo <%=llest.getSaldoActasAsString(WebKeysGlobal.AMTIMA)%><%}%></a>
	                <a  style="color:#FF0000" > Atr: $<%= resumen.getActasPagosAtrasadosAmtima()%> </a>&nbsp;&nbsp;
	                <a  style="color:green" > A Cobr: $<%= resumen.getActasPagosACobrarAmtima()%> </a>
	                </legend></td>
</tr>	
<tr>
	<td colspan="5"><legend>|&nbsp;&nbsp;&nbsp;|--- <a href="javascript:<portlet:namespace />showHideDivActa('CONVENIO',3);">Acuerdos (<%=llest.getCantConvenios(WebKeysGlobal.AMTIMA)%>) <%if (llest.getCantConvenios(WebKeysGlobal.AMTIMA)>0){ %>  Saldo <%=llest.getSaldoConveniosAsString(WebKeysGlobal.AMTIMA)%><%}%></a>
	    <a  style="color:#FF0000" > Atr: $<%= resumen.getConveniosPagosAtrasadosAmtima()%> </a>&nbsp;&nbsp;
	    <a  style="color:green" > A Cobr: $<%= resumen.getConveniosPagosACobrarAmtima()%> </a>
	</legend></td>
</tr>
</table>