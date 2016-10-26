package com.erpdevelopment.vbvm.adapter;

import java.util.ArrayList;
import java.util.List;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.activity.AudioPlayerService;
import com.erpdevelopment.vbvm.db.DBHandleLessons;
import com.erpdevelopment.vbvm.helper.AudioPlayerHelper;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.service.DownloadServiceTest;
import com.erpdevelopment.vbvm.service.downloader.DownloadService;
import com.erpdevelopment.vbvm.service.downloader.DownloaderThread;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.FontManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LessonsCompleteAdapter extends BaseAdapter {

    private Context context;
    private List<Lesson> lessons;
    private static int count = 0;
    private ArrayList<Lesson> listTempLesson = new ArrayList<Lesson>();
    private View rootView;

    public LessonsCompleteAdapter(Context context, List<Lesson> lessons, View rootView) {
        this.context = context;
        this.lessons = lessons;
        this.rootView = rootView;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolderItem = null;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_listview_lesson, parent, false);
            viewHolderItem = new ViewHolderItem();
            viewHolderItem.itemTvLessonNo = (TextView) convertView.findViewById(R.id.tv_lesson_no);
            viewHolderItem.itemTvLessonLength = (TextView) convertView.findViewById(R.id.tv_lesson_length);
            viewHolderItem.itemTvLessonDescription = (TextView) convertView.findViewById(R.id.tv_lesson_description);
            viewHolderItem.itemTvIconPlayMini = (TextView) convertView.findViewById(R.id.tv_icon_play_mini);
            viewHolderItem.itemTvIconTeacherAid = (TextView) convertView.findViewById(R.id.tv_icon_teacher_aid);
            viewHolderItem.itemTvIconTranscript = (TextView) convertView.findViewById(R.id.tv_icon_transcript);
            viewHolderItem.itemPbAudio = (ProgressBar) convertView.findViewById(R.id.pb_audio);
            viewHolderItem.itemPbTeacher = (ProgressBar) convertView.findViewById(R.id.pb_teacher);
            viewHolderItem.itemPbTranscript = (ProgressBar) convertView.findViewById(R.id.pb_transcript);
            viewHolderItem.itemViewDownloadedAudio = convertView.findViewById(R.id.vw_downloaded_audio);
            viewHolderItem.itemViewDownloadedTeacher = convertView.findViewById(R.id.vw_downloaded_teach);
            viewHolderItem.itemViewDownloadedTranscript = convertView.findViewById(R.id.vw_downloaded_trans);
            viewHolderItem.rlTeacherAid = (RelativeLayout) convertView.findViewById(R.id.rl_teacher_aid);
            viewHolderItem.rlTranscript = (RelativeLayout) convertView.findViewById(R.id.rl_transcript);
            viewHolderItem.rlPlayMini = (RelativeLayout) convertView.findViewById(R.id.rl_play_mini);
            convertView.setTag(viewHolderItem);
        } else {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }

        final Lesson lesson = lessons.get(position);

        viewHolderItem.itemTvLessonNo.setText(lesson.getTitle().substring(lesson.getTitle().lastIndexOf(" ")+1));
        viewHolderItem.itemTvLessonDescription.setText(lesson.getLessonsDescription());
        viewHolderItem.itemTvLessonLength.setText(lesson.getAudioLength());
        viewHolderItem.itemTvIconTeacherAid.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
        viewHolderItem.itemTvIconTranscript.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
        viewHolderItem.itemTvIconPlayMini.setTypeface(FontManager.getTypeface(context,FontManager.FONTAWESOME));
        viewHolderItem.itemPbAudio.setProgress(lesson.getDownloadProgressAudio());
        viewHolderItem.itemPbTeacher.setProgress(lesson.getDownloadProgressTeacher());
        viewHolderItem.itemPbTranscript.setProgress(lesson.getDownloadProgressTranscript());
        viewHolderItem.itemViewDownloadedAudio.setVisibility(View.GONE);

        if (lesson.getAudioSource().equals("")) {
            viewHolderItem.itemTvIconPlayMini.setVisibility(View.INVISIBLE);
        } else {
            viewHolderItem.itemTvIconPlayMini.setVisibility(View.VISIBLE);
            if (lesson.getDownloadStatusAudio() == 1) {
                viewHolderItem.itemTvIconPlayMini.setText(context.getResources().getString(R.string.fa_icon_play_mini));
                viewHolderItem.itemTvIconPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                viewHolderItem.itemViewDownloadedAudio.setVisibility(View.VISIBLE);
            } else if (lesson.getDownloadStatusAudio() == 2) {
                viewHolderItem.itemTvIconPlayMini.setText(context.getResources().getString(R.string.fa_icon_stop));
                viewHolderItem.itemTvIconPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                viewHolderItem.itemViewDownloadedAudio.setVisibility(View.GONE);
            } else {
                viewHolderItem.itemTvIconPlayMini.setText(context.getResources().getString(R.string.fa_icon_play_mini));
                viewHolderItem.itemTvIconPlayMini.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                viewHolderItem.itemViewDownloadedAudio.setVisibility(View.GONE);
            }
        }
        if (lesson.getTeacherAid().equals(""))
            viewHolderItem.itemTvIconTeacherAid.setVisibility(View.INVISIBLE);
        else {
            viewHolderItem.itemTvIconTeacherAid.setVisibility(View.VISIBLE);
            if (lesson.getDownloadStatusTeacherAid() == 1)
                viewHolderItem.itemViewDownloadedTeacher.setVisibility(View.VISIBLE);
            else
                viewHolderItem.itemViewDownloadedTeacher.setVisibility(View.GONE);
        }

        if (lesson.getTranscript().equals(""))
            viewHolderItem.itemTvIconTranscript.setVisibility(View.INVISIBLE);
        else {
            viewHolderItem.itemTvIconTranscript.setVisibility(View.VISIBLE);
            if (lesson.getDownloadStatusTranscript() == 1)
                viewHolderItem.itemViewDownloadedTranscript.setVisibility(View.VISIBLE);
            else
                viewHolderItem.itemViewDownloadedTranscript.setVisibility(View.GONE);
        }

        viewHolderItem.rlPlayMini.setOnClickListener(view -> onClickItemLessons(view,lesson,position));
        viewHolderItem.rlTeacherAid.setOnClickListener(view -> onClickItemLessons(view,lesson,position));
        viewHolderItem.rlTranscript.setOnClickListener(view -> onClickItemLessons(view,lesson,position));

        return convertView;
    }

    static class ViewHolderItem {
        RelativeLayout rlTeacherAid;
        RelativeLayout rlTranscript;
        RelativeLayout rlPlayMini;
        TextView itemTvLessonNo;
        TextView itemTvLessonLength;
        TextView itemTvLessonDescription;
        TextView itemTvIconTeacherAid;
        TextView itemTvIconTranscript;
        TextView itemTvIconPlayMini;
        ProgressBar itemPbAudio;
        ProgressBar itemPbTeacher;
        ProgressBar itemPbTranscript;
        View itemViewDownloadedTeacher;
        View itemViewDownloadedTranscript;
        View itemViewDownloadedAudio;
    }

    public void setLessonsCompleteItems(List<Lesson> newList) {
        lessons = newList;
//        TextView tvTitleLessons = (TextView) rootView.findViewById(R.id.tv_title_lessons_complete);
//        TextView tvCountLessons = (TextView) rootView.findViewById(R.id.tv_lesson_count_complete);
        RelativeLayout rlTitleLvLessons = (RelativeLayout) rootView.findViewById(R.id.rl_title_lv_lessons_complete);
        if (newList.size()==0){
//            tvTitleLessons.setVisibility(View.GONE);
//            tvCountLessons.setVisibility(View.GONE);
            rlTitleLvLessons.setVisibility(View.GONE);
        } else {
//            tvTitleLessons.setVisibility(View.VISIBLE);
//            tvCountLessons.setVisibility(View.VISIBLE);
            rlTitleLvLessons.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }

    private void onClickItemLessons(View view, Lesson lesson, int position) {

        DownloaderThread thread = null;
        int status = 0;
        String downloadUrl = "";
        String downloadType = "";
        switch (view.getId()) {
            case R.id.rl_play_mini:
                status = lesson.getDownloadStatusAudio();
                downloadUrl = lesson.getAudioSource();
                downloadType = "audio";
                break;
            case R.id.rl_teacher_aid:
                status = lesson.getDownloadStatusTeacherAid();
                downloadUrl = lesson.getTeacherAid();
                downloadType = "teacher";
                break;
            case R.id.rl_transcript:
                status = lesson.getDownloadStatusTranscript();
                downloadUrl = lesson.getTranscript();
                downloadType = "transcript";
                break;
        }
        System.out.println("LessonsAdapter.onClickItemLessons: " + status);
        if (status == 1) {
            if (downloadType.equals("audio")) {
                if (!(lesson.getIdProperty().equals(FilesManager.lastLessonId))) {
                    AudioPlayerService.created = false;
                    //save current/old position in track before updating to new position
                    if (!FilesManager.lastLessonId.equals("")) {
                        System.out.println("old lesson Id is: " + FilesManager.lastLessonId);
                        DBHandleLessons.saveCurrentPositionInTrack(FilesManager.lastLessonId, (int) AudioPlayerService.currentPositionInTrack);
                        System.out.println("position saved is: " + AudioPlayerService.currentPositionInTrack);

                        Lesson oldLesson = DBHandleLessons.getLessonById(FilesManager.lastLessonId);
                        if (oldLesson.getState().equals("playing"))
                            DBHandleLessons.updateLessonState(FilesManager.lastLessonId, AudioPlayerService.currentPositionInTrack, "partial");
                    }
                    //Update position in track with new selected lesson's
                    Lesson newLesson = DBHandleLessons.getLessonById(lesson.getIdProperty());
                    AudioPlayerService.currentPositionInTrack = newLesson.getCurrentPosition();
                    AudioPlayerService.savedOldPositionInTrack = AudioPlayerService.currentPositionInTrack;
                    System.out.println("position restored is: " + AudioPlayerService.currentPositionInTrack);
                }
                FilesManager.lastLessonId = lesson.getIdProperty();
                for (int i = 0; i<lessons.size(); i++) {
                    Lesson les = new Lesson();
                    les.setIdProperty(lessons.get(i).getIdProperty());
                    les.setAudioSource(lessons.get(i).getAudioSource());
                    les.setTitle(lessons.get(i).getTitle());
                    les.setLessonsDescription(lessons.get(i).getLessonsDescription());
                    les.setStudyThumbnailSource(lessons.get(i).getStudyThumbnailSource());
                    listTempLesson.add(les);
                }
                AudioPlayerService.listTempLesson2 = listTempLesson;
                System.out.println("lesson to be playted: " + FilesManager.lastLessonId);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putString("thumbnailSource", lesson.getStudyThumbnailSource());
                bundle.putString("description", lesson.getLessonsDescription());
                bundle.putString("title", lesson.getTitle());
                bundle.putInt("size", lesson.getStudyLessonsSize());
                AudioPlayerHelper helper = new AudioPlayerHelper();
                helper.setBundleExtras(bundle);
                helper.initContext((Activity) context, rootView);
            }
        }
    }
}