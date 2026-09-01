<%@ include file="/html/portlet/prestadores/init.jsp"%>

<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.prestadores.services.ImportarCartillaSOPServiceUtil" %>
<%

	List<Object[]> importaciones =
		(List<Object[]>) request.getAttribute("importacionesCartillaSOP");

	if (importaciones == null) {
		try {
			importaciones = ImportarCartillaSOPServiceUtil.getImportacionesCartillaSOP();
		} catch (Exception e) {
			importaciones = Collections.emptyList();
		}
	}

	SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
%>

<portlet:renderURL var="volverCartillaSopURL" windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
    <portlet:param name="struts_action" value="/prestadores/view"/>
    <portlet:param name="tabs1" value="cartilla-sop"/>
</portlet:renderURL>


<portlet:actionURL var="importarCartillaSopURL" windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
    <portlet:param name="struts_action" value="/prestadores/cartilla_sop"/>
</portlet:actionURL>

<form
    id="<portlet:namespace />formCartillaSOP"
    name="<portlet:namespace />formCartillaSOP"
    action="<%= importarCartillaSopURL %>"
    method="post"
    enctype="multipart/form-data"
    onsubmit="return <portlet:namespace />importarCartillaSOP();">

    <input
        type="hidden"
        name="redirect"
        value="<%= volverCartillaSopURL %>"/>

    <fieldset class="block-labels">

        <legend>Importar Cartilla SOP</legend>

        <table class="lfr-table" style="width: 100%;">
            <tr>
                <td style="vertical-align: top; width: 45%;">
                    <table class="lfr-table">
                        <tr>
                            <td>
                                <label for="<portlet:namespace />archivo">
                                    Seleccionar archivo:
                                </label>
                            </td>
                            <td>
                                <input
                                    type="file"
                                    id="<portlet:namespace />archivo"
                                    name="archivo"
                                    accept=".xlsx"/>
                            </td>
                            <td>
                                <a href="javascript:void(0)" onclick="help(event, 'helpCartillaSOP')">
								    <img
								        style="height: 16px; width: 16px"
								        src="/html/images/help.png"
								        title="Ayuda"
								        alt="Ayuda"/>
								</a>
                            </td>
                            <td>
                                <input
                                    id="<portlet:namespace />botonImportar"
                                    type="submit"
                                    value="Subir archivo"/>
                            </td>
                        </tr>
                    </table>

                    <div id="<portlet:namespace />procesandoCartillaSOP" style="display: none; margin-top: 15px;">

                        <liferay-ui:message key="procesando"/>

                        <img alt="Procesando"
                            src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif"/>
                    </div>

                </td>

				  <td style="vertical-align: top; width: 55%;">
				    <table style="width: 100%;">
				        <tr>
				            <td>
				                <label>
				                    Listado de Cartilla SOP importados:
				                </label>
				            </td>
				        </tr>

				        <tr>
				            <td>&nbsp;</td>
				        </tr>

				        <% if (importaciones.isEmpty()) { %>

				            <tr>
				                <td>
				                    No hay archivos subidos.
				                </td>
				            </tr>

				        <% } else { %>

				            <tr>
				                <td>
				                    Mostrando <%= importaciones.size() %> resultados.
				                </td>
				            </tr>

				            <tr>
				                <td>&nbsp;</td>
				            </tr>

				            <tr>
				                <td>

				                    <table class="cartilla-sop-tabla">

				                        <thead>
				                            <tr>
				                                <th>
				                                    Fecha Importación
				                                </th>

				                                <th>
				                                    Cantidad de registros
				                                </th>
				                            </tr>
				                        </thead>

				                        <tbody>

				                            <%
				                                int numeroFila = 0;

				                                for (
				                                    Object[] importacion :
				                                    importaciones
				                                ) {
				                                    String claseFila =
				                                        numeroFila % 2 == 0
				                                            ? "cartilla-sop-fila-par"
				                                            : "cartilla-sop-fila-impar";

				                                    java.sql.Timestamp fechaImportacion =
				                                        (java.sql.Timestamp)
				                                            importacion[1];

				                                    Integer cantidadRegistros =
				                                        (Integer)
				                                            importacion[2];
				                            %>

				                                <tr class="<%= claseFila %>">

				                                    <td>
				                                        <%
				                                            if (
				                                                fechaImportacion != null
				                                            ) {
				                                        %>

				                                            <%= formatoFecha.format(
				                                                fechaImportacion
				                                            ) %>

				                                        <%
				                                            } else {
				                                        %>

				                                            -

				                                        <%
				                                            }
				                                        %>
				                                    </td>

				                                    <td>
				                                        <%= cantidadRegistros %>
				                                    </td>

				                                </tr>

				                            <%
				                                    numeroFila++;
				                                }
				                            %>

				                        </tbody>

				                    </table>

				                </td>
				            </tr>

				        <% } %>

				    </table>

				</td>

            </tr>

        </table>

    </fieldset>

</form>

<div
    id="helpCartillaSOP"
    class="containerPlus draggable {buttons:'c', skin:'default', width:'700', title:'Ayuda', closed:'true'}"
    style="top: 100px; left: 250px">

    El diseño del archivo Cartilla SOP es:

	<br/><br/>

	1. nombre<br/>
	2. direccion<br/>
	3. telefono<br/>
	4. localidad<br/>
	5. provincia

	<hr/>

	El archivo informado debe llamarse
	<b>cartilla_sop.xlsx</b>.

	<hr/>

	El sistema valida que:

	<ul>
	    <li>
	        El nombre del archivo cumpla con el formato requerido.
	    </li>

	    <li>
	        El archivo incluya la primera fila con los nombres de
	        las columnas (encabezado).
	    </li>
	</ul>

</div>

<script type="text/javascript">

function <portlet:namespace />importarCartillaSOP() {

    var archivo = document.getElementById("<portlet:namespace />archivo");
    var boton = document.getElementById("<portlet:namespace />botonImportar");
    var procesando = document.getElementById("<portlet:namespace />procesandoCartillaSOP");

    if (archivo == null || archivo.value == null || archivo.value == "") {
        alert("Debe seleccionar un archivo Excel.");
        return false;
    }

    boton.disabled = true;
    boton.value = "Procesando...";

    procesando.style.display = "block";

    return true;
}

</script>

<style type="text/css">

    .cartilla-sop-tabla {
        width: 100%;
        border-collapse: collapse;
    }

    .cartilla-sop-tabla th {
        background-color: #918580;
        color: #FFFFFF;
        font-weight: bold;
        padding: 8px 6px;
        text-align: center;
    }

    .cartilla-sop-tabla td {
        padding: 7px 6px;
        text-align: center;
    }

    .cartilla-sop-fila-par {
        background-color: #F3EEEB;
    }

    .cartilla-sop-fila-impar {
        background-color: #DED9D6;
    }

</style>
