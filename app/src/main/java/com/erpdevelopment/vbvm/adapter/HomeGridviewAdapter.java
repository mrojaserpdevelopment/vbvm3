package com.erpdevelopment.vbvm.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;

import java.util.List;

/**
 * Created by usuario on 14/09/2016.
 */
public class HomeGridviewAdapter extends BaseAdapter {

    private Activity activity;
    private List<Lesson> lessons;
    private ImageLoader2 imageLoader;

    public HomeGridviewAdapter(Activity activity, List<Lesson> lessons, ImageLoader2 imageLoader) {
        this.activity = activity;
        this.lessons = lessons;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return lessons.size();
    }

    @Override
    public Object getItem(int position) {
        return lessons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_hlistview_study, null);

            viewHolder = new ViewHolderItem();
            viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.txt);
            viewHolder.imageViewItem = (ImageView) convertView.findViewById(R.id.img);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        Lesson lesson = (Lesson) getItem(position);

        if (lesson != null) {
            viewHolder.textViewItem.setText(lesson.getTitle());
            imageLoader.DisplayImage(lesson.getStudyThumbnailSource(), viewHolder.imageViewItem);
        }
        return convertView;
    }

    public void setLessonListItems(List<Lesson> newList) {
        lessons = newList;
        notifyDataSetChanged();
    }

    static class ViewHolderItem {
        TextView textViewItem;
        ImageView imageViewItem;
    }
}
