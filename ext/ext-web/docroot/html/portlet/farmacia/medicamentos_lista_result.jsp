<%@page import="java.text.SimpleDateFormat"%>
<%@ include file="/html/portlet/farmacia/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
				
				List <ReintegroMedicamentoItem> medicamentos = (ArrayList<ReintegroMedicamentoItem>)request.getSession().getAttribute(WebKeysFarmacia.REINTEGRO_PRESTACIONES_EN_EDICION);
					ReintegroMedicamento reintegro = (ReintegroMedicamento) request
					.getAttribute(WebKeysLiquidaciones.REINTEGRO_EN_EDICION);

				String total_precio_pub = (String)request.getSession().getAttribute("total_precio_pub");
				String total_cobertura = (String)request.getSession().getAttribute("total_cobertura");
					
				PortletURL portletURL= renderResponse.createRenderURL();
		 		List<String> header= new ArrayList<String>();		 		
		 		//header.add("numero-receta");
		 		header.add("troquel");
				header.add("nombre");
				header.add("Presentación");
				header.add("%");
				header.add("Fecha Prestación");
				header.add("Cpte");
				header.add("Cuit");
				header.add("Cpte Total");
				header.add("cant");
				header.add("precio-publico");
				header.add("monto-ospim");
				header.add("monto-amtima");		
				header.add("monto-prestadora");	
				header.add("Monto IMESA");	
				header.add("Total Precio Pub.");
				header.add("Total Cobertura");
				header.add("Eliminar / Editar");

				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, header,
				LanguageUtil.get(pageContext, "no-medicamentos-were-found"));

				if(null!=medicamentos){
	 				//Seteo el total de la lista.
				 	int total = medicamentos.size();
				 	searchContainer.setTotal(total);
				 	String reclamodata="";
	 				List resultRows = searchContainer.getResultRows();	 				
				 	for (int i = 0; i < medicamentos.size(); i++) {
				 		ReintegroMedicamentoItem medicamento = (ReintegroMedicamentoItem) medicamentos.get(i);
				 		if (medicamento.isDelete()) {
				 			continue;
				 		}
				 		StringBuilder sb= new StringBuilder();
	 					ResultRow row = new ResultRow(medicamento,medicamento.getIdAsString(), i);	 					
	 				//	row.addText(medicamento.getNumeroReceta()!=0?String.valueOf(medicamento.getNumeroReceta()):"");
	 					row.addText(medicamento.getMedicamento().getTroquel()!=0?String.valueOf(medicamento.getMedicamento().getTroquel()):"");
	 					
	 					if (medicamento.getIdReclamoPrestacional()   >0){
	 						reclamodata="(Reclamo)";
	 					}else{
	 						reclamodata="";
	 					}
	 					
	 					
	 					
	 					
 						row.addText(null!=medicamento.getMedicamento().getNombre()?medicamento.getMedicamento().getNombre()+reclamodata : "");
	 					row.addText(null!=medicamento.getMedicamento().getPresentacion() ? medicamento.getMedicamento().getPresentacion() : "");
	 					row.addText(null!=medicamento.getTotalCobertura()? medicamento.getTotalCobertura().toString() : "");
	 					
	 					
	 					row.addText(medicamento.getFechaPrestacionTexto());

	 					
	 				
	 					String comprobante ;
	 					try {
		 					 String tipo =  medicamento.getComproaDebitarTipo() + " " ;
		 					 String letra = medicamento.getComproaDebitarLetra() != null ? medicamento.getComproaDebitarLetra() + " " : " ";
		 					 String sucursal =  medicamento.getComproaDebitarSucursal() != null ?   medicamento.getComproaDebitarSucursal() + "-" : " ";
		 					 String numero = 	medicamento.getComproaDebitarNumero() + " "  ;
		 					 String comproTexto = medicamento.getComprobanteTexto() ; 
		 					 
		 					 
		 					 comprobante = tipo + letra + sucursal + numero + comproTexto;
	 					}catch(Exception e){
	 						comprobante = null;
	 					}
	 								
	 							
	 					row.addText(comprobante!=null ?   comprobante   : "");
	 					
	 					
	 					row.addText(medicamento.getCuitEntidad()!=null ? String.valueOf(medicamento.getCuitEntidad()) : "");
	 					row.addText(medicamento.getImporteComprobante()!=null ? String.valueOf(medicamento.getImporteComprobante()) : "");

	 					
	 					row.addText(medicamento.getCantidad()!=0 ? String.valueOf(medicamento.getCantidad()) : "");
	 					row.addText(null!=medicamento.getPrecio_al_publico()?String.valueOf(medicamento.getPrecio_al_publico().doubleValue()):"");	 						 						 					
	 					row.addText(null!=medicamento.getImporteCoberturaOspim()?String.valueOf(medicamento.getImporteCoberturaOspim().doubleValue()):"");	 						
 						row.addText(null!=medicamento.getImporteCoberturaAmtima()?String.valueOf(medicamento.getImporteCoberturaAmtima().doubleValue()):""); 						
 						row.addText(null!=medicamento.getImporteCoberturaPrestadora()?String.valueOf(medicamento.getImporteCoberturaPrestadora().doubleValue()):"");
 						row.addText(null!=medicamento.getImporteCoberturaImesa()?String.valueOf(medicamento.getImporteCoberturaImesa().doubleValue()):"");
 						row.addText(null!=medicamento.getPrecio_al_publico() ?
								(medicamento.getPrecio_al_publico().multiply(new BigDecimal(medicamento.getCantidad()))).toString() :"");
 						/*
 						row.addText(
 								(((null!=medicamento.getImporteCoberturaOspim() ? medicamento.getImporteCoberturaOspim(): BigDecimal.ZERO).add
 								 (null!=medicamento.getImporteCoberturaAmtima() ? medicamento.getImporteCoberturaAmtima():BigDecimal.ZERO)).multiply(new BigDecimal(medicamento.getCantidad()))).toString() 
							 );
 						*/
 						
 						row.addText(
 								
 								(((null!=medicamento.getImporteCoberturaOspim() ? medicamento.getImporteCoberturaOspim(): BigDecimal.ZERO).add
 								  (null!=medicamento.getImporteCoberturaAmtima() ? medicamento.getImporteCoberturaAmtima():BigDecimal.ZERO)).add
 								  (null!=medicamento.getImporteCoberturaPrestadora() ? medicamento.getImporteCoberturaPrestadora():BigDecimal.ZERO).add
 								  (null!=medicamento.getImporteCoberturaImesa() ? medicamento.getImporteCoberturaImesa():BigDecimal.ZERO)	  
 								).toString()
 								     
						);
 						
 						
 						
	 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
	 					sb.append(themeDisplay.getPathThemeImages());
	 					sb.append("/common/delete.png\" onClick=\"javascript:borraMedicamento('");
	 					sb.append(medicamento.getIdAsString());	 						 					
	 					sb.append("','");
	 					sb.append(medicamento.getIdReclamoPrestacional() );
	 					sb.append("','");
	 					sb.append(medicamento.getIdPrestacionReclamo() );
	 					sb.append("');\" />");
	 					
	 					
	 					
	 					
	 					sb.append(" / <img alt=\"<liferay-ui:message key='action.EDIT'/>\" src=\"");
	 					sb.append(themeDisplay.getPathThemeImages());
	 					sb.append("/common/edit.png\" onClick=\"javascript:seleccionaMedicamentoPrestacion('");		 							 					
	 					sb.append(medicamento.getIdAsString());
	 					sb.append("','");
	 					sb.append(medicamento.getIdReclamoPrestacional() );
	 					sb.append("','");
	 					sb.append(medicamento.getIdPrestacionReclamo() );
	 					sb.append("');\" />");
	 					row.addText(sb.toString());	 					
			 			resultRows.add(row);
				 	}
				 	if (total_precio_pub != null && !total_precio_pub.equals("0")) {
					 	ResultRow row = new ResultRow(new ReintegroMedicamentoItem(), new String("500"), 500);	 					
 						row.addText("");
 						row.addText("");
						row.addText("");
 						row.addText("");
 						row.addText("");
 						row.addText("");
 						row.addText("");	 						 						 					
 						row.addText("");	
 						row.addText("");
 						row.addText("");
 						row.addText("");	
 						row.addText("");
 						row.addText("");
						row.addText("<b>Total</b>");
						row.addText(total_precio_pub != null ? total_precio_pub : "");
						row.addText(total_cobertura != null ? total_cobertura : ""); 					
						row.addText("");
						resultRows.add(row);
				 	}
	 			}
 		%>
	<input type="hidden" id="medicamentosSize" name="medicamentosSize" value="<%=medicamentos.size()%>"/>
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
	<script type="text/javascript">

//desde el listado de prestaciones
function seleccionaMedicamentoPrestacion(idd,idReclamo,idPrestacionReclamo) {
	jQuery('#<portlet:namespace />id_prestacion').val(idd);
	
	
	<%
	ReintegroMedicamentoItem medicamento = new ReintegroMedicamentoItem();
	int id = 0;
	for (int i = 0; i < medicamentos.size(); i++) {
		medicamento = medicamentos.get(i);
		id = medicamento.getId();
	%>				
		if (idd == <%=id%>) {
		var n_receta = '<%=medicamento.getNumeroReceta()!=0?String.valueOf(medicamento.getNumeroReceta()):""%>';
		var n_troquel = '<%=medicamento.getMedicamento().getTroquel()!=0?String.valueOf(medicamento.getMedicamento().getTroquel()):""%>';
		var n_registro = '<%=medicamento.getMedicamento().getRegistro()!=0?String.valueOf(medicamento.getMedicamento().getRegistro()):""%>';
		var n_codbarras = '<%=medicamento.getMedicamento().getCod_barra()!=null?String.valueOf(medicamento.getMedicamento().getCod_barra()):""%>';
		var n_droga = '<%=medicamento.getMedicamento().getDroga()!=null?String.valueOf(medicamento.getMedicamento().getDroga()):""%>';
		var n_nombre = '<%=null!=medicamento.getMedicamento().getNombre() ? medicamento.getMedicamento().getNombre() : ""%>';
		var n_accion = '<%=null!=medicamento.getMedicamento().getAccion() ? medicamento.getMedicamento().getAccion() : ""%>';
		var n_presentacion = '<%=null!=medicamento.getMedicamento().getPresentacion() ? medicamento.getMedicamento().getPresentacion() : ""%>';
		var n_cobertura = '<%=null!=medicamento.getTotalCobertura()? medicamento.getTotalCobertura().toString() : ""%>';
		var n_cantidad = '<%=medicamento.getCantidad()!=0 ? String.valueOf(medicamento.getCantidad()) : ""%>';
		var n_laboratorio = '<%=medicamento.getMedicamento().getLaboratorio()!=null ? String.valueOf(medicamento.getMedicamento().getLaboratorio()) : ""%>';
		var p_publico = '<%=null!=medicamento.getPrecio_al_publico()?String.valueOf(medicamento.getPrecio_al_publico().doubleValue()):""%>';
		var importe_cob_ospim = '<%=null!=medicamento.getImporteCoberturaOspim()?String.valueOf(medicamento.getImporteCoberturaOspim().doubleValue()):""%>';
		var importe_cob_amtima = '<%=null!=medicamento.getImporteCoberturaAmtima()?String.valueOf(medicamento.getImporteCoberturaAmtima().doubleValue()):""%>';	
		var importe_cob_prestadora = '<%=null!=medicamento.getImporteCoberturaPrestadora()?String.valueOf(medicamento.getImporteCoberturaPrestadora().doubleValue()):""%>';
		var importe_cob_imesa = '<%=null!=medicamento.getImporteCoberturaImesa()?String.valueOf(medicamento.getImporteCoberturaImesa().doubleValue()):""%>';
		
		var total_med = '<%= null!=medicamento.getPrecio_al_publico() ?
				(medicamento.getPrecio_al_publico().multiply(new BigDecimal(medicamento.getCantidad()))).toString() : "" %>';						
		var total_cob = '<%= (
				((null != medicamento.getImporteCoberturaOspim() ? medicamento.getImporteCoberturaOspim() : BigDecimal.ZERO).add			 
				(null!=medicamento.getImporteCoberturaAmtima() ? medicamento.getImporteCoberturaAmtima() : BigDecimal.ZERO).
				add(null!=medicamento.getImporteCoberturaPrestadora() ? medicamento.getImporteCoberturaPrestadora() : BigDecimal.ZERO).add(
					null!=medicamento.getImporteCoberturaImesa() ? medicamento.getImporteCoberturaImesa() : BigDecimal.ZERO)
				)
				).toString() %>';
		var id_med = '<%=medicamento.getMedicamento().getId_medicamento()!=0?String.valueOf(medicamento.getMedicamento().getId_medicamento()):""%>';
		var cob_sssalud = '<%=medicamento.getMedicamento().getCober_sssalud()!=null? medicamento.getMedicamento().getCober_sssalud().toString() : "0" %>';
		var cob_ospim = '<%=medicamento.getMedicamento().getCober_ospim()!=null? medicamento.getMedicamento().getCober_ospim().toString() : "0" %>';
		var cob_amtima = '<%=medicamento.getMedicamento().getCober_amtima()!=null? medicamento.getMedicamento().getCober_amtima().toString() : "0" %>';
		var id_prestacion = '<%=medicamento.getIdAsString()!=null?medicamento.getIdAsString():""%>'
		var fecha_receta = ''; 
		//fecha_receta esta como yyyy-MM-dd
		<%	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			java.util.Date fecRec = medicamento.getFechaReceta();
			String diaRece="", anioRece = "";
			int mesRece = 0;
			if(medicamento != null &&  medicamento.getFechaReceta() != null){
				String fecha_rece_formatteada = sdf.format(medicamento.getFechaReceta());
				String[] fecha_rece_spliteada = fecha_rece_formatteada.split("-");
				diaRece = fecha_rece_spliteada[0];
				mesRece = Integer.parseInt(fecha_rece_spliteada[1])-1;
				anioRece = fecha_rece_spliteada[2];
			}
		%>
		
		
		
		var fechaComprobante = ''; 
		//fecha_receta esta como yyyy-MM-dd
		<%	sdf = new SimpleDateFormat("dd-MM-yyyy");
			java.util.Date fecCompro = medicamento.getFechaComprobante();
			String diaCompro="", anioCompro = "";
			int mesCompro = 0;
			if(medicamento != null &&  medicamento.getFechaComprobante() != null){
				String fecha_compro_formatteada = sdf.format(medicamento.getFechaComprobante());
				String[] fecha_compro_spliteada = fecha_compro_formatteada.split("-");
				diaCompro = fecha_compro_spliteada[0];
				mesCompro = Integer.parseInt(fecha_compro_spliteada[1])-1;
				anioCompro = fecha_compro_spliteada[2];
			}
		%>
		
		
		var fechaPrestacion = ''; 
		//fecha_receta esta como yyyy-MM-dd
		<%	sdf = new SimpleDateFormat("dd-MM-yyyy");
			java.util.Date fecPresta = medicamento.getFechaPrestacion();
			String diaPresta="", anioPresta = "";
			int mesPresta = 0;
			if(medicamento != null &&  medicamento.getFechaPrestacion() != null){
				String fecha_presta_formatteada = sdf.format(medicamento.getFechaPrestacion());
				String[] fecha_presta_spliteada = fecha_presta_formatteada.split("-");
				diaPresta = fecha_presta_spliteada[0];
				mesPresta = Integer.parseInt(fecha_presta_spliteada[1])-1;
				anioPresta = fecha_presta_spliteada[2];
			}
		%>
		
		
		if(idReclamo>0 && idPrestacionReclamo>0){
			try {
				jQuery('#<portlet:namespace />id_reclamo_prestacional').val(idReclamo);
				jQuery('#<portlet:namespace />id_prestacion_reclamo_prestacional').val(idPrestacionReclamo);
			}
				catch (err) 
				{	 
				}
		}
		var comproaDebitarTipo = '<%=medicamento.getComproaDebitarTipo()!=null ? medicamento.getComproaDebitarTipo() : ""%>';
		var comprobanteSuc = '<%=medicamento.getComproaDebitarSucursal()!=null ? medicamento.getComproaDebitarSucursal() : ""%>';
		var comprobanteLetra = '<%=medicamento.getComproaDebitarLetra()!=null ? medicamento.getComproaDebitarLetra() : ""%>';
		var comprobanteNro = '<%=medicamento.getComproaDebitarNumero()!=null ? medicamento.getComproaDebitarNumero() : ""%>';
		var importeCompro = '<%=medicamento.getImporteComprobante()!=null ? medicamento.getImporteComprobante() : ""%>';
		var importeCompro = '<%=medicamento.getImporteComprobante()!=null ? medicamento.getImporteComprobante() : ""%>';
		var cuitEntidad = '<%=medicamento.getCuitEntidad()!=null ? medicamento.getCuitEntidad() : ""%>';
		var sucursalEntidad = '<%=medicamento.getSucursalEntidad()!=null ? medicamento.getSucursalEntidad() : ""%>';


		 
		
			
		jQuery('#<portlet:namespace />fechaRecetaDia').val(<%=diaRece%>);	 
		jQuery('#<portlet:namespace />fechaRecetaMes').val(<%=mesRece%>);
		jQuery('#<portlet:namespace />fechaRecetaAnio').val(<%=anioRece%>);
		
		jQuery('#<portlet:namespace />id_medicamento').val(id_med);
		jQuery('#<portlet:namespace />id_prestacion').val(id_prestacion);
		
		jQuery('#<portlet:namespace />troquel').val(n_troquel);
		jQuery('#<portlet:namespace />registro').val(n_registro);
		jQuery('#<portlet:namespace />nombre_med').val(n_nombre);
		jQuery('#<portlet:namespace />droga').val(n_droga);
		jQuery('#<portlet:namespace />presentacion').val(n_presentacion);
		jQuery('#<portlet:namespace />laboratorio').val(n_laboratorio);
		jQuery('#<portlet:namespace />precio').val(p_publico);
		jQuery('#<portlet:namespace />porcentaje').val(n_cobertura);
		jQuery('#<portlet:namespace />precio_ospim').val(importe_cob_ospim);
		jQuery('#<portlet:namespace />monto_cober_ospim').val(importe_cob_ospim);
		jQuery('#<portlet:namespace />monto_cober_amtima').val(importe_cob_amtima);
		
		jQuery('#<portlet:namespace />monto_cober_prestadora').val(importe_cob_prestadora);
		jQuery('#<portlet:namespace />monto_cober_imesa').val(importe_cob_imesa);
		//jQuery('#<portlet:namespace />monto_cober_ospim').val(importe_cob_ospim);  linea repetida PC
		//jQuery('#<portlet:namespace />monto_cober_amtima').val(importe_cob_amtima); linea repetida PC		
		//jQuery('#<portlet:namespace />pmo').val(pmo);
		jQuery('#<portlet:namespace />codBarras').val(n_codbarras);		
		jQuery('#<portlet:namespace />total_med').val(total_med);
		
		
		jQuery('#<portlet:namespace />total_cob').val(total_cob);
		jQuery('#<portlet:namespace />cantidad').val(n_cantidad);
		jQuery('#<portlet:namespace />porc_sss').val(cob_sssalud);
		jQuery('#<portlet:namespace />porc_ospim').val(cob_ospim);
		jQuery('#<portlet:namespace />porc_amtima').val(cob_amtima);
		jQuery('#<portlet:namespace />accion_t').val(n_accion);
		
		
		jQuery('#<portlet:namespace />comprobante_tipo').val(comproaDebitarTipo);
		jQuery('#<portlet:namespace />comprobante_suc').val(comprobanteSuc);
		jQuery('#<portlet:namespace />comprobante_letra').val(comprobanteLetra);
		jQuery('#<portlet:namespace />comprobante_nro').val(comprobanteNro);
		jQuery('#<portlet:namespace />importeCompro').val(importeCompro);	
		jQuery('#<portlet:namespace />cuit_entidad').val(cuitEntidad);
		jQuery('#<portlet:namespace />sucursal_entidad').val(sucursalEntidad);

		jQuery('#<portlet:namespace />comproFechaDia').val(<%=diaCompro%>);
		jQuery('#<portlet:namespace />comproFechaMes').val(<%=mesCompro%>);
		jQuery('#<portlet:namespace />comproFechaAnio').val(<%=anioCompro%>);
		
		jQuery('#<portlet:namespace />fechaPrestacionDia').val(<%=diaPresta%>);
		jQuery('#<portlet:namespace />fechaPrestacionMes').val(<%=mesPresta%>);
		jQuery('#<portlet:namespace />fechaPrestacionAnio').val(<%=anioPresta%>);

		//monto_cober_prestadora

		
		<portlet:namespace />buscarEntidad();
		
		
		if (idReclamo>0 && idPrestacionReclamo >0 ){
			<portlet:namespace />desactivaControlesPrestacionDesdeReclamo(true);
		}
	}
	<%	
	}
	%>
}

<portlet:namespace />actualizaNroReceta();

function <portlet:namespace />actualizaNroReceta() {
	<%
		medicamentos = (ArrayList<ReintegroMedicamentoItem>)request.getSession().getAttribute(WebKeysFarmacia.REINTEGRO_PRESTACIONES_EN_EDICION);
		int nro_receta = 0;
		if(null!=medicamentos && medicamentos.size() > 0){
			 if (medicamentos.get(0) != null) {
				nro_receta = medicamentos.get(0).getNumeroReceta();
	 	 	 }
		}
     	%>
		jQuery('#<portlet:namespace />receta').val(<%=nro_receta == 0 ? "" : String.valueOf(nro_receta) %>);
	    jQuery("#<portlet:namespace />receta").attr('readonly', <%=nro_receta == 0 ? false : true %>);
}


</script>