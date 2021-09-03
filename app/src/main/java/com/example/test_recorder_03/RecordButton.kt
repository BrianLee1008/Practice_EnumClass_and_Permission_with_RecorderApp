package com.example.test_recorder_03

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.example.test_recorder_03.State.*

class RecordButton(context: Context, attr: AttributeSet) : AppCompatImageButton(context, attr) {

	init {
		setImageResource(R.drawable.recordbutton)
	}

	fun updateIconWithState(state: State) {
		when (state) {
			BEFORE_RECORDING -> setImageResource(R.drawable.ic_recording)
			ON_RECORDING -> setImageResource(R.drawable.ic_stop)
			AFTER_RECORDING -> setImageResource(R.drawable.ic_play)
			ON_PLAYING -> setImageResource(R.drawable.ic_stop)
		}
	}
}