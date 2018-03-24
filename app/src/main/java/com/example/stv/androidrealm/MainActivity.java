package com.example.stv.androidrealm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Testa a inclusão em multiplas tabelas com uma terceira fazendo rollback.
 *  sem prejudicar a inclusão a inclusão da multiplas tabelas
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RealmInt();

    }

    public void InsertOnClick(View view) {
        // threadTbl1();
        Log.i("InsertOnClick", "Inicializando Click");

        realTransactionAsync();
        Log.i("InsertOnClick", "Passou pela Async Tranction Async");

        realTransactionAsyncRollback();
        Log.i("InsertOnClick", "Passou pela Async Tranction Async Rollback");
    }

    public void ListRegistrosOnClick(View view) {
        listRegistros();

    }

//    private void threadTbl1() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Realm realm = Realm.getDefaultInstance();
//
//                for (int i = 0; i < 10; i++) {
//                    realm.beginTransaction();
//
//                    Tbl1 tbl1 = realm.createObject(Tbl1.class);
//                    tbl1.setId(i);
//                    tbl1.setValor("Valor: " + i);
//
//                    Tbl2 tbl2 = realm.createObject(Tbl2.class);
//                    tbl2.setId(i);
//                    tbl2.setValor("Valor: " + i);
//
//                    realm.commitTransaction();
//
//                }
//            }
//        }).start();
//    }

    //Incluir registros em tabelas para tendo um outro método forçando um rollback
    private void realTransactionAsync() {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.i("realTransactionAsync", "Inicializado");

                int contador = 0;
                for (int x = 0; x < 10; x++) {

                    RealmList<Tbl1> tbl1s = new RealmList<>();
                    RealmList<Tbl2> tbl2s = new RealmList<>();

                    for (int i = 1; i <= 10; i++) {
                        contador++;
                        Tbl1 tbl1 = realm.createObject(Tbl1.class);
                        tbl1.setId(contador);
                        tbl1.setValor("Valor tbl1: i, x, contador " + i + ", " + x + ", " + contador);
                        tbl1s.add(tbl1);

                        Tbl2 tbl2 = realm.createObject(Tbl2.class);
                        tbl2.setId(i);
                        tbl2.setValor("Valor tbl2: i, x, contador " + i + ", " + x + ", " + contador);
                        tbl2s.add(tbl2);
                    }
                    realm.insert(tbl1s);
                    realm.insert(tbl2s);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("realTransactionAsync", "Finalizado");
            }
        });

    }


    // Método para forçar um roolback
    public void realTransactionAsyncRollback() {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(
                new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Log.i("realTransactionAsyncRollback", "Inicializado");

                        for (int x = 0; x < 1000; x++) {

                            RealmList<Tbl3> tbl3s = new RealmList<>();

                            for (int i = 1; i <= 10; i++) {

                                Tbl3 tbl3 = realm.createObject(Tbl3.class);
                                tbl3.setId(i);
                                tbl3.setValor("Valor Rollback tbl3: i, x, contador " + i);
                                tbl3s.add(tbl3);
                            }

                            realm.insert(tbl3s);

                            // Forçando erro para rollback automatico
                            Integer.parseInt("aa");

                        }


                        Log.i("realTransactionAsyncRollback", "Finalizado");

                    }
                },
                new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Log.i("realTransactionAsyncRollback", "Erro");
                    }
                }
        );

    }


    private void listRegistros() {
        Realm realm = Realm.getDefaultInstance();

        RealmResults<Tbl1> tbl1s = realm.where(Tbl1.class).findAll();
        for (Tbl1 tbl : tbl1s) {
            Log.i("Tbl1", tbl.getId() + " " + tbl.getValor());
        }

        RealmResults<Tbl2> tbl2s = realm.where(Tbl2.class).findAll();
        for (Tbl2 tbl : tbl2s) {
            Log.i("Tbl2", tbl.getId() + " " + tbl.getValor());
        }

        RealmResults<Tbl3> tbl3s = realm.where(Tbl3.class).findAll();
        for (Tbl3 tbl : tbl3s) {
            Log.i("Tbl3", tbl.getId() + " " + tbl.getValor());
        }
        if (tbl3s == null || tbl3s.size() == 0) {
            Log.i("Tbl3", "Sem Registro 3");

        }
    }

    public void RealmInt() {
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(realmConfiguration); // Clean slate
        Realm.setDefaultConfiguration(realmConfiguration); // Make this Realm the default
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
