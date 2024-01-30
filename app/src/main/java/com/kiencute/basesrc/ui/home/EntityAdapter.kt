package com.kiencute.basesrc.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiencute.basesrc.data.entities.Entity
import com.kiencute.basesrc.databinding.ItemEmpBinding
import com.kiencute.basesrc.extentions.load

class EntityAdapter(private val listener: EItemListener) : RecyclerView.Adapter<EViewHolder>() {

    interface EItemListener {
        fun onClickedItem(entity: Entity)
    }

    private val items = ArrayList<Entity>()

    fun setItems(items: ArrayList<Entity>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EViewHolder {
        val binding: ItemEmpBinding = ItemEmpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: EViewHolder, position: Int) = holder.bind(items[position])
}

class EViewHolder(private val itemBinding: ItemEmpBinding, private val listener: EntityAdapter.EItemListener) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var entity: Entity

    init {
        itemBinding.root.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: Entity) {
        this.entity = item
        itemBinding.name.text = item.firstName  + " "+ item.lastName
        itemBinding.subName.text = item.email
        // load image with glide
        itemBinding.imgView.load("https://robohash.org/6336ad010c0984744dd1960402d5fc6f?set=set4&bgset=&size=200x200")
    }

    override fun onClick(v: View?) {
        listener.onClickedItem(entity)
    }
}

