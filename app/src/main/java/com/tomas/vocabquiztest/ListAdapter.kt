package com.tomas.vocabquiztest

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue


class ListAdapter(private val list: MutableMap<String,String>)
    : RecyclerView.Adapter<WordViewHolder>() {

    private var removedPosition: Int = 0
    private var removedKey: String = ""
    private var removedValue: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return WordViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val key = list.keys.toList()[position]
        val value = list.values.toList()[position]
        holder.bind(key, value)
    }

    fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder, docRef: DocumentReference) {
        removedKey = list.keys.toList()[position]
        removedValue = list[removedKey]!!
        removedPosition = position

        list.remove(removedKey)
        val updates = hashMapOf<String, Any>(
            removedKey to FieldValue.delete()
        )

        docRef.update(updates)

        //notifyItemRemoved(position)
        notifyDataSetChanged()
        Snackbar.make(viewHolder.itemView, "$removedKey removed", Snackbar.LENGTH_LONG).setAction("UNDO") {
            list[removedKey] = removedValue
            //notifyItemInserted(removedPosition)
            notifyDataSetChanged()
            docRef.update(removedKey, removedValue)
        }.show()
    }

    fun addItem (docRef: DocumentReference, key: String, value: String, recView: RecyclerView) {
        var newValue = value
        var oldValue = ""
        var deleteOnUndo = true
        if(list[key] != null && list[key] != value){
            oldValue = list[key]!!
            newValue = list[key] + "; " + value
            deleteOnUndo = false
        }
        docRef.update(key, newValue)
        list[key] = newValue

        notifyDataSetChanged()

        Snackbar.make(recView, "New translation to $key added", Snackbar.LENGTH_LONG).setAction("UNDO") {
            if(deleteOnUndo){
                list.remove(key)
                //notifyItemRemoved(position)
                notifyDataSetChanged()
                val updates = hashMapOf<String, Any>(
                    key to FieldValue.delete()
                )
                docRef.update(updates)
            } else {
                list[key] = oldValue
                //notifyItemChanged(position)
                notifyDataSetChanged()
                docRef.update(key, oldValue)
            }
        }.show()
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