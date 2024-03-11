<jsp:useBean id="manageextractionProfil" scope="session" class="fr.paris.lutece.plugins.identityexport.web.ProfilJspBean" />
<% String strContent = manageextractionProfil.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
