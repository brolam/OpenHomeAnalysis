package br.com.brolam.oha.supervisory.ui.adapters.holders;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.EnergyUseLogEntry;

/**
 * Suporte para exibir um cartão com a utilização de energia de um dia.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseDayHolder extends OhaMainHolder {

    TextView textViewDay;
    TextView textViewAccuracy;
    TextView textViewDailyCost;
    TextView textViewBody;
    TextView textViewWattsMax;
    Toolbar toolbar;

    public OhaEnergyUseDayHolder(View itemView) {
        super(itemView);
        this.textViewDay = (TextView)itemView.findViewById(R.id.textViewDay);
        this.textViewAccuracy = (TextView)itemView.findViewById(R.id.textViewAccuracy);
        this.textViewDailyCost = (TextView)itemView.findViewById(R.id.textViewDailyCost);
        this.textViewBody = (TextView)itemView.findViewById(R.id.textViewBody);
        this.textViewWattsMax = (TextView)itemView.findViewById(R.id.textViewWattsMax);
        this.toolbar = (Toolbar)itemView.findViewById(R.id.toolbar);
        this.toolbar.inflateMenu(R.menu.holder_energy_use_day);
    }

    @Override
    public void bindView(final Cursor cursor, final IOhaMainHolder iOhaMainHolder) {
        Context context = this.itemView.getContext();
        final long beginDate = cursor.getLong(EnergyUseLogEntry.INDEX_COLUMN_CALC_DATE);
        final double kwhCost = cursor.getDouble(EnergyUseLogEntry.INDEX_COLUMN_CALC_KWH_COST);
        Double duration = cursor.getDouble(EnergyUseLogEntry.INDEX_COLUMN_CALC_DURATION_SUN);
        Double totalKWH = OhaHelper.convertWhToKWH(cursor.getDouble(EnergyUseLogEntry.INDEX_COLUMN_CALC_WH_TOTAL_SUN));
        Double dailyCost = totalKWH * kwhCost;
        Double avgKWH = totalKWH > 0 ? totalKWH / OhaHelper.convertMillisToHours(duration * 1000) : 0.00;
        Double wattsMax = cursor.getDouble(EnergyUseLogEntry.INDEX_COLUMN_CALC_WATTS_MAX);
        String titleDay = OhaHelper.formatDate(beginDate, "EEE, dd MMM yyyy");
        this.textViewDay.setText(OhaHelper.formatCamelCase(titleDay));
        this.textViewAccuracy.setText(OhaHelper.formatAccuracyDay(context, duration, DateUtils.isToday(beginDate)));
        String strCost = context.getString(R.string.energy_use_day_card_cost, OhaHelper.formatNumber(dailyCost, "#0.00"));
        this.textViewDailyCost.setText(strCost);
        String body = context.getString(R.string.energy_use_day_card_body, OhaHelper.formatNumber(totalKWH, "#0.00"), OhaHelper.formatNumber(avgKWH, "#0.00"));
        this.textViewBody.setText(body);
        this.textViewWattsMax.setText(OhaHelper.formatNumber(wattsMax, ",##0.00"));
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iOhaMainHolder != null) {
                    iOhaMainHolder.onEnergyUseDaySelect(beginDate, kwhCost, R.id.action_details);
                }
            }
        });

        this.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                iOhaMainHolder.onEnergyUseDaySelect(beginDate, kwhCost, item.getItemId() );
                return true;
            }
        });
    }
}
