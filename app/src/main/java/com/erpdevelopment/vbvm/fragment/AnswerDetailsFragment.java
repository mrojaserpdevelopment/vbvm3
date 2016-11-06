package com.erpdevelopment.vbvm.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.utils.Utilities;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;

public class AnswerDetailsFragment extends Fragment {

    private View rootView;
    private Answer mAnswer;
    private TextView tvAnswerDetailsTitle;
    private TextView tvAnswerDetailsAuthor;
    private TextView tvAnswerDetailsDate;
    private TextView tvAnswerDetailsDescription;
    private ImageView imgAuthor;
//    private ImageLoader imageLoader;
private ImageLoader2 imageLoader2;
    private Activity activity;

    public AnswerDetailsFragment() {
    }

    public static AnswerDetailsFragment newInstance() {
        AnswerDetailsFragment f = new AnswerDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("index", 0);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null)
            mAnswer = getArguments().getParcelable("answer");
        return inflater.inflate(R.layout.fragment_article_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootView = getView();
        tvAnswerDetailsTitle = (TextView) rootView.findViewById(R.id.tv_article_details_title);
        tvAnswerDetailsAuthor = (TextView) rootView.findViewById(R.id.tv_article_details_author);
        tvAnswerDetailsDate = (TextView) rootView.findViewById(R.id.tv_article_details_date);
        tvAnswerDetailsDescription = (TextView) rootView.findViewById(R.id.tv_article_details_description);
        imgAuthor = (ImageView) rootView.findViewById(R.id.img_article_details_author);
        imgAuthor.setVisibility(View.GONE);

        tvAnswerDetailsTitle.setText(mAnswer.getTitle());
        tvAnswerDetailsAuthor.setText(mAnswer.getAuthorName());
        tvAnswerDetailsDate.setText(Utilities.getSimpleDateFormat(mAnswer.getPostedDate(),"dd MMM yyyy"));
        tvAnswerDetailsDescription.setText(mAnswer.getBody());
    }


}
