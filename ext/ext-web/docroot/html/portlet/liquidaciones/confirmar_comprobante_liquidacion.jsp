<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page import="ar.com.ospim.global.services.ComprobanteServiceUtil" %>
<%@ page import="ar.com.ospim.global.beans.Empresa" %>
<%

String portlet_name = "liquidaciones";

if(renderResponse.getNamespace().equals("_COR_1_")){
	portlet_name = "correspondencia";
} else{
	portlet_name = "liquidaciones";
}

  String cuit = ParamUtil.getString(request,"cuit");
  String nombre = ParamUtil.getString(request,"nombre");
  String tipo= ParamUtil.getString(request,"comprobante_tipo");
  String sucu= ParamUtil.getString(request,"comprobante_sucu");
  String nro= ParamUtil.getString(request,"comprobante_nro");
  String letra= ParamUtil.getString(request,"comprobante_letra");
  String importe=ParamUtil.getString(request,"importe");
  String id_prestador = ParamUtil.getString(request,"id_prestador");
  
  Comprobante comp = new Comprobante();
  Empresa e = new Empresa();
  e.setCuit(cuit);
  comp.setAcreedorEmpresa(e);
  comp.setCuit(cuit);
  comp.setTipoComprobante(tipo);
  comp.setNroComprobante(nro);
  List<Comprobante> l = ComprobanteServiceUtil.getComprobantesGlobales(comp, WebKeysGlobal.OSPIM,0);
  
  
%>
<style>

input[type=button].verde {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
	color: black;
	padding: 15px 32px;
	background: -webkit-gradient(
		linear, left top, left bottom,
		from(#AAF2D0),
		to(#53896F));
	background: linear-gradient(
		top,
		#AAF2D0 0%,
		#53896F);

	border: 2px solid #000000;
	box-shadow:
		0px 1px 3px rgba(000,000,000,0.5),
		inset 0px 0px 1px rgba(255,255,255,0.7);
	text-shadow:
		0px -1px 0px rgba(000,000,000,0.4),
		0px 1px 0px rgba(255,255,255,0.3);
}


input[type=button].rojo {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
	color: black;
	padding: 15px 32px;
	background: -webkit-gradient(
		linear, left top, left bottom,
		from(#FCBEC5),
		to(#FC5062));
	background: linear-gradient(
		top,
		#FCBEC5 0%,
		#FC5062);

	border: 2px solid #000000;
	box-shadow:
		0px 1px 3px rgba(000,000,000,0.5),
		inset 0px 0px 1px rgba(255,255,255,0.7);
	text-shadow:
		0px -1px 0px rgba(000,000,000,0.4),
		0px 1px 0px rgba(255,255,255,0.3);
}

</style>

<!--  <form action="" method="post" name="<portlet:namespace />fmPop"> -->
	
		<table  style="font-family: "Lucida Sans Unicode", "Lucida Grande", Sans-Serif;
                  font-size: 12px;    margin: 45px;     width: 530px; text-align: left;
                  border-collapse: collapse; "  >
          <tr>     
          <th colspan="2"
              style=" font-size: 16px;     font-weight: normal;     padding: 8px;     background: #b9c9fe;
              border-top: 4px solid #aabcfe;    border-bottom: 1px solid #fff; color: #039;">
              Prestador: <%=id_prestador +" - "+nombre%>
           </th>
          </tr>
          
          <tr>     
          <th colspan="2"
              style=" font-size: 16px;     font-weight: normal;     padding: 8px;     background: #D7DFFB;
              border-top: 4px solid #aabcfe;    border-bottom: 1px solid #fff; color: #039;">
              Cuit: <%=cuit %>
           </th>
          </tr>
          
          <tr> 
          <th colspan="2"
              style=" font-size: 16px;     font-weight: normal;     padding: 8px;     background: #E0E7FC;
              border-top: 4px solid #aabcfe;    border-bottom: 1px solid #fff; color: #039;">
            Comprobante:  <%=tipo+" "+letra+" "+ sucu +"-"+nro +" -------------> $" + importe%>
          </th>
          </tr>	
          <tr><td>&nbsp;</td></tr>
          
          <tr>
             <td colspan="2">
                <%if(l!=null && !l.isEmpty()){%>
                  <table style="background-color: rgba(150, 212, 212, 0.4); font-size: 14px;width:100%">
                   <th>
                     Comprobantes encontrados con la misma numeración
                   </th>
                  <%for(Comprobante c:l){%>
                    <tr>
                      <td>
                       <%=c.getTipoComprobante() + " " + c.getLetraComprobante() + " " +c.getPtoVenta() +
                          " "+ c.getNroComprobante() + " $" + c.getImporteComprobante().toString()%>
                      </td>
                    </tr> 
                  <%}%>
                  </table>
                <%}%>
             </td>
          </tr>
          
          <tr><td>&nbsp;</td></tr>
          <tr>
				<td>
				   <input type="button" value="Aceptar" class="verde"
				      onClick="<portlet:namespace />comprobanteConfirmado();return false;" />
				</td>
				
				<td align="right">
				   <input type="button" value="Cancelar" class="rojo"
			          onClick="<portlet:namespace />comprobanteCancelado();return false;" />
				</td>
		     </tr>
          		
		</table>

<script>

</script>