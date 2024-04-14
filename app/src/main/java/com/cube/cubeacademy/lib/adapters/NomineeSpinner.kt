package com.cube.cubeacademy.lib.adapters

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.cube.cubeacademy.R
import com.cube.cubeacademy.activities.CreateNominationActivity
import com.cube.cubeacademy.lib.models.Nominee

class NomineeSpinner @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr), View.OnClickListener {

    private var items: List<Nominee> = emptyList()
    private var popupWindow: PopupWindow? = null
    private var adapter: ArrayAdapter<Nominee>? = null
    var selectedNominee: Nominee? = null
        private set

    private var onNomineeSelectedListener: CreateNominationActivity.NomineeSelectedListener? = null

    fun setNomineeSelectedListener(listener: CreateNominationActivity.NomineeSelectedListener) {
        this.onNomineeSelectedListener = listener
    }


    init {
        isClickable = true
        setOnClickListener(this)
        updateDisplayText()
        setPadding(48, 0, 48, 16) // Adjust left padding as needed
      //  setPadding(12, 12, 12, 12)
        typeface = ResourcesCompat.getFont(context, R.font.anonymous_pro_regular)
        gravity = Gravity.CENTER_VERTICAL
        minHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics).toInt()
        setTextColor(Color.BLACK)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.spinner_icon, 0)
    }

    fun setItems(newItems: List<Nominee>) {
        this.items = newItems
        adapter = ArrayAdapter(context, R.layout.spinner_dropdown_item, items)
        updateDisplayText() // Update display text when items are set
    }

    override fun onClick(v: View?) {
        showDropDown()
    }

    private fun showDropDown() {
        val listView = ListView(context).apply {
            adapter = this@NomineeSpinner.adapter
            divider = null
            setOnItemClickListener { adapter, view, position, _ ->
                selectedNominee = getItemAtPosition(position) as Nominee
                onNomineeSelectedListener!!.onItemSelected(adapter, view, position, 0)
                updateDisplayText()
                popupWindow?.dismiss()
            }
        }

        popupWindow = PopupWindow(context).apply {
            contentView = listView
            // Calculate the desired width by subtracting the margin from the screen width
            val metrics = context.resources.displayMetrics
            val screenWidth = metrics.widthPixels
            val horizontalMargin = (metrics.density * 20).toInt() // A margin of 20dp to each side
            width = screenWidth - 2 * horizontalMargin
            height = WindowManager.LayoutParams.WRAP_CONTENT
            isFocusable = true
            elevation = 20f // Shadow
            setBackgroundDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.dropdown_background
                )
            )
            showAsDropDown(this@NomineeSpinner, 0, 0)
        }
    }

    private fun updateDisplayText() {
        text = selectedNominee?.toString() ?: items.firstOrNull()?.toString()
    }
}