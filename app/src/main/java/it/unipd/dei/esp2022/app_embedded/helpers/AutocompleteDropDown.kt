package it.unipd.dei.esp2022.app_embedded.helpers

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatAutoCompleteTextView


class AutocompleteDropDown : AppCompatAutoCompleteTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun showDropDown() {
        val visibleDisplayFrame = Rect()
        getWindowVisibleDisplayFrame(visibleDisplayFrame)

        dropDownHeight = ViewGroup.LayoutParams.MATCH_PARENT
        dropDownVerticalOffset = visibleDisplayFrame.height()

        super.showDropDown()
    }
}