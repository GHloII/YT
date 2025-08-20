# REST API Documentation for YouTube Downloader

This document describes the REST API endpoints provided by the YouTube Downloader application.

## Table of Contents
* [1. Get Video Information](#1-get-video-information)
* [2. Download Video](#2-download-video)

---

## 1. Get Video Information

### Endpoint

`GET /info`

### Description

This endpoint allows you to retrieve detailed information about a YouTube video, including its title, available resolutions, thumbnail URL, and format details for various qualities.

### Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| `url` | `String` | Yes | The URL of the YouTube video for which to retrieve information. |

### Response (JSON)

The response is a JSON object of type `VideoInfoResponse`.

```json
{
  "title": "String",
  "resolutions": [
    "String"
  ],
  "thumbnail": "String",
  "sizeByQualityName": {
    "String": "Long"
  },
  "idByQualityName": {
    "String": "String"
  },
  "audioId": "int"
}
```

#### Fields

*   `title` (String): The title of the YouTube video.
*   `resolutions` (List<String>): A list of available video resolutions (e.g., "1080p", "720p").
*   `thumbnail` (String): The URL of the video's thumbnail image.
*   `sizeByQualityName` (Map<String, Long>): A map where keys are quality names (resolutions) and values are the file sizes in bytes for that quality.
*   `idByQualityName` (Map<String, String>): A map where keys are quality names (resolutions) and values are the format IDs for that quality.
*   `audioId` (int): The format ID of the best available audio stream.

### Example

#### Request

```bash
curl -X GET "http://localhost:8080/info?url=https://www.youtube.com/watch?v=dQw4w9WgXcQ"
```

#### Response (Example)

```json
{
  "title": "Rick Astley - Never Gonna Give You Up (Official Music Video)",
  "resolutions": [
    "360p",
    "480p",
    "720p",
    "1080p"
  ],
  "thumbnail": "https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg",
  "sizeByQualityName": {
    "360p": 1234567,
    "480p": 2345678,
    "720p": 3456789,
    "1080p": 4567890
  },
  "idByQualityName": {
    "360p": "18",
    "480p": "336",
    "720p": "22",
    "1080p": "137"
  },
  "audioId": 251
}
```

## 2. Download Video

### Endpoint

`GET /download`

### Description

This endpoint allows you to download a YouTube video directly through a stream. The video will be streamed as `video/mp4`.

### Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| `url` | `String` | Yes | The URL of the YouTube video to download. |

### Response

The video content is streamed directly as a `video/mp4` file. The response headers are set to facilitate streaming and direct download (`Content-Disposition: attachment; filename="video.mp4"`).

### Example

#### Request

```bash
curl -X GET "http://localhost:8080/download?url=https://www.youtube.com/watch?v=dQw4w9WgXcQ" -o video.mp4
```
