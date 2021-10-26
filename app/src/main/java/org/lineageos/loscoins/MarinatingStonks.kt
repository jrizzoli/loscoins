package org.lineageos.loscoins

import org.lineageos.loscoins.model.Commit
import java.time.LocalDate
import kotlin.math.ceil

class MarinatingStonks(
    private val branchAges: List<String> = DEFAULT_BRANCHES,
) {

    fun valueOf(commit: Commit): Long {
        val end = commit.closeDate ?: LocalDate.now()
        val dayDiff = ((end.year - commit.creationDate.year) * 365) +
                (end.dayOfYear - commit.creationDate.dayOfYear)
        return ceil(dayDiff * DAY_MULTIPLIER * branchMultiplier(commit.branch)).toLong()
    }

    private fun branchMultiplier(commitBranch: String): Float {
        if (branchAges.isEmpty()) {
            return 1f
        }

        var i = 0
        while (i < branchAges.size) {
            if (commitBranch == branchAges[i]) {
                break
            } else {
                i++
            }
        }
        return when (i) {
            0, branchAges.size -> 1f
            else -> 1f + (BRANCH_MULTIPLIER * i)
        }
    }

    private companion object {
        const val BRANCH_MULTIPLIER = 0.15f
        const val DAY_MULTIPLIER = 2.5f
        val DEFAULT_BRANCHES = listOf(
            "refs/heads/lineage-19.0",
            "refs/heads/lineage-18.1",
            "refs/heads/lineage-18.0",
            "refs/heads/lineage-17.1",
            "refs/heads/lineage-17.0",
            "refs/heads/lineage-16.0",
            "refs/heads/lineage-15.1",
            "refs/heads/lineage-14.1",
        )
    }
}