package org.lineageos.loscoins.util

import android.util.Log
import org.json.JSONArray
import org.lineageos.loscoins.db.ValueConverter
import org.lineageos.loscoins.model.Commit
import java.net.URL
import java.util.regex.Pattern

class GerritRemoteContent(
    private val gerritHttpUrl: String
) {
    fun getCommitInfo(id: Int, mustBeOpen: Boolean = false): Commit? {
        val json = getJsonObject(URL(GET_COMMIT.format(gerritHttpUrl, id)), skipLines = 1L)
        Log.d(TAG, json.toString(2))

        return if (json.has("id")) {
            val creationDate = ValueConverter.stringToLocalDate(
                json.getString("created").substring(0, 10)
            )
            val closeDate = if (json.has("submitted")) {
                ValueConverter.stringToLocalDate(json.getString("submitted").substring(0, 10))
            } else {
                null
            }

            if (mustBeOpen && json.has("status") && "NEW" != json.getString("status")) {
                null
            } else {
                Commit(
                    id = id,
                    branch = json.getString("branch"),
                    owner = json.getJSONObject("owner").getString("_account_id"),
                    subject = json.getString("subject"),
                    creationDate = creationDate,
                    closeDate = closeDate,
                )
            }
        } else {
            null
        }
    }

    fun getBranches(): List<String> {
        val json = getJsonObject(
            URL(
                GET_BRANCHES.format(
                    gerritHttpUrl,
                    BRANCH_PROJECT.replace("/", "%2F")
                )
            ),
            skipLines = 1L,
        )
        return if (json is JSONArray) {
            val list = mutableListOf<String>()
            var i = 0
            while (i < json.length()) {
                val obj = json.getJSONObject(i++)
                if (obj.has("ref")) {
                    val ref = obj.getString("ref")
                    if (BRANCH_REGEX.matcher(ref).matches()) {
                        list.add(ref)
                    } else {
                        Log.w(TAG, "Skipping branch: $ref")
                    }
                }
            }
            list
        } else {
            emptyList()
        }
    }

    private companion object {
        private const val TAG = "GerritRemoteContent"
        private const val BRANCH_PROJECT = "LineageOS/android"
        private const val REF_HEADS = "refs/heads/"
        private val BRANCH_REGEX = Pattern.compile("^${REF_HEADS}lineage-\\d\\d(.\\d)?\$")

        // https://review.lineageos.org/Documentation/rest-api-changes.html#get-change-detail
        private const val GET_COMMIT = "%1\$s/changes/%2\$d"

        // https://review.lineageos.org/Documentation/rest-api-projects.html#list-branches
        private const val GET_BRANCHES = "%1\$s/%2\$s/branches"
    }
}