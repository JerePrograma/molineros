<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.ConceptoSueldos" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<%@ page import="ar.com.uoma.beans.CentroCosto" %>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
	
<%	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
    String  entidad="";
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
		entidad="O";
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
		entidad="A";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
		entidad = "U";
	} 
	
	ConceptoSueldos concepto=(ConceptoSueldos)request.getSession().getAttribute(WebKeysTesoreria.EQUIVALENCIAS_SUELDOS_EN_EDICION);
	
	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	String ejDesde = (String) request.getAttribute("ejercicio_desde");
	String ejHasta = (String) request.getAttribute("ejercicio_hasta");
	Calendar  desde = null;
	Calendar  hasta = null;
	if (ejDesde !=null){
		desde = Calendar.getInstance();
		desde.setTime(format.parse(ejDesde));
		hasta = Calendar.getInstance();
		hasta.setTime(format.parse(ejHasta));
	}
	
	
	List<PlanCuentas> pCuentas = (List<PlanCuentas>)request.getAttribute("planCuentas");
	List<CentroCosto> pSectores=TraeListasServiceUtil.getSectoresLiquidacionSueldos(entidad);
	int i = 1;
	
	boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONTABILIDAD);
	boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
	rolABM = true;
	String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
	
	
	String esEditableStr = ParamUtil.getString(request, "esEditable");
	if (esEditableStr == null || esEditableStr.equals("false")){
			esEditableStr ="false";
	}
	boolean esEditable = Boolean.parseBoolean(esEditableStr);
		
%>

<input type="hidden" id="<portlet:namespace />id" name="<portlet:namespace />id" value="<%=concepto.getId()%>"/>
<input type="hidden" id="<portlet:namespace />entidad" name="<portlet:namespace />entidad" value="<%=entidad%>"/>



<fieldset class="block-labels" >
  <legend>Parámetros de Importación</legend>
  <table class="lfr-table">
         <tr>	
			<td><label>Código Sistema Sueldos:</label></td>
			<td> <input type="text" value="<%=concepto.getCodigo()%>" name="<portlet:namespace />codigo" id="<portlet:namespace />codigo"  readonly="readonly" /></td>
			<td> <input type="text" value="<%=concepto.getDescripcion()%>" name="<portlet:namespace />descripcion" id="<portlet:namespace />descripcion"  readonly="readonly" /></td>
        </tr>
  </table>
  <table class="lfr-table">
		<tr>
		    
			<td><label>Area Liquidación:</label></td>
			<td>
			  
					<select id="<portlet:namespace />sectLiq" name="<portlet:namespace />sectLiq">
						<%for(CentroCosto sector:pSectores) {%>
						<option
							value="<%=sector.getId() %>"
							<%if(concepto.getSectorLiquidado()==sector.getId()){ %>
                                   selected="selected"    
                            <%}%>>
                            <%=sector.getDescripcion() %>
						</option>
					    <%}%>					
				    </select>
		      		    
			</td>
		</tr>
		
        <tr>   			
			<td><label>Cuenta de Imputación:</label></td>
			<td>
					<select id="<portlet:namespace />pcta" name="<portlet:namespace />pcta">
						<%for(PlanCuentas cuenta:pCuentas) {%>
						<option
							value="<%=cuenta.getId() %>"
							<%if( cuenta.getId()==concepto.getCuentaContable().getId() ){ %>
					         selected="selected"
					         <%}%> ><%=cuenta.getCuenta() + " - " + cuenta.getNumero() %>
                        </option>
					    <%}%>					
				    </select>
			</td>
			
			
			<td><label>Columna:</label></td>
			<td>
			   <select id="<portlet:namespace />debeHaber" name="<portlet:namespace />debeHaber">
					<option value="D"  <%if("D".equals( concepto.getDebeHaber()) ){ %> selected="selected"<% }%>>DEBE</option>
				    <option value="H"  <%if("H".equals( concepto.getDebeHaber()) ){ %> selected="selected"<% }%>>HABER</option>
			   </select>
			</td>
			
		</tr>
		<tr>
		    <td colspan="4">&nbsp;</td>
	    </tr>
		
   </table>
</fieldset>   


<br/>
<% if (rolABM && !soloVer) {%> 
<input type="button" value="Guardar" onclick="guardarEquivalencia()"/>&nbsp;
	
<% } %>
<div align="center" id="<portlet:namespace />buscando">
	<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
</div>

				 
<script type="text/javascript">	
jQuery("#<portlet:namespace />buscando").hide();

function guardarEquivalencia(){
		var error = false;
		var errorFecha = false;
		
		var id = jQuery('#<portlet:namespace />id').val();
		var codigo= jQuery('#<portlet:namespace />codigo').val();
		var sector= jQuery('#<portlet:namespace />sectLiq').val();
		var enti= jQuery('#<portlet:namespace />entidad').val();
		var descripcion=jQuery('#<portlet:namespace />descripcion').val();
		var debeHaber=jQuery('#<portlet:namespace />debeHaber').val();
		var cuentaId=jQuery('#<portlet:namespace />pcta').val();
	
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/importar_asiento_sueldos_equivalencias';
		url += '&cmd=update_equivalencias';
		url += '&id='+id;
		url += '&codigo='+codigo;
		url += '&sector='+sector;
		url += '&entidad='+enti;
		url += '&descripcion='+encodeURI(descripcion);
		url += '&debeHaber='+debeHaber;
		url += '&cuentaId='+cuentaId;
		
		jQuery("#<portlet:namespace />buscando").show();
		
		jQuery.ajax({   
			url: url,
			success: function(data){
				jQuery("#<portlet:namespace />buscando").hide();
				Liferay.Popup.close(popupE);				
			}				                                                                                                                                                                                                                                                            
			
		});
		
}
	
	

	
</script>

