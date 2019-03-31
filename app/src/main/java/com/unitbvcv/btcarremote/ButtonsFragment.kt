package com.unitbvcv.btcarremote

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.buttons_fragment.*


class ButtonsFragment : Fragment() {

    companion object {
        fun newInstance() = ButtonsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.buttons_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity != null) {
            val bluetoothViewModel = ViewModelProviders.of(activity!!).get(BluetoothViewModel::class.java)

            toggleLedButton?.setOnCheckedChangeListener { buttonView, isChecked ->
                    bluetoothViewModel.ledToggle.value = isChecked
            }
//            bluetoothViewModel.ledToggle.observe(this, Observer { isChecked: Boolean? ->
//                if (isChecked != null)
//                    toggleLedButton?.isChecked = isChecked
//            })

            toggleMusicButton?.setOnCheckedChangeListener { buttonView, isChecked ->
                    bluetoothViewModel.musicToggle.value = isChecked
            }
//            bluetoothViewModel.musicToggle.observe(this, Observer { isChecked: Boolean? ->
//                if (isChecked != null)
//                    toggleMusicButton?.isChecked = isChecked
//            })


            buttonSendToDisplay?.setOnClickListener { buttonView ->
                val textToSend = editTextDisplay?.text?.toString() ?: ""
                bluetoothViewModel.displayText.value = textToSend
            }
//            bluetoothViewModel.displayText.observe(this, Observer { text: String? ->
//                if (text != null)
//                    editTextDisplay?.setText(text)
//            })

        }
    }

}
