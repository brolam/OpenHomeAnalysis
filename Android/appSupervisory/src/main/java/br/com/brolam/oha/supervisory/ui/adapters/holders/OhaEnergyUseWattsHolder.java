package br.com.brolam.oha.supervisory.ui.adapters.holders;


import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.data.helpers.OhaEnergyUseLogHelper;

/**
 * Suporte para exibir um linha com a utilização de energia em watts.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseWattsHolder extends RecyclerView.ViewHolder {
    TextView textViewTime;
    TextView textViewDuration;
    TextView textViewWatts1;
    TextView textViewWatts2;
    TextView textViewWatts3;
    TextView textViewWattsTotal;



    public OhaEnergyUseWattsHolder(View itemView) {
        super(itemView);
        this.textViewTime = (TextView)itemView.findViewById(R.id.textViewTime);
        this.textViewDuration = (TextView)itemView.findViewById(R.id.textViewDuration);
        this.textViewWatts1 = (TextView)itemView.findViewById(R.id.textViewWatts1);
        this.textViewWatts2 = (TextView)itemView.findViewById(R.id.textViewWatts2);
        this.textViewWatts3 = (TextView)itemView.findViewById(R.id.textViewWatts3);
        this.textViewWattsTotal = (TextView)itemView.findViewById(R.id.textViewWattsTotal);
    }

    public void bindView(final Cursor cursor) {
        String time = OhaHelper.formatDate(cursor.getLong(OhaEnergyUseLogHelper.INDEX_LOG_DATE_TIME), "HH:mm:ss");
        this.textViewTime.setText(time);
        this.textViewDuration.setText(DateUtils.formatElapsedTime(cursor.getLong(OhaEnergyUseLogHelper.INDEX_DURATION)));
        if ( this.textViewWatts1 != null) {
            textViewWatts1.setText(OhaHelper.formatNumber(cursor.getDouble(OhaEnergyUseLogHelper.INDEX_WATTS_1), "#,##0.00"));
            textViewWatts2.setText(OhaHelper.formatNumber(cursor.getDouble(OhaEnergyUseLogHelper.INDEX_WATTS_2), "#,##0.00"));
            textViewWatts3.setText(OhaHelper.formatNumber(cursor.getDouble(OhaEnergyUseLogHelper.INDEX_WATTS_3), "#,##0.00"));
        }
        this.textViewWattsTotal.setText(OhaHelper.formatNumber(cursor.getDouble(OhaEnergyUseLogHelper.INDEX_WATTS_TOTAL), "#,##0.00"));
    }
}
