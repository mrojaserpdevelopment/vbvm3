package com.erpdevelopment.vbvm.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.MainActivity;
import com.erpdevelopment.vbvm.adapter.AnswersAdapter;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.utils.DownloadJsonData;
import com.erpdevelopment.vbvm.utils.FilesManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class AnswersFragment extends Fragment implements TextWatcher {

    private View rootView;
    private AnswersAdapter adapterAnswers;
    private ListView lvAnswers;
    private EditText etSearchAnswers;
    private InputMethodManager imm;
    private Activity activity;

    private OnAnswerSelectedListener mListener;

    public AnswersFragment() {
    }

    public static AnswersFragment newInstance(int index) {
        AnswersFragment f = new AnswersFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
//        adapterAnswers = new AnswersAdapter(getActivity(), FilesManager.listAnswers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_answers, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rootView = getView();
        adapterAnswers = new AnswersAdapter(getActivity(), FilesManager.listAnswers);
        lvAnswers = (ListView) rootView.findViewById(R.id.lvAnswers);
        lvAnswers.setAdapter(adapterAnswers);
        lvAnswers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Answer answer = (Answer) parent.getItemAtPosition(position);
                mListener.onAnswerSelected(answer);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        etSearchAnswers = (EditText) rootView.findViewById(R.id.et_search_answers);
        etSearchAnswers.addTextChangedListener(this);
        if ( (FilesManager.listAnswers == null) || (FilesManager.listAnswers.size() == 0) ) {
            DownloadJsonData.getInstance().asyncJsonAnswers(adapterAnswers);
            System.out.println("FilesManager.listAnswers is null");
        } else {
            adapterAnswers.setAnswersListItems(FilesManager.listAnswers);
            System.out.println("FilesManager.listAnswers not null");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapterAnswers.getFilter().filter(s.toString().trim());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAnswerSelectedListener) {
            mListener = (OnAnswerSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAnswerSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(Answer answer);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            System.out.println("AnswersFragment.onHiddenChanged");
        }

    }

}
