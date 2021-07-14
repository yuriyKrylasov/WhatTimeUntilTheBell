package com.whatTimeUntilTheBell;

import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {
    private IdGenerator() {
    }

    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {@link android.view.View#setId(int)}
     * <p>
     * This value will not collide with ID values generated at build time by aapt for R.id
     *
     * @return a generated ID value
     */
    public static int next() {
        while (true) {
            int result = NEXT_ID.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) {
                newValue = 1;
            }
            if (NEXT_ID.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

}
