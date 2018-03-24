package com.example.stv.androidrealm;

import io.realm.RealmObject;

/**
 * Created by Steva on 24/03/2018.
 */

public class Tbl3 extends RealmObject {

    private long id;
    private String valor;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
