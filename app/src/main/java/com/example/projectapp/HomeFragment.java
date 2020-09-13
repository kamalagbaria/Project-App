package com.example.projectapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private DatabaseReference DatabaseRef;
    private FirebaseListAdapter Adapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public FirebaseAuth mAuth;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //changed
        mAuth = FirebaseAuth.getInstance();
        //end change
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button askBtn = view.findViewById(R.id.askBtn);
        //start change
        if (mAuth.getCurrentUser() == null){
            askBtn.setVisibility(View.INVISIBLE);
        }
        //end change
        askBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SubmitQuestionActivity.class));
            }
        });

        ListView listView = view.findViewById(R.id.questionsLV);

        //get instance of database
        DatabaseRef = FirebaseDatabase.getInstance().getReference().child("questions");

        Adapter = new FirebaseListAdapter<Question>(getActivity(), Question.class, android.R.layout.two_line_list_item, DatabaseRef) {
            @Override
            protected void populateView(View view, Question question, final int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(question.getTitle());
                ((TextView)view.findViewById(android.R.id.text2)).setText(question.getContent());

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getActivity(), QuestionDetailActivity.class);
                        i.putExtra("question", (Serializable) Adapter.getItem(position));
                        i.putExtra("question_key", Adapter.getRef(position).getKey());
                        startActivity(i);
                    }
                });

            }
        };
        listView.setAdapter(Adapter);
        return view;
    }
}