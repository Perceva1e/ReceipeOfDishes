package com.example.recipeofthedishes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ItemsAdapter(private var items: MutableList<Item>, private val context: Context) : RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.itemListImage)
        val title: TextView = view.findViewById(R.id.itemListTitle)
        val descr: TextView = view.findViewById(R.id.itemListDescrOne)
        val button: Button = view.findViewById(R.id.itemListButton)
        val deleteButton: Button = view.findViewById(R.id.itemListButtonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.descr.text = item.desc
        Glide.with(context)
            .load(item.image)
            .into(holder.image)

        holder.button.setOnClickListener {
            val intent = Intent(context, ItemActivity::class.java)
            intent.putExtra("itemTitle", item.title)
            intent.putExtra("itemIngredient", item.ingredient)
            intent.putExtra("itemInstruction",item.instructions)
            context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            val dbHelper = DbHelper(context, null)
            dbHelper.deleteItem(item.id)
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
