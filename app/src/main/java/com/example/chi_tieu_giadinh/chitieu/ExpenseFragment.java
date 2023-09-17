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


public class ExpenseFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
    private RecyclerView recyclerView;
    ArrayList<ExpenseModel> arrayList;
    private TextView expensetextTotalSum;
    private FirebaseRecyclerAdapter<ExpenseModel, MyViewholerrr> adapter;
    // edit data item
    private EditText edtAmmount,edtType,edtNote;
    private Button btnUpdate,btnDelete;
    // data variable
    private String type,note;
    private int ammount;
    private String pos_key;
    BarChart barChartExpense;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mview= inflater.inflate(R.layout.fragment_expense, container, false);
        // bar char
        barChartExpense=mview.findViewById(R.id.bartCharExpense);
        //
        expensetextTotalSum=mview.findViewById(R.id.expense_txt_result);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child(Constraints.KEY_COLLECTIONS_EXPENSIVE).child(uid);
        recyclerView = mview.findViewById(R.id.recycview_id_expense);
        arrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseRecyclerOptions<ExpenseModel> options=new FirebaseRecyclerOptions.Builder<ExpenseModel>()
                .setQuery(mExpenseDatabase,ExpenseModel.class)
                        .build();
        adapter=new FirebaseRecyclerAdapter<ExpenseModel, MyViewholerrr>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewholerrr holder, @SuppressLint("RecyclerView") int position, @NonNull ExpenseModel model) {
                holder.setType(model.getType());
                holder.setDate(model.getDate());
                holder.setNote(model.getNote());
                holder.setAmmount(String.valueOf(model.getAmount()));
                holder.mmiew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pos_key=getRef(position).getKey();
                        type= model.getType();
                        note= model.getNote();
                        ammount=model.getAmount();
                        updateDataitem();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewholerrr onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
              View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycle_data,parent,false);
                return new MyViewholerrr(view);
            }
        };
        recyclerView.setAdapter(adapter);
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalExpense = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ExpenseModel data = dataSnapshot.getValue(ExpenseModel.class);
                    int amount = data.getAmount();
                    String date = data.getDate();
                    totalExpense += amount;

                    String stTotalValue = String.valueOf(totalExpense);
                    expensetextTotalSum.setText(stTotalValue + "đ");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return mview;
    }
    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChartExpense = view.findViewById(R.id.bartCharExpense);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalValue = 0;
                HashMap<String, Integer> dailyTotalMap = new HashMap<>();

                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();
                ArrayList<Integer> colors = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ExpenseModel data = dataSnapshot.getValue(ExpenseModel.class);
                    totalValue += data.getAmount();
                    String date = data.getDate();
                    int color = getColorForDate(date); // Replace with your custom logic
                    colors.add(color);

                    if (dailyTotalMap.containsKey(date)) {
                        int previousTotal = dailyTotalMap.get(date);
                        dailyTotalMap.put(date, previousTotal + data.getAmount());
                    } else {
                        dailyTotalMap.put(date, data.getAmount());
                        labels.add(date);
                    }
                }

                int index = 0;
                for (String date : labels) {
                    int amount = dailyTotalMap.get(date);
                    entries.add(new BarEntry(index, amount));
                    index++;
                }
               // colors.add(color);
                String stTotalValue = String.valueOf(totalValue);
                expensetextTotalSum.setText(stTotalValue + "đ");

                BarDataSet barDataSet = new BarDataSet(entries, "Visitors");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(14f);
                barDataSet.setColors(colors);


// Đặt mảng màu sắc cho các cột


                BarData barData = new BarData(barDataSet);
                barChartExpense.setFitBars(true);
                barChartExpense.setData(barData);
                float barWidth = 0.3f; // Đặt chiều rộng cột
                barData.setBarWidth(barWidth);

                // Đặt các nhãn ngày lên cột BarChart
                XAxis xAxis = barChartExpense.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1);
                xAxis.setLabelCount(labels.size());

                barChartExpense.getDescription().setText("Income Total");
                barChartExpense.animateY(2000);
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
    public static class MyViewholerrr extends RecyclerView.ViewHolder{

        View mmiew;
        public MyViewholerrr(@NonNull View itemView) {
            super(itemView);
            mmiew=itemView;
        }
        public void setType(String type){
            TextView mType=mmiew.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }
        public void setNote(String note){
            TextView mNote=mmiew.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }
        public void setDate(String date){
            TextView mDate=mmiew.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
        public void setAmmount(String ammount){
            TextView mAmmount=mmiew.findViewById(R.id.ammount_txt_expense);
            mAmmount.setText(ammount);
        }
    }
    private void updateDataitem(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View mview=inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(mview);
        edtAmmount=mview.findViewById(R.id.ammount_edit);
        edtNote=mview.findViewById(R.id.note_edit);
        edtType=mview.findViewById(R.id.type_edit);

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());

        btnUpdate=mview.findViewById(R.id.btn_upd_Update);
        btnDelete=mview.findViewById(R.id.btnuPD_delete);
        AlertDialog dialog=mydialog.create();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();
                String stammount=String.valueOf(ammount);
                stammount=edtAmmount.getText().toString().trim();
                int intamount=Integer.parseInt(stammount);
                String mDate= DateFormat.getDateInstance().format(new Date());
                ExpenseModel data=new ExpenseModel(intamount,type,note,pos_key,mDate);
                mExpenseDatabase.child(pos_key).setValue(data);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExpenseDatabase.child(pos_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}