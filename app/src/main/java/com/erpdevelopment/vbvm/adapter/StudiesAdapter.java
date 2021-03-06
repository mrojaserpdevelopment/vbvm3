package com.erpdevelopment.vbvm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.utils.imageloading.ImageLoader2;

import java.util.List;

public class StudiesAdapter extends BaseAdapter {

    private Context context;
    private List<Study> listStudies;
//    private ImageLoader imageLoader;
    private ImageLoader2 imageLoader2;

    public StudiesAdapter(Context context, List<Study> listStudies, ImageLoader2 imageLoader2) {
        this.context = context;
        this.listStudies = listStudies;
//        this.imageLoader = imageLoader;
        this.imageLoader2 = imageLoader2;
    }

    @Override
    public int getCount() {
        return listStudies.size();
    }

    @Override
    public Object getItem(int position) {
        return listStudies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolderItem viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_gridview_study, null);
            viewHolder = new ViewHolderItem();
            viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.txt);
            viewHolder.imageViewItem = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        Study study = (Study) getItem(position);
        if (study != null) {
            viewHolder.textViewItem.setText(study.getTitle());
//            imageLoader.DisplayImage(study.getThumbnailSource(), viewHolder.imageViewItem);
            imageLoader2.DisplayImage(study.getThumbnailSource(), viewHolder.imageViewItem);
        }
        return convertView;
    }

    public void setStudyListItems(List<Study> newList) {
        listStudies = newList;
        notifyDataSetChanged();
    }

    static class ViewHolderItem {
        TextView textViewItem;
        ImageView imageViewItem;
    }
}
