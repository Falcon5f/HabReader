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

package net.meiolania.apps.habrahabr.ui.dashboard;

import net.meiolania.apps.habrahabr.ApplicationFragment;
import net.meiolania.apps.habrahabr.R;
import net.meiolania.apps.habrahabr.api.ConnectionApi;
import net.meiolania.apps.habrahabr.utils.VibrateUtils;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class DashboardFragment extends ApplicationFragment{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        checkMobileInternetPreferences();
        checkInternetConnection();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(container == null)
            return null;

        return inflater.inflate(R.layout.dashboard_fragment, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.dashboard, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(preferences.isVibrate())
            VibrateUtils.doVibrate(getActivity());
        switch(item.getItemId()){
            case R.id.about:
                startActivity(new Intent(getActivity(), AboutInfoActivity.class));
                break;
            case R.id.preferences:
                startActivity(new Intent(getActivity(), PreferencesActivity.class));
                break;
            case R.id.other_applications:
                try{
                    Uri uri = Uri.parse("https://market.android.com/developer?pub=Meiolania");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                catch(ActivityNotFoundException e){
                }
                break;
        }
        return true;
    }

    private void checkInternetConnection(){
        if(!ConnectionApi.isConnection(getActivity())){
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle(R.string.attention);
            alertDialog.setMessage(getString(R.string.attention_no_network_connection));
            alertDialog.setButton(getString(android.R.string.ok), new OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.dismiss();
                }
            });
            alertDialog.setCancelable(true);
            alertDialog.show();
        }
    }

    private void checkMobileInternetPreferences(){
        if(!preferences.isUse3g()){
            if(ConnectionApi.isMobileNetwork(getActivity())){
                if(!preferences.isRoaming() && ConnectionApi.isRoaming(getActivity())){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle(R.string.attention);
                    alertDialog.setMessage(getString(R.string.attention_roaming));
                    alertDialog.setButton(getString(R.string.continue_anyway), new OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton2(getString(android.R.string.cancel), new OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            getActivity().finish();
                        }
                    });
                    alertDialog.setCancelable(true);
                    alertDialog.show();
                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle(R.string.attention);
                    alertDialog.setMessage(getString(R.string.attention_use_3g));
                    alertDialog.setButton(getString(R.string.continue_anyway), new OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton2(getString(android.R.string.cancel), new OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            getActivity().finish();
                        }
                    });
                    alertDialog.setCancelable(true);
                    alertDialog.show();
                }
            }
        }
    }

}