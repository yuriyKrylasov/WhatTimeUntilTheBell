package com.whatTimeUntilTheBell;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChangeLessonDialog extends DialogFragment {
    interface OnApplyDialogListener {
        void onApply(Lesson lesson);
    }
    @NonNull OnApplyDialogListener onApply = lesson -> {};

    interface OnDeleteLessonListener {
        void onDeleteLesson();
    }
    @NonNull OnDeleteLessonListener onDeleteLesson = () -> {};

    private Lesson mLesson = new Lesson(new Time(7, 30), new Time(8, 15), "");

    private EditText mLessonName;
    private Button mDeleteLessonButton;

    private int mDeleteLessonButtonVisibility = View.VISIBLE;

    public void setLessonData(@NonNull Lesson lesson) {
        mLesson = new Lesson(new Time(lesson.getBegin().toString()), new Time(lesson.getEnd().toString()), lesson.getTitle());
    }

    public void setLessonTitle(String title) {
        mLesson.setTitle(title);
    }

    public void setDeleteLessonButtonVisibility(int visibility) {
        if (mDeleteLessonButton != null) {
            mDeleteLessonButton.setVisibility(visibility);
        }
        else {
            mDeleteLessonButtonVisibility = visibility;
        }
    }

    private void showTimePickerDialog(Button b, @NonNull Time time, int stringId) {
        new TimePickerDialog(getActivity(), (v, h, m) -> {
            time.setHours(h);
            time.setMinutes(m);
            b.setText(getResources().getString(stringId, time.toString()));
        }, time.getHours(), time.getMinutes(), true).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_change_lesson, null);

        mLessonName               = view.findViewById(R.id.lesson_name);
        mDeleteLessonButton       = view.findViewById(R.id.delete_lesson_button);
        Button mLessonBeginButton = view.findViewById(R.id.lesson_begin);
        Button mLessonEndButton   = view.findViewById(R.id.lesson_end);
        Button mCancelButton      = view.findViewById(R.id.cancel_button);
        Button mOkButton          = view.findViewById(R.id.ok_button);

        mLessonName.setText(mLesson.getTitle());
        mLessonBeginButton.setText(getResources().getString(R.string.begin, mLesson.getBegin().toString()));
        mLessonEndButton.setText(getResources().getString(R.string.end, mLesson.getEnd().toString()));
        mDeleteLessonButton.setVisibility(mDeleteLessonButtonVisibility);

        mLessonBeginButton.setOnClickListener(v -> showTimePickerDialog((Button) v, mLesson.getBegin(), R.string.begin));
        mLessonEndButton.setOnClickListener(v -> showTimePickerDialog((Button) v, mLesson.getEnd(), R.string.end));

        mDeleteLessonButton.setOnClickListener(v ->
            new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.delete, (d, w) -> {
                    onDeleteLesson.onDeleteLesson();
                    dismiss();
                })
                .setNegativeButton(android.R.string.cancel, (d, w) -> {})
                .setCancelable(true)
                .create().show()
        );
        mCancelButton.setOnClickListener(v -> dismiss());
        mOkButton.setOnClickListener(v -> {
            mLesson.setTitle(mLessonName.getText().toString());
            onApply.onApply(mLesson);
            dismiss();
        });

        return view;
    }
}
