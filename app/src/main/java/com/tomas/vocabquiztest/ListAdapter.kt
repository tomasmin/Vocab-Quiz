package com.tomas.vocabquiztest

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(private val list: Map<String,String>)
    : RecyclerView.Adapter<WordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return WordViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val key = list.keys.toList()[position]
        val value = list.values.toList()[position]
        holder.bind(key, value)
    }

    override fun getItemCount(): Int = list.size

}

class WordViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)) {
    private var mWordView: TextView? = null
    private var mTranslationView: TextView? = null


    init {
        mWordView = itemView.findViewById(R.id.list_word)
        mTranslationView = itemView.findViewById(R.id.list_translation)
    }

    fun bind(key: String, value: String) {
        mWordView?.text = key
        mTranslationView?.text = value
    }

}