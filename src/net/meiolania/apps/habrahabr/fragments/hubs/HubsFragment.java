/*
Copyright 2012 Andrey Zaytsev

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

package net.meiolania.apps.habrahabr.fragments.hubs;

import java.util.ArrayList;

import net.meiolania.apps.habrahabr.R;
import net.meiolania.apps.habrahabr.activities.HubsSearchActivity;
import net.meiolania.apps.habrahabr.activities.HubsShowActivity;
import net.meiolania.apps.habrahabr.adapters.HubsAdapter;
import net.meiolania.apps.habrahabr.data.HubsData;
import net.meiolania.apps.habrahabr.fragments.hubs.loader.HubsLoader;
import net.meiolania.apps.habrahabr.utils.ConnectionUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class HubsFragment extends SherlockListFragment implements OnScrollListener, LoaderCallbacks<ArrayList<HubsData>>
{
	public final static int LOADER_HUBS = 0;
	public final static String URL_ARGUMENT = "url";
	private ArrayList<HubsData> hubs;
	private HubsAdapter adapter;
	private int page;
	private boolean isLoadData;
	private String url;
	private boolean noMoreData;

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);

		url = getArguments().getString(URL_ARGUMENT);

		if(adapter == null)
		{
			hubs = new ArrayList<HubsData>();
			adapter = new HubsAdapter(getSherlockActivity(), hubs);
		}

		setListAdapter(adapter);
		setListShown(true);

		getListView().setOnScrollListener(this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.hubs_fragment, menu);

		final EditText searchQuery = (EditText) menu.findItem(R.id.search).getActionView().findViewById(R.id.search_query);
		searchQuery.setOnEditorActionListener(new OnEditorActionListener()
		{
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if(actionId == EditorInfo.IME_ACTION_SEARCH)
				{
					Intent intent = new Intent(getSherlockActivity(), HubsSearchActivity.class);
					intent.putExtra(HubsSearchActivity.EXTRA_QUERY, searchQuery.getText().toString());
					startActivity(intent);
					return true;
				}
				return false;
			}
		});

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onListItemClick(ListView list, View view, int position, long id)
	{
		showHub(position);
	}

	protected void showHub(int position)
	{
		HubsData data = hubs.get(position);

		Intent intent = new Intent(getSherlockActivity(), HubsShowActivity.class);
		intent.putExtra(HubsShowActivity.EXTRA_URL, data.getUrl());
		intent.putExtra(HubsShowActivity.EXTRA_TITLE, data.getTitle());

		startActivity(intent);
	}

	protected void restartLoading()
	{
		if(ConnectionUtils.isConnected(getSherlockActivity()))
		{
			getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);

			HubsLoader.setPage(++page);

			getSherlockActivity().getSupportLoaderManager().restartLoader(LOADER_HUBS, null, this);

			isLoadData = true;
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		if((firstVisibleItem + visibleItemCount) == totalItemCount && !isLoadData && !noMoreData)
			restartLoading();
	}

	public void onScrollStateChanged(AbsListView view, int scrollState)
	{

	}

	@Override
	public Loader<ArrayList<HubsData>> onCreateLoader(int id, Bundle args)
	{
		HubsLoader loader = new HubsLoader(getSherlockActivity(), url);
		loader.forceLoad();

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<HubsData>> loader, ArrayList<HubsData> data)
	{
		if(data.isEmpty())
		{
			noMoreData = true;

			Toast.makeText(getSherlockActivity(), R.string.no_more_pages, Toast.LENGTH_SHORT).show();
		}

		hubs.addAll(data);
		adapter.notifyDataSetChanged();

		if(getSherlockActivity() != null)
			getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);

		isLoadData = false;
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<HubsData>> loader)
	{

	}

}