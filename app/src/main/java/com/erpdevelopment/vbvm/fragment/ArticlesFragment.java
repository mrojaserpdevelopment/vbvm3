package com.erpdevelopment.vbvm.fragment;


import android.content.Context;
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
import com.erpdevelopment.vbvm.adapter.ArticlesAdapter;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.utils.DownloadJsonData;
import com.erpdevelopment.vbvm.utils.FilesManager;

public class ArticlesFragment extends Fragment implements TextWatcher {

    private View rootView;
    private ArticlesAdapter adapterArticles;
    private ListView lvArticles;
    private EditText etSearchArticles;

    private OnArticleSelectedListener mListener;

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
                Article article = (Article) parent.getItemAtPosition(position);
                if (mListener != null)
                    mListener.onArticleSelected(article);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArticleSelectedListener) {
            mListener = (OnArticleSelectedListener) context;
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

    public interface OnArticleSelectedListener {
        void onArticleSelected(Article article);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            System.out.println("ArticlesFragment.onHiddenChanged");
//            View current = getActivity().getCurrentFocus();
//            if (current != null) {
//                System.out.println("clearing focus...");
//                current.clearFocus();
//            }
            if ( (FilesManager.listArticles == null) || (FilesManager.listArticles.size() == 0) )
                DownloadJsonData.getInstance().asyncJsonArticles(adapterArticles);
            else
                adapterArticles.setArticleListItems(FilesManager.listArticles);
        }

    }

//    private void filterByTopic(String topic){
////        if ( !CheckConnectivity.isOnline(QAndAPostsActivity.this)) {
////            CheckConnectivity.showMessage(QAndAPostsActivity.this);
////        } else {
//            List<Article> tempList = new ArrayList<Article>();
//            for ( int i=0; i < FilesManager.listArticles.size(); i++) {
//                if ( FilesManager.listArticles != null ) {
//                    List<String> topics = FilesManager.listArticles.get(i).getTopics();
//                    for ( int j=0; j < topics.size(); j++) {
//                        if ( topics.get(j).equals(topic) ) {
//                            tempList.add(FilesManager.listArticles.get(i));
//                            break;
//                        }
//                    }
//                }
//            }
//            adapterArticles.setArticleListItems(tempList);
////        }
//    }
}
