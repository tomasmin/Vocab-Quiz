package com.tomas.vocabquiztest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_vocab.*
import android.content.DialogInterface
import android.content.Intent
import android.widget.EditText
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.appcompat.app.AlertDialog


class MyVocabActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var docRef: DocumentReference

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable
    private var selectedSpinner = ""

    private val TAG = "poop"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_vocab)

        val playerId = intent.getStringExtra("playerId")

        colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)!!

        getDocumentsForSpinner(playerId)

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                viewHolder2: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                (viewAdapter as ListAdapter).removeItem(
                    viewHolder.adapterPosition,
                    viewHolder,
                    docRef
                )
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMarginVertical =
                    (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    colorDrawableBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.left + iconMarginVertical,
                        itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMarginVertical
                    )
                } else {
                    colorDrawableBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                        itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical,
                        itemView.bottom - iconMarginVertical
                    )
                    deleteIcon.level = 0
                }

                colorDrawableBackground.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )

                deleteIcon.draw(c)

                c.restore()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(list_recycler_view)

        addButton.setOnClickListener {
            if (editKey.text.isNotBlank() && editValue.text.isNotBlank() && selectedSpinner.isNotEmpty()) {
                (viewAdapter as ListAdapter).addItem(
                    docRef,
                    editKey.text.toString(),
                    editValue.text.toString(),
                    list_recycler_view
                )
                editValue.text.clear()
                editKey.text.clear()
                val inputManager: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(
                    currentFocus!!.windowToken,
                    InputMethodManager.SHOW_FORCED
                )
            } else {
                Toast.makeText(
                    this,
                    "Dictionary, word and translation values must not be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        newDictButton.setOnClickListener {
            showAddItemDialog(this, playerId)
        }

        deleteDictButton.setOnClickListener {
            if(selectedSpinner.isNotEmpty()) {
                showRemoveItemDialog(this, playerId)
            } else {
                Toast.makeText(
                    this,
                    "Nothing to delete",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        myPlayButton.setOnClickListener {
            if(selectedSpinner.isEmpty()) {
                Toast.makeText(
                    this,
                    "Create a dictionary first",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (viewAdapter.itemCount < 4) {
                Toast.makeText(
                    this,
                    "Not enough words. Create at least 4",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this, PlayActivity::class.java)
                intent.putExtra("playerId", playerId)
                intent.putExtra("document", selectedSpinner)
                startActivity(intent)
            }
        }

    }

    private fun populateSpinner(vocabNames: ArrayList<String>?, playerId: String) {

        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val arrayAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, vocabNames!!.toArray())
            spinner.adapter = arrayAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    docRef = db.collection(playerId).document(vocabNames[position])
                    populateList()
                    selectedSpinner = vocabNames[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    //showAddItemDialog(this@MyVocabActivity, playerId)
                }
            }
        }

    }

    private fun populateList() {
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val vocabMap = document.data as MutableMap<String, String>
                    viewAdapter = ListAdapter(vocabMap)
                    viewManager = LinearLayoutManager(this)
                    list_recycler_view.apply {
                        setHasFixedSize(true)
                        adapter = viewAdapter
                        layoutManager = viewManager
                        addItemDecoration(
                            DividerItemDecoration(
                                this.context,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun showAddItemDialog(c: Context, playerId: String) {
        val taskEditText = EditText(c)
        val dialog = AlertDialog.Builder(c)
            .setTitle("Create a new dictionary")
            .setMessage("Enter the name of the dictionary")
            .setView(taskEditText)
            .setPositiveButton(
                "Add"
            ) { _, _ ->
                if (taskEditText.text.isBlank()) {
                    Toast.makeText(
                        this,
                        "Dictionary name must not be blank",
                        Toast.LENGTH_SHORT
                    ).show()
                    showAddItemDialog(this, playerId)
                } else {
                    db.collection(playerId).document(taskEditText.text.toString()).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                Toast.makeText(
                                    this,
                                    "Dictionary with that name already exists",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                db.collection(playerId).document(taskEditText.text.toString())
                                    .set(mapOf("Swipe" to "to delete"))
                                Toast.makeText(
                                    this,
                                    "Dictionary created successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                getDocumentsForSpinner(playerId)
                            }
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun getDocumentsForSpinner(playerId: String) {
        db.collection(playerId)
            .get()
            .addOnSuccessListener { result ->
                val vocabNames = arrayListOf<String>()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    vocabNames!!.add(document.id)
                    //vocabMap = document.data as Map<String, String>
                    //list_recycler_view.adapter = ListAdapter(vocabMap)
                }
                if (vocabNames.isEmpty()) {
                    showAddItemDialog(this, playerId)
                }
                populateSpinner(vocabNames, playerId)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun showRemoveItemDialog(c: Context, playerId: String) {
        val dialog = AlertDialog.Builder(c)
            .setTitle("Delete dictionary")
            .setMessage("are you sure you want to delete $selectedSpinner?")
            .setPositiveButton(
                "Delete"
            ) { _, _ ->
                db.collection(playerId).document(selectedSpinner)
                    .delete()
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!")
                        Toast.makeText(
                            this,
                            "Dictionary deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        selectedSpinner = ""
                        getDocumentsForSpinner(playerId)
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e)
                        Toast.makeText(
                            this,
                            "Error deleting document",
                            Toast.LENGTH_SHORT
                        ).show()}
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

}
