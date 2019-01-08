package com.example.kiril.softwaredesign

import android.os.Build
import android.text.Html
import android.text.Html.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prof.rssparser.Article
import android.text.Spanned
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy


class ArticleAdapter(private val articleList: ArrayList<Article>, private val listener: (String) -> Unit) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title?.text = articleList[position].title
        holder.description?.text = fromHtml(articleList[position].description)
        holder.date?.text = articleList[position].pubDate.toString()
        if (holder.image != null) {
            GlideApp.with(holder.image.context)
                    .load(articleList[position].image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rss_card, parent, false)
        return ViewHolder(v).listen { pos, _ ->
            val item = articleList[pos]
            listener(item.link)
        }
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView? = itemView.findViewById(R.id.cardTitle)
        val date: TextView? = itemView.findViewById(R.id.cardDate)
        val description: TextView? = itemView.findViewById(R.id.cardDescription)
        val image: ImageView? = itemView.findViewById(R.id.cardImage)
    }

    @Suppress("DEPRECATION")
    private fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fromHtml(html, FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }
}