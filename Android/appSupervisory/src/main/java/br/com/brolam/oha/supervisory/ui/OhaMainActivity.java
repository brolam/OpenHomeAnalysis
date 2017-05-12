package br.com.brolam.oha.supervisory.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.File;
import java.util.Date;

import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.oha.supervisory.OhaBroadcast;
import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.data.OhaEnergyUseContract;
import br.com.brolam.oha.supervisory.ui.adapters.OhaMainAdapter;
import br.com.brolam.oha.supervisory.ui.adapters.holders.OhaEnergyUseBillHolder;
import br.com.brolam.oha.supervisory.ui.adapters.holders.OhaMainHolder;
import br.com.brolam.oha.supervisory.ui.fragments.OhaEnergyUseBillFragment;
import br.com.brolam.oha.supervisory.ui.fragments.OhaRestoreDatabaseFragment;
import br.com.brolam.oha.supervisory.ui.helpers.OhaBackupHelper;
import static br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.*;

/**
 * Atividade principal do aplicativo para disponibilizar as seguintes funcionalidades:
 *  Exibir os cartões de utilização de energia por dia.
 *  Exibir os cartões de utilização de energia por conta.
 *  Exibir o NavigationView com o menu do aplicativo.
 *  Solicitar o backup do banco de dados.
 *  Solicitar o restore do banco de dados.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaMainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener, OhaMainHolder.IOhaMainHolder,
        OhaEnergyUseBillFragment.IOhaEnergyUseBillFragment,
        OhaRestoreDatabaseFragment.IOhaRestoreDatabaseFragment{

    GridLayoutManager gridLayoutManager;
    RecyclerView recyclerView;
    NavigationView navigationView;
    FloatingActionButton floatingActionButton;
    //Adaptador para exibir os cartões no RecyclerView conforme o menu selecionado no NavigationView
    OhaMainAdapter ohaMainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //Informar a quantidade de colunas conforme o tamanho da tela.
        this.gridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.cards_columns));
        this.recyclerView.setLayoutManager(this.gridLayoutManager);
        this.floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        this.floatingActionButton.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        this.ohaMainAdapter = new OhaMainAdapter(this);
        this.recyclerView.setAdapter(this.ohaMainAdapter);

        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Realizar o agendamento para executar o serviço de sincronização.
        OhaBroadcast.registerSyncAlarm(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = parseMenuItem(navigationView);
        //Acionar o carregamento do cursor no onResume, para garantir que o status do NavigationView
        //já foi atualizar quando a tela for reconstruida.
        getSupportLoaderManager().initLoader(menuItem.getItemId(), null, this);
    }

    /**
     * Validar e recupear o menu selecionado no NavigationView ou selecioar o nav_energy_use_day se
     * não existir um menu selecionado.
     * @return
     */
    private MenuItem parseMenuItem(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isChecked()) {
                return menuItem;
            }
        }
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_energy_use_day);
        menuItem.setChecked(true);
        return navigationView.getMenu().findItem(R.id.nav_energy_use_day);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                filter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackBar(String message){
        Snackbar.make(this.floatingActionButton, message, Snackbar.LENGTH_LONG)
                .setAction(null, null).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        try {
            switch (id) {
                case R.id.nav_energy_use_day:
                case R.id.nav_energy_use_bill:
                    getSupportLoaderManager().initLoader(id, null, this);
                    this.recyclerView.getLayoutManager().scrollToPosition(0);
                    return true;
                case R.id.nav_altert:
                    showSnackBar("Add Alert is not implemented yet!");
                    return false;
                case R.id.nav_settings:
                    startActivity(new Intent(this, OhaSettingsActivity.class));
                    return false;
                case R.id.nav_backup:
                    requestBackup();
                    return false;
                case R.id.nav_restore:
                    OhaRestoreDatabaseFragment.show(this);
                    return false;
                default:
                    return true;
            }
        } finally {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Solicitar a confirmação do backup
     */
    private void requestBackup() {
        String message = getString(R.string.main_activity_request_backup_database);
        Snackbar.make(this.floatingActionButton, message, Snackbar.LENGTH_LONG)
                .setAction(
                        getString(R.string.action_accept),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                OhaBackupHelper ohaBackupHelper = new OhaBackupHelper(OhaMainActivity.this);
                                ohaBackupHelper.setBackupTime();
                                showSnackBar(getString(R.string.main_activity_request_backup_database_started));
                            }
                        }).show();
    }

    /**
     * Envento para solicitar a confirmação do restore do banco de dados.
     * @param backup informar o arquivo do backup válido.
     */
    @Override
    public void onRequestRestoreDatabase(final File backup) {
        String message = getString(R.string.main_activity_request_restore_database, backup.getName());
        Snackbar.make(this.floatingActionButton, message, Snackbar.LENGTH_LONG)
                .setAction(
                        getString(R.string.action_accept),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                OhaBackupHelper ohaBackupHelper = new OhaBackupHelper(OhaMainActivity.this);
                                ohaBackupHelper.setBackupRestoreFilePath(backup.getAbsolutePath());
                                showSnackBar(getString(R.string.main_activity_request_restore_database_started));
                            }
                        }).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == R.id.nav_energy_use_day) {
            //Carregar o cursor com o a utilização de energia por dia.
            return new CursorLoader(
                    this,
                    OhaEnergyUseContract.getUriDays(new Date()),
                    null,
                    null,
                    null,
                    String.format("%s DESC", EnergyUseLogEntry._ID)

            );
        } else if (id == R.id.nav_energy_use_bill) {
            //Carregar o cursor com o a utilização de energia por conta.
            return new CursorLoader(
                    this,
                    CONTENT_URI_BILL,
                    null,
                    null,
                    null,
                    String.format("%s DESC", EnergyUseBillEntry.COLUMN_FROM));

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == R.id.nav_energy_use_day) {
            data.setNotificationUri(getContentResolver(), OhaEnergyUseContract.CONTENT_URI_DAYS);
            this.floatingActionButton.setImageResource(R.drawable.ic_add_alert_white);
        } else if (loader.getId() == R.id.nav_energy_use_bill) {
            data.setNotificationUri(getContentResolver(), CONTENT_URI_BILL);
            this.floatingActionButton.setImageResource(R.drawable.ic_add_white);
        }
        this.ohaMainAdapter.swapCursor(data, loader.getId());
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fab_button);
        this.floatingActionButton.startAnimation(animation);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.recyclerView.setAdapter(null);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onEnergyUseBillSelect(int id, long fromDate, long toDate, double kwhCost, int menuItemId) {
        switch (menuItemId){
            case R.id.action_details:
                showSnackBar("Bill details not implemented yet!");
                return;
            case R.id.action_edit:
                OhaEnergyUseBillFragment.update(this, id, fromDate, toDate, kwhCost);
                return;
            case R.id.action_delete:
                requestDeleteEnergyBill(id, fromDate, toDate);
                return;
            case R.id.action_chart:
                showSnackBar("Bill chart not implemented yet!");
                return;
        }

    }

    @Override
    public void onEnergyUseDaySelect(long beginDate, double kwhCost, int menuItemId) {
        switch (menuItemId){
            case R.id.action_details:
                long endDateTime = OhaHelper.getDateEnd(new Date(beginDate), false).getTime();
                OhaEnergyUseDetailsActivity.show(this, beginDate, endDateTime, kwhCost, false, EnergyUseLogEntry.FilterWatts.NONE, 0, 0);
                return;
            case R.id.action_chart:
                showSnackBar("Day energy use chart not implemented yet!");
                return;

        }

    }

    @Override
    public void onClick(View view) {
        if (view.equals(this.floatingActionButton)) {
            MenuItem menuItem = parseMenuItem(this.navigationView);
            switch (menuItem.getItemId()) {
                case R.id.nav_energy_use_day:
                    addAlert();
                    return;
                case R.id.nav_energy_use_bill:
                    addBill();
                    return;
            }
        }
    }

    private void addAlert() {
        showSnackBar("Add Alert is not implemented yet!");
    }

    private void addBill() {
        OhaEnergyUseBillFragment.add(this);
    }

    private void filter() {
        showSnackBar("Filter is not implemented yet!");
    }

    @Override
    public void onSaveEnergyUseBill(int id, long fromDate, long toDate, double kwhCost) {
        ContentValues contentValues = EnergyUseBillEntry.parse(new Date(fromDate), new Date(toDate), kwhCost);
        //Alterar a conta de energia se o id for válido:
        if ( id > 0){
            Uri uriEnergyBill = OhaEnergyUseContract.getUriBillById(id);
            getContentResolver().update(uriEnergyBill, contentValues, null, null);
        } else {
            getContentResolver().insert(CONTENT_URI_BILL, contentValues);
            this.gridLayoutManager.scrollToPosition(0);
        }
    }

    //Exibir uma pergunta para confirmar a exclusão da conta de energia selecionada.
    private void requestDeleteEnergyBill(final int id, long fromDate, long toDate) {
        String message = getString(R.string.main_activity_request_delete_energy_use_bill, OhaEnergyUseBillHolder.getEnergyBillTitle(this, fromDate, toDate));
        Snackbar.make(this.floatingActionButton, message, Snackbar.LENGTH_LONG)
                .setAction(
                        getString(R.string.action_accept),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri uriEnergyBill = OhaEnergyUseContract.getUriBillById(id);
                                getContentResolver().delete(uriEnergyBill, null, null);
                            }
                        }).show();
    }
}
