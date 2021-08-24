package com.whatTimeUntilTheBell;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class ChangeLessonDialog extends DialogFragment {
    interface OnApplyDialogListener {
        void onApply(Lesson lesson);
    }
    OnApplyDialogListener onApply = lesson -> {};

    interface OnDeleteLessonListener {
        void onDeleteLesson();
    }
    OnDeleteLessonListener onDeleteLesson = () -> {};

    private Lesson mLesson = new Lesson(new Time(7, 30), new Time(8, 15), "");

    private EditText mLessonName;
    private Button mDeleteLessonButton;

    private int mDeleteLessonButtonVisibility = View.VISIBLE;

    void setLessonData(Lesson lesson) {
        mLesson = new Lesson(new Time(lesson.begin.toString()), new Time(lesson.end.toString()), lesson.title);
    }

    void setLessonTitle(String title) {
        mLesson.title = title;
    }

    void setDeleteLessonButtonVisibility(int visibility) {
        if (mDeleteLessonButton != null) {
            mDeleteLessonButton.setVisibility(visibility);
        }
        else {
            mDeleteLessonButtonVisibility = visibility;
        }
    }

    private void showTimePickerDialog(Button b, Time time, int stringId) {
        new TimePickerDialog(getActivity(), (v, h, m) -> {
            time.hours = h;
            time.minutes = m;
            b.setText(getResources().getString(stringId, time.toString()));
        }, time.hours, time.minutes, true).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_change_lesson, null);

        mLessonName               = view.findViewById(R.id.lesson_name);
        mDeleteLessonButton       = view.findViewById(R.id.delete_lesson_button);
        Button mLessonBeginButton = view.findViewById(R.id.lesson_begin);
        Button mLessonEndButton   = view.findViewById(R.id.lesson_end);
        Button mCancelButton      = view.findViewById(R.id.cancel_button);
        Button mOkButton          = view.findViewById(R.id.ok_button);

        mLessonName.setText(mLesson.title);
        mLessonBeginButton.setText(getResources().getString(R.string.begin, mLesson.begin.toString()));
        mLessonEndButton.setText(getResources().getString(R.string.end, mLesson.end.toString()));
        mDeleteLessonButton.setVisibility(mDeleteLessonButtonVisibility);

        mLessonBeginButton.setOnClickListener(v -> showTimePickerDialog((Button) v, mLesson.begin, R.string.begin));
        mLessonEndButton.setOnClickListener(v -> showTimePickerDialog((Button) v, mLesson.end, R.string.end));

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
            mLesson.title = mLessonName.getText().toString();
            onApply.onApply(mLesson);
            dismiss();
        });

        return view;
    }
}
