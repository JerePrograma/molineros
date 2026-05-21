<%@ include file="/html/portlet/compras/init.jsp" %>

<%
RequerimientoCompra req = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);
if (req == null) {
    req = new RequerimientoCompra();
}

boolean esNuevo = req.getIdRequerimientoCompra() == 0;
boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

List<ClaseBase> sectores = new ArrayList<ClaseBase>();
try {
    sectores = TraeListasServiceUtil.getSectoresLiquidaciones();
} catch (Exception e) {
    sectores = new ArrayList<ClaseBase>();
}

PortletURL volverURL = renderResponse.createRenderURL();
volverURL.setWindowState(WindowState.MAXIMIZED);
volverURL.setParameter("struts_action", "/compras/view");

PortletURL verURL = renderResponse.createRenderURL();
verURL.setWindowState(WindowState.MAXIMIZED);
verURL.setParameter("struts_action", "/compras/ver_requerimiento");
verURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

PortletURL actionURL = renderResponse.createActionURL();
actionURL.setWindowState(WindowState.MAXIMIZED);
actionURL.setParameter("struts_action", "/compras/editar_requerimiento");

String solicitanteDefault = req.getSolicitanteUsr() != null ? req.getSolicitanteUsr() : (user != null ? user.getScreenName() : "");
String entidadDefault = req.getEntidad() != null ? req.getEntidad() : "O.S.P.I.M.";
String reqSectorId = req.getSectorId() != null ? String.valueOf(req.getSectorId().intValue()) : "";
%>

<c:if test="<%= !puedeABM %>">
    <div class="portlet-msg-error">No posee permisos para editar requerimientos de compras.</div>
</c:if>

<c:if test="<%= puedeABM %>">
    <form action="<%= actionURL.toString() %>" method="post" name="<portlet:namespace />fm">
        <input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace /><%= Constants.CMD %>" value="<%= esNuevo ? Constants.ADD : Constants.UPDATE %>" />
        <input type="hidden" name="<portlet:namespace />id_requerimiento_compra" id="<portlet:namespace />id_requerimiento_compra" value="<%= req.getIdRequerimientoCompra() %>" />

        <fieldset class="block-labels">
            <legend><%= esNuevo ? "Nuevo requerimiento de compra" : "Editar requerimiento de compra" %></legend>

            <table class="lfr-table" width="100%">
                <tr>
                    <td><label>Numero:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />numero" id="<portlet:namespace />numero" value="<%= req.getNumeroString() %>" size="10" maxlength="10" />
                        <c:if test="<%= esNuevo %>">
                            <span class="portlet-msg-info">Si queda vacio, se genera automaticamente.</span>
                        </c:if>
                    </td>

                    <td><label>Estado:</label></td>
                    <td><strong><%= req.getEstadoDescripcion() %></strong></td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Solicitante:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />solicitante_usr" id="<portlet:namespace />solicitante_usr" value="<%= solicitanteDefault %>" size="30" maxlength="75" />
                    </td>

                    <td><label>Entidad:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />entidad" id="<portlet:namespace />entidad" value="<%= entidadDefault %>" size="30" maxlength="75" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Sector:</label></td>
                    <td>
                        <select name="<portlet:namespace />sector_id" id="<portlet:namespace />sector_id">
                            <option value="0">Seleccione</option>
                            <% for (ClaseBase sector : sectores) {
                                String sectorId = sector.getId() != null ? sector.getId().trim() : "";
                                String sectorDescripcion = sector.getDescripcion() != null ? sector.getDescripcion() : "";
                                String selected = reqSectorId.equals(sectorId) ? "selected=\"selected\"" : "";
                            %>
                                <option value="<%= sectorId %>" <%= selected %>><%= sectorDescripcion %></option>
                            <% } %>
                        </select>
                    </td>

                    <td><label>Sector texto:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />sector_descripcion" id="<portlet:namespace />sector_descripcion" value="<%= req.getSectorDescripcion() != null ? req.getSectorDescripcion() : "" %>" size="28" maxlength="120" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Prioridad:</label></td>
                    <td>
                        <select name="<portlet:namespace />prioridad" id="<portlet:namespace />prioridad">
                            <option value="<%= WebKeysCompras.PRIORIDAD_BAJA %>" <%= req.getPrioridad() == WebKeysCompras.PRIORIDAD_BAJA ? "selected=\"selected\"" : "" %>>Baja</option>
                            <option value="<%= WebKeysCompras.PRIORIDAD_MEDIA %>" <%= req.getPrioridad() == WebKeysCompras.PRIORIDAD_MEDIA ? "selected=\"selected\"" : "" %>>Media</option>
                            <option value="<%= WebKeysCompras.PRIORIDAD_ALTA %>" <%= req.getPrioridad() == WebKeysCompras.PRIORIDAD_ALTA ? "selected=\"selected\"" : "" %>>Alta</option>
                            <option value="<%= WebKeysCompras.PRIORIDAD_URGENTE %>" <%= req.getPrioridad() == WebKeysCompras.PRIORIDAD_URGENTE ? "selected=\"selected\"" : "" %>>Urgente</option>
                        </select>
                    </td>

                    <td><label>Importe estimado:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />importe_estimado_total" id="<portlet:namespace />importe_estimado_total" value="<%= req.getImporteEstimadoTotalString() %>" size="12" />
                    </td>
                </tr>
            </table>
        </fieldset>

        <fieldset class="block-labels">
            <legend>Afiliado</legend>

            <table class="lfr-table" width="100%">
                <tr>
                    <td><label>Afiliado:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />afiliado" id="<portlet:namespace />afiliado" value="<%= req.getAfiliado() != null ? req.getAfiliado() : "" %>" size="45" maxlength="255" />
                    </td>

                    <td><label>DNI:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />dni" id="<portlet:namespace />dni" value="<%= req.getDniString() %>" size="14" maxlength="20" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Localidad:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />localidad" id="<portlet:namespace />localidad" value="<%= req.getLocalidad() != null ? req.getLocalidad() : "" %>" size="30" maxlength="120" />
                    </td>

                    <td><label>Provincia:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />provincia" id="<portlet:namespace />provincia" value="<%= req.getProvincia() != null ? req.getProvincia() : "" %>" size="30" maxlength="120" />
                    </td>
                </tr>
            </table>
        </fieldset>

        <fieldset class="block-labels">
            <legend>Solicitud</legend>

            <table class="lfr-table" width="100%">
                <tr>
                    <td><label>Fecha solicitud:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />fecha_solicitud" id="<portlet:namespace />fecha_solicitud" value="<%= req.getFechaSolicitudAsString() %>" size="10" maxlength="10" /> dd/MM/yyyy
                    </td>

                    <td><label>Fecha necesidad:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />fecha_necesidad" id="<portlet:namespace />fecha_necesidad" value="<%= req.getFechaNecesidadAsString() %>" size="10" maxlength="10" /> dd/MM/yyyy
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Detalle:</label></td>
                    <td colspan="3">
                        <input type="text" name="<portlet:namespace />detalle_requerimiento" id="<portlet:namespace />detalle_requerimiento" value="<%= req.getDetalleRequerimiento() != null ? req.getDetalleRequerimiento() : "" %>" size="90" maxlength="255" />
                    </td>
                </tr>

                <tr>
                    <td><label>Motivo:</label></td>
                    <td colspan="3">
                        <input type="text" name="<portlet:namespace />motivo" id="<portlet:namespace />motivo" value="<%= req.getMotivo() != null ? req.getMotivo() : "" %>" size="90" maxlength="255" />
                    </td>
                </tr>

                <tr>
                    <td><label>Observaciones:</label></td>
                    <td colspan="3">
                        <textarea name="<portlet:namespace />observaciones" id="<portlet:namespace />observaciones" cols="100" rows="4"><%= req.getObservaciones() != null ? req.getObservaciones() : "" %></textarea>
                    </td>
                </tr>
            </table>
        </fieldset>

        <fieldset class="block-labels">
            <legend>Cotizacion y presupuestos</legend>

            <table class="lfr-table" width="100%">
                <tr>
                    <td><label>Pedidos presupuestos:</label></td>
                    <td colspan="3">
                        <input type="text" name="<portlet:namespace />pedidos_presupuestos" id="<portlet:namespace />pedidos_presupuestos" value="<%= req.getPedidosPresupuestos() != null ? req.getPedidosPresupuestos() : "" %>" size="90" maxlength="255" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Fecha pedido cotizacion:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />fecha_pedido_cotizacion" id="<portlet:namespace />fecha_pedido_cotizacion" value="<%= req.getFechaPedidoCotizacionAsString() %>" size="10" maxlength="10" /> dd/MM/yyyy
                    </td>

                    <td><label>Cotizado:</label></td>
                    <td>
                        <select name="<portlet:namespace />cotizado" id="<portlet:namespace />cotizado">
                            <option value="0" <%= req.getCotizado() == null ? "selected=\"selected\"" : "" %>>Sin informar</option>
                            <option value="1" <%= Boolean.TRUE.equals(req.getCotizado()) ? "selected=\"selected\"" : "" %>>SI</option>
                            <option value="2" <%= Boolean.FALSE.equals(req.getCotizado()) ? "selected=\"selected\"" : "" %>>NO</option>
                        </select>
                    </td>
                </tr>

                <tr>
                    <td><label>Comparativa:</label></td>
                    <td colspan="3">
                        <textarea name="<portlet:namespace />comparativa" id="<portlet:namespace />comparativa" cols="100" rows="3"><%= req.getComparativa() != null ? req.getComparativa() : "" %></textarea>
                    </td>
                </tr>
            </table>
        </fieldset>

        <fieldset class="block-labels">
            <legend>RP y Orden de Compra</legend>

            <table class="lfr-table" width="100%">
                <tr>
                    <td><label>RP:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />rp_numero" id="<portlet:namespace />rp_numero" value="<%= req.getRpNumeroString() %>" size="12" maxlength="10" />
                    </td>

                    <td><label>Orden compra:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />orden_compra_numero" id="<portlet:namespace />orden_compra_numero" value="<%= req.getOrdenCompraNumeroString() %>" size="12" maxlength="10" />
                        <input type="hidden" name="<portlet:namespace />id_orden_compra" id="<portlet:namespace />id_orden_compra" value="<%= req.getOrdenCompraNumeroString() %>" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Obs. RP:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />rp_observacion" id="<portlet:namespace />rp_observacion" value="<%= req.getRpObservacion() != null ? req.getRpObservacion() : "" %>" size="45" maxlength="255" />
                    </td>

                    <td><label>Obs. OC:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />orden_compra_observacion" id="<portlet:namespace />orden_compra_observacion" value="<%= req.getOrdenCompraObservacion() != null ? req.getOrdenCompraObservacion() : "" %>" size="45" maxlength="255" />
                    </td>
                </tr>
            </table>
        </fieldset>

        <fieldset class="block-labels">
            <legend>Cargos y recupero</legend>

            <table class="lfr-table" width="100%">
                <tr>
                    <td><label>Cargo OSPIM:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />cargo_ospim" id="<portlet:namespace />cargo_ospim" value="<%= req.getCargoOspim() != null ? req.getCargoOspim() : "" %>" size="40" maxlength="255" />
                    </td>

                    <td><label>Cargo Ensalud:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />cargo_ensalud" id="<portlet:namespace />cargo_ensalud" value="<%= req.getCargoEnsalud() != null ? req.getCargoEnsalud() : "" %>" size="40" maxlength="255" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Recupero:</label></td>
                    <td>
                        <select name="<portlet:namespace />recupero" id="<portlet:namespace />recupero">
                            <option value="0" <%= req.getRecupero() == null ? "selected=\"selected\"" : "" %>>Sin informar</option>
                            <option value="1" <%= Boolean.TRUE.equals(req.getRecupero()) ? "selected=\"selected\"" : "" %>>SI</option>
                            <option value="2" <%= Boolean.FALSE.equals(req.getRecupero()) ? "selected=\"selected\"" : "" %>>NO</option>
                        </select>
                    </td>

                    <td></td>
                    <td></td>
                </tr>
            </table>
        </fieldset>

        <table class="lfr-table">
            <tr>
                <td>
                    <input type="button" value="Guardar" onclick="<portlet:namespace />guardar();" />

                    <c:if test="<%= !esNuevo %>">
                        &nbsp;&nbsp;<input type="button" value="Ver" onclick="window.location.href='<%= verURL.toString() %>';" />
                    </c:if>

                    &nbsp;&nbsp;<input type="button" value="Volver" onclick="window.location.href='<%= volverURL.toString() %>';" />
                </td>
            </tr>
        </table>
    </form>

    <c:if test="<%= req.getIdRequerimientoCompra() > 0 %>">
        <liferay-util:include page="/html/portlet/compras/requerimiento_items.jsp" />
        <liferay-util:include page="/html/portlet/compras/requerimiento_adjuntos.jsp" />
        <liferay-util:include page="/html/portlet/compras/requerimiento_historial.jsp" />
    </c:if>
</c:if>

<script type="text/javascript">
    function <portlet:namespace />guardar() {
        if (jQuery("#<portlet:namespace />solicitante_usr").val() == "") {
            alert("Debe informar solicitante.");
            return;
        }

        if (jQuery("#<portlet:namespace />detalle_requerimiento").val() == "" && jQuery("#<portlet:namespace />motivo").val() == "") {
            alert("Debe informar detalle o motivo del requerimiento.");
            return;
        }

        submitForm(document.<portlet:namespace />fm);
    }
</script>
