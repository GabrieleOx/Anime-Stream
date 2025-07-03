package com.me.animedownloader


fun getSingleEp(episodeUrl: String): String {
    var j = episodeUrl.length - 4
    while (episodeUrl[j - 1] != '/') j--
    return episodeUrl.substring(j, episodeUrl.length - 4)
}