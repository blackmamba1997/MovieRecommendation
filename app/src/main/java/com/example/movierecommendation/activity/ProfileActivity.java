package com.example.movierecommendation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.example.movierecommendation.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    BarChart barChart;
    FirebaseFirestore db;
    TextView emailText;
    int genrecount = 0;
    List<BarEntry> barEntries;
    String[] genresname={"Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary", "Drama", "Family", "Fantasy", "History", "Horror", "Music", "Mystery", "Romance", "Science Fiction", "TV Movie", "Thriller", "War", "Western"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        emailText=findViewById(R.id.profile_email);
        barChart = findViewById(R.id.search_graph);
        barEntries = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        DocumentReference userdoc = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userdoc.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            DocumentSnapshot snapshot = task.getResult();
                            emailText.setText(snapshot.getString("email"));
                            for (int i = 0; i < genresname.length; i++) {
                                int count = Integer.parseInt(snapshot.getString("genre." + genresname[i]));
                                System.out.println(count);
                                genrecount += count;

                            }
                            for (int i = 0; i < genresname.length; i++) {
                                int count = Integer.parseInt(snapshot.getString("genre." + genresname[i]));
                                float f=(count*1.0f/genrecount)*100f;
                                barEntries.add(new BarEntry(i,f));

                            }
                            BarDataSet dataSet=new BarDataSet(barEntries,"Percentage");
                            dataSet.setColor(Color.parseColor("#fed268"));
                            BarData data=new BarData(dataSet);
                            data.setValueTypeface(Typeface.DEFAULT_BOLD);
                            data.setValueTextSize(9);
                            data.setValueTextColor(Color.parseColor("#ffd369"));
                            List<String> g=Arrays.asList(genresname);
                            barChart.setFitBars(true);
                            XAxis xAxis=barChart.getXAxis();
                            xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
                            xAxis.setDrawAxisLine(false);
                            xAxis.setDrawGridLines(false);
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(g));
                            xAxis.setGranularity(1f);
                            xAxis.setGranularityEnabled(true);
                            xAxis.setTypeface(Typeface.DEFAULT_BOLD);
                            barChart.getXAxis().setTextColor(Color.parseColor("#ffd369"));
                            barChart.getAxisLeft().setTextColor(Color.parseColor("#ffd369"));
                            barChart.getAxisLeft().setTypeface(Typeface.DEFAULT_BOLD);
                            barChart.getLegend().setTextColor(Color.parseColor("#ffd369"));
                            barChart.getDescription().setText("Genres");
                            barChart.getDescription().setTextColor(Color.parseColor("#ffd369"));
                            barChart.setData(data);
                            barChart.invalidate();


                        }
                    }
                });
    }
}
