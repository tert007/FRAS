package com.example.alexander.fastreading.motivator;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.alexander.fastreading.CircularProgressBar;
import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.speedreading.SpeedReadingActivity;

public class MotivatorsPageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.motivator_view_pager_fragment, container, false);

        int drawableId = getArguments().getInt("drawable_id");

        ImageView imageView = (ImageView) view.findViewById(R.id.motivation_view_pager_image_view);
        imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), drawableId, null));

        return view;
    }
}
