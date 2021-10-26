package org.lineageos.loscoins.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

fun getJsonObject(url: URL, skipLines: Long = 0L): JSONObject {
    return getJson(url, skipLines) as JSONObject
}

fun getJsonArray(url: URL, skipLines: Long = 0L): JSONArray {
    return getJson(url, skipLines) as JSONArray
}

private fun getJson(url: URL, skipLines: Long): Any {
    val connection = url.openConnection() as HttpsURLConnection
    try {
        if (connection.responseCode != 200) {
            return JSONObject()
        }

        BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
            val str = if (skipLines > 0L) {
                reader.lines()
                    // Gerrit API uses the first line for obj structure
                    // definition, ignore it and assume the API won't change
                    .skip(skipLines)
                    .collect(Collectors.joining("\n"))
            } else {
                reader.readText()
            }
            return JSONTokener(str).nextValue()
        }
    } finally {
        connection.disconnect()
    }
}
