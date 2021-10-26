package org.lineageos.loscoins

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import org.lineageos.loscoins.adapter.CollectiblesAdapter
import org.lineageos.loscoins.data.CollectibleDataSource
import org.lineageos.loscoins.db.data.DbCollectibleDataSource
import org.lineageos.loscoins.model.Collectible

class CollectiblesActivity : AppCompatActivity() {
    private lateinit var collectibleDataSource: CollectibleDataSource

    private lateinit var emptyListView: TextView
    private lateinit var collectiblesList: RecyclerView

    private lateinit var collectiblesAdapter: CollectiblesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        collectibleDataSource = DbCollectibleDataSource(this)

        setContentView(R.layout.activity_collectibles)

        emptyListView = findViewById(R.id.collectibles_empty)
        collectiblesList = findViewById(R.id.collectibles_list)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        collectiblesAdapter = CollectiblesAdapter()
        collectiblesList.apply {
            layoutManager = GridLayoutManager( this@CollectiblesActivity, 2)
            itemAnimator = DefaultItemAnimator()
            adapter = collectiblesAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        loadCollectibles()
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("deprecation")
    private fun loadCollectibles() {
        object : AsyncTask<Unit, Unit, List<Collectible>>() {
            override fun doInBackground(vararg params: Unit?): List<Collectible> {
                return collectibleDataSource.getAll()
            }

            override fun onPostExecute(result: List<Collectible>?) {
                result?.let {
                    collectiblesAdapter.setData(it)
                    if (it.isEmpty()) {
                        emptyListView.visibility = View.VISIBLE
                        collectiblesList.visibility = View.GONE
                    } else {
                        emptyListView.visibility = View.GONE
                        collectiblesList.visibility = View.VISIBLE
                    }
                }
            }
        }.execute()
    }
}