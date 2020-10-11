package PostPc.Ask.projectapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class SubscriptionToTopic {

    private FirebaseUser user;

    public SubscriptionToTopic()
    {
        //this.user = FirebaseAuth.getInstance().getCurrentUser();
    }



    public void SubscribeToTopic(final String userId)
    {
        FirebaseMessaging.getInstance().subscribeToTopic(Objects.requireNonNull(userId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (!task.isSuccessful())
                        {
                            //Handle If there is error
                        }
                        else {
                            Log.d("Subscribe", "successful");
                            Log.d("ID", userId);
                        }
                    }
                });
    }

    public void unSubscribeToTopic(String userId)
    {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Objects.requireNonNull(userId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (!task.isSuccessful())
                        {
                            //Handle If there is error
                        }
                    }
                });

    }
}
