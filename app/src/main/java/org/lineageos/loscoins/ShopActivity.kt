package org.lineageos.loscoins

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import org.lineageos.loscoins.adapter.ShopAdapter
import org.lineageos.loscoins.data.CollectibleDataSource
import org.lineageos.loscoins.data.RemoteStatusDataSource
import org.lineageos.loscoins.data.ShopItemDataSource
import org.lineageos.loscoins.data.TransactionDataSource
import org.lineageos.loscoins.db.data.DbCollectibleDataSource
import org.lineageos.loscoins.db.data.DbRemoteStatusDataSource
import org.lineageos.loscoins.db.data.DbShopItemDataSource
import org.lineageos.loscoins.db.data.DbTransactionDataSource
import org.lineageos.loscoins.model.Collectible
import org.lineageos.loscoins.model.ShopItem
import org.lineageos.loscoins.model.Transaction
import org.lineageos.loscoins.util.ShopRemoteContent
import java.time.LocalDate
import java.time.LocalDateTime

class ShopActivity : AppCompatActivity() {
    private lateinit var transactionsDataSource: TransactionDataSource
    private lateinit var remoteStatusDataSource: RemoteStatusDataSource
    private lateinit var shopItemDataSource: ShopItemDataSource
    private lateinit var collectibleDataSource: CollectibleDataSource

    private lateinit var emptyListView: TextView
    private lateinit var shopList: RecyclerView

    private lateinit var shopAdapter: ShopAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transactionsDataSource = DbTransactionDataSource(this)
        remoteStatusDataSource = DbRemoteStatusDataSource(this)
        shopItemDataSource = DbShopItemDataSource(this)
        collectibleDataSource = DbCollectibleDataSource(this)

        setContentView(R.layout.activity_shop)

        emptyListView = findViewById(R.id.shop_empty)
        shopList = findViewById(R.id.shop_list)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        shopAdapter = ShopAdapter { buyItem(it) }
        shopList.apply {
            layoutManager = GridLayoutManager(this@ShopActivity, 2)
            itemAnimator = DefaultItemAnimator()
            adapter = shopAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        loadItems()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            MenuInflater(this).inflate(R.menu.shop, it)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.shop_refresh) {
             loadItems(true)
             true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("deprecation")
    private fun loadItems(forceRefresh: Boolean = false) {
        val storeUrl = getString(R.string.config_store_url)
        object : AsyncTask<Unit, Unit, List<ShopItem>>() {
            override fun doInBackground(vararg params: Unit?): List<ShopItem> {
                return ShopRemoteContent.getShopItems(
                    shopItemDataSource,
                    remoteStatusDataSource,
                    storeUrl,
                    forceRefresh,
                )
            }

            override fun onPostExecute(result: List<ShopItem>?) {
                result?.let {
                    shopAdapter.setData(it)
                    if (it.isEmpty()) {
                        emptyListView.visibility = View.VISIBLE
                        shopList.visibility = View.GONE
                    } else {
                        emptyListView.visibility = View.GONE
                        shopList.visibility = View.VISIBLE
                    }
                }
            }
        }.execute()
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("deprecation")
    private fun buyItem(item: ShopItem) {
        object : AsyncTask<ShopItem, Unit, ShopItem?>() {
            override fun doInBackground(vararg params: ShopItem): ShopItem? {
                if (params.isEmpty()) {
                    return null
                } else {
                    val toBuy = params[0]
                    val currentBalance = transactionsDataSource.currentBalance()
                    return if (currentBalance >= toBuy.price) {
                        collectibleDataSource.add(
                            Collectible(
                                name = toBuy.name,
                                type = toBuy.type,
                                obtainedDate = LocalDate.now(),
                                quantity = 1,
                            )
                        )
                        transactionsDataSource.add(
                            Transaction(
                                amount = toBuy.price * -1L,
                                time = LocalDateTime.now(),
                                type = Transaction.Type.PURCHASE
                            )
                        )
                        toBuy
                    } else {
                        null
                    }
                }
            }

            override fun onPostExecute(result: ShopItem?) {
                if (result == null) {
                    onPurchaseFailure()
                } else {
                    onPurchaseSuccess(result)
                }
            }
        }.execute(item)
    }

    private fun onPurchaseFailure() {
        AlertDialog.Builder(this)
            .setTitle(R.string.shop_purchase_failure_title)
            .setMessage(R.string.shop_purchase_failure_message)
            .setPositiveButton(R.string.yes) { d, _ -> d.dismiss() }
            .setNegativeButton(android.R.string.ok) { d, _ -> d.dismiss() }
            .setNeutralButton(R.string.yes) { d, _ -> d.dismiss() }
            .show()
    }

    private fun onPurchaseSuccess(item: ShopItem) {
        AlertDialog.Builder(this)
            .setTitle(R.string.shop_purchase_success_title)
            .setMessage(getString(R.string.shop_purchase_success_message, item.name))
            .setPositiveButton(android.R.string.ok) { d, _ -> d.dismiss() }
            .setNegativeButton(R.string.yes) { d, _ -> d.dismiss() }
            .show()
    }
}