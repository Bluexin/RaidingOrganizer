module.exports = {

    showNameEdit: function () {
        const input = document.getElementById("name_input");
        const display = document.getElementById("name_display");
        display.classList.add("is-hidden");
        display.setAttribute('aria-hidden', 'true');
        display.hidden = true;
        input.classList.remove("is-hidden");
        input.removeAttribute('aria-hidden');
        input.hidden = false;
    },

    hideNameEdit: function () {
        const input = document.getElementById("name_input");
        const display = document.getElementById("name_display");
        input.classList.add("is-hidden");
        input.setAttribute('aria-hidden', 'true');
        input.hidden = true;
        display.classList.remove("is-hidden");
        display.removeAttribute('aria-hidden');
        display.hidden = false;
    },

    saveName: function (userid) {
        const name_field = document.getElementById("name_field");
        if (name_field.value.toString() === "") {
            name_field.classList.add("is-danger");
        } else {
            name_field.classList.remove("is-danger");
            postUser(userid, {
                username: name_field.value
            });
        }
    },

    resetName: function (userid, name) {
        console.log(userid + "->" + name);
        postUser(userid, {
            username: name
        });
    },

    syncDiscord: async function (userid) {
        await window.fetch(`/user/${userid}/sync`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(r => {
            if (!r.ok) {
                alert("Something went wrong !")
            }
        });

        window.localStorage.setItem('logged_in', Date.now().toString());
        window.location.reload();
    },

    showUppy: function () {
        avatar_uppy.getPlugin('Dashboard').openModal();
    },

    hideUppy: function () {
        avatar_uppy.getPlugin('Dashboard').closeModal();
    },

    setAvatar: function (userid, avatar) {
        postUser(userid, {
            avatar: avatar
        })
    },

    setTheme: function (userid, theme) {
        postUser(userid, {
            theme: theme
        })
    }
};

async function postUser(userid, user) {
    // username: String? = null, avatar: String? = null, theme: String? = null
    window.fetch(`/user/${userid}`, {
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(user),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }).then(r => {
        if (r.ok) {
            window.localStorage.setItem('logged_in', Date.now().toString());
            window.location.reload();
        } else {
            /*const notif = document.createElement("notification");
            notif.innerHtml = "<div class='notification is-danger' onclick='document.getElementById(\"notification\").remove()'>" +
                    " <button class='delete'></button>" +
                    "  Primar lorem ipsum dolor sit amet, consectetur" +
                    "  adipiscing elit lorem ipsum dolor. <strong>Pellentesque risus mi</strong>, tempus quis placerat ut, porta nec nulla. Vestibulum rhoncus ac ex sit amet fringilla. Nullam gravida purus diam, et dictum <a>felis venenatis</a> efficitur. Sit amet," +
                    "  consectetur adipiscing elit" +
                    "</div>"*/
            alert("Something went wrong !")
        }
    });

}

const Uppy = require('uppy/lib/core');

const Dashboard = require('uppy/lib/plugins/Dashboard');
const Ul = require('uppy/lib/plugins/XHRUpload');

const avatar_uppy = Uppy({
    meta: {type: 'avatar'},
    restrictions: {
        maxNumberOfFiles: 1,
        // allowedFileTypes: ['image/*']
    },
    autoProceed: true,
    id: 'avatar'
})
    .use(Dashboard, {
        trigger: '#select-files',
        showProgressDetails: true
    })
    .use(Ul, {
        endpoint: '/upload/avatar'
    })
    .run();

avatar_uppy.on('upload-success', (file, body) => {
    t.setAvatar(body.user, body.file)
});