package com.example.roadprotector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class sliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;


    public int[] slide_images;


    public int[] navigation_set= {
            R.drawable.home_page,
            R.drawable.search,
            R.drawable.rest_stop_preference,
            R.drawable.colour_coded_routes,
            R.drawable.route_info,
            R.drawable.geofence
    };

    public sliderAdapter(Context context, String ActivityName){
        this.context = context;
        if (ActivityName.equals("NavigationActivity")){
            slide_images = navigation_set;
        }
        else if (ActivityName.equals("ExploreActivity")){
            slide_images = navigation_set;
        }
    }



    @Override
    public int getCount() {
        return slide_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.imageView);

        slideImageView.setImageResource(slide_images[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((RelativeLayout) object);
    }
}
