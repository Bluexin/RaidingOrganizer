<div class="card">
    <div class="card-image">
        <figure class="image is-4by3">
            <img src="https://bulma.io/images/placeholders/1280x960.png" alt="Placeholder image">
        </figure>
    </div>
    <div class="card-content">
        <div class="media">
            <div class="media-left">
                <figure class="image is-64x64">
                    <img src="${user.avatar}" alt="Avatar">
                </figure>
            </div>
            <div class="media-content">
                <p class="title is-4">${user.name}</p>
                <#if user.name != user.discordUser.handle>
                        <p class="subtitle is-6">@${user.discordUser.handle}</p>
                </#if>
            </div>
        </div>

        <div class="content">
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
            Phasellus nec iaculis mauris.
        </div>
    </div>
</div>