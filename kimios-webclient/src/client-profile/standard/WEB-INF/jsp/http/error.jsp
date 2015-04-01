<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Kimios Error</title>
</head>
<body>

<div style="text-align: center; margin-top: 64px;">
    <img src="images/logo.png" style="border: 0; width: 128px;"/>
</div>

<p style="color: #333333; font-family: arial; font-size: 12px; text-align: center;">encountered an error.<br/>Please
    contact
    your administrator.
    <br />
    <br />
    <pre>

    <%
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        if(throwable != null){
           for(StackTraceElement el: throwable.getStackTrace()){
               response.getWriter().println(el.toString());
           }
        } else {
            response.getWriter().println("No exception data");
        }


    %>

</pre>

</p>




</body>
</html>