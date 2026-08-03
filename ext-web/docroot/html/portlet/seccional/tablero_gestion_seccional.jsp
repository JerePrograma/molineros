<%@ include file="/html/portlet/seccional/init.jsp" %>

<%

String portlet_name = ParamUtil.getString(request, "portlet_name");
NumberFormat formatter = new DecimalFormat("$#,###,###,##0.00");

if (portlet_name == null && portlet_name.trim().equals("_AFI_1_")){
	portlet_name = "afiliados";
}

if (portlet_name == null && portlet_name.trim().equals("_SEC_1_")){
	portlet_name = "sec";
}

if(renderResponse.getNamespace().equals("_SEC_1_")){
	portlet_name = "sec";
}

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "afiliados";
} 

String urlUomaSeccional  = (String) request.getAttribute(WebKeysSeccionales.UOMA_URL);

Seccional secc = (Seccional) request.getAttribute(WebKeysSeccionales.SECCIONAL_VIEW);

Integer idSeccional = secc.getId_seccional();

boolean showGestion=PermissionUtil.userContainsRole(user,WebKeysSeccionales.ROL_GESTION_SECCIONALES);

Calendar fechaActual = CalendarFactoryUtil.getCalendar();

Calendar fechaDesde = (Calendar) request.getAttribute("fechaExplosion");
if(fechaDesde==null){
	fechaDesde=Calendar.getInstance(); 
	fechaDesde.add(Calendar.DATE, -31);
}


PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/sec/view");

List<Contacto> contactos = (ArrayList<Contacto>) request.getAttribute(WebKeysSeccionales.CONTACTOS_SECCIONALES);
Map<String,Integer> desglosePadron = (HashMap<String,Integer>) request.getAttribute(WebKeysSeccionales.DESGLOSE_PADRON);
List<Empresa> empresas = (ArrayList<Empresa>) request.getAttribute(WebKeysSeccionales.EMPRESAS_SECCIONALES);
List<CentroCosto>centros = (ArrayList<CentroCosto>) request.getAttribute(WebKeysSeccionales.CENTROSCOSTO);

StringBuffer domicilio = new StringBuffer();
StringBuffer contactosSecc = new StringBuffer();

if(secc.getDomicilio()!=null){
	
	if(StringUtils.checkNotEmpty(secc.getDomicilio().getCalle())){
		domicilio.append(secc.getDomicilio().getCalle());
		domicilio.append(" ");
	}
	if(StringUtils.checkNotEmpty(secc.getDomicilio().getNumero())){
		domicilio.append("n° ");
		domicilio.append(secc.getDomicilio().getNumero());
		domicilio.append(" ");
	}
	if(StringUtils.checkNotEmpty(secc.getDomicilio().getPiso())){
		domicilio.append("piso ");
		domicilio.append(secc.getDomicilio().getPiso());
		domicilio.append(" ");
	}
	if(StringUtils.checkNotEmpty(secc.getDomicilio().getDepto())){
		domicilio.append("dpto ");
		domicilio.append(secc.getDomicilio().getDepto());
		domicilio.append(" ");
	}
	
	if(StringUtils.checkNotEmpty(secc.getDomicilio().getLocalidadAsString())){
		domicilio.append(" ");
		domicilio.append(secc.getDomicilio().getLocalidadAsString() );
		domicilio.append(" ");
	}
	if(StringUtils.checkNotEmpty(secc.getDomicilio().getPostal_codi())){
		domicilio.append(" (");
		domicilio.append(secc.getDomicilio().getPostal_codi());
		domicilio.append(") ");
	}
	if(StringUtils.checkNotEmpty(secc.getDomicilio().getProvinciaAsString())){
		domicilio.append(", ");
		domicilio.append(secc.getDomicilio().getProvinciaAsString());
		domicilio.append(" ");
	}
}
if(secc.getContactos()!=null){
	for (Contacto c : secc.getContactos()){ 
		contactosSecc.append(StringUtils.checkNotEmpty(c.getContacto())?c.getContacto().getContacto():" ");
		contactosSecc.append(StringUtils.checkNotEmpty(c.getTelefono())?c.getTelefono().toString():" ");
		contactosSecc.append(" ");
	}
}
String usuario_modi = user.getScreenName();
%>

<style type="text/css" media="all">
 body {
  /* para centrar el contenido */
  background-color:#ffffff;
  text-align:center;
  margin:0 auto;
 }
 #container {
  /* # la almoadilla aplica el estilo a los tag html con ese id */
  margin: 0 auto;
  text-align:left;
  /*width:780px; /* para 800 x 600 */
  width: 100%;
  /* background-image: url('http://www.uoma.org.ar/img/seccionales/BahiaBlanca.jpg'); */
  /* background-image: url('http://www.uoma.org.ar/img/seccionales/Ca%C3%B1uelas.jpg'); */
 
 }
 #cabecera {
  background-color:  #e5e7e9; /* #33ccff;  ; mejor definir color notación #333ccc */ 
  color: black;  /*white*/
  font-size: 20px;
  font-weight: bold;
 }
 #menu {
  background-color: orange;
  float: right;
  width: 20%;
 }
 #contenido {
  /* background-color: #eadcdc; */
  float: left;
  width: 25%;
  overflow-y:scroll;
  height:400px;
 }
 #contenido50 {
  /* background-color: #eadcdc; */
  float: left;
  width: 50%;
  overflow-y:scroll;
  height:400px;
 }
 #pie {
  clear: both;
  background-color: white; /* #333ccc;*/
  color: white; /*#ffffff;*/
 }
 
/*  .table-striped>tbody>tr:nth-child(odd)>td, 
 .table-striped>tbody>tr:nth-child(odd)>th {
  background-color: #ff0;
 } */
 /* .table-striped>tbody>tr:nth-child(even)>td, 
 .table-striped>tbody>tr:nth-child(even)>th {
  border-bottom: medium;
 } */
 /* .table-striped>thead>tr>th {
    background-color: #eee;
 } */
 .table-striped>tbody>tr:nth-child(even)>td {
  border-bottom: thin;
  border-bottom-style: solid;
 }
</style>
</head>

<form action="<%= portletURL %>" method="post" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">
<liferay-portlet:renderURLParams varImpl="portletURL" />

<div id="container">
 <div id="cabecera" align="center">
  <p> <%=secc.getDescripcion().toUpperCase() + " ( " + idSeccional + " )" %>  </p>
  <!-- </br> -->
  <p style="font-size: 12px; font-style: normal; text-align: left;" >Horario Atención de <%=StringUtils.checkNotEmpty(secc.getHorarioAtencion())?secc.getHorarioAtencion():"N/D" %>  </br>
  Domicilio: <%=domicilio %> </br>
  Contacto: <%=secc.getContacto() %> </br>
  Contactos: <%=contactosSecc %></br>
 </div>
 <div id="contenido">
  
  <h2>Galer&iacute;a</h2>
<%-- 		<legend><liferay-ui:message key="image-gallery" /></legend>
		<div>

		  <a href="#" target="" onclick="<portlet:namespace />subirImgGaleria();" >Agregar mas imagenes</a>
		  
		  <a href="#" target="" onclick="<portlet:namespace />prepararGaleria();" >Ver galería</a>
 		</div>  --%>		
  <p>
  	<ul>
  		<li>
  			<a href="#" target="" onclick="<portlet:namespace />subirImgGaleria();" >Agregar m&aacute;s im&aacute;genes</a>
		</li>
		<li>  
			<a href="#" target="" onclick="<portlet:namespace />prepararGaleria();" >Ver galería</a>
		</li>	
	</ul>
	<br/>
	<br/>
	
    <img style="width: 250px; height: 250px;" src="<%= urlUomaSeccional %>" >

  </p>
 </div>
 <div id="contenido">
  <h2>Comisión Ejecutiva</h2> <!-- Integrantes -->
  	<!-- <div> -->
  		<%ArrayList<Contacto> colaboradores = new ArrayList<Contacto>(); %>
		<ul>
         <% for(Contacto c:contactos){ 
         	if(!c.getCargoDescripcion().equalsIgnoreCase("ADMINISTRATIVO/SECRETARIA")){ %>	
		   <li><%= c.getCargoDescripcion() + " - " + c.getNombreApe() + " " + (StringUtils.checkNotEmpty(c.getTelefono())?c.getTelefono().toString():"") %> </li>
		   
		 <%}else{
		   colaboradores.add(c);
		   }	
		 } %>  
		 
		</ul> 
   <h2>Colaboradores</h2>
  	<!-- <div> -->
		<ul>
         <% for(Contacto c:colaboradores){ %>	
		   <li><%= c.getCargoDescripcion() + " - " + c.getNombreApe() + " " + (StringUtils.checkNotEmpty(c.getTelefono())?c.getTelefono().toString():"") %> </li>
		 <%} %>  
		</ul> 	
 	<!-- </div>  -->
	
 </div>
 
 <div id="contenido">
  <h2>Padrón</h2>
  
  <div>
  <%-- <ul>
   <li>OSPIM <%=desglosePadron.get("OSPIM") %></li>
   <li>UOMA <%=desglosePadron.get("UOMA") %></li>
   <li>AMTIMA <%=desglosePadron.get("AMTIMA") %></li>
   <li>USUFRUCTO <%=desglosePadron.get("USUFRUCTO") %></li>
  </ul> 
  <ul>
   <li>TITULARES <%=desglosePadron.get("TITULAR") %></li>
   <li>FAMILIARES <%=desglosePadron.get("FAMILIARES") %></li>
  </ul> 
  <ul>
   <li>PROPIOS <%=desglosePadron.get("PROPIOS") %></li>
   <li>DESREGULADOS <%=desglosePadron.get("DESREGULADOS") %></li>
  </ul>   		
  <ul>
   <li>MONOTRIBUTISTAS <%=desglosePadron.get("MONOTRIBUTISTAS") %></li>
   <li>MOLINEROS <%=desglosePadron.get("MOLINEROS") %></li>
  </ul>  --%>
  
  <table style="border-collapse: separate; border-spacing: 5px;">
  	<tr>
  		<td>
  			<table style="border-collapse: separate; border-spacing: 5px;">
  				<tr>
  					<td>UOMA</td><td> <%=desglosePadron.get("UOMA") %></td>
  				</tr>
  				<tr>
  					<td>AMTIMA</td><td> <%=desglosePadron.get("AMTIMA")!=null?desglosePadron.get("AMTIMA"):0 %></td>
  				</tr>
  				<tr>
  					<td>USUFRUCTO</td><td> <%=desglosePadron.get("USUFRUCTO")!=null?desglosePadron.get("USUFRUCTO"):0 %></td>
  				</tr>
  				<tr>
  					<td>OSPIM</td><td> <%=desglosePadron.get("OSPIM") %></td>
  				</tr>	
  			</table>
  		</td>
  	</tr>
  	
  	<tr>
  		<td>
  			<fieldset class="block-labels">
  				<legend><liferay-ui:message key="OSPIM" /></legend>
  			<table style="border-collapse: collapse; border-spacing: 5px;" class="table table-striped">
  				<tbody>
	  				<tr>
	  					<td>TITULARES</td><td>&nbsp;&nbsp;<%=desglosePadron.get("TITULAR") %></td>
	  				</tr>
	  				<tr>
	  					<td>FAMILIARES</td><td>&nbsp;&nbsp;<%=desglosePadron.get("FAMILIARES") %></td>
	  				</tr>
	  				<tr>
	  					<td>PROPIOS</td><td>&nbsp;&nbsp;<%=desglosePadron.get("PROPIOS")!=null?desglosePadron.get("PROPIOS"):0 %></td>
	  				</tr>
	  				<tr>
	  					<td>DESREGULADOS</td><td>&nbsp;&nbsp;<%=desglosePadron.get("DESREGULADOS")!=null?desglosePadron.get("DESREGULADOS"):0 %></td>
	  				</tr>
	  				<tr>
	  					<td>MONOTRIBUTISTAS</td><td>&nbsp;&nbsp;<%=desglosePadron.get("MONOTRIBUTISTAS")!=null?desglosePadron.get("MONOTRIBUTISTAS"):0 %></td>
	  				</tr>
	  				<tr>
	  					<td>MOLINEROS</td><td>&nbsp;&nbsp;<%=desglosePadron.get("MOLINEROS")!=null?desglosePadron.get("MOLINEROS"):0 %></td>
	  				</tr>
	  			</tbody>	
  			</table>
  			</fieldset>			
  		</td>
  	</tr>
  	<tr>
  		<td><img id="<portlet:namespace />reporte" alt="Exportar Planilla" src="<%=themeDisplay.getPathThemeImages() + "/document_library/xls.png" %>">&nbsp;Exportar </td>
  	</tr>
  </table> 		
  </div>
 </div>
 
 
 <div id="contenido">
 
   <h2>Empresas</h2>
  	<div>
		<ul>
		<%if(empresas == null || empresas.size()==0){ %>
			<li>No se encontraron empresas asociadas a esta seccional</li>
		<%}%>
         <% for(Empresa c:empresas){ %>	
		   <li>
		      <a href="#" target="" onclick="<portlet:namespace />seguimientoEmpresas(<%=c.getCuit() %>,<%=c.getSucursal()%>);" >
		        <%= c.getRazon_soc() + " (" + c.getCuit()+")"  %> 
			  </a>
			</li>
		 <%} %>  
		</ul> 
		
 	</div> 
</div>


<div id="container">
 
 <div id="contenido">
  
  <h2>Gestiones</h2>
  <p>
  	<liferay-ui:input-date dayParam="fechaActualDia"
					dayValue="<%= fechaActual.get(Calendar.DATE) %>"
					monthParam="fechaActualMes"
					monthValue="<%= fechaActual.get(Calendar.MONTH) %>"
					yearParam="fechaActualAnio"
					yearValue="<%= fechaActual.get(Calendar.YEAR) %>"
					yearRangeStart="<%= fechaActual.get(Calendar.YEAR) - 2 %>"
					yearRangeEnd="<%= fechaActual.get(Calendar.YEAR) + 2 %>"
					firstDayOfWeek="<%= fechaActual.getFirstDayOfWeek()%>"
					disabled="<%= false %>" />
  </p>
  <p>
  	<textarea rows="3" cols="30" name="<portlet:namespace />obs_gestion" id="<portlet:namespace />obs_gestion"></textarea>
  </p>
  <p>
  	<input type="button" value="Ingresar" onclick="javascript:insertarGestion();"> 
  </p>

  <div id="<portlet:namespace />gestionesResultadosDiv" style="width: 100%; overflow-y: scroll;" >
		<liferay-util:include page="/html/portlet/seccional/gestiones_seccional_search_result.jsp">
		</liferay-util:include>
  </div>
 </div>
 <div id="contenido">
 
 
   <h2>Inversiones - Centros de Costos</h2>
  	<div>
		<ul>
		<%if(centros == null || centros.size()==0){ %>
			<li>No se encontraron centros de costos para esta seccional</li>
		<%}%>
         <% for(CentroCosto c:centros){ %>	
		   <li>
		      <a href="#" target="" onclick="<portlet:namespace />centrosCosto(<%=c.getId() %>);"> <%=c.getDescripcion()%></a>
		      <br>
			</li>
			<table style="border-spacing: 3px;">
			  <tr>
			    <td>Presupuesto:&nbsp;&nbsp;</td>
			    <td>&nbsp;<%=formatter.format(c.getPresupuesto())%></td>
			  </tr>
			  <tr>
			    <td>Ejecución: &nbsp;&nbsp;</td>
			    <td>&nbsp;<%=formatter.format(c.getEjecutado())%> </td>
			 </tr>   
		    </table> 
		 <%} %>  
		</ul> 
		
 	</div> 
 </div>
 
 <div id="contenido50">
  
  <h2>Gastos</h2>
  	<p>
  		<label><liferay-ui:message key="fecha-desde" />:</label>
				<liferay-ui:input-date
					dayParam="fechaDesdeDia"
					dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
					dayNullable="<%= true %>" 
					monthParam="fechaDesdeMes"
					monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
					monthNullable="<%= true %>"				
					yearParam="fechaDesdeAnio"
					yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
					yearNullable="<%= true %>"
					yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
					yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 10 %>"
					firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
					disabled="<%= false %>" />
  		<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onclick="<portlet:namespace />buscarExplosionCpteIngEgr();" />	
  	</p>
  	<div align="center" id="<portlet:namespace />buscandoControlIngEgrCte">
	<table style="align: center;">
		<tr>
			<td><liferay-ui:message key='buscando'/></td>
			<td align="center"><img
				alt="<liferay-ui:message key='buscando'/>"
				src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
			</td>
		</tr>
	</table>
	</div>
  	<div align="center" id="<portlet:namespace />busquedaControlIngEgrCteDiv">
  		<liferay-util:include page="/html/portlet/uoma/reportes/control_ingresos_egresos_explosion_cpte.jsp"/>	
  	</div>
  
 </div>
  
 <!-- <div id="contenido">
  contenido
  <h2>La Ñ con utf-8</h2>
  <p>
  Podemos usar cualquier caracter de nuestro queridisimo lenguaje
  con la codificación UTF-8
  Ñ ñ á é í ó ú Á É Í Ó Ú ç Ç
  </p>
  <h2>Titulo dentro del contenido</h2>
  <p>
  Texto dentro del contenido, texto dentro del contenido
  Ah los diseñadores gráficos usan el siguiente texto como borrador 
  </p>
  <h2>Lorem ipsum dolor sit amet</h2>
  <p>
Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
  </p>
  
 </div> -->
 
<div id="pie">
  pie
 </div>
</div>


<script type="text/javascript">


/* var seccionalImg;
var popUpCierre; */
jQuery('#<portlet:namespace />buscandoControlIngEgrCte').hide();


function <portlet:namespace />subirImgGaleria(){
	
	var galeria;
    var popUpCierre;
    var params = "";
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/sec/upload_imagenes_seccional" /></portlet:renderURL>';
	url = url +'&id_seccional=<%=idSeccional %>';
	
	galeria = Liferay.Popup({title:"<liferay-ui:message key="Galería" />",modal:true,width:1200});

	jQuery(galeria).load(url,params, function(){});		 
	
	
<%--  	var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
    '<liferay-portlet:param name="struts_action" value="/sec/upload_imagenes_seccional"/>'+
/*     '<liferay-portlet:param name="name" value="__Name"/>'+
    '<liferay-portlet:param name="folderId" value="__FolderId"/>'+ */
    '</liferay-portlet:actionURL>';      
    /* url = url.replace("__Name",fileName).replace("__FolderId",folderId); */
    window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') ; --%>
    
}	


function <portlet:namespace />uploadImagenSeccional() {	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_seccional';
	document.<portlet:namespace />fm.method = 'post';
	url = url+'&imagen='+'<%=Constants.ADD%>'+'&id_seccional=<%=idSeccional %>';
	submitForm(document.<portlet:namespace />fm, url);
}

function verImagenSeccional(folderId,fileName){
   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
   '<liferay-portlet:param name="struts_action" value="/afiliados/documentacion_adjunta_recuperar"/>'+
   '<liferay-portlet:param name="name" value="__Name"/>'+
   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
   '</liferay-portlet:actionURL>';      
   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
   window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}

function deleteImagenSeccional(folderId,fileName) {	
	var confirmar=false;
	confirmar = confirm ('Está seguro de eliminar este documento');
	if(confirmar){	
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_seccional';						
		document.<portlet:namespace />fm.method = 'post';
		url = url+'&imagen'+'='+'<%=Constants.DELETE %>';
		url += "&folderid="+folderId;
		url += "&filename="+fileName;
		submitForm(document.<portlet:namespace />fm, url);
	}else{
		return false;
	}	
}

function <portlet:namespace />prepararGaleria(){

	var visitaGaleria;
    var params = {'<%=Constants.CMD%>':'<%=Constants.PREVIEW%>','id_seccional':<%=idSeccional%>};
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/sec/upload_imagenes_seccional" /></portlet:renderURL>';
	visitaGaleria = Liferay.Popup({title:"<liferay-ui:message key="La Galería de Imágenes" />",modal:true,width:400});
	jQuery(visitaGaleria).load(url,params, function(){});		 
    
}	

function insertarGestion(){
	
	var idSec = <%=idSeccional%>;
	
	var diaDesde=jQuery('#<portlet:namespace />fechaActualDia').val();	    
    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaActualMes').val())+1;	    
    var anioDesde=jQuery('#<portlet:namespace />fechaActualAnio').val();
    var fechaFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
    
	var obs = jQuery('#<portlet:namespace />obs_gestion').val();
	var obser = obs.trim();
	
	jQuery('#<portlet:namespace />ejecutaDerivacion').hide(); 
		
	var params = {"id_seccional":idSec, "fecha_final":fechaFinal, "gestion_observaciones":obser};	
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/sec/buscar_gestiones_seccional';
	
    jQuery('#<portlet:namespace />gestionesResultadosDiv').load(url,params, function() {
    	jQuery('#<portlet:namespace />obs_gestion').val('');
    		/* Liferay.Popup.close(popupDeriva); */          															
    		 }
    );
	
}

function <portlet:namespace />seguimientoEmpresas(cuit,sucursal){
	var newURL = window.location.protocol + "//" + window.location.host //+ "/" + window.location.pathname;
	var url = newURL+"/web/guest/estudio-isidro1?p_p_id=EST_1&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_EST_1_struts_action=%2Festudio_isidro%2Fbuscar_seguimiento_molinera&cuit="+cuit;
	window.open(url,'mywindow');
}

function <portlet:namespace />centrosCosto(idCentro){
    var newURL = window.location.protocol + "//" + window.location.host
	var url = newURL+"/web/guest/uoma?p_p_id=UOM_1&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_UOM_1_struts_action=%2Fuoma%2Fcentro_costo_edicion&cmd=detalleComprobantes&usuario_modi=<%=usuario_modi%>&entidad_centro=1&id_centro_costo="+idCentro;
	
	window.open(url,'mywindow');
}


function <portlet:namespace />buscarExplosionCpteIngEgr(){

	jQuery('#<portlet:namespace />buscandoControlIngEgrCte').show();
	
	var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDia').val();	    
    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMes').val())+1;	    
    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
	
	var busquedaCte = { "fechaDesdeFinal": fechaDesdeFinal, 
						"id_seccional": <%=idSeccional%>,
						"cmd" : "<%=Constants.SEARCH%>"};

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/sec/view" /></portlet:renderURL>';
	
    jQuery('#<portlet:namespace />busquedaControlIngEgrCteDiv').load(url,busquedaCte, function() {
    																jQuery('#<portlet:namespace />buscandoControlIngEgrCte').hide();            															
    															  }
    );

}

jQuery('#<portlet:namespace />reporte').click(function(){
	
	window.location.href ='/xlsservlet/?reporte=LISTADO_PADRON_SECCIONAL'
		+'&idSeccional='+<%=idSeccional%>
		<%-- +'&descSeccionales='+<%=secc.getDescripcion()%> --%>
		+'&fechaDesdeMes=1'
		+'&fechaHastaMes=1'
		+'&tituYFliares=0'
		+'&tituYFliaresDesc=Titulares y Familiares'
		+'&tipoBusqueda=4';
});


</script>

</form>




