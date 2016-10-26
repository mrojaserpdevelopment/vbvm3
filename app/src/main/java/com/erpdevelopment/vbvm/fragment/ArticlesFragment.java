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
import com.erpdevelopment.vbvm.activity.ArticleDetailsActivity;
import com.erpdevelopment.vbvm.adapter.ArticlesAdapter;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.service.WebServiceCall;
import com.erpdevelopment.vbvm.utils.DownloadJsonData;
import com.erpdevelopment.vbvm.utils.FilesManager;

public class ArticlesFragment extends Fragment implements TextWatcher {

    private View rootView;
//    private Resources res;
    private ArticlesAdapter adapterArticles;
    private ListView lvArticles;
    private EditText etSearchVideos;

    public static ArticlesFragment newInstance(int index) {
        ArticlesFragment f = new ArticlesFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_articles, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rootView = getView();
        adapterArticles = new ArticlesAdapter(getActivity(), FilesManager.listArticles);
        lvArticles = (ListView) rootView.findViewById(R.id.lvArticles);
        lvArticles.setAdapter(adapterArticles);
        lvArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                adapterArticles.setSelectedPosition(position);
                Article article = (Article) parent.getItemAtPosition(position);
                Intent i = new Intent(getActivity(), ArticleDetailsActivity.class);
                i.putExtra("article", article);
                startActivity(i);
            }
        });

        etSearchVideos = (EditText) rootView.findViewById(R.id.et_search_articles);
        etSearchVideos.addTextChangedListener(this);
        if ( (FilesManager.listArticles == null) || (FilesManager.listArticles.size() == 0) )
            DownloadJsonData.getInstance().asyncJsonArticles(adapterArticles);
        else
            adapterArticles.setArticleListItems(FilesManager.listArticles);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapterArticles.getFilter().filter(s.toString().trim());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        System.out.println("ArticlesFragment.setUserVisibleHint: " + isVisibleToUser);
        if (isVisibleToUser) {

        } else {

        }
    }
}
