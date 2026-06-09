<c:if test="<%= mostrarPanelAfiliadoEnVista %>">
    <fieldset class="block-labels">
        <legend>
            <liferay-ui:message key="datos-afiliado" />
        </legend>

        <table class="lfr-table compras-afiliado-readonly">
            <tr>
                <td><label>CUIL titular:</label></td>
                <td><%= HtmlUtil.escape(afiliadoCuilVisible) %></td>
                <td><label>Integrante:</label></td>
                <td><%= HtmlUtil.escape(afiliadoIntVisible) %></td>
            </tr>

            <tr>
                <td><label>Tipo documento:</label></td>
                <td><%= HtmlUtil.escape(afiliadoTipoDocumento) %></td>
                <td><label>Número documento:</label></td>
                <td><%= HtmlUtil.escape(afiliadoNumeroDocumento) %></td>
            </tr>

            <tr>
                <td><label>Apellido:</label></td>
                <td><%= HtmlUtil.escape(afiliadoApellido) %></td>
                <td><label>Nombre:</label></td>
                <td><%= HtmlUtil.escape(afiliadoNombre) %></td>
            </tr>

            <tr>
                <td><label>ID seccional:</label></td>
                <td><%= HtmlUtil.escape(afiliadoIdSeccional) %></td>
                <td><label>Seccional:</label></td>
                <td><%= HtmlUtil.escape(afiliadoSeccional) %></td>
            </tr>

            <tr>
                <td><label>Número afiliado:</label></td>
                <td><%= HtmlUtil.escape(afiliadoNumeroAfiliado) %></td>
                <td><label>OSPIM:</label></td>
                <td><%= HtmlUtil.escape(afiliadoNumeroOspim) %></td>
            </tr>

            <tr>
                <td><label>UOMA:</label></td>
                <td><%= HtmlUtil.escape(afiliadoNumeroUoma) %></td>
                <td><label>AMTIMA:</label></td>
                <td><%= HtmlUtil.escape(afiliadoNumeroAmtima) %></td>
            </tr>

            <tr>
                <td><label>Plan:</label></td>
                <td><%= HtmlUtil.escape(afiliadoNombrePlan) %></td>
                <td><label>ID plan:</label></td>
                <td><%= HtmlUtil.escape(afiliadoIdPlan) %></td>
            </tr>

            <tr>
                <td><label>ID tercerizadora:</label></td>
                <td><%= HtmlUtil.escape(afiliadoIdTercerizadora) %></td>
                <td><label>Tercerizadora:</label></td>
                <td><%= HtmlUtil.escape(afiliadoAfiTercerizadora) %></td>
            </tr>

            <tr>
                <td><label>Fecha alta:</label></td>
                <td><%= HtmlUtil.escape(afiliadoFechaAlta) %></td>
                <td><label>Fecha baja:</label></td>
                <td>
                    <span style="<%= !WebKeysCompras.isEmpty(afiliadoBajaFecha) ? "background:red;color:white;padding:2px 4px;" : "" %>">
                        <%= HtmlUtil.escape(afiliadoBajaFecha) %>
                    </span>
                </td>
            </tr>

            <tr>
                <td><label>Incapacidad:</label></td>
                <td><%= HtmlUtil.escape(afiliadoIncapacidad) %></td>
                <td><label>Antecedentes:</label></td>
                <td><%= HtmlUtil.escape(afiliadoAntecedentes) %></td>
            </tr>
        </table>
    </fieldset>
</c:if>
