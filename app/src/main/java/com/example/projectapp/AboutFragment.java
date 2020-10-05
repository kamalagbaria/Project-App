package com.example.projectapp;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
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

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_about, container, false);
        MainActivity.setBarText("About");
        TextView aboutTtitle=view.findViewById(R.id.aboutTitle);
        aboutTtitle.setText("ASK app");
        TextView aboutText=view.findViewById(R.id.aboutFirstText);
        aboutText.setText("This app was built as a final project for Post PC course at the Hebrew University in Jerusalem, by Kamal Igbarya, Anas Yousef, and Sarah Hegazi. ASK is an app that was made to share and grow its user's knowledge. We aim to connect the people who have the knowledge and ready to share with others who need it. The essence of ASK is questions, any user can ask a question that issues him/her, and the answers are provided by other users who understands the main issue in the question.");
        TextView aboutTitle2=view.findViewById(R.id.aboutTitle2);
        aboutTitle2.setText("Account and Profile Information");
        TextView aboutText2=view.findViewById(R.id.aboutSecondText);
        aboutText2.setText(
                Html.fromHtml("When you create an account and profile on the ASK platform, we collect your name and photo. The information displayed in your profile are only available for you and save on the cloud. \n" +
                        "<br><br><b>Your Content:</b> We collect the information that you post to the ASK platform, including your questions, answers, photos, and comments. These information are viewable by the other users as well. \n",Html.FROM_HTML_MODE_COMPACT));
        return view;
    }
}