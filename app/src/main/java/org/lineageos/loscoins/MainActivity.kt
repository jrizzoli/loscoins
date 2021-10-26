package org.lineageos.loscoins

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.lineageos.loscoins.adapter.CommitsAdapter
import org.lineageos.loscoins.data.RemoteStatusDataSource
import org.lineageos.loscoins.data.TrackedCommitDataSource
import org.lineageos.loscoins.data.TransactionDataSource
import org.lineageos.loscoins.db.data.DbRemoteStatusDataSource
import org.lineageos.loscoins.db.data.DbTrackedCommitDataSource
import org.lineageos.loscoins.db.data.DbTransactionDataSource
import org.lineageos.loscoins.model.TrackedCommit
import org.lineageos.loscoins.user.UserInfoProvider
import org.lineageos.loscoins.util.GerritRemoteContent
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MainActivity : Activity() {
    private lateinit var userInfoProvider: UserInfoProvider
    private lateinit var transactionsDataSource: TransactionDataSource
    private lateinit var trackedCommitsDataSource: TrackedCommitDataSource
    private lateinit var remoteStatusDataSource: RemoteStatusDataSource
    private lateinit var gerritRemoteContent: GerritRemoteContent

    private lateinit var balanceView: TextView
    private lateinit var ownerView: TextView
    private lateinit var shopBtn: Button
    private lateinit var collectiblesBtn: Button
    private lateinit var commitsList: RecyclerView
    private lateinit var emptyListView: TextView
    private lateinit var addCommitsBtn: FloatingActionButton

    private lateinit var commitsAdapter: CommitsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userInfoProvider = UserInfoProvider(this)
        transactionsDataSource = DbTransactionDataSource(this)
        trackedCommitsDataSource = DbTrackedCommitDataSource(this, MarinatingStonks())
        remoteStatusDataSource = DbRemoteStatusDataSource(this)
        gerritRemoteContent = GerritRemoteContent(getString(R.string.config_gerrit_url))

        if (!userInfoProvider.isInitialized()) {
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_main)

        balanceView = findViewById(R.id.main_card_balance)
        ownerView = findViewById(R.id.main_card_owner)
        shopBtn = findViewById(R.id.main_btn_shop)
        collectiblesBtn = findViewById(R.id.main_btn_collectibles)
        commitsList = findViewById(R.id.commits_list)
        emptyListView = findViewById(R.id.commits_empty)
        addCommitsBtn = findViewById(R.id.commits_add)

        shopBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ShopActivity::class.java
                )
            )
        }
        collectiblesBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CollectiblesActivity::class.java
                )
            )
        }

        commitsAdapter = CommitsAdapter()
        commitsList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = DefaultItemAnimator()
            adapter = commitsAdapter
        }

        addCommitsBtn.setOnClickListener { addCommitDialog() }
    }

    override fun onResume() {
        super.onResume()

        loadBalance()
        loadCommits()
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("deprecation")
    private fun loadBalance() {
        object : AsyncTask<Unit, Unit, Long>() {
            override fun doInBackground(vararg params: Unit?): Long {
                return transactionsDataSource.currentBalance()
            }

            override fun onPostExecute(result: Long?) {
                result?.let {
                    ownerView.text = userInfoProvider.getName()
                    balanceView.text = it.toString()
                }
            }
        }.execute()
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("deprecation")
    private fun loadCommits() {
        object : AsyncTask<Unit, Unit, List<TrackedCommit>>() {
            override fun doInBackground(vararg params: Unit?): List<TrackedCommit> {
                return fetchCommits()
            }

            override fun onPostExecute(result: List<TrackedCommit>?) {
                result?.let {
                    commitsAdapter.setData(it)
                    if (it.isEmpty()) {
                        emptyListView.visibility = View.VISIBLE
                        commitsList.visibility = View.GONE
                    } else {
                        emptyListView.visibility = View.GONE
                        commitsList.visibility = View.VISIBLE
                    }
                }
            }
        }.execute()
    }

    private fun addCommitDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.add_commits)
            .setMessage(R.string.commits_add_message)
            .setView(R.layout.dialog_add_commit)
            .create()

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.commits_add)) { d, _ ->
            val field = dialog.findViewById<EditText>(R.id.commit_number)
            addCommit(field?.text?.toString()?.toInt() ?: 0)
            d.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel)) { d, _ ->
            d.dismiss()
        }
//        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.commits_add_my)) { d, _ ->
//            TODO()
//        }
        dialog.show()
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("deprecation")
    private fun addCommit(id: Int) {
        object : AsyncTask<Int, Unit, TrackedCommit?>() {
            override fun doInBackground(vararg params: Int?): TrackedCommit? {
                val ci = gerritRemoteContent.getCommitInfo(params[0]!!)
                return ci?.let { trackedCommitsDataSource.trackCommit(it) }
            }

            override fun onPostExecute(result: TrackedCommit?) {
                if (result == null) {
                    onCommitNotAdded()
                } else {
                    onCommitAdded(result)
                }
            }
        }.execute(id)
    }

    @WorkerThread
    private fun fetchCommits(): List<TrackedCommit> {
        if (remoteStatusDataSource.getLastCommitsUpdate()
                .until(LocalDateTime.now(), ChronoUnit.HOURS) > 24
        ) {
            // Refresh status
            trackedCommitsDataSource.getOpenCommits().forEach {
                val info = gerritRemoteContent.getCommitInfo(it.commit.id)
                if (info?.closeDate != null) {
                    trackedCommitsDataSource.closeCommit(it.commit)
                }
            }
        }
        return trackedCommitsDataSource.getOpenCommits()
    }

    private fun onCommitNotAdded() {
        AlertDialog.Builder(this)
            .setTitle(R.string.no)
            .setMessage(R.string.commit_add_error)
            .setPositiveButton(android.R.string.ok) { d, _ -> d.dismiss() }
            .show()
    }

    private fun onCommitAdded(commit: TrackedCommit) {
        commitsAdapter.add(commit)
        if (commitsList.visibility == View.GONE) {
            commitsList.visibility = View.VISIBLE
            emptyListView.visibility = View.GONE
        }
    }
}