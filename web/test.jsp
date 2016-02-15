<%--
  Created by IntelliJ IDEA.
  User: juliofdiaz
  Date: 2/14/16
  Time: 1:40 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>

<%@ page import="org.sqlite.SQLiteConfig" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="pe.jfdc.Series" %>
<%@ page import="pe.jfdc.UtilitiesDB" %>
<%@ page import="pe.jfdc.Season" %>
<%@ page import="pe.jfdc.Episode" %>

<html>
<head>
    <title>Title2</title>
</head>
<body>




<% String DB_NAME = "/Users/juliofdiaz/Development/TvdbApp/.tvshows.db"; %>

<% Connection conn; %>
<% try { %>

<% Class.forName("org.sqlite.JDBC"); %>
<% SQLiteConfig conf = new SQLiteConfig(); %>
<% conf.enforceForeignKeys(true); %>
<% conn = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME, conf.toProperties()); %>
<% conn.setAutoCommit(false); %>




<% ArrayList<Integer> shows = UtilitiesDB.getAllTvdbIdFromDb( conn ); %>
<%    for( Integer eachShow : shows ) { %>
<%       Series tempShow = UtilitiesDB.getSeriesFromDb( conn, eachShow ); %>
<b> <%= tempShow.getName()  %> </b>
<%       ArrayList<Season> tempSeason = tempShow.getSeasons(); %>
<%       for( Season eachSeason: tempSeason){ %>
<%=         eachSeason.getNumber() %>
<%          ArrayList<Episode> tempEpisode = eachSeason.getEpisodes(); %>
.<%=         tempEpisode.size() %>
<%          for( Episode eachEpisode: tempEpisode ){ %>
<%            eachEpisode.getName(); %>
<%          }%>
<%       } %>
<br/>
<%    }%>

<%    conn.close(); %>
<%    out.println("Records created successfully, na mean?"); %>

<%    } catch (Exception e) { %>
<%    System.err.println(e.getClass().getName() + ": " + e.getMessage()); %>
<%    e.printStackTrace(); %>
<%    }%>




</body>
</html>
