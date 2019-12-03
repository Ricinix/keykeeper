package com.example.keykeeper.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.keykeeper.R
import com.example.keykeeper.view.adapter.GeneralSettingRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_general_setting.*

class GeneralSettingFragment : Fragment() {
    private lateinit var myContext:Context
    private lateinit var adapter: GeneralSettingRecyclerAdapter
    val fragChangeId = MutableLiveData<Int>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = GeneralSettingRecyclerAdapter(myContext)
        setObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_general_setting, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // recyclerView的设置
        recycler_frag_general_setting.let {
            it.layoutManager = LinearLayoutManager(myContext)
            it.adapter = adapter
        }
//        adapter.notifyDataSetChanged()
    }

    private fun setObserver(){
        adapter.buttonPressId.observe(this, Observer {
            fragChangeId.value = it
        })
    }
}
