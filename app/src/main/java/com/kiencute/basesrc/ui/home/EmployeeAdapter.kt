package com.kiencute.basesrc.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiencute.basesrc.data.entities.Beer
import com.kiencute.basesrc.databinding.ItemEmpBinding

class EmployeeAdapter(private val listener: EItemListener) : RecyclerView.Adapter<EViewHolder>() {

    interface EItemListener {
        fun onClickedCharacter(characterId: Int)
    }

    private val items = ArrayList<Beer>()

    fun setItems(items: ArrayList<Beer>) {
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

class EViewHolder(private val itemBinding: ItemEmpBinding, private val listener: EmployeeAdapter.EItemListener) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var beer: Beer

    init {
        itemBinding.root.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: Beer) {
        this.beer = item
        itemBinding.name.text = item.name
        itemBinding.subName.text = item.tagline
//        Glide.with(itemBinding.root)
//            .load(item.image)
//            .transform(CircleCrop())
//            .into(itemBinding.image)
    }

    override fun onClick(v: View?) {
        listener.onClickedCharacter(beer.id)
    }
}

