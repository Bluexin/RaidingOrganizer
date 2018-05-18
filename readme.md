# Raiding Organizer

## Requirements
### Running

 * JRE 8
 * MySQL-compatible DB server
 * A [Discord App](https://discordapp.com/developers/applications/me)

### Building

 * JDK 8
 * Local npm modules (so Browserify can import what's needed) :
    * uppy
 * Global npm modules (so Gradle can run the commands) :
    * browserify
    * uglifycss
    * uglify-es

Everything else is managed by Gradle.