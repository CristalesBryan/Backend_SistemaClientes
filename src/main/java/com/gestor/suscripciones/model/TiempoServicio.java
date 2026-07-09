package com.gestor.suscripciones.model;

public enum TiempoServicio {
    UN_MES("1 mes", 30),
    TRES_MESES("3 meses", 90),
    SEIS_MESES("6 meses", 180),
    UN_ANIO("1 año", 365),
    PERSONALIZADO("Personalizado", 0);

    private final String etiqueta;
    private final int dias;

    TiempoServicio(String etiqueta, int dias) {
        this.etiqueta = etiqueta;
        this.dias = dias;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public int getDias() {
        return dias;
    }
}
