package me.danbeneventano.todo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.logging.Logger
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import jp.wasabeef.recyclerview.animators.FadeInAnimator

class MainActivity : AppCompatActivity() {

    val list = mutableListOf(
            TodoItem("Example todo item")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        if(sharedPrefs.contains("list")) {
            list.clear()
            val type = object : TypeToken<List<TodoItem>>(){}.type
            val gson = Gson()
            val json = sharedPrefs.getString("list", "")
            list.addAll(gson.fromJson(json, type))
            Logger.getGlobal().info("loaded list")
        }
        val input = add_layout.input
        input.maxWidth = input.width
        val recycler = this.recycler
        recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        val adapter = TodoAdapter(this, list)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.itemAnimator = FadeInAnimator()

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator() //add this
        fadeIn.duration = 1000

        no_items.animation = fadeIn

        if(list.isEmpty()) {
            no_items.post { no_items.visibility = View.VISIBLE }
        }

        add_layout.submit.setOnClickListener {
            if(input.text.toString().isEmpty()) return@setOnClickListener
            list.add(TodoItem(input.text.toString()))
            adapter.notifyItemInserted(adapter.itemCount)
            input.text.clear()
            if(no_items.visibility == View.VISIBLE) {
                no_items.post {
                    no_items.visibility = View.GONE
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Logger.getGlobal().info("pause")
        val gson = Gson()
        val jsonString = gson.toJson(list)
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        val editor = sharedPrefs.edit()
        editor.putString("list", jsonString)
        editor.apply()
    }
}
