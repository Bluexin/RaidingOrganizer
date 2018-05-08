<#-- @ftlvariable name="error" type="io.ktor.http.HttpStatusCode" -->
<html>
<head>
    <#include "head.ftl">
    <link href="https://transloadit.edgly.net/releases/uppy/v0.24.2/dist/uppy.min.css" rel="stylesheet">
    <title>Error ${error.value} - Raiding Organizer</title>
</head>
<body>
<#include "header.ftl">
<section class="section">
    <h1 class="title container">An error has occurred: ${error}</h1>
</section>
<#include "footer.ftl">
</body>
</html>
