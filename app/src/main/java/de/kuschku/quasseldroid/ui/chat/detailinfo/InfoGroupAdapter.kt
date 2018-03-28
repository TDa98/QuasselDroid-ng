package de.kuschku.quasseldroid.ui.chat.detailinfo

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.viewmodel.data.InfoGroup

class InfoGroupAdapter :
  ListAdapter<InfoGroup, InfoGroupAdapter.InfoGroupViewHolder>(
    object : DiffUtil.ItemCallback<InfoGroup>() {
      override fun areItemsTheSame(oldItem: InfoGroup, newItem: InfoGroup) =
        oldItem.name == newItem.name

      override fun areContentsTheSame(oldItem: InfoGroup, newItem: InfoGroup) =
        oldItem == newItem
    }
  ) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InfoGroupViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.widget_userinfo_group, parent, false)
  )

  override fun onBindViewHolder(holder: InfoGroupViewHolder, position: Int) =
    holder.bind(getItem(position))

  class InfoGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.title_container)
    lateinit var titleContainer: View

    @BindView(R.id.title)
    lateinit var title: TextView

    @BindView(R.id.properties)
    lateinit var properties: RecyclerView

    private val adapter: InfoPropertyAdapter

    init {
      ButterKnife.bind(this, itemView)

      adapter = InfoPropertyAdapter()

      properties.layoutManager = LinearLayoutManager(itemView.context)
      properties.adapter = adapter
    }

    fun bind(item: InfoGroup) {
      title.text = item.name
      titleContainer.visibleIf(item.name != null)

      adapter.submitList(item.properties)
    }
  }
}