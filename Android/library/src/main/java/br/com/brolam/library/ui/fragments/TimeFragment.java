package br.com.brolam.library.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;


import br.com.brolam.library.R;

/**
 * Fragmento para o usuário selecionar uma  hora, minuto e segundos divididos em três listas
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class TimeFragment extends Fragment {
    TextView textViewTitle;
    Spinner spinnerHour;
    Spinner spinnerMinute;
    Spinner spinnerSecond;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_time, container, false);
        this.textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        this.spinnerHour = (Spinner) view.findViewById(R.id.spinnerHour);
        this.spinnerMinute = (Spinner) view.findViewById(R.id.spinnerMinute);
        this.spinnerSecond = (Spinner) view.findViewById(R.id.spinnerSecond);
        return view;
    }

    public void build(String title){
        build(title, 0, 0, 0);
    }

    public void build(String title, int selectHour, int selectMinute, int selectSecond ){
        this.textViewTitle.setText(title);
        this.buildSpinnerHour(selectHour);
        this.buildSpinnerMinuteAndSeconds(selectMinute, selectSecond);

    }

    private void buildSpinnerHour(int selectHour){
        ArrayList<String> hours = new ArrayList<>();
        for(byte hour= 0; hour < 24; hour++){
            hours.add(String.format("%02d",hour));
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(textViewTitle.getContext(),android.R.layout.simple_spinner_item, hours);
        this.spinnerHour.setAdapter(arrayAdapter);
        this.spinnerHour.setSelection(selectHour);

    }

    private void buildSpinnerMinuteAndSeconds(int selectMinute, int selectSecond){
        ArrayList<String> list = new ArrayList<>();
        for(byte index= 0; index < 60; index++){
            list.add(String.format("%02d",index));
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(textViewTitle.getContext(),android.R.layout.simple_spinner_item, list);
        this.spinnerMinute.setAdapter(arrayAdapter);
        this.spinnerMinute.setSelection(selectMinute);
        this.spinnerSecond.setAdapter(arrayAdapter);
        this.spinnerSecond.setSelection(selectSecond);
    }

    public int getHour(){
        return this.spinnerHour.getSelectedItemPosition();
    }

    public int getMinute() {
        return this.spinnerMinute.getSelectedItemPosition();
    }

    public int getSecond(){
        return this.spinnerSecond.getSelectedItemPosition();
    }

    public void setEnabled(boolean hour, boolean minute, boolean second){
        this.spinnerHour.setEnabled(hour);
        this.spinnerMinute.setEnabled(minute);
        this.spinnerSecond.setEnabled(second);
    }



}
