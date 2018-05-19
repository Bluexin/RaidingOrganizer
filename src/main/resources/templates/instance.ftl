<#--noinspection JSIgnoredPromiseFromCall-->
<#-- @ftlvariable name="target" type="be.bluexin.raidingorganizer.database.DbInstance" -->
<#-- @ftlvariable name="game" type="be.bluexin.raidingorganizer.database.DbGame" -->
<html>
<head>
    <#include "include/head.ftl">
    <link href="https://transloadit.edgly.net/releases/uppy/v0.24.2/dist/uppy.min.css" rel="stylesheet">
    <title>${(target.name)!"Instance not found"} (${game.name}) - Raiding Organizer</title>
</head>
<body>
<#include "include/header.ftl">
<section class="section">
    <div class="container">
    <#if target??>
        <#include "include/instancecard.ftl">
    <#else>
        <section class="section">
            <h1 class="title container">Instance not found.</h1>
        </section>
    </#if>
    </div>
</section>
<#include "include/footer.ftl">
</body>
</html>
