package taller2.match_client;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

/* Chat Activity manage Chat Tab and Match Tab. It create then and setup view pages */
public class ChatActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String matchEmail = "";
    private static final String TAG = "ChatActivity";

    /* On create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Match Email
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            matchEmail = bundle.getString(getResources().getString(R.string.email));
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        Log.i(TAG, "ChatActivity is created");
    }

    /* Setup Chat Tab and Match Tab */
    private void setupViewPager(ViewPager viewPager) {
        Log.d(TAG, "Setup Chat and Match Tab");
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.email), matchEmail);
        ChatTab chatTab = new ChatTab();
        MatchTab matchTab = new MatchTab();
        chatTab.setArguments(bundle);
        matchTab.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(chatTab, getResources().getString(R.string.chat_tab_en));
        adapter.addFragment(matchTab, getResources().getString(R.string.info_tab_en));
        viewPager.setAdapter(adapter);
    }

    /* ViewPager Adapter is an adapter for ViewPager that save Fragment classes */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /* Handle menu item click */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {    // Back to previus Activity
            Log.i(TAG, "Back to previous Activity");
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
