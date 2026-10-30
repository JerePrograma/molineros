<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>

<portlet:defineObjects/>

<%
    SimpleDateFormat formatoPeriodo =
        new SimpleDateFormat("MMMM yyyy", new Locale("es", "AR"));

    String periodoActual = formatoPeriodo.format(new Date());

    periodoActual =
        periodoActual.substring(0, 1).toUpperCase()
        + periodoActual.substring(1);
%>

<form action="" method="post" name="<portlet:namespace />fm">

    <div id="<portlet:namespace />div_padrones_ppp">

        <fieldset class="block-labels">

            <legend>Listado de Padrones PPP</legend>

            <table class="lfr-table">

                <tr>
                    <td>
                        <label>Período:</label>
                    </td>

                    <td>
                        <select
                            id="<portlet:namespace />periodo"
                            name="<portlet:namespace />periodo"
                            disabled="disabled">

                            <option value="<%= periodoActual %>">
                                <%= periodoActual %>
                            </option>

                        </select>
                    </td>

                    <td>
					    <input
					        id="<portlet:namespace />descargar"
					        type="button"
					        value="Descargar"
					        title="Descargar"
					        onclick="<portlet:namespace />descargarPadronesPPP();" />
					</td>
					
					<td>
					    <a href="javascript:void(0)"
					       onclick="help(event, 'helpPadronesPPP')">
					        <img
					            style="height:16px;width:16px"
					            src="/html/images/help.png"
					            title="Ayuda"
					            alt="Ayuda"/>
					    </a>
					</td>

					<td>
					    <span id="<portlet:namespace />buscando" style="display:none;">
					        Descargando archivo...
					        <img
					            alt="Buscando"
					            src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif"
					            style="vertical-align:middle;" />
					    </span>
					</td>

                </tr>

            </table>

        </fieldset>

    </div>

</form>

<div id="helpPadronesPPP"
     class="containerPlus draggable {buttons:'c', skin:'default', width:'460',title:'Ayuda',closed:'true'}"
     style="top:100px; left:250px;">

    <div style="padding:8px 10px; line-height:1.4;">

        <span style="white-space:nowrap;">
            Se muestran los afiliados vigentes en los últimos 30 días con aportes
        </span>
        <br/>
        OSPIM MONOTRIBUTISTA
        <br/>
        OSPIM GENERAL y OSPIM OSPIM GENERAL

    </div>

</div>

<script type="text/javascript">

var <portlet:namespace />intervaloPPP = null;

function <portlet:namespace />descargarPadronesPPP() {

    var boton = jQuery('#<portlet:namespace />descargar');
    var buscando = jQuery('#<portlet:namespace />buscando');

    if (boton.attr('disabled')) {
        return;
    }

    boton.attr('disabled', true);
    buscando.show();

    var descargaId = new Date().getTime().toString();

    document.cookie = 'descargaPPP=; path=/; max-age=0';
    clearInterval(<portlet:namespace />intervaloPPP);

    <portlet:namespace />intervaloPPP = setInterval(function() {

        if (document.cookie.indexOf('descargaPPP=' + descargaId) !== -1) {

            clearInterval(<portlet:namespace />intervaloPPP);
            buscando.hide();
            boton.attr('disabled', false);
            document.cookie = 'descargaPPP=; path=/; max-age=0';
        }
    }, 500);

    window.location.href ='/txtservlet/?reporte=REPORTE_PADRONES_PPP' + '&descargaId=' + descargaId;
}

</script>