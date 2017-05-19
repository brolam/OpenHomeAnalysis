package br.com.brolam.oha.supervisory.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

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
public class OhaDatePickerDialogPreference extends DialogPreference {
    DatePicker datePicker;

    public OhaDatePickerDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.fragment_date_picker_dialog_preference);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        this.datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        //Remover o date_picker_header do datePicker se o mesmo existir
        View viewDayDatePicker = datePicker.findViewById(Resources.getSystem().getIdentifier("date_picker_header", "id", "android"));
        if (viewDayDatePicker != null) {
            viewDayDatePicker.setVisibility(View.GONE);
        }
        Calendar calendar = Calendar.getInstance();
        try {
            //recuperar a preferência no formato yyyyMMdd e converter para um Calendar:
            calendar = OhaHelper.getCalendar(this.getPersistedString(OhaHelper.getStrDate(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if ( positiveResult){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, datePicker.getYear());
            calendar.set(Calendar.MONTH, datePicker.getMonth());
            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
            String strDate = OhaHelper.getStrDate(calendar.getTime());
            SharedPreferences.Editor editor = getEditor();
            editor.putString(getKey(), strDate);
            editor.commit();
            this.setSummary(strDate);
        }
    }
}
