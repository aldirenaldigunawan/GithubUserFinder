package self.com.gituserfinder.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_load_button.view.*
import kotlinx.android.synthetic.main.item_user.view.*
import self.com.gituserfinder.R
import self.com.gituserfinder.domain.model.UserModel

class ItemAdapter(private val loadMoreClick: () -> Unit) :
    ListAdapter<MainContract.MainViewObject, RecyclerView.ViewHolder>(ItemDifUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_user -> UserViewHolder(view)
            R.layout.item_progress -> EmptyViewHolder(view)
            R.layout.item_load_button -> ButtonLoadMoreViewHolder(view, loadMoreClick)
            else -> throw  java.lang.IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> {
                val item = getItem(position) as MainContract.MainViewObject.Item
                holder.bindView(item.user)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MainContract.MainViewObject.Item -> R.layout.item_user
            is MainContract.MainViewObject.LoadMoreProgress -> R.layout.item_progress
            is MainContract.MainViewObject.LoadMoreButton -> R.layout.item_load_button
            else -> throw IllegalStateException()
        }
    }

    fun replaceLastItem(oldItem: MainContract.MainViewObject, newItem: MainContract.MainViewObject) {
        val currentItems = if (currentList.last() == oldItem) currentList.dropLast(1) else currentList
        submitList(currentItems + listOf(newItem))
    }
}

class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindView(model: UserModel) {
        itemView.userName.text = model.username
        Glide.with(itemView.avatar).load(model.avatar).into(itemView.avatar)
    }
}

class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)
class ButtonLoadMoreViewHolder(view: View, private val clickListener: () -> Unit) : RecyclerView.ViewHolder(view) {

    init {
        itemView.loadMoreButton.setOnClickListener { clickListener() }
    }
}

class ItemDifUtil : DiffUtil.ItemCallback<MainContract.MainViewObject>() {
    override fun areItemsTheSame(oldItem: MainContract.MainViewObject, newItem: MainContract.MainViewObject): Boolean {
        return oldItem.identifier == newItem.identifier
    }

    override fun areContentsTheSame(
        oldItem: MainContract.MainViewObject,
        newItem: MainContract.MainViewObject
    ): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

}