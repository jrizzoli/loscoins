package org.lineageos.loscoins.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.lineageos.loscoins.R
import org.lineageos.loscoins.model.TrackedCommit
import java.time.format.DateTimeFormatter

class CommitsAdapter(
    private var data: List<TrackedCommit> = emptyList()
) : RecyclerView.Adapter<CommitsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(data[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_commit, parent, false)
        )
    }

    fun add(trackedCommit: TrackedCommit) {
        setData((this.data + listOf(trackedCommit))
            .sortedBy { a -> a.commit.creationDate.toEpochDay() })
    }

    fun setData(data: List<TrackedCommit>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun setData(trackedCommit: TrackedCommit) {
            val title = itemView.findViewById<TextView>(android.R.id.title)
            val summary = itemView.findViewById<TextView>(android.R.id.summary)

            title.text = trackedCommit.commit.subject
            summary.text = itemView.resources.getString(
                R.string.commit_summary_open,
                DateTimeFormatter.ISO_LOCAL_DATE.format(trackedCommit.commit.creationDate),
                trackedCommit.value
            )
        }
    }
}