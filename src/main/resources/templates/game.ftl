<#--noinspection JSIgnoredPromiseFromCall-->
<#-- @ftlvariable name="target" type="be.bluexin.raidingorganizer.database.DbGame" -->
<html>
<head>
    <#include "include/head.ftl">
    <link href="https://transloadit.edgly.net/releases/uppy/v0.24.2/dist/uppy.min.css" rel="stylesheet">
    <title>Game ${(target.name)!"not found"} - Raiding Organizer</title>
</head>
<body>
<#include "include/header.ftl">
<section class="section">
    <div class="container">
    <#if target??>
        <#include "include/gamecard.ftl">
    <#else>
        <section class="section">
            <h1 class="title container">Game not found.</h1>
        </section>
    </#if>
    </div>
</section>
<#include "include/footer.ftl">
</body>
</html>
