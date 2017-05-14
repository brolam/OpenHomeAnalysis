package br.com.brolam.oha.supervisory.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.EnergyUseLogEntry.FilterWatts;
import br.com.brolam.oha.supervisory.data.helpers.OhaEnergyUseLogHelper;
import br.com.brolam.oha.supervisory.ui.adapters.OhaEnergyUseWattsAdapter;
import br.com.brolam.oha.supervisory.ui.adapters.OhaEnergyUseWhAdapter;
import br.com.brolam.oha.supervisory.ui.adapters.holders.OhaEnergyUseWhHolder;
import br.com.brolam.oha.supervisory.ui.fragments.OhaEnergyUseLogFilterFragment;

/**
 * Atividade para exibir os detalhes da utilização de energia
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseDetailsActivity extends AppCompatActivity implements OhaEnergyUseLogHelper.IOhaEnergyUseLogHelper, View.OnClickListener, OhaEnergyUseLogFilterFragment.IOhaFilterWattsFragment, OhaEnergyUseWhHolder.IOhaEnergyUseWhHolder {

    //Parâmetro obrigatório para informar a data e hora inicial.
    private static final String PARAM_BEGIN_DATE_TIME = "param_begin_date_time";
    private long beginDateTime;
    //Parâmetro obrigatório para informar a data e hora final.
    private static final String PARAM_End_DATE_TIME = "param_end_date_time";
    private long endDateTime;
    //Parâmetro obrigatório para informar o custo por KWh.
    private static final String PARAM_KWH_COST = "param_kwh_cost";
    private double kwhCost;
    //Parâmetro obrigatório para exibir os logs de utilização de energia ou o resumo por Watts hora.
    private static final String PARAM_SHOW_LOG = "param_show_log";
    private boolean isShowLog;
    //Parâmetro para informar a Fase no filtro.
    private static final String PARAM_FILTER_WATTS = "param_filter_watts";
    private FilterWatts filterWatts;
    //Parâmetro para informar o inicio do watts no filtro.
    private static final String PARAM_WATTS_GREATER_EQUAL = "param_watts_greater_equal";
    private double wattsGreaterEqual;
    //Parâmetro para informar o final do watts no filtro.
    private static final String PARAM_WATTS_LESS_EQUAL = "param_watts_less_equal";
    private double wattsLessEqual;

    //Ajudante para carregar o cursor e também realizar os calculos da utilização de energia.
    private OhaEnergyUseLogHelper ohaEnergyUseLogHelper;


    Toolbar toolbar;
    RecyclerView recyclerView;
    View titleEnergyUseWhValues;
    RecyclerView.Adapter ohaEnergyUseAdapter;
    FloatingActionButton floatingActionButton;

    /**
     * Iniciar a atividade conforme os parâmetros abaixo:
     * @param context informar um contexto válido.
     * @param beginDateTime informar a data e hora de incial.
     * @param endDateTime informar a data e hora final
     * @param kwhCost informar o custo por kwh da data informada.
     * @param isShowLog exibir os logs de utilização de energia ou o resumo por Watts hora.
     * @param filterWatts informar a fase que deve ser filtrada.
     * @param wattsGreaterEqual informar o watts inicial
     * @param wattsLessEqual informar o watts final.
     */
    public static void show(Context context, long beginDateTime, long endDateTime, double kwhCost, boolean isShowLog, FilterWatts filterWatts, double wattsGreaterEqual, double wattsLessEqual) {
        Intent intent = new Intent(context, OhaEnergyUseDetailsActivity.class);
        intent.putExtra(PARAM_BEGIN_DATE_TIME, beginDateTime);
        intent.putExtra(PARAM_End_DATE_TIME, endDateTime);
        intent.putExtra(PARAM_KWH_COST, kwhCost);
        intent.putExtra(PARAM_SHOW_LOG, isShowLog);
        intent.putExtra(PARAM_FILTER_WATTS, filterWatts);
        intent.putExtra(PARAM_WATTS_GREATER_EQUAL, wattsGreaterEqual);
        intent.putExtra(PARAM_WATTS_LESS_EQUAL, wattsLessEqual);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_use_details);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle bundle = getIntent().getExtras();
        this.beginDateTime = bundle.getLong(PARAM_BEGIN_DATE_TIME, 0);
        this.endDateTime = bundle.getLong(PARAM_End_DATE_TIME, 0);
        this.kwhCost = bundle.getDouble(PARAM_KWH_COST, 0.0);
        this.isShowLog = bundle.getBoolean(PARAM_SHOW_LOG, false);
        this.filterWatts = (FilterWatts) bundle.get(PARAM_FILTER_WATTS);
        this.wattsGreaterEqual = bundle.getDouble(PARAM_WATTS_GREATER_EQUAL);
        this.wattsLessEqual = bundle.getDouble(PARAM_WATTS_LESS_EQUAL);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Atualizar o título da tela
        String title = OhaHelper.formatDate(this.beginDateTime, "EEE, dd MMM yyyy");
        this.toolbar.setTitle(OhaHelper.formatCamelCase(title));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.titleEnergyUseWhValues = findViewById(R.id.titleEnergyUseWhValues);
        this.recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Instanciar o adaptador conforme o paramemto isShowLog
        if ( this.isShowLog){
            this.ohaEnergyUseAdapter =  new  OhaEnergyUseWattsAdapter(this);
        } else {
            this.ohaEnergyUseAdapter = new OhaEnergyUseWhAdapter(this, this.kwhCost, this);
        }
        this.recyclerView.setAdapter(this.ohaEnergyUseAdapter);

        this.floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        this.floatingActionButton.setOnClickListener(this);
        this.ohaEnergyUseLogHelper = new OhaEnergyUseLogHelper(this, this.beginDateTime, this.endDateTime, this.filterWatts, this.wattsGreaterEqual, this.wattsLessEqual);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_energy_use_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_filter:
                this.filter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackBar(String mensage){
        Snackbar.make(this.floatingActionButton, mensage, Snackbar.LENGTH_LONG)
                .setAction(null, null).show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onCalculationCompleted(double totalDuration, double totalWh1, double totalWh2, double totalWh3, double totalWh, ArrayList<OhaEnergyUseLogHelper.EnergyUseWh> energyUseWhs, Cursor cursor) {
        double totalKwh = OhaHelper.convertWhToKWH(totalWh);
        this.toolbar.setSubtitle(OhaHelper.formatAccuracy(this, this.beginDateTime, this.endDateTime, totalDuration, DateUtils.isToday(this.beginDateTime)));
        ((TextView) this.titleEnergyUseWhValues.findViewById(R.id.textViewDuration)).setText(DateUtils.formatElapsedTime((long) totalDuration));
        ((TextView) this.titleEnergyUseWhValues.findViewById(R.id.textViewKwh)).setText(OhaHelper.formatNumber(totalKwh, "#0.00"));
        ((TextView) this.titleEnergyUseWhValues.findViewById(R.id.textViewCost)).setText(OhaHelper.formatNumber(totalKwh * this.kwhCost, "#0.00"));

        TextView textViewWh1 = ((TextView) this.titleEnergyUseWhValues.findViewById(R.id.textViewWh1));
        if (textViewWh1 != null) {
            textViewWh1.setText(OhaHelper.formatNumber(totalWh1, "#,##0.00"));
            ((TextView) this.titleEnergyUseWhValues.findViewById(R.id.textViewWh2)).setText(OhaHelper.formatNumber(totalWh2, "#,##0.00"));
            ((TextView) this.titleEnergyUseWhValues.findViewById(R.id.textViewWh3)).setText(OhaHelper.formatNumber(totalWh3, "#,##0.00"));
        }

        if (isShowLog) {
            ((OhaEnergyUseWattsAdapter) this.ohaEnergyUseAdapter).swapCursor(cursor);
        } else {
            ((OhaEnergyUseWhAdapter) this.ohaEnergyUseAdapter).swapCursor(energyUseWhs);
        }
    }

    @Override
    public void onSelectedEnergyUseWh(long beginDateTime, long endDateTime, double kwhCost) {
        show(
                this,
                beginDateTime < this.beginDateTime ? this.beginDateTime : beginDateTime,
                endDateTime > this.endDateTime ? this.endDateTime : endDateTime,
                kwhCost,
                true,
                this.filterWatts,
                this.wattsGreaterEqual,
                this.wattsLessEqual
        );
    }

    @Override
    public void onClick(View view) {
        if (view.equals(this.floatingActionButton)) {
            showSnackBar("Add Alert is not implemented yet!");
        }
    }

    private void filter() {
        OhaEnergyUseLogFilterFragment.show(this);
    }

    @Override
    public long getSelectedBeginDateTime() {
        return this.beginDateTime;
    }

    @Override
    public long getSelectedEndDateTime() {
        return this.endDateTime;
    }

    @Override
    public FilterWatts getSelectedPhase() {
        return this.filterWatts;
    }

    @Override
    public double getWattsGreaterEqual() {
        return this.wattsGreaterEqual;
    }

    @Override
    public double getWattsLessEqual() {
        return this.wattsLessEqual;
    }

    @Override
    public void onAcceptFilter(int beginHour, int beginMinute, int beginSecond, int endHour, int endMinute, int endSecond, FilterWatts filterWatts, double wattsGreaterEqual, double wattsLessEqual) {
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(new Date(this.beginDateTime));
        beginCalendar.set(Calendar.HOUR_OF_DAY, beginHour);
        beginCalendar.set(Calendar.MINUTE, beginMinute);
        beginCalendar.set(Calendar.SECOND, beginSecond);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(new Date(this.endDateTime));
        endCalendar.set(Calendar.HOUR_OF_DAY, endHour);
        endCalendar.set(Calendar.MINUTE, endMinute);
        endCalendar.set(Calendar.SECOND, endSecond);
        long beginDateTime = beginCalendar.getTime().getTime();
        long endDateTime = endCalendar.getTime().getTime();
        show(this,beginDateTime, endDateTime, this.kwhCost, this.isShowLog, filterWatts, wattsGreaterEqual, wattsLessEqual);
        this.finish();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

}
