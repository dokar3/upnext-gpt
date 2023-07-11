package io.upnextgpt.base.util

import java.security.MessageDigest

fun String.md5(): String {
    val digest = MessageDigest.getInstance("md5")
    return digest.digest(this.toByteArray()).decodeToString()
}

fun String.isHttpUrl(): Boolean {
    return startsWith("https://") || startsWith("http://")
}