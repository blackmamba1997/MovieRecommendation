package com.example.movierecommendation;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.movierecommendation.activity.MovieActivity;
import com.example.movierecommendation.activity.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NotificationService extends Service {

    String[] genresname = {"Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary", "Drama", "Family", "Fantasy", "History",
            "Horror", "Music", "Mystery", "Romance", "Science Fiction", "TV Movie", "Thriller", "War", "Western"};

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        System.out.println("inside onCommand");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference ref = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.getResult().contains("search_counter") && task.getResult().getString("search_counter").equals("2")) {
                            int freq[] = new int[genresname.length];
                            String genres = "";
                            int min = Integer.MAX_VALUE;
                            int c = 0;
                            for (int i = 0; i < freq.length; i++) {
                                freq[i] = Integer.parseInt(task.getResult().getString("genre." + genresname[i]));
                                if (freq[i] < min) {
                                    min = freq[i];
                                }
                            }
                            for (int i = 0; i < freq.length; i++) {
                                System.out.println(genresname[i]+" "+freq[i]);
                                if (freq[i] == min && c == 0) {
                                    genres += genresname[i];
                                    c++;
                                } else if (freq[i] == min && c != 0) {
                                    c++;
                                }
                            }
                            System.out.println("the value of c "+c);

                            System.out.println("SERVICE ONCOMPLETE");
                            String email = task.getResult().getString("email");
                            //Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Channel 1");
                            builder.setSmallIcon(R.drawable.ic_launcher_foreground);
                            builder.setContentTitle(email);
                            if (c == 1) {

                                builder.setContentText(genres + " has been least searched. Go to your Profile section for more details");
                                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(genres + " has been least searched. Go to your Profile section for more details"));
                            } else {
                                builder.setContentText(genres + " and others have been least searched. Go to your Profile section for more details");
                                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(genres + " and others have been least searched. Go to your Profile section for more details"));
                            }

                            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            builder.setAutoCancel(true);
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                            builder.setContentIntent(pendingIntent);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                            // notificationId is a unique int for each notification that you must define
                            notificationManager.notify(1997, builder.build());
                        }
                    }
                });
        return Service.START_STICKY;
    }

}
