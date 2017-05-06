package br.com.brolam.oha.supervisory.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Date;

import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.data.helpers.OhaEnergyUseLogHelper;
import br.com.brolam.oha.supervisory.ui.OhaEnergyUseDetailsActivity;

/**
 * Suporte para exibir um linha com a utilização de energia em watts por hora.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseWhHolder extends RecyclerView.ViewHolder {

    public interface IOhaEnergyUseWhHolder{
        void onSelectedEnergyUseWh(long beginDateTime, long endDateTime, double kwhCost);

    }


    RadioButton radioButtonHour;
    TextView textViewHour;
    TextView textViewDuration;
    TextView textViewWh1;
    TextView textViewWh2;
    TextView textViewWh3;
    TextView textViewKwh;
    TextView textViewCost;

    public OhaEnergyUseWhHolder(View itemView) {
        super(itemView);
        this.radioButtonHour = (RadioButton) itemView.findViewById(R.id.radioButtonHour);
        this.textViewHour = (TextView) itemView.findViewById(R.id.textViewHour);
        this.textViewDuration = (TextView) itemView.findViewById(R.id.textViewDuration);
        this.textViewWh1 = (TextView) itemView.findViewById(R.id.textViewWh1);
        this.textViewWh2 = (TextView) itemView.findViewById(R.id.textViewWh2);
        this.textViewWh3 = (TextView) itemView.findViewById(R.id.textViewWh3);
        this.textViewKwh = (TextView) itemView.findViewById(R.id.textViewKwh);
        this.textViewCost = (TextView) itemView.findViewById(R.id.textViewCost);
    }

    public void bindView(OhaEnergyUseLogHelper.EnergyUseWh energyUseWh, final double kwhCost, final IOhaEnergyUseWhHolder iOhaEnergyUseWhHolder) {
        double totalKwh = OhaHelper.convertWhToKWH(energyUseWh.whTotal);
        String hour = OhaHelper.formatDate(energyUseWh.dateTime, "HH");
        this.textViewHour.setText(hour);
        this.textViewDuration.setText(DateUtils.formatElapsedTime((long) energyUseWh.duration));
        if (this.textViewWh1 != null) {
            this.textViewWh1.setText(OhaHelper.formatNumber(energyUseWh.wh1, "#,##0.00"));
            this.textViewWh2.setText(OhaHelper.formatNumber(energyUseWh.wh2, "#,##0.00"));
            this.textViewWh3.setText(OhaHelper.formatNumber(energyUseWh.wh3, "#,##0.00"));
        }
        this.textViewKwh.setText(OhaHelper.formatNumber(totalKwh, "#,##0.00"));
        this.textViewCost.setText(OhaHelper.formatNumber(totalKwh * kwhCost, "#,##0.00"));


        Date date = new Date(energyUseWh.dateTime);
        final long beginDateHour = OhaHelper.getBeginHour(date).getTime();
        final long endDateHour = OhaHelper.getEndHour(date, false).getTime();

        radioButtonHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOhaEnergyUseWhHolder.onSelectedEnergyUseWh(beginDateHour, endDateHour, kwhCost);
                radioButtonHour.setChecked(false);
            }
        });


    }
}
