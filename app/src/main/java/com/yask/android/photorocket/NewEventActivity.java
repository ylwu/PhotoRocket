package com.yask.android.photorocket;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class NewEventActivity extends ActionBarActivity{
    private String eventName = "";
    private String eventStartDate = "";
    private String eventStartTime = "";
    private String eventEndDate = "";
    private String eventEndTime = "";
    private String eventId = "";

    private boolean isNewEvent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isNewEvent = false;
            eventName = extras.getString(Event.NAME_KEY);
            eventId = extras.getString(Event.ID_TEXT);
            eventStartDate = extras.getString(Event.STARTTIME_KEY).split(" ")[0];
            eventStartTime = extras.getString(Event.STARTTIME_KEY).split(" ")[1];
            eventEndDate = extras.getString(Event.ENDTIME_KEY).split(" ")[0];
            eventEndTime = extras.getString(Event.ENDTIME_KEY).split(" ")[1];
        }

        setContentView(R.layout.activity_new_event);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(isNewEvent, eventName, eventStartDate, eventStartTime, eventEndDate, eventEndTime))
                    .commit();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }


    /*=*/ //use this to save event
    //Helper function to save an Event
    private void saveEvent(String eventName, Date startTime,final Date endTime){
        final Event event = new Event(eventName,startTime,endTime);
        NotificationAlarmReceiver.setAlarm(getApplicationContext(), startTime);

        //event is first saved locally and then saved in cloud
        event.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.d("parse NewEventActivity", "event saved locally");
                    event.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null){
                                Log.e("parse error",e.getLocalizedMessage());
                            } else {
                                UploadAlarmReceiver.setAlarm(getApplicationContext(), endTime, event.getObjectId());
                                String s = (String) ((TextView) findViewById(R.id.invited)).getText();
                                String[] ss = s.split("\n");
                                sendInvitations(Arrays.asList(ss), event.getObjectId());
                                Log.d("parse NewEventActivity", "event saved in cloud");
                            }
                        }
                    });
                }
            }
        });
    }

    /*=*/ //use this to update event
    //Helper function to update an Event
    private void updateEvent(String eventID, String eventName, Date startTime,final Date endTime){
//            Log.d("parse", "loading from parse");
//            ParseQuery query = new ParseQuery("Event");
//            query.whereEqualTo("objectId", eventID);

        // Create a pointer to an object of class Point with id dlkj83d
        ParseObject point = ParseObject.createWithoutData("Event", eventID);

        point.put(Event.NAME_KEY, eventName);
        point.put(Event.STARTTIME_KEY, startTime);
        point.put(Event.ENDTIME_KEY, endTime);

    //  Save
        point.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("parse error", e.getLocalizedMessage());
                } else {
                    UploadAlarmReceiver.setAlarm(getApplicationContext(), endTime, eventId);
                    String s = (String) ((TextView) findViewById(R.id.invited)).getText();
                    String[] ss = s.split("\n");
                    sendInvitations(Arrays.asList(ss), eventId);
                    Log.d("parse NewEventActivity", "event updated in cloud");
                }
            }
        });



//        final Event event = new Event(eventName,startTime,endTime);
//        NotificationAlarmReceiver.setAlarm(getApplicationContext(), startTime);
//
//        //event is first saved locally and then saved in cloud
//        event.pinInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null){
//                    Log.d("parse NewEventActivity", "event updated locally");
//                    event.saveEventually(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e != null){
//                                Log.e("parse error",e.getLocalizedMessage());
//                            } else {
//                                UploadAlarmReceiver.setAlarm(getApplicationContext(), endTime, event.getObjectId());
//                                Log.d("parse NewEventActivity", "event updated in cloud");
//                            }
//                        }
//                    });
//                }
//            }
//        });
    }


    private void sendInvitations(List<String> receipients, String eventID){
        String[] listOfReceipients = receipients.toArray(new String[receipients.size()]);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , listOfReceipients);
        i.putExtra(Intent.EXTRA_SUBJECT, "PhotoRocket: Event Invitation");
        i.putExtra(Intent.EXTRA_TEXT   , Html.fromHtml("Please join my event in PhotoRocket: " + "<a href='http://eventid/" + eventID + "'>" + "http://eventid/" + eventID + "</a>"));
        try {
            startActivityForResult(Intent.createChooser(i, "Send invitations"), 0);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private String name, sd, st, ed, et;
        private boolean isNewEvent;
        public PlaceholderFragment(boolean isNewEvent, String name, String sd, String st, String ed, String et) {
            this.isNewEvent = isNewEvent;
            this.name = name;
            this.sd = sd;
            this.st = st;
            this.ed = ed;
            this.et = et;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_event, container, false);

            // pull data if is updating event
            if (!this.isNewEvent) {
                EditText nameView = (EditText) rootView.findViewById(R.id.eventName);
                nameView.setText(this.name);

                TextView startDateView = (TextView) rootView.findViewById(R.id.startDate);
                startDateView.setText(this.sd);

                TextView startTimeView = (TextView) rootView.findViewById(R.id.startTime);
                startTimeView.setText(this.st);

                TextView endDateView = (TextView) rootView.findViewById(R.id.endDate);
                endDateView.setText(this.ed);

                TextView endTimeView = (TextView) rootView.findViewById(R.id.endTime);
                endTimeView.setText(this.et);

                Button createButton = (Button) rootView.findViewById(R.id.createButton);
                createButton.setText("Update Event");
            }


            if (!haveNetworkConnection()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("You need a network connection to create a new event. Please turn on mobile network or Wi-Fi in Settings.")
                        .setTitle("Unable to Connect")
                        .setCancelable(false)
                        .setPositiveButton("OK", null);
                AlertDialog alert = builder.create();
                alert.show();
            }

            return rootView;
        }

        private boolean haveNetworkConnection() {
            boolean connected = false;
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.isConnected()){
                    connected = true;
                    break;
                }
            }
            return connected;
        }
    }
//----------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------
//  adding an email

    public void addPerson(View v){
        View par = (View) v.getParent();
        EditText newInv = (EditText) par.findViewById(R.id.invites);
        String newInvStr = newInv.getText().toString();
        TextView inv = (TextView) par.findViewById(R.id.invited);
        if (! newInvStr.equals("")) {
            inv.setText(newInvStr + "\n" + inv.getText());
            newInv.setText("");
        } else {
//            inv.setText("[" + eventId + "][" + eventName + "][" + eventStartTime + "]");
        }
    }


//----------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------
// setting start and end time and date

    private static int startHr = -1;
    private static int startMin = -1;


    public void showStartTimePickerDialog(View v) {
        Log.e("showstarttime", "hereherehere");
        StartTimePickerFragment newFragment = new StartTimePickerFragment();
        newFragment.setV(v);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class StartTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private View v;

        public void setV(View v){
            this.v = v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Log.d("starttimedialog", "here");
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hr, int min) {
            Log.d("onstarttimeset", "here");
            TextView st = (TextView) v.findViewById(R.id.startTime);
            startHr = hr;
            startMin = min;
            String strhr;
            String strmin;
            if (hr < 10){
                strhr = "0" + hr;
            } else {
                strhr = "" + hr;
            }
            if (min < 10){
                strmin = "0" + min;
            } else {
                strmin = "" + min;
            }
            st.setText(strhr + ":" + strmin);

        }
    }

    private static int endHr = -1;
    private static int endMin = -1;


    public void showEndTimePickerDialog(View v) {
        EndTimePickerFragment newFragment = new EndTimePickerFragment();
        newFragment.setV(v);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class EndTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private View v;

        public void setV(View v){
            this.v = v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hr, int min) {
            TextView et = (TextView) v.findViewById(R.id.endTime);
            endHr = hr;
            endMin = min;
            String strhr;
            String strmin;
            if (hr < 10){
                strhr = "0" + hr;
            } else {
                strhr = "" + hr;
            }
            if (min < 10){
                strmin = "0" + min;
            } else {
                strmin = "" + min;
            }
            et.setText(strhr + ":" + strmin);
        }
    }


    private static int startYr = -1;
    private static int startMn = -1;
    private static int startDt = -1;


    public void showStartDatePickerDialog(View v) {
        StartDatePickerFragment newFragment = new StartDatePickerFragment();
        newFragment.setV(v);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class StartDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private View v;

        public void setV(View v){
            this.v = v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            TextView sd = (TextView) v.findViewById(R.id.startDate);
            startYr = year;
            startMn = month;
            startDt = day;
            String strmn;
            String strdt;
            if (day < 10){
                strdt = "0" + day;
            } else {
                strdt = "" + day;
            }
            if (month < 9){
                strmn = "0" + (month + 1);
            } else {
                strmn = "" + (month + 1);
            }
            sd.setText(year + "-" + strmn + "-" + strdt);
        }

    }



    private static int endYr = -1;
    private static int endMn = -1;
    private static int endDt = -1;


    public void showEndDatePickerDialog(View v) {
        EndDatePickerFragment newFragment = new EndDatePickerFragment();
        newFragment.setV(v);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class EndDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private View v;

        public void setV(View v){
            this.v = v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            TextView ed = (TextView) v.findViewById(R.id.endDate);
            endYr = year;
            endMn = month;
            endDt = day;
            String strmn;
            String strdt;
            if (day < 10){
                strdt = "0" + day;
            } else {
                strdt = "" + day;
            }
            if (month < 9){
                strmn = "0" + (month + 1);
            } else {
                strmn = "" + (month + 1);
            }
            ed.setText(year + "-" + strmn + "-" + strdt);
        }
    }

//----------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------
// create event
    public void processCreate(View v){
        View par = (View) v.getParent();

        if (startDt < 0 || startMin < 0 || endDt < 0 || endMin < 0){
            //invalid date / time
            showDialog(v, "Invalid Date/Time", "Set a Date/Time");
        } else {
            Calendar scal = new GregorianCalendar(startYr,startMn,startDt,startHr,startMin);
            Date sd =  scal.getTime();
            Calendar ecal = new GregorianCalendar(endYr,endMn,endDt,endHr,endMin);
            Date ed =  ecal.getTime();

            final Date currd = Calendar.getInstance().getTime();

            if (sd.before(currd)){
                showDialog(v, "Invalid Date/Time", "Start Time is at/before Current Time");
            } else if (ed.before(sd)){
                showDialog(v, "Invalid Date/Time", "End Time is before Start Time");
            } else {
                String nameStr = ((EditText) par.findViewById(R.id.eventName)).getText().toString();
                if (nameStr.equals("")){
                    showDialog(v, "Invalid Event Name", "Enter an event name");
                } else {
                    //call save or update
                    if (isNewEvent) {
                        saveEvent(nameStr, sd, ed);
                    }
                    else {
                        updateEvent(this.eventId, nameStr, sd, ed);
                    }

//                    String s = (String) ((TextView) par.findViewById(R.id.invited)).getText();
//                    String[] ss = s.split("\n");
//                    sendInvitations(Arrays.asList(ss));

                }
            }
        }
    }
    public void showDialog (View v, String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
