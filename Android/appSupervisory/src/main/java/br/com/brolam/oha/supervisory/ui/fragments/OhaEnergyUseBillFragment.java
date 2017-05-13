package br.com.brolam.oha.supervisory.ui.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.Calendar;
import java.util.Date;

import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.ui.helpers.OhaHelper;
import static br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.*;


/**
 * Exibir um caixa de dialogo com os campos para adicionar ou alterar uma conta de energir.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */public class OhaEnergyUseBillFragment extends DialogFragment implements View.OnClickListener, Toolbar.OnMenuItemClickListener, DatePicker.OnDateChangedListener {
    public static String TAG = OhaEnergyUseBillFragment.class.getName();

    public interface IOhaEnergyUseBillFragment{
        void onSaveEnergyUseBill(int id, long fromDate, long toDate, double kwhCost );
    }

    public static final String PARAM_ID = "param_id";
    private int id;
    public static final String PARAM_FROM_DATE = "param_from_date";
    private long fromDate;
    public static final String PARAM_TO_DATE = "param_to_date";
    private long toDate;
    public static final String PARAM_KWH_COST = "param_kwh_cost";
    double kwhCost;
    int amountDays;

    RadioButton radioButtonFromDate;
    RadioButton radioButtonToDate;
    EditText editKwhCost;
    DatePicker datePicker;
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = savedInstanceState != null ? savedInstanceState : this.getArguments();
        this.id = this.getArguments().getInt(PARAM_ID);
        this.fromDate = bundle.getLong(PARAM_FROM_DATE);
        this.toDate = bundle.getLong(PARAM_TO_DATE);
        this.kwhCost = bundle.getDouble(PARAM_KWH_COST);
        this.amountDays = (int) OhaHelper.getAmountDays(this.fromDate, this.toDate);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PARAM_FROM_DATE, this.fromDate);
        outState.putLong(PARAM_TO_DATE, this.toDate);
        outState.putDouble(PARAM_KWH_COST, this.kwhCost);
    }

    public static OhaEnergyUseBillFragment getInstance(Activity activity){
        OhaEnergyUseBillFragment ohaEnergyUseBillFragment = (OhaEnergyUseBillFragment) activity.getFragmentManager().findFragmentByTag(TAG);
        return (ohaEnergyUseBillFragment == null)? new OhaEnergyUseBillFragment(): ohaEnergyUseBillFragment;
    }

    public static void add(Activity activity) {
        OhaEnergyUseBillFragment ohaEnergyUseBillFragment = getInstance(activity);
        Calendar calendar = Calendar.getInstance();
        double defaultKwhCost = 0.0;
        int amountDays = 30;
        //Recuperar a ultima conta para definir os valores padrão da nova conta de energia:
        Cursor cursor = activity.getContentResolver().query(CONTENT_URI_BILL, null, null, null, String.format("%s DESC", EnergyUseBillEntry.COLUMN_TO));
        try {
            if (cursor.moveToFirst()) {
                calendar.setTime(new Date(cursor.getLong(EnergyUseBillEntry.INDEX_COLUMN_TO)));
                calendar.add(Calendar.DATE, 1);
                defaultKwhCost = cursor.getDouble(EnergyUseBillEntry.INDEX_COLUMN_KWH_COST);
                amountDays = (int) OhaHelper.getAmountDays(cursor.getLong(EnergyUseBillEntry.INDEX_COLUMN_FROM), cursor.getLong(EnergyUseBillEntry.INDEX_COLUMN_TO));
            }
        } finally {
            cursor.close();
        }

        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_ID, -1);
        bundle.putLong(PARAM_FROM_DATE, OhaHelper.getDateBegin(calendar.getTime()).getTime());
        calendar.add(Calendar.DATE, amountDays);
        bundle.putLong(PARAM_TO_DATE, OhaHelper.getDateBegin(calendar.getTime()).getTime());
        bundle.putDouble(PARAM_KWH_COST, defaultKwhCost);
        ohaEnergyUseBillFragment.setArguments(bundle);
        ohaEnergyUseBillFragment.show(activity.getFragmentManager(), TAG);
    }


    public static void update(Activity activity, int id, long fromDate, long toDate, double kwhCost ) {
        OhaEnergyUseBillFragment ohaEnergyUseBillFragment = getInstance(activity);
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_ID, id);
        bundle.putLong(PARAM_FROM_DATE, fromDate);
        bundle.putLong(PARAM_TO_DATE, toDate);
        bundle.putDouble(PARAM_KWH_COST, kwhCost);
        ohaEnergyUseBillFragment.setArguments(bundle);
        ohaEnergyUseBillFragment.show(activity.getFragmentManager(), TAG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_energy_use_bill, container, false);
        this.radioButtonFromDate = (RadioButton) view.findViewById(R.id.radioButtonFromDate);
        this.radioButtonToDate = (RadioButton) view.findViewById(R.id.radioButtonToDate);
        this.editKwhCost = (EditText) view.findViewById(R.id.editKwhCost);
        this.datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        this.toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        this.setTextRadioButtonDate(radioButtonFromDate, this.fromDate);
        this.setTextRadioButtonDate(radioButtonToDate, this.toDate);
        this.setCalendarView();
        this.editKwhCost.setText(OhaHelper.getEditable(this.kwhCost));
        this.radioButtonFromDate.setOnClickListener(this);
        this.radioButtonToDate.setOnClickListener(this);
        this.datePicker.setOnClickListener(this);
        //Remover o date_picker_header do datePicker se o mesmo existir
        View viewDayDatePicker = datePicker.findViewById(Resources.getSystem().getIdentifier("date_picker_header", "id", "android"));
        if (viewDayDatePicker != null) {
            viewDayDatePicker.setVisibility(View.GONE);
        }
        this.toolbar.inflateMenu(R.menu.fragment_energy_use_bill);
        this.toolbar.setOnMenuItemClickListener(this);
        return view;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                this.kwhCost = OhaHelper.convertToDouble(this.editKwhCost.getText().toString(), 0.0);
                //Acionar o evento onSaveEnergyUseBill se a atividade suportar a interface IOhaEnergyUseBillFragment
                IOhaEnergyUseBillFragment iOhaEnergyUseBillFragment  = getActivity() instanceof IOhaEnergyUseBillFragment? (IOhaEnergyUseBillFragment)getActivity(): null;
                if ( iOhaEnergyUseBillFragment != null ){
                    iOhaEnergyUseBillFragment.onSaveEnergyUseBill(this.id, this.fromDate, this.toDate, this.kwhCost);
                }
                this.dismiss();
                return false;

            case R.id.action_cancel:
                this.dismiss();
                return false;
        }
        return false;
    }

    private void setCalendarView() {
        long selectedDate;
        if (radioButtonFromDate.isChecked()) {
            selectedDate = this.fromDate;
        } else if ( radioButtonToDate.isChecked()){
            selectedDate = this.toDate;
        } else {
            selectedDate = this.fromDate;
            this.radioButtonFromDate.setChecked(true);
            this.radioButtonToDate.setChecked(false);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(selectedDate));
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
    }

    private void setTextRadioButtonDate(RadioButton radioButtonDate, long date) {
        radioButtonDate.setText(OhaHelper.formatDate(date, "dd, MMM yyyy"));
    }

    @Override
    public void onResume() {
        //Definir o tamanho da janela conforme percentual definido no dimens.xml:
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        double percentWidth = getResources().getInteger(R.integer.energy_use_bill_fragment_width_percent) / 100.00;
        double percentHeight = getResources().getInteger(R.integer.energy_use_bill_fragment_height_percent) / 100.00;
        window.setLayout((int) (size.x * percentWidth), (int) (size.y * percentHeight));
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radioButtonFromDate:
                this.radioButtonToDate.setChecked(false);
                setCalendarView();
                return;
            case R.id.radioButtonToDate:
                this.radioButtonFromDate.setChecked(false);
                setCalendarView();
                return;
        }
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        if (this.radioButtonFromDate.isChecked()) {
            this.fromDate = calendar.getTime().getTime();
            //Corrigir o toDate
            if (this.fromDate > this.toDate) {
                calendar.add(Calendar.DATE, this.amountDays);
                this.toDate = calendar.getTime().getTime();
            }

        } else if (radioButtonToDate.isChecked()) {
            //Corrigir o fromDate
            this.toDate = calendar.getTime().getTime();
            if (this.fromDate > this.toDate) {
                calendar.add(Calendar.DATE, this.amountDays * -1);
                this.fromDate = calendar.getTime().getTime();
            }
        }
        setTextRadioButtonDate(radioButtonFromDate, this.fromDate);
        setTextRadioButtonDate(radioButtonToDate, this.toDate);
        this.amountDays = (int) OhaHelper.getAmountDays(this.fromDate, this.toDate);
    }

}
