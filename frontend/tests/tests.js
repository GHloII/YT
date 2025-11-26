
import { videoIdByYoutubeUrl, qualityNameIdPairs, youtubeUrlById } from '/functions.js'
QUnit.config.autostart = false
QUnit.module('videoIdByYoutubeUrl', function () {
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
    QUnit.test('video id in pathname with params 1', (assert) => {
        assert.equal(
            videoIdByYoutubeUrl('https://youtu.be/uOkV7Z-yDYg?si=MK3LoN8Kbq-X0uIG'),
            'uOkV7Z-yDYg'
        )
    })
    QUnit.test('watch with v param 1', (assert) => {
        assert.equal(
            videoIdByYoutubeUrl('https://www.youtube.com/watch?v=uOkV7Z-yDYg'),
            'uOkV7Z-yDYg'
        )
    })
    QUnit.test('shorts url', (assert) => {
        assert.deepEqual(
            videoIdByYoutubeUrl('https://youtube.com/shorts/t-7ZP09lkIA?si=NiiKCasMbp92EDzL'),
            't-7ZP09lkIA'
        )
    })

    QUnit.test('gibberish returns null', (assert) => {
        assert.equal(videoIdByYoutubeUrl('asdfljasdas;lsdkjf'), null)
    })
})

QUnit.module('youtubeUrlById', function () {
    QUnit.test('example', (assert) => assert.equal(youtubeUrlById('dQw4w9WgXcQ'), 'https://youtu.be/dQw4w9WgXcQ'))
})


QUnit.module('qualityNameIdPairs', function () {
    QUnit.test('example', (assert) => {
        assert.deepEqual(
            qualityNameIdPairs({
                "144p": "278",
                "240p": "242",
                "360p": "243",
                "480p": "244",
                "720p": "247",
                "1080p": "248"
            }),

            [
                { "name": "1080p", "id": "248" },
                { "name": "720p", "id": "247" },
                { "name": "480p", "id": "244" },
                { "name": "360p", "id": "243" },
                { "name": "240p", "id": "242" },
                { "name": "144p", "id": "278" }
            ]
        )
    })
})

QUnit.start()