package br.com.brolam.oha.supervisory.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CalendarView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.ui.helpers.OhaHelper;

/**
 * Exibir um calendário na tela de configurações para o usuário selecionar uma data.
 * Observação: a data será gravada no formato yyyyMMdd conforme a chave da preferência.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaDatePickerDialogPreference extends DialogPreference implements CalendarView.OnDateChangeListener {
    CalendarView calendarView;

    public OhaDatePickerDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.fragment_date_picker_dialog_preference);
        setPositiveButtonText(null);
        setNegativeButtonText(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        this.calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        try {
            //recuperar a preferência no formato yyyyMMdd e converter para um Calendar:
            Calendar calendar = OhaHelper.getCalendar(this.getPersistedString(OhaHelper.getStrDate(new Date())));
            calendarView.setDate(calendar.getTime().getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            this.calendarView.setDate(new Date().getTime());
        }
        this.calendarView.setOnDateChangeListener(this);
    }

    /*
      Atualizar a preferência e fechar a caixa de dialogo.
     */
    @Override
    public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String strDate = OhaHelper.getStrDate(calendar.getTime());
        SharedPreferences.Editor editor = getEditor();
        editor.putString(getKey(), strDate);
        editor.commit();
        this.setSummary(strDate);
        this.getDialog().dismiss();
    }
}
