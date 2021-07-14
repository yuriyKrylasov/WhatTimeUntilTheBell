package com.whatTimeUntilTheBell;

import android.content.Context;

import androidx.annotation.NonNull;

public class DensityConverter {
    private DensityConverter() {
    }

    static float dpToPx(@NonNull Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
