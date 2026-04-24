<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
    response.setHeader("Cache-Control","no-store");
    response.setHeader("Pragma","no-cache");
    response.setDateHeader("Expires", 0);
    
    session.removeAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
    session.removeAttribute("cmd");
%>

<liferay-ui:error
    exception="<%=IntegranteGrupoNoBorrableException.class %>"
    message="the-integrante-no-puede-ser-borrado" />

<fieldset class="block-labels">
  <legend><liferay-ui:message key="grupo-filtro-busqueda-afiliado" />
    <% if(seccionalFijada!=0){%> en Seccional <%=seccionalString%> <%}%>
  </legend>

  <table class="lfr-table"> 
    <tr>
      <td><label><liferay-ui:message key="entidad" />:</label></td>
      <td>
        <select name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad">
          <% for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) { %>
            <c:if test="<%=((entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_OSPIM)) ||
                             (entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_AMTIMA)) ||
                             (entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_UOMA)))%>">									
              <option value="<%= entidad %>"><%=entidad%></option>
            </c:if>
          <% } %>
        </select>
      </td>
      <td>&nbsp;</td>
      <td><label><liferay-ui:message key="numero-afi" />:</label></td>
      <td><input id="<portlet:namespace />numero_afi" name="<portlet:namespace />numero_afi" size="6" maxlength="10" type="text"/></td>
      <td><label><liferay-ui:message key="nro-socio-prevencion" />:</label></td>
      <td><input id="<portlet:namespace />numero_socio_prev" name="<portlet:namespace />numero_socio_prev" size="6" maxlength="6" type="text"/></td>
      <td><label><liferay-ui:message key="nro-credencial-prevencion" />:</label></td>
      <td><input id="<portlet:namespace />numero_credencial_prev" name="<portlet:namespace />numero_credencial_prev" size="8" maxlength="11" type="text"/></td>
    </tr>

    <tr><td colspan="16">&nbsp;</td></tr>

    <tr>
      <td><label><liferay-ui:message key="cuil" />:</label></td>
      <td><input id="<portlet:namespace />cuil" name="<portlet:namespace />cuil" size="13" maxlength="11" type="text" /></td>
      <td>&nbsp;</td>
      <td><label><liferay-ui:message key="integrante" />:</label></td>
      <td><input id="<portlet:namespace />inte" name="<portlet:namespace />inte" size="2" maxlength="2" type="text" /></td>
      <td><label><liferay-ui:message key="tipo-documento" />:</label></td>
      <td>
        <select name="<portlet:namespace/>tipoDoc" id="<portlet:namespace/>tipoDoc">
          <option value=""></option>
          <% for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) { %>
            <option value="<%= tipoDoc %>"><%=tipoDoc%></option>
          <% } %>
        </select>
      </td>
      <td><label><liferay-ui:message key="nro-documento" />:</label></td>
      <td><input id="<portlet:namespace />nroDoc" name="<portlet:namespace />nroDoc" size="9" maxlength="8" type="text"/></td>
      
      <% if(seccionalFijada==0){%>
        <td><label><liferay-ui:message key="seccional" />:</label></td>
        <td colspan="2" rowspan="3" style="vertical-align:top">
          <liferay-util:include page="/html/portlet/afiliados/busqueda_seccional.jsp"/> &nbsp;&nbsp;&nbsp;
        </td>
      <%}else{%>
        <td colspan="3">&nbsp;</td>
      <%} %>
    </tr>

    <tr><td colspan="16">&nbsp;</td></tr>

    <tr>
      <td><label><liferay-ui:message key="apellido" />:</label></td>
      <td colspan="2"><input id="<portlet:namespace />apellido" name="<portlet:namespace />apellido" size="20" maxlength="100" type="text"/></td>
      <td><label><liferay-ui:message key="nombre" />:</label></td>
      <td colspan="2"><input id="<portlet:namespace />nombre" name="<portlet:namespace />nombre" size="20" maxlength="100" type="text"/></td>
      
      <td colspan>&nbsp;</td>
      
      <td colspan="6">							
        <input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" type="button"/>							
        &nbsp;&nbsp;								
        <input id="<portlet:namespace />limpiar-campos" value="<liferay-ui:message key="limpiar-campos"/>" type="button" onClick='javascript:<portlet:namespace />limpiarCampos()'/>		
      </td>	
    </tr>

    <tr><td colspan="16">&nbsp;(<liferay-ui:message key="refine-busqueda" />)</td></tr>
  </table>
</fieldset>

<fieldset class="block-labels">
  <div align="center" id="<portlet:namespace />buscando">
    <table style="align:center;">
      <tr>
        <td><liferay-ui:message key="buscando"/></td>
        <td align="center">
          <img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
        </td>
      </tr>
    </table>		
  </div>	

  <div align="center" id="<portlet:namespace />busquedaAfiliadoDiv"></div>
</fieldset> 

<script type="text/javascript">
  jQuery('#<portlet:namespace />buscando').hide();
  
  var url = "<portlet:renderURL windowState='<%= LiferayWindowState.EXCLUSIVE.toString() %>'/>&struts_action=/afiliados/buscar_afiliados_cuenta_bancaria_sesion";
  jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url);
  
  jQuery('#<portlet:namespace />buscar').click(function(){
    <portlet:namespace />busqueda();
  });

  function <portlet:namespace />busqueda(){
    var cuil=jQuery('#<portlet:namespace />cuil').val();
    var inte=jQuery('#<portlet:namespace />inte').val();		
    var tipoDoc=jQuery('#<portlet:namespace />tipoDoc').val();		
    var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();		

    <% if(seccionalFijada==0){%>
      var seccional=jQuery('#<portlet:namespace />id_seccional').val();		
    <%}else{%>
      var seccional="<%=seccionalFijada%>";		
    <%}%>

    var apellido=jQuery('#<portlet:namespace />apellido').val();		
    var nombre=jQuery('#<portlet:namespace />nombre').val();		
    var entidad=jQuery('#<portlet:namespace />entidad').val();		
    var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
    var nroSocioPrev =jQuery('#<portlet:namespace />numero_socio_prev').val();
    var nroCredencialPrev =jQuery('#<portlet:namespace />numero_credencial_prev').val();

    if(!<portlet:namespace />validarBusqueda(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi,nroSocioPrev,nroCredencialPrev)){
      return false;
    }

    if(cuil.length>0){
      if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
        jQuery('#<portlet:namespace />cuil').focus();
        return false;
      }
    }

    jQuery('#<portlet:namespace />buscando').show();
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliados_cuenta_bancaria';
	var paramsAfi = {
      "cuil" : cuil, "inte" : inte, "tipoDoc" : tipoDoc, "nroDoc" : escape(nroDoc),
      "seccional" : seccional, "nombre" : nombre, "apellido" : apellido, "entidad" : entidad, 
      "numero_afi" : numero_afi, "nroSocioPrevencion" : nroSocioPrev, "nroCredencialPrevencion" : nroCredencialPrev
    };

    jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, paramsAfi, function() {
      jQuery('#<portlet:namespace />buscando').hide();            															
    });
  }

  function <portlet:namespace/>buscaGrupo(cuil){
    jQuery('#<portlet:namespace />buscando').show();				
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliados_cuenta_bancaria&cuil='+cuil;		
    jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, function() {
      jQuery('#<portlet:namespace />buscando').hide();            															
    });			
  }

  function <portlet:namespace />validarBusqueda(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi,nroSocioPrev,nroCredPrev){
    if(trim(cuil.length)==0 && trim(inte.length)==0 && trim(tipoDoc.length)==0 && trim(nroDoc.length)==0 && trim(seccional.length)==0 &&  
       trim(apellido.length)==0 && trim(nombre.length)==0 && trim(entidad.length)==0 && trim(numero_afi.length)==0
       && trim(nroSocioPrev.length)==0 && trim(nroCredPrev.length)==0 ){
      alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
      return false;
    }else{
      return true;
    }
  }

  function <portlet:namespace />limpiarCampos(){
    jQuery('#<portlet:namespace />cuil').val('');
    jQuery('#<portlet:namespace />inte').val('');
    jQuery('#<portlet:namespace />tipoDoc').val('');
    jQuery('#<portlet:namespace />nroDoc').val('');
    <% if(seccionalFijada==0){%>
      jQuery('#<portlet:namespace />id_seccional').val('');		
      jQuery('#<portlet:namespace />seccional').val('');
    <%}%>
    jQuery('#<portlet:namespace />apellido').val('');
    jQuery('#<portlet:namespace />nombre').val('');
    jQuery('#<portlet:namespace />entidad').val('');
    jQuery('#<portlet:namespace />numero_afi').val('');
    jQuery('#<portlet:namespace />numero_socio_prev').val('');
    jQuery('#<portlet:namespace />numero_credencial_prev').val('');
  }
</script>
