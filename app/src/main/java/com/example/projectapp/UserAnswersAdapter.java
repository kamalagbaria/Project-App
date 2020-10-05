package com.example.projectapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAnswersAdapter extends ArrayAdapter<Answer> {
    private Context context;
    private ArrayList<Answer> list ;
    private static LayoutInflater inflater = null;


    public UserAnswersAdapter(@NonNull Context context, int resource, ArrayList<Answer> list) {
        super(context, resource, list);
        try {
            this.context = context;
            this.list = list;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {
        }
    }

    public Answer getItem(Answer position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View vi = convertView;
        final UserAnswersAdapter.ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.answer_layout, null);
                holder = new UserAnswersAdapter.ViewHolder();

                holder.text1 = (TextView) vi.findViewById(R.id.owner_name);
                holder.text2 = (TextView) vi.findViewById(R.id.answerText);
                holder.imageView = vi.findViewById(R.id.answerImage);
                vi.setTag(holder);
            } else {
                holder = (UserAnswersAdapter.ViewHolder) vi.getTag();
            }
            holder.text1.setText(list.get(position).getQuestion_title());
            holder.text2.setText(list.get(position).getText());
            if (list.get(position).getText().equals("")){holder.text2.setVisibility(View.GONE);}
            Answer answer = list.get(position);
            if (answer.getImageUrl()!=null && !answer.getImageUrl().equals("None")){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                storageRef.child("images/Answers/"+answer.getImageUrl()).getDownloadUrl().
                        addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(Uri.parse(uri.toString())).resize(1200, 1600)
                                        .onlyScaleDown().into(holder.imageView);
                                holder.imageView.setVisibility(View.VISIBLE);
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Toast.makeText(getContext(),
                                "Done", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {


        }
        return vi;
    }

    protected static class ViewHolder{
        protected TextView text1;
        protected TextView text2;
        protected ImageView imageView;
    }
    @Override
    public int getCount(){
        return this.list.size();
    }
}