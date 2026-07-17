<%@ page import="ar.com.ospim.autorizaciones.beans.SituacionMedica" %>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

<%
String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
    portlet_name = "autorizaciones";
}

String cmd = (String) request.getAttribute(Constants.CMD);
String modoConsulta = (String) request.getAttribute("ModoConsulta");

String idSituacionMedica = ParamUtil.getString(request, "idSituacionMedica", null);

if (idSituacionMedica == null || idSituacionMedica.trim().equals("")) {
    idSituacionMedica = ParamUtil.getString(request, "id_registro_sitmed", null);
}

if ((idSituacionMedica == null || idSituacionMedica.trim().equals("")) && request.getAttribute("idSituacionMedica") != null) {
    idSituacionMedica = String.valueOf(request.getAttribute("idSituacionMedica"));
}

if ((idSituacionMedica == null || idSituacionMedica.trim().equals("")) && request.getAttribute("id_registro_sitmed") != null) {
    idSituacionMedica = String.valueOf(request.getAttribute("id_registro_sitmed"));
}

if (idSituacionMedica == null || idSituacionMedica.trim().equals("") || "0".equals(idSituacionMedica)) {
    SituacionMedica situacionMedicaEnEdicion =
        (SituacionMedica) request.getSession().getAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_EN_EDICION);

    if (situacionMedicaEnEdicion != null) {
        idSituacionMedica = String.valueOf(situacionMedicaEnEdicion.getId_Situacion());
    }
}

if (idSituacionMedica == null || idSituacionMedica.trim().equals("")) {
    idSituacionMedica = "0";
}
%>

<form action="UploadImagenesSituacionMedicaAction"
      method="post"
      name="<portlet:namespace />situacionmedica_fm"
      id="<portlet:namespace />situacionmedica_fm"
      enctype="multipart/form-data">

    <fieldset class="block-labels">
        <legend>Imágenes de Situación Médica</legend>

        <liferay-ui:error key="errorUploadFile" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />

        <table class="lfr-table">
            <tr>
                <% if (!"si".equalsIgnoreCase(modoConsulta)) { %>

                    <td>Añadir Imagen:</td>

                    <td>
                        <input type="file"
                               name="importa_imagenes"
                               id="importa_imagenes"/>
                    </td>

                    <td>&nbsp;</td>

                    <td>
                        <label><liferay-ui:message key="descripcion" />:</label>
                    </td>

                    <td>
                        <input id="<portlet:namespace />descripcionFile"
                               name="<portlet:namespace />descripcionFile"
                               size="90"
                               maxlength="120"
                               type="text"
                               value="" />
                    </td>

                    <td>
                        <input id="<portlet:namespace />uploadIMGSituacionMedica"
                               value="<liferay-ui:message key="subir-archivo"/>"
                               title="<liferay-ui:message key="subir-archivo" />"
                               onClick="javascript: <portlet:namespace />uploadImagenSituacionMedica('archivos');"
                               type="button" />
                    </td>

                <% } else { %>

                    <td><b>Solo Consulta</b></td>

                <% } %>
            </tr>

            <tr>
                <td>&nbsp;</td>
            </tr>
        </table>
    </fieldset>

    <div id="<portlet:namespace />listado_imagenes_situacionmedica">
        <jsp:include page="/html/portlet/autorizaciones/patologias/sitmedica_imagenes_search_documentos.jsp" />
    </div>

    <input type="hidden"
           name="idSituacionMedica"
           id="idSituacionMedica"
           value="<%= idSituacionMedica %>" />

    <input type="hidden"
           name="id_registro_sitmed"
           id="id_registro_sitmed"
           value="<%= idSituacionMedica %>" />

</form>

<script type="text/javascript">

function <portlet:namespace />uploadImagenSituacionMedica(solapa) {
	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_situacionmedica';
	
	document.<portlet:namespace />situacionmedica_fm.method = 'post';

	url = url + '&imagen=<%=Constants.ADD%>';
	url = url + '&idSituacionMedica=<%=idSituacionMedica%>';
	url = url + '&id_registro_sitmed=<%=idSituacionMedica%>';
	url = url + '&solapa=' + solapa;

 	submitForm(document.<portlet:namespace />situacionmedica_fm, url);
}

function verImagenSituacionMedica(folderId, fileName){
	
	var url = '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/autorizaciones/documentacion_adjunta_recuperar"/>'+
		'<liferay-portlet:param name="name" value="__Name"/>'+
		'<liferay-portlet:param name="folderId" value="__FolderId"/>'+
		'</liferay-portlet:actionURL>';

	url = url.replace("__Name", fileName).replace("__FolderId", folderId);

	window.open(url, 'mywindow', 'width=800,height=800,toolbar=no,resizable=yes');
}

function deleteImagenSituacionMedica(folderId, fileName, solapa) {
	var confirmar = false;

	<%
	if (cmd != null && cmd.equalsIgnoreCase(Constants.VIEW)) {
	%>
		alert('Se encuentra en modo consulta, no puede eliminar este archivo.');
		return false;
	<%
	}
	%>

	confirmar = confirm('Está seguro de eliminar este documento');

	if(confirmar){
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_situacionmedica';

		document.<portlet:namespace />situacionmedica_fm.method = 'post';

		url = url + '&imagen=<%=Constants.DELETE %>';
		url = url + '&folderid=' + folderId;
		url = url + '&filename=' + fileName;
		url = url + '&idSituacionMedica=<%=idSituacionMedica%>';
		url = url + '&id_registro_sitmed=<%=idSituacionMedica%>';
		url = url + '&solapa=' + solapa;

		submitForm(document.<portlet:namespace />situacionmedica_fm, url);
	}else{
		return false;
	}
}

</script>