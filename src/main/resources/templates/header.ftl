<section class="hero is-primary is-bold">
    <div class="hero-body">
        <nav class="level">
            <script>
                function logout() {
                    window.fetch(new Request('/logout', {
                        method: 'POST',
                        credentials: 'include'
                    })).then(result => {
                        if (result.ok) {
                            window.localStorage.setItem('logged_in', 'false');
                            window.location.reload();
                        }
                    })
                }

                function popup() {
                    open("/login", "", "menubar=yes,location=no,personalbar=no,scrollbars=yes,width=500,height=518.5,resizable");
                }

                function storageChange(event) {
                    if (event.key === 'logged_in') {
                        window.location.reload();
                    }
                }

                window.addEventListener('storage', storageChange, false)
            </script>
            <p class="title level-item has-text-centered">
                <a href="/">Raiding Organizer <strong>BETA</strong></a>
            </p>
            <p class="level-item has-text-centered">
                <#if user??>
                    Logged in as&nbsp;<a href="/user/${user.id}">${user.name}</a>&nbsp;(<a onclick="logout()">logout</a>)
                <#else>
                    <a onclick="popup()">Log in</a>
                </#if>
            </p>
            <p class="level-item has-text-centered">
                <a class="link is-info">Search</a>
            </p>
        </nav>
    </div>
</section>