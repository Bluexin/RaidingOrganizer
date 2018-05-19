<#-- @ftlvariable name="target" type="be.bluexin.raidingorganizer.database.DbGame" -->
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
                    <h1 class="title">${target.name}</h1>
                    <#if target.description??>
                        ${target.description}
                    <#else>
                        <i>No description provided</i>
                    </#if>
                </div>
            </span>
</article>