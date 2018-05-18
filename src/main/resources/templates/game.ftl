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
        <article class="media">
            <figure class="media-left">
                <#if target.background??>
                    <lightbox src="${target.background}" class="is-128x128" iclass="game-logo"></lightbox>
                <#else>
                    <p class="image is-128x128">
                        <img src="">
                    </p>
                </#if>
            </figure>
            <span class="media-content">
                <div class="content is-medium">
                    <h1>${target.name}</h1>
                    <#if target.description??>
                        ${target.description}
                    <#else>
                        <h2><i>No description provided</i></h2>
                    </#if>
                </div>
            </span>
        </article>
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
