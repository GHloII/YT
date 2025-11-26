module.exports = {
    ui: {
        port: 3101
    },
    port: 3100,
    files: ["../tests/**/*"],
    watch: true,
    single: false,
    watchOptions: {
        ignoreInitial: true
    },
    proxy: false,
    server: {
        // serve tests first so root loads tests/index.html
        baseDir: ["../tests", "../static"],
        // map /dependencies for absolute imports
        routes: {
            "/dependencies": "../dependencies"
        }
    },
    minify: false,
    snippetOptions: {
        rule: {
            match: /<\/body>/i,
            fn: (snippet, match) => `${match}${snippet}\n`
        }
    },
    startPath: '/index.html'
};