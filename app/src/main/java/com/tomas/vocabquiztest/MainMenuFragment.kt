package com.tomas.vocabquiztest

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainMenuFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMenuFragment : Fragment(), View.OnClickListener {
    override fun onClick(view: View) {
        when (view.id) {
            R.id.sign_in_button -> mListener!!.onSignInButtonClicked()
            R.id.sign_out_button -> mListener!!.onSignOutButtonClicked()
            R.id.show_leaderboards_button -> mListener!!.onShowLeaderboardsRequested()
            R.id.play_button -> {
                val intent = Intent(activity, PlayActivity::class.java)
                startActivity(intent) }
            R.id.my_vocab_button -> mListener!!.onMyVocabButtonClicked()
        }
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var mSignInBarView: View? = null
    private var mSignOutBarView: View? = null
    private var mShowLeaderboardsButton: View? = null
    private var mMyVocabButton: View? = null

    private var mListener: Listener? = null
    private var mShowSignInButton = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_main_menu, container, false)

        val clickableIds = intArrayOf(
            R.id.sign_in_button,
            R.id.sign_out_button,
            R.id.play_button,
            R.id.show_leaderboards_button,
            R.id.my_vocab_button
        )

        for (clickableId in clickableIds) {
            view!!.findViewById<View>(clickableId).setOnClickListener(this)
        }

        mSignInBarView = view.findViewById(R.id.sign_in_bar)
        mSignOutBarView = view.findViewById(R.id.sign_out_bar)
        mShowLeaderboardsButton = view.findViewById(R.id.show_leaderboards_button)
        mMyVocabButton = view.findViewById(R.id.my_vocab_button)

        updateUI()

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun setListener(listener: Listener) {
        mListener = listener
    }


    private fun updateUI() {
        mShowLeaderboardsButton!!.isVisible = !mShowSignInButton
        mMyVocabButton!!.isVisible = !mShowSignInButton
        mSignInBarView!!.visibility = if (mShowSignInButton) View.VISIBLE else View.GONE
        mSignOutBarView!!.visibility = if (mShowSignInButton) View.GONE else View.VISIBLE
    }

    fun setShowSignInButton(showSignInButton: Boolean) {
        mShowSignInButton = showSignInButton
        updateUI()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    interface Listener {
        // called when the user presses the `Sign In` button
        fun onSignInButtonClicked()

        // called when the user presses the `Sign Out` button
        fun onSignOutButtonClicked()

        fun onShowLeaderboardsRequested()

        fun onMyVocabButtonClicked()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
