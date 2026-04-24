<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%
	response.setHeader("Cache-Control","no-store"); //HTTP 1.1
	response.setHeader("Pragma","no-cache"); //HTTP 1.0
	response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
	String amarillo ="#F7F767";
	String rojo="#F72437";
	String naranja="#FAAC58";
	
	String msg=ParamUtil.getString(request, "aviso");
	
	String colorStr=ParamUtil.getString(request, "colorstr");
	String colorH1="";
	switch (colorStr) { 
      case "red":
    	  colorH1=rojo;
    	  break;
      case "yellow":
    	  colorH1=amarillo;
    	  break;	  
      case "orange":
    	  colorH1=naranja;
    	  break;	  
	}	  
    	  
	
%>	


<div id="div_titulo"  >
    <h1 style="text-align: center;">
     <br>
     <br>
     <label><%=msg%></label>
      <br>
      <br>
      <br>
    </h1>
</div>

<table>
<tr>
<td>
</td>
</tr>
<tr><td>&nbsp;</td></tr>
</table>
<script type="text/javascript">
jQuery("#div_titulo").css("background-color", "<%=colorH1%>");
</script>
