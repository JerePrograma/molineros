<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA);

				
				String cuil_titular = request.getParameter("cuil");
				String inte = request.getParameter("inte");
				String tipo_reintegro = request.getParameter("tipo_reintegro");
								
				List<Catastro> catastroList = (List<Catastro>)request.getAttribute(WebKeysLiquidaciones.BUSQUEDA_CATASTRAL);
				if (catastroList == null) {
					catastroList = CatastroServiceUtil.buscaCatastro(cuil_titular, Integer.valueOf(inte));
				}
 			%>
 			<table align="center">
 			<tr>
	 			<td colspan="1">
	 				<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 18)%>" />	 				
	 			</td>
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 17)%>" />
	 			</td>
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 16)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 15)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 14)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 13)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 12)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 11)%>" />
	 			</td> 			
				
				<td colspan="1">
					&nbsp;&nbsp;&nbsp;&nbsp;
	 			</td> 			

				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 21)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 22)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 23)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 24)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 25)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 26)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 27)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 28)%>" />
	 			</td>
	 			 			
			</tr>
			<tr>
	 			<td colspan="1" align="center">
	 			18
	 			</td>
				<td colspan="1" align="center">
				17
	 			</td>
				<td colspan="1" align="center">
				16
	 			</td> 			
				<td colspan="1" align="center">
				15
	 			</td> 			
				<td colspan="1" align="center">
				14
	 			</td> 			
				<td colspan="1" align="center">
				13
	 			</td>	 			 		
				<td colspan="1" align="center">
				12
	 			</td> 			
				<td colspan="1" align="center">
				11
	 			</td> 			

				<td colspan="1" align="center">
				&nbsp;&nbsp;&nbsp;&nbsp;
	 			</td>
				<td colspan="1" align="center">
				21
	 			</td> 			
				<td colspan="1" align="center">
				22
	 			</td> 			
				<td colspan="1" align="center">
				23
	 			</td> 			
				<td colspan="1" align="center">
				24
	 			</td> 			
				<td colspan="1" align="center">
				25
	 			</td> 			
				<td colspan="1" align="center">
				26
	 			</td> 			
				<td colspan="1" align="center">
				27
	 			</td> 			
				<td colspan="1" align="center">
				28
	 			</td>	 			 			
			</tr>
			<tr>
				<td colspan="17">&nbsp;</td>
			</tr>
			<tr>
	 			<td colspan="1">
	 				<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 48)%>" />	 				
	 			</td>
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 47)%>" />
	 			</td>
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 46)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 45)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 44)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 43)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 42)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 41)%>" />
	 			</td> 			
				
				<td colspan="1">
					&nbsp;&nbsp;&nbsp;&nbsp;
	 			</td> 			

				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 31)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 32)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 33)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 34)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 35)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 36)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 37)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 38)%>" />
	 			</td>
	 			 			
			</tr>
			<tr>
			
	 			<td colspan="1" align="center">
	 			48
	 			</td>
				<td colspan="1" align="center">
				47
	 			</td>
				<td colspan="1" align="center">
				46
	 			</td> 			
				<td colspan="1" align="center">
				45
	 			</td> 			
				<td colspan="1" align="center">
				44
	 			</td> 			
				<td colspan="1" align="center">
				43
	 			</td>	 			 		
				<td colspan="1" align="center">
				42
	 			</td> 			
				<td colspan="1" align="center">
				41
	 			</td> 			

				<td colspan="1" align="center">
				&nbsp;&nbsp;&nbsp;&nbsp;
	 			</td>
				<td colspan="1" align="center">
				31
	 			</td> 			
				<td colspan="1" align="center">
				32
	 			</td> 			
				<td colspan="1" align="center">
				33
	 			</td> 			
				<td colspan="1" align="center">
				34
	 			</td> 			
				<td colspan="1" align="center">
				35
	 			</td> 			
				<td colspan="1" align="center">
				36
	 			</td> 			
				<td colspan="1" align="center">
				37
	 			</td> 			
				<td colspan="1" align="center">
				38
	 			</td>
	 		</tr>
	 		<tr>
				<td colspan="17">&nbsp;</td>
			</tr>
			
		<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)%>">
			
			<tr>
				<td colspan="17">&nbsp;</td>
			</tr>			
			<tr>
			
				<td colspan="1" align="center">
	 			&nbsp;
	 			</td>
				<td colspan="1" align="center">
	 			&nbsp;
	 			</td>
				<td colspan="1" align="center">
	 			&nbsp;
	 			</td>
			
	 			<td colspan="1">
	 				<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 55)%>" />	 				
	 			</td>
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 54)%>" />
	 			</td>
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 53)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 52)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 51)%>" />
	 			</td> 			
				
				<td colspan="1">
					&nbsp;&nbsp;&nbsp;&nbsp;
	 			</td> 			

				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 61)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 62)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 63)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 64)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 65)%>" />
	 			</td> 			
	 			 			
			</tr>
			<tr>
			
				<td colspan="1" align="center">
	 			&nbsp;
	 			</td>
				<td colspan="1" align="center">
	 			&nbsp;
	 			</td>
				<td colspan="1" align="center">
	 			&nbsp;
	 			</td>
			
	 			<td colspan="1" align="center">
	 			55
	 			</td>
				<td colspan="1" align="center">
				54
	 			</td>
				<td colspan="1" align="center">
				53
	 			</td> 			
				<td colspan="1" align="center">
				52
	 			</td> 			
				<td colspan="1" align="center">
				51
	 			</td>		

				<td colspan="1" align="center">
				&nbsp;&nbsp;&nbsp;&nbsp;
	 			</td>
	 			
				<td colspan="1" align="center">
				61
	 			</td>	 			 		
				<td colspan="1" align="center">
				62
	 			</td> 			
				<td colspan="1" align="center">
				63
	 			</td> 	
				<td colspan="1" align="center">
				64
	 			</td> 			
				<td colspan="1" align="center">
				65
	 			</td> 			
	 		</tr>
			<tr>
				<td colspan="17">&nbsp;</td>
			</tr>
			<tr>
			
				<td colspan="1" align="center">
	 			&nbsp;
	 			</td>
				<td colspan="1" align="center">
	 			&nbsp;
	 			</td>
				<td colspan="1" align="center">
	 			&nbsp;
	 			</td>
			
	 			<td colspan="1">
	 				<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 85)%>" />	 				
	 			</td>
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 84)%>" />
	 			</td>
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 83)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 82)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 81)%>" />
	 			</td> 			
				
				<td colspan="1">
					&nbsp;&nbsp;&nbsp;&nbsp;
	 			</td> 			

				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 71)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 72)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 73)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 74)%>" />
	 			</td> 			
				<td colspan="1">
					<img border="0" src="/html/images/<%=CatastroServiceUtil.imagenDadaPieza(catastroList, 75)%>" />
	 			</td> 			
	 			 			
			</tr>
			<tr>
			
				<td colspan="1" align="center">
	 			&nbsp;
	 			</td>
				<td colspan="1" align="center">
	 			&nbsp;
	 			</td>
				<td colspan="1" align="center">
	 			&nbsp;
	 			</td>
			
				<td colspan="1" align="center">
				85
	 			</td> 			
				<td colspan="1" align="center">
				84
	 			</td> 			
				<td colspan="1" align="center">
				83
	 			</td>	 			 		
				<td colspan="1" align="center">
				82
	 			</td> 			
				<td colspan="1" align="center">
				81
	 			</td> 			

				<td colspan="1" align="center">
				&nbsp;&nbsp;&nbsp;&nbsp;
	 			</td>
				<td colspan="1" align="center">
				71
	 			</td> 			
				<td colspan="1" align="center">
				72
	 			</td> 			
				<td colspan="1" align="center">
				73
	 			</td> 			
				<td colspan="1" align="center">
				74
	 			</td> 			
				<td colspan="1" align="center">
				75
	 			</td> 			
	 		</tr>
	 		
	 	</c:if>
	 	
 	</table> 		
