package com.erpdevelopment.vbvm.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.QAndAPostDetailsActivity;
import com.erpdevelopment.vbvm.adapter.QAndAPostsAdapter;
import com.erpdevelopment.vbvm.model.QandAPost;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.utils.DownloadJsonData;
import com.erpdevelopment.vbvm.utils.FilesManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnswersFragment extends Fragment implements TextWatcher {

    private View rootView;
    private QAndAPostsAdapter adapterQAPosts;
    private ListView lvQAPosts;
    private EditText etSearchAnswers;

    public AnswersFragment() {
        // Required empty public constructor
    }

    public static AnswersFragment newInstance(int index) {
        AnswersFragment f = new AnswersFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_answers, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rootView = getView();
        adapterQAPosts = new QAndAPostsAdapter(getActivity(), FilesManager.listQAPosts);
        lvQAPosts = (ListView) rootView.findViewById(R.id.lvAnswers);
        lvQAPosts.setAdapter(adapterQAPosts);
        lvQAPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                adapterQAPosts.setSelectedPosition(position);
                QandAPost post = (QandAPost) parent.getItemAtPosition(position);
                Intent i = new Intent(getActivity(), QAndAPostDetailsActivity.class);
                i.putExtra("article", post);
                startActivity(i);
            }
        });

        etSearchAnswers = (EditText) rootView.findViewById(R.id.et_search_answers);
        etSearchAnswers.addTextChangedListener(this);
        if ( (FilesManager.listQAPosts == null) || (FilesManager.listQAPosts.size() == 0) )
            DownloadJsonData.getInstance().asyncJsonQAndAPosts(adapterQAPosts);
        else
            adapterQAPosts.setQAndAPostsListItems(FilesManager.listQAPosts);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapterQAPosts.getFilter().filter(s.toString().trim());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
