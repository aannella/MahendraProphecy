package company.mahendraprophecy.PROFILE;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import io.karim.MaterialTabs;
import company.mahendraprophecy.R;


public class PROFILE extends ActionBarActivity {
    MaterialTabs tabs;
    ViewPager viewPager;
    String[] titles = new String[]{"My Profile", "My Subscriptions", "Change Password"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        tabs = (MaterialTabs) findViewById(R.id.profileTabs);
        viewPager = (ViewPager) findViewById(R.id.profilePager);
        profileAdapter adapter = new profileAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabs.setViewPager(viewPager);
        tabs.setTextColorSelected(Color.parseColor("#FFFFFF"));
        tabs.setTextColorUnselected(Color.parseColor("#FAFAFA"));

        ((ImageButton)findViewById(R.id.profile_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    class profileAdapter extends FragmentPagerAdapter {

        public profileAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new Fragment();
            switch (position) {
                case 0: {
                    fragment = new MY_PROFILE();
                    break;
                }

                case 1:{
                    fragment=new MY_SUBSCRIPTIONS();
                    break;
                }

                case 2: {
                    fragment = new CHANGE_PASSWORD();
                    break;
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
