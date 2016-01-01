package company.mahendraprophecy.MAIN_FRAGMENTS;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.karim.MaterialTabs;
import company.mahendraprophecy.SUB_FRAGMENTS.SUB_FRAGMENT;
import company.mahendraprophecy.R;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class LATEST_NEWSLETTERS extends android.support.v4.app.Fragment
{
    MaterialTabs subTabs;
    ViewPager subPager,mainPager;
    ArrayList<String> headers, header_colors, category_ids;
    JSONObject object;
    JSONArray array;
    MyPageAdapter pageAdapter;

    public LATEST_NEWSLETTERS()
    {

    }

    @SuppressLint("ValidFragment")
    public LATEST_NEWSLETTERS(String categoryData,ViewPager mainPager)
    {
        try
        {
            this.mainPager=mainPager;
            object = new JSONObject(categoryData);
            array = object.getJSONArray("categories");
            headers = new ArrayList<>();
            header_colors = new ArrayList<>();
            category_ids = new ArrayList<>();
            for (int i = 0; i < array.length(); i++)
            {
                object = array.getJSONObject(i);
                headers.add(object.getString("name"));
                header_colors.add(object.getString("bgcolor"));
                category_ids.add(object.getString("cat_id"));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private List<Fragment> getFragments()
    {
        List<Fragment> fList = new ArrayList<Fragment>();
        for (int i = 0; i < array.length(); i++)
        {
            try
            {
                object = array.getJSONObject(i);
                fList.add(SUB_FRAGMENT.newInstance(object.getString("cat_id"),object.getString("bgcolor"),object.getString("name"),mainPager));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return fList;
    }

    void setupTabs()
    {
        subTabs = (MaterialTabs) getActivity().findViewById(R.id.subTabs);
        subPager = (ViewPager) getActivity().findViewById(R.id.subPager);
        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getFragmentManager(), fragments);
        subPager.setAdapter(pageAdapter);
        subTabs.setViewPager(subPager);
        subTabs.setTextColorUnselected(Color.parseColor("#333333"));
        /*
        subPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                subTabs.setIndicatorColor(Color.parseColor(header_colors.get(position)));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        */
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mainfragment_latest_newsletters, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setupTabs();
    }


    private class MyPageAdapter extends FragmentPagerAdapter
    {
        private List<Fragment> fragments;

        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments)
        {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public Fragment getItem(int position)
        {
            return this.fragments.get(position);
        }

        @Override
        public int getCount()
        {
            return this.fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String x = headers.get(position);
            if (x.contains("&")) {
                x = x.replaceAll("&amp;", " & ");
            }
            return x;
        }

        /*
        @Override
        public View getCustomTabView(ViewGroup viewGroup, int i)
        {
            TextView header = new TextView(getActivity());
            String x = headers.get(i);
            if (x.contains("&")) {
                x = x.replaceAll("&amp;", " & ");
            }
            header.setTextColor(Color.parseColor(header_colors.get(i)));
            header.setText(x);
            header.setGravity(Gravity.CENTER);
            header.setAllCaps(true);
            header.setTypeface(Typeface.DEFAULT_BOLD);
            return header;
        }
        */
    }
}



