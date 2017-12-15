package com.mfp.nytsearchmfp.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.mfp.nytsearchmfp.R;
import com.mfp.nytsearchmfp.activity.NYTSearchActivity;
import com.mfp.nytsearchmfp.databinding.ActivityWebViewBinding;
import com.mfp.nytsearchmfp.databinding.FilterLayoutBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by anushree on 9/19/2017.
 */

public class SearchFilterFragment extends DialogFragment {
    Context mCtx;

    Calendar myCalendar;
    EditText begin_date;
    Spinner  sort_spin;
    CheckBox arts;
    CheckBox sports;
    CheckBox fashion;
    CheckBox business;
    CheckBox weekend;
    private Bundle data;
    Button save;
    private FilterLayoutBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCtx = context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.R.style.Theme_Holo_Light_Dialog,getTheme());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.filter_layout,container,false);
        data = getArguments();
        View view = binding.getRoot();
        begin_date = binding.editBegin;
        begin_date.setFocusable(false);
        sort_spin = binding.spinner;
        arts = binding.artsBox;
        sports = binding.sportBox;
        fashion = binding.fashionBox;
        weekend = binding.weekendBox;
        business = binding.businessBox;
        save = binding.save;

        myCalendar = Calendar.getInstance();
        String beginDate = data.getString(NYTSearchActivity.BEGIN_DATE,"");
        if(!beginDate.isEmpty())
        {
            myCalendar.set(Calendar.YEAR,Integer.parseInt(beginDate.split("/")[2]));
            myCalendar.set(Calendar.MONTH, Integer.parseInt(beginDate.split("/")[0])-1);
            myCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(beginDate.split("/")[1]));
        }

        begin_date.setOnClickListener(v -> {
            DatePickerDialog pickerDialog = new DatePickerDialog(mCtx, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            pickerDialog.show();

        });
        ArrayList<String> sortOrderList = new ArrayList<>();
        sortOrderList.add(mCtx.getResources().getString(R.string.old));
        sortOrderList.add(mCtx.getResources().getString(R.string.newest));
        ArrayAdapter<String> sortAdp = new ArrayAdapter<String>(mCtx,android.R.layout.simple_spinner_dropdown_item,sortOrderList);
        sort_spin.setAdapter(sortAdp);
        save.setOnClickListener(view1 -> saveFilter());
        fillDataFromBundle();
        return view;
    }

    private void fillDataFromBundle(){
        begin_date.setText(data.getString(NYTSearchActivity.BEGIN_DATE,""));
        sort_spin.setSelection(data.getInt(NYTSearchActivity.SORT_ORDER));
        arts.setChecked(data.getBoolean(NYTSearchActivity.ARTS,false));
        sports.setChecked(data.getBoolean(NYTSearchActivity.SPORTS,false));
        fashion.setChecked(data.getBoolean(NYTSearchActivity.FASHION,false));
        business.setChecked(data.getBoolean(NYTSearchActivity.BUSINESS,false));
        weekend.setChecked(data.getBoolean(NYTSearchActivity.WEEKEND,false));
    }


    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //date format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        begin_date.setText(sdf.format(myCalendar.getTime()));
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };


    public interface SaveFilterListener {
        void onFilterSaved(Bundle bundle);
    }

    private void saveFilter(){
        //Filling data to bundle before closing the Filter fragment
        data.putString(NYTSearchActivity.BEGIN_DATE,begin_date.getText().toString());
        data.putInt(NYTSearchActivity.SORT_ORDER,sort_spin.getSelectedItemPosition());
        data.putBoolean(NYTSearchActivity.ARTS,arts.isChecked());
        data.putBoolean(NYTSearchActivity.SPORTS,sports.isChecked());
        data.putBoolean(NYTSearchActivity.FASHION,fashion.isChecked());
        data.putBoolean(NYTSearchActivity.BUSINESS,business.isChecked());
        data.putBoolean(NYTSearchActivity.WEEKEND,weekend.isChecked());
        //Log.i(NYTSearchActivity.TAG,"arts = "+arts.isChecked()+" sports = "+sports.isChecked()+" fashion = "+fashion.isChecked());
        //Log.i(NYTSearchActivity.TAG,"SORT = "+sort_spin.getSelectedItemPosition());
        SaveFilterListener listener = (SaveFilterListener) getActivity();
        listener.onFilterSaved(data);
        dismiss();
    }
}
