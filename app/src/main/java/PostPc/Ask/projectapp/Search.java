package PostPc.Ask.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView lv2;
    private HomeQuestionAdapter adapter;

    private EditText input;


    public Search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search.
     */
    // TODO: Rename and change types and number of parameters
    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root=inflater.inflate(R.layout.fragment_search, container, false);
        input=root.findViewById(R.id.searchInput);
        MainActivity.setBarText("Search");
        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                getQuesions(root,input.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        return root;
    }


    public  void showSearch(final ArrayList<QuestionWrapper> wrappers, ArrayList<Question>questions, View view){
        lv2=view.findViewById(R.id.searchQuestionsList);
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

    public void getQuesions(final View root, final String inputSearch){

        FirebaseDatabase.getInstance().getReference().child("questions").
                orderByChild("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<QuestionWrapper> wrappersList = new ArrayList<>();
                ArrayList<Question>  questionsList=new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()) {
                    Question question=ds.getValue(Question.class);
                    QuestionWrapper wrapper = new QuestionWrapper(question,ds.getKey());
                    assert question != null;
                    if(question.getTitle().toLowerCase().contains(inputSearch.toLowerCase())){
                        wrappersList.add(wrapper);
                        questionsList.add(question);
                    }
                }
                showSearch(wrappersList,questionsList,root);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}