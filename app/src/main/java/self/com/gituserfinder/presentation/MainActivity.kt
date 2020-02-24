package self.com.gituserfinder.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import self.com.gituserfinder.R
import self.com.gituserfinder.presentation.MainContract.MainViewObject
import self.com.gituserfinder.presentation.di.Dependencies.generatePresenter

class MainActivity : AppCompatActivity(), MainContract.View {

    private val presenter by lazy { generatePresenter() }
    private val adapter by lazy { ItemAdapter(presenter::onLoadMore) }
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSearch.setOnClickListener { presenter.onSearchClick(input.text.toString()) }
        setupList()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun renderUsers(users: List<MainViewObject>) {
        recyclerView.visibility = View.VISIBLE
        message.visibility = View.GONE
        adapter.submitList(users)
    }

    override fun replaceLastItem(oldItem: MainViewObject, newItem: MainViewObject) {
        adapter.replaceLastItem(oldItem, newItem)
    }

    override fun showError(message: String) {
        this@MainActivity.message.text = getString(R.string.message_error)
        recyclerView.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
    }

    override fun showEmptyResult() {
        recyclerView.visibility = View.GONE
        message.text = getString(R.string.message_no_result)
    }

    private fun setupList() {
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        disposable = recyclerView.scrollEvents()
            .map { ItemScrollEventData(layoutManager.findLastVisibleItemPosition(), layoutManager.itemCount) }
            .distinctUntilChanged()
            .filter { it.isLastItemDisplayed() && it.itemCount > 20 }
            .subscribe({ presenter.onLoadMore() }, { Log.e(TAG, "Error on scrolling events of recyclerview") })

        presenter.attachView(this)
    }

    private data class ItemScrollEventData(val lastVisiblePosition: Int, val itemCount: Int) {
        fun isLastItemDisplayed() = lastVisiblePosition >= itemCount - 1
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}