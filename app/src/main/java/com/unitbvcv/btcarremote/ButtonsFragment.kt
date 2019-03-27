package com.unitbvcv.btcarremote

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class ButtonsFragment : Fragment() {

    companion object {
        fun newInstance() = ButtonsFragment()
    }

    private lateinit var viewModel: ButtonsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.buttons_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ButtonsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
