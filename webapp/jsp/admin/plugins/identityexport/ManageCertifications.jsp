<jsp:useBean id="manageextractionCertification" scope="session" class="fr.paris.lutece.plugins.identityexport.web.CertificationJspBean" />
<% String strContent = manageextractionCertification.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
