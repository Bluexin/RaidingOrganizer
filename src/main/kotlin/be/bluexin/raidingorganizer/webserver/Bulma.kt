package be.bluexin.raidingorganizer.webserver

enum class Themes(val description: String) {
    CERULEAN("A calm blue sky"),
    COSMO("An ode to Metro"),
    CYBORG("Jet black and electric blue"),
    DARKLY("Flatly in night-mode"),
    DEFAULT("Bulma as-is"),
    FLATLY("Flat and thick"),
    JOURNAL("Crisp like a new sheet of paper"),
    LITERA("The medium is the message"),
    LUMEN("Light and shadow"),
    LUX("A touch of class"),
    MATERIA("Material is the metaphor"),
    MINTY("A fresh feel"),
    NUCLEAR("A dark theme with irradiated highlights"),
    PULSE("A trace of purple"),
    SANDSTONE("A touch of warmth"),
    SIMPLEX("Mini and minimalist"),
    SLATE("Shades of gunmetal gray"),
    SOLAR("A spin on Solarized"),
    SPACELAB("Silvery and sleek"),
    SUPERHERO("The brave and the blue"),
    UNITED("Ubuntu orange and unique font"),
    YETI("A friendly foundation");

    override fun toString(): String {
        return super.toString().toLowerCase()
    }

    fun pretty() = toString().capitalize()
}

/*
DL from https://jenil.github.io/bulmaswatch/api/themes.json
Regex on json (after removing the useless stuff) :
From `\{\s+"name": "(\w+)",\s+"\w+": "([\w -]+)"([^}]|\n)+\}`
To `\U$1("\E$2")`
 */