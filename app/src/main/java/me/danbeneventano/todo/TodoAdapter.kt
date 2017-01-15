package me.danbeneventano.todo

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import kotlinx.android.synthetic.main.item_todo.view.*
import android.app.Activity
import android.os.Handler
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator

class TodoAdapter(val context: Context, val list: MutableList<TodoItem>) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val context = parent?.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_todo, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = list[position]
        holder?.todoText?.text = item.text
        holder?.checkbox?.isChecked = false
    }

    fun delete (position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var todoText: TextView = itemView.todo_text
        var checkbox: CheckBox = itemView.checkbox

        init {
            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.interpolator = DecelerateInterpolator() //add this
            fadeIn.duration = 1000

            checkbox.setOnCheckedChangeListener { button, checked ->
                if (checked) {
                    val handler = Handler()
                    handler.postDelayed({
                        delete(adapterPosition)
                        if(list.isEmpty()) {
                            val noItems = (context as Activity).findViewById(R.id.no_items) as TextView
                            noItems.visibility = View.VISIBLE
                            noItems.animation = fadeIn
                            noItems.animate()
                        }
                    }, 300)
                }
            }
        }
    }

}