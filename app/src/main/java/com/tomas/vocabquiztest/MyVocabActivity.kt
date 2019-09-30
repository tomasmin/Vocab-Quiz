package com.tomas.vocabquiztest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_vocab.*


class MyVocabActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var vocabMap = Dictionary().vocabMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_vocab)

        val playerId = intent.getStringExtra("playerId")

        list_recycler_view.layoutManager = LinearLayoutManager(this)

        db.collection(playerId)
            .get()
            .addOnSuccessListener { result ->
                val vocabNames = arrayListOf<String>()
                for (document in result) {
                    Log.d("poop", "${document.id} => ${document.data}")
                    vocabNames!!.add(document.id)
                    //vocabMap = document.data as Map<String, String>
                    //list_recycler_view.adapter = ListAdapter(vocabMap)
                }
                populateSpinner(vocabNames, playerId)
            }
            .addOnFailureListener { exception ->
                Log.d("poop", "Error getting documents: ", exception)
            }
    }

    private fun populateSpinner(vocabNames: ArrayList<String>?, playerId: String) {

        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vocabNames!!.toArray())
            spinner.adapter = arrayAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val docRef = db.collection(playerId).document(vocabNames[position])
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Log.d("poop", "DocumentSnapshot data: ${document.data}")
                                vocabMap = document.data as Map<String, String>
                                list_recycler_view.adapter = ListAdapter(vocabMap)
                            } else {
                                Log.d("poop", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("poop", "get failed with ", exception)
                        }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }
        }

    }
}
