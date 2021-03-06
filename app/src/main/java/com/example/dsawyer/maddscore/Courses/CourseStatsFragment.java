package com.example.dsawyer.maddscore.Courses;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.CardObject;
import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Objects.Player;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Stats.PiChartPercentFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CourseStatsFragment extends Fragment implements View.OnClickListener {

    private DatabaseReference ref;
    private String UID;
    private Course course;

    ImageView back_arrow;
    TextView scoreAVG, aces, eagles;

    private int endScoreAVG;

    ArrayList<Player> myPlays;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scoreAVG = view.findViewById(R.id.score_avg);
        aces = view.findViewById(R.id.num_aces);
        eagles = view.findViewById(R.id.num_eagles);
        back_arrow = view.findViewById(R.id.back_btn);
        back_arrow.setOnClickListener(this);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if ((course = getCourseFromBundle()) != null) {
            getPlayerRounds();
        }
    }

    public void getPlayerRounds() {
        ArrayList<String> cardIds = new ArrayList<>();
        ArrayList<CardObject> cardObjects = new ArrayList<>();
        Query query = ref.child("scorecards").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        cardIds.add(ds.getKey());
                    }

                    if (!cardIds.isEmpty()) {
                        Query cardObjectQuery;
                        for (int i = 0; i < cardIds.size(); i++) {
                            cardObjectQuery = ref.child("cardObjects").child(cardIds.get(i));
                            cardObjectQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        cardObjects.add(dataSnapshot.getValue(CardObject.class));
                                        filterCards(cardObjects);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void filterCards(ArrayList<CardObject> cardObjects) {
        List<CardObject> cards = cardObjects
                .stream().filter(cardObject -> !cardObject.getCourseID().equals(course.getCourseId()))
                .collect(Collectors.toList());

        myPlays = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            for (int j = 0; j < cards.get(i).getPlayers().size(); j++) {
                if (cards.get(i).getPlayers().get(j).getUserId().equals(UID))
                    myPlays.add(cards.get(i).getPlayers().get(j));
            }
        }

//        setLineChart();
    }

//    private void setLineChart() {
//
//        for (int i = 0; i < myPlays.size(); i++) {
//            for (int j = 0; j < myPlays.get(i).getHoleScores().size(); j++) {
//
//            }
//        }
//        scoreAVG.setText(String.valueOf(stats.getScoreAVG()));
//
//        LineChart chart = findViewById(R.id.chart1);
//
//        scorecards.sort(Comparator.comparing(CardObject::getDateCreated));
//
//        ArrayList<Entry> yValues = new ArrayList<>();
//        for (int i = 0; i < scorecards.size(); i++) {
//            for (int k = 0; k < scorecards.get(i).getPlayers().size(); k++) {
//                if (scorecards.get(i).getPlayers().get(k).getUserId().equals(UID)) {
//                    yValues.add(new Entry(i, scorecards.get(i).getPlayers().get(k).getTotal()));
//                }
//            }
//        }
//
//        LineDataSet set1;
//        if (scorecards.size() > 20)
//            set1 = new LineDataSet(yValues, "Your last 20 rounds");
//        else
//            set1 = new LineDataSet(yValues, "Your last " + scorecards.size() + " rounds");
//
//        set1.setLineWidth(3);
//        set1.setCircleRadius(5);
//        set1.setCircleHoleRadius(2);
//        set1.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        set1.setCircleColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        set1.setDrawValues(false);
//
//        ArrayList<Entry> zeroAxis = new ArrayList<>();
//        for (int i = 0; i < scorecards.size(); i++)
//            zeroAxis.add(new Entry(i, 0));
//        LineDataSet set2 = new LineDataSet(zeroAxis, "Even");
//
//        set2.setDrawCircles(false);
//        set2.setDrawCircleHole(false);
//        set2.setColor(ContextCompat.getColor(this, R.color.blueButton));
//        set2.setDrawValues(false);
//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1);
//        dataSets.add(set2);
//        LineData data = new LineData(dataSets);
//        ((LineDataSet) data.getDataSetByIndex(0)).setCircleColorHole(Color.BLACK);
//        chart.setData(data);
//        chart.setExtraOffsets(10, 10, 10, 10);
//
//        chart.getXAxis().setValueFormatter((value, axis) -> String.valueOf((int) value));
//        chart.getXAxis().setGranularityEnabled(true);
//        chart.getXAxis().setTextSize(14);
//
//        chart.getAxisLeft().setValueFormatter((value, axis) -> String.valueOf((int) value));
//        chart.getAxisLeft().setGranularityEnabled(true);
//        chart.getAxisLeft().setTextSize(14);
//
//        chart.getAxisRight().setValueFormatter((value, axis) -> String.valueOf((int) value));
//        chart.getAxisRight().setGranularityEnabled(true);
//        chart.getAxisRight().setTextSize(14);
//
//        chart.getDescription().setEnabled(false);
//        chart.setTouchEnabled(false);
//        chart.setDragEnabled(false);
//        chart.setScaleEnabled(false);
//        chart.getLegend().setEnabled(true);
//        chart.getLegend().setTextSize(12);
//        chart.getLegend().setFormSize(16);
//        chart.getLegend().setXEntrySpace(20f);
//        chart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.white));
//        chart.getLegend().setForm(Legend.LegendForm.CIRCLE);
//        chart.animateX(500);
//        chart.setBackgroundColor(ContextCompat.getColor(this, R.color.PrimaryBackground));
//        chart.getXAxis().setTextColor(Color.WHITE);
//        chart.getAxisLeft().setTextColor(Color.WHITE);
//        chart.getAxisRight().setTextColor(Color.WHITE);
//    }
//
//    private void setUpPieChart() {
//        final String[] score_classification = {"Birdies or less", "Pars", "Bogies", "Doubles +"};
//        List<PieEntry> pieEntries = new ArrayList<>();
//
//        if (stats.getHoleInOnes() != 0 || stats.getEagles() != 0 || stats.getBirdies() != 0)
//            pieEntries.add(new PieEntry( (float) ( ((stats.getHoleInOnes() + stats.getEagles() + stats.getBirdies() - stats.getEagleAces()) * 100) / stats.getHolesThrown()), score_classification[0]));
//        if (stats.getPars() != 0)
//            pieEntries.add(new PieEntry( (float) ( (stats.getPars() * 100) / stats.getHolesThrown()), score_classification[1]));
//        if (stats.getBogies() != 0)
//            pieEntries.add(new PieEntry( (float) ( (stats.getBogies() * 100) / stats.getHolesThrown()), score_classification[2]));
//        if (stats.getDoublePlus() != 0)
//            pieEntries.add(new PieEntry( (float) ( (stats.getDoublePlus() * 100) / stats.getHolesThrown()), score_classification[3]));
//
//        PieDataSet dataSet = new PieDataSet(pieEntries, "");
//        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//        dataSet.setSliceSpace(2);
//        PieData data = new PieData(dataSet);
//        data.setValueTextColor(Color.WHITE);
//        data.setValueFormatter(new PiChartPercentFormatter());
//        data.setValueTextSize(18);
//
//        PieChart chart = findViewById(R.id.chart2);
//        chart.setData(data);
//        chart.setExtraOffsets(10, 10, 10, 10);
//        chart.animateY(1000);
//        chart.setHoleRadius(20);
//        chart.setTransparentCircleRadius(0f);
//        chart.setHoleColor(ContextCompat.getColor(this, R.color.PrimaryBackground));
//        chart.setDrawEntryLabels(false);
//        chart.setRotationEnabled(false);
//
//        chart.getDescription().setEnabled(false);
//        chart.getLegend().setEnabled(true);
//        chart.getLegend().setTextSize(12);
//        chart.getLegend().setFormSize(16);
//        chart.getLegend().setXEntrySpace(20f);
//        chart.getLegend().setTextColor(Color.WHITE);
//        chart.getLegend().setWordWrapEnabled(true);
//        chart.getLegend().setForm(Legend.LegendForm.CIRCLE);
//        chart.setBackgroundColor(ContextCompat.getColor(this, R.color.PrimaryBackground));
//        chart.invalidate();
//    }
//
//    public void setUpBarGraph() {
//        BarChart chart = findViewById(R.id.chart3);
//        ArrayList<BarEntry> barEntries = new ArrayList<>();
//        final ArrayList<String> score_classification = new ArrayList<>(Arrays.asList("Birdies", "Pars", "Bogies", "Double +"));
//
//        barEntries.add(new BarEntry(0, stats.getBirdies()));
//        barEntries.add(new BarEntry(1, stats.getPars()));
//        barEntries.add(new BarEntry(2, stats.getBogies()));
//        barEntries.add(new BarEntry(3, stats.getDoublePlus()));
//
//        BarDataSet dataSet = new BarDataSet(barEntries, "");
//        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
//        BarData data = new BarData(dataSet);
//        data.setValueTextColor(Color.WHITE);
//        data.setValueFormatter(((value, entry, dataSetIndex, viewPortHandler) -> String.valueOf((int) value)));
//        data.setValueTextSize(18);
//
//        chart.setData(data);
//        chart.setExtraOffsets(10, 10, 10, 40);
//        chart.animateY(1000);
//        chart.setFitBars(true);
//        chart.getXAxis().setDrawLabels(true);
//        chart.getXAxis().setLabelRotationAngle(-45f);
//        chart.getXAxis().setGranularityEnabled(true);
//        chart.getXAxis().setTextSize(14);
//        chart.getXAxis().setTextColor(Color.WHITE);
//        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//        chart.getAxisRight().setEnabled(false);
//        chart.getXAxis().setDrawGridLines(false);
//        chart.getAxisLeft().setTextSize(14);
//        chart.getAxisLeft().setTextColor(Color.WHITE);
//
//        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                if (value >= 0) {
//                    if (value <= score_classification.size() - 1)
//                        return score_classification.get((int) value);
//                }
//                return "";
//            }
//        });
//        chart.setTouchEnabled(false);
//        chart.getDescription().setEnabled(false);
//        chart.getXAxis().setEnabled(true);
//        chart.getAxisLeft().setDrawLabels(true);
//        chart.getAxisRight().setDrawLabels(true);
//
//        chart.getLegend().setEnabled(false);
//        chart.getLegend().setTextColor(Color.WHITE);
//        chart.getLegend().setWordWrapEnabled(true);
//        chart.getLegend().setForm(Legend.LegendForm.CIRCLE);
//        chart.setBackgroundColor(ContextCompat.getColor(this, R.color.PrimaryBackground));
//        chart.invalidate();
//
//        aces.setText(String.valueOf(stats.getHoleInOnes()));
//        eagles.setText(String.valueOf(stats.getEagles()));
//    }

    public Course getCourseFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null)
            return bundle.getParcelable("course");
        else
            return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.back_arrow):
                if (getActivity() != null)
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
        }
    }
}



















