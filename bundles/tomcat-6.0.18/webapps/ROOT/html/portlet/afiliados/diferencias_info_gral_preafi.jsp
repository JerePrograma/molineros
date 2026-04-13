<%@ include file="/html/portlet/afiliados/init.jsp"%>

<% 
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);

Afiliado preAfiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.PREAFILIADO_EN_SESSION);

if(preAfiliado!=null && afiliado != null){ %>

<table class="lfr-table">
 	  <tr>
		<th><label><liferay-ui:message key="dif-pre-carga-afi" />:</label></th>
	  </tr>
	  <tr>
	  	  <td>
	  	       <table class="lfr-table" style="font: fantasy; font-style: italic; color: red;">
	  	       <tr>
	  	           <%if(preAfiliado.getSeccional().getId_seccional() != afiliado.getSeccional().getId()){ %>
	   			   <td>Seccional:&nbsp;<%=preAfiliado.getSeccional().getId_seccional()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte() != afiliado.getInte()){ %>
	   			   <td>Integrante n°:&nbsp;<%=preAfiliado.getInte()%>&nbsp;</td>
	  		 	   <% } %>
	  	           <%if(!preAfiliado.getNombre().equalsIgnoreCase(afiliado.getNombre())){ %>
	   			   <td>Nombre:&nbsp;<%=preAfiliado.getNombre().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  	           <%if(!preAfiliado.getApellido().equalsIgnoreCase(afiliado.getApellido())){ %>
	   			   <td>Apellido:&nbsp;<%=preAfiliado.getApellido().trim()%>&nbsp;</td>
	  			   <% } %>
	  			   <%if(!preAfiliado.getSexo().equalsIgnoreCase(afiliado.getSexo())){ %>
	   			   <td>Sexo:&nbsp;<%=preAfiliado.getSexo().equalsIgnoreCase("f")?"Femenino":"Masculino"%>&nbsp;</td>
	  		 	   <% } %>
	  		   <tr>	 	
	  		   <tr>
	  	           <%if(preAfiliado.getInte()==0 && preAfiliado.getDomicilioDefault().getProvinciaId() != afiliado.getDomicilioDefault().getProvinciaId()){ %>
	   			  <%--  <td>Provincia:&nbsp;<%=preAfiliado.getDomicilioDefault().getProvinciaId()%>&nbsp;</td> --%>
	   			   <%Provincia p_aux = new Provincia(preAfiliado.getDomicilioDefault().getProvinciaId());
	   			   	 int pos = provincias.indexOf(p_aux);
	   			   	 p_aux = provincias.get(pos);
	   			    %>
	   			    <td>Provincia:&nbsp;<%=p_aux.getDescripcion()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte()==0 && preAfiliado.getDomicilioDefault().getLocalidadId() != afiliado.getDomicilioDefault().getLocalidadId()){ %>
	   			   <%-- <td>Localidad:&nbsp;<%=preAfiliado.getDomicilioDefault().getLocalidadId()%>&nbsp;</td> --%>
	   			   <%Localidad l_aux = new Localidad(preAfiliado.getDomicilioDefault().getLocalidadId());
	   			   	 int pos = localidades.indexOf(l_aux);
	   			   	 l_aux = localidades.get(pos);
	   			    %>
	   			     <td>Localidad:&nbsp;<%=l_aux.getDescripcion()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getCalle().equalsIgnoreCase(afiliado.getDomicilioDefault().getCalle())){ %>
	   			   <td>Calle:&nbsp;<%=preAfiliado.getDomicilioDefault().getCalle().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getNumero().equalsIgnoreCase(afiliado.getDomicilioDefault().getNumero())){ %>
	   			   <td>Altura:&nbsp;<%=preAfiliado.getDomicilioDefault().getNumero().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getPiso().equalsIgnoreCase(afiliado.getDomicilioDefault().getPiso())){ %>
	   			   <td>Piso:&nbsp;<%=preAfiliado.getDomicilioDefault().getPiso().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getDepto().equalsIgnoreCase(afiliado.getDomicilioDefault().getDepto())){ %>
	   			   <td>Dpto:&nbsp;<%=preAfiliado.getDomicilioDefault().getDepto().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getPostal_codi().equalsIgnoreCase(afiliado.getDomicilioDefault().getPostal_codi())){ %>
	   			   <td>Código Postal:&nbsp;<%=preAfiliado.getDomicilioDefault().getPostal_codi().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getBarrio().equalsIgnoreCase(afiliado.getDomicilioDefault().getBarrio())){ %>
	   			   <td>Barrio:&nbsp;<%=preAfiliado.getDomicilioDefault().getBarrio().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	 </tr>
	  		 	 <tr>
	  		 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getCod_area_telefono().equalsIgnoreCase(afiliado.getDomicilioDefault().getCod_area_telefono())){ %>
	   			   <td>Cod.Area Teléfono:&nbsp;<%=preAfiliado.getDomicilioDefault().getCod_area_telefono().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getTelefono().equalsIgnoreCase(afiliado.getDomicilioDefault().getTelefono())){ %>
	   			   <td>Teléfono:&nbsp;<%=preAfiliado.getDomicilioDefault().getTelefono().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getCod_area_telefono().equalsIgnoreCase(afiliado.getDomicilioDefault().getCod_area_telefono())){ %>
	   			   <td>Cod.Area Laboral:&nbsp;<%=preAfiliado.getDomicilioDefault().getCod_area_tel_laboral().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getTelefono().equalsIgnoreCase(afiliado.getDomicilioDefault().getTelefono())){ %>
	   			   <td>Tel. Laboral:&nbsp;<%=preAfiliado.getDomicilioDefault().getTel_laboral().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getCod_area_celular().equalsIgnoreCase(afiliado.getDomicilioDefault().getCod_area_celular())){ %>
	   			   <td>Cod.Area Celular:&nbsp;<%=preAfiliado.getDomicilioDefault().getCod_area_celular().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getCelular().equalsIgnoreCase(afiliado.getDomicilioDefault().getCelular())){ %>
	   			   <td>Celular:&nbsp;<%=preAfiliado.getDomicilioDefault().getCelular().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(!preAfiliado.getEmail().equalsIgnoreCase(afiliado.getEmail())){ %>
	   			   <td>Email:&nbsp;<%=preAfiliado.getEmail().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		     </tr>	 
	  		     <tr>
	  		 	   <%if(preAfiliado.getNacionalidad() != afiliado.getNacionalidad()){ %>
	   			   <%-- <td>Nacionalidad:&nbsp;<%=preAfiliado.getNacionalidad()%>&nbsp;</td> --%>
	   			   <%Nacionalidad n_aux = new Nacionalidad(preAfiliado.getNacionalidad(),"");
	   			   	 int pos = nacionalidades.indexOf(n_aux);
	   			   	 n_aux = nacionalidades.get(pos);
	   			    %>
	   			    <td>Nacionalidad:&nbsp;<%=n_aux.getDescripcion()%>&nbsp;</td> 
	  		 	   <% } %>
	  		 	   <%if(!preAfiliado.getDocumento_tipo().equalsIgnoreCase(afiliado.getDocumento_tipo())){ %>
	   			   <td>Tipo Doc.:&nbsp;<%=preAfiliado.getDocumento_tipo().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(!preAfiliado.getDocu_numero().equalsIgnoreCase(afiliado.getDocu_numero())){ %>
	   			   <td>Nro. Doc.:&nbsp;<%=preAfiliado.getDocu_numero().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(!preAfiliado.getCuil().equalsIgnoreCase(afiliado.getCuil())){ %>
	   			   <td>CUIL:&nbsp;<%=preAfiliado.getCuil().trim()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(!preAfiliado.getNaci_fecha().equals(afiliado.getNaci_fecha())){ %>
	   			   <td>Fecha Nac.:&nbsp;<%=sdf.format(preAfiliado.getNaci_fecha())%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getId_civil_esta() != afiliado.getId_civil_esta()){ %>
	   			  <%--  <td>Estado Civil:&nbsp;<%=preAfiliado.getId_civil_esta()%>&nbsp;</td> --%>
	   			    <%EstadoCivil e_aux = new EstadoCivil(preAfiliado.getId_civil_esta(),"");
	   			   	 int pos = estados_civil.indexOf(e_aux);
	   			   	 e_aux = estados_civil.get(pos);
	   			    %>
	   			    <td>Estado Civil:&nbsp;<%=e_aux.getDescripcion()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(preAfiliado.getId_parentesco() != afiliado.getId_parentesco()){ %>
	   			   <%-- <td>Parentesco:&nbsp;<%=preAfiliado.getId_parentesco()%>&nbsp;</td> --%>
	   			    <%Parentesco pp_aux = new Parentesco(preAfiliado.getId_parentesco(),"");
	   			   	 int pos = parentescos.indexOf(pp_aux);
	   			   	 pp_aux = parentescos.get(pos);
	   			    %>
	   			   <td>Parentesco:&nbsp;<%=pp_aux.getDescripcion()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	    <%if(!preAfiliado.getDiscapacitado().equalsIgnoreCase(afiliado.getDiscapacitado())){ %>
	   			   <td>Discapacitado:&nbsp;<%=preAfiliado.getDiscapacitado().equals("1")?"SI":"NO"%>&nbsp;</td>
	  		 	   <% } %>
	  		 	 </tr>   	
	  	       </table>		
	  	  </td>
	  </tr>
	  <tr>
	  		<td align="left"><input type="button" name="pasarCambios" value="Cargar Cambios" onclick="<portlet:namespace />pasarCambios();" /> </td>
	  </tr>	  	
</table>
<%} %>	   


<script type="text/javascript">
function <portlet:namespace />pasarCambios(){
		
	   <%if(preAfiliado.getSeccional().getId_seccional() != afiliado.getSeccional().getId()){ %>
	   	   jQuery('#<portlet:namespace />id_seccional').val('<%=preAfiliado.getSeccional().getId_seccional()%>');
	   	   <portlet:namespace />buscarSeccional();
 	   <% } %>
 	   <%if(preAfiliado.getInte() != afiliado.getInte()){ %>
 	  	  jQuery('#<portlet:namespace/>inte').val('<%=preAfiliado.getInte()%>');
 	   <% } %>
       <%if(!preAfiliado.getNombre().equalsIgnoreCase(afiliado.getNombre())){ %>
   	   	  jQuery('#<portlet:namespace />nombre').val('<%=preAfiliado.getNombre()%>');
 	   <% } %>
       <%if(!preAfiliado.getApellido().equalsIgnoreCase(afiliado.getApellido())){ %>
       	  jQuery('#<portlet:namespace />apellido').val('<%=preAfiliado.getApellido()%>');
	   <% } %>
	   <%if(!preAfiliado.getSexo().equalsIgnoreCase(afiliado.getSexo())){ %>
	   	  jQuery("#<portlet:namespace />sexo option").each(function() { this.selected = (this.text == '<%=preAfiliado.getSexo().equalsIgnoreCase("f")?"Femenino":"Masculino"%>'); });
 	   <% } %>
       <%if(preAfiliado.getInte()==0 && preAfiliado.getDomicilioDefault().getProvinciaId() != afiliado.getDomicilioDefault().getProvinciaId()){ %>
		    jQuery("#<portlet:namespace />provincia option[value="+<%=preAfiliado.getDomicilioDefault().getProvinciaId()%> +"]").attr("selected",true);
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && preAfiliado.getDomicilioDefault().getLocalidadId() != afiliado.getDomicilioDefault().getLocalidadId()){ %>
 	 	    jQuery("#<portlet:namespace />localidad option[value="+<%=preAfiliado.getDomicilioDefault().getLocalidadId()%> +"]").attr("selected",true);
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getCalle().equalsIgnoreCase(afiliado.getDomicilioDefault().getCalle())){ %>
 	  	    jQuery('#<portlet:namespace />calle').val('<%=preAfiliado.getDomicilioDefault().getCalle()%>');
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getNumero().equalsIgnoreCase(afiliado.getDomicilioDefault().getNumero())){ %>
			jQuery('#<portlet:namespace />numero').val('<%=preAfiliado.getDomicilioDefault().getNumero()%>');
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getPiso().equalsIgnoreCase(afiliado.getDomicilioDefault().getPiso())){ %>
 	  		jQuery('#<portlet:namespace />piso').val('<%=preAfiliado.getDomicilioDefault().getPiso()%>');
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getDepto().equalsIgnoreCase(afiliado.getDomicilioDefault().getDepto())){ %>
 	  		jQuery('#<portlet:namespace />dpto').val('<%=preAfiliado.getDomicilioDefault().getDepto()%>');
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getPostal_codi().equalsIgnoreCase(afiliado.getDomicilioDefault().getPostal_codi())){ %>
 	 		jQuery('#<portlet:namespace />cod_postal').val('<%=preAfiliado.getDomicilioDefault().getPostal_codi()%>');
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getBarrio().equalsIgnoreCase(afiliado.getDomicilioDefault().getBarrio())){ %>
 	  		jQuery('#<portlet:namespace />barrio').val('<%=preAfiliado.getDomicilioDefault().getBarrio()%>');
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getCod_area_telefono().equalsIgnoreCase(afiliado.getDomicilioDefault().getCod_area_telefono())){ %>
 	  		jQuery('#<portlet:namespace />cod_area_telefono').val('<%=preAfiliado.getDomicilioDefault().getCod_area_telefono()%>');
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getTelefono().equalsIgnoreCase(afiliado.getDomicilioDefault().getTelefono())){ %>
 	  		jQuery('#<portlet:namespace />telefono').val('<%=preAfiliado.getDomicilioDefault().getTelefono()%>');
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getCod_area_telefono().equalsIgnoreCase(afiliado.getDomicilioDefault().getCod_area_telefono())){ %>
 	  		jQuery('#<portlet:namespace />cod_area_tel_laboral').val('<%=preAfiliado.getDomicilioDefault().getCod_area_tel_laboral()%>');
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getTelefono().equalsIgnoreCase(afiliado.getDomicilioDefault().getTelefono())){ %>
 	  		jQuery('#<portlet:namespace />tel_laboral').val('<%=preAfiliado.getDomicilioDefault().getTel_laboral()%>');
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getCod_area_celular().equalsIgnoreCase(afiliado.getDomicilioDefault().getCod_area_celular())){ %>
 	  		jQuery('#<portlet:namespace />cod_area_tel_celular').val('<%=preAfiliado.getDomicilioDefault().getCod_area_celular()%>');
 	   <% } %>
 	   <%if(preAfiliado.getInte()==0 && !preAfiliado.getDomicilioDefault().getCelular().equalsIgnoreCase(afiliado.getDomicilioDefault().getCelular())){ %>
  			jQuery('#<portlet:namespace />celular').val('<%=preAfiliado.getDomicilioDefault().getCelular()%>');
 	   <% } %>	   
 	   <%if(!preAfiliado.getEmail().equalsIgnoreCase(afiliado.getEmail())){ %>
		 	jQuery('#<portlet:namespace/>email').val('<%=preAfiliado.getEmail()%>');
	   <% } %>
 	   <%if(preAfiliado.getNacionalidad() != afiliado.getNacionalidad()){ %>
			jQuery("#<portlet:namespace />nacionalidad option[value="+<%=preAfiliado.getNacionalidad()%> +"]").attr("selected",true);
 	   <% } %>
 	   <%if(!preAfiliado.getDocumento_tipo().equalsIgnoreCase(afiliado.getDocumento_tipo())){ %>
 	  jQuery("#<portlet:namespace />documento_tipo option").each(function() { this.selected = (this.text == '<%=preAfiliado.getDocumento_tipo()%>'); });
 	   <% } %>
 	   <%if(!preAfiliado.getDocu_numero().equalsIgnoreCase(afiliado.getDocu_numero())){ %>
 	  		jQuery('#<portlet:namespace/>nroDoc').val('<%=preAfiliado.getDocu_numero()%>');
 	   <% } %>
 	   <%if(!preAfiliado.getCuil().equalsIgnoreCase(afiliado.getCuil())){ %>
 	  		jQuery('#<portlet:namespace/>cuil').val('<%=preAfiliado.getCuil()%>');
 	   <% } %>
 	   <%if(!preAfiliado.getNaci_fecha().equals(afiliado.getNaci_fecha())){ 
		   Calendar f_nac = Calendar.getInstance();
 	   	   f_nac.setTime(preAfiliado.getNaci_fecha()); %>
	  		jQuery('#<portlet:namespace/>fechaNacimientoDia').val('<%=f_nac.get(Calendar.DATE) %>');
	  		jQuery('#<portlet:namespace/>fechaNacimientoMes').val('<%=f_nac.get(Calendar.MONTH ) %>');
	  		jQuery('#<portlet:namespace/>fechaNacimientoAnio').val('<%=f_nac.get(Calendar.YEAR) %>');
 	  		
 	   <% } %>
 	   <%if(preAfiliado.getId_civil_esta() != afiliado.getId_civil_esta()){ %>
  			jQuery("#<portlet:namespace />estado_civil option[value="+<%=preAfiliado.getId_civil_esta()%> +"]").attr("selected",true);
 	   <% } %>
 	   <%if(preAfiliado.getId_parentesco() != afiliado.getId_parentesco()){ %>
			jQuery("#<portlet:namespace />parentesco option[value="+<%=preAfiliado.getId_parentesco()%> +"]").attr("selected",true);
 	   <% } %>
 	   <%if(!preAfiliado.getDiscapacitado().equalsIgnoreCase(afiliado.getDiscapacitado())){ %>
 	  		jQuery("#<portlet:namespace />discapacitado option[value='"+<%=preAfiliado.getDiscapacitado()%> +"']").attr("selected",true);
 	   <% } %>
	}
</script>

