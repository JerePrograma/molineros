<%@ include file="/html/portlet/afiliados/init.jsp" %>

<div id="<portlet:namespace/>selectBeneficiario">
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px; width: 100%;">
		<tr>
			<td align="center">
				<liferay-ui:message key="pre-carga-sel" />
			</td>	
			<td> 
				<input type="radio" name="<portlet:namespace/>tipo_benef" value="0" checked="checked"
					onchange="javascript:<portlet:namespace />activaSelCuilTitular();">SI &nbsp;
				<input type="radio" name="<portlet:namespace/>tipo_benef" value="1"
				    onchange="javascript:<portlet:namespace />activaSelCuilTitular();">NO &nbsp;		
			</td>
		</tr>
		<!-- <tr><td colspan="2">&nbsp;</td></tr> -->
		<tr>	
			<td colspan="2">
				<div id="<portlet:namespace/>div_tipo_benef_cuil_titular">
					<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
						<tr><td colspan="2" align="center"><i>Ingrese el Cuil del titular y presione 'Buscar'</i></td></tr>
						<tr>
							<td><input type="text" value="" id="<portlet:namespace/>benef_cuil_titular" 
										name="<portlet:namespace/>benef_cuil_titular" size="13" maxlength="11" />
								<i>cuil sin guiones</i>		
							</td>
							<td>
								<input type="button" value="<liferay-ui:message key="buscar" />" 
									   onClick='javascript:<portlet:namespace />validarCuilTitular("cuil_titular");' />
								<input type="hidden" value=""  name="<portlet:namespace/>cuil_titular_ok" 
									   id="<portlet:namespace/>cuil_titular_ok" />	   
							</td>
						</tr>
						
						<tr>
							<td colspan="2" width="100%">
								<div id ="<portlet:namespace/>divApellidoyNombreTitular" >
									Titular : <input type = "text" readonly="readonly" value = "" id="<portlet:namespace/>apeynomtitu" style="width: 200px;">
								</div>	
							</td>
						</tr>	
					</table>
				</div>
			</td>
		</tr>	
		<tr><td colspan="2">&nbsp;</td></tr>
		<tr>	
			<td colspan="2" align="center">
				<input type="button" value="<liferay-ui:message key="next" />" onClick='javascript:<portlet:namespace />realizaAltaPreAfi()' />
				
				<input id="<portlet:namespace />limpiar-campos" value="<liferay-ui:message key="limpiar-campos"/>" 
					   title="<liferay-ui:message key="limpiar-campos" />" type="button" 
					   onClick='javascript:<portlet:namespace />limpiarCuilTit()'/>							
			</td>
		</tr>
	</table>
</div>

<script type="text/javascript">

jQuery('#<portlet:namespace />div_tipo_benef_cuil_titular').hide();
jQuery('#<portlet:namespace />divApellidoyNombreTitular').hide();


function <portlet:namespace />limpiarCuilTit(){
	jQuery('#<portlet:namespace />benef_cuil_titular').val('');
	jQuery('#<portlet:namespace />apeynomtitu').val('');
	jQuery('#<portlet:namespace />divApellidoyNombreTitular').hide();
}

function <portlet:namespace />activaSelCuilTitular(){
	var sel = jQuery("input[name='<portlet:namespace />tipo_benef']:checked").val();
	
	if (sel == 1){
    	jQuery('#<portlet:namespace />div_tipo_benef_cuil_titular').show();
    }else{
    	jQuery('#<portlet:namespace />div_tipo_benef_cuil_titular').hide();
    }
} 

function <portlet:namespace />validarCuilTitular(cuil){
	var cuil_final = jQuery('#<portlet:namespace/>benef_cuil_titular').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_existe_pre_afiliado&cuil='+cuil_final;
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			if(obj.validado=="1"){
				jQuery('#<portlet:namespace />cuil_titular_ok').val('fail');
				alert("<liferay-ui:message key='cuil-invalido'/> o <liferay-ui:message key='the-integrante-key-already-exists'/> ");
			}else if(obj.validado=="2" || obj.validado=="3"){
				jQuery('#<portlet:namespace />cuil_titular_ok').val('ok');
				var ape = obj.apellido;
				var nom = obj.nombre;

				<portlet:namespace />mostrarApellidoNombreTitular(ape,nom);
			}else{
				jQuery('#<portlet:namespace />cuil_titular_ok').val('fail');
			} 	
		}				                                                                                                                                                                                                                                                            
		
	});
	
}

function <portlet:namespace />mostrarApellidoNombreTitular(ape,nom){
	var apeynom = ape + ', '+nom; 

	jQuery('#<portlet:namespace />divApellidoyNombreTitular').show();
	jQuery('#<portlet:namespace />apeynomtitu').val(apeynom);
}

function <portlet:namespace />realizaAltaPreAfi(){
	var msgOk = jQuery("#<portlet:namespace/>cuil_titular_ok").val();
	var cuil_titular = jQuery("#<portlet:namespace/>benef_cuil_titular").val();
	var sel = jQuery("input[name='<portlet:namespace />tipo_benef']:checked").val();
	
	if (sel==0 || (sel ==1 && msgOk == 'ok')){
		<portlet:namespace />tipoAlta(cuil_titular);
    }else /* if (msgOk == 'fail') */{
    	alert('Error al seleccionar Cuil Titular');
    	return false;
    }
	
	return true;
} 


</script>