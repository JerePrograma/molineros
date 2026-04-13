<%@ include file="/html/portlet/afiliados/init.jsp" %>
<portlet:defineObjects/>
			<% 
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
				String view=ParamUtil.getString(request,"view");
				String cuil=request.getParameter("cuil_titular");
				int inte=0;
				if(null!=request.getParameter("inte")&&!request.getParameter("inte").trim().equals("")){
					inte=Integer.parseInt(request.getParameter("inte"));
				}
				List<AfiDocumentacion> documentacionList= (ArrayList<AfiDocumentacion>)renderRequest.getAttribute(WebKeysAfiliados.BUSQUEDA_DOCUMENTOS);
				if(null==documentacionList){					
					documentacionList=DocumentacionServiceUtil.buscaDocumentacion(cuil,inte);
					
				}
				PortletURL portletURLDocumentacion = renderResponse.createRenderURL();
		 		List<String> headerNamesDocumentacion = new ArrayList<String>();
		 		headerNamesDocumentacion.add("cuil-titular");
		 		headerNamesDocumentacion.add("inte");		 		
		 		headerNamesDocumentacion.add("documento");		 		
		 		headerNamesDocumentacion.add("ingre-fecha");
		 		headerNamesDocumentacion.add("vto-fecha");
		 		headerNamesDocumentacion.add("cod.CUD");
				if(showABMButtons && (null==view || !view.equals("true"))) { 
					headerNamesDocumentacion.add("editar-borrar");
				}				
				SearchContainer searchContainerDocumentacion= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLDocumentacion, headerNamesDocumentacion,
				LanguageUtil.get(pageContext, "no-docs-were-found"));
			
				if(null!=documentacionList){
					int total=documentacionList.size();	 				
	 				searchContainerDocumentacion.setTotal(total);
	 			 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
	 				List resultRowsDocumentacion = searchContainerDocumentacion.getResultRows();
	 				boolean certifDefun=false;
	 			 	for (int i = 0; i < documentacionList.size(); i++) {
	 			 		AfiDocumentacion doc = (AfiDocumentacion) documentacionList.get(i);	 			 		
	 			 		ResultRow rowDocumentacion=null;
	 			 		if(doc.getAfiliado().getInte()==inte){
	 						rowDocumentacion = new ResultRow(doc,doc.getDocumento().getId_documento(), i, true);
	 			 		}else{
	 			 			rowDocumentacion = new ResultRow(doc,doc.getDocumento().getId_documento(), i);
	 			 		}
	 					// Name and short description	 	
	 					rowDocumentacion.addText(doc.getAfiliado().getCuil_titular());
	 					rowDocumentacion.addText(String.valueOf(doc.getAfiliado().getInte()));
	 					rowDocumentacion.addText(doc.getDocumento().getDescripcion());	 					
	 					rowDocumentacion.addText(doc.getFecha_ingreAsString());
	 					rowDocumentacion.addText(doc.getFecha_bajaAsString());
	 					rowDocumentacion.addText(doc.getCodigoCUD()!=null?doc.getCodigoCUD():"");
	 					if(showABMButtons && (null==view || !view.equals("true"))) {
		 					StringBuilder sb= new StringBuilder();
		 					if(inte == doc.getAfiliado().getInte()){
			 					sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/portlet/edit_guest.png\" onClick=\"javascript:editaDocumentacion('");
			 					sb.append(doc.getDocumento().getId_documento());
			 					if(doc.getDocumento().getId_documento()==14){
			 						certifDefun=true;
			 					}
			 					sb.append("','");	 					
			 					sb.append(doc.getFecha_ingreAsString());	 					
			 					sb.append("','");
			 					sb.append(doc.getFecha_bajaAsString());
			 					sb.append("','");	 					
			 					sb.append(doc.getAfiliado().getId_motivo_baja());
			 					sb.append("','");	 					
			 					sb.append(doc.getAfiliado().getInte());
			 					sb.append("','");	 					
			 					sb.append(doc.getId());
			 					sb.append("','");	 					
			 					sb.append(doc.getCodigoCUD());
			 					sb.append("');\" />");
			 					sb.append(" / ");
			 					
			 					//Se comenta el eliminar hoy no anda del todo bien y no se casi nada
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraDocumentacion('");
			 					sb.append(doc.getDocumento().getId_documento());
			 					sb.append("','");	 					
			 					sb.append(doc.getFecha_ingreAsString());	 					
			 					sb.append("','");
			 					sb.append(doc.getFecha_bajaAsString());	 
			 					sb.append("','");
			 					sb.append(doc.getId());				 					
			 					sb.append("');\" />");		
								//sb.append("<img height='12'  width='14'  src='/html/themes/classic/images/common/close.png' />");
	 						}else{
	 							sb.append("<img height='12'  width='14'  src='/html/themes/classic/images/common/close.png' />");
	 							sb.append(" /  ");
	 							sb.append("<img height='12'  width='14'  src='/html/themes/classic/images/common/close.png' />");
	 						}
		 					rowDocumentacion.addText(sb.toString());
	 					}
	 					resultRowsDocumentacion.add(rowDocumentacion);
	 			 	}
	 			 	if(certifDefun){
	 			 		%>
		 					<script type="text/javascript">		 						
		 						jQuery('#subsidio_fallecimiento').show();
		 					</script>
		 				<%
		 			}else{
		 				%>
		 					<script type="text/javascript">		 						
		 						jQuery('#subsidio_fallecimiento').hide();
		 					</script>
		 				<%
		 			}
	 			}
				
 		%>	
	<liferay-ui:success key="request_processed" message="grabar-exitoso" />
	<liferay-ui:success key="documentacionAfiOk"  message="<%=(String)request.getAttribute(\"msgDocumentacionAfiOk\")  %>"  />			
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator searchContainer="<%=searchContainerDocumentacion%>" />
   <script type="text/javascript">	
   	
 			  <% String parentesco =(String)request.getAttribute("idParentescoDoc");%>
 			  <% String documentancion =(String)request.getAttribute("discapacitadoDoc");%>
			 
 			  <% String isCredencial =(String)request.getAttribute("isCredencial");%>
 			  <% String cuil_aux =(String)request.getAttribute("cuil_titular_aux");%>
 			  <% String inte_aux =(String)request.getAttribute("inte_aux");%>
 			  
 			  <%if (parentesco !=  null){%>
 				 jQuery('#<portlet:namespace />parentesco').val("<%=parentesco%>");
 			  <%}%>
 			 <%if (documentancion !=  null){%>
 			     jQuery('#<portlet:namespace />discapacitado').val("<%=documentancion%>");
 			  <%}%>
 			
 			 <%if (isCredencial !=  null){%>
			 	    if (validarCredencialExentoCoPago()){
						window.location.href ='/pdfservlet/?accion=credencialExentoCoPago&cuil=<%=cuil_aux%>+&inte=+<%=inte_aux%>';
			 	    }
			 <%}%>
			
			 
			 


		
			 

			 function validarCredencialExentoCoPago() {
			     var params="";
			     var respuesta=true;
			     var rta=false;

			     params += "&cuil_titular="+<%=cuil_aux%>+"&inte="+<%=inte_aux%>;
			     
			     var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validarCredencialExentoCoPago';
			 	   url = url + params;
			 	   jQuery.ajax({   
			 		   url: url,
			 		   async: false,
			 		   success: function(data) {
			 				var obj = jQuery.parseJSON(data);
			 				var resp = obj.existe;
			 				rta=(resp  === 'true');
			 	   		}
			 	   }); 
			 	   
			 	   if(rta){
			 		 	respuesta=confirm ('Desea Imprimir la credencial? ' ); 
			 	   }
			 	   
				   return  respuesta;    
			 	 
			 }
 			  
   	
	</script> 	
