package com.erpdevelopment.vbvm.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.MainActivity;
import com.erpdevelopment.vbvm.helper.FlowTextHelper;
import com.erpdevelopment.vbvm.helper.LeadingMarginSpanHelper;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.utils.BitmapManager2;
import com.erpdevelopment.vbvm.utils.Utilities;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;

import java.util.Locale;

public class ArticleDetailsFragment extends Fragment {

    private View rootView;
    private Article mArticle;
    private TextView tvArticleDetailsTitle;
    private TextView tvArticleDetailsAuthor;
    private TextView tvArticleDetailsDate;
    private TextView tvArticleDetailsDescription;
    private TextView tvArticleDetailsDescription2;
    private ImageView imgArticleDetailsAuthor;
//    private ImageLoader imageLoader;
    private ImageLoader2 imageLoader2;
    private Activity activity;
    private String dateFormat;

    public ArticleDetailsFragment() {
    }

    public static ArticleDetailsFragment newInstance() {
        ArticleDetailsFragment f = new ArticleDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("index", 0);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
//        imageLoader = new ImageLoader(activity);
        imageLoader2 = new ImageLoader2(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null)
            mArticle = getArguments().getParcelable("article");
        return inflater.inflate(R.layout.fragment_article_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootView = getView();
        tvArticleDetailsTitle = (TextView) rootView.findViewById(R.id.tv_article_details_title);
        tvArticleDetailsAuthor = (TextView) rootView.findViewById(R.id.tv_article_details_author);
        tvArticleDetailsDate = (TextView) rootView.findViewById(R.id.tv_article_details_date);
        tvArticleDetailsDescription = (TextView) rootView.findViewById(R.id.tv_article_details_description);
        tvArticleDetailsDescription2 = (TextView) rootView.findViewById(R.id.tv_article_details_description2);
        imgArticleDetailsAuthor = (ImageView) rootView.findViewById(R.id.img_article_details_author);

        tvArticleDetailsTitle.setText(mArticle.getTitle());
        tvArticleDetailsAuthor.setText(mArticle.getAuthorName());

        dateFormat = Locale.getDefault().getCountry().equals("US") ? "MMM dd yyyy" : "dd MMM yyyy";
        tvArticleDetailsDate.setText(Utilities.getSimpleDateFormat(mArticle.getPostedDate(),dateFormat));

        String text = mArticle.getBody();
        int index = text.indexOf("\n");
        text = text.substring(0, index);
        tvArticleDetailsDescription.setText(Utilities.getFloatingText(activity, text, R.drawable.photo_brady));
        tvArticleDetailsDescription2.setText( mArticle.getBody().substring(index+1) );

        switch (mArticle.getAuthorName()) {
            case "Stephen Armstrong": BitmapManager2.setImageBitmap(activity, imgArticleDetailsAuthor, R.drawable.photo_stephen_scaled);
                break;
            case "Melissa Church": BitmapManager2.setImageBitmap(activity, imgArticleDetailsAuthor, R.drawable.photo_melissa);
                break;
            case "Brian Smith": BitmapManager2.setImageBitmap(activity, imgArticleDetailsAuthor, R.drawable.photo_brian_scaled);
                break;
            case "Ivette Irizarry": BitmapManager2.setImageBitmap(activity, imgArticleDetailsAuthor, R.drawable.photo_ivette_scaled);
                break;
            case "Brady Stephenson": BitmapManager2.setImageBitmap(activity, imgArticleDetailsAuthor, R.drawable.photo_brady);
                break;
            case "Guest Contributor": BitmapManager2.setImageBitmap(activity, imgArticleDetailsAuthor, R.drawable.photo_guest);
                break;
            case "VBVM Staff":
                break;
            default:
                break;
        }

    }

//    private SpannableString getFloatingText(String description, int iconRefSize) {
////        String text = getString(R.string.text);
//
//        // Получаем иконку и ее ширину
////        Drawable dIcon = getResources().getDrawable(R.drawable.photo_brady);
//        Drawable dIcon = ResourcesCompat.getDrawable(getResources(), iconRefSize, null);
//        int leftMargin = (dIcon != null ? dIcon.getIntrinsicWidth() : 0) + 15;
////        int leftMargin = 100;
//
//        // Устанавливаем иконку в R.id.icon
////        ImageView icon = (ImageView) findViewById(R.id.icon);
////        icon.setBackgroundDrawable(dIcon);
//
////        SpannableString ss = new SpannableString(text);
//        SpannableString ss = new SpannableString(description);
//        // Выставляем отступ для первых трех строк абазца
//        ss.setSpan(new LeadingMarginSpanHelper(leftMargin, 5), 0, ss.length(), 0);
//
////        TextView messageView = (TextView) findViewById(R.id.message_view);
////        messageView.setText(ss);
//        return ss;
//    }

}
