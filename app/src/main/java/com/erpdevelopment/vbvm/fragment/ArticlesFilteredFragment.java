package com.erpdevelopment.vbvm.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.erpdevelopment.vbvm.adapter.ArticlesAdapter;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.utils.DownloadJsonData;
import com.erpdevelopment.vbvm.utils.FilesManager;

public class ArticlesFilteredFragment extends Fragment implements TextWatcher {

    private View rootView;
    private ArticlesAdapter adapterArticles;
    private ListView lvArticles;
    private EditText etSearchArticles;

    private OnArticleFilteredSelectedListener mListener;

    public ArticlesFilteredFragment() {
    }

    public static ArticlesFilteredFragment newInstance(int index) {
        ArticlesFilteredFragment f = new ArticlesFilteredFragment();
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
        return inflater.inflate(R.layout.fragment_articles_filtered, container, false);
    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onArticleFilteredSelected(ar);
//        }
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rootView = getView();
        adapterArticles = new ArticlesAdapter(getActivity(), FilesManager.listArticles);
        lvArticles = (ListView) rootView.findViewById(R.id.lvArticles);
        lvArticles.setAdapter(adapterArticles);
        lvArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                Article article = (Article) parent.getItemAtPosition(position);
                if (mListener != null)
                    mListener.onArticleFilteredSelected(article);
            }
        });

        etSearchArticles = (EditText) rootView.findViewById(R.id.et_search_articles);
        etSearchArticles.addTextChangedListener(this);
        if ( (FilesManager.listArticles == null) || (FilesManager.listArticles.size() == 0) )
            DownloadJsonData.getInstance().asyncJsonArticles(adapterArticles);
        else
            adapterArticles.setArticleListItems(FilesManager.listArticles);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArticleFilteredSelectedListener) {
            mListener = (OnArticleFilteredSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnArticleFilteredSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public interface OnArticleFilteredSelectedListener {
        // TODO: Update argument type and name
        void onArticleFilteredSelected(Article article);
    }
}
