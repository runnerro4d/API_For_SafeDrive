package com.example.roadprotector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private LinearLayout mDotLayout;
    private TextView[] mDots;
    private sliderAdapter sliderAdapter;
    private Button mNextButton;
    private Button mBackButton;
    private Button mFinishBtn;
    private String activityName;
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Intent i = getIntent();
        activityName = i.getStringExtra("Activity");

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotLayout);

        mNextButton = (Button) findViewById(R.id.nextButton);
        mBackButton = (Button) findViewById(R.id.prevButton);
        mFinishBtn = (Button) findViewById(R.id.finishbtn);


        sliderAdapter = new sliderAdapter(this, activityName);

        mViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mViewPager.addOnPageChangeListener(viewListener);


        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mViewPager.setCurrentItem(mCurrentPage + 1);
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (activityName.equals("NavigationActivity")){
                    Intent intent = new Intent(OnboardingActivity.this, NavigationActivity.class);
                    startActivity(intent);
                }
                else if (activityName.equals("ExploreActivity")){
                    Intent intent = new Intent(OnboardingActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    public void addDotsIndicator(int position){
        int size = 1;
        if(activityName.equals("NavigationActivity")){
            size = 6;
        }
        else if(activityName.equals("ExploreActivity")){
            size = 6;
        }

        mDots = new TextView[size];
        mDotLayout.removeAllViews();

        for(int i = 0; i < mDots.length; i++){

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorPrimary));

            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDotsIndicator(position);
            mCurrentPage = position;

            if (position == 0){
                mFinishBtn.setEnabled(false);
                mNextButton.setEnabled(true);
                mBackButton.setEnabled(false);
                mBackButton.setVisibility(View.INVISIBLE);
                mFinishBtn.setVisibility(View.INVISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
                mFinishBtn.setText("");
                mNextButton.setText("Next");
                mBackButton.setText("");
            }
            else if (position == mDots.length - 1){
                mFinishBtn.setEnabled(true);
                mNextButton.setEnabled(false);
                mBackButton.setEnabled(true);
                mNextButton.setVisibility(View.INVISIBLE);
                mFinishBtn.setVisibility(View.VISIBLE);
                mFinishBtn.setText("Finish");
                mNextButton.setText("");
                mBackButton.setText("Back");
            }
            else{
                mFinishBtn.setEnabled(false);
                mNextButton.setEnabled(true);
                mBackButton.setEnabled(true);
                mFinishBtn.setVisibility(View.INVISIBLE);
                mBackButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
                mFinishBtn.setText("");
                mNextButton.setText("Next");
                mBackButton.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
