<#-- @ftlvariable name="target" type="be.bluexin.raidingorganizer.database.DbGame" -->
<html style="background: none">
<head>
    <#include "../include/head.ftl">
</head>
<body>
<article class="message is-medium">
    <div class="message-header">
        <p>${(target.name)!"Game not found"}</p>
    </div>
    <div class="message-body">
        <article class="media">
            <#if target?? && target.background??>
                <figure class="media-left">
                    <p class="image is-64x64">
                        <img src="${target.background}" class="game-logo">
                    </p>
                </figure>
            </#if>
            <div class="media-content">
                <div class="content">
                    <h1 class="title">${(target.name)!"Game not found"}
                </div>
            </div>
            <br/>
        </article>
    </div>
</article>
</body>
</html>
