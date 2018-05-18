<#-- @ftlvariable name="error" type="io.ktor.http.HttpStatusCode" -->
<html>
<head>
    <#include "include/head.ftl">
    <link href="https://transloadit.edgly.net/releases/uppy/v0.24.2/dist/uppy.min.css" rel="stylesheet">
    <title>Error ${error.value} - Raiding Organizer</title>
</head>
<body>
<#include "include/header.ftl">
<section class="section">
    <h1 class="title container">An error has occurred: ${error}</h1>
</section>
<#include "include/footer.ftl">
</body>
</html>
