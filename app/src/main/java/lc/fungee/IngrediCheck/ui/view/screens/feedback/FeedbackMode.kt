package lc.fungee.IngrediCheck.ui.view.screens.feedback

sealed class FeedbackMode {
    data object FeedbackOnly : FeedbackMode()
    data object ImagesOnly : FeedbackMode()
    data object FeedbackAndImages : FeedbackMode()
}
