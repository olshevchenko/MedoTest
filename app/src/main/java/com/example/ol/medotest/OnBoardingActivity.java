package com.example.ol.medotest;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OnBoardingActivity extends AppCompatActivity {

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link FragmentPagerAdapter} derivative, which will keep every
   * loaded fragment in memory. If this becomes too memory intensive, it
   * may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  private SectionsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  private ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_onboarding);

    final ImageView ivPagerIndicator1 = (ImageView) findViewById(R.id.ivPagerIndicator1);
    final ImageView ivPagerIndicator2 = (ImageView) findViewById(R.id.ivPagerIndicator2);
    final ImageView ivPagerIndicator3 = (ImageView) findViewById(R.id.ivPagerIndicator3);
    /// highlight page #1
    ivPagerIndicator1.setImageLevel(1);
    ivPagerIndicator2.setImageLevel(0);
    ivPagerIndicator3.setImageLevel(0);

    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(R.id.container);
    mViewPager.setAdapter(mSectionsPagerAdapter);
    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

      @Override
      public void onPageSelected(int position) {
        switch (position+1) {
          case 1:
            ivPagerIndicator1.setImageLevel(1);
            ivPagerIndicator2.setImageLevel(0);
            ivPagerIndicator3.setImageLevel(0);
            break;

          case 2:
            ivPagerIndicator1.setImageLevel(0);
            ivPagerIndicator2.setImageLevel(1);
            ivPagerIndicator3.setImageLevel(0);
            break;

          case 3:
          default:
            ivPagerIndicator1.setImageLevel(0);
            ivPagerIndicator2.setImageLevel(0);
            ivPagerIndicator3.setImageLevel(1);
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {}
    });

    Button btStart = (Button) findViewById(R.id.btStart);
    btStart.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), LoginActivity.class);
            startActivity(intent);
          }
        }
    );

  }

  /**
   * A placeholder fragment containing a simple view.
   */
  public static class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private int sectionNumber;
    private static String[] SECTION_MESSAGES_ARRAY;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
      PlaceholderFragment fragment = new PlaceholderFragment();
      Bundle args = new Bundle();
      args.putInt(Constants.Extras.ARG_SECTION_NUMBER, sectionNumber);
      fragment.setArguments(args);
      return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      SECTION_MESSAGES_ARRAY = getResources().getStringArray(R.array.section_messages_text_array);
      View rootView = inflater.inflate(R.layout.fragment_onboarding, container, false);
      sectionNumber = getArguments().getInt(Constants.Extras.ARG_SECTION_NUMBER);

      ImageView ivOnBoarding = (ImageView) rootView.findViewById(R.id.ivOnBoarding);


      switch (sectionNumber) {
        case 1:
          ivOnBoarding.setImageDrawable(
              ContextCompat.getDrawable(getContext(), R.drawable.onboarding1));
        break;

        case 2:
          /// special layout_marginTop value for section #2
          ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) ivOnBoarding.getLayoutParams();
          params.topMargin = (int) getResources().getDimension(R.dimen.onboarding2_top_fullmargin);
          ivOnBoarding.setLayoutParams(params);
          ivOnBoarding.setImageDrawable(
              ContextCompat.getDrawable(getContext(), R.drawable.onboarding2));
        break;

        case 3:
        default:
          ivOnBoarding.setImageDrawable(
              ContextCompat.getDrawable(getContext(), R.drawable.onboarding3));
        break;
      }

      TextView tvOnBoardingInfo = (TextView) rootView.findViewById(R.id.tvOnBoardingInfo);
      tvOnBoardingInfo.setText(SECTION_MESSAGES_ARRAY[sectionNumber-1]);

      return rootView;
    }
  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
   * one of the sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      // getItem is called to instantiate the fragment for the given page.
      // Return a PlaceholderFragment (defined as a static inner class below).
      return PlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
      // Show 3 total pages.
      return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return null;
    }
  }
}
