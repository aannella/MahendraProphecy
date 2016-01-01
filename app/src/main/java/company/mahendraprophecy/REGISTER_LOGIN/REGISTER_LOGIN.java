package company.mahendraprophecy.REGISTER_LOGIN;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import io.karim.MaterialTabs;
import company.mahendraprophecy.R;

public class REGISTER_LOGIN extends ActionBarActivity
{

    private ViewPager pager;
    MaterialTabs tabs;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_login);
        pager= (ViewPager) findViewById(R.id.register_login_pager);
        tabs= (MaterialTabs) findViewById(R.id.login_register_tabs);
        login_register_pager adapter=new login_register_pager(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
    }


    class login_register_pager extends FragmentPagerAdapter
    {

        public login_register_pager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {

            Fragment fragment=new Fragment();

            switch (position)
            {
                case 0:
                {
                    fragment=new LOGIN(pager);
                    break;
                }

                case 1:
                {
                    fragment=new REGISTER();
                    break;
                }

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        String[] titles={"LOGIN","REGISTER"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }


}
