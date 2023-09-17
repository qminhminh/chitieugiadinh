package com.example.chi_tieu_giadinh.chitieu;

import static java.lang.Integer.parseInt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.model.ExpenseModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.util.Date;


public class DashBoardFragment extends Fragment {
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;
    private TextView fab_income_txt;
    private TextView fab_expense_txt,notice;
    private boolean isOpen = false;
    private Animation FadOpen, FadClose;
   DatabaseReference mIncomeDatabase;
    DatabaseReference mExpenseDatabase;
    private FirebaseAuth mauth;
    private Preferencemanager preferencemanager;
    // dash income and expense result
    private TextView totalIncomeResult;
    private TextView totalExpenseResult;
    private TextView totaldu;
    private RecyclerView mRecycleIncome;
    private RecyclerView mRecycleExpense;

    private FirebaseRecyclerAdapter<ExpenseModel, IncomeViewholder> adapterIncome;
    private FirebaseRecyclerAdapter<ExpenseModel, ExpenseViewHolder> adapterExpense;
    int totaldu_das=0;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dash_board, container, false);
        totaldu=view.findViewById(R.id.dash_money_du);
        notice=view.findViewById(R.id.note_dash);
        mauth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mauth.getCurrentUser();
        String uid = mUser.getUid();
        preferencemanager = new Preferencemanager(getActivity());
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child(Constraints.KEY_COLLECTIONS_INCOME).child(uid);
        mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child(Constraints.KEY_COLLECTIONS_EXPENSIVE).child(uid);
        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);
// connect floating button to layout
        fab_main_btn = view.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = view.findViewById(R.id.income_Ft_btn);
        fab_expense_btn = view.findViewById(R.id.expense_Ft_btn);
        //connect floating text
        fab_income_txt = view.findViewById(R.id.income_ft_text);
        fab_expense_txt = view.findViewById(R.id.expense_ft_text);
        // total income and exoense
        totalIncomeResult=view.findViewById(R.id.income_set_result);
        totalExpenseResult=view.findViewById(R.id.expense_set_result);
        //recycle
        mRecycleIncome=view.findViewById(R.id.recycler_income);
        mRecycleExpense=view.findViewById(R.id.recycler_expense);
        setListeners();
        //calculate total income
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            int totalsum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ExpenseModel data=dataSnapshot.getValue(ExpenseModel.class);
                    totalsum+=data.getAmount();
                    String stResult=String.valueOf(totalsum);
                    totalIncomeResult.setText(stResult);
                }
                totalIncomeResult.setText(String.valueOf(totalsum));
                calculateTotalBalance(totalsum,0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //calculator total expense
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
           int totlasum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    ExpenseModel data=dataSnapshot.getValue(ExpenseModel.class);
                    totlasum+= data.getAmount();
                    String strTotalSum=String.valueOf(totlasum);
                    totalExpenseResult.setText(strTotalSum);
                }
                totalExpenseResult.setText(String.valueOf(totlasum));
                calculateTotalBalance(0,totlasum);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //totaldu.setText("hvjjhb");
        //recycler
        LinearLayoutManager layoutManagerIncome=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecycleIncome.setHasFixedSize(true);
        mRecycleIncome.setLayoutManager(layoutManagerIncome);
        FirebaseRecyclerOptions<ExpenseModel> optionsIncome=new FirebaseRecyclerOptions.Builder<ExpenseModel>()
                .setQuery(mIncomeDatabase,ExpenseModel.class)
                .build();
        adapterIncome=new FirebaseRecyclerAdapter<ExpenseModel, IncomeViewholder>(optionsIncome) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewholder holder, int position, @NonNull ExpenseModel model) {

                holder.setIncomeType(model.getType());
                holder.setIncomeAmmount(model.getAmount());
                holder.setIncomeDate(model.getDate());
            }

            @NonNull
            @Override
            public IncomeViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income_item,parent,false);
                return new IncomeViewholder(view);
            }
        };
        mRecycleIncome.setAdapter(adapterIncome);
        //=======================

        LinearLayoutManager layoutManagerExpense=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecycleExpense.setHasFixedSize(true);
        mRecycleExpense.setLayoutManager(layoutManagerExpense);
        FirebaseRecyclerOptions<ExpenseModel> optionsExpense=new FirebaseRecyclerOptions.Builder<ExpenseModel>()
                .setQuery(mExpenseDatabase,ExpenseModel.class)
                .build();
        adapterExpense=new FirebaseRecyclerAdapter<ExpenseModel, ExpenseViewHolder>(optionsExpense) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull ExpenseModel model) {

                holder.setExpenseType(model.getType());
                holder.setExpenseAmmount(model.getAmount());
                holder.setExpenseDate(model.getDate());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense_item,parent,false);
                return new ExpenseViewHolder(view);
            }
        };
        mRecycleExpense.setAdapter(adapterExpense);
        return view;
    }
    private void calculateTotalBalance(int totalIncome,int totalExpense) {
        int totalExpensea = Integer.parseInt(totalExpenseResult.getText().toString());
        int totalBalance = Integer.parseInt(totalIncomeResult.getText().toString());
        int a=totalBalance-totalExpensea;
        if(a<0){
            notice.setText("Bạn đã sài quá mức");
            notice.setVisibility(View.VISIBLE);
            notice.setTextColor(Color.RED);
        }
        else {
            notice.setText("");
            notice.setVisibility(View.GONE);
        }
        totaldu.setText(String.valueOf(a));
    }
    public void incomeDataInsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);

       final AlertDialog dialog = mydialog.create();
        //Khi cancelable được đặt thành false,
        // có nghĩa là người dùng không thể huỷ dialog bằng cách nhấn phím "Back" trên thiết bị hoặc nhấn bên ngoài dialog.
       dialog.setCancelable(false);
        EditText editAmount = myview.findViewById(R.id.ammount_edit);
        EditText editType = myview.findViewById(R.id.type_edit);
        EditText editNote = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancle);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    editType.setError("Bạn chưa nhập lọai danh mục");
                    return;
                }

                if (TextUtils.isEmpty(amount)) {
                    editType.setError("Bạn chưa nhập số tiền");
                    return;
                }
                int ourammountint = parseInt(amount);
                if (TextUtils.isEmpty(note)) {
                    editNote.setError("Bạn chưa nhập ghi chú");
                    return;
                }
               String id=mIncomeDatabase.push().getKey();
                String mDate= DateFormat.getDateInstance().format(new Date());
                ExpenseModel data = new ExpenseModel(ourammountint, type, note, id, mDate);
                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Sucees add Income", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void expenseDataInsert(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View mview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(mview);
       final AlertDialog dialog = mydialog.create();
       //Khi cancelable được đặt thành false,
        // có nghĩa là người dùng không thể huỷ dialog bằng cách nhấn phím "Back" trên thiết bị hoặc nhấn bên ngoài dialog.
        dialog.setCancelable(false);
        EditText ammount=mview.findViewById(R.id.ammount_edit);
        EditText type=mview.findViewById(R.id.type_edit);
        EditText note=mview.findViewById(R.id.note_edit);

        Button btnSave=mview.findViewById(R.id.btnSave);
        Button btnCancle=mview.findViewById(R.id.btnCancle);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tmAmmount=ammount.getText().toString().trim();
                String tmType=type.getText().toString().trim();
                String tmmote=note.getText().toString().trim();

                if(TextUtils.isEmpty(tmAmmount)){
                    ammount.setError("error..");return;
                }
                if (TextUtils.isEmpty(tmType)){
                    type.setError("error..");return;
                }
                if (TextUtils.isEmpty(tmmote)){
                    note.setError("error..");return;
                }
                int inamount= parseInt(tmAmmount);
                String id=mExpenseDatabase.push().getKey();
                String mDate=DateFormat.getDateInstance().format(new Date());
                ExpenseModel data = new ExpenseModel(inamount, tmType, tmmote, id, mDate);
                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    // floating button
    private void ftAnimation(){
        if (isOpen) {
            fab_income_btn.startAnimation(FadClose);
            fab_expense_btn.startAnimation(FadClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadClose);
            fab_expense_txt.startAnimation(FadClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;
        } else {
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;
        }

    }
    private void addData() {
        //fab button income
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDataInsert();
            }
        });
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDataInsert();
            }
        });
    }

    private void setListeners() {
        FadOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);
        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
                if (isOpen) {
                    fab_income_btn.startAnimation(FadClose);
                    fab_expense_btn.startAnimation(FadClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadClose);
                    fab_expense_txt.startAnimation(FadClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;
                } else {
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;
                }

            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        adapterIncome.startListening();
        adapterExpense.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterIncome.stopListening();
        adapterExpense.stopListening();
    }
    // for income data
    public static class IncomeViewholder extends RecyclerView.ViewHolder{

        View mItemIcome;
        public IncomeViewholder(@NonNull View itemView) {
            super(itemView);
            mItemIcome=itemView;
        }
        public void setIncomeType(String type){
            TextView mtype=mItemIcome.findViewById(R.id.type_income_ds);
            mtype.setText(type);
        }
        public void setIncomeAmmount(int ammount){
            TextView mAmmount=mItemIcome.findViewById(R.id.ammount_income_ds);
            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);
        }
        public void setIncomeDate(String date){
            TextView mDate=mItemIcome.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }
    }


    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mItemExpense;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemExpense=itemView;
        }
        public void setExpenseType(String type){
            TextView mType=mItemExpense.findViewById(R.id.type_expense_ds);
            mType.setText(type);
        }
        public void setExpenseAmmount(int ammount){
            TextView mAmount=mItemExpense.findViewById(R.id.ammount_expense_ds);
            String trAmmount=String.valueOf(ammount);
            mAmount.setText(trAmmount);
        }
        public void setExpenseDate(String date){
            TextView mDate=mItemExpense.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }
    }
}
