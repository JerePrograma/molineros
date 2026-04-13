<%
/**
 */
%>

<%@ include file="/html/portlet/afiliados/init.jsp" %>

<liferay-ui:tabs names="error" backURL="javascript: history.go(-1);" />

<liferay-ui:error exception="<%= ImposibleBorrarEmpresaException.class %>" message="imposible-borrar-empresa-asociada" />
<liferay-ui:error exception="<%= NoSuchAfiliadoEntryException.class %>" message="the-afiliado-could-not-be-found" />
<liferay-ui:error exception="<%= DuplicateAfiliadoIdException.class %>" message="the-afiliado-key-already-exists" />
<liferay-ui:error exception="<%= AfliadoYaTieneConyugeException.class %>" message="the-afiliado-ya-tiene-conyuge" />
<liferay-ui:error exception="<%= SystemException.class %>" message="sistema-no-disponible" />
<liferay-ui:error exception="<%= PrincipalException.class %>" message="you-do-not-have-the-required-permissions" />
<liferay-ui:error exception="<%= HijoNoPuedeSerCasadoException.class %>" message="the-hijo-no-puede-ser-casado" />
<liferay-ui:error exception="<%= ConyugeNoPuedeSerSolteroException.class %>" message="the-conyuge-no-puede-ser-soltero" />
<liferay-ui:error exception="<%= IntegranteGrupoNoBorrableException.class %>" message="the-integrante-no-puede-ser-borrado" />
<liferay-ui:error exception="<%= TitularNoPuedeSerSolteroException.class %>" message="the-titular-no-puede-ser-soltero" />