package org.lineageos.loscoins.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.lineageos.loscoins.R
import org.lineageos.loscoins.model.Collectible
import org.lineageos.loscoins.model.ShopItem
import java.time.format.DateTimeFormatter

class CollectiblesAdapter(
    private var data: List<Collectible> = emptyList()
) : RecyclerView.Adapter<CollectiblesAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_collectible, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(data[position])
    }

    fun setData(data: List<Collectible>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun setData(collectible: Collectible) {
            val iconView = itemView.findViewById<ImageView>(android.R.id.icon)
            val titleView = itemView.findViewById<TextView>(android.R.id.title)
            val summaryView = itemView.findViewById<TextView>(android.R.id.summary)

            iconView.setImageResource(
                when (collectible.type) {
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
            titleView.text = collectible.name
            summaryView.text = summaryView.resources.getString(
                R.string.collectibles_summary,
                DateTimeFormatter.ISO_DATE.format(collectible.obtainedDate),
                collectible.quantity,
            )
        }
    }
}