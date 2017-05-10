package br.com.brolam.oha.supervisory.ui.adapters.holders;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.oha.supervisory.R;
import static br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.*;


/**
 * Suporte para exibir um cartão com a utilização de energia de uma conta.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseBillHolder extends OhaMainHolder {

    TextView textViewTitle;
    TextView textViewRead;
    TextView textViewDailyCost;
    TextView textViewBody;
    TextView textViewWattsMax;
    Toolbar toolbar;

    public OhaEnergyUseBillHolder(View itemView) {
        super(itemView);
        this.textViewTitle = (TextView)itemView.findViewById(R.id.textViewTitle);
        this.textViewRead = (TextView)itemView.findViewById(R.id.textViewRead);
        this.textViewDailyCost = (TextView)itemView.findViewById(R.id.textViewDailyCost);
        this.textViewBody = (TextView)itemView.findViewById(R.id.textViewBody);
        this.textViewWattsMax = (TextView)itemView.findViewById(R.id.textViewWattsMax);
        this.toolbar = (Toolbar)itemView.findViewById(R.id.toolbar);
        this.toolbar.inflateMenu(R.menu.holder_energy_use_bill);
    }

    @Override
    public void bindView(final Cursor cursor, final OhaMainHolder.IOhaMainHolder iOhaMainHolder) {

        long billAmountDays = OhaHelper.getAmountDays(cursor.getLong(EnergyUseBillEntry.INDEX_COLUMN_FROM), cursor.getLong(EnergyUseBillEntry.INDEX_COLUMN_TO));
        double billReadingDays = OhaHelper.convertSecondsToDays(cursor.getDouble(EnergyUseBillEntry.INDEX_COLUMN_CALC_DURATION_SUN));

        Double totalKWH = OhaHelper.convertWhToKWH(cursor.getDouble(EnergyUseBillEntry.INDEX_COLUMN_CALC_WH_TOTAL_SUN));
        Double dailyCost = totalKWH * cursor.getDouble(EnergyUseBillEntry.INDEX_COLUMN_KWH_COST);
        Double avgKWH = totalKWH > 0 ? totalKWH / billReadingDays : 0.00;
        Double wattsMax = cursor.getDouble(EnergyUseBillEntry.INDEX_COLUMN_CALC_WATTS_MAX);
        this.textViewTitle.setText(getEnergyBillTitle(this.textViewTitle.getContext(), cursor.getLong(EnergyUseBillEntry.INDEX_COLUMN_FROM), cursor.getLong(EnergyUseBillEntry.INDEX_COLUMN_TO)));
        String read = textViewRead.getContext().getString(R.string.energy_use_bill_card_read, OhaHelper.formatNumber(billReadingDays, "#0.00"), OhaHelper.formatNumber(billAmountDays, "#0") );
        this.textViewRead.setText(read);
        this.textViewDailyCost.setText(OhaHelper.formatNumber(dailyCost, "$#,##0.00"));
        String body = this.textViewBody.getContext().getString(R.string.energy_use_bill_card_body, OhaHelper.formatNumber(totalKWH, "#0.00"), OhaHelper.formatNumber(avgKWH, "##0.00"));
        this.textViewBody.setText(body);
        this.textViewWattsMax.setText(OhaHelper.formatNumber(wattsMax, ",##0.00"));
        final int id = cursor.getInt(EnergyUseBillEntry.INDEX_COLUMN_ID);
        final long fromDate = cursor.getLong(EnergyUseBillEntry.INDEX_COLUMN_FROM);
        final long toDate = cursor.getLong(EnergyUseBillEntry.INDEX_COLUMN_TO);
        final double kwhCost = cursor.getDouble(EnergyUseBillEntry.INDEX_COLUMN_KWH_COST);
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (iOhaMainHolder != null) {
                    iOhaMainHolder.onEnergyUseBillSelect(id, fromDate, toDate, kwhCost, R.id.action_details);
                }
            }
        });

        this.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                iOhaMainHolder.onEnergyUseBillSelect(id, fromDate, toDate, kwhCost, item.getItemId());
                return true;
            }
        });
    }

    public static String getEnergyBillTitle(Context context, long fromDate, long toDate) {
        return context.getString(
                R.string.energy_use_bill_card_title,
                OhaHelper.formatDate(fromDate, "yyyy, MMM dd"),
                OhaHelper.formatDate(toDate, "MMM dd")
        );
    }
}
