<#-- @ftlvariable name="target" type="be.bluexin.raidingorganizer.database.User" -->
<html style="background: none">
<head>
    <#include "../include/head.ftl">
</head>
<body>
<article class="message is-medium">
    <div class="message-header">
        <p>${(target.name)!"User not found"}</p>
    </div>
    <div class="message-body">
        <article class="media">
            <#if target?? && target.avatarUrl??>
                <figure class="media-left">
                    <p class="image is-64x64">
                        <img src="${target.avatarUrl}">
                    </p>
                </figure>
            </#if>
            <div class="media-content">
                <div class="content">
                    <h1 class="title">${(target.name)!"User not found"}
                    <#if target?? && target.name != target.discordUser.handle>
                        <i class="subtitle">@${target.discordUser.handle}</i>
                    </#if></h1>
                </div>
            </div>
            <br/>
        </article>
    </div>
</article>
</body>
</html>
