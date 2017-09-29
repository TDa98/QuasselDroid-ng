package de.kuschku.quasseldroid_ng.ui.setup

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.helper.observeSticky
import de.kuschku.quasseldroid_ng.util.helper.or
import de.kuschku.quasseldroid_ng.util.helper.switchMap

abstract class SetupActivity : AppCompatActivity() {
  @BindView(R.id.view_pager)
  lateinit var viewPager: ViewPager

  @BindView(R.id.next_button)
  lateinit var button: FloatingActionButton

  private lateinit var adapter: SlidePagerAdapter

  protected abstract val fragments: List<SlideFragment>

  private val currentPage = MutableLiveData<SlideFragment?>()
  private val isValid = currentPage.switchMap(SlideFragment::valid).or(false)

  private val pageChangeListener = object : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {
      when (state) {
        ViewPager.SCROLL_STATE_SETTLING -> pageChanged()
      }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float,
                                positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) = Unit
  }

  private fun pageChanged() {
    currentPage.value = adapter.getItem(viewPager.currentItem)
    val drawable = if (viewPager.currentItem == adapter.totalCount - 1)
      R.drawable.ic_check
    else
      R.drawable.ic_arrow_right
    button.setImageResource(drawable)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_SetupTheme)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_setup)
    ButterKnife.bind(this)

    adapter = SlidePagerAdapter(supportFragmentManager)
    fragments.forEach(adapter::addFragment)
    viewPager.adapter = adapter

    button.setOnClickListener {
      if (viewPager.currentItem == adapter.totalCount - 1)
        onDoneInternal()
      else
        viewPager.setCurrentItem(viewPager.currentItem + 1, true)
    }
    isValid.observeSticky(this, Observer {
      if (it == true) {
        button.show()
        adapter.lastValidItem = viewPager.currentItem
      } else {
        button.hide()
        adapter.lastValidItem = viewPager.currentItem - 1
      }
    })
    viewPager.addOnPageChangeListener(pageChangeListener)
    pageChanged()
  }

  private fun onDoneInternal() {
    onDone(adapter.result)
  }

  fun setInitData(data: Bundle?) {
    adapter.result.putAll(data)
  }

  abstract fun onDone(data: Bundle)

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putInt(currentItemKey, viewPager.currentItem)
    outState.putInt(lastValidItemKey, adapter.lastValidItem)
    outState.putBundle(resultKey, adapter.result)
    super.onSaveInstanceState(outState)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey(resultKey))
        adapter.result.putAll(savedInstanceState.getBundle(resultKey))
      if (savedInstanceState.containsKey(lastValidItemKey))
        adapter.lastValidItem = savedInstanceState.getInt(lastValidItemKey)
      if (savedInstanceState.containsKey(currentItemKey))
        viewPager.currentItem = savedInstanceState.getInt(currentItemKey)
      currentPage.value = adapter.getItem(viewPager.currentItem)
    }
    pageChanged()
  }

  private class SlidePagerAdapter(private val fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager) {
    private val retainedFragments = SparseArray<SlideFragment>()

    val result = Bundle()
      get() {
        (0 until retainedFragments.size()).map(retainedFragments::valueAt).forEach {
          it.getData(field)
        }
        return field
      }

    var lastValidItem = -1
      set(value) {
        field = value
        notifyDataSetChanged()
      }
    private val list = mutableListOf<SlideFragment>()

    override fun getItem(position: Int): SlideFragment {
      return retainedFragments.get(position) ?: list[position]
    }

    override fun getCount() = Math.min(list.size, lastValidItem + 2)
    val totalCount get() = list.size
    fun addFragment(fragment: SlideFragment) {
      list.add(fragment)
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
      val fragment = super.instantiateItem(container, position)
      storeNewFragment(position, fragment as SlideFragment)
      return fragment
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
      retainedFragments.get(position)?.getData(result)
      retainedFragments.remove(position)
      super.destroyItem(container, position, `object`)
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
      super.restoreState(state, loader)
      if (state != null) {
        val bundle = state as Bundle
        val keys = bundle.keySet()
        for (key in keys) {
          if (key.startsWith("f")) {
            val index = Integer.parseInt(key.substring(1))
            val f = fragmentManager.getFragment(bundle, key)
            if (f != null && f is SlideFragment) {
              storeNewFragment(index, f)
            }
          }
        }
      }
    }

    private fun storeNewFragment(index: Int, fragment: SlideFragment) {
      fragment.initData = result
      retainedFragments.put(index, fragment)
    }
  }

  override fun onBackPressed() {
    if (viewPager.currentItem == 0)
      super.onBackPressed()
    else
      viewPager.currentItem -= 1
  }

  companion object {
    private const val currentItemKey = ":setupActivity:currentItem"
    private const val lastValidItemKey = ":setupActivity:lastValidItem"
    private const val resultKey = ":setupActivity:result"
  }
}