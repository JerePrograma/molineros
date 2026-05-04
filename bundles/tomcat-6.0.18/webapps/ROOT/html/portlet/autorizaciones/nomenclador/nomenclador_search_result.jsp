<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ page import="ar.com.ospim.autorizaciones.services.NomencladorServiceUtil" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>

<%

PortletSession ps= renderRequest.getPortletSession();
List<Nomenclador> archivos=null;

String descripcionNomenclador=renderRequest.getParameter("descripcionnomenclador");	
String codigoNomenclador=renderRequest.getParameter("codigonomenclador");
String tipoNomenclador=renderRequest.getParameter("tiponomenclador");
String muestrabaja=renderRequest.getParameter("muestrabaja");
String from=renderRequest.getParameter("from");
String marcaReinLiq=renderRequest.getParameter("marcareinliq");
String soloActivos=renderRequest.getParameter("soloActivos");
String esPrestMed=renderRequest.getParameter("esPrestMed");
%>

<%
boolean viewBaja="false".equalsIgnoreCase(muestrabaja)?false:true;
boolean soloPrestacionesActivas=false;

Integer tipoNomencladorId=0;
Integer marcaReinLiqInt=0;
Integer esPrestMedInt=0;
PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.POP_UP);
portletURL.setParameter(Constants.CMD,"PopUp");

if(tipoNomenclador!=null && !"".equalsIgnoreCase(tipoNomenclador)){
	tipoNomencladorId=Integer.parseInt(tipoNomenclador);
}

if(marcaReinLiq!=null && !"".equalsIgnoreCase(marcaReinLiq) && !"null".equalsIgnoreCase(marcaReinLiq)){
	marcaReinLiqInt=Integer.parseInt(marcaReinLiq);
}
if(soloActivos!=null && !"".equalsIgnoreCase(soloActivos) && soloActivos.equals("true") ){
	soloPrestacionesActivas=true;
}
if (esPrestMed==null) {
	esPrestMed="0";
}
if(esPrestMed!=null && !"".equalsIgnoreCase(esPrestMed) && !"null".equalsIgnoreCase(esPrestMed)){
	esPrestMedInt=Integer.parseInt(esPrestMed);
}

List<String> headerNames = new ArrayList<String>();
headerNames.add("Tipo");
headerNames.add("Código");
headerNames.add("Descripción");
headerNames.add("Especialidad");
headerNames.add("Recupera SUR");
headerNames.add("Fecha Baja");
if(from!=null && "preautorizaciones".equalsIgnoreCase(from)){
	if (marcaReinLiqInt>0){
		archivos = NomencladorServiceUtil.getListaNomencladorMarcaReinLiq(tipoNomencladorId,descripcionNomenclador,0,codigoNomenclador,false,"",marcaReinLiqInt);
	}else{
	    archivos = NomencladorServiceUtil.getListaNomencladorPreautorizaciones(tipoNomencladorId,descripcionNomenclador,0,codigoNomenclador,false,"");
	}
}else{

  if (marcaReinLiqInt>0){
	  /* 
	  El Nomenclador de ODONTOLOGIA viene con marca 3 y se envia con 
	  tipo nomenclador 1 
	  */
	  if ((marcaReinLiqInt == 3)) {
		  archivos = NomencladorServiceUtil.getListaNomencladorMarcaReinLiq(tipoNomencladorId,descripcionNomenclador,0,codigoNomenclador,false,"",marcaReinLiqInt);
	  } else {
		archivos = NomencladorServiceUtil.getListaNomencladorMarcaReinLiq(0,descripcionNomenclador,0,codigoNomenclador,false,"",6);		  
	  }
  }else{
	  if (esPrestMedInt == 1) {
		  archivos = NomencladorServiceUtil.getListaNomencladorPrestacionesMedicas(tipoNomencladorId,descripcionNomenclador,0,codigoNomenclador,false,"");
	  } else {
		  archivos = NomencladorServiceUtil.getListaNomenclador(tipoNomencladorId,descripcionNomenclador,0,codigoNomenclador,false,"");  
	  }
  }
}
//if(tipoNomenclador==null || "".equalsIgnoreCase(tipoNomenclador) || "0".equalsIgnoreCase(tipoNomenclador) ){
   if(from!=null && "preautorizaciones".equalsIgnoreCase(from)){
	   String strTipo = TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_TIPOS_NOMENCLADOR_VALIDOS");
	   
	   String strMarcaRein = TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_DISCAPACIDAD_MARCA_REINLIQ");
	   List<Nomenclador> archivosAux=new ArrayList<Nomenclador>();
	   for(Nomenclador n:archivos){
		   
		    String tipo = n.getId_tipo_nomenclador_string().trim();
	        int resultado = strTipo.indexOf(tipo);

            if((marcaReinLiqInt==0)){
            	if(resultado != -1) {
            	  if ( soloPrestacionesActivas) {	        		
     			   	if (n.getBaja_fecha()==null ){
     			    		archivosAux.add(n);	
     			   	 }
     	          }else{
     	        	archivosAux.add(n);	
     	          }
            	}
            }
            /* hay que arreglar esta mierda, el servicio debería recibir el parámetro de activas o de baja */
            if((marcaReinLiqInt!=0 && n.getMarcaReintegroLiquidacion()==Integer.parseInt(strMarcaRein)) || 
            		(marcaReinLiqInt!=0 &&	strMarcaRein.equalsIgnoreCase("6")) ){
            	if ( soloPrestacionesActivas) {	        		
			    	if (n.getBaja_fecha()==null ){
			    		archivosAux.add(n);	
			    	}
	        	}else{
	        		archivosAux.add(n);	
	        	}
            }
	        
	   }
	   archivos=archivosAux;
   }
//}

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "nomenclador-no-encontrado"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();	
	
	pageContext.setAttribute("total", total);	
	if(total==1){
		Nomenclador nom=(Nomenclador) archivos.get(0);
		if(nom.getBaja_fecha()==null || (nom.getBaja_fecha()!=null && viewBaja)){
		%>
			<script type="text/javascript">
				pasarParametrosAParentNm("<%=nom.getId_tipo_nomenclador_string() %>", "<%=nom.getCodigo().trim() %>", "<%=nom.getDescripcion().trim() %>", "", "", "<%=nom.getDescripcionTipoNomenclador().trim()%>");
			</script>
		<%
		}
	//More de una coincidencia	
	}else {
	  List resultRows = searchContainer.getResultRows();
	
	  for (int i = 0; i < archivos.size(); i++) {	    
		Nomenclador liq = (Nomenclador) archivos.get(i);
		
		if(liq.getBaja_fecha()==null || (liq.getBaja_fecha()!=null && viewBaja)){
		
	  	    ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		    PortletURL rowURL = renderResponse.createRenderURL();
		    rowURL.setWindowState(WindowState.MAXIMIZED);	
        
		    StringBuilder s = new StringBuilder();
		    s.append("<a href='javascript:pasarParametrosAParentNm(\"");
		    s.append(liq.getId_tipo_nomenclador_string());
		    s.append("\",\"");
		    s.append(liq.getCodigo().trim());
		    s.append("\",\"");
		    s.append(liq.getDescripcion().trim());
		    s.append("\")'>");		
		    s.append(liq.getDescripcionTipoNomenclador().trim());
		    s.append("</a>");
		    row.addText(s.toString());
		
		    StringBuilder s1 = new StringBuilder();
		    s1.append("<a href='javascript:pasarParametrosAParentNm(\"");
		    s1.append(liq.getId_tipo_nomenclador_string());
		    s1.append("\",\"");
		    s1.append(liq.getCodigo().trim());
		    s1.append("\",\"");
		    s1.append(liq.getDescripcion().trim());
		    s1.append("\")'>");		
		    s1.append(liq.getCodigo().trim());
		    s1.append("</a>");
		    row.addText(s1.toString());
		
		    StringBuilder s2 = new StringBuilder();
		    s2.append("<a href='javascript:pasarParametrosAParentNm(\"");
		    s2.append(liq.getId_tipo_nomenclador_string());
		    s2.append("\",\"");
		    s2.append(liq.getCodigo().trim());
		    s2.append("\",\"");
		    s2.append(liq.getDescripcion().trim());
		    s2.append("\")'>");		
		    s2.append(liq.getDescripcion().trim());
		    s2.append("</a>");
		    row.addText(s2.toString());
		
		    StringBuilder s3 = new StringBuilder();
		    s3.append("<a href='javascript:pasarParametrosAParentNm(\"");
		    s3.append(liq.getId_tipo_nomenclador_string());
		    s3.append("\",\"");
		    s3.append(liq.getCodigo().trim());
		    s3.append("\",\"");
		    s3.append(liq.getDescripcion().trim());
		    s3.append("\")'>");		
		    s3.append(liq.getEspecialidadDescripcion().trim());
		    s3.append("</a>");
		    row.addText(s3.toString());
		
		    StringBuilder s4 = new StringBuilder();
		    s4.append("<a href='javascript:pasarParametrosAParentNm(\"");
		    s4.append(liq.getId_tipo_nomenclador_string());
		    s4.append("\",\"");
		    s4.append(liq.getCodigo().trim());
		    s4.append("\",\"");
		    s4.append(liq.getDescripcion().trim());
		    s4.append("\")'>");		
		    s4.append(liq.getRecuperaSUR()?"Si":"No");
		    s4.append("</a>");
		    row.addText(s4.toString());
		
		    StringBuilder s5 = new StringBuilder();
		    s5.append("<a href='javascript:pasarParametrosAParentNm(\"");
		    s5.append(liq.getId_tipo_nomenclador_string());
		    s5.append("\",\"");
		    s5.append(liq.getCodigo().trim());
		    s5.append("\",\"");
		    s5.append(liq.getDescripcion().trim());
		    s5.append("\")'>");		
		    s5.append(liq.getBaja_fecha()!=null?liq.getBaja_Fecha_string():"");
		    s5.append("</a>");
		    row.addText(s5.toString());
		    if ( soloPrestacionesActivas) {
		    	if (liq.getBaja_fecha()==null ){
		    		resultRows.add(row);	
		    	}		    	
		    }else{
		    	resultRows.add(row);	
		    }
		    
		}
	  }
	}  
}
%>
	
 		
<script type="text/javascript">
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>