<#-- @ftlvariable name="instances" type="java.util.List<be.bluexin.raidingorganizer.database.DbInstance>" -->
<#-- @ftlvariable name="game" type="be.bluexin.raidingorganizer.database.DbGame" -->
<#--noinspection JSIgnoredPromiseFromCall-->
<html>
<head>
    <#include "include/head.ftl">
    <link href="https://transloadit.edgly.net/releases/uppy/v0.24.2/dist/uppy.min.css" rel="stylesheet">
    <title>Instance Index - Raiding Organizer</title>
</head>
<body>
<#include "include/header.ftl">
<section class="section">
    <div class="container">
    <#list instances as target>
        <#include "include/instancecard.ftl">
    </#list>
    </div>
</section>
<#include "include/footer.ftl">
</body>
</html>
