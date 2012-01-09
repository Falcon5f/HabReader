/*
   Copyright (C) 2011 Andrey Zaytsev <a.einsam@gmail.com>
  
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
  
        http://www.apache.org/licenses/LICENSE-2.0
  
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package net.meiolania.apps.habrahabr.ui.fragments;

import java.io.IOException;
import java.util.ArrayList;

import net.meiolania.apps.habrahabr.R;
import net.meiolania.apps.habrahabr.activities.PostsShow;
import net.meiolania.apps.habrahabr.adapters.PostsAdapter;
import net.meiolania.apps.habrahabr.data.PostsData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class PostsFragment extends ApplicationListFragment{
    private final ArrayList<PostsData> postsDataList = new ArrayList<PostsData>();
    private PostsAdapter postsAdapter;
    private int page;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        
        loadList();
    }
    
    @Override
    public void onListItemClick(ListView list, View view, int position, long id){
        PostsData postsData = postsDataList.get(position);

        Intent intent = new Intent(getActivity(), PostsShow.class);
        intent.putExtra("link", postsData.getLink());
        
        startActivity(intent);
    }
    
    private void loadList(){
        ++page;
        new LoadPostsList().execute();
    }
    
    private class LoadPostsList extends AsyncTask<Void, Void, Void>{
        private ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... params){
            try{
                Document document = Jsoup.connect("http://habrahabr.ru/blogs/page" + page + "/").get();

                Elements postsList = document.select("div.post");

                for(Element post : postsList){
                    PostsData postsData = new PostsData();

                    Element title = post.select("a.post_title").first();
                    Element blog = post.select("a.blog_title").first();

                    postsData.setTitle(title.text());
                    postsData.setBlog(blog.text());
                    
                    postsData.setLink(title.attr("abs:href"));

                    postsDataList.add(postsData);
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading_posts_list));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result){
            if(!isCancelled() && page == 1){
                postsAdapter = new PostsAdapter(getActivity(), postsDataList);
                setListAdapter(postsAdapter);
            }else
                postsAdapter.notifyDataSetChanged();

            progressDialog.dismiss();
        }

    }
    
}