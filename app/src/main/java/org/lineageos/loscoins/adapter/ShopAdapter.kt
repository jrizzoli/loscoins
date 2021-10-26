package org.lineageos.loscoins.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.lineageos.loscoins.R
import org.lineageos.loscoins.model.ShopItem

class ShopAdapter(
    private var data: List<ShopItem> = emptyList(),
    private val onBuy: (ShopItem) -> Unit,
) : RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_shop, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.setData(item) { onBuy(item) }
    }

    fun setData(data: List<ShopItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun setData(item: ShopItem, purchaseItem: () -> Unit) {
            val icon = itemView.findViewById<ImageView>(android.R.id.icon)
            val title = itemView.findViewById<TextView>(android.R.id.title)
            val buyButton = itemView.findViewById<Button>(android.R.id.primary)

            icon.setImageResource(
                when (item.type) {
                    ShopItem.Type.BUG -> R.drawable.ic_shop_item_bug
                    ShopItem.Type.DEVICE -> R.drawable.ic_shop_item_device
                    ShopItem.Type.ETA -> R.drawable.ic_shop_item_eta
                    ShopItem.Type.FEATURE -> R.drawable.ic_shop_item_feature
                    ShopItem.Type.KEY -> R.drawable.ic_shop_item_key
                    ShopItem.Type.MONEY -> R.drawable.ic_shop_item_money
                    ShopItem.Type.PERMISSION -> R.drawable.ic_shop_item_permission
                    ShopItem.Type.PROMO -> R.drawable.ic_shop_item_promo
                    ShopItem.Type.ROM -> R.drawable.ic_shop_item_rom
                    ShopItem.Type.UNDEFINED -> R.drawable.ic_shop_item_undefined
                    ShopItem.Type.YACHT -> R.drawable.ic_shop_item_yacht
                }
            )
            title.text = item.name
            buyButton.apply {
                text = itemView.resources.getString(R.string.shop_buy, item.price)
                setOnClickListener { purchaseItem() }
            }
        }
    }
}