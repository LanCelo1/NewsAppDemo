package uz.gita.newsappdemo.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.databinding.ItemLayoutBinding

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.VH>() {

    var onItemViewClickListener : ((Article) -> Unit)? = null

    fun onItemClickListener(block : ((Article) -> Unit)){
        onItemViewClickListener = block
    }

    inner class VH(var binding : ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val item = asyncList.currentList[adapterPosition]
            binding.apply {
                titleNew.isSelected = true
                titleNew.ellipsize = TextUtils.TruncateAt.MARQUEE
                titleNew.text = item.title
                Glide.with(root).load(item.urlToImage).into(imageNew)
            }
        }

        init {
            itemView.setOnClickListener {
                onItemViewClickListener?.invoke(asyncList.currentList[adapterPosition])
            }
        }
    }

    var asyncList = AsyncListDiffer(this, diffUtil)

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.source?.id == newItem.source?.id
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        return holder.bind()
    }

    override fun getItemCount(): Int {
        return asyncList.currentList.size
    }
}