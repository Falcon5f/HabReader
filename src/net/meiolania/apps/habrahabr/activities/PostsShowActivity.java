package net.meiolania.apps.habrahabr.activities;

import net.meiolania.apps.habrahabr.R;
import net.meiolania.apps.habrahabr.fragments.ShowCommentsFragment;
import net.meiolania.apps.habrahabr.fragments.ShowPostFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class PostsShowActivity extends SherlockFragmentActivity implements TabListener{
    public final static String EXTRA_URL = "url";
    private String url;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra(EXTRA_URL);
        showActionBar();
    }
    
    private void showActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        Tab tab = actionBar.newTab().setText(R.string.post).setTag("post").setTabListener(this);
        actionBar.addTab(tab);
        
        tab = actionBar.newTab().setText(R.string.comments).setTag("comments").setTabListener(this);
        actionBar.addTab(tab);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(this, PostsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onTabSelected(Tab tab, FragmentTransaction ft){
        if(tab.getTag().equals("post")){
            ShowPostFragment showPostFragment = new ShowPostFragment(url);
            ft.replace(android.R.id.content, showPostFragment);
        }else if(tab.getTag().equals("comments")){
            ShowCommentsFragment showCommentsFragment = new ShowCommentsFragment();
            ft.replace(android.R.id.content, showCommentsFragment);
        }
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft){
        
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft){
        
    }
    
}