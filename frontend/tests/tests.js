
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
        assert.equal(
            videoIdByYoutubeUrl('https://youtube.com/shorts/t-7ZP09lkIA?si=NiiKCasMbp92EDzL'),
            't-7ZP09lkIA'
        )
    })
    

})

QUnit.start()