package com.forrest.gymr.gymrfragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.forrest.gymr.MatchingCriteriaActivity;
import com.forrest.gymr.R;
import com.forrest.gymr.utils.TimeUtils;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MatchingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MatchingFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * A Fragment that handles the main Pairing Result / Matching Request Page
 */
public class MatchingFragment extends Fragment {

    private Queue<ParseUser> pairedUsers;
    private List<ParseObject> userRequests;

    private HashSet<String> pairedIds;

    private TextView matchingName;
    private TextView matchingHeight;
    private TextView matchingWeight;
    private TextView matchingBench;
    private TextView matchingSquat;
    private TextView matchingDeadlift;

    private Button acceptButton;
    private Button rejectButton;
    private Button viewFbProfileButton;

    private ParseUser curUser;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MatchingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MatchingFragment newInstance() {
        MatchingFragment fragment = new MatchingFragment();
        return fragment;
    }

    public MatchingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pairedUsers = new LinkedList<>();
        pairedIds = new HashSet<>();
        curUser = null;
    }


    // On Resume method always checks for when does the user previously configured its gym day preference, and if it is more than one day ago, it will prompt the user to configure again.
    @Override
    public void onResume() {
        super.onResume();
        if (TimeUtils.checkConfiguredTimeDayAgo(ParseUser.getCurrentUser())) {
            Intent i = new Intent(this.getActivity(), MatchingCriteriaActivity.class);
            startActivity(i);
        } else {
            List<String> ids = ParseUser.getCurrentUser().getList("pairedToday");
            pairedIds.clear();
            if (ids != null) {
                pairedIds.addAll(ids);
            }
            reloadMatches();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matching, container, false);
        matchingName = (TextView) v.findViewById(R.id.matching_name);
        matchingBench = (TextView) v.findViewById(R.id.matching_bench);
        matchingSquat = (TextView) v.findViewById(R.id.matching_squat);
        matchingDeadlift = (TextView) v.findViewById(R.id.matching_deadlift);
        matchingHeight = (TextView) v.findViewById(R.id.matching_height);
        matchingWeight = (TextView) v.findViewById(R.id.matching_weight);
        acceptButton = (Button) v.findViewById(R.id.matching_confirm_btn);
        rejectButton = (Button) v.findViewById(R.id.matching_cancel_btn);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return v;
    }

    // Triggered when a user result suggestion is accepted
    private void acceptUser() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Match");
        query.whereEqualTo("userTo", ParseUser.getCurrentUser());
        query.whereEqualTo("userFrom", curUser);
        try {
            List<ParseObject> results = query.find();
            results.get(0).put("matched", true);
            results.get(0).save();
        } catch (ParseException e) {
            ParseObject match = new ParseObject("Match");
            match.put("userFrom", ParseUser.getCurrentUser());
            match.put("userTo", curUser);
            match.put("matched", false);
            try {
                match.save();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            updateUIAndDequeue();
            addCurrentPairedUser();
            saveCurrentUser();
            reloadMatches();
            return;
        }
        matchUsersUpdateUI();
    }

    // Triggered when a user result suggestion is rejected
    private void rejectUser() {
        addCurrentPairedUser();
        updateUIAndDequeue();
        saveCurrentUser();
        reloadMatches();
    }

    // Adds the current inspected user into "Paired User" List, such that it is saved into the server that this user will
    // Not appear at the results of the app again for the rest of the day
    private void addCurrentPairedUser() {
        if (curUser != null) {
            ParseUser.getCurrentUser().getList("pairedToday").add(curUser.getObjectId());
            pairedIds.add(curUser.getObjectId());
        }
    }

    // Saves the current user object
    private void saveCurrentUser() {
        try {
            ParseUser.getCurrentUser().save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Updates the UI when two users are matched
    private void matchUsersUpdateUI() {
        acceptButton.setVisibility(View.GONE);
        rejectButton.setVisibility(View.GONE);
        viewFbProfileButton.setVisibility(View.VISIBLE);
    }

    // Creates a facebook "Intent" that launches the facebook app
    private static Intent getOpenFacebookIntent(Context context, long id) {
        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://profile/" + id)); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/" + id)); //catches and opens a url to the desired page
        }
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    // Dequeues one user and updates the UI with the user's data
    private void updateUIAndDequeue() {
        this.curUser = pairedUsers.poll();
        if (curUser != null) {
            matchingName.setText(curUser.getString("name"));
            matchingHeight.setText(getResources().getString(R.string.matching_height_string, Integer.toString(curUser.getInt("height"))));
            matchingWeight.setText(getResources().getString(R.string.matching_weight_string, Integer.toString(curUser.getInt("weight"))));
            matchingSquat.setText(Integer.toString(curUser.getInt("squatWeight")));
            matchingBench.setText(Integer.toString(curUser.getInt("benchWeight")));
            matchingDeadlift.setText(Integer.toString(curUser.getInt("deadliftWeight")));
        } else {
            matchingName.setText("");
            matchingHeight.setText("");
            matchingWeight.setText("");
            matchingSquat.setText("");
            matchingBench.setText("");
            matchingDeadlift.setText("");
        }
    }


    /**
     * Loads the matches from the database based on current body part and the opposite sex preference of the user. (Location Preference coming up)
     */
    private void reloadMatches() {
        pairedUsers.clear();
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("bodyPart", currentUser.get("bodyPart"));
        if (currentUser.get("oppositeSex") == false) {
            query.whereEqualTo("male", currentUser.get("male"));
        }
        query.whereNotEqualTo("objectId", currentUser.getObjectId());
        ParseGeoPoint userLocation = currentUser.getParseGeoPoint("prevLocation");
        double locationPreference = currentUser.getDouble("locationPreference");
        //  query.whereWithinMiles("prevLocation", userLocation, locationPreference);
        try {
            List<ParseUser> parseUsers = query.find();
            addToQueue(parseUsers);
            updateUIAndDequeue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a list of user to the queue, also keeps track of the current paired Id of the day
     *
     * @param parseUsers users fetched from the Parse API
     */
    private void addToQueue(List<ParseUser> parseUsers) {
        for (ParseUser user : parseUsers) {
            if (!pairedIds.contains(user.getObjectId())) {
                pairedIds.add(user.getObjectId()); // This line should be executed when a user confirms/cancels another user's request instead.
                pairedUsers.add(user);
            }
        }
    }


}
