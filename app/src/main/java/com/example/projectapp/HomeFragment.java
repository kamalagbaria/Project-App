package com.example.projectapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private DatabaseReference DatabaseRef;
    private FirebaseListAdapter Adapter;
    private ListView lv;
    private ListView lv2;
    private HomeQuestionAdapter adapter;
    private HomeQuestionAdapter arrayAdapter;

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
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        showNewlyAddedLists(view);
        showLastViewedList(view);
        return view;
    }

    public void showLastViewed(final ArrayList<QuestionWrapper>questionWrappers, ArrayList<Question> questions, View view){
        lv = (ListView) view.findViewById(R.id.LastViewedQuestions);
        arrayAdapter = new HomeQuestionAdapter(view.getContext(),0,questions);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), QuestionDetailActivity.class);
                intent.putExtra("question", arrayAdapter.getItem(i));
                intent.putExtra("question_key", questionWrappers.get(i).getKey());
                startActivity(intent);

            }
        });
    }

    public  void showNewlyAdded(final ArrayList<QuestionWrapper> wrappers, ArrayList<Question>questions, View view){
        lv2=view.findViewById(R.id.NewlyAddedQuestions);
         adapter =new HomeQuestionAdapter(view.getContext(),0,questions);
        lv2.setAdapter(adapter);
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), QuestionDetailActivity.class);
                intent.putExtra("question", adapter.getItem(i));
                intent.putExtra("question_key", wrappers.get(i).getKey());
                startActivity(intent);

            }
        });
    }

    public void showNewlyAddedLists(final View view){
        FirebaseDatabase.getInstance().getReference().child("questions").orderByChild("questionUploadTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<QuestionWrapper> wrappersList = new ArrayList<>();
                ArrayList<Question>  questionsList=new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()) {
                    Question question=ds.getValue(Question.class);
                    QuestionWrapper wrapper = new QuestionWrapper(question,ds.getKey());
                    wrappersList.add(wrapper);
                    questionsList.add(question);
                }
                int last=10;
                if(wrappersList.size()<10){
                    last=wrappersList.size();
                }
                Collections.reverse(wrappersList.subList(0,last));
                Collections.reverse(questionsList.subList(0,last));
                showNewlyAdded(wrappersList,questionsList,view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    public void showLastViewedList(final View view){
        if (mAuth.getCurrentUser() != null){
            FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user=dataSnapshot.getValue(User.class);
                    if(user!=null){

                        ArrayList<QuestionWrapper> questionWrappers=user.getLastViewed();
                        ArrayList<Question> questions=new ArrayList<>();
                        for (QuestionWrapper questionWrapper:questionWrappers){
                            questions.add(questionWrapper.getQuestion());
                        }
                        int last=10;
                        if(questionWrappers.size()<10){
                            last=questionWrappers.size();
                        }
                        //Collections.reverse(questionWrappers.subList(0,last));
                        //Collections.reverse(questions.subList(0,last));
                        showLastViewed(new ArrayList<>(questionWrappers.subList(0, last)), new ArrayList<>(questions.subList(0, last)),view);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showLastViewedList(getView());
    }
}