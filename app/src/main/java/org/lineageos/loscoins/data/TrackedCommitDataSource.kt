package org.lineageos.loscoins.data

import org.lineageos.loscoins.model.Commit
import org.lineageos.loscoins.model.TrackedCommit

interface TrackedCommitDataSource {
    fun getOpenCommits(): List<TrackedCommit>
    fun getClosedCommits(): List<TrackedCommit>
    fun trackCommit(commit: Commit): TrackedCommit?
    fun unTrackCommit(commit: Commit)
    fun closeCommit(commit: Commit): Boolean
}