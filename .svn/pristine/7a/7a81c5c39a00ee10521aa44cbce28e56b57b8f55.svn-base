<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>

<%

String tabs1 = ParamUtil.getString(request, "tabs1", null);

boolean showArchivosUOMA = PermissionUtil.userContainsRole(user, WebKeysUOMA.ROL_ABM_UOMA_ARCHIVOS);
boolean showUnidadOperativa = PermissionUtil.userContainsRole(user, WebKeysUOMA.ROL_ABM_UNIDAD_OPERATIVA);
boolean showOrdenPagoUOMA = PermissionUtil.userContainsRole(user, WebKeysUOMA.ROL_ABM_ORDEN_PAGO_UOMA);
boolean showCalculoDeuda = PermissionUtil.userContainsRole(user,"ABM_CalculoDeuda");
boolean showCheques = PermissionUtil.userContainsRole(user,"ABM_Cheques");
boolean showActaUOMA = PermissionUtil.userContainsRole(user, "Ver_Acta_UOMA"); //WebKeysUOMA.VER_SOLAPA_ACTA_UOMA);
boolean showConvenioUOMA = PermissionUtil.userContainsRole(user, WebKeysUOMA.VER_SOLAPA_CONVENIO_UOMA);
boolean showCajaChica = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_ADMINISTRADOR_CAJA_CHICA) 
							|| PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_USUARIO_CAJA_CHICA)  ;
boolean showReportesUOMA = PermissionUtil.userContainsRole(user, WebKeysUOMA.VER_REPORTES_UOMA);
boolean showABMCentroCostoUOMA = PermissionUtil.userContainsRole(user, WebKeysUOMA.ROL_ABM_CENTRO_COSTO_UOMA);
boolean showTABLEROCentroCostoUOMA = PermissionUtil.userContainsRole(user, WebKeysUOMA.ROL_TABLERO_CENTRO_COSTO_UOMA);
boolean showTSUBIR_PARITARIAS = PermissionUtil.userContainsRole(user, WebKeysUOMA.SUBIR_PARITARIAS);

boolean showABMProveedores = PermissionUtil.userContainsRole(user, WebKeysUOMA.ROL_PROVEEDORES);

boolean showABMFacturacion = PermissionUtil.userContainsRole(user, WebKeysUOMA.ROL_FACTURACION);
boolean showInterbanking = PermissionUtil.userContainsRole(user, WebKeysGlobal.ROL_INTERBANKING);
boolean showCtaCteEmpresa = PermissionUtil.userContainsRole(user, "CUENTA_CORRIENTE_EMPRESAS");


if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}
if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}
if (tabs1 == null){
	if(showUnidadOperativa){
		tabs1="unidad-operativa";
	}else if(showOrdenPagoUOMA){
		tabs1="comprobantes";
	}else if(showArchivosUOMA){
		tabs1="subir-archivo";
	}else if(showCajaChica){
		tabs1="adm-caja-chica";
	}else if(showReportesUOMA){
		tabs1="reportes";
	}else if(showABMCentroCostoUOMA){
		tabs1="centro-costo";
	}else if(showABMProveedores){
		tabs1="proveedores";
	}else if(showABMFacturacion){
		tabs1="facturacion";
	}else if(showInterbanking){
		tabs1="interbanking";
	}else if(showCtaCteEmpresa){
		tabs1="ctacte-empresa";
	}	
}

if (tabs1 != null){
	if(!tabs1.contains("unidad-operativa")
	&&!tabs1.contains("ordenes-pago")&&!tabs1.contains("comprobantes")&&!tabs1.contains("reportes")&&!tabs1.contains("contabilidad")
	&&!tabs1.contains("ingresos")&&!tabs1.contains("bancos")&&!tabs1.contains("subir-archivo")&&!tabs1.contains("calculo-deuda")
	&&!tabs1.contains("actas")&&!tabs1.contains("convenios")&&!tabs1.contains("cheques") &&!tabs1.contains("adm-caja-chica")
	&&!tabs1.contains("centro-costo")  &&!tabs1.contains("carga-paritarias") 
	&&!tabs1.contains("proveedores")&&!tabs1.contains("facturacion")
	&&!tabs1.contains("interbanking")&&!tabs1.contains("ctacte-empresa")&&!tabs1.contains("ctacte-saldoinicial")
	){
		if(showUnidadOperativa){
			tabs1="unidad-operativa";
		}else if(showOrdenPagoUOMA){
			tabs1="comprobantes";
		}else if(showArchivosUOMA){
			tabs1="subir-archivo";
		}else if(showCajaChica){
			tabs1="adm-caja-chica";
		}else if(showReportesUOMA){
			tabs1="reportes";
		}else if(showABMCentroCostoUOMA){
			tabs1="centro-costo";
		}else if(showABMProveedores){
			tabs1="proveedores";
		}else if(showABMFacturacion){
			tabs1="facturacion";
		}else if(showABMFacturacion){
			tabs1="interbanking";
		}else if(showCtaCteEmpresa){
			tabs1="ctacte-empresa";
		}
	}
}

String tabs1Values=null;

if(showUnidadOperativa){
	tabs1Values = "unidad-operativa";
}


if(showOrdenPagoUOMA){
	tabs1Values=tabs1Values!=null?tabs1Values+",comprobantes,ordenes-pago,ingresos,bancos,contabilidad"
			:"comprobantes,ordenes-pago,ingresos,bancos,reportes,contabilidad";
}

if(showReportesUOMA || showOrdenPagoUOMA){
	tabs1Values=tabs1Values!=null?tabs1Values+",reportes":"reportes";
}

if(showCalculoDeuda){
	tabs1Values=tabs1Values!=null?tabs1Values+",calculo-deuda,actas,convenios,cheques":"calculo-deuda,actas,convenios,cheques";
}/* else if(!showCajaChica){	
	tabs1Values=tabs1Values!=null?tabs1Values+",actas,convenios,cheques":"actas,convenios,cheques";
} */
if(showActaUOMA){	
	tabs1Values=tabs1Values!=null?tabs1Values+",actas":"actas";
}
if(showConvenioUOMA){	
	tabs1Values=tabs1Values!=null?tabs1Values+",convenios":"convenios";
}
if(showCheques){	
	tabs1Values=tabs1Values!=null?tabs1Values+",cheques":"cheques";
}
if(showArchivosUOMA){
	tabs1Values=tabs1Values!=null?tabs1Values+",subir-archivo":"subir-archivo";
}
if(showCajaChica){	
	tabs1Values=tabs1Values!=null?tabs1Values+",adm-caja-chica":"adm-caja-chica";
}
if(showABMCentroCostoUOMA || showTABLEROCentroCostoUOMA){	
	tabs1Values=tabs1Values!=null?tabs1Values+",centro-costo":"centro-costo";
}
if(showTSUBIR_PARITARIAS){	
	tabs1Values=tabs1Values!=null?tabs1Values+",carga-paritarias":"carga-paritarias";
}
if(showABMProveedores){	
	tabs1Values=tabs1Values!=null?tabs1Values + ",proveedores":"proveedores";
}

if(showABMFacturacion){	
	tabs1Values=tabs1Values!=null?tabs1Values + ",facturacion":"facturacion";
}

if(showInterbanking){	
	tabs1Values=tabs1Values!=null?tabs1Values + ",interbanking":"interbanking";
}

if(showCtaCteEmpresa){	
	
	tabs1Values=tabs1Values!=null?tabs1Values + ",ctacte-empresa":"ctacte-empresa";
/*	
	tabs1Values=tabs1Values!=null?tabs1Values + ",ctacte-saldoinicial":"ctacte-saldoinicial";
*/	
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/uoma/view");
portletURL.setParameter("tabs1", tabs1);
currentURL = PortalUtil.getCurrentURL(request);
%>

<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">
<liferay-portlet:renderURLParams varImpl="portletURL" />

<liferay-ui-custom:tabs
	names="<%= tabs1Names %>"
	tabsValues="<%= tabs1Values %>"
	portletURL="<%= portletURL %>"
/>
<!-- REPRESENTACIÓN DE LOS TABS DE UOMA -->
<c:choose>
	<c:when test='<%= tabs1.equals("unidad-operativa") %>'>
		<liferay-util:include page="/html/portlet/uoma/unidad_operativa/busqueda_incidente.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("comprobantes") %>'>	
		<liferay-util:include page="/html/portlet/uoma/comprobantes/busqueda_comprobantes_uoma.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("ordenes-pago") %>'>	
		<liferay-util:include page="/html/portlet/uoma/busqueda_ordenes_pago.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("cheques") %>'>	
		<liferay-util:include page="/html/portlet/liquidaciones/cheques/busqueda_cheques.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("ingresos") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/recibos/busqueda_recibos.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("calculo-deuda") %>'>
		<liferay-util:include page="/html/portlet/uoma/actasNoOS/busqueda_calculo_deuda.jsp">
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("actas") %>'>
		<liferay-util:include page="/html/portlet/uoma/actasNoOS/busqueda_actasNoOS.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("convenios") %>'>
		<liferay-util:include page="/html/portlet/uoma/conveniosNoOS/busqueda_convenios_no_os.jsp">
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("bancos") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/bancos/bancos.jsp"/>	
	</c:when>	
	<c:when test='<%= tabs1.equals("reportes") %>'>	
		<liferay-util:include page="/html/portlet/uoma/reportes.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("contabilidad") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/equivalencias/contabilidad.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("subir-archivo") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/afip/upload_archivos_afip.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("adm-caja-chica") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/caja_chica/caja_chica_adm.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("centro-costo") %>'>
		<liferay-util:include page="/html/portlet/uoma/centro_costo/centro_costo_opc.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("carga-paritarias") %>'>
		<liferay-util:include page="/html/portlet/uoma/paritarias/paritarias.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("proveedores") %>'>
		<liferay-util:include page="/html/portlet/uoma/proveedores/proveedores_list.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("facturacion") %>'>
		<liferay-util:include page="/html/portlet/uoma/facturacion/facturas_list.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("interbanking") %>'>
		<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/interbanking_menu.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("ctacte-empresa") %>'>
		<liferay-util:include page="/html/portlet/uoma/cuentacorriente/empleadores_reimputacion_pagos_list.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("ctacte-saldoinicial") %>'>
		<liferay-util:include page="/html/portlet/uoma/cuentacorriente/saldoinicial_adm.jsp"/>	
	</c:when>
	
</c:choose>

</form>

<%
if (!tabs1.equals("unidad-operativa")) {
	PortalUtil.setPageSubtitle(LanguageUtil.get(pageContext, StringUtil.replace(tabs1, StringPool.UNDERLINE, StringPool.DASH)), request);
}
%>