package br.com.brolam.oha.supervisory.ui.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import br.com.brolam.library.ui.fragments.TimeFragment;
import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.EnergyUseLogEntry.FilterWatts;
import br.com.brolam.oha.supervisory.ui.helpers.OhaHelper;


/**
 * Exibir um caixa de dialogo com os paramentro para realizar o filtro nos logs de utilização de energir.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */public class OhaEnergyUseLogFilterFragment extends DialogFragment implements Toolbar.OnMenuItemClickListener {
    public static String TAG = OhaEnergyUseLogFilterFragment.class.getName();

    /**
     * Interface para recuperar os valores atuais dos parametros do filtor e definir o
     * evento de aceitação do filtro.
     */
    public interface IOhaFilterWattsFragment{
        long getSelectedBeginDateTime();
        long getSelectedEndDateTime();
        FilterWatts getSelectedPhase();
        double getWattsGreaterEqual();
        double getWattsLessEqual();
        void onAcceptFilter(int beginHour, int beginMinute, int beginSecond, int endHour, int endMinute, int endSecond, FilterWatts filterWatts, double wattsGreaterEqual, double wattsLessEqual );
    }

    IOhaFilterWattsFragment iOhaFilterWattsFragment;
    TimeFragment timeFragmentBegin;
    TimeFragment timeFragmentEnd;
    Spinner spinnerPhases;
    EditText editWattsGreaterEqual;
    EditText editWattsLessEqual;
    Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_energy_use_log_filter, container, false);
        this.timeFragmentBegin = (TimeFragment) getFragmentManager().findFragmentById(R.id.timeFragmentBegin);
        this.timeFragmentEnd =  (TimeFragment) getFragmentManager().findFragmentById(R.id.timeFragmentEnd);
        this.spinnerPhases = (Spinner) view.findViewById(R.id.spinnerPhases);
        this.editWattsGreaterEqual = (EditText) view.findViewById(R.id.editWattsGreaterEqual);
        this.editWattsLessEqual = (EditText) view.findViewById(R.id.editWattsLessEqual);
        this.toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        this.iOhaFilterWattsFragment = (IOhaFilterWattsFragment) this.getActivity();
        //Recuperar os valores atuais do filtro se a interface foi definida:
        if (iOhaFilterWattsFragment != null) {
            int[] beginHourMinuteSecond = getHourMinuteSecond(iOhaFilterWattsFragment.getSelectedBeginDateTime());
            this.timeFragmentBegin.build(getString(R.string.energy_use_log_filter_fragment_begin_time), beginHourMinuteSecond[0],beginHourMinuteSecond[1], beginHourMinuteSecond[2]);
            int[] endHourMinuteSecond = getHourMinuteSecond(iOhaFilterWattsFragment.getSelectedEndDateTime());
            this.timeFragmentEnd.build(getString(R.string.energy_use_log_filter_fragment_end_time), endHourMinuteSecond[0], endHourMinuteSecond[1], endHourMinuteSecond[2]);
            this.buildSpinnerPhases(getActivity(), iOhaFilterWattsFragment.getSelectedPhase().ordinal());
            this.editWattsGreaterEqual.setText(OhaHelper.getEditable(iOhaFilterWattsFragment.getWattsGreaterEqual()));
            this.editWattsLessEqual.setText(OhaHelper.getEditable(iOhaFilterWattsFragment.getWattsLessEqual()));
        }
        this.toolbar.inflateMenu(R.menu.fragment_energy_use_log_filter);
        this.toolbar.setOnMenuItemClickListener(this);
        this.setViewsBySpinnerPhases(this.spinnerPhases.getSelectedItemPosition());
        this.spinnerPhases.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                setViewsBySpinnerPhases(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    public static void show(Activity activity) {
        OhaEnergyUseLogFilterFragment ohaFilterWattsFragment = (OhaEnergyUseLogFilterFragment) activity.getFragmentManager().findFragmentByTag(TAG);
        if (ohaFilterWattsFragment == null) ohaFilterWattsFragment = new OhaEnergyUseLogFilterFragment();
        Bundle args = new Bundle();
        ohaFilterWattsFragment.setArguments(args);
        ohaFilterWattsFragment.show(activity.getFragmentManager(), TAG);
        //ft.commit();
    }

    @Override
    public void onResume() {
        //Definir o tamanho da janela conforme percentual definido no dimens.xml:
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        double percentWidth = getResources().getInteger(R.integer.energy_use_log_filter_fragment_width_percent) / 100.00;
        double percentHeight = getResources().getInteger(R.integer.energy_use_log_filter_fragment_height_percent) / 100.00;
        window.setLayout((int) (size.x * percentWidth), (int) (size.y * percentHeight));
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    private void buildSpinnerPhases(Context context, int selectPhase){
        FilterWatts[] filterValues = FilterWatts.values();
        String[] strings = new String[filterValues.length];
        for(FilterWatts filterWatts: filterValues){
            strings[filterWatts.ordinal()] = OhaHelper.getEnergyUsePhaseDescription(context, filterWatts);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, strings);
        this.spinnerPhases.setAdapter(arrayAdapter);
        this.spinnerPhases.setSelection(selectPhase);
    }

    private void setViewsBySpinnerPhases(int  SelectedSpinnerPhases){
        boolean enabled =  !( SelectedSpinnerPhases == FilterWatts.NONE.ordinal());
        View view = this.editWattsGreaterEqual.getRootView();
        view.findViewById(R.id.textInputLayoutWattsGreaterEqual).setEnabled(enabled);
        view.findViewById(R.id.textInputLayoutWattsLessEqual).setEnabled(enabled);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        removeSubFragments();
    }

    private void acceptFilter(){
        double wattsGreaterEqual = OhaHelper.convertToDouble(this.editWattsGreaterEqual.getText().toString(), 0.0);
        double wattsLessEqual = OhaHelper.convertToDouble(this.editWattsLessEqual.getText().toString(), 0.0);
        if ( this.iOhaFilterWattsFragment != null){
            this.iOhaFilterWattsFragment.onAcceptFilter(
                    this.timeFragmentBegin.getHour(),
                    this.timeFragmentBegin.getMinute(),
                    this.timeFragmentBegin.getSecond(),
                    this.timeFragmentEnd.getHour(),
                    this.timeFragmentEnd.getMinute(),
                    this.timeFragmentEnd.getSecond(),
                    FilterWatts.values()[this.spinnerPhases.getSelectedItemPosition()],
                    wattsGreaterEqual,
                    wattsLessEqual
            );
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try {
            switch (item.getItemId()){
                case R.id.action_accept:
                    acceptFilter();
                    return false;
                case R.id.action_cancel:
                    return false;
            }
        } finally {
            this.dismiss();
            removeSubFragments();
        }
        return false;
    }

    /**
     * Remover os subs fragments timeFragmentBegin e timeFragmentEnd no onCancel ou
     * no dismiss, porque nesses eventos esses fragments não são renovidos automaticamente gerando erro
     * de duplicidade quando a OhaEnergyUseLogFilterFragment for acionada pela segunda vez;
     */
    private void removeSubFragments(){
        getFragmentManager().beginTransaction()
                .remove(getFragmentManager().findFragmentById(R.id.timeFragmentBegin))
                .remove(getFragmentManager().findFragmentById(R.id.timeFragmentEnd))
                .commit();
    }

    private int[] getHourMinuteSecond(long dateTime){
        int hour = Integer.parseInt(OhaHelper.formatDate(dateTime, "HH"));
        int minute = Integer.parseInt(OhaHelper.formatDate(dateTime, "mm"));
        int second = Integer.parseInt(OhaHelper.formatDate(dateTime, "ss"));
        return new int[]{hour, minute, second};
    }
}
