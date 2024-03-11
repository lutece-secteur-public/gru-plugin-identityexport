<jsp:useBean id="manageextractionExtraction" scope="session" class="fr.paris.lutece.plugins.identityexport.web.ExtractionJspBean" />
<% String strContent = manageextractionExtraction.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
