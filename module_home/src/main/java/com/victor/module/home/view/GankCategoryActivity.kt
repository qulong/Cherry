package com.victor.module.home.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.victor.cherry.viewmodel.GankCategoryLiveDataVMFactory
import com.victor.cherry.viewmodel.GankCategoryViewModel
import com.victor.clips.util.AppUtil.Companion.startActivityForResult
import com.victor.lib.common.base.ARouterPath
import com.victor.lib.common.base.BaseActivity
import com.victor.lib.common.util.Constant
import com.victor.lib.common.util.ImageUtils
import com.victor.lib.common.util.JsonUtils
import com.victor.lib.common.util.SharePreferencesUtil
import com.victor.module.home.R
import com.victor.module.home.databinding.ActivityGankCategoryBinding
import com.victor.module.home.view.adapter.GankCategoryAdapter
import kotlinx.android.synthetic.main.activity_gank_category.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GankCategoryActivity
 * Author: Victor
 * Date: 2020/8/1 下午 05:18
 * Description: 
 * -----------------------------------------------------------------
 */
@Route(path = ARouterPath.GankCategoryAct)
class GankCategoryActivity: BaseActivity(),AdapterView.OnItemClickListener,View.OnClickListener {
    private val viewmodel: GankCategoryViewModel by viewModels { GankCategoryLiveDataVMFactory }
    var gankCategoryAdapter: GankCategoryAdapter? = null

    companion object {
        const val SELECT_CATEGORY_REQUEST_CODE = 6
        const val SELECT_CATEGORY_KEY = "SELECT_CATEGORY_KEY"

        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, GankCategoryActivity::class.java)
            activity.startActivity(intent)
        }

        fun  intentStart (activity: AppCompatActivity, sharedElement: View,
                          sharedElementName: String) {
            var intent = Intent(activity, GankCategoryActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,sharedElement, sharedElementName)

            ActivityCompat.startActivity(activity!!, intent,options.toBundle())
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_gank_category
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
        initData()
    }

    fun initialize () {
        val binding = viewDataBinding as ActivityGankCategoryBinding

        // Set the LifecycleOwner to be able to observe LiveData objects
        binding?.lifecycleOwner = this

        // Bind ViewModel
        binding?.viewmodel = viewmodel

        gankCategoryAdapter = GankCategoryAdapter(this,this)
        mRvGankCategory.setHasFixedSize(true)
        mRvGankCategory.adapter = gankCategoryAdapter

        mFabClose.setOnClickListener(this)

    }

    fun initData () {
        viewmodel.girlData.observe(this, Observer {
            it.let {
                ImageUtils.instance.loadImage(this,mIvGirl,it.data?.get(0)?.images?.get(0))
            }
        })
        viewmodel.gankData.observe(this, Observer {
            it.let {
                gankCategoryAdapter?.clear()
                gankCategoryAdapter?.add(it.data)
                gankCategoryAdapter?.notifyDataSetChanged()
            }
        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var categoryRes = JsonUtils.toJSONString(gankCategoryAdapter?.getItem(position)!!)!!
        SharePreferencesUtil.putString(this,Constant.CATEGORY_TYPE_KEY,categoryRes)
        onBackPressed()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.mFabClose -> {
                onBackPressed()
            }
        }
    }

}