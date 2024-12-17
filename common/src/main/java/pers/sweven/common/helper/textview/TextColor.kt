package pers.sweven.common.helper.textview

import android.graphics.Color

data class TextColor(
    var colorString: String = "#",
    var colorInt: Int = 0,
) {
    constructor(colorInt: Int) : this("#", colorInt)

    fun toColor(): Int {
        if (colorInt != 0) {
            return colorInt
        }
        try {
            return Color.parseColor(colorString)
        } catch (e: Exception) {
            return 0
        }
    }
}