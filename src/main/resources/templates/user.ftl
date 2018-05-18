<#--noinspection JSIgnoredPromiseFromCall-->
<#-- @ftlvariable name="target" type="be.bluexin.raidingorganizer.database.User" -->
<html>
<head>
    <#include "include/head.ftl">
    <link href="https://transloadit.edgly.net/releases/uppy/v0.24.2/dist/uppy.min.css" rel="stylesheet">
    <title>User ${(target.name)!"not found"} - Raiding Organizer</title>
</head>
<body>
<#include "include/header.ftl">
<section class="section">
    <div class="container">
    <#if target??>
        <#if user??>
            <#assign edit=target.id.value == user.id.value>
            <#if edit>
            <script src="/static/js/edituser.min.js"></script>
            </#if>
        <#else>
            <#assign edit=false>
        </#if>
        <article class="media">
            <figure class="media-left">
                <lightbox src="${target.avatarUrl}" class="is-128x128"></lightbox>
            </figure>
            <div class="content">
                <h1 id="name_display" class="title">${target.name}
                    <#if target.name != target.discordUser.handle>
                            &nbsp;<small>@${target.discordUser.handle}</small>
                    </#if>
                    <#if edit>
                        <a class="button" onclick="t.showNameEdit()">
                            <i class="fas fa-edit"></i>
                        </a>
                    </#if>
                </h1>
                <#if edit>
                    <div id="name_input" class="field has-addons is-hidden" hidden>
                        <div class="control">
                            <input id="name_field" class="input is-medium" type="text" placeholder="${user.name}">
                        </div>
                        <div class="control">
                            <a class="button is-success is-medium" onclick="t.saveName(${user.id})">
                                Save
                            </a>
                        </div>
                        <div class="control">
                            <a class="button is-info is-medium" onclick="t.hideNameEdit()">
                                Cancel
                            </a>
                        </div>
                        <div class="control">
                            <a class="button is-danger is-medium"
                               onclick="t.resetName(${user.id} ,'${user.discordUser.handle}')">
                                Reset
                            </a>
                        </div>
                        <div class="control has-icons-left">
                            <input class="input is-medium" type="text" value="@${user.discordUser.handle}"
                                   placeholder="Discord handle" readonly>
                            <span class="icon is-left is-medium">
                              <i class="fab fa-discord"></i>
                            </span>
                        </div>
                        <div class="control">
                            <a class="button is-medium">
                                <i class="fas fa-sync" onclick="t.syncDiscord(${user.id})"></i>
                            </a>
                        </div>
                    </div>
                    <div class="field has-addons">
                        <div class="control" onclick="t.showUppy()">
                            <a class="button is-success is-medium">
                                <i class="fas fa-upload"></i>
                            </a>
                        </div>
                        <div class="control">
                            <a class="button is-medium" onclick="t.setAvatar(${user.id}, 'discord')">
                                <span class="icon">
                                    <i class="fab fa-discord"></i>
                                </span>
                                <span>Use Discord Avatar</span>
                            </a>
                        </div>
                    </div>
                    <div class="field has-addons">
                        <div class="control">
                            <a class="button is-medium">
                                Theme
                            </a>
                        </div>
                        <div class="dropdown is-hoverable control">
                            <div class="dropdown-trigger">
                                <button class="button is-medium" aria-haspopup="true" aria-controls="dropdown-menu">
                                    <span>${user.theme.pretty()}</span>
                                    <span class="icon is-small">
                                        <i class="fas fa-angle-down" aria-hidden="true"></i>
                                    </span>
                                </button>
                            </div>
                            <div class="dropdown-menu" id="dropdown-menu" role="menu">
                                <div class="dropdown-content">
                                    <#list user.theme.declaringClass.enumConstants as theme>
                                        <a class="dropdown-item<#if theme == user.theme> is-active</#if>"
                                           onclick="t.setTheme(${user.id}, '${theme.name()}')">
                                            <strong>${theme.pretty()}</strong> : ${theme.description}<br/>
                                        </a>
                                    </#list>
                                </div>
                            </div>
                        </div>
                    </div>

                </#if>
            </div>
            <br/>
        </article>
    <#else>
        <section class="section">
            <h1 class="title container">User not found.</h1>
        </section>
    </#if>
    </div>
</section>
<#include "include/footer.ftl">
</body>
</html>
