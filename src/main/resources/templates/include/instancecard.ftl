<#-- @ftlvariable name="target" type="be.bluexin.raidingorganizer.database.DbInstance" -->
<#-- @ftlvariable name="game" type="be.bluexin.raidingorganizer.database.DbGame" -->
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
                    <a href="/game/${game.slug}/instance/${target.slug}"><h1 class="title">${target.name}
                    <#if target.altname??><i class="subtitle"> aka ${target.altname}</i></#if>
                    <small>${target.spots} <i class="fas fa-users"></i></small></h1></a>
                    <#if target.description??>
                        ${target.description}
                    <#else>
                        <i>No description provided</i>
                    </#if>
                </div>
            </span>
</article>