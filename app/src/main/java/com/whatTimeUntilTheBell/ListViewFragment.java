package com.whatTimeUntilTheBell;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListViewFragment extends Fragment {
    private int mDay;
    private static MyApplication mApp;
    private ActionButton mFab;

    private final static String[] hashMapValues = { "title", "time" };
    private final static int[] arrayOfIndices = { android.R.id.text1, android.R.id.text2 };

    @NonNull
    private static HashMap<String, String> lessonToMap(Lesson lesson) {
        HashMap<String, String> map = new HashMap<>();
        map.put(hashMapValues[0], lesson.getTitle());
        map.put(hashMapValues[1], lesson.toString());
        return map;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mDay = getArguments().getInt("day");
        mApp = (MyApplication) getActivity().getApplication();

        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mFab = ((MainActivity) getActivity()).getActionButton();

        List<Lesson> lessons = mApp.lessons[mDay];
        ListView lessonsLv = view.findViewById(R.id.lessons_list);
        ArrayList<HashMap<String, String>> lessonsList = new ArrayList<>();

        for (Lesson lesson : lessons) {
            lessonsList.add(lessonToMap(lesson));
        }

        SimpleAdapter adapter = new SimpleAdapter(getContext(), lessonsList, android.R.layout.simple_list_item_2,
                hashMapValues, arrayOfIndices);
        lessonsLv.setAdapter(adapter);

        lessonsLv.setOnItemClickListener((p, v, i, id) -> {
            ChangeLessonDialog dialog = new ChangeLessonDialog();
            dialog.setLessonData(mApp.lessons[mDay].get(i));
            dialog.onDeleteLesson = () -> {
                mApp.lessons[mDay].remove(i);
                mApp.saveLessons();
                lessonsList.remove(i);
                adapter.notifyDataSetChanged();
            };
            dialog.onApply = lesson -> {
                mApp.lessons[mDay].set(i, lesson);
                mApp.saveLesson(mDay, i);
                lessonsList.set(i, lessonToMap(lesson));
                adapter.notifyDataSetChanged();
            };

            dialog.show(getActivity().getFragmentManager(), ChangeLessonDialog.class.getSimpleName());
        });
        lessonsLv.setOnTouchListener(new View.OnTouchListener() {
            int before = -1;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    before = ((ListView) v).getFirstVisiblePosition();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    int now = ((ListView) v).getFirstVisiblePosition();
                    if(now < before) {
                        mFab.show();
                    }
                    else if(now > before) {
                        mFab.hide();
                    }
                }
                return false;
            }
        });
    }

    @NonNull
    public static ListViewFragment newInstance(int day) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle argumentsBundle = new Bundle(1);
        argumentsBundle.putInt("day", day);
        ;
        fragment.setArguments(argumentsBundle);
        return fragment;
    }
}
