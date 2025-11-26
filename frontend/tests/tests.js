
import { videoIdByYoutubeUrl } from '/functions.js'
QUnit.config.autostart = false
QUnit.module('youtubeUrlTools', function () {
    QUnit.test('watch v', (assert) => {
        assert.equal(
            videoIdByYoutubeUrl(
                'https://www.youtube.com/watch?v=dQw4w9WgXcQ'
            ),
            'dQw4w9WgXcQ'
        )
    })
    QUnit.test('video id in pathname', (assert) => {
        assert.equal(
            videoIdByYoutubeUrl('https://youtu.be/dQw4w9WgXcQ'),
            'dQw4w9WgXcQ'
        )
    })
    QUnit.test('video id in pathname with params', (assert) => {
        assert.equal(
            videoIdByYoutubeUrl(
                'https://youtu.be/tk7qTNW5g0c?si=r3mtTefigXMjrzcy'
            ),
            'tk7qTNW5g0c'
        )
    })
})

QUnit.start()