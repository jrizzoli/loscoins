package org.lineageos.loscoins.db.data

import android.content.ContentValues
import android.content.Context
import org.lineageos.loscoins.MarinatingStonks
import org.lineageos.loscoins.data.TrackedCommitDataSource
import org.lineageos.loscoins.db.AppDbHelper
import org.lineageos.loscoins.db.ValueConverter
import org.lineageos.loscoins.db.column.CommitColumns
import org.lineageos.loscoins.db.table.CommitTable
import org.lineageos.loscoins.model.Commit
import org.lineageos.loscoins.model.TrackedCommit

class DbTrackedCommitDataSource(
    context: Context?,
    private val stonks: MarinatingStonks,
) : TrackedCommitDataSource {
    private val dbHelper = AppDbHelper.getInstance(context)

    override fun getOpenCommits(): List<TrackedCommit> {
        dbHelper.readableDatabase.use { db ->
            db.query(
                CommitTable.NAME, PROJECTION, OPEN_SELECTION, null,
                null, null, ORDER_BY
            ).use { c ->
                val list = mutableListOf<Commit>()
                while (c.moveToNext()) {
                    list.add(
                        Commit(
                            id = c.getInt(0),
                            branch = c.getString(1),
                            owner = c.getString(2),
                            subject = c.getString(3),
                            creationDate = ValueConverter.stringToLocalDate(c.getString(4)),
                            closeDate = null,
                        )
                    )
                }
                return list.map { TrackedCommit(it, stonks.valueOf(it)) }
            }
        }
    }

    override fun getClosedCommits(): List<TrackedCommit> {
        dbHelper.readableDatabase.use { db ->
            db.query(
                CommitTable.NAME, PROJECTION, CLOSED_SELECTION, null,
                null, null, ORDER_BY
            ).use { c ->
                val list = mutableListOf<Commit>()
                while (c.moveToNext()) {
                    list.add(
                        Commit(
                            id = c.getInt(0),
                            branch = c.getString(1),
                            owner = c.getString(2),
                            subject = c.getString(3),
                            creationDate = ValueConverter.stringToLocalDate(c.getString(4)),
                            closeDate = ValueConverter.stringToLocalDate(c.getString(5)),
                        )
                    )
                }
                return list.map { TrackedCommit(it, stonks.valueOf(it)) }
            }
        }
    }

    override fun trackCommit(commit: Commit): TrackedCommit? {
        if (commit.closeDate != null) {
            return null
        }

        dbHelper.writableDatabase.use { db ->
            db.beginTransaction()
            val cv = ContentValues().apply {
                put(CommitColumns._ID, commit.id)
                put(CommitColumns.BRANCH, commit.branch)
                put(CommitColumns.OWNER, commit.owner)
                put(CommitColumns.SUBJECT, commit.subject)
                put(
                    CommitColumns.CREATION_DATE,
                    ValueConverter.localDateToString(commit.creationDate)
                )
                put(CommitColumns.CLOSE_DATE, null as String?)
            }

            db.insert(CommitTable.NAME, null, cv)
            db.setTransactionSuccessful()
            db.endTransaction()

            return TrackedCommit(commit, stonks.valueOf(commit))
        }
    }

    override fun unTrackCommit(commit: Commit) {
        dbHelper.writableDatabase.use { db ->
            db.beginTransaction()

            db.delete(CommitTable.NAME, BY_ID_SELECTION, arrayOf(commit.id.toString()))

            db.setTransactionSuccessful()
            db.endTransaction()
        }
    }

    override fun closeCommit(commit: Commit): Boolean {
        if (commit.closeDate == null) {
            return false
        }

        dbHelper.writableDatabase.use { db ->
            db.beginTransaction()

            val cv = ContentValues().apply {
                put(CommitColumns.CLOSE_DATE, ValueConverter.localDateToString(commit.closeDate))
            }

            db.update(CommitTable.NAME, cv, BY_ID_SELECTION, arrayOf(commit.id.toString()))

            db.setTransactionSuccessful()
            db.endTransaction()
            return true
        }
    }

    private companion object {
        val PROJECTION = arrayOf(
            CommitColumns._ID,
            CommitColumns.BRANCH,
            CommitColumns.OWNER,
            CommitColumns.SUBJECT,
            CommitColumns.CREATION_DATE,
            CommitColumns.CLOSE_DATE,
        )

        const val OPEN_SELECTION = "${CommitColumns.CLOSE_DATE} IS NULL"
        const val CLOSED_SELECTION = "${CommitColumns.CLOSE_DATE} IS NOT NULL"
        const val ORDER_BY = CommitColumns.CREATION_DATE
        const val BY_ID_SELECTION = "${CommitColumns._ID} = ?"
    }
}