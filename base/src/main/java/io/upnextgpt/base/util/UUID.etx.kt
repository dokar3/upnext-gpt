package io.upnextgpt.base.util

import java.util.UUID

fun UUID.longValue(): Long {
    return mostSignificantBits and Long.MAX_VALUE
}