package com.mayank.hiddencamerapoc

import android.hardware.Camera
import java.util.*

internal class PictureSizeComparator : Comparator<Camera.Size> {
    // Used for sorting in ascending order of
    // roll name
    override fun compare(a: Camera.Size, b: Camera.Size): Int {
        return b.height * b.width - a.height * a.width
    }
}