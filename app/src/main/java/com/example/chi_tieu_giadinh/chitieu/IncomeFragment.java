package com.example.chi_tieu_giadinh.chitieu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.model.ExpenseModel;
import com.example.chi_tieu_giadinh.model.TongIncomeThangModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class IncomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    DatabaseReference mIncomeDatabase;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView_tong;
    ArrayList<ExpenseModel> arrayList;

    private TextView incometextTotalSum;
    private EditText edtAmount, edtType, edtNote;
    private Button btnUpdate, btnDelete;
    //data item value
    private String type,note,pos_key;
    private int ammount;
    BarChart barChartIncome;
    private FirebaseRecyclerAdapter<ExpenseModel, MyViewHolder> adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.fragment_income, container, false);

        barChartIncome=mview.findViewById(R.id.bartCharIncome);
        incometextTotalSum = mview.findViewById(R.id.income_txt_result);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child(Constraints.KEY_COLLECTIONS_INCOME).child(uid);
        recyclerView = mview.findViewById(R.id.recycview_id_income);

        arrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerOptions<ExpenseModel> options =
                new FirebaseRecyclerOptions.Builder<ExpenseModel>()
                        .setQuery(mIncomeDatabase, ExpenseModel.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<ExpenseModel, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ExpenseModel model) {
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.setAmmount(String.valueOf(model.getAmount()));
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pos_key=getRef(position).getKey();
                        type= model.getType();
                        note=model.getNote();
                        ammount=model.getAmount();
                        updateDataItem();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycle_data, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);



        return mview;
    }
    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChartIncome = view.findViewById(R.id.bartCharIncome);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalValue = 0;
                HashMap<String, Integer> dailyTotalMap = new HashMap<>();

                ArrayList<BarEntry> entries = new ArrayList<>();// mục
                ArrayList<String> labels = new ArrayList<>();//nhãn
                ArrayList<Integer> colors = new ArrayList<>();


                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ExpenseModel data = dataSnapshot.getValue(ExpenseModel.class);
                    String date = data.getDate();

                    if (dailyTotalMap.containsKey(date)) {
                        int previousTotal = dailyTotalMap.get(date);
                        dailyTotalMap.put(date, previousTotal + data.getAmount());
                    } else {
                        dailyTotalMap.put(date, data.getAmount());
                    }
                }

                int index = 0;

                for (String date : dailyTotalMap.keySet()) {
                    int amount = dailyTotalMap.get(date);
                    totalValue += amount;

                    entries.add(new BarEntry(index, amount));
                    labels.add(date);
                    int color = getColorForDate(date); // Replace with your custom logic
                    colors.add(color);
                    index++;
                }

                String stTotalValue = String.valueOf(totalValue);
                incometextTotalSum.setText(stTotalValue + "đ");

                BarDataSet barDataSet = new BarDataSet(entries, "Visitors");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(14f);
                barDataSet.setColors(colors);


// Đặt mảng màu sắc cho các cột


                BarData barData = new BarData(barDataSet);
                barChartIncome.setFitBars(true);
                barChartIncome.setData(barData);
                float barWidth = 0.3f; // Đặt chiều rộng cột
                barData.setBarWidth(barWidth);

                // Đặt các nhãn ngày lên cột BarChart
                XAxis xAxis = barChartIncome.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1);
                xAxis.setLabelCount(labels.size());

                barChartIncome.getDescription().setText("Income Total");
                barChartIncome.animateY(2000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private int getColorForDate(String date) {
        // Custom logic to determine the color based on the date
        // You can use a switch statement, if-else conditions, or any other logic

        // Example logic: Assign different colors based on the day of the week
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date dateObj = sdf.parse(date);
            calendar.setTime(dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return Color.RED;
            case Calendar.MONDAY:
                return Color.GREEN;
            case Calendar.TUESDAY:
                return Color.BLUE;
            case Calendar.WEDNESDAY:
                return Color.YELLOW;
            case Calendar.FRIDAY:return  Color.GREEN;
            case Calendar.THURSDAY:return Color.WHITE;
            // Add more cases for other days of the week as needed
            default:
                return Color.BLACK;
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }






    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setType(String type) {
            TextView mType = view.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        public void setNote(String note) {
            TextView mNote = view.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        public void setDate(String date) {
            TextView mDate = view.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        public void setAmmount(String ammount) {
            TextView mAmmount = view.findViewById(R.id.ammount_txt_income);
            mAmmount.setText(ammount);
        }
    }

    private void updateDataItem(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View mview=inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(mview);

        edtAmount=mview.findViewById(R.id.ammount_edit);
        edtType=mview.findViewById(R.id.type_edit);
        edtNote=mview.findViewById(R.id.note_edit);
// set data to edit text
        edtType.setText(type);
        edtType.setSelection(type.length());
        //....
        edtNote.setText(note);
        edtNote.setSelection(note.length());
        //.....
        edtAmount.setText(String.valueOf(ammount));
        edtAmount.setSelection(String.valueOf(ammount).length());
        //....
        btnUpdate=mview.findViewById(R.id.btn_upd_Update);
        btnDelete=mview.findViewById(R.id.btnuPD_delete);
        AlertDialog dialog= mydialog.create();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();
                //.....
                String mdammount=String.valueOf(ammount);
                mdammount=edtAmount.getText().toString().trim();
                int myAmmount=Integer.parseInt(mdammount);
                String mDate= DateFormat.getDateInstance().format(new Date());
                ExpenseModel data=new ExpenseModel(myAmmount,type,note,pos_key,mDate);
                mIncomeDatabase.child(pos_key).setValue(data);
                dialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIncomeDatabase.child(pos_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
