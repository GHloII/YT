
/*
 |--------------------------------------------------------------------------
 | Browser-sync config file
 |--------------------------------------------------------------------------
 |
 | For up-to-date information about the options:
 |   http://www.browsersync.io/docs/options/
 |
 | There are more options than you see here, these are just the ones that are
 | set internally. See the website for more info.
 |
 |
 */
module.exports = {
    "ui": {
        "port": 3001
    },
    "port": 3000,
    "files": ["../static/**/*"],
    "watch": true,
    "single": false,
    "watchOptions": {
        "ignoreInitial": true
    },
    "server": false,
    "proxy": "http://localhost:80",
    "serveStatic": ["../static"],
    "minify": false,
    snippetOptions: {
        rule: {
            match: /<\/body>/i,
            fn: (snippet, match) => `${match}\n<script src="/dev-only.js" defer type="module"></script>\n${snippet}\n`
        }
    }
};