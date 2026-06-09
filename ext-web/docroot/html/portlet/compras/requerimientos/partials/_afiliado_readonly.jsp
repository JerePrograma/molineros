<%
	boolean afiliadoVistaTieneAntecedentes =
		!WebKeysCompras.isEmpty(afiliadoAntecedentes)
		&& !"0".equals(afiliadoAntecedentes.trim())
		&& !"false".equalsIgnoreCase(afiliadoAntecedentes.trim())
		&& !"no".equalsIgnoreCase(afiliadoAntecedentes.trim());

	String afiliadoVistaPanelClass = afiliadoVistaTieneAntecedentes ? " afiliado-con-antecedentes-panel" : "";

	String afiliadoBajaFechaStyle = !WebKeysCompras.isEmpty(afiliadoBajaFecha)
		? "background:red;color:white;"
		: "background:white;color:black;";
%>

<style type="text/css">
	#<portlet:namespace />panelDatosAfiliadoVista {
		position: relative;
	}

	#<portlet:namespace />panelDatosAfiliadoVista.afiliado-con-antecedentes-panel {
		background: #fdeaea !important;
		border: 1px solid #d9a3a3 !important;
		border-left: 6px solid #c62828 !important;
		border-radius: 4px;
		padding: 6px;
		padding-top: 34px;
	}

	#<portlet:namespace />panelDatosAfiliadoVista.afiliado-con-antecedentes-panel td,
	#<portlet:namespace />panelDatosAfiliadoVista.afiliado-con-antecedentes-panel span,
	#<portlet:namespace />panelDatosAfiliadoVista.afiliado-con-antecedentes-panel b {
		color: #333333 !important;
	}

	#<portlet:namespace />panelDatosAfiliadoVista.afiliado-con-antecedentes-panel label {
		color: #7a1f1f !important;
		font-weight: bold;
	}

	#<portlet:namespace />panelDatosAfiliadoVista input,
	#<portlet:namespace />panelDatosAfiliadoVista select,
	#<portlet:namespace />panelDatosAfiliadoVista textarea {
		background: #f3f3f3;
		color: #222222;
		border: 1px solid #d0d0d0;
	}

	#<portlet:namespace />panelDatosAfiliadoVista.afiliado-con-antecedentes-panel input,
	#<portlet:namespace />panelDatosAfiliadoVista.afiliado-con-antecedentes-panel select,
	#<portlet:namespace />panelDatosAfiliadoVista.afiliado-con-antecedentes-panel textarea {
		background: #ffffff !important;
		color: #222222 !important;
		border: 1px solid #c9c9c9 !important;
	}

	#<portlet:namespace />antecedentesJudicialesBoxVista {
		display: <%= afiliadoVistaTieneAntecedentes ? "block" : "none" %>;
		position: absolute;
		top: 6px;
		right: 12px;
		z-index: 2;
		white-space: nowrap;
		font-weight: bold;
	}

	#<portlet:namespace />antecedentesJudicialesLabelVista {
		display: inline-block;
		padding: 2px 8px;
		background: #c62828;
		border: 1px solid #8e0000;
		border-radius: 4px;
		color: #ffffff !important;
		line-height: 1.2;
	}
</style>

<c:if test="<%= mostrarPanelAfiliadoEnVista %>">
	<fieldset class="block-labels">
		<legend>
			<liferay-ui:message key="datos-afiliado" />
		</legend>

		<div id="<portlet:namespace />panelDatosAfiliadoVista" class="<%= afiliadoVistaPanelClass %>">

			<div id="<portlet:namespace />antecedentesJudicialesBoxVista">
				<span id="<portlet:namespace />antecedentesJudicialesLabelVista">
					Antecedentes Judiciales
				</span>
			</div>

			<table class="lfr-table compras-afiliado-readonly" style="width:100%; border-collapse: separate; border-spacing: 5px;">

				<tr>
					<td><label>CUIL titular:</label></td>
					<td>
						<input id="<portlet:namespace />cuilVista"
							   name="<portlet:namespace />cuilVista"
							   size="13"
							   maxlength="11"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoCuilVisible) %>" />
					</td>

					<td><label>Integrante:</label></td>
					<td>
						<input id="<portlet:namespace />inteVista"
							   name="<portlet:namespace />inteVista"
							   size="2"
							   maxlength="2"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoIntVisible) %>" />
					</td>

					<td><label>Tipo documento:</label></td>
					<td>
						<input id="<portlet:namespace />tipoDocVista"
							   name="<portlet:namespace />tipoDocVista"
							   size="5"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoTipoDocumento) %>" />
					</td>

					<td><label>Número documento:</label></td>
					<td>
						<input id="<portlet:namespace />nroDocVista"
							   name="<portlet:namespace />nroDocVista"
							   size="9"
							   maxlength="8"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoNumeroDocumento) %>" />
					</td>
				</tr>

				<tr>
					<td><label>Seccional:</label></td>
					<td colspan="3">
						<input id="<portlet:namespace />seccionalVista"
							   name="<portlet:namespace />seccionalVista"
							   size="40"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoSeccional) %>" />
					</td>

					<td><label>ID seccional:</label></td>
					<td>
						<input id="<portlet:namespace />idSeccionalVista"
							   name="<portlet:namespace />idSeccionalVista"
							   size="6"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoIdSeccional) %>" />
					</td>

					<td><label>Plan:</label></td>
					<td>
						<input id="<portlet:namespace />nombrePlanVista"
							   name="<portlet:namespace />nombrePlanVista"
							   size="20"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoNombrePlan) %>" />
					</td>
				</tr>

				<tr>
					<td><label>Apellido:</label></td>
					<td colspan="2">
						<input id="<portlet:namespace />apellidoVista"
							   name="<portlet:namespace />apellidoVista"
							   size="20"
							   maxlength="100"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoApellido) %>" />
					</td>

					<td><label>Nombre:</label></td>
					<td colspan="2">
						<input id="<portlet:namespace />nombreVista"
							   name="<portlet:namespace />nombreVista"
							   size="20"
							   maxlength="100"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoNombre) %>" />
					</td>

					<td><label>Fecha baja:</label></td>
					<td>
						<input id="<portlet:namespace />bajaFechaVista"
							   name="<portlet:namespace />bajaFechaVista"
							   type="text"
							   readonly="readonly"
							   style="<%= afiliadoBajaFechaStyle %>"
							   value="<%= HtmlUtil.escape(afiliadoBajaFecha) %>" />
					</td>
				</tr>

				<tr>
					<td><label>Número afiliado:</label></td>
					<td>
						<input id="<portlet:namespace />numeroAfiliadoVista"
							   name="<portlet:namespace />numeroAfiliadoVista"
							   size="10"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoNumeroAfiliado) %>" />
					</td>

					<td><label>OSPIM:</label></td>
					<td>
						<input id="<portlet:namespace />ospimVista"
							   name="<portlet:namespace />ospimVista"
							   size="10"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoNumeroOspim) %>" />
					</td>

					<td><label>UOMA:</label></td>
					<td>
						<input id="<portlet:namespace />uomaVista"
							   name="<portlet:namespace />uomaVista"
							   size="10"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoNumeroUoma) %>" />
					</td>

					<td><label>AMTIMA:</label></td>
					<td>
						<input id="<portlet:namespace />amtimaVista"
							   name="<portlet:namespace />amtimaVista"
							   size="10"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoNumeroAmtima) %>" />
					</td>
				</tr>

				<tr>
					<td><label>ID plan:</label></td>
					<td>
						<input id="<portlet:namespace />idPlanVista"
							   name="<portlet:namespace />idPlanVista"
							   size="6"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoIdPlan) %>" />
					</td>

					<td><label>ID tercerizadora:</label></td>
					<td>
						<input id="<portlet:namespace />idTercerizadoraVista"
							   name="<portlet:namespace />idTercerizadoraVista"
							   size="10"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoIdTercerizadora) %>" />
					</td>

					<td><label>Tercerizadora:</label></td>
					<td colspan="3">
						<input id="<portlet:namespace />afiTercerizadoraVista"
							   name="<portlet:namespace />afiTercerizadoraVista"
							   size="35"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoAfiTercerizadora) %>" />
					</td>
				</tr>

				<tr>
					<td><label>Fecha alta:</label></td>
					<td>
						<input id="<portlet:namespace />fechaAltaAfiliadoVista"
							   name="<portlet:namespace />fechaAltaAfiliadoVista"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoFechaAlta) %>" />
					</td>

					<td><label>Incapacidad:</label></td>
					<td>
						<input id="<portlet:namespace />incapacidadVista"
							   name="<portlet:namespace />incapacidadVista"
							   size="10"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoIncapacidad) %>" />
					</td>

					<td><label>Antecedentes:</label></td>
					<td colspan="3">
						<input id="<portlet:namespace />antecedentesVista"
							   name="<portlet:namespace />antecedentesVista"
							   size="35"
							   type="text"
							   readonly="readonly"
							   value="<%= HtmlUtil.escape(afiliadoAntecedentes) %>" />
					</td>
				</tr>

			</table>
		</div>
	</fieldset>
</c:if>