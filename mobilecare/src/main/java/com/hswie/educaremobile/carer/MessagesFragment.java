package com.hswie.educaremobile.carer;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hswie.educaremobile.R;
import com.hswie.educaremobile.adapter.MessagesAdapter;
import com.hswie.educaremobile.api.dao.CarerMessageRDH;
import com.hswie.educaremobile.api.pojo.CarerMessage;
import com.hswie.educaremobile.dialog.MessageDialog;
import com.hswie.educaremobile.helper.CarerModel;
import com.hswie.educaremobile.helper.PreferencesManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment implements MessagesAdapter.MessagesAdapterCallbacks {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    private RecyclerView messagesRV;
    private TextView emptyTV;
    private MessagesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean asyncTaskWorking = false;

    private Context context;

    public static Handler handler;

    private static ArrayList<CarerMessage> messages;
    private final static String TAG = "MessagesFragment";


    public static MessagesFragment newInstance(int page, String title) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, page);
        args.putString(ARG_PARAM2, title);
        fragment.setArguments(args);
        return fragment;
    }

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        context = getActivity();


        emptyTV = (TextView)rootView.findViewById(R.id.text_empty_list);

        messagesRV = (RecyclerView) rootView.findViewById(R.id.list);
        messagesRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MessagesAdapter(this);
        messagesRV.setAdapter(adapter);
        messagesRV.setItemAnimator(new DefaultItemAnimator());

        checkAdapterIsEmpty();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                downloadMessages();
            }
        };

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    public void onListItemClick(int position) {
        confirmMessage(position);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment oldFragment = getActivity().getSupportFragmentManager().findFragmentByTag("Message Dialog");
        if (oldFragment != null) {
            fragmentTransaction.remove(oldFragment);
        }

        MessageDialog newFragment = MessageDialog.newInstance(adapter.getItem(position));
        newFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EduCareAppDialog);
        newFragment.show(fragmentTransaction, "Message Dialog");


    }

    private void checkAdapterIsEmpty() {
        if (adapter.getItemCount() == 0) {
            emptyTV.setVisibility(View.VISIBLE);
            messagesRV.setVisibility(View.GONE);
        } else {
            emptyTV.setVisibility(View.GONE);
            messagesRV.setVisibility(View.VISIBLE);
        }

        emptyTV.setText(R.string.message_empty);
    }



    private void downloadMessages() {
        if (!asyncTaskWorking)
            new DownloadMessages().execute();
    }

    private void confirmMessage(int position){
        if (!asyncTaskWorking){
            CarerMessage message = adapter.getItem(position);

            if(!message.isRead()){
                new ConfirmMessage().execute(adapter.getItem(position).getId());
                message.setIsRead(true);
            }
        }
    }

    private class DownloadMessages extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            asyncTaskWorking = true;
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            CarerMessageRDH carerMessagesRDH = new CarerMessageRDH();
            String id_mobile_user = String.valueOf(PreferencesManager.getCurrentCarerID());

            CarerModel.get().getCurrentCarer().setCarerMessages(carerMessagesRDH.getCarerMessages(id_mobile_user));

            return null;
        }

        @Override
        protected void onPostExecute(Void bitmap) {
            adapter.notifyDataSetChanged();

            asyncTaskWorking = false;
            checkAdapterIsEmpty();

            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private class ConfirmMessage extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            asyncTaskWorking = true;
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            CarerMessageRDH carerMessagesRDH = new CarerMessageRDH();

            Log.d(TAG, "ConfirmMessage doInBackground params[0] = " + params[0]);
            //return carerMessagesRDH.setIsRead(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            adapter.notifyDataSetChanged();

            asyncTaskWorking = false;
            checkAdapterIsEmpty();

        }
    }

    public static ArrayList<CarerMessage> getMessages() {
        return messages;
    }


}
