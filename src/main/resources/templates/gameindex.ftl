<#-- @ftlvariable name="games" type="java.util.List<be.bluexin.raidingorganizer.database.DbGame>" -->
<#--noinspection JSIgnoredPromiseFromCall-->
<html>
<head>
    <#include "include/head.ftl">
    <link href="https://transloadit.edgly.net/releases/uppy/v0.24.2/dist/uppy.min.css" rel="stylesheet">
    <title>Game Index - Raiding Organizer</title>
</head>
<body>
<#include "include/header.ftl">
<section class="section">
    <div class="container">
    <#list games as target>
        <#include "include/gamecard.ftl">
    </#list>
    </div>
</section>
<#include "include/footer.ftl">
</body>
</html>
