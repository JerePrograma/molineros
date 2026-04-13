<%@include file="/html/portlet/seccional/init.jsp"%>


<portlet:defineObjects />

<%
String dibu = (String) request.getAttribute("ImgSeccional");
String idSeccional = (String) request.getAttribute("idSeccional");
Integer pos = (Integer)request.getAttribute("posicion");

%>

<p>
<%-- <img src="/imgservlet?idseccional=103,${AAA}" alt="Smiley face" height="300" width="300" /> --%>
<!-- <img src="/sec/upload_imagenes_seccional?idseccional=103&cmd='PREVIEW'" alt="Smiley face" height="300" width="300" /> -->
<div id="galeria">
   <img style="width: 350px; height: 350px;" src="data:image;base64,<%= dibu %>"
</div>
</p>
<p> <a href="#" onclick="siguiente();" >Siguiente</a> </p>
<p> <a href="#" onclick="anterior();" > Anterior</a> </p>




<script type="text/javascript">

function siguiente(){
	var params = {'<%=Constants.CMD%>':'<%=Constants.PREVIEW%>','id_seccional':<%=idSeccional%>, 'posicion' :<%= pos +1%>};

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/sec/upload_imagenes_seccional" /></portlet:renderURL>';

	jQuery("#galeria").load(url,params, function(){});		

}

function anterior(){
	
	var params = {'<%=Constants.CMD%>':'<%=Constants.PREVIEW%>','id_seccional':<%=idSeccional%>, 'posicion' :<%= pos -1%>};

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/sec/upload_imagenes_seccional" /></portlet:renderURL>';

	jQuery("#galeria").load(url,params, function(){});		

}

</script>