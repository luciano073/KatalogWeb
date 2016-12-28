package br.com.katalog.katalogweb.utils;

/**
 * Created by luciano on 19/12/2016.
 */

public enum CoverType {
    HARDCOVER(0), PAPERBACK(1);

    private int valor;

    CoverType(int valor){
        this.valor = valor;
    }

    public int getValor(){
        return this.valor;
    }

}
