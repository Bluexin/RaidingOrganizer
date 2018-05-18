function replaceAll() {
    replaces("user");
    replaces("game");
    makeLightBoxes();
}

function replaces(type) {
    const elements = document.getElementsByTagName(type);
    while (elements.length > 0) {
        const target = elements[0];
        const id = target.getAttribute("uid");
        if (id != null) {
            const replaceWith = document.createElement("a");
            replaceWith.innerHTML = target.innerHTML;
            replaceWith.setAttribute("class", ((target.getAttribute("class") || "") + " tooltip").trim());
            replaceWith.setAttribute("href", `/${type}/${id}`);
            const frame = document.createElement("iframe");
            frame.setAttribute("onload", "iframeLoaded(this)");
            frame.setAttribute("src", `/embed/${type}/${id}`);
            frame.setAttribute("scrolling", "no");
            replaceWith.appendChild(frame);
            target.parentNode.replaceChild(replaceWith, target);
        }
    }
}

let lightbox_id = 0;

function makeLightBoxes() {
    const elements = document.getElementsByTagName("lightbox");
    while (elements.length > 0) {
        const target = elements[0];
        const replaceWith = document.createElement("div");
        const lightbox = document.createElement("a");
        const lightbox_target = document.createElement("a");
        const ref = `lb_${lightbox_id++}`;

        const image = document.createElement("img");
        image.src = target.getAttribute("src");
        image.setAttribute("class", target.getAttribute("iclass") || "");
        let _style = target.style;
        if (!_style) _style = new CSSStyleDeclaration();
        _style.setProperty("background-color", "black");
        image.style = _style;

        lightbox.setAttribute("class", ((target.getAttribute("class") || "") + " lightbox image").trim());
        lightbox.href = `#${ref}`;
        lightbox.appendChild(image.cloneNode());

        lightbox_target.setAttribute("class", "lightbox-target");
        lightbox_target.href = "#";
        lightbox_target.id = ref;
        lightbox_target.appendChild(image);

        replaceWith.appendChild(lightbox);
        replaceWith.appendChild(lightbox_target);
        target.parentNode.replaceChild(replaceWith, target);
    }
}

function iframeLoaded(frame) {
    if (frame) {
        frame.style = "display: inline;";
        frame.height = frame.contentDocument.body.scrollHeight + "px";
        frame.width = frame.contentDocument.body.scrollWidth + "px";
        frame.style = "";
    }
}

if (window.addEventListener)
    window.addEventListener("load", replaceAll, false);
else if (window.attachEvent)
    window.attachEvent("onload", replaceAll);
else window.onload = replaceAll;