package self.com.gituserfinder.presentation

import self.com.gituserfinder.domain.model.UserModel

interface MainContract {
    interface View {
        fun renderUsers(users: List<MainViewObject>)
        fun showError(message: String)
        fun showProgress()
        fun hideProgress()
        fun replaceLastItem(oldItem: MainViewObject, newItem: MainViewObject)
        fun showEmptyResult()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onSearchClick(input: String)
        fun onLoadMore()
    }

    sealed class MainViewObject(val identifier: Int) {
        data class Item(val user: UserModel) : MainViewObject(user.hashCode())
        object LoadMoreProgress : MainViewObject(-1)
        object LoadMoreButton : MainViewObject(-2)
    }
}