package PostPc.Ask.projectapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class HomeQuestionAdapter extends ArrayAdapter<Question> {
    private Context context;
    private ArrayList<Question> list ;
    private static LayoutInflater inflater = null;


    public HomeQuestionAdapter(@NonNull Context context, int resource, ArrayList<Question> list) {
        super(context, resource, list);
        try {
            this.context = context;
            this.list = list;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {
        }
    }
    public Question getItem(Question position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.two_line_list_item, null);
                holder = new ViewHolder();

                holder.text1 = (TextView) vi.findViewById(R.id.text1);
                holder.text2 = (TextView) vi.findViewById(R.id.text2);
                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }
            holder.text1.setText(list.get(position).getTitle());
            holder.text2.setText(list.get(position).getContent());

        } catch (Exception e) {


        }
        return vi;
    }

    protected static class ViewHolder{
        protected TextView text1;
        protected TextView text2;
    }
    @Override
    public int getCount(){
        return this.list.size();
    }

}
