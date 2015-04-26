package com.yask.android.photorocket;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PastEventsFragment extends Fragment {

    public EventListAdapter eventListAdapter;

    public PastEventsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("parse past events", "create view");
        View rootView = inflater.inflate(R.layout.fragment_past_events, container, false);
        getActivity().setTitle("Past Events");
        eventListAdapter = new EventListAdapter(this.getActivity(),PastEventsFragment.this,true);
        eventListAdapter.setTextKey(Event.NAME_KEY);
        final ListView eventListView = (ListView) rootView.findViewById(R.id.listview_past_events);
        if (eventListView == null){
            Log.d("parse", "listView null");
        }
        eventListView.setAdapter(eventListAdapter);

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = eventListAdapter.getItem(position);
                Intent intent = new Intent(view.getContext(),EventDetailActivity.class)
                        .putExtra(Event.ID_TEXT,event.getObjectId())
                        .putExtra(Event.ISFUTURE_TEXT,event.isFuture())
                        .putExtra(Event.ISPAST_TEXT,event.isPast())
                        .putExtra(Event.NAME_KEY, event.getEventName());
                startActivity(intent);
            }
        });

        return rootView;
    }
}
