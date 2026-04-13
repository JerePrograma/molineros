<%@ include file="/html/portlet/utils/prestaciones/init.jsp" %>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="ar.com.ospim.afiliados.beans.Afiliado"%>
<%@page import="ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil"%>
<%@page import="ar.com.ospim.afiliados.beans.AfiPlan"%>
<%@page import="ar.com.ospim.afiliados.services.PlanServiceUtil"%>
<%@page import="ar.com.ospim.autorizaciones.services.NomencladorServiceUtil"%>
<%@page import="ar.com.ospim.autorizaciones.beans.NomencladorPlan"%>
<%@page import="java.math.BigDecimal"%>

<%
    PlanServiceUtil planService = new PlanServiceUtil();
    SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	PortletSession ps= renderRequest.getPortletSession();
	List<PlanPrestacion> prestaciones=null;	
	
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("codigo");
	headerNames.add("prestacion");
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-prestaciones-were-found"));
		String id_prestacion=(String)renderRequest.getParameter("id_prestacion");		
		String codigo=(String)renderRequest.getParameter("codigo");
		String prestacionString=(String)renderRequest.getParameter("prestacion");
		String protesisString = (String)renderRequest.getParameter("protesis") != null ? (String)renderRequest.getParameter("protesis") : "0";
		
		prestaciones=PlanPrestacionServiceUtil.traePlanPrestaciones(codigo != null ? codigo : "", prestacionString, PlanPrestacionServiceUtil.PLAN_ID_DUMMY, protesisString);
		
		//DS - Agregado para traer importe prestacion de acuerdo al plan del afiliado
		String cuil =(String)renderRequest.getParameter("cuil");
		String dia =(String)renderRequest.getParameter("dia");
		String mes =(String)renderRequest.getParameter("mes");
		String anio =(String)renderRequest.getParameter("anio");
		if(cuil!=null && !"".equalsIgnoreCase(cuil)){
		   Date fecha= null;
		   try {
			fecha = formatoDeFechas.parse(dia + "/"
					+ (Integer.parseInt(mes) + 1) + "/"
					+ anio);
		   } catch (Exception e) {
			fecha = null;
	 	   }
		 
		   Integer plan =-1;
		   try{
			   
			 String idPlan =(String)renderRequest.getParameter("id_plan_afi"); 
//		     AfiPlan afiPlan = planService.buscarUltimoPlanAportes(cuil);
//		     plan=afiPlan.getPlan().getId();
             plan =Integer.parseInt(idPlan);
		   } catch (Exception e) {
			   plan=-1;
		   }
		
		   if(fecha!=null){
			 for(PlanPrestacion p:prestaciones){
				if(p.getId_prestacion()>0 && plan !=null && fecha !=null){
				   NomencladorPlan np=NomencladorServiceUtil.buscarNomencladorPlanTopesReintegros(p.getId_prestacion(),plan,fecha);
				   if(np!=null && np.getPlan() != null){
				     if(/*p.getId_plan()==np.getPlan().getId() &&*/ p.getId_prestacion()==np.getId_prestacion()){
					   p.getNomenclador().setImporte(BigDecimal.valueOf(np.getTopeReintegro())); 
				     }
				   }
				}  
			 }
		   }	
		}
		//DS - Fin Agregado
		
	if(null!=prestaciones){
		//Seteo el total de la lista.
	 	int total = prestaciones.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if(total==1){
			Prestacion presUnica=(Prestacion) prestaciones.get(0).getNomenclador();
			PlanPrestacion planPrestacion = (PlanPrestacion) prestaciones.get(0);
			%>
				<script type="text/javascript">
					pasarParametrosAParentPresc("<%=presUnica.getId()%>","<%=presUnica.getCodigo()%>","<%=presUnica.getDescripcion()%>",
							"<%= planPrestacion.getTope_cantidad()%>","<%= planPrestacion.getTope_importe()%>",
							"<%= planPrestacion.getTope_individ_cantidad()%>","<%= planPrestacion.getTope_individ_importe()%>");
					<c:if test="<%= Integer.valueOf(protesisString) == 1 || Integer.valueOf(protesisString) == 2 || Integer.valueOf(protesisString) == 3%>">						  
						pasarParametroImporte("<%=presUnica.getImporte()%>");
					</c:if>
				</script>
			<%
		//More de una coincidencia	
		}else {
		 	searchContainer.setTotal(total);
		 	//prestaciones = ListUtil.subList(prestaciones, searchContainer.getStart(),searchContainer.getEnd());
			List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < prestaciones.size(); i++) {
		 		Prestacion prestacion = (Prestacion) prestaciones.get(i).getNomenclador();
		 		PlanPrestacion planPrestacion = (PlanPrestacion) prestaciones.get(i);
				ResultRow row = new ResultRow(prestacion.getCodigo(),prestacion.getDescripcion(), i);			
				// Name and short description
				StringBuilder sb = new StringBuilder();
				sb.append("<a href='javascript:pasarParametrosAParentPresc(\"");
				sb.append(prestacion.getId());
				sb.append("\",\"");
				sb.append(prestacion.getCodigo());
				sb.append("\",\"");
				sb.append(prestacion.getDescripcion());
				sb.append("\",\"");
				sb.append(planPrestacion.getTope_cantidad());
				sb.append("\",\"");
				sb.append(planPrestacion.getTope_importe());
				sb.append("\",\"");
				sb.append(planPrestacion.getTope_individ_cantidad());
				sb.append("\",\"");
				sb.append(planPrestacion.getTope_individ_importe());
				sb.append("\");");
				if (Integer.valueOf(protesisString) == 1 || Integer.valueOf(protesisString) == 2 || Integer.valueOf(protesisString) == 3) {
					sb.append("pasarParametroImporte(\"");
					sb.append(prestacion.getImporte());
					sb.append("\");");
				}
				sb.append("'>");
				sb.append(prestacion.getCodigo());
				sb.append("</a>");
				row.addText(sb.toString());
				StringBuilder sb2 = new StringBuilder();
				sb2.append("<a href='javascript:pasarParametrosAParentPresc(\"");
				sb2.append(prestacion.getId());
				sb2.append("\",\"");				
				sb2.append(prestacion.getCodigo());
				sb2.append("\",\"");
				sb2.append(prestacion.getDescripcion());
				sb2.append("\",\"");
				sb2.append(planPrestacion.getTope_cantidad());
				sb2.append("\",\"");
				sb2.append(planPrestacion.getTope_importe());
				sb2.append("\",\"");
				sb2.append(planPrestacion.getTope_individ_cantidad());
				sb2.append("\",\"");
				sb2.append(planPrestacion.getTope_individ_importe());
				sb2.append("\");");
				if (Integer.valueOf(protesisString) == 1 || Integer.valueOf(protesisString) == 2 || Integer.valueOf(protesisString) == 3) {
					sb2.append("pasarParametroImporte(\"");
					sb2.append(prestacion.getImporte());
					sb2.append("\");");
				}
				sb2.append("'>");
				sb2.append(prestacion.getDescripcion());
				sb2.append("</a>");
				row.addText(sb2.toString());
				resultRows.add(row);
		 	}
		%>
		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<%
		}
	}
%>