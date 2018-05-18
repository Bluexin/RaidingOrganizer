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
    <#list games as game>
        <article class="media">
            <figure class="media-left">
                <#if game.background??>
                    <lightbox src="${game.background}" class="is-128x128" iclass="game-logo"></lightbox>
                <#else>
                    <p class="image is-128x128">
                        <img src="">
                    </p>
                </#if>
            </figure>
            <span class="media-content">
                <div class="content is-medium">
                    <a href="/game/${game.slug}"><h1>${game.name}</h1></a>
                    <#if game.description??>
                        ${game.description}
                    <#else>
                        <h2><i>No description provided</i></h2>
                    </#if>
                </div>
            </span>
        </article>
    </#list>
    </div>
</section>
<#include "include/footer.ftl">
</body>
</html>
